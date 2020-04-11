package me.steffenjacobs.supersocial.persistence.exception;

import me.steffenjacobs.supersocial.persistence.SystemConfigurationManager;

/**
 * Should be fired if a system configuration of a specific type does not exist.
 * 
 * @author Steffen Jacobs
 */
public class SystemConfigurationNotFoundException extends RuntimeException {
	private static final long serialVersionUID = -5940797324238159301L;

	public SystemConfigurationNotFoundException(SystemConfigurationManager.SystemConfigurationType type) {
		super(String.format("No System Configuration with descriptor '%s' could be not found.", type.getDescriptor()));
	}
}
