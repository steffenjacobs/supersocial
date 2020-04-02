package me.steffenjacobs.supersocial.service.exception;

import me.steffenjacobs.supersocial.domain.Platform;

/** @author Steffen Jacobs */
public class PlatformNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 5355304066397527849L;

	public PlatformNotFoundException(Platform platform) {
		super(String.format("Selected platform (id=%s) does not exist.", platform.getId()));
	}
}
