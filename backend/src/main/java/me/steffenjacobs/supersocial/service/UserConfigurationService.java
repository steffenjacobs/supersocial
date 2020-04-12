package me.steffenjacobs.supersocial.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jayway.jsonpath.JsonPath;

import me.steffenjacobs.supersocial.domain.SupersocialUserRepository;
import me.steffenjacobs.supersocial.domain.UserConfigurationRepository;
import me.steffenjacobs.supersocial.domain.dto.LocationDTO;
import me.steffenjacobs.supersocial.domain.dto.UserConfigurationDTO;
import me.steffenjacobs.supersocial.domain.entity.SecuredAction;
import me.steffenjacobs.supersocial.domain.entity.SupersocialUser;
import me.steffenjacobs.supersocial.domain.entity.UserConfiguration;
import me.steffenjacobs.supersocial.domain.entity.UserConfigurationType;
import me.steffenjacobs.supersocial.persistence.SystemConfigurationManager;
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

	@Autowired
	private TwitterService twitterService;

	@Autowired
	private SystemConfigurationManager systemConfigurationManager;
	
	@Autowired
	private ScheduledTrendingTopicFetcher scheduledTrendingTopicFetcher;

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
		UserConfiguration config = findConfigurationForUserByDescriptor(user, userConfig.getDescriptor()).orElseGet(UserConfiguration::new);
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
	 * Retrieve the associated {@link UserConfiguration} to the given
	 * {@code descriptor} from the given {@link SupersocialUser user}.
	 */
	private Optional<UserConfiguration> findConfigurationForUserByDescriptor(SupersocialUser user, String descriptor) {
		return user.getUserConfigurations().stream().filter(c -> c.getDescriptor() != null && c.getDescriptor().equals(descriptor)).findFirst();
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
		UserConfiguration config = findConfigurationForUserByDescriptor(user, descriptor).orElseThrow(() -> new UserConfigurationNotFoundException(descriptor));

		securityService.checkIfCurrentUserIsPermitted(config, SecuredAction.DELETE);
		user.getUserConfigurations().remove(config);
		supersocialUserRepository.save(user);

		userConfigurationRepository.deleteById(config.getId());
	}

	/**
	 * Update a the current user's location and append the associated woeid of
	 * the new location to be tracked by the
	 * {@link ScheduledTrendingTopicFetcher}.
	 */
	public LocationDTO updateUserLocation(LocationDTO userLocation) {
		SupersocialUser user = securityService.getCurrentUser();

		String locationResult = twitterService.fetchTwitterRegionForLatLng(userLocation.getLatitude(), userLocation.getLongitude(),
				systemConfigurationManager.getSystemTwitterAccount());

		createOrUpdateSetting("" + userLocation.getLatitude(), user, UserConfigurationType.Latitude.getKey());
		createOrUpdateSetting("" + userLocation.getLongitude(), user, UserConfigurationType.Longitude.getKey());
		createOrUpdateSetting(locationResult, user, UserConfigurationType.Location.getKey());
		long woeid = Long.valueOf("" + JsonPath.read(locationResult, "$[0].woeid"));
		systemConfigurationManager.appendToTrackedTrendsWoeids(woeid);
		scheduledTrendingTopicFetcher.fetchTrendingTopic(woeid);
		userLocation.setLocationName("" +JsonPath.read(locationResult, "$[0].name"));
		return userLocation;
	}

	/** Create or update a single user setting. */
	private UserConfiguration createOrUpdateSetting(String value, SupersocialUser user, String key) {
		UserConfiguration config = findConfigurationForUserByDescriptor(user, key).orElseGet(UserConfiguration::new);
		config.setDescriptor(key);
		config.setValue(value);
		config.setUser(user);
		boolean configLatCreated = config.getId() == null;
		config = userConfigurationRepository.save(config);
		if (configLatCreated) {
			securityService.appendCurrentUserAcl(config);
		}
		return config;
	}
}
