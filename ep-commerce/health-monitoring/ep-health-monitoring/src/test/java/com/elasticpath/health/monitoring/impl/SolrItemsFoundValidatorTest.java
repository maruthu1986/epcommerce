/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.health.monitoring.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.health.monitoring.Status;
import com.elasticpath.health.monitoring.StatusType;

@RunWith(MockitoJUnitRunner.class)
public class SolrItemsFoundValidatorTest {

	@InjectMocks
	SolrItemsFoundValidator solrItemsFoundValidator;

	@Test
	public void validateEmptyResponse() {
		Status status = solrItemsFoundValidator.validate("");
		assertEquals("StatusType should be UNKNOWN", StatusType.UNKNOWN, status.getStatus());
		assertEquals("Invalid response body", "No response body returned", status.getMessage());
		assertNull("Should return null info", status.getInfo());
	}

	@Test
	public void validateNonEmptyResponse() {
		String response = "{\"responseHeader\":{\"status\":0,\"QTime\":31,\"params\":{\"q\":\"*:*\",\"rows\":\"0\",\"wt\":\"json\"}},"
				+ "\"response\":{\"numFound\":3908,\"start\":0,\"docs\":[]}}";

		Status status = solrItemsFoundValidator.validate(response);
		assertEquals("StatusType should be OK", StatusType.OK, status.getStatus());
		assertEquals("Invalid response body", "Number of items returned: 3908; minimum number required: 1", status.getMessage());
		assertNull("Should return null info", status.getInfo());
	}


	@Test
	public void validateNonEmptyResponseWithNonZeroStatus() {
		String response = "{\"responseHeader\": {\"status\": 1}}";

		Status status = solrItemsFoundValidator.validate(response);
		assertEquals("StatusType should be UNKNOWN", StatusType.UNKNOWN, status.getStatus());
		assertEquals("Invalid response body", "Solr response status non-zero", status.getMessage());
		assertEquals("Should return response", response, status.getInfo());
	}
}
