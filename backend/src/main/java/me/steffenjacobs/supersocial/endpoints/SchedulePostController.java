package me.steffenjacobs.supersocial.endpoints;

import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import me.steffenjacobs.supersocial.domain.dto.LinkedScheduledPostDTO;
import me.steffenjacobs.supersocial.domain.dto.ScheduledPostDTO;
import me.steffenjacobs.supersocial.persistence.ScheduledPostPersistenceManager;
import me.steffenjacobs.supersocial.persistence.exception.ScheduledPostNotFoundException;
import me.steffenjacobs.supersocial.util.Pair;

/** @author Steffen Jacobs */

@RestController
public class SchedulePostController {
	private static final Logger LOG = LoggerFactory.getLogger(SchedulePostController.class);

	@Autowired
	ScheduledPostPersistenceManager scheduledPostPersistenceManager;

	@PutMapping(path = "/api/schedule/post", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ScheduledPostDTO> schedulePost(@RequestBody LinkedScheduledPostDTO post) throws Exception {
		LOG.info("Scheduling new post {}", post);
		Pair<ScheduledPostDTO, Boolean> result = scheduledPostPersistenceManager.scheduleOrUpdateScheduledPost(post);
		return new ResponseEntity<>(result.getA(), result.getB() ? HttpStatus.CREATED : HttpStatus.ACCEPTED);
	}

	@DeleteMapping(path = "/api/schedule/post/{id}")
	public ResponseEntity<ScheduledPostDTO> schedulePost(@PathVariable(name = "id") UUID id) throws Exception {
		LOG.info("Deleting scheduled post post {}", id);
		try {
			scheduledPostPersistenceManager.deleteScheduledPost(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (ScheduledPostNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping(path = "/api/schedule/post", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Set<ScheduledPostDTO>> getAllScheduledPosts() throws Exception {
		LOG.info("Retrieving all scheduled posts.");
		return new ResponseEntity<>(scheduledPostPersistenceManager.getAllScheduledPosts(), HttpStatus.OK);
	}
}
