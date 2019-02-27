/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.service.search.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.settings.provider.SettingValueProvider;
import com.elasticpath.settings.test.support.SimpleSettingValueProvider;

/**
 * Test class for SettingsSearchHostLocatorImpl.
 */
public class SettingsSearchHostLocatorImplTest {

	private static final String HTTP_MASTER_URL = "http://masterurl";
	private static final String HTTP_DEFAULT_URL = "http://defaulturl";

	private SettingValueProvider<String> defaultSearchHostLocationProvider;
	private SettingValueProvider<String> masterSearchHostLocationProvider;

	private SettingsSearchHostLocatorImpl locator;

	/**
	 * Setup required for each test.
	 */
	@Before
	public void setUp() {
		defaultSearchHostLocationProvider = new SimpleSettingValueProvider<>(HTTP_DEFAULT_URL);
		masterSearchHostLocationProvider = new SimpleSettingValueProvider<>(HTTP_MASTER_URL);

		locator = new SettingsSearchHostLocatorImpl();
		locator.setDefaultSearchHostLocationProvider(defaultSearchHostLocationProvider);
		locator.setMasterSearchHostLocationProvider(masterSearchHostLocationProvider);
	}

	/**
	 * Test that the host url is retrieved from the setting for the master context.
	 */
	@Test
	public void testReturnMasterHostUrl() {
		locator.setRequiresMaster(true);
		assertEquals("The URL should be the value returned by the settings reader for the master context", HTTP_MASTER_URL,
				locator.getSearchHostLocation());
	}

	/**
	 * Test that the host url is retrieved from the setting for the default context when master is not required.
	 */
	@Test
	public void testReturnDefaultHostUrl() {
		locator.setRequiresMaster(false);
		assertEquals("The URL should be the value returned by the settings reader for the default contect", HTTP_DEFAULT_URL,
				locator.getSearchHostLocation());
	}

}
