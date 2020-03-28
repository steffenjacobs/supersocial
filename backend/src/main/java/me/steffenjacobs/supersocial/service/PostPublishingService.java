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

	@SuppressWarnings("unchecked")
	public PostDTO publish(PostDTO post) {
		if (post.getPlatformId() == Platform.FACEBOOK.getId()) {
			String result = facebookService.postMessage(post.getText());
			Map<String, Object> json = JsonParserFactory.getJsonParser().parseMap(result);
			if (json.containsKey("error")) {
				Map<String, ?> error = (Map<String, ?>) json.get("error");
				LOG.error("Received error from Facebook API: {}", json);
				return postPersistenceManager.updateWithErrorMessage(post.getId(), "" + error.get("message"));
			} else {
				return postPersistenceManager.updateWithExternalId(post.getId(), "" + json.get("id"));
			}
		} else if (post.getPlatformId() == Platform.TWITTER.getId()) {
			String result = twitterService.tweet(post.getText());
			Map<String, Object> json = JsonParserFactory.getJsonParser().parseMap(result);
			if (json.containsKey("errors")) {
				List<Map<String, ?>> errors = (List<Map<String, ?>>) json.get("errors");
				for (Map<String, ?> error : errors) {
					LOG.error("Received error from Twitter API: {}", json);
					postPersistenceManager.updateWithErrorMessage(post.getId(), "" + error.get("message"));
				}
				return postPersistenceManager.findPostById(post.getId());
			} else {
				return postPersistenceManager.updateWithExternalId(post.getId(), "" + json.get("id"));
			}
		} else {
			throw new PlatformNotFoundException(post.getPlatformId());
		}
	}
}
