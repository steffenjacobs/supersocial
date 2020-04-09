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

/** @author Steffen Jacobs */
@RestController
public class AnalyticsEndpoint {
	private static final Logger LOG = LoggerFactory.getLogger(AnalyticsEndpoint.class);

	@Autowired
	private StatisticService statisticService;

	@GetMapping(path = "/api/analytics/post/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getStatistics(@PathVariable(name = "id") UUID id, @RequestParam(name = "query") String query) throws Exception {
		LOG.info("Retrieving all statistics for '{}'.", id);
		try {
			return new ResponseEntity<>(statisticService.getStatistics(id, query), HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error retrieving analytics for {} ({}): ", id, query);
			return new ResponseEntity<>(String.format("{\"error\": \"%s\"}", e.getMessage()), HttpStatus.NOT_FOUND);
		}
	}

}
