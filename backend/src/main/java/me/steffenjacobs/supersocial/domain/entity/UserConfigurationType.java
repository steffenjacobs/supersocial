package me.steffenjacobs.supersocial.domain.entity;

/** @author Steffen Jacobs */
public enum UserConfigurationType {
	Longitude("user.longitude"), Latitude("user.latitude"), Location("user.location");

	private final String key;

	private UserConfigurationType(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}
