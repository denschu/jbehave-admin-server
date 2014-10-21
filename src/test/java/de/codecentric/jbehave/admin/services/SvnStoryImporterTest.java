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

import org.junit.Ignore;
import org.junit.Test;

/**
 * Integration tests for {@link SvnStoryImporter}.
 * 
 * @author Dennis Schulte
 */
public class SvnStoryImporterTest {

	private final static String REPO = "https://github.com/denschu/jbehave-admin-server/trunk/src/test/resources";

	@Test
	@Ignore
	public void shouldCheckoutSuccessfully() throws InterruptedException {

		// Given
		SvnStoryImporter service = new SvnStoryImporter();
		service.setRemoteStoryPath(REPO);
		service.setLocalStoryPath("target/stories");

		// When
		service.importStories();

		// Then

	}

	@Test
	@Ignore
	public void shouldUpdateSuccessfully() throws InterruptedException {

		// Given
		SvnStoryImporter service = new SvnStoryImporter();
		service.setRemoteStoryPath(REPO);
		service.setLocalStoryPath("target/stories");

		// When
		service.updateStories();

		// Then

	}
}
