package de.codecentric.jbehave.admin.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import de.codecentric.jbehave.admin.repository.InMemoryStoryRepository;
import de.codecentric.jbehave.admin.repository.StoryRepository;
import de.codecentric.jbehave.admin.services.StoryRunnerService;

@Configuration
public class JBehaveConfiguration  implements ApplicationContextAware{

	@Autowired
	private Environment environment;

	@Autowired
	private StoryRepository storyRepository;

	private ApplicationContext applicationContext;
	@Bean
	public StoryRepository testRepository() {
		return new InMemoryStoryRepository();
	}

	@Bean
	public StoryRunnerService testRunnerService() {
		StoryRunnerService service = new StoryRunnerService(applicationContext);
		service.setStoryRepository(storyRepository);
		return service;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}

