package me.steffenjacobs.supersocial.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.stereotype.Component;

import me.steffenjacobs.supersocial.domain.Platform;
import me.steffenjacobs.supersocial.domain.dto.PostDTO;
import me.steffenjacobs.supersocial.domain.entity.Post;
import me.steffenjacobs.supersocial.persistence.PostPersistenceManager;
import me.steffenjacobs.supersocial.service.exception.PlatformNotFoundException;

/** @author Steffen Jacobs */
@Component
public class PostPublishingService {

	private static final Logger LOG = LoggerFactory.getLogger(PostPublishingService.class);

	@Autowired
	private FacebookService facebookService;

	@Autowired
	private TwitterService twitterService;

	@Autowired
	private PostPersistenceManager postPersistenceManager;
	
	@Autowired
	private PostService postService;

	@SuppressWarnings("unchecked")
	public PostDTO publish(Post post) {
		if (post.getSocialMediaAccountToPostWith().getPlatform() == Platform.FACEBOOK) {
			String result = facebookService.postMessage(post);
			Map<String, Object> json = JsonParserFactory.getJsonParser().parseMap(result);
			if (json.containsKey("error")) {
				Map<String, ?> error = (Map<String, ?>) json.get("error");
				LOG.error("Received error from Facebook API: {}", json);
				return postPersistenceManager.updateWithErrorMessage(post.getId(), "" + error.get("message"));
			} else {
				LOG.info("Published post on Facebook: {}", post);
				return postPersistenceManager.updateWithExternalId(post.getId(), "" + json.get("id"));
			}
		} else if (post.getSocialMediaAccountToPostWith().getPlatform() == Platform.TWITTER) {
			String result = twitterService.tweet(post);
			Map<String, Object> json = JsonParserFactory.getJsonParser().parseMap(result);
			if (json.containsKey("errors")) {
				List<Map<String, ?>> errors = (List<Map<String, ?>>) json.get("errors");
				for (Map<String, ?> error : errors) {
					LOG.error("Received error from Twitter API: {}", json);
					postPersistenceManager.updateWithErrorMessage(post.getId(), "" + error.get("message"));
				}
				return postService.findPostById(post.getId());
			} else {
				LOG.info("Published post on Twitter: {}", post);
				return postPersistenceManager.updateWithExternalId(post.getId(), "" + json.get("id"));
			}
		} else {
			throw new PlatformNotFoundException(post.getSocialMediaAccountToPostWith().getPlatform());
		}
	}
}
