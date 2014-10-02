package de.codecentric.jbehave.admin.repository;

import java.util.List;

import org.jbehave.core.model.Story;

import de.codecentric.jbehave.admin.domain.StoryView;

public interface StoryRepository {

	public List<StoryView> findAll();

	public Story findStory(String name);

	public String getStatus(String name);
}
