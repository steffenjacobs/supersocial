package me.steffenjacobs.supersocial.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import me.steffenjacobs.supersocial.domain.AccessControlListRepository;
import me.steffenjacobs.supersocial.domain.SocialMediaAccountRepository;
import me.steffenjacobs.supersocial.domain.SupersocialUserRepository;
import me.steffenjacobs.supersocial.domain.SystemConfigurationRepository;
import me.steffenjacobs.supersocial.domain.dto.SupersocialUserDTO;
import me.steffenjacobs.supersocial.domain.dto.SystemConfigurationDTO;
import me.steffenjacobs.supersocial.domain.entity.AccessControlList;
import me.steffenjacobs.supersocial.domain.entity.SecuredAction;
import me.steffenjacobs.supersocial.domain.entity.SocialMediaAccount;
import me.steffenjacobs.supersocial.domain.entity.SupersocialUser;
import me.steffenjacobs.supersocial.domain.entity.SystemConfiguration;
import me.steffenjacobs.supersocial.domain.entity.UserGroup;
import me.steffenjacobs.supersocial.persistence.exception.AccessControlListNotFoundException;
import me.steffenjacobs.supersocial.persistence.exception.SystemConfigurationNotFoundException;
import me.steffenjacobs.supersocial.persistence.exception.SystemConfigurationTypeNotFoundException;
import me.steffenjacobs.supersocial.security.SecurityService;
import me.steffenjacobs.supersocial.security.UserService;
import me.steffenjacobs.supersocial.security.exception.AuthorizationException;
import me.steffenjacobs.supersocial.service.exception.SocialMediaAccountNotFoundException;
import me.steffenjacobs.supersocial.util.Pair;

/**
 * Manages CRUD operations for system configuration objects.
 * 
 * @author Steffen Jacobs
 */
@Component
public class SystemConfigurationManager {
	private static final Logger LOG = LoggerFactory.getLogger(SystemConfigurationManager.class);

	public enum SystemConfigurationType {
		TWITTER_ACCOUNT_ID("twitter_account_id"), SYSTEM_ACL("system_acl"), SYSTEM_USER("system_user");

		private final String descriptor;

		private SystemConfigurationType(String descriptor) {
			this.descriptor = descriptor;
		}

		public String getDescriptor() {
			return descriptor;
		}

		public static SystemConfigurationType fromDescriptor(String descriptor) {
			for (SystemConfigurationType type : SystemConfigurationType.values()) {
				if (type.getDescriptor().equalsIgnoreCase(descriptor)) {
					return type;
				}
			}
			throw new SystemConfigurationTypeNotFoundException(descriptor);
		}
	}

	@Autowired
	private SystemConfigurationRepository systemConfigurationRepository;

	@Autowired
	private SocialMediaAccountRepository socialMediaAccountRepository;

	@Autowired
	private AccessControlListRepository accessControlListRepository;

	@Autowired
	private SecurityService securityService;

	@Autowired
	private UserService userService;

	@Autowired
	private SupersocialUserRepository supersocialUserRepository;

	@PostConstruct
	public void initializeSystemConfigurationIfNecessary() {
		LOG.info("Initializing system...");
		initializeSystemUserIfNecessary();
		initializeSystemAclIfNecessary();
		LOG.info("Initialization of system complete.");
	}

	private void initializeSystemUserIfNecessary() {
		try {
			getSystemUser();
			LOG.info("System user found.");
		} catch (SystemConfigurationNotFoundException | UsernameNotFoundException e) {
			String password = (UUID.randomUUID().toString() + UUID.randomUUID().toString()).replace("-", "");
			SupersocialUserDTO user = userService.registerNewUser("systemuser", password, "systemuser@supersocial.cloud");
			SystemConfiguration systemConfiguration = new SystemConfiguration();
			systemConfiguration.setDescriptor(SystemConfigurationType.SYSTEM_USER.getDescriptor());
			systemConfiguration.setValue(user.getUuid().toString());
			systemConfiguration = systemConfigurationRepository.save(systemConfiguration);
			// ACL for configuration object is added on first request because of
			// a missing context at this point in time.
			LOG.info("Created a new system user with password '{}'. Write this password down because it will not be displayed again.", password);
		}
	}

	private void initializeSystemAclIfNecessary() {
		try {
			// check if there is at least one user who on the ACL who can update
			// it
			boolean invalidAcl = true;
			for (Entry<UserGroup, SecuredAction> e : getSystemAcl().getPermittedActions().entrySet()) {
				if (securityService.implies(e.getValue(), SecuredAction.UPDATE) && e.getKey().getUsers().size() > 0) {
					invalidAcl = false;
					break;
				}
			}
			if (invalidAcl) {
				setupInitialAcl();
			} else {
				LOG.info("System ACL found.");
			}
		} catch (SystemConfigurationNotFoundException | AccessControlListNotFoundException e) {
			setupInitialAcl();
		}
	}

	private void setupInitialAcl() {
		AccessControlList acl = new AccessControlList();
		Map<UserGroup, SecuredAction> permittedActions = new HashMap<>();
		permittedActions.put(getSystemUser().getDefaultUserGroup(), SecuredAction.ALL);
		acl.setPermittedActions(permittedActions);
		acl = accessControlListRepository.save(acl);
		SystemConfiguration systemConfiguration = systemConfigurationRepository.findByDescriptor(SystemConfigurationType.SYSTEM_ACL.getDescriptor())
				.orElseGet(SystemConfiguration::new);
		systemConfiguration.setDescriptor(SystemConfigurationType.SYSTEM_ACL.getDescriptor());
		systemConfiguration.setValue(acl.getId().toString());
		systemConfigurationRepository.save(systemConfiguration);
		// ACL for configuration object is added on first request because of a
		// missing context at this point in time.
		LOG.info("Created a new system ACL.");
	}

	/**
	 * Retrieve the system twitter account e.g. to load global statistics like
	 * trends.
	 */
	public SocialMediaAccount getSystemTwitterAccount() {
		SystemConfiguration twitterAccountId = systemConfigurationRepository.findByDescriptor(SystemConfigurationType.TWITTER_ACCOUNT_ID.getDescriptor())
				.orElseThrow(() -> new SystemConfigurationNotFoundException(SystemConfigurationType.TWITTER_ACCOUNT_ID));

		return socialMediaAccountRepository.findById(UUID.fromString(twitterAccountId.getValue())).orElseThrow(() -> new SocialMediaAccountNotFoundException(null));
	}

	/** Retrieve all configuration objects. */
	public Stream<SystemConfigurationDTO> getAllConfigurations() {
		return securityService.filterForCurrentUser(StreamSupport.stream(systemConfigurationRepository.findAll().spliterator(), false).map(c -> {
			if (c.getAccessControlList() == null) {
				securityService.appendSystemAcl(c);
			}
			return c;
		}), SecuredAction.READ).map(SystemConfigurationDTO::fromSystemConfiguration);
	}

	/**
	 * Retrieve the system acl which contains system-wide permissions (without
	 * permission check).
	 */
	public AccessControlList getSystemAcl() {
		SystemConfiguration aclConfig = systemConfigurationRepository.findByDescriptor(SystemConfigurationType.SYSTEM_ACL.getDescriptor())
				.orElseThrow(() -> new SystemConfigurationNotFoundException(SystemConfigurationType.SYSTEM_ACL));

		UUID aclId = UUID.fromString(aclConfig.getValue());
		return accessControlListRepository.findById(aclId).orElseThrow(() -> new AccessControlListNotFoundException(aclId));
	}

	/** Retrieves the system user (without permission check). */
	public SupersocialUser getSystemUser() {
		SystemConfiguration userConfig = systemConfigurationRepository.findByDescriptor(SystemConfigurationType.SYSTEM_USER.getDescriptor())
				.orElseThrow(() -> new SystemConfigurationNotFoundException(SystemConfigurationType.SYSTEM_USER));

		return supersocialUserRepository.findById(UUID.fromString(userConfig.getValue())).orElseThrow(() -> new UsernameNotFoundException(userConfig.getValue()));
	}

	/**
	 * Set the system twitter account.
	 * 
	 * @return {@code <true,account>} if a new configuration object was
	 *         created.<br/>
	 *         {@code <false,account>} if the already existing configuration
	 *         object was updated.
	 */
	private Pair<Boolean, SystemConfigurationDTO> setSystemTwitterAccount(SocialMediaAccount account) {
		SystemConfiguration systemConfiguration = systemConfigurationRepository.findByDescriptor(SystemConfigurationType.TWITTER_ACCOUNT_ID.getDescriptor())
				.orElseGet(this::createSystemConfiguration);
		securityService.checkIfCurrentUserIsPermitted(systemConfiguration, SecuredAction.UPDATE);
		boolean created = systemConfiguration.getId() == null;
		systemConfiguration.setDescriptor(SystemConfigurationType.TWITTER_ACCOUNT_ID.getDescriptor());
		systemConfiguration.setValue(account.getId().toString());
		return new Pair<>(created, SystemConfigurationDTO.fromSystemConfiguration(systemConfigurationRepository.save(systemConfiguration)));
	}

	/**
	 * Create or update a given system configuration object.
	 * 
	 * @return {@code <true,configuration>} if a new configuration object was
	 *         created.<br/>
	 *         {@code <false,configuration>} if the already existing
	 *         configuration object was updated.
	 */
	public Pair<Boolean, SystemConfigurationDTO> createOrUpdate(SystemConfigurationDTO systemConfigurationDTO) {
		SystemConfigurationType type = SystemConfigurationType.fromDescriptor(systemConfigurationDTO.getDescriptor());
		switch (type) {
		case TWITTER_ACCOUNT_ID:
			final UUID parsedId = UUID.fromString(systemConfigurationDTO.getValue());
			SocialMediaAccount account = socialMediaAccountRepository.findById(parsedId).orElseThrow(() -> new SocialMediaAccountNotFoundException(parsedId));
			return setSystemTwitterAccount(account);
		case SYSTEM_ACL:
			throw new AuthorizationException(SystemConfigurationType.SYSTEM_ACL.getDescriptor(), SecuredAction.UPDATE);
		case SYSTEM_USER:
			throw new AuthorizationException(SystemConfigurationType.SYSTEM_USER.getDescriptor(), SecuredAction.UPDATE);

		default:
			throw new SystemConfigurationTypeNotFoundException(systemConfigurationDTO.getDescriptor());
		}
	}

	/**
	 * @return a newly created and already persisted system configuration
	 *         object.
	 */
	private SystemConfiguration createSystemConfiguration() {
		securityService.checkIfCurrentUserIsPermitted(getSystemAcl(), SecuredAction.CREATE);
		SystemConfiguration config = new SystemConfiguration();
		securityService.appendSystemAcl(config);
		return config;
	}
}
