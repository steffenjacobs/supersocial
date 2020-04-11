package me.steffenjacobs.supersocial.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
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
import me.steffenjacobs.supersocial.persistence.CredentialPersistenceManager.CredentialType;
import me.steffenjacobs.supersocial.service.exception.TwitterException;
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
			return stringify(httpClient.execute(request).getEntity());
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

	public String fetchPostStatistics(String externalId) {
		// TODO Auto-generated method stub
		return "";
	}

	public String fetchAccountStatistics(SocialMediaAccount account) {
		// TODO Auto-generated method stub
		return "";
	}
}
