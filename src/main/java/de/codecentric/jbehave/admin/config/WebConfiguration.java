package de.codecentric.jbehave.admin.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import de.codecentric.jbehave.admin.web.StoryController;

@Configuration
@ConfigurationProperties(prefix = "jbehave")
public class WebConfiguration extends WebMvcConfigurerAdapter {

	private static Log LOGGER = LogFactory.getLog(WebMvcConfigurerAdapter.class);

	private String reportPath;

	@Autowired(required = false)
	private ResourceProperties resourceProperties = new ResourceProperties();

	/**
	 * Maps all AngularJS routes to index.html so that they work with direct linking.
	 */
	@Controller
	static class Routes {

		@RequestMapping({ "/stories", "/stories/{id:\\w+" })
		public String index() {
			return "/index.html";
		}
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		if (resourceProperties != null) {
			if (!this.resourceProperties.isAddMappings()) {
				LOGGER.debug("Default resource handling disabled");
				return;
			}
			Integer cachePeriod = this.resourceProperties.getCachePeriod();
			if (!registry.hasMappingForPattern("/reports/**")) {
				registry.addResourceHandler("/reports/**").addResourceLocations("file:" + reportPath).setCachePeriod(cachePeriod);
			}
		}
	}

	@Bean
	public StoryController storyController() {
		return new StoryController();
	}

	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}
}
