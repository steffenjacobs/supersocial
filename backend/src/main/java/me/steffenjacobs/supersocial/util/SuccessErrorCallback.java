package me.steffenjacobs.supersocial.util;

/**
 * A simple callback that receives a generic type on success or an
 * {@link Exception} on error.
 * 
 * @author Steffen Jacobs
 */
public interface SuccessErrorCallback<T> {
	void onSuccess(T json);

	void onError(Exception e);
}
