package me.steffenjacobs.supersocial.domain.entity;

/** @author Steffen Jacobs */
public enum UserConfigurationType {
	LONGITUDE("user.longitude"), LATITUDE("user.latitude"), LOCATION("user.location");

	private final String key;

	private UserConfigurationType(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}
