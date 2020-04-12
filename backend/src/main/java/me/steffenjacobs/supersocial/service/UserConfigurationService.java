package me.steffenjacobs.supersocial.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import me.steffenjacobs.supersocial.domain.SupersocialUserRepository;
import me.steffenjacobs.supersocial.domain.UserConfigurationRepository;
import me.steffenjacobs.supersocial.domain.dto.UserConfigurationDTO;
import me.steffenjacobs.supersocial.domain.entity.SecuredAction;
import me.steffenjacobs.supersocial.domain.entity.SupersocialUser;
import me.steffenjacobs.supersocial.domain.entity.UserConfiguration;
import me.steffenjacobs.supersocial.security.SecurityService;
import me.steffenjacobs.supersocial.service.exception.UserConfigurationNotFoundException;
import me.steffenjacobs.supersocial.util.Pair;

/**
 * Handles {@link UserConfiguration} objects which store settings in the user
 * context.
 * 
 * @author Steffen Jacobs
 */
@Component
public class UserConfigurationService {

	@Autowired
	private SecurityService securityService;

	@Autowired
	private UserConfigurationRepository userConfigurationRepository;

	@Autowired
	private SupersocialUserRepository supersocialUserRepository;

	/**
	 * Creates or updates a user configuration for the current user.
	 * 
	 * @return {@code <true,config>} if the user configuration was newly
	 *         created.</br>
	 *         {@code <false,config>} if the user configuration was merely
	 *         updated.
	 * 
	 * @throws me.steffenjacobs.supersocial.security.exception.AuthorizationException
	 *             if the current user is not allowed to update the associated
	 *             user or potentially existing user configuration.
	 */
	public Pair<Boolean, UserConfigurationDTO> createOrUpdateConfig(UserConfigurationDTO userConfig) {

		SupersocialUser user = securityService.getCurrentUser();
		UserConfiguration config = user.getUserConfigurations().stream().filter(c -> c.getDescriptor().equals(userConfig.getDescriptor())).findFirst()
				.orElseGet(UserConfiguration::new);
		boolean created = config.getId() == null;
		if (!created) {
			securityService.checkIfCurrentUserIsPermitted(config, SecuredAction.UPDATE);
		} else {
			config = userConfigurationRepository.save(config);
			securityService.appendCurrentUserAcl(config);
		}

		config.setUser(user);
		config.setDescriptor(userConfig.getDescriptor());
		config.setValue(userConfig.getValue());
		config = userConfigurationRepository.save(config);

		user.getUserConfigurations().add(config);
		supersocialUserRepository.save(user);

		return new Pair<>(created, UserConfigurationDTO.fromConfiguration(config));
	}

	/**
	 * Deletes a user configuration object for the current user.
	 * 
	 * @throws UserConfigurationNotFoundException
	 *             if the selected descriptor does not match any given user
	 *             configuration object
	 * @throws me.steffenjacobs.supersocial.security.exception.AuthorizationException
	 *             if the current user is not allowed to remove this
	 *             configuration.
	 */
	public void deleteUserConfigByDescriptor(String descriptor) {
		final SupersocialUser user = securityService.getCurrentUser();
		UserConfiguration config = user.getUserConfigurations().stream().filter(c -> c.getDescriptor().equals(descriptor)).findFirst()
				.orElseThrow(() -> new UserConfigurationNotFoundException(descriptor));

		securityService.checkIfCurrentUserIsPermitted(config, SecuredAction.DELETE);
		user.getUserConfigurations().remove(config);
		supersocialUserRepository.save(user);

		userConfigurationRepository.deleteById(config.getId());
	}
}
