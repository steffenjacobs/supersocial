package me.steffenjacobs.supersocial.service.exception;

/**
 * Should be fired if the selected
 * {@link me.steffenjacobs.supersocial.domain.entity.UserConfiguration} does not
 * exist or the current user is not permitted to use it.
 * 
 * @author Steffen Jacobs
 */
public class UserConfigurationNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 7789826486660721650L;

	public UserConfigurationNotFoundException(String descriptor) {
		super(String.format("User configuration with descriptor '%s' does not exist.", descriptor));
	}
}
