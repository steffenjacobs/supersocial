package me.steffenjacobs.supersocial;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import me.steffenjacobs.supersocial.domain.MessagePublishingDTO;
import me.steffenjacobs.supersocial.service.TwitterService;

/** @author Steffen Jacobs */
@RestController
public class PublishingController {
	
	@Autowired
	private TwitterService twitterService;

	@PostMapping(path = "/api/publish", consumes = MediaType.APPLICATION_JSON_VALUE)
	public String publishMessage(@RequestBody MessagePublishingDTO messagePublishingDto) {
		if(messagePublishingDto.getPlatforms().contains("twitter")) {
			twitterService.tweet(messagePublishingDto.getMessage());
			return "Tweeted" + messagePublishingDto.getMessage();
		}
		return "OK" + messagePublishingDto.toString();
	}
}
