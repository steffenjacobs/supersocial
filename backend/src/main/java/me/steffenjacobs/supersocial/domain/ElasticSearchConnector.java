package me.steffenjacobs.supersocial.domain;

import java.io.IOException;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.ParseException;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseListener;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jayway.jsonpath.JsonPath;

import me.steffenjacobs.supersocial.util.SuccessCallback;
import me.steffenjacobs.supersocial.util.SuccessErrorCallback;

@Component
public class ElasticSearchConnector {
	private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchConnector.class);

	@Value("${elasticsearch.host}")
	private String host;

	@Value("${elasticsearch.port}")
	private int port;

	@Value("${elasticsearch.protocol}")
	private String protocol;

	public void insert(String json, String index, UUID id) {
		try (RestClient restClient = RestClient.builder(new HttpHost(host, port, protocol)).build()) {
			LOG.info("Inserting {} to {}.", json, index);
			performInsert(restClient, json, index, id, (SuccessCallback) System.out::println);
		} catch (ParseException | IOException e) {
			LOG.error("Could not insert into elasticsearch index.", e);
		}
	}

	public void find(String query, String index, boolean pretty, SuccessCallback callback) {
		try (RestClient restClient = RestClient.builder(new HttpHost(host, port, protocol)).build()) {
			LOG.info("Finding {} in {}.", query, index);
			performSearchQuery(restClient, query, index, pretty, callback);
		} catch (ParseException | IOException e) {
			LOG.error("Could not execute elasticsearch search query.", e);
		}
	}

	private void performSearchQuery(RestClient restClient, String searchQuery, String index, boolean pretty, SuccessErrorCallback callback) {
		HttpEntity entity = new NStringEntity("{\"query\" : { \"match\": { " + searchQuery + "} } }", ContentType.APPLICATION_JSON);
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

	private void performInsert(RestClient restClient, String json, String index, UUID id, SuccessErrorCallback callback) {
		HttpEntity entity = new NStringEntity(json, ContentType.APPLICATION_JSON);
		Request request = new Request("POST", index + "/_doc/" + id);
		request.setEntity(entity);
		restClient.performRequestAsync(request, map(callback));
	}

	private ResponseListener mapSearch(SuccessErrorCallback callback) {
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

	private ResponseListener map(SuccessErrorCallback callback) {
		return new ResponseListener() {
			@Override
			public void onSuccess(Response response) {
				try {
					callback.onSuccess(JsonPath.read(response.getEntity().getContent(), "$"));
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
