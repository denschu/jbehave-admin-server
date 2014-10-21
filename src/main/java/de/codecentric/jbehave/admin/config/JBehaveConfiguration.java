package de.codecentric.jbehave.admin.config;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import javax.validation.constraints.NotNull;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.failures.PassingUponPendingStep;
import org.jbehave.core.failures.RethrowingFailure;
import org.jbehave.core.reporters.FilePrintStreamFactory;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporter;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.spring.SpringStepsFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import de.codecentric.jbehave.admin.reporters.EmailStoryReporter;
import de.codecentric.jbehave.admin.repository.StoryRepository;
import de.codecentric.jbehave.admin.services.AbsoluteFilePathResolver;
import de.codecentric.jbehave.admin.services.StoryImporter;
import de.codecentric.jbehave.admin.services.StoryRunnerService;
import de.codecentric.jbehave.admin.services.SvnStoryImporter;

@Configuration
@ConfigurationProperties(prefix = "jbehave")
public class JBehaveConfiguration implements ApplicationContextAware {

	private static final Log LOGGER = LogFactory.getLog(JBehaveConfiguration.class);

	@Autowired
	private Environment environment;

	@Autowired
	private StoryRepository storyRepository;

	@NotNull
	private String reportPath;

	private String reportBaseUrl;

	private String mailHostname;

	private String mailPort;

	private String mailFrom;

	private ApplicationContext applicationContext;

	@Bean
	public StoryRepository storyRepository() {
		StoryRepository storyRepository = new StoryRepository();
		storyRepository.setStoryImporter(storyImporter());
		return storyRepository;
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
				new StoryReporterBuilder().withFormats(Format.CONSOLE, Format.HTML, Format.STATS, emailFormat())
						.withPathResolver(new AbsoluteFilePathResolver()).withRelativeDirectory(reportPath).withFailureTrace(true)
						.withFailureTraceCompression(false));
		return embedder;
	}

	@Bean
	public Format emailFormat() {
		Integer port = environment.getProperty("server.port", Integer.class);
		try {
			reportBaseUrl = new URL("http", InetAddress.getLocalHost().getCanonicalHostName(), port, "").toString();
		} catch (MalformedURLException e) {
			LOGGER.error("MalformedURLException", e);
		} catch (UnknownHostException e) {
			LOGGER.error("UnknownHostException", e);
		}
		return new EmailFormat();
	}

	@Bean
	@ConditionalOnProperty("jbehave.mailHostname")
	public JavaMailSender mailSender() {
		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
		javaMailSender.setHost(mailHostname);
		javaMailSender.setPort(Integer.valueOf(mailPort));
		return javaMailSender;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}

	public void setMailHostname(String mailHostname) {
		this.mailHostname = mailHostname;
	}

	public void setMailPort(String mailPort) {
		this.mailPort = mailPort;
	}

	public void setMailFrom(String mailFrom) {
		this.mailFrom = mailFrom;
	}

	public class EmailFormat extends Format {

		public EmailFormat() {
			super("EMAIL");
		}

		@Override
		public StoryReporter createStoryReporter(FilePrintStreamFactory factory, StoryReporterBuilder storyReporterBuilder) {
			factory.useConfiguration(storyReporterBuilder.fileConfiguration("xml"));
			return new EmailStoryReporter(mailFrom, reportPath, reportBaseUrl, mailSender());
		}
	};
}
