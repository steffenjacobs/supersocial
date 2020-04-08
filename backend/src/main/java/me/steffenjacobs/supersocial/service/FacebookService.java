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

import com.jayway.jsonpath.JsonPath;

import me.steffenjacobs.supersocial.domain.entity.Post;
import me.steffenjacobs.supersocial.domain.entity.SocialMediaAccount;
import me.steffenjacobs.supersocial.domain.entity.TrackedStatistic;
import me.steffenjacobs.supersocial.persistence.CredentialPersistenceManager.CredentialType;
import me.steffenjacobs.supersocial.service.exception.FacebookException;
import net.minidev.json.JSONObject;

/** @author Steffen Jacobs */

@Component
public class FacebookService {
	private static final Logger LOG = LoggerFactory.getLogger(FacebookService.class);

	private static final String FACEBOOK_POST_TO_PAGE_ENDPOINT = "https://graph.facebook.com/%s/feed?message=%s&access_token=%s";
	private static final String FACEBOOK_EXCHANGE_TO_PAGE_TOKEN_ENDPOINT = "https://graph.facebook.com/%s?access_token=%s&fields=access_token";
	private static final String FACEBOOK_POST_STATISTICS = "https://graph.facebook.com/%s_%s?fields=shares,likes.summary(true),comments.summary(true),insights.metric(post_impressions)&access_token=%s";
	private static final String FACEBOOK_PAGE_STATISTICS = "https://graph.facebook.com/%s?fields=fan_count,posts,insights.metric(page_impressions,page_engaged_users)&access_token=%s";

	@Autowired
	private CredentialLookupService credentialLookupService;

	/**
	 * Publishes {@code text} with the credentials given in the
	 * credentials.properties file to the given facebook page.
	 */
	public String postMessage(Post post) {
		try {
			return attemptPostMessage(post);
		} catch (UnsupportedOperationException | IOException e) {
			LOG.error("Could not post message to page");
			throw new FacebookException("Could not post message to page.", e);
		}
	}

	/**
	 * Publishes {@code text} with the credentials given in the
	 * credentials.properties file to the given Facebook page.
	 */
	private String attemptPostMessage(Post post) throws UnsupportedOperationException, IOException {
		final HttpPost httpPost = new HttpPost(String.format(FACEBOOK_POST_TO_PAGE_ENDPOINT, credentialLookupService.getCredential(post, CredentialType.FACEBOOK_PAGE_ID),
				URLEncoder.encode(post.getText(), StandardCharsets.UTF_16), credentialLookupService.getCredential(post, CredentialType.FACEBOOK_PAGE_ACCESSTOKEN)));

		try (final CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
			final HttpResponse httpResponse = httpClient.execute(httpPost);
			return IOUtils.toString(httpResponse.getEntity().getContent(), Charset.forName("UTF-8"));
		}
	}

	/**
	 * Exchange an {@code userToken} for a page token using the Facebook Graph
	 * API. @return the result from the Facebook API.
	 */
	public String exchangeForPageToken(SocialMediaAccount account, String userToken) {
		final HttpGet httpGet = new HttpGet(String.format(FACEBOOK_EXCHANGE_TO_PAGE_TOKEN_ENDPOINT, credentialLookupService.getCredential(account, CredentialType.FACEBOOK_PAGE_ID),
				URLEncoder.encode(userToken, StandardCharsets.UTF_8)));

		try (final CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
			HttpResponse httpResponse = httpClient.execute(httpGet);
			return IOUtils.toString(httpResponse.getEntity().getContent(), Charset.forName("UTF-8"));
		} catch (IOException e1) {
			throw new FacebookException("Could not exchange user token for page token", e1);
		}
	}

	public JSONObject fetchPostStatistics(Post post) {
		final HttpGet httpGet = new HttpGet(String.format(FACEBOOK_POST_STATISTICS, credentialLookupService.getCredential(post, CredentialType.FACEBOOK_PAGE_ID),
				post.getExternalId(), credentialLookupService.getCredential(post, CredentialType.FACEBOOK_PAGE_ACCESSTOKEN)));
		try (final CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
			final HttpResponse httpResponse = httpClient.execute(httpGet);
			JSONObject json = JsonPath.read(httpResponse.getEntity().getContent(), "$");
			JSONObject jsonResult = new JSONObject();
			jsonResult.appendField("postId", post.getId());
			jsonResult.appendField(TrackedStatistic.POST_LIKES.key(), JsonPath.read(json, "$.likes.summary.total_count"));
			jsonResult.appendField(TrackedStatistic.POST_COMMENTS.key(), JsonPath.read(json, "$.comments.summary.total_count"));
			jsonResult.appendField(TrackedStatistic.POST_SHARES.key(), JsonPath.read(json, "$.shares.count"));
			jsonResult.appendField(TrackedStatistic.POST_VIEWS.key(), JsonPath.read(json, "$.insights.data.values.value"));
			return jsonResult;

		} catch (UnsupportedOperationException | IOException e) {
			LOG.error("Could not retrieve tracked statistics from facebook for post {}", post.getId(), e);
			throw new FacebookException(String.format("Could not retrieve tracked statistics from facebook for post '%s': %s.", post.getId(), e.getMessage()), e);
		}
	}

	public JSONObject fetchAccountStatistics(SocialMediaAccount account) {
		final HttpGet httpGet = new HttpGet(String.format(FACEBOOK_PAGE_STATISTICS, credentialLookupService.getCredential(account, CredentialType.FACEBOOK_PAGE_ID),
				credentialLookupService.getCredential(account, CredentialType.FACEBOOK_PAGE_ACCESSTOKEN)));
		try (final CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
			final HttpResponse httpResponse = httpClient.execute(httpGet);
			JSONObject json = JsonPath.read(httpResponse.getEntity().getContent(), "$");
			JSONObject jsonResult = new JSONObject();
			jsonResult.appendField("accountId", account.getId());
			jsonResult.appendField(TrackedStatistic.ACCOUNT_FOLLOWERS.key(), JsonPath.read(json, "$.fan_count"));
			jsonResult.appendField(TrackedStatistic.ACCOUNT_ENGAGED_USERS.key(),
					JsonPath.read(json, "$.insights.data[?(@.name=='page_engaged_users')][?(@.period=='days_28')].values[-1:].value"));
			jsonResult.appendField(TrackedStatistic.ACCOUNT_POST_COUNT.key(), JsonPath.read(json, "$.posts.data.length()"));
			jsonResult.appendField(TrackedStatistic.ACCOUNT_VIEWS.key(),
					JsonPath.read(json, "$.insights.data[?(@.name=='page_impressions')][?(@.period=='days_28')].values[-1:].value"));
			return jsonResult;

		} catch (UnsupportedOperationException | IOException e) {
			LOG.error("Could not retrieve tracked statistics from facebook for account {}", account.getId(), e);
			throw new FacebookException(String.format("Could not retrieve tracked statistics from facebook for account '%s': %s.", account.getId(), e.getMessage()), e);
		}
	}
}
