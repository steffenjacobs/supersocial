package me.steffenjacobs.supersocial.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import me.steffenjacobs.supersocial.domain.ElasticSearchConnector;
import me.steffenjacobs.supersocial.persistence.SystemConfigurationManager;
import me.steffenjacobs.supersocial.persistence.exception.SystemConfigurationNotFoundException;
import me.steffenjacobs.supersocial.service.exception.SocialMediaAccountNotFoundException;

/**
 * Fetches the current twitter trends regulary and stores them into the
 * twitter_trending_{woeid} elasticsearch index.
 * 
 * @author Steffen Jacobs
 */
@Component
public class ScheduledTrendingTopicFetcher {

	private static final Logger LOG = LoggerFactory.getLogger(ScheduledTrendingTopicFetcher.class);
	private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

	public static final String TRENDING_INDEX_PATTERN = "twitter_trending_%s";
	public static final String TRENDING_INDEX_DOCUMENT_PATTERN = "{\"trends\": %s, \"created\": \"%s\"}";

	@Autowired
	private TwitterService twitterService;

	@Autowired
	private ElasticSearchConnector elasticSearchConnector;

	@Autowired
	private SystemConfigurationManager systemConfigurationManager;

	@PostConstruct
	public void setup() {
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::fetchTrendingTopics, 1, 15, TimeUnit.MINUTES);
	}

	/** Fetches the trending topics from twitter. */
	public void fetchTrendingTopics() {

		LOG.info("Fetching trending topics from twitter...");
		try {
			// Collect for deduplication
			systemConfigurationManager.getTrackedTrendsWoeids().collect(Collectors.toSet()).forEach(this::fetchTrendingTopic);
		} catch (SystemConfigurationNotFoundException e) {
			LOG.warn("No tracked topics. Retrieving global Twitter trends only.");
			fetchTrendingTopic(1);
		}
		LOG.info("Fetched all trending topics from twitter.");
	}

	/** Fetches the trending topics from twitter for a given {@code woeid}. */
	public void fetchTrendingTopic(long woeid) {
		final String indexName = String.format(TRENDING_INDEX_PATTERN, woeid);
		createIndexIfNecessary(indexName);
		try {
			String result = twitterService.fetchTrendingTopics(woeid, systemConfigurationManager.getSystemTwitterAccount());
			elasticSearchConnector.insert(String.format(TRENDING_INDEX_DOCUMENT_PATTERN, result, dateTimeFormat.format(new Date())), indexName, UUID.randomUUID());
			LOG.info("Fetched trending topics from twitter for woeid '{}'.", woeid);
		} catch (SystemConfigurationNotFoundException e) {
			LOG.error("Could not fetch trending topics: System Twitter Account not set.");
		} catch (SocialMediaAccountNotFoundException e) {
			LOG.error("Could not fetch trending topics: System Twitter Account does not exist.");
		} catch (Exception e) {
			LOG.error("Could not fetch trending topics: ", e);
		}
	}

	/** Create an elasticsearch index for the trends if none exists yet. */
	private void createIndexIfNecessary(String index) {
		if (elasticSearchConnector.hasIndex(index)) {
			return;
		}
		try {
			elasticSearchConnector.insertIndex("{\"mappings\": {\"properties\": {\"created\": {\"type\":  \"date\", \"format\": \"yyyy-MM-dd-HH-mm-ss\"} } } }", index);
			LOG.info("Created index '{}'.", index);
		} catch (Exception e) {
			LOG.error("Could not create index.", e);
		}
	}
}
