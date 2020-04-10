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
import me.steffenjacobs.supersocial.persistence.ScheduledPostService;
import me.steffenjacobs.supersocial.persistence.exception.PostAlreadyScheduledException;
import me.steffenjacobs.supersocial.persistence.exception.PostNotFoundException;
import me.steffenjacobs.supersocial.persistence.exception.ScheduledPostNotFoundException;
import me.steffenjacobs.supersocial.util.Pair;

/**
 * Contains endpoints handling scheduling of posts.
 * 
 * @author Steffen Jacobs
 */

@RestController
public class SchedulePostController {
	private static final Logger LOG = LoggerFactory.getLogger(SchedulePostController.class);

	@Autowired
	ScheduledPostService scheduledPostService;

	/**
	 * Schedule an existing post or update the already present scheduling
	 * information.
	 */
	@PutMapping(path = "/api/schedule/post", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ScheduledPostDTO> schedulePost(@RequestBody LinkedScheduledPostDTO post) throws Exception {
		LOG.info("Scheduling new post {}", post);
		try {
			Pair<ScheduledPostDTO, Boolean> result = scheduledPostService.scheduleOrUpdateScheduledPost(post);
			return new ResponseEntity<>(result.getA(), result.getB() ? HttpStatus.CREATED : HttpStatus.ACCEPTED);
		} catch (PostAlreadyScheduledException e) {
			return new ResponseEntity<>(HttpStatus.FOUND);
		} catch (PostNotFoundException e2) {
			return new ResponseEntity<>(new ScheduledPostDTO(e2.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Delete the associated scheduling information of a post without deleting
	 * the post itself.
	 */
	@DeleteMapping(path = "/api/schedule/post/{id}")
	public ResponseEntity<ScheduledPostDTO> deleteScheduledPost(@PathVariable(name = "id") UUID id) throws Exception {
		LOG.info("Deleting scheduled post {}", id);
		try {
			scheduledPostService.deleteScheduledPost(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (ScheduledPostNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	/** Retrieve all scheduled posts. */
	@GetMapping(path = "/api/schedule/post", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Set<ScheduledPostDTO>> getAllScheduledPosts() throws Exception {
		LOG.info("Retrieving all scheduled posts.");
		return new ResponseEntity<>(scheduledPostService.getAllScheduledPosts(), HttpStatus.OK);
	}
}
