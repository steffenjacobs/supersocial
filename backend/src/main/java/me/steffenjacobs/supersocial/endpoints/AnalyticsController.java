package me.steffenjacobs.supersocial.endpoints;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import me.steffenjacobs.supersocial.service.StatisticService;

/**
 * Contains all analytics-related endpoints like retrieveing statistics for
 * posts and social media accounts.
 * 
 * @author Steffen Jacobs
 */
@RestController
public class AnalyticsController {
	private static final String ERROR_JSON_PATTERN = "{\"error\": \"%s\"}";

	private static final Logger LOG = LoggerFactory.getLogger(AnalyticsController.class);

	@Autowired
	private StatisticService statisticService;

	/** Query all statistics for a given post. */
	@GetMapping(path = "/api/analytics/post/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getPostStatistics(@PathVariable(name = "id") UUID id, @RequestParam(name = "query") String query) throws Exception {
		LOG.info("Retrieving all statistics for '{}'.", id);
		try {
			return new ResponseEntity<>(statisticService.getPostStatistics(id, query), HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error retrieving analytics for {} ({}): ", id, query);
			return new ResponseEntity<>(String.format(ERROR_JSON_PATTERN, e.getMessage()), HttpStatus.NOT_FOUND);
		}
	}

	/** Query all statistics for all post. */
	@GetMapping(path = "/api/analytics/post", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getPostStatisticsForPosts(@RequestParam(name = "query") String query, @RequestParam(required = false, name = "posts") Set<String> posts,
			@RequestParam(required = false, name = "accounts") Set<String> accounts) throws Exception {
		LOG.info("Retrieving posts statistics for {} posts.", posts != null ? posts.size() : "all");
		try {
			return new ResponseEntity<>(statisticService.getAllPostStatistics(query, Optional.ofNullable(posts), Optional.ofNullable(accounts)), HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error retrieving all post analytics ({}): ", query, e);
			return new ResponseEntity<>(String.format(ERROR_JSON_PATTERN, e.getMessage()), HttpStatus.NOT_FOUND);
		}
	}

	/** Query all statistics for all social media accounts. */
	@GetMapping(path = "/api/analytics/account", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getAllAccountStatistics(@RequestParam(name = "query") String query, @RequestParam(required = false, name = "accounts") Set<String> accounts)
			throws Exception {
		LOG.info("Retrieving account statistics for {} accounts.", accounts != null ? accounts.size() : "all");
		try {
			return new ResponseEntity<>(statisticService.getAllAccountStatistics(query, Optional.ofNullable(accounts)), HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error retrieving all account analytics ({}): ", query, e);
			return new ResponseEntity<>(String.format(ERROR_JSON_PATTERN, e.getMessage()), HttpStatus.NOT_FOUND);
		}
	}

	/** Get trending topics from twitter. */
	@GetMapping(path = "/api/analytics/trending/{woeid}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getTrendingTopics(@PathVariable(name = "woeid") long woeid) throws Exception {
		LOG.info("Retrieving trending topics for region {}.", woeid);
		try {
			return new ResponseEntity<>(statisticService.getTrendingTopics(woeid), HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error retrieving trending topics: ", e);
			return new ResponseEntity<>(String.format(ERROR_JSON_PATTERN, e.getMessage()), HttpStatus.NOT_FOUND);
		}
	}

}
