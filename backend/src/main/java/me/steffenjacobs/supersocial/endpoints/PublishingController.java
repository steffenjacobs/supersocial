package me.steffenjacobs.supersocial.endpoints;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import me.steffenjacobs.supersocial.domain.dto.MessagePublishingDTO;
import me.steffenjacobs.supersocial.domain.dto.PostDTO;
import me.steffenjacobs.supersocial.service.PostPublishingService;

/** @author Steffen Jacobs */
@RestController
public class PublishingController {

	private static final Logger LOG = LoggerFactory.getLogger(PublishingController.class);

	@Autowired
	private PostPublishingService postPublishingService;

	@PostMapping(path = "/api/publish", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Set<PostDTO>> publishMessage(@RequestBody MessagePublishingDTO messagePublishingDto) {
		LOG.info("Publish: {}", messagePublishingDto);
		Set<PostDTO> result = new HashSet<>();
		Set<PostDTO> posts = postPublishingService.createPosts(messagePublishingDto);
		for (PostDTO post : posts) {
			result.add(postPublishingService.publish(post));
		}
		return new ResponseEntity<>(result, HttpStatus.CREATED);
	}
}
