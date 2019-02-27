/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.repo.ext.health.monitor;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test constructed urls.
 */
public class ApplicationUrlHelperTest {

	private static final String INCORRECT_URL_GENERATED = "Incorrect URL generated";
	private static final String HEALTH_CHECK_URL = "http://localhost:9080/cortex/healthcheck";

	private static final String STATUS_URL = "http://localhost:9080/cortex/status";
	private static final String STATUS_INFO_URL = "http://localhost:9080/cortex/status/info.html";
	private static final String STATUS_JSON_URL = "http://localhost:9080/cortex/status/info.json";

	private static final String CORTEX_HEALTH_CHECK_URL = "http://localhost/cortex/healthcheck";

	@Test
	public void extractAndSetHealthCheckUrlForStatus() {
		ApplicationUrlHelper.extractAndSetHealthCheckUrl(STATUS_URL);
		assertEquals(INCORRECT_URL_GENERATED, HEALTH_CHECK_URL, ApplicationUrlHelper.getHealthCheckUrl());
	}

	@Test
	public void extractAndSetHealthCheckUrlForStatusInfo() {
		ApplicationUrlHelper.extractAndSetHealthCheckUrl(STATUS_INFO_URL);
		assertEquals(INCORRECT_URL_GENERATED, HEALTH_CHECK_URL, ApplicationUrlHelper.getHealthCheckUrl());
	}

	@Test
	public void extractAndSetHealthCheckUrlForStatusJSON() {
		ApplicationUrlHelper.extractAndSetHealthCheckUrl(STATUS_JSON_URL);
		assertEquals(INCORRECT_URL_GENERATED, HEALTH_CHECK_URL, ApplicationUrlHelper.getHealthCheckUrl());
	}

	@Test
	public void extractAndSetHealthCheckUrlForDuplicatedStatusString() {
		ApplicationUrlHelper.extractAndSetHealthCheckUrl("http://status.test.com/status");
		assertEquals(INCORRECT_URL_GENERATED, CORTEX_HEALTH_CHECK_URL, ApplicationUrlHelper.getHealthCheckUrl());
	}

	@Test
	public void extractAndSetHealthCheckUrlForDuplicatedStatusStringAtTheEnd() {
		ApplicationUrlHelper.extractAndSetHealthCheckUrl("http://status.test.com/status/status");
		assertEquals(INCORRECT_URL_GENERATED, CORTEX_HEALTH_CHECK_URL, ApplicationUrlHelper.getHealthCheckUrl());
	}

}
