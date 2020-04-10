package me.steffenjacobs.supersocial.endpoints;

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
public class AnalyticsEndpoint {
	private static final Logger LOG = LoggerFactory.getLogger(AnalyticsEndpoint.class);

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
			return new ResponseEntity<>(String.format("{\"error\": \"%s\"}", e.getMessage()), HttpStatus.NOT_FOUND);
		}
	}

	/** Query all statistics for all post. */
	@GetMapping(path = "/api/analytics/post", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getAllPostStatistics(@RequestParam(name = "query") String query) throws Exception {
		LOG.info("Retrieving all posts statistics.");
		try {
			return new ResponseEntity<>(statisticService.getAllPostStatistics(query), HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error retrieving all post analytics ({}): ", query, e);
			return new ResponseEntity<>(String.format("{\"error\": \"%s\"}", e.getMessage()), HttpStatus.NOT_FOUND);
		}
	}

	/** Query all statistics for all social media accounts. */
	@GetMapping(path = "/api/analytics/account", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getAllAccountStatistics(@RequestParam(name = "query") String query) throws Exception {
		LOG.info("Retrieving all account statistics.");
		try {
			return new ResponseEntity<>(statisticService.getAllAccountStatistics(query), HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error retrieving all account analytics ({}): ", query, e);
			return new ResponseEntity<>(String.format("{\"error\": \"%s\"}", e.getMessage()), HttpStatus.NOT_FOUND);
		}
	}

}
