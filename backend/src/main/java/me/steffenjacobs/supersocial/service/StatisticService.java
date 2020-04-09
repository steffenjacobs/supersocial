package me.steffenjacobs.supersocial.service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.client.ResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import me.steffenjacobs.supersocial.domain.ElasticSearchConnector;
import me.steffenjacobs.supersocial.domain.entity.Post;
import me.steffenjacobs.supersocial.domain.entity.SocialMediaAccount;
import me.steffenjacobs.supersocial.service.exception.AnalyticsException;
import me.steffenjacobs.supersocial.util.SuccessCallback;
import net.minidev.json.JSONArray;

/** @author Steffen Jacobs */
@Component
public class StatisticService {
	private static final String POST_INDEX_TEMPLATE = "post_%s";
	private static final String ACCOUNT_INDEX_TEMPLATE = "account_%s";

	@Autowired
	private FacebookService facebookService;

	@Autowired
	private TwitterService twitterService;

	@Autowired
	private ElasticSearchConnector elasticSearchConnector;

	@Autowired
	private PostService postService;

	public String getStatistics(UUID postId, String query) {
		// permission + existence check
		postService.findOriginalPostById(postId);

		CompletableFuture<JSONArray> f = new CompletableFuture<>();
		elasticSearchConnector.find(query, String.format(POST_INDEX_TEMPLATE, postId), false, new SuccessCallback() {

			@Override
			public void onSuccess(JSONArray json) {
				f.complete(json);
			}

			public void onError(Exception e) {
				f.completeExceptionally(e);
			};
		});

		try {
			return f.get().toString();
		} catch (InterruptedException | ExecutionException e) {
			if (e.getCause() instanceof ResponseException) {
				if (e.getCause().getMessage().contains("index_not_found_exception")) {
					throw new AnalyticsException(String.format("Error retrieving analytics data: No analytics data for '%s'.", postId), e);
				}
				if (e.getCause().getMessage().contains("json_parse_exception")) {
					throw new AnalyticsException(String.format("Error retrieving analytics data: Invalid query '%s'.", query), e);
				}
			}
			throw new AnalyticsException(String.format("Error retrieving analytics data: %s.", e.getMessage()), e);
		}
	}

	public void fetchAll(Post p) {
		switch (p.getSocialMediaAccountToPostWith().getPlatform()) {
		case TWITTER:
			// twitterService.fetchPostStatistics(p.getExternalId()).forEach(trackedStatisticRepository::save);
			break;
		case FACEBOOK:
			elasticSearchConnector.insert(facebookService.fetchPostStatistics(p).toString(), String.format(POST_INDEX_TEMPLATE, p.getId()), UUID.randomUUID());
			break;
		default:
			break;
		}
	}

	public void fetchAll(SocialMediaAccount account) {
		switch (account.getPlatform()) {
		case TWITTER:
			// twitterService.fetchAccountStatistics(account).forEach(trackedStatisticRepository::save);
			break;
		case FACEBOOK:
			elasticSearchConnector.insert(facebookService.fetchAccountStatistics(account).toString(), String.format(ACCOUNT_INDEX_TEMPLATE, account.getId()), UUID.randomUUID());
			break;
		default:
			break;
		}

	}
}
