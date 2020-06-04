package me.steffenjacobs.supersocial.domain;

import java.util.UUID;

import me.steffenjacobs.supersocial.util.SuccessCallback;

/** @author Steffen Jacobs */
public interface ElasticSearchConnector {

	/**
	 * Inserts the given {@code json} object with the given {@code id} into the
	 * given {@code index}.
	 */
	void insert(String json, String index, UUID id);

	/**
	 * Create a new index with the given {@code json} object.
	 */
	void insertIndex(String json, String index);

	boolean hasIndex(String index);

	/**
	 * Find all objects via the given {@code query} in the given {@code index}.
	 * Calls the {@code callback} when complete.
	 * 
	 * @param pretty
	 *            pretty print JSON response if set to true.
	 */
	void find(String query, String index, boolean pretty, SuccessCallback<?> callback);

}