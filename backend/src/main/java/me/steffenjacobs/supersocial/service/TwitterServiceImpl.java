package me.steffenjacobs.supersocial.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jayway.jsonpath.JsonPath;

import me.steffenjacobs.supersocial.domain.dto.ImportedPostDTO;
import me.steffenjacobs.supersocial.domain.entity.Post;
import me.steffenjacobs.supersocial.domain.entity.SocialMediaAccount;
import me.steffenjacobs.supersocial.domain.entity.TrackedStatistic;
import me.steffenjacobs.supersocial.persistence.CredentialPersistenceManager.CredentialType;
import me.steffenjacobs.supersocial.service.exception.TwitterException;
import me.steffenjacobs.supersocial.service.exception.TwitterPostNotFoundException;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.ParseException;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

/**
 * Handles all interactions with the Twitter-APIs.
 * 
 * @author Steffen Jacobs
 */

@Component
public class TwitterServiceImpl implements TwitterService {
	private static final Logger LOG = LoggerFactory.getLogger(TwitterServiceImpl.class);

	private static final String TWITTER_STATUS_ENDPOINT_TEMPLATE = "https://api.twitter.com/1.1/statuses/update.json?status=%s";
	private static final String TWITTER_TRENDS_ENDPOINT_TEMPLATE = "https://api.twitter.com/1.1/trends/place.json?id=%s";
	private static final String TWITTER_USER_STATS_ENDPOINT_TEMPLATE = "https://api.twitter.com/1.1/users/show.json?screen_name=%s";
	private static final String TWITTER_TWEET_STATS_ENDPOINT_TEMPLATE = "https://api.twitter.com/1.1/statuses/show/%s.json";
	private static final String TWITTER_REGION_ENDPOINT_TEMPLATE = "https://api.twitter.com/1.1/trends/closest.json?lat=%s&long=%s";
	private static final String TWITTER_USER_TWEETS = "https://api.twitter.com/1.1/statuses/user_timeline.json?user_id=%s&count=500";
	private static final DateTimeFormatter TWITTER_DATE_FORMAT = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss X uuuu", Locale.ROOT);

	@Autowired
	private CredentialLookupService credentialLookupService;

	/**
	 * Tweets the given {@link Post} with the credentials associated to the
	 * {@link SocialMediaAccount} associated to the given {@link Post}.
	 */
	@Override
	public String tweet(Post post) {
		try {
			return attemptAuthorizedRequest(new HttpPost(String.format(TWITTER_STATUS_ENDPOINT_TEMPLATE, URLEncoder.encode(post.getText(), StandardCharsets.UTF_16))),
					post.getSocialMediaAccountToPostWith());
		} catch (OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException | IOException e) {
			LOG.error("Could not send tweet");
			throw new TwitterException("Could not send tweet.", e);
		}
	}

	/**
	 * Attempt to perform an authorized request against the Twitter API via the
	 * given {@code account}.
	 * 
	 * @throws TwitterException
	 *             if something goes wrong with the API call.
	 */
	private String attemptAuthorizedRequest(HttpUriRequest request, SocialMediaAccount account)
			throws UnsupportedOperationException, IOException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
		final OAuthConsumer oAuthConsumer = new CommonsHttpOAuthConsumer(credentialLookupService.getCredential(account, CredentialType.TWITTER_API_KEY),
				credentialLookupService.getCredential(account, CredentialType.TWITTER_API_KEY_SECRET));
		oAuthConsumer.setTokenWithSecret(credentialLookupService.getCredential(account, CredentialType.TWITTER_ACCESS_TOKEN),
				credentialLookupService.getCredential(account, CredentialType.TWITTER_ACCESS_TOKEN_SECRET));
		oAuthConsumer.sign(request);
		try (final CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
			final CloseableHttpResponse response = httpClient.execute(request);
			final String responseJson = stringify(response.getEntity());
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				if (JsonPath.read(responseJson, "$.errors[0].code").toString().equals("144")) {
					// TODO Tweet had been deleted -> set to unpublished
					throw new TwitterPostNotFoundException(JsonPath.read(responseJson, "$.errors[0].message"));
				} else {
					throw new TwitterException(String.format("Could not execute request: %s - %s", response.getStatusLine().getStatusCode(), responseJson));
				}
			}
			return responseJson;
		}
	}

	/** Convert the body of the given {@link HttpEntity} to a string. */
	private String stringify(HttpEntity entity) throws UnsupportedOperationException, IOException {
		return IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8);
	}

	/**
	 * Fetch the trending topics from Twitter for the given {@code region} with
	 * the given {@link SocialMediaAccount}.
	 */
	@Override
	public String fetchTrendingTopics(long regionId, SocialMediaAccount account) {
		LOG.info("Fetching trending topics for {}", regionId);
		try {
			final String result = attemptAuthorizedRequest(new HttpGet(String.format(TWITTER_TRENDS_ENDPOINT_TEMPLATE, regionId)), account);
			return JsonPath.read(result, "$..['name','tweet_volume']").toString();
		} catch (OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException | IOException e) {
			LOG.error("Could not fetch trending topics");
			throw new TwitterException("Could not fetch trending topics.", e);
		}
	}

	/**
	 * Fetch the statistics for the given {@link Post} from the Twitter API.
	 * 
	 * @return a cleaned-up JSON object with the retrieved
	 *         {@link TrackedStatistic tracked statistics} in it.
	 */
	@Override
	public JSONObject fetchPostStatistics(Post post) {
		try {
			String json = attemptAuthorizedRequest(new HttpGet(String.format(TWITTER_TWEET_STATS_ENDPOINT_TEMPLATE, post.getExternalId())), post.getSocialMediaAccountToPostWith());
			JSONObject jsonResult = new JSONObject();
			jsonResult.appendField(TrackedStatistic.POST_LIKES.key(), JsonPath.read(json, "$.favorite_count"));
			jsonResult.appendField(TrackedStatistic.POST_COMMENTS.key(), 0);
			jsonResult.appendField(TrackedStatistic.POST_SHARES.key(), JsonPath.read(json, "$.retweet_count"));
			jsonResult.appendField(TrackedStatistic.POST_IMPRESSIONS.key(), 0);
			return jsonResult;
		} catch (UnsupportedOperationException | OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException | IOException e) {
			LOG.error("Could not fetch post statistics for tweet {}: ", post.getExternalId(), e);
			throw new TwitterException("Could not fetch post statistics for tweet.", e);
		}
	}

	/**
	 * Fetch the statistics for the given {@link SocialMediaAccount} from the
	 * Twitter API.
	 * 
	 * @return a cleaned-up JSON object with the retrieved
	 *         {@link TrackedStatistic tracked statistics} in it.
	 */
	@Override
	public JSONObject fetchAccountStatistics(SocialMediaAccount account) {
		try {
			String json = attemptAuthorizedRequest(
					new HttpGet(String.format(TWITTER_USER_STATS_ENDPOINT_TEMPLATE, credentialLookupService.getCredential(account, CredentialType.TWITTER_ACCOUNT_NAME))), account);
			JSONObject jsonResult = new JSONObject();
			jsonResult.appendField(TrackedStatistic.ACCOUNT_FOLLOWERS.key(), JsonPath.read(json, "$.followers_count"));
			jsonResult.appendField(TrackedStatistic.ACCOUNT_POST_COUNT.key(), JsonPath.read(json, "$.statuses_count"));

			jsonResult.appendField(TrackedStatistic.ACCOUNT_ENGAGED_USERS.key(), 0);
			jsonResult.appendField(TrackedStatistic.ACCOUNT_VIEWS.key(), 0);
			return jsonResult;
		} catch (UnsupportedOperationException | OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException | IOException e) {
			LOG.error("Could not fetch account statistics for user {}: ", account.getId(), e);
			throw new TwitterException("Could not fetch account statistics.", e);
		}
	}

	/**
	 * Fetch all tweets for the user associated to the given
	 * {@link SocialMediaAccount}.
	 * 
	 * @return a list of imported posts.
	 */
	@Override
	public List<ImportedPostDTO> fetchAllTweetsForUser(SocialMediaAccount account) {
		try {
			String json = attemptAuthorizedRequest(
					new HttpGet(String.format(TWITTER_USER_TWEETS, credentialLookupService.getCredential(account, CredentialType.TWITTER_ACCOUNT_NAME))), account);

			// TODO: extract followers + post count from the given json as well

			List<ImportedPostDTO> importedPosts = new ArrayList<>();
			JSONArray jsonPosts = (JSONArray) JSONValue.parseStrict(json);
			jsonPosts.forEach(jsonPost -> {
				JSONObject post = (JSONObject) jsonPost;
				ImportedPostDTO p = new ImportedPostDTO();
				p.setAssociatedAccount(account);
				p.setExternalId(post.getAsString("id_str"));
				p.setText(post.getAsString("text"));
				p.setPublished(OffsetDateTime.parse(post.getAsString("created_at"), TWITTER_DATE_FORMAT).toInstant());
				importedPosts.add(p);
			});

			LOG.info("Fetched {} tweets for account {}.", importedPosts.size(), account.getId());
			return importedPosts;
		} catch (UnsupportedOperationException | OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException | IOException | ParseException e) {
			LOG.error("Could not fetch posts for user {}: ", account.getId(), e);
			throw new TwitterException("Could not fetch posts.", e);
		}
	}

	/**
	 * Fetch region information based on longitude and latitude from the Twitter
	 * API to later e.g. localize trends.
	 */
	@Override
	public String fetchTwitterRegionForLatLng(double latitude, double longitude, SocialMediaAccount account) {
		try {
			return attemptAuthorizedRequest(new HttpGet(String.format(TWITTER_REGION_ENDPOINT_TEMPLATE, latitude, longitude)), account);
		} catch (UnsupportedOperationException | OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException | IOException e) {
			LOG.error("Could not fetch location from twitter API {}/{}: ", latitude, longitude, e);
			throw new TwitterException(String.format("Could not fetch location: %s. ", e.getMessage()));
		}
	}
}
