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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.codecentric.jbehave.admin.TestApplication;
import de.codecentric.jbehave.admin.repository.StoryRepository;

/**
 * Integration tests for {@link StoryRunnerService}.
 * 
 * @author Dennis Schulte
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestApplication.class)
public class StoryRunnerIntegrationTest {

	static {
		System.setProperty("spring.config.location", "classpath:/environment.properties");
	}

	@Autowired
	StoryRunnerService service;

	@Autowired
	StoryRepository repository;
	
	@Test
	public void shouldRunSuccessfully() throws InterruptedException {

		// Given
		String name = "example";
		repository.findAllStories();
		
		// When
		service.run(name);

		// Then
		while (true) {
			String status = service.getStatus(name);
			System.out.println(status);
			if (status.equals("FAILED")) {
				break;
			}
			if (status.equals("SUCCESSFUL")) {
				break;
			}
			Thread.sleep(1000);
		}
		// assertThat(failures, is(notNullValue()));
		// assertThat(failures.values(), is(empty()));
	}

	@Test
	public void shouldRunWithFilter() throws InterruptedException {

		// Given
		String filter = "+category api";
		// When
		service.runMultipleStoriesWithFilter(filter);

		// Then
		Thread.sleep(1000);
		assertThat(service.getStatus("example"), is(equalTo("SUCCESSFUL")));

	}
}
