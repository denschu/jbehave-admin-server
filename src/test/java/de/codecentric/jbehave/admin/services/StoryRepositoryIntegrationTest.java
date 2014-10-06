/*
 * Copyright 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.codecentric.jbehave.admin.services;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.jbehave.core.model.Story;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.codecentric.jbehave.admin.TestApplication;
import de.codecentric.jbehave.admin.domain.StoryView;
import de.codecentric.jbehave.admin.repository.StoryRepository;

/**
 * Integration tests for {@link StoryRepository}.
 * 
 * @author Dennis Schulte
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestApplication.class)
public class StoryRepositoryIntegrationTest {

	static {
		System.setProperty("spring.config.location", "classpath:/environment.properties");
	}

	@Autowired
	StoryRepository repository;

	@Test
	public void findAll() {

		// Given

		// When
		List<StoryView> result = repository.findAllStoriesForView();

		// Then
		assertThat(result, is(notNullValue()));
		assertThat(result.get(0).getName(), is(notNullValue()));
	}

	@Test
	public void getStatus() {

		// Given

		// When
		String result = repository.getStatus("example");

		// Then
		assertThat(result, is(notNullValue()));

	}

	@Test
	public void findStory() {

		// Given

		// When
		Story result = repository.findStory("example");

		// Then
		assertThat(result, is(notNullValue()));
	}
}
