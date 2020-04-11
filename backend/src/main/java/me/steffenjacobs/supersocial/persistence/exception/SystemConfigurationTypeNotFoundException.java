package me.steffenjacobs.supersocial.persistence.exception;

/**
 * Should be fired if a system configuration of a type not familiar to the
 * system is requested.
 * 
 * @author Steffen Jacobs
 */
public class SystemConfigurationTypeNotFoundException extends RuntimeException {
	private static final long serialVersionUID = -1601608123883899044L;

	public SystemConfigurationTypeNotFoundException(String descriptor) {
		super(String.format("No System Configuration Type with descriptor '%s' could be not found.", descriptor));
	}
}
