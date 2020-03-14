package me.steffenjacobs.supersocial;

import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private TwitterService twitterService;

	@Autowired
	private FacebookService facebookService;
	
	@Autowired
	private PostPersistenceManager postPersistenceManager;

	@PostMapping(path = "/api/publish", consumes = MediaType.APPLICATION_JSON_VALUE)
	public String publishMessage(@RequestBody MessagePublishingDTO messagePublishingDto) {
		if (messagePublishingDto.getPlatforms().contains("twitter")) {
			long id = postPersistenceManager.storePost(messagePublishingDto.getMessage(), Platform.TWITTER).getId();
			String externalId = twitterService.tweet(messagePublishingDto.getMessage());
			postPersistenceManager.updateWithExternalId(id, externalId);
			return "Tweeted" + messagePublishingDto.getMessage();
		}
		if (messagePublishingDto.getPlatforms().contains("facebook")) {
			long id = postPersistenceManager.storePost(messagePublishingDto.getMessage(), Platform.FACEBOOK).getId();
			String externalId = facebookService.postMessage(messagePublishingDto.getMessage());
			postPersistenceManager.updateWithExternalId(id, externalId);
			return "Posted to Facebook: " + messagePublishingDto.getMessage();
		}
		return "Not posted anywhere: " + messagePublishingDto.toString();
	}
}
