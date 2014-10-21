package de.codecentric.jbehave.admin.repository;

import static org.jbehave.core.io.CodeLocations.codeLocationFromPath;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.io.LoadFromURL;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.model.Story;
import org.jbehave.core.parsers.StoryParser;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import de.codecentric.jbehave.admin.domain.StoryView;
import de.codecentric.jbehave.admin.services.StoryImporter;

@ConfigurationProperties(prefix = "jbehave")
public class StoryRepository {

	private static final Log LOGGER = LogFactory.getLog(StoryRepository.class);

	private StoryParser storyParser = new MostUsefulConfiguration().storyParser();

	private List<Story> stories = new ArrayList<Story>();

	private Map<String, Properties> statistics = new HashMap<String, Properties>();

	private StoryImporter storyImporter;

	private String localStoryPath;

	private String reportPath;

	@PostConstruct
	public List<Story> findAllStories() {
		stories.clear();
		// TODO Reload only, when Story was executed
		storyImporter.updateStories();
		loadStatistics();
		List<String> resolvedStories = new StoryFinder().findPaths(localStoryPath, Arrays.asList("**/*.story"), null);
		for (String resolvedStory : resolvedStories) {
			Story story = loadStory(resolvedStory);
			stories.add(story);
		}
		return stories;
	}

	public List<StoryView> findAllStoriesForView() {
		List<StoryView> storiesForView = new ArrayList<StoryView>();
		List<Story> stories = findAllStories();
		for (Story story : stories) {
			String status = getStatus(story.getName());
			String duration = getDuration(story.getName());
			storiesForView.add(new StoryView(story.getName(), story.getMeta().toString(), status, duration));
		}
		return storiesForView;
	}

	private Story loadStory(String name) {
		String storyAsText = new LoadFromURL().loadStoryAsText("file:" + localStoryPath + name);
		Story story = storyParser.parseStory(storyAsText, name);
		story.namedAs(FilenameUtils.getBaseName(name));
		return story;
	}

	public Story findStory(String name) {
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

	public void setStoryImporter(StoryImporter storyImporter) {
		this.storyImporter = storyImporter;
	}

	public void setLocalStoryPath(String localStoryPath) {
		this.localStoryPath = localStoryPath;
	}

	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}
}
