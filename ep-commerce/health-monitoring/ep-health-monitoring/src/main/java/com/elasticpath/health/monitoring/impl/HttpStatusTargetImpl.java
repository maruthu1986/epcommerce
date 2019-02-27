/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.health.monitoring.impl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;

import com.elasticpath.health.monitoring.ResponseValidator;
import com.elasticpath.health.monitoring.Status;
import com.elasticpath.health.monitoring.StatusType;

/**
 * Implementation of StatusCheckerTarget which checks provided URL. URL should responde with JSON object or just give a status code.
 */
public class HttpStatusTargetImpl extends AbstractStatusCheckerTarget {

	private static final Logger LOG = Logger.getLogger(HttpStatusTargetImpl.class);

	private static final int NANOSECONDS_IN_1_MS = 1000000;
	private static final String URL_ERR_PREFIX = "URL = ";
	private static final String UTF_8 = "UTF-8";

	/**
	 * Connection timeout for HttpURLConnection.
	 */
	private static final int DEFAULT_CONNECT_TIMEOUT = 5 * 1000; // in milliseconds

	/**
	 * URL to check and get response with JSON object or just a status code.
	 */
	private String url;

	/**
	 * The optional validator of the response body returned from the HTTP call.
	 */
	private ResponseValidator<String> responseBodyValidator;

	private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;

	/**
	 * Hits URL for test.
	 *
	 * @return null status.
	 */
	@Override
	public Status check() {
		LOG.info("Checking URL status: name = " + getName() + ", URL = " + getUrl());

		HttpURLConnection conn = null;
		int statusInt;
		StopWatch watch = new StopWatch();

		try {
			watch.start();
			conn = (HttpURLConnection) new URL(getUrl()).openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setConnectTimeout(getConnectTimeout());
			conn.connect();
			statusInt = conn.getResponseCode();
			watch.stop();

			// If we have a valid response code and a response body validator supplied then validate the body
			final ResponseValidator<String> stringResponseValidator = getResponseBodyValidator();
			if (statusInt == HttpURLConnection.HTTP_OK && stringResponseValidator != null) {
				final String responseBody = IOUtils.toString(conn.getInputStream(), UTF_8);
				return stringResponseValidator.validate(responseBody);
			}
		} catch (MalformedURLException e) {
			return createStatus(StatusType.UNKNOWN, "Malformed URL " + e.getMessage(), URL_ERR_PREFIX + url);
		} catch (ProtocolException e) {
			return createStatus(StatusType.UNKNOWN, "Protocol exception " + e.getMessage(), URL_ERR_PREFIX + url);
		} catch (IOException e) {
			return createStatus(StatusType.CRITICAL, "IO Error " + e.getMessage(), URL_ERR_PREFIX + url);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		LOG.info("Response code: " + statusInt);
		// expecting JSON in response
		if (statusInt == HttpURLConnection.HTTP_OK) {
			return createStatus(StatusType.OK, "Target was successfully reached in "
					+ watch.getNanoTime() / NANOSECONDS_IN_1_MS + "ms", URL_ERR_PREFIX
					+ url);
		}

		return createStatus(StatusType.CRITICAL, "Got a non-OK status: " + statusInt, URL_ERR_PREFIX + url);
	}

	protected String getUrl() {
		return url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	protected int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(final int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	protected ResponseValidator<String> getResponseBodyValidator() {
		return responseBodyValidator;
	}

	public void setResponseBodyValidator(final ResponseValidator<String> responseBodyValidator) {
		this.responseBodyValidator = responseBodyValidator;
	}
}
