package me.steffenjacobs.supersocial.domain;

import java.util.HashSet;
import java.util.Set;

/** @author Steffen Jacobs */
public class MessagePublishingDTO {

	private String message;
	
	//TODO: use enum constants
	private Set<String> platforms = new HashSet<String>();

	public MessagePublishingDTO() {

	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Set<String> getPlatforms() {
		return platforms;
	}

	public void setPlatforms(Set<String> platforms) {
		this.platforms = platforms;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MessagePublishingDTO [message=").append(message).append(", platforms=").append(platforms).append("]");
		return builder.toString();
	}

}
