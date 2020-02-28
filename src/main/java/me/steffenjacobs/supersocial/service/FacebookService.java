package me.steffenjacobs.supersocial.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import me.steffenjacobs.supersocial.service.CredentialService.Credential;
import me.steffenjacobs.supersocial.service.exception.FacebookException;

/** @author Steffen Jacobs */

@Component
public class FacebookService {
	private static final Logger LOG = LoggerFactory.getLogger(FacebookService.class);

	@Autowired
	private CredentialService credentialService;

	private static final String FACEBOOK_POST_TO_PAGE_ENDPOINT = "https://graph.facebook.com/%s/feed?message=%s&access_token=%s";

	/**
	 * Publishes {@code text} with the credentials given in the
	 * credentials.properties file to the given facebook page.
	 */
	public void postMessage(String text) {
		try {
			attemptPostMessage(text);
		} catch (UnsupportedOperationException | IOException e) {
			LOG.error("Could not post message to page");
			throw new FacebookException("Could not post message to page.", e);
		}
	}

	/**
	 * Publishes {@code text} with the credentials given in the
	 * credentials.properties file to the given facebook page.
	 */
	private void attemptPostMessage(String text) throws UnsupportedOperationException, IOException {
		final HttpPost httpPost = new HttpPost(String.format(FACEBOOK_POST_TO_PAGE_ENDPOINT, credentialService.getCredential(Credential.FACEBOOK_PAGE_ID),
				URLEncoder.encode(text, StandardCharsets.UTF_16), credentialService.getCredential(Credential.FACEBOOK_PAGE_ACCESSTOKEN)));

		try (final CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
			final HttpResponse httpResponse = httpClient.execute(httpPost);
			LOG.info(IOUtils.toString(httpResponse.getEntity().getContent(), Charset.forName("UTF-8")));
		}
	}

}
