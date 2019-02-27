/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.search.index.solr.service.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.util.UUID;

import com.google.common.collect.ImmutableMap;
import org.assertj.core.api.SoftAssertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.service.search.IndexType;

/**
 * Test class for {@link SearchIndexLocatorImpl}.
 */
public class SearchIndexLocatorImplTest {

	private static final String CATEGORY_SYSTEM_PROPERTY_KEY = UUID.randomUUID().toString();
	private static final String CATEGORY_SYSTEM_PROPERTY_VALUE = UUID.randomUUID().toString();
	private static final String CM_USER_SYSTEM_PROPERTY_KEY = UUID.randomUUID().toString();
	private static final String CM_USER_SYSTEM_PROPERTY_VALUE = UUID.randomUUID().toString();

	private final SearchIndexLocatorImpl locator = new SearchIndexLocatorImpl();

	@Before
	@After
	public void clearSystemProperties() {
		System.clearProperty(CATEGORY_SYSTEM_PROPERTY_KEY);
		System.clearProperty(CM_USER_SYSTEM_PROPERTY_KEY);
	}

	@Test
	public void verifyIndexTypeDeterminesSystemPropertyThatContainsTheLocationValue() throws Exception {
		System.setProperty(CATEGORY_SYSTEM_PROPERTY_KEY, CATEGORY_SYSTEM_PROPERTY_VALUE);
		System.setProperty(CM_USER_SYSTEM_PROPERTY_KEY, CM_USER_SYSTEM_PROPERTY_VALUE);

		locator.setIndexTypeSystemPropertyKeyMap(ImmutableMap.of(
				IndexType.CATEGORY, CATEGORY_SYSTEM_PROPERTY_KEY,
				IndexType.CMUSER, CM_USER_SYSTEM_PROPERTY_KEY
		));

		final SoftAssertions softly = new SoftAssertions();

		softly.assertThat(locator.getSearchIndexLocation(IndexType.CATEGORY))
				.isEqualTo(new File(CATEGORY_SYSTEM_PROPERTY_VALUE));

		softly.assertThat(locator.getSearchIndexLocation(IndexType.CMUSER))
				.isEqualTo(new File(CM_USER_SYSTEM_PROPERTY_VALUE));

		softly.assertAll();
	}

	@Test
	public void verifyIllegalArgumentExceptionThrownWhenNoMappingForIndexType() throws Exception {
		assertThatThrownBy(() -> locator.getSearchIndexLocation(IndexType.PRODUCT))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void verifyIllegalArgumentExceptionThrownWhenNoSuchSystemPropertyValue() throws Exception {
		// System property with key CM_USER_SYSTEM_PROPERTY_KEY not set.

		locator.setIndexTypeSystemPropertyKeyMap(ImmutableMap.of(
				IndexType.CMUSER, CM_USER_SYSTEM_PROPERTY_KEY
		));

		assertThatThrownBy(() -> locator.getSearchIndexLocation(IndexType.CMUSER))
				.isInstanceOf(IllegalArgumentException.class);
	}

}