package me.steffenjacobs.supersocial.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import me.steffenjacobs.supersocial.persistence.CredentialPersistenceManager;
import me.steffenjacobs.supersocial.persistence.CredentialPersistenceManager.CredentialType;
import me.steffenjacobs.supersocial.service.exception.FacebookException;

/** @author Steffen Jacobs */

@Component
public class FacebookService {
	private static final Logger LOG = LoggerFactory.getLogger(FacebookService.class);

	@Autowired
	private CredentialPersistenceManager credentialPersistenceManager;

	private static final String FACEBOOK_POST_TO_PAGE_ENDPOINT = "https://graph.facebook.com/%s/feed?message=%s&access_token=%s";
	private static final String FACEBOOK_EXCHANGE_TO_PAGE_TOKEN_ENDPOINT = "https://graph.facebook.com/%s?access_token=%s&fields=access_token";

	/**
	 * Publishes {@code text} with the credentials given in the
	 * credentials.properties file to the given facebook page.
	 */
	public String postMessage(String text) {
		try {
			return attemptPostMessage(text);
		} catch (UnsupportedOperationException | IOException e) {
			LOG.error("Could not post message to page");
			throw new FacebookException("Could not post message to page.", e);
		}
	}

	/**
	 * Publishes {@code text} with the credentials given in the
	 * credentials.properties file to the given Facebook page.
	 */
	private String attemptPostMessage(String text) throws UnsupportedOperationException, IOException {
		final HttpPost httpPost = new HttpPost(String.format(FACEBOOK_POST_TO_PAGE_ENDPOINT, credentialPersistenceManager.getCredential(CredentialType.FACEBOOK_PAGE_ID).get(),
				URLEncoder.encode(text, StandardCharsets.UTF_16), credentialPersistenceManager.getCredential(CredentialType.FACEBOOK_PAGE_ACCESSTOKEN).get()));

		try (final CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
			final HttpResponse httpResponse = httpClient.execute(httpPost);
			return IOUtils.toString(httpResponse.getEntity().getContent(), Charset.forName("UTF-8"));
		}
	}

	/**
	 * Exchange an {@code userToken} for a page token using the Facebook Graph
	 * API. @return the result from the Facebook API.
	 */
	public String exchangeForPageToken(String userToken) {
		final HttpGet httpGet = new HttpGet(String.format(FACEBOOK_EXCHANGE_TO_PAGE_TOKEN_ENDPOINT, credentialPersistenceManager.getCredential(CredentialType.FACEBOOK_PAGE_ID).get(),
				URLEncoder.encode(userToken, StandardCharsets.UTF_8)));

		try (final CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
			HttpResponse httpResponse = httpClient.execute(httpGet);
			return IOUtils.toString(httpResponse.getEntity().getContent(), Charset.forName("UTF-8"));
		} catch (IOException e1) {
			throw new FacebookException("Could not exchange user token for page token", e1);
		}
	}

}
