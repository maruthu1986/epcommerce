/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.repo.ext.health.monitor;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;

/**
 * Helper class used to get and set the health check url.
 */
public final class ApplicationUrlHelper {

	private static final Logger LOG = Logger.getLogger(ApplicationUrlHelper.class);

	private static String healthCheckUrl;
	private static final String CORTEX_HEALTHCHECK = "/cortex/healthcheck";

	private static final String CORTEX_HOST = "localhost";

	private ApplicationUrlHelper() {
		// Do nothing.
	}


	/**
	 * Extract and set the healthcheck url.
	 * @param url string containing the URL.
	 */
	public static void extractAndSetHealthCheckUrl(final String url) {
		try {
			URI uri = new URI(url);

			// Extract port if present.
			Integer port = uri.getPort();
			String portInfo = (port == -1) ? "" : new StringBuilder().append(":").append(port.toString()).toString();

			// Construct url.
			String newUrl = new StringBuilder()
					.append(uri.getScheme())
					.append("://")
					.append(CORTEX_HOST)
					.append(portInfo)
					.append(CORTEX_HEALTHCHECK)
					.toString();

			synchronized (ApplicationUrlHelper.class) {
				healthCheckUrl = newUrl;
			}

		} catch (URISyntaxException e) {
			LOG.error("Error with extractAndSetHealthCheckUrl: " + e.getMessage());
		}
	}

	/**
	 * Return healthcheck url.
	 * @return the health check url.
	 */
	public static String getHealthCheckUrl() {
		return healthCheckUrl;
	}

}
