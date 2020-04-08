package me.steffenjacobs.supersocial.util;

import org.slf4j.LoggerFactory;

/** @author Steffen Jacobs */
public interface SuccessCallback extends SuccessErrorCallback {
	default void onError(Exception e) {
		LoggerFactory.getLogger(SuccessCallback.class).error("Error executing callback", e);
	}
}
