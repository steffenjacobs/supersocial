package me.steffenjacobs.supersocial;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import me.steffenjacobs.supersocial.domain.Platform;
import me.steffenjacobs.supersocial.domain.dto.MessagePublishingDTO;
import me.steffenjacobs.supersocial.persistence.PostPersistenceManager;
import me.steffenjacobs.supersocial.service.FacebookService;
import me.steffenjacobs.supersocial.service.TwitterService;

/** @author Steffen Jacobs */
@RestController
public class PublishingController {
	
	private static final Logger LOG = LoggerFactory.getLogger(PublishingController.class);

	@Autowired
	private TwitterService twitterService;

	@Autowired
	private FacebookService facebookService;
	
	@Autowired
	private PostPersistenceManager postPersistenceManager;

	@SuppressWarnings("unchecked")
	@PostMapping(path = "/api/publish", consumes = MediaType.APPLICATION_JSON_VALUE)
	public String publishMessage(@RequestBody MessagePublishingDTO messagePublishingDto) {
		LOG.info("Publish: {}", messagePublishingDto);
		StringBuilder sb = new StringBuilder();
		if (messagePublishingDto.getPlatforms().contains("twitter")) {
			UUID id = postPersistenceManager.storePost(messagePublishingDto.getMessage(), Platform.TWITTER).getId();
			String result = twitterService.tweet(messagePublishingDto.getMessage());
			Map<String, Object> json = JsonParserFactory.getJsonParser().parseMap(result);
			if(json.containsKey("errors")) {
				List<Map<String,?>> errors = (List<Map<String, ?>>) json.get("errors");
				for(Map<String,?> error : errors) {
					LOG.error("Received error from Twitter API: {}", json);
					sb.append(error.get("message"));
					postPersistenceManager.updateWithErrorMessage(id, "" + error.get("message"));
				}
			} else {
				postPersistenceManager.updateWithExternalId(id, "" + json.get("id"));
				sb.append("Posted to Twitter: ").append(messagePublishingDto.getMessage()).append("\n");
			}
		}
		if (messagePublishingDto.getPlatforms().contains("facebook")) {
			UUID id = postPersistenceManager.storePost(messagePublishingDto.getMessage(), Platform.FACEBOOK).getId();
			String result = facebookService.postMessage(messagePublishingDto.getMessage());
			Map<String, Object> json = JsonParserFactory.getJsonParser().parseMap(result);
			if(json.containsKey("error")) {
				Map<String, ?> error = (Map<String, ?>) json.get("error");
				LOG.error("Received error from Facebook API: {}", json);
				sb.append("Error posting to Facebook\n");
				postPersistenceManager.updateWithErrorMessage(id, "" + error.get("message"));
			} else {
				postPersistenceManager.updateWithExternalId(id, "" + json.get("id"));
				sb.append("Posted to Facebook: ").append(messagePublishingDto.getMessage()).append("\n");
			}
		}
		return sb.toString();
	}
}
