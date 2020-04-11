package me.steffenjacobs.supersocial.util;

import org.slf4j.LoggerFactory;

/**
 * Basically a {@link SuccessErrorCallback} that logs the error with a logger if
 * the onError method is not overwritten.
 * 
 * @author Steffen Jacobs
 */
public interface SuccessCallback<T> extends SuccessErrorCallback<T> {
	default void onError(Exception e) {
		LoggerFactory.getLogger(SuccessCallback.class).error("Error executing callback", e);
	}
}
