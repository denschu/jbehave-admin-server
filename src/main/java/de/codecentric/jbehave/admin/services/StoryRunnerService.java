package de.codecentric.jbehave.admin.services;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.embedder.MetaFilter;
import org.jbehave.core.embedder.StoryManager.RunningStory;
import org.jbehave.core.model.Story;

import de.codecentric.jbehave.admin.repository.StoryRepository;

public class StoryRunnerService {

	private StoryRepository storyRepository;

	private Map<String, RunningStory> runningStories;

	private Embedder embedder;

	public void run(String name) {
		Story story = storyRepository.findStory(name);
		if (story == null) {
			throw new IllegalArgumentException("Story " + name + " not found in Repository!");
		}
		runningStories = embedder.storyManager().runningStories(Arrays.asList(story), embedder.metaFilter(), null);
	}

	public void runMultipleStoriesWithFilter(String filter) {
		List<Story> stories = storyRepository.findAllStories();
		if (stories == null || stories.isEmpty()) {
			throw new IllegalArgumentException("No Story found in Repository!");
		}
		runningStories = embedder.storyManager().runningStories(stories, new MetaFilter(filter), null);
	}

	public String getStatus(String name) {
		if (runningStories != null) {
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
		return "UNKNOWN";
	}

	public void setStoryRepository(StoryRepository storyRepository) {
		this.storyRepository = storyRepository;
	}

	public void setEmbedder(Embedder embedder) {
		this.embedder = embedder;
	}
}
