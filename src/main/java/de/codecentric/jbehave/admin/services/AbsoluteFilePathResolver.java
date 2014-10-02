package de.codecentric.jbehave.admin.services;

import org.apache.commons.lang.StringUtils;
import org.jbehave.core.io.StoryLocation;
import org.jbehave.core.reporters.FilePrintStreamFactory.FilePathResolver;

public class AbsoluteFilePathResolver implements FilePathResolver {

	@Override
	public String resolveDirectory(StoryLocation storyLocation, String relativeDirectory) {
		return relativeDirectory;
	}

	public String resolveName(StoryLocation storyLocation, String extension) {
		String name = storyLocation.getPath().replace('/', '.');
		if (name.startsWith(".")) {
			name = name.substring(1);
		}
		return StringUtils.substringBeforeLast(name, ".") + "." + extension;
	}

}
