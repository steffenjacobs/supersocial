package me.steffenjacobs.supersocial.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import me.steffenjacobs.supersocial.service.FacebookService;

/** @author Steffen Jacobs */

@RestController
public class FacebookHelperController {

	@Autowired
	private FacebookService facebookService;

	@GetMapping(path = "/api/facebook/exchange/{token}")
	public String exchangeToken(@PathVariable("token") String token) {
		return facebookService.exchangeForPageToken(token);
	}

}
