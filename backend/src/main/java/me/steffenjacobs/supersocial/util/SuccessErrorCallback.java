package me.steffenjacobs.supersocial.util;

import net.minidev.json.JSONArray;

/**
 * A simple callback that receives a {@link JSONArray} on success or an
 * {@link Exception} on error.
 * 
 * @author Steffen Jacobs
 */
public interface SuccessErrorCallback {
	void onSuccess(JSONArray json);

	void onError(Exception e);
}
