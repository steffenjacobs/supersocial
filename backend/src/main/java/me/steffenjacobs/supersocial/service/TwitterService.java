package me.steffenjacobs.supersocial.service;

import me.steffenjacobs.supersocial.domain.entity.Post;
import me.steffenjacobs.supersocial.domain.entity.SocialMediaAccount;
import net.minidev.json.JSONObject;

/** @author Steffen Jacobs */
public interface TwitterService {

	/**
	 * Tweets the given {@link Post} with the credentials associated to the
	 * {@link SocialMediaAccount} associated to the given {@link Post}.
	 */
	String tweet(Post post);

	/**
	 * Fetch the trending topics from Twitter for the given {@code region} with
	 * the given {@link SocialMediaAccount}.
	 */
	String fetchTrendingTopics(long regionId, SocialMediaAccount account);

	/**
	 * Fetch the statistics for the given {@link Post} from the Twitter API.
	 * 
	 * @return a cleaned-up JSON object with the retrieved
	 *         {@link TrackedStatistic tracked statistics} in it.
	 */
	JSONObject fetchPostStatistics(Post post);

	/**
	 * Fetch the statistics for the given {@link SocialMediaAccount} from the
	 * Twitter API.
	 * 
	 * @return a cleaned-up JSON object with the retrieved
	 *         {@link TrackedStatistic tracked statistics} in it.
	 */
	JSONObject fetchAccountStatistics(SocialMediaAccount account);

	/**
	 * Fetch region information based on longitude and latitude from the Twitter
	 * API to later e.g. localize trends.
	 */
	String fetchTwitterRegionForLatLng(double latitude, double longitude, SocialMediaAccount account);

}