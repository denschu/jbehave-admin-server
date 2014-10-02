package de.codecentric.jbehave.admin.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbehave.core.model.Story;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.codecentric.jbehave.admin.domain.StoryView;
import de.codecentric.jbehave.admin.repository.StoryRepository;
import de.codecentric.jbehave.admin.services.StoryRunnerService;

@RestController
public class StoryController {

	private static final Log LOGGER = LogFactory.getLog(StoryController.class);

	@Autowired
	StoryRepository repository;

	@Autowired
	StoryRunnerService runner;

	@RequestMapping("/api/stories")
	public List<StoryView> getStories() {
		List<StoryView> stories = new ArrayList<StoryView>();
		for (StoryView story : repository.findAll()) {
			stories.add(story);
		}
		return stories;
	}

	@RequestMapping("/api/stories/{name}")
	public Story getStory(@PathVariable String name) {
		return repository.findStory(name);
	}

	@RequestMapping("/api/stories/{name}/status")
	public String getStoryStatus(@PathVariable String name) throws InterruptedException {
		Thread.sleep(500);
		String status = runner.getStatus(name);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Status for Story " + name + " is " + status);
		}
		return status;
	}

	@RequestMapping(value = "/api/stories", method = RequestMethod.POST)
	public void createStoryExecution(@RequestBody String name) {
		runner.run(name);
	}

	@ExceptionHandler(StoryNotFoundException.class)
	public String handleException(StoryNotFoundException ex, HttpServletResponse response) {
		return ex.getMessage();
	}

}
