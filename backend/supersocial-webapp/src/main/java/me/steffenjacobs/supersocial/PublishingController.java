package me.steffenjacobs.supersocial;

import java.util.Map;

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

	@PostMapping(path = "/api/publish", consumes = MediaType.APPLICATION_JSON_VALUE)
	public String publishMessage(@RequestBody MessagePublishingDTO messagePublishingDto) {
		StringBuilder sb = new StringBuilder();
		if (messagePublishingDto.getPlatforms().contains("twitter")) {
			long id = postPersistenceManager.storePost(messagePublishingDto.getMessage(), Platform.TWITTER).getId();
			String externalId = twitterService.tweet(messagePublishingDto.getMessage());
			postPersistenceManager.updateWithExternalId(id, externalId);
			sb.append("Tweeted").append(messagePublishingDto.getMessage()).append("\n");
		}
		if (messagePublishingDto.getPlatforms().contains("facebook")) {
			long id = postPersistenceManager.storePost(messagePublishingDto.getMessage(), Platform.FACEBOOK).getId();
			String result = facebookService.postMessage(messagePublishingDto.getMessage());
			Map<String, Object> json = JsonParserFactory.getJsonParser().parseMap(result);
			if(json.containsKey("error")) {
				LOG.error("Received error from facebook api: {0}", json);
				sb.append("Error posting to facebook\n");
			}
			postPersistenceManager.updateWithExternalId(id, "" + json.get("id"));
			sb.append("Posted to Facebook: ").append(messagePublishingDto.getMessage()).append("\n");
		}
		return sb.toString();
	}
}
