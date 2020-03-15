package me.steffenjacobs.supersocial.domain.entity;

/** @author Steffen Jacobs */
public enum LoginProvider {
	NONE(-1), UNKNOWN(0), SUPERSOCIAL(1), DISCOURSE(2);
	private final int id;

	private LoginProvider(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public static LoginProvider fromId(int id) {
		switch (id) {
		case 1:
			return SUPERSOCIAL;
		case 2:
			return DISCOURSE;
		default:
			return UNKNOWN;
		}
	}

}
