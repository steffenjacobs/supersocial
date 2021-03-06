package me.steffenjacobs.supersocial.endpoints;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import me.steffenjacobs.supersocial.domain.dto.MessagePublishingDTO;
import me.steffenjacobs.supersocial.domain.dto.PostDTO;
import me.steffenjacobs.supersocial.service.PostPublishingService;
import me.steffenjacobs.supersocial.service.PostService;
import me.steffenjacobs.supersocial.service.exception.CredentialMissingException;
import me.steffenjacobs.supersocial.service.exception.PlatformNotFoundException;
import me.steffenjacobs.supersocial.service.exception.SocialMediaAccountNotFoundException;

/**
 * Contains endpoints which handle publishing of posts
 * 
 * @author Steffen Jacobs
 */
@RestController
public class PublishingController {

	private static final Logger LOG = LoggerFactory.getLogger(PublishingController.class);

	@Autowired
	private PostService postService;

	@Autowired
	private PostPublishingService postPublishingService;

	/** Creates a new post and publishes it immediately. */
	@PostMapping(path = "/api/publish", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PostDTO> publishMessage(@RequestBody MessagePublishingDTO messagePublishingDto) {
		LOG.info("Publish: {}", messagePublishingDto);
		try {
			return new ResponseEntity<>(postService.createAndPublishPost(messagePublishingDto), HttpStatus.CREATED);
		} catch (SocialMediaAccountNotFoundException | CredentialMissingException | PlatformNotFoundException e) {
			return new ResponseEntity<>(new PostDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	/** Immediately publish an existing post. */
	@PostMapping(path = "/api/publishnow/{id}")
	public ResponseEntity<PostDTO> publishMessage(@PathVariable("id") UUID id) {
		LOG.info("Publish: {}", id);
		try {
			return new ResponseEntity<>(postPublishingService.publishNow(id), HttpStatus.CREATED);
		} catch (SocialMediaAccountNotFoundException | CredentialMissingException | PlatformNotFoundException e) {
			return new ResponseEntity<>(new PostDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}
}
