package me.steffenjacobs.supersocial.endpoints;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import me.steffenjacobs.supersocial.domain.dto.CurrentUserDTO;
import me.steffenjacobs.supersocial.domain.dto.UserConfigurationDTO;
import me.steffenjacobs.supersocial.domain.dto.UserRegistrationDTO;
import me.steffenjacobs.supersocial.security.SecurityService;
import me.steffenjacobs.supersocial.security.UserService;
import me.steffenjacobs.supersocial.service.UserConfigurationService;
import me.steffenjacobs.supersocial.service.exception.UserConfigurationNotFoundException;
import me.steffenjacobs.supersocial.util.Pair;

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

	@Autowired
	private SecurityService securityService;

	@Autowired
	private UserConfigurationService userConfigurationService;

	/** Creates a new user. */
	@PostMapping(path = "/api/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public void registerUser(@RequestBody UserRegistrationDTO userRegistration) {
		LOG.info("Registering {}", userRegistration);
		userService.registerNewUser(userRegistration.getDisplayName(), userRegistration.getPassword(), userRegistration.getEmail());
	}

	/** @return the login status of the current user. */
	@GetMapping(path = "/api/loginstatus")
	public CurrentUserDTO getLoginStatus() throws Exception {
		return CurrentUserDTO.fromUser(securityService.getCurrentUser());
	}

	/**
	 * Creates or updates a user configuration object for the current user by
	 * its descriptor.
	 */
	@PutMapping(path = "api/user/config", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserConfigurationDTO> createUserConfig(@RequestBody UserConfigurationDTO userConfig) {
		LOG.info("Creating user config {}", userConfig);
		final Pair<Boolean, UserConfigurationDTO> acc = userConfigurationService.createOrUpdateConfig(userConfig);
		return new ResponseEntity<>(acc.getB(), acc.getA() ? HttpStatus.CREATED : HttpStatus.ACCEPTED);
	}

	/**
	 * Deletes a user configuration object for the current user by its
	 * descriptor
	 */
	@DeleteMapping(path = "api/user/config/{descriptor}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserConfigurationDTO> deleteUserConfig(@PathVariable(name = "descriptor") String descriptor) {
		LOG.info("Deleting user config {}", descriptor);
		try {
			userConfigurationService.deleteUserConfigByDescriptor(descriptor);
			return new ResponseEntity<UserConfigurationDTO>(HttpStatus.NO_CONTENT);
		} catch (UserConfigurationNotFoundException e) {
			return new ResponseEntity<>(new UserConfigurationDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}
}
