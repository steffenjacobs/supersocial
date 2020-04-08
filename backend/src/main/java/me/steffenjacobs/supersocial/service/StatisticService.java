package me.steffenjacobs.supersocial.service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import me.steffenjacobs.supersocial.domain.ElasticSearchConnector;
import me.steffenjacobs.supersocial.domain.entity.Post;
import me.steffenjacobs.supersocial.domain.entity.SocialMediaAccount;
import me.steffenjacobs.supersocial.service.exception.AnalyticsException;
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
		elasticSearchConnector.find(query, String.format(POST_INDEX_TEMPLATE, postId), false, f::complete);

		try {
			return f.get().toString();
		} catch (InterruptedException | ExecutionException e) {
			throw new AnalyticsException("Error retrieving analytics data", e);
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
