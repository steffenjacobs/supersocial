package me.steffenjacobs.supersocial.persistence;

import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import me.steffenjacobs.supersocial.domain.SocialMediaAccountRepository;
import me.steffenjacobs.supersocial.domain.SystemConfigurationRepository;
import me.steffenjacobs.supersocial.domain.dto.SystemConfigurationDTO;
import me.steffenjacobs.supersocial.domain.entity.SocialMediaAccount;
import me.steffenjacobs.supersocial.domain.entity.SystemConfiguration;
import me.steffenjacobs.supersocial.persistence.exception.SystemConfigurationNotFoundException;
import me.steffenjacobs.supersocial.persistence.exception.SystemConfigurationTypeNotFoundException;
import me.steffenjacobs.supersocial.service.exception.SocialMediaAccountNotFoundException;
import me.steffenjacobs.supersocial.util.Pair;

/**
 * Manages CRUD operations for system configuration objects.
 * 
 * @author Steffen Jacobs
 */
@Component
public class SystemConfigurationManager {

	public enum SystemConfigurationType {
		TWITTER_ACCOUNT_ID("twitter_account_id");

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

	/**
	 * Retrieve the system twitter account e.g. to load global statistics like
	 * trends.
	 */
	public SocialMediaAccount getSystemTwitterAccount() {
		SystemConfiguration twitterAccountId = systemConfigurationRepository.findByDescriptor(SystemConfigurationType.TWITTER_ACCOUNT_ID.getDescriptor())
				.orElseThrow(() -> new SystemConfigurationNotFoundException(SystemConfigurationType.TWITTER_ACCOUNT_ID));

		return socialMediaAccountRepository.findById(UUID.fromString(twitterAccountId.getValue())).orElseThrow(() -> new SocialMediaAccountNotFoundException(null));
	}

	/** Retrieve all configuration objects (without permission check). */
	public Stream<SystemConfigurationDTO> getAllConfigurations() {
		// TODO: permission check
		return StreamSupport.stream(systemConfigurationRepository.findAll().spliterator(), false).map(SystemConfigurationDTO::fromSystemConfiguration);
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
		// TODO: permission check
		SystemConfiguration systemConfiguration = systemConfigurationRepository.findByDescriptor(SystemConfigurationType.TWITTER_ACCOUNT_ID.getDescriptor())
				.orElse(new SystemConfiguration());
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
	 *         {@code <false,configuration>} if the already existing configuration
	 *         object was updated.
	 */
	public Pair<Boolean, SystemConfigurationDTO> createOrUpdate(SystemConfigurationDTO systemConfigurationDTO) {
		SystemConfigurationType type = SystemConfigurationType.fromDescriptor(systemConfigurationDTO.getDescriptor());
		switch (type) {
		case TWITTER_ACCOUNT_ID:
			final UUID parsedId = UUID.fromString(systemConfigurationDTO.getValue());
			SocialMediaAccount account = socialMediaAccountRepository.findById(parsedId).orElseThrow(() -> new SocialMediaAccountNotFoundException(parsedId));
			return setSystemTwitterAccount(account);
		default:
			throw new SystemConfigurationTypeNotFoundException(systemConfigurationDTO.getDescriptor());
		}
	}
}
