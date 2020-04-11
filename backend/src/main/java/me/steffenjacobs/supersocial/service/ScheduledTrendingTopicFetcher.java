package me.steffenjacobs.supersocial.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
 * twitter_trending elasticsearch index.
 * 
 * @author Steffen Jacobs
 */
@Component
public class ScheduledTrendingTopicFetcher {

	private static final Logger LOG = LoggerFactory.getLogger(ScheduledTrendingTopicFetcher.class);
	private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("YYYY-MM-dd-HH-mm-ss");

	public static final String TRENDING_INDEX = "twitter_trending";
	public static final String TRENDING_INDEX_PATTERN = "{\"trends\": %s, \"created\": \"%s\"}";

	@Autowired
	private TwitterService twitterService;

	@Autowired
	private ElasticSearchConnector elasticSearchConnector;

	@Autowired
	private SystemConfigurationManager systemConfigurationManager;

	@PostConstruct
	public void setup() {
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::refresh, 1, 15, TimeUnit.MINUTES);
	}

	/** Fetches the trending topics from twitter. */
	public void refresh() {
		LOG.info("Fetching trending topics from twitter...");
		try {
			String result = twitterService.fetchTrendingTopics(1, systemConfigurationManager.getSystemTwitterAccount());
			elasticSearchConnector.insert(String.format(TRENDING_INDEX_PATTERN, result, DATE_TIME_FORMAT.format(new Date())), TRENDING_INDEX, UUID.randomUUID());
			LOG.info("Fetched trending topics from twitter: '{}'", result);
		} catch (SystemConfigurationNotFoundException e) {
			LOG.error("Could not fetch trending topics: System Twitter Account not set.");
		} catch (SocialMediaAccountNotFoundException e) {
			LOG.error("Could not fetch trending topics: System Twitter Account does not exist.");
		} catch (Exception e) {
			LOG.error("Could not fetch trending topics: ", e);
		}
	}
}
