package me.steffenjacobs.supersocial.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import me.steffenjacobs.supersocial.service.CredentialService.Credential;
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

	private static final String TWITTER_STATUS_ENDPOINT = "https://api.twitter.com/1.1/statuses/update.json?status=";

	@Autowired
	private CredentialService credentialService;

	/**
	 * Tweets {@code tweetText} with the credentials given in the
	 * credentials.properties file.
	 */
	public String tweet(String tweetText) {
		try {
			if (!credentialService.hasCredentials()) {
				throw new TwitterException("Please provide credentials!");
			}
			return attemptTweet(tweetText, credentialService.getCredential(Credential.TWITTER_ACCESS_TOKEN),
					credentialService.getCredential(Credential.TWITTER_ACCESS_TOKEN_SECRET));
		} catch (OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException | IOException e) {
			LOG.error("Could not send tweet");
			throw new TwitterException("Could not send tweet.", e);
		}
	}

	/**
	 * Attempt to tweet {@code tweetText} with the given {@code accessToken} and
	 * {@code accessTokenSecret}.
	 */
	private String attemptTweet(String tweetText, String accessToken, String accessTokenSecret)
			throws ClientProtocolException, IOException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
		final OAuthConsumer oAuthConsumer = new CommonsHttpOAuthConsumer(credentialService.getCredential(Credential.TWITTER_API_KEY),
				credentialService.getCredential(Credential.TWITTER_API_KEY_SECRET));
		oAuthConsumer.setTokenWithSecret(accessToken, accessTokenSecret);
		final HttpPost httpPost = new HttpPost(TWITTER_STATUS_ENDPOINT + URLEncoder.encode(tweetText, StandardCharsets.UTF_16));
		oAuthConsumer.sign(httpPost);
		try (final CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
			final HttpResponse httpResponse = httpClient.execute(httpPost);
			return IOUtils.toString(httpResponse.getEntity().getContent(), Charset.forName("UTF-8"));
		}
	}
}
