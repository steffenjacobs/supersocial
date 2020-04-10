package me.steffenjacobs.supersocial.endpoints;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import me.steffenjacobs.supersocial.service.FacebookService;
import me.steffenjacobs.supersocial.service.SocialMediaAccountService;

/**
 * Contains endpoints with actions to help with the social media integration of
 * Facebook accounts.
 * 
 * @author Steffen Jacobs
 */

@RestController
public class FacebookHelperController {

	@Autowired
	private FacebookService facebookService;

	@Autowired
	private SocialMediaAccountService socialMediaAccountService;

	/**
	 * Exchanges a given temporary user token into a longer-lived site token for
	 * a given social media account and it's associated facebook page.
	 */
	@GetMapping(path = "/api/facebook/exchange/{accountId}/{token}")
	public String exchangeToken(@PathVariable("accountId") String accountId, @PathVariable("token") String token) {
		return facebookService.exchangeForPageToken(socialMediaAccountService.findByIdNonDto(UUID.fromString(accountId)), token);
	}

}
