package de.codecentric.jbehave.admin.config;

import javax.validation.constraints.NotNull;

import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.failures.PassingUponPendingStep;
import org.jbehave.core.failures.RethrowingFailure;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.spring.SpringStepsFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import de.codecentric.jbehave.admin.repository.StoryRepository;
import de.codecentric.jbehave.admin.services.AbsoluteFilePathResolver;
import de.codecentric.jbehave.admin.services.StoryImporter;
import de.codecentric.jbehave.admin.services.StoryRunnerService;
import de.codecentric.jbehave.admin.services.SvnStoryImporter;

@Configuration
@ConfigurationProperties(prefix = "jbehave")
public class JBehaveConfiguration implements ApplicationContextAware {

	@Autowired
	private Environment environment;

	@Autowired
	private StoryRepository storyRepository;

	@NotNull
	private String reportPath;

	private ApplicationContext applicationContext;

	@Bean
	public StoryRepository storyRepository() {
		return new StoryRepository();
	}

	@Bean
	@ConditionalOnMissingBean(StoryImporter.class)
	public StoryImporter storyImporter() {
		return new SvnStoryImporter();
	}

	@Bean
	public StoryRunnerService storyRunnerService() {
		StoryRunnerService service = new StoryRunnerService();
		service.setStoryRepository(storyRepository);
		service.setEmbedder(embedder());
		return service;
	}

	@Bean
	public Embedder embedder() {
		Embedder embedder = new Embedder();
		embedder.useConfiguration(new MostUsefulConfiguration().usePendingStepStrategy(new PassingUponPendingStep()).useFailureStrategy(
				new RethrowingFailure()));
		embedder.useStepsFactory(new SpringStepsFactory(embedder.configuration(), applicationContext));
		embedder.configuration().useStoryReporterBuilder(
				new StoryReporterBuilder().withFormats(Format.CONSOLE, Format.HTML, Format.STATS).withPathResolver(new AbsoluteFilePathResolver())
						.withRelativeDirectory(reportPath).withFailureTrace(true).withFailureTraceCompression(false));
		return embedder;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}
}
