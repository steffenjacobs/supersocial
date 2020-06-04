package me.steffenjacobs.supersocial.domain.entity;

/** @author Steffen Jacobs */
public enum TrackedStatistic {
	POST_IMPRESSIONS("impressions"), POST_COMMENTS("comments"), POST_LIKES("likes"), POST_SHARES("shares"), ACCOUNT_FOLLOWERS("acc_followers"), ACCOUNT_POST_COUNT(
			"acc_posts"), ACCOUNT_ENGAGED_USERS("acc_enganged_users"), ACCOUNT_VIEWS("acc_impressions");

	private final String key;

	private TrackedStatistic(String key) {
		this.key = key;
	}

	public String key() {
		return this.key;
	}
}
