package me.steffenjacobs.supersocial.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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

import me.steffenjacobs.supersocial.domain.entity.Post;
import me.steffenjacobs.supersocial.domain.entity.SocialMediaAccount;
import me.steffenjacobs.supersocial.domain.entity.TrackedStatistic;
import me.steffenjacobs.supersocial.persistence.CredentialPersistenceManager.CredentialType;
import me.steffenjacobs.supersocial.service.exception.TwitterException;
import net.minidev.json.JSONObject;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

/** @author Steffen Jacobs */

@Component
public class TwitterService {
	private static final Logger LOG = LoggerFactory.getLogger(TwitterService.class);

	private static final String TWITTER_STATUS_ENDPOINT_TEMPLATE = "https://api.twitter.com/1.1/statuses/update.json?status=%s";
	private static final String TWITTER_TRENDS_ENDPOINT_TEMPLATE = "https://api.twitter.com/1.1/trends/place.json?id=%s";
	private static final String TWITTER_USER_STATS_ENDPOINT_TEMPLATE = "https://api.twitter.com/1.1/users/show.json?screen_name=%s";
	private static final String TWITTER_TWEET_STATS_ENDPOINT_TEMPLATE = "https://api.twitter.com/1.1/statuses/show/%s.json";

	@Autowired
	private CredentialLookupService credentialLookupService;

	/**
	 * Tweets {@code tweetText} with the credentials requested from the
	 * credential service.
	 */
	public String tweet(Post post) {
		try {
			return attemptAuthorizedRequest(new HttpPost(String.format(TWITTER_STATUS_ENDPOINT_TEMPLATE, URLEncoder.encode(post.getText(), StandardCharsets.UTF_16))),
					post.getSocialMediaAccountToPostWith());
		} catch (OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException | IOException e) {
			LOG.error("Could not send tweet");
			throw new TwitterException("Could not send tweet.", e);
		}
	}

	private String attemptAuthorizedRequest(HttpUriRequest request, SocialMediaAccount account)
			throws UnsupportedOperationException, IOException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
		final OAuthConsumer oAuthConsumer = new CommonsHttpOAuthConsumer(credentialLookupService.getCredential(account, CredentialType.TWITTER_API_KEY),
				credentialLookupService.getCredential(account, CredentialType.TWITTER_API_KEY_SECRET));
		oAuthConsumer.setTokenWithSecret(credentialLookupService.getCredential(account, CredentialType.TWITTER_ACCESS_TOKEN),
				credentialLookupService.getCredential(account, CredentialType.TWITTER_ACCESS_TOKEN_SECRET));
		oAuthConsumer.sign(request);
		try (final CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
			final CloseableHttpResponse response = httpClient.execute(request);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				throw new TwitterException(String.format("Could not execute request: %s - %s", response.getStatusLine().getStatusCode(), stringify(response.getEntity())));
			}
			return stringify(response.getEntity());
		}
	}

	private String stringify(HttpEntity entity) throws UnsupportedOperationException, IOException {
		return IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8);
	}

	public String fetchTrendingTopics(long regionId, SocialMediaAccount account) {
		try {
			final String result = attemptAuthorizedRequest(new HttpGet(String.format(TWITTER_TRENDS_ENDPOINT_TEMPLATE, regionId)), account);
			return JsonPath.read(result, "$..['name','tweet_volume']").toString();
		} catch (OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException | IOException e) {
			LOG.error("Could not fetch trending topics");
			throw new TwitterException("Could not fetch trending topics.", e);
		}
	}

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
}
