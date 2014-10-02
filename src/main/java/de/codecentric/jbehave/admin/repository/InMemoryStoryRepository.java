package de.codecentric.jbehave.admin.repository;

import static org.jbehave.core.io.CodeLocations.codeLocationFromPath;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.io.LoadFromURL;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.model.Story;
import org.jbehave.core.parsers.StoryParser;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import de.codecentric.jbehave.admin.domain.StoryView;

@ConfigurationProperties(prefix = "jbehave")
public class InMemoryStoryRepository implements StoryRepository {

	private static final Log LOGGER = LogFactory.getLog(InMemoryStoryRepository.class);

	private StoryParser storyParser = new MostUsefulConfiguration().storyParser();

	private List<Story> stories = new ArrayList<Story>();
	private Map<String, Properties> statistics = new HashMap<String, Properties>();

	private String localStoryPath;
	private String remoteStoryPath;
	private String remoteUser;
	private String remotePassword;
	private String reportPath;

	@Override
	public List<StoryView> findAll() {
		stories.clear();
		// TODO Reload only, when Story was executed
		loadStatistics();
		List<StoryView> tests = new ArrayList<StoryView>();
		List<String> resolvedStories = new StoryFinder().findPaths(localStoryPath, Arrays.asList("**/*.story"), null);
		for (String resolvedStory : resolvedStories) {
			Story story = loadStory(resolvedStory);
			stories.add(story);
			String name = FilenameUtils.getBaseName(resolvedStory);
			String status = getStatus(name);
			String duration = getDuration(name);
			tests.add(new StoryView(FilenameUtils.getBaseName(resolvedStory), story.getMeta().toString(), status, duration));
		}
		return tests;
	}

	private Story loadStory(String name) {
		String storyAsText;
		if (!StringUtils.isBlank(remoteStoryPath)) {
			storyAsText = new LoadFromURL().loadStoryAsText("file://" + localStoryPath + name);
		} else {
			storyAsText = new LoadFromClasspath().loadStoryAsText(name);
		}
		Story story = storyParser.parseStory(storyAsText, name);
		story.namedAs(FilenameUtils.getBaseName(name));
		return story;
	}

	@Override
	public Story findStory(String name) {
		findAll();
		for (Story story : stories) {
			if (story.getName().equals(name)) {
				return story;
			}
		}
		return null;
	}

	public String getDuration(String name) {
		Properties stats = findStatistics(name);
		if (stats != null) {
			return stats.getProperty("duration");
		}
		return "UNKNOWN";
	}

	public String getStatus(String name) {
		Integer failedStepsCount = getFailedStepsCount(name);
		if (failedStepsCount != null) {
			if (failedStepsCount > 0) {
				return "FAILED";
			}
			return "SUCCESSFUL";
		}
		return "UNKNOWN";
	}

	public Integer getFailedStepsCount(String name) {
		Properties stats = findStatistics(name);
		if (stats != null && stats.containsKey("scenariosFailed")) {
			return Integer.parseInt(stats.getProperty("scenariosFailed"));
		}
		return null;
	}

	public Properties findStatistics(String name) {
		try {
			return PropertiesLoaderUtils.loadProperties(new FileSystemResource(reportPath + name + ".stats"));
		} catch (IOException e) {
			return null;
		}
	}

	@PostConstruct
	private void loadStories() throws SVNException {
		if (!StringUtils.isBlank(remoteStoryPath)) {
			LOGGER.info("Checking out Stories from " + remoteStoryPath);
			File dstPath = new File(localStoryPath);
			SVNURL url = SVNURL.parseURIEncoded(remoteStoryPath);
			ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(remoteUser, remotePassword);
			SVNUpdateClient uc = new SVNUpdateClient(authManager, SVNWCUtil.createDefaultOptions(true));
			uc.doCheckout(url, dstPath, SVNRevision.UNDEFINED, SVNRevision.HEAD, SVNDepth.INFINITY, true);
		}
	}

	private void loadStatistics() {
		statistics.clear();
		List<String> paths = new StoryFinder().findPaths(codeLocationFromPath(reportPath).getFile(), Arrays.asList("**/*.stats"), null);
		for (String path : paths) {
			Properties stats = findStatistics(path);
			if (stats != null) {
				statistics.put(FilenameUtils.getBaseName(path), stats);
			}
		}
	}

	public void setLocalStoryPath(String localStoryPath) {
		this.localStoryPath = localStoryPath;
	}

	public void setRemoteStoryPath(String remoteStoryPath) {
		this.remoteStoryPath = remoteStoryPath;
	}

	public void setRemoteUser(String remoteUser) {
		this.remoteUser = remoteUser;
	}

	public void setRemotePassword(String remotePassword) {
		this.remotePassword = remotePassword;
	}

	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}
}
