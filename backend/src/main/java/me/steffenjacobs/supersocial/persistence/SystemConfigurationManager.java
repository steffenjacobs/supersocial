package me.steffenjacobs.supersocial.persistence;

import java.util.stream.Stream;

import me.steffenjacobs.supersocial.domain.dto.SystemConfigurationDTO;
import me.steffenjacobs.supersocial.domain.entity.AccessControlList;
import me.steffenjacobs.supersocial.domain.entity.SocialMediaAccount;
import me.steffenjacobs.supersocial.domain.entity.SupersocialUser;
import me.steffenjacobs.supersocial.util.Pair;

/** @author Steffen Jacobs */
public interface SystemConfigurationManager {

	void initializeSystemConfigurationIfNecessary();

	/**
	 * Retrieve the system twitter account e.g. to load global statistics like
	 * trends. No permission check.
	 */
	SocialMediaAccount getSystemTwitterAccount();

	/** Retrieve all configuration objects. */
	Stream<SystemConfigurationDTO> getAllConfigurations();

	/**
	 * Retrieve the system acl which contains system-wide permissions (without
	 * permission check).
	 */
	AccessControlList getSystemAcl();

	/** Retrieves the system user (without permission check). */
	SupersocialUser getSystemUser();

	/**
	 * Retrieves the currently tracked woeids for Twitter Trends plus 1 (world
	 * id).
	 */
	Stream<Long> getTrackedTrendsWoeids();

	/**
	 * Create or update a given system configuration object.
	 * 
	 * @return {@code <true,configuration>} if a new configuration object was
	 *         created.<br/>
	 *         {@code <false,configuration>} if the already existing
	 *         configuration object was updated.
	 */
	Pair<Boolean, SystemConfigurationDTO> createOrUpdate(SystemConfigurationDTO systemConfigurationDTO);

	/**
	 * Append a new woeid to the list of tracked woeids for Twitter Trends. No
	 * permission check.
	 */
	Pair<Boolean, SystemConfigurationDTO> appendToTrackedTrendsWoeids(long value);

}