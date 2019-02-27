/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.reporting.registration.service.impl;

import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.hamcrest.core.StringContains;
import org.hamcrest.core.StringStartsWith;

import com.elasticpath.cmclient.reporting.registration.CustomerRegistrationReportSection;
import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilder;

/**
 * Tests for CustomerRegistrationReportServiceImpl.
 * 
 * There should always be an END_DATE parameter, a STORE parameter, and an ANONYMOUS_REG parameter.
 * There may not always be a START_DATE parameter.
 */
public class CustomerRegistrationReportServiceImplTest {
	
	private final Date endDate = new Date(1);
	private final Date startDate = new Date(2);
	private final List<String> storeNames = new ArrayList<String>();
	
	private final Map<String, Object> params = new HashMap<String, Object>();
	private CustomerRegistrationReportServiceImpl service;
	
	/**
	 * Setup tests.
	 */
	@Before
	public void setUp() {
		service = new CustomerRegistrationReportServiceImpl();
		storeNames.addAll(Arrays.asList("store1", "store2")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private static void assertBasicQuery(final String query) {
		Assert.assertThat(query, StringStartsWith.startsWith("SELECT")); //$NON-NLS-1$
		Assert.assertThat(query, StringContains.containsString("CustomerImpl")); //$NON-NLS-1$
	}

	private void assertStoreJoin(final JpqlQueryBuilder builder) {
		String query = builder.toString();
		Assert.assertThat("Missing store join", query, StringContains.containsString("StoreImpl")); //$NON-NLS-1$ //$NON-NLS-2$
		assertFalse("Store not added as a paramter", builder.getParameterList().isEmpty()); //$NON-NLS-1$
		assertParameterListContains("Store parameter not found in query parameter list!", builder, storeNames); //$NON-NLS-1$
	}

	private static void assertNoAnonymous(final JpqlQueryBuilder builder) {
		String query = builder.toString();
		Assert.assertThat("Anonymous customers shouldn't be allowed", query, //$NON-NLS-1$
				StringContains.containsString("localizedAttributeKey = ")); //$NON-NLS-1$
		assertParameterListContains("Anonymous customer parameter not found in paramter list!", builder, //$NON-NLS-1$
				"CP_ANONYMOUS_CUST"); //$NON-NLS-1$
	}

	private void assertContainsEndDate(final JpqlQueryBuilder builder) {
		String query = builder.toString();
		Assert.assertThat("Missing end date filter", query, StringContains.containsString("creationDate <= ")); //$NON-NLS-1$ //$NON-NLS-2$
		assertParameterListContains("End date not found in query parameter list!", builder, endDate); //$NON-NLS-1$
	}

	private void assertContainsStartDate(final JpqlQueryBuilder builder) {
		String query = builder.toString();
		Assert.assertThat("Missing start date filter", query, StringContains.containsString("creationDate >= ")); //$NON-NLS-1$ //$NON-NLS-2$
		assertParameterListContains("Start date not found in query parameter list!", builder, startDate); //$NON-NLS-1$
	}

	@SuppressWarnings("PMD.CompareObjectsWithEquals")
	private static void assertParameterListContains(final String message, final JpqlQueryBuilder builder, final Object object) {
		boolean storeParameterFound = false;
		for (Object parameter : builder.getParameterList()) {
			storeParameterFound |= parameter == object;
		}

		if (!storeParameterFound) {
			Assert.fail(message);
		}
	}
	
	/**
	 * Test the query when no parameters are set.
	 */
	@Test
	public void testQueryNoParameters() {
		String query = service.buildQuery(params).toString();
		assertBasicQuery(query);
	}
	
	/**
	 * Test that if the Anonymous Registrations parameter is not present it will
	 * be treated as FALSE when the query is being generated.
	 */
	@Test
	public void testQueryAnonymousRegistrationFalseIfNotPresent() {
		params.put(CustomerRegistrationReportSection.PARAMETER_STORE, storeNames);
		JpqlQueryBuilder builder = service.buildQuery(params);
		String query = builder.toString();

		assertBasicQuery(query);
		assertStoreJoin(builder);
		assertNoAnonymous(builder);
	}
	
	/**
	 * Test that if the Anonymous Registrations parameter is FALSE
	 * it's treated as FALSE when the query is being generated.
	 */
	@Test
	public void testAnonymousRegistrationFalse() {
		params.put(CustomerRegistrationReportSection.PARAMETER_STORE, storeNames);
		params.put(CustomerRegistrationReportSection.PARAMETER_ANONYMOUS_REGISTRATION, Boolean.FALSE);
		JpqlQueryBuilder builder = service.buildQuery(params);
		String query = builder.toString();

		assertBasicQuery(query);
		assertStoreJoin(builder);
		assertNoAnonymous(builder);
	}
	
	/**
	 * Test the query with Stores, EndDate.
	 */
	@Test
	public void testQueryStoresEndDate() {
		params.put(CustomerRegistrationReportSection.PARAMETER_END_DATE, endDate);
		params.put(CustomerRegistrationReportSection.PARAMETER_STORE, storeNames);
		JpqlQueryBuilder builder = service.buildQuery(params);
		String query = builder.toString();

		assertBasicQuery(query);
		assertStoreJoin(builder);
		assertContainsEndDate(builder);
	}
	
	/**
	 * Test the query with Stores, StartDate, EndDate.
	 */
	@Test
	public void testQueryStoresStartDateEndDate() {
		params.put(CustomerRegistrationReportSection.PARAMETER_START_DATE, startDate);
		params.put(CustomerRegistrationReportSection.PARAMETER_END_DATE, endDate);
		params.put(CustomerRegistrationReportSection.PARAMETER_STORE, storeNames);
		JpqlQueryBuilder builder = service.buildQuery(params);
		String query = builder.toString();

		assertBasicQuery(query);
		assertStoreJoin(builder);
		assertContainsEndDate(builder);
		assertContainsStartDate(builder);
	}

	/**
	 * Test the query when anonymous registration is true.
	 */
	@Test 
	public void testQueryAnonymousRegTrue() {
		params.put(CustomerRegistrationReportSection.PARAMETER_ANONYMOUS_REGISTRATION, Boolean.TRUE);
		JpqlQueryBuilder builder = service.buildQuery(params);
		String query = builder.toString();

		assertBasicQuery(query);
	}
	
	/**
	 * Test the query when anonymous registration is true and there are store names.
	 */
	@Test
	public void testQueryStoresAnonymousTrue() {
		params.put(CustomerRegistrationReportSection.PARAMETER_STORE, storeNames);
		params.put(CustomerRegistrationReportSection.PARAMETER_ANONYMOUS_REGISTRATION, Boolean.TRUE);
		JpqlQueryBuilder builder = service.buildQuery(params);
		String query = builder.toString();

		assertBasicQuery(query);
		assertStoreJoin(builder);
	}
}
