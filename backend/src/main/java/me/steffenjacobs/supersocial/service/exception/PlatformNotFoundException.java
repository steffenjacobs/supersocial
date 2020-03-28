package me.steffenjacobs.supersocial.service.exception;

/** @author Steffen Jacobs */
public class PlatformNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 5355304066397527849L;

	public PlatformNotFoundException(int platformId) {
		super(String.format("Platform %s does not exist.", platformId));
	}
}
