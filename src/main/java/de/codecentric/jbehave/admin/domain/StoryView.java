package de.codecentric.jbehave.admin.domain;


public class StoryView {

	/* The name of the story */
	private String name;

	private String meta;

	private String status;

	private String duration;

	public StoryView(String name, String meta, String status, String duration) {
		super();
		this.name = name;
		this.meta = meta;
		this.status = status;
		this.duration = duration;
	}

	public String getName() {
		return name;
	}

	public String getMeta() {
		return meta;
	}

	public String getStatus() {
		return status;
	}

	public String getDuration() {
		return duration;
	}

	// @Override
	// public String toString() {
	// return String.format("Service[id=%d, name='%s', version='%s', url='%s', message='%s', active='%s']", id, name, version, url, message, active);
	// }

}
