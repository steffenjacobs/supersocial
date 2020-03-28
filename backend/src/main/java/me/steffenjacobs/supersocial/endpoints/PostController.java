package me.steffenjacobs.supersocial.endpoints;

import java.util.HashSet;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import me.steffenjacobs.supersocial.domain.Platform;
import me.steffenjacobs.supersocial.domain.dto.MessagePublishingDTO;
import me.steffenjacobs.supersocial.domain.dto.PostDTO;
import me.steffenjacobs.supersocial.persistence.PostPersistenceManager;
import me.steffenjacobs.supersocial.persistence.exception.PostNotFoundException;

/** @author Steffen Jacobs */

@RestController
public class PostController {

	private static final Logger LOG = LoggerFactory.getLogger(PostController.class);

	@Autowired
	PostPersistenceManager postPersistenceManager;

	@GetMapping(path = "/api/post")
	public Set<PostDTO> getAllPublishedPosts() {
		LOG.info("Retrieving all posts");
		return postPersistenceManager.getAllPosts();
	}

	@GetMapping(path = "/api/post/{id}")
	public ResponseEntity<PostDTO> getPublishedPostById(@PathVariable(name = "id") UUID id) {
		LOG.info("Retrieving post with id {}", id);
		try {
			return new ResponseEntity<>(postPersistenceManager.findPostById(id), HttpStatus.OK);
		} catch (PostNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PutMapping(path = "/api/post", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Set<PostDTO>> createUnpublishedPost(@RequestBody MessagePublishingDTO message) throws Exception {
		LOG.info("Creating new post {}", message);
		Set<PostDTO> resultingPosts = new HashSet<>();
		for (String platform : message.getPlatforms()) {
			resultingPosts.add(PostDTO.fromPost(postPersistenceManager.storePost(message.getMessage(), Platform.fromId(Integer.parseInt(platform))), ""));
		}
		if (message.getPlatforms().size() == 0) {
			resultingPosts.add(PostDTO.fromPost(postPersistenceManager.storePost(message.getMessage(), Platform.UNKNOWN), ""));
		}
		return new ResponseEntity<>(resultingPosts, HttpStatus.ACCEPTED);
	}
}
