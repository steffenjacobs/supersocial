package me.steffenjacobs.supersocial.domain;

import java.io.IOException;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.ResponseListener;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.jayway.jsonpath.JsonPath;

import me.steffenjacobs.supersocial.util.SuccessCallback;
import me.steffenjacobs.supersocial.util.SuccessErrorCallback;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

/**
 * This class is an interface to Elasticsearch. It allows search and insert
 * operations on elastic search indices with lucene queries and pure JSON
 * objects.
 */
@Component
@PropertySource("classpath:application.properties")
public class ElasticSearchConnectorImpl implements ElasticSearchConnector {
	private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchConnectorImpl.class);

	@Value("${elasticsearch.host}")
	private String host;

	@Value("${elasticsearch.port}")
	private int port;

	@Value("${elasticsearch.protocol}")
	private String protocol;

	/**
	 * Inserts the given {@code json} object with the given {@code id} into the
	 * given {@code index}.
	 */
	@Override
	public void insert(String json, String index, UUID id) {
		try (RestClient restClient = RestClient.builder(new HttpHost(host, port, protocol)).build()) {
			LOG.info("Inserting into {}.", index);
			performInsert(restClient, json, index, id, (SuccessCallback<JSONArray>) e -> {});
		} catch (ParseException | IOException e) {
			LOG.error("Could not insert into elasticsearch index.", e);
		}
	}

	/**
	 * Create a new index with the given {@code json} object.
	 */
	@Override
	public void insertIndex(String json, String index) {
		try (RestClient restClient = RestClient.builder(new HttpHost(host, port, protocol)).build()) {
			LOG.info("Inserting index {} ({}).", index, json);
			performIndexInsert(restClient, json, index, (SuccessCallback<JSONArray>) e -> LOG.info(e.toString()));
		} catch (ParseException | IOException e) {
			LOG.error("Could not insert into elasticsearch index.", e);
		}
	}

	@Override
	public boolean hasIndex(String index) {
		try (RestClient restClient = RestClient.builder(new HttpHost(host, port, protocol)).build()) {
			Request request = new Request("HEAD", "/" + index);
			return restClient.performRequest(request).getStatusLine().getStatusCode() == HttpStatus.SC_OK;
		} catch (ParseException | IOException e) {
			LOG.error("Could not query for elasticsearch index.", e);
		}
		return false;
	}

	/**
	 * Find all objects via the given {@code query} in the given {@code index}.
	 * Calls the {@code callback} when complete.
	 * 
	 * @param pretty
	 *            pretty print JSON response if set to true.
	 */
	@Override
	public void find(String query, String index, boolean pretty, SuccessCallback<?> callback) {
		try (RestClient restClient = RestClient.builder(new HttpHost(host, port, protocol)).build()) {
			LOG.info("Finding {} in {}.", query, index);
			performSearchQuery(restClient, query, index, pretty, callback);
		} catch (ResponseException e) {
			LOG.error("Could not execute elasticsearch query: {}", e.getMessage());
		} catch (ParseException | IOException e) {
			LOG.error("Could not execute elasticsearch search query.", e);
		}
	}

	/** Perform a query search via the given {@code restClient}. */
	private void performSearchQuery(RestClient restClient, String searchQuery, String index, boolean pretty, SuccessErrorCallback<?> callback) {
		String matchQuery = StringUtils.isEmpty(searchQuery) ? "{\"query\" : { \"match_all\":{}}}" : searchQuery;
		HttpEntity entity = new NStringEntity(matchQuery, ContentType.APPLICATION_JSON);
		Request request = new Request("GET", index + "/_search");
		if (pretty) {
			request.addParameter("pretty", "true");
		}
		request.setEntity(entity);
		try {
			mapSearch(callback).onSuccess(restClient.performRequest(request));
		} catch (IOException e) {
			callback.onError(e);
		}
	}

	/**
	 * Perform an insert into the given index via the given {@code restClient}.
	 */
	private void performInsert(RestClient restClient, String json, String index, UUID id, SuccessErrorCallback<JSONArray> callback) {
		HttpEntity entity = new NStringEntity(json, ContentType.APPLICATION_JSON);
		Request request = new Request("PUT", "/" + index + "/_doc/" + id);
		request.setEntity(entity);
		ResponseListener rl = map(callback);
		try {
			rl.onSuccess(restClient.performRequest(request));
		} catch (IOException e) {
			rl.onFailure(e);
		}
	}

	/**
	 * Perform the insertion of a new index via the given {@code restClient}.
	 */
	private void performIndexInsert(RestClient restClient, String json, String index, SuccessErrorCallback<JSONArray> callback) {
		HttpEntity entity = new NStringEntity(json, ContentType.APPLICATION_JSON);
		Request request = new Request("PUT", "/" + index);
		request.setEntity(entity);
		ResponseListener rl = map(callback);
		try {
			rl.onSuccess(restClient.performRequest(request));
		} catch (IOException e) {
			rl.onFailure(e);
		}
	}

	/**
	 * Select the original JSON from the JSON response from the elasticsearch
	 * query.
	 */
	private ResponseListener mapSearch(SuccessErrorCallback<?> callback) {
		return new ResponseListener() {
			@Override
			public void onSuccess(Response response) {
				try {
					callback.onSuccess(JsonPath.read(response.getEntity().getContent(), "$.hits.hits..['_id', '_source']"));
				} catch (ParseException | IOException e) {
					callback.onError(e);
				}
			}

			@Override
			public void onFailure(Exception exception) {
				callback.onError(exception);
			}
		};
	}

	/** Maps a {@code SuccessErrorCallback} to a {@code ResponseListener}. */
	private ResponseListener map(SuccessErrorCallback<JSONArray> callback) {
		return new ResponseListener() {
			@Override
			public void onSuccess(Response response) {
				try {
					JSONArray arr = new JSONArray();
					arr.add(new JSONObject(JsonPath.read(response.getEntity().getContent(), "$")));
					callback.onSuccess(arr);
				} catch (ParseException | IOException e) {
					callback.onError(e);
				}
			}

			@Override
			public void onFailure(Exception exception) {
				callback.onError(exception);
			}
		};
	}
}
