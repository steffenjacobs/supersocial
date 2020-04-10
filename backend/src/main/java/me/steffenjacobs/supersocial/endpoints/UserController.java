package me.steffenjacobs.supersocial.endpoints;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import me.steffenjacobs.supersocial.domain.dto.UserRegistrationDTO;
import me.steffenjacobs.supersocial.security.UserService;

/**
 * Contains endpoints handling user-related actions.
 * 
 * @author Steffen Jacobs
 */
@RestController
public class UserController {
	private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;

	/** Creates a new user. */
	@PostMapping(path = "/api/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public void registerUser(@RequestBody UserRegistrationDTO userRegistration) {
		LOG.info("Registering {}", userRegistration);
		userService.registerNewUser(userRegistration.getDisplayName(), userRegistration.getPassword(), userRegistration.getEmail());
	}

}
