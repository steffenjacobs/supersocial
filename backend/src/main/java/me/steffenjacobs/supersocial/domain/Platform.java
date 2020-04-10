package me.steffenjacobs.supersocial.domain;

/**
 * Social Media Platform. Contains a unique identifier to identify it.
 * 
 * @author Steffen Jacobs
 */
public enum Platform {
	UNKNOWN(0), FACEBOOK(1), TWITTER(2);

	private final int id;

	private Platform(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public String getIdAsString() {
		return "" + id;
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
