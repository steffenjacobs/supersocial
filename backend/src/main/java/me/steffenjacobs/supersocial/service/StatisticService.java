package me.steffenjacobs.supersocial.service;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import org.elasticsearch.client.ResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import me.steffenjacobs.supersocial.domain.ElasticSearchConnector;
import me.steffenjacobs.supersocial.domain.dto.PostDTO;
import me.steffenjacobs.supersocial.domain.dto.SocialMediaAccountDTO;
import me.steffenjacobs.supersocial.domain.entity.Post;
import me.steffenjacobs.supersocial.domain.entity.SocialMediaAccount;
import me.steffenjacobs.supersocial.service.exception.AnalyticsException;
import me.steffenjacobs.supersocial.util.SuccessCallback;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

/**
 * Handles statistics for {@link Post posts} and {@link SocialMediaAccount
 * social media accounts}.
 * 
 * @author Steffen Jacobs
 */
@Component
public class StatisticService {
	private static final Logger LOG = LoggerFactory.getLogger(StatisticService.class);

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

	@Autowired
	private SocialMediaAccountService socialMediaAccountService;

	/**
	 * Retrieve all statistics associated to all {@link SocialMediaAccount
	 * social media accounts} the current user is allowed to view and aggregates
	 * them.
	 * 
	 * @return the aggregated JSON.
	 */
	public String getAllAccountStatistics(String query, Optional<Set<String>> selectedAccounts) {
		Stream<SocialMediaAccountDTO> accountStream = socialMediaAccountService.getAllSocialMediaAccounts();
		Collection<CompletableFuture<JSONArray>> futures = new LinkedList<CompletableFuture<JSONArray>>();
		JSONArray filteredAccounts = new JSONArray();
		if (selectedAccounts.isPresent()) {
			accountStream = accountStream.filter(a -> {
				if (selectedAccounts.get().contains(a.getId().toString())) {
					return true;
				}
				appendAccountToJson(filteredAccounts, a);
				return false;
			});
		}
		JSONArray accounts = new JSONArray();
		accountStream.forEach(acc -> {
			CompletableFuture<JSONArray> f = new CompletableFuture<>();
			elasticSearchConnector.find(query, String.format(ACCOUNT_INDEX_TEMPLATE, acc.getId()), false, createFutureCallback(f));
			futures.add(f);
			appendAccountToJson(accounts, acc);
		});
		return aggregateFutures(futures, accounts, filteredAccounts);
	}

	/**
	 * Retrieve all statistics associated to all {@link Post posts} the current
	 * user is allowed to view and aggregates them.
	 * 
	 * @return the aggregated JSON.
	 */
	public String getAllPostStatistics(String query, Optional<Set<String>> selectedPosts, Optional<Set<String>> selectedAccounts) {
		Collection<CompletableFuture<JSONArray>> futures = new LinkedList<CompletableFuture<JSONArray>>();
		Stream<PostDTO> postStream = postService.getAllPosts().stream().filter(p -> p.getPublished() != null);
		JSONArray filteredPosts = new JSONArray();

		// filter posts
		if (selectedPosts.isPresent()) {
			postStream = postStream.filter(p -> {
				if (selectedPosts.get().contains(p.getId().toString())) {
					return true;
				}
				appendPostToJson(filteredPosts, p);
				return false;
			});
		}

		// filter by account
		if (selectedAccounts.isPresent()) {
			postStream = postStream.filter(p -> {
				if (selectedAccounts.get().contains(p.getAccountId().toString())) {
					return true;
				}
				appendPostToJson(filteredPosts, p);
				return false;
			});
		}

		JSONArray posts = new JSONArray();
		postStream.forEach(post -> {
			CompletableFuture<JSONArray> f = new CompletableFuture<>();
			elasticSearchConnector.find(query, String.format(POST_INDEX_TEMPLATE, post.getId()), false, createFutureCallback(f));
			futures.add(f);
			appendPostToJson(posts, post);
		});
		return aggregateFutures(futures, posts, filteredPosts);
	}

	/**
	 * Appends data source information about the given
	 * {@link SocialMediaAccount} to the given {@link JSONArray}.
	 */
	private void appendAccountToJson(JSONArray accounts, SocialMediaAccountDTO acc) {
		JSONObject json = new JSONObject();
		json.put("id", acc.getId().toString());
		json.put("type", "account");
		json.put("platform", acc.getPlatformId());
		json.put("name", acc.getDisplayName());
		accounts.add(json);
	}

	/**
	 * Appends data source information about the given {@link Post} to the given
	 * {@link JSONArray}.
	 */
	private void appendPostToJson(JSONArray posts, PostDTO post) {
		JSONObject json = new JSONObject();
		json.put("id", post.getId().toString());
		json.put("type", "post");
		json.put("platform", post.getPlatformId());
		json.put("account", post.getAccountId());
		json.put("summary", post.getText().length() > 20 ? post.getText().substring(0, 17) + "..." : post.getText());
		posts.add(json);
	}

	/** Aggregates the JSON properties returned by all the futures. */
	@SuppressWarnings("unchecked")
	private String aggregateFutures(Collection<CompletableFuture<JSONArray>> futures, JSONArray searchedEntities, JSONArray filteredEntities) {
		LinkedHashMap<String, Double> map = new LinkedHashMap<String, Double>();
		futures.stream().map(t -> {
			try {
				return Optional.of(t.get());
			} catch (InterruptedException | ExecutionException e) {
				LOG.error("Could not aggregate statistics.", e);
				return Optional.<JSONArray> empty();
			}
		}).filter(o -> o.isPresent()).map(o -> o.get()).map(a -> (LinkedHashMap<String, ?>) a.get(0)).map(m -> (LinkedHashMap<String, ?>) m.get("_source")).forEach(c -> {
			for (String k : c.keySet()) {
				Double d = 0d;
				if (map.containsKey(k)) {
					d = map.get(k);
				}
				d += Double.valueOf("" + c.get(k));
				map.put(k, d);
			}
		});

		JSONArray json = new JSONArray();
		JSONObject src = new JSONObject();
		src.appendField("_source", new JSONObject(map));
		src.appendField("entities", searchedEntities);
		src.appendField("filtered", filteredEntities);
		json.add(src);

		return json.toString();
	}

	/**
	 * Completes the {@link CompletableFuture} as soon as the
	 * {@link SuccessCallback} is finished.
	 */
	private SuccessCallback<JSONArray> createFutureCallback(CompletableFuture<JSONArray> f) {
		return new SuccessCallback<JSONArray>() {
			@Override
			public void onSuccess(JSONArray json) {
				f.complete(json);
			}

			@Override
			public void onError(Exception e) {
				f.completeExceptionally(e);
			}
		};
	}

	/** Retrieve all statistics queried for a single post. */
	public String getPostStatistics(UUID postId, String query) {
		// permission + existence check
		postService.findOriginalPostById(postId);

		CompletableFuture<JSONArray> f = new CompletableFuture<>();
		elasticSearchConnector.find(query, String.format(POST_INDEX_TEMPLATE, postId), false, createFutureCallback(f));

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

	/**
	 * Fetch all statistics for the given {@link Post} and store them in an
	 * elasticsearch index.
	 */
	public void fetchAll(Post p) {
		switch (p.getSocialMediaAccountToPostWith().getPlatform()) {
		case TWITTER:
			elasticSearchConnector.insert(twitterService.fetchPostStatistics(p).toString(), String.format(POST_INDEX_TEMPLATE, p.getId()), UUID.randomUUID());
			break;
		case FACEBOOK:
			elasticSearchConnector.insert(facebookService.fetchPostStatistics(p).toString(), String.format(POST_INDEX_TEMPLATE, p.getId()), UUID.randomUUID());
			break;
		default:
			break;
		}
	}

	/**
	 * Fetch all statistics for the given {@link SocialMediaAccount account} and
	 * store them in an elasticsearch index.
	 */
	public void fetchAll(SocialMediaAccount account) {
		switch (account.getPlatform()) {
		case TWITTER:
			elasticSearchConnector.insert(twitterService.fetchAccountStatistics(account).toString(), String.format(ACCOUNT_INDEX_TEMPLATE, account.getId()), UUID.randomUUID());
			break;
		case FACEBOOK:
			elasticSearchConnector.insert(facebookService.fetchAccountStatistics(account).toString(), String.format(ACCOUNT_INDEX_TEMPLATE, account.getId()), UUID.randomUUID());
			break;
		default:
			break;
		}

	}

	/**
	 * Retrieve the currently trending topics from Twitter from the
	 * elasticsearch index.
	 * 
	 * @throws AnalyticsException
	 *             if the index was not yet created or there was another error
	 *             with the elasticsearch.
	 */
	public String getTrendingTopics(long woeid) {
		CompletableFuture<JSONArray> f = new CompletableFuture<>();
		elasticSearchConnector.find("{\"query\":{\"match_all\":{}}, \"sort\":{\"created\": \"desc\"}, \"size\": 1}",
				String.format(ScheduledTrendingTopicFetcher.TRENDING_INDEX_PATTERN, woeid), false, createFutureCallback(f));

		try {
			return f.get().toString();
		} catch (InterruptedException | ExecutionException e) {
			if (e.getCause() instanceof ResponseException) {
				if (e.getCause().getMessage().contains("index_not_found_exception")) {
					throw new AnalyticsException(String.format("Error retrieving analytics data: No trending topics for %s yet.", woeid), e);
				}
				if (e.getCause().getMessage().contains("json_parse_exception")) {
					throw new AnalyticsException(String.format("Error retrieving analytics data: Invalid query."), e);
				}
			}
			throw new AnalyticsException(String.format("Error retrieving analytics data: %s.", e.getMessage()), e);
		}
	}
}
