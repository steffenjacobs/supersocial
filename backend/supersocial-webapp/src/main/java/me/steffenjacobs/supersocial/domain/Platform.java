package me.steffenjacobs.supersocial.domain;

/** @author Steffen Jacobs */
public enum Platform {
	UNKNOWN(0), FACEBOOK(1), TWITTER(2);

	private final int id;

	private Platform(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public static Platform fromId(int platformId) {
		switch (platformId) {
		case 1:
			return FACEBOOK;
		case 2:
			return TWITTER;
		default:
			return UNKNOWN;
		}
	}
}
