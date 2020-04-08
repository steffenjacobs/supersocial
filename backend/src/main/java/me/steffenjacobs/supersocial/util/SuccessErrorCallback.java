package me.steffenjacobs.supersocial.util;

import net.minidev.json.JSONArray;

/** @author Steffen Jacobs */
public interface SuccessErrorCallback {
	void onSuccess(JSONArray json);

	void onError(Exception e);
}
