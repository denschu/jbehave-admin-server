package de.codecentric.jbehave.admin.services;

import java.util.Arrays;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.embedder.StoryManager;
import org.jbehave.core.embedder.StoryManager.RunningStory;
import org.jbehave.core.failures.PassingUponPendingStep;
import org.jbehave.core.failures.RethrowingFailure;
import org.jbehave.core.model.Story;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.spring.SpringStepsFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;

import de.codecentric.jbehave.admin.repository.StoryRepository;

@ConfigurationProperties(prefix = "jbehave")
public class StoryRunnerService {

	private StoryRepository storyRepository;

	private Embedder embedder;
	private StoryManager storyManager;
	private Map<String, RunningStory> runningStories;

	@NotNull
	private String reportPath;

	private ApplicationContext applicationContext;

	public StoryRunnerService(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@PostConstruct
	public void setupRunner() {
		// TODO make it singleton
		embedder = new Embedder();
		embedder.useConfiguration(new MostUsefulConfiguration().usePendingStepStrategy(new PassingUponPendingStep()).useFailureStrategy(
				new RethrowingFailure()));
		embedder.useStepsFactory(new SpringStepsFactory(embedder.configuration(), applicationContext));
		embedder.configuration().useStoryReporterBuilder(
				new StoryReporterBuilder().withFormats(Format.CONSOLE, Format.HTML, Format.STATS).withPathResolver(new AbsoluteFilePathResolver())
						.withRelativeDirectory(reportPath).withFailureTrace(true).withFailureTraceCompression(false));
		storyManager = embedder.storyManager();
	}

	public void run(String name) {
		Story story = storyRepository.findStory(name);
		if (story == null) {
			throw new IllegalArgumentException("Story " + name + " not found in Repository!");
		}
		runningStories = storyManager.runningStories(Arrays.asList(story), embedder.metaFilter(), null);
	}

	public String getStatus(String name) {
		RunningStory runningStory = runningStories.get(name + ".story");
		if (runningStory != null) {
			if (!runningStory.isDone()) {
				return "RUNNING";
			} else if (runningStory.isFailed()) {
				return "FAILED";
			}
		}
		return storyRepository.getStatus(name);
	}

	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}

	public void setStoryRepository(StoryRepository storyRepository) {
		this.storyRepository = storyRepository;
	}
}
