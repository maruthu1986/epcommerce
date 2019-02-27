/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.health.monitoring.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.health.monitoring.Status;
import com.elasticpath.health.monitoring.StatusType;

@RunWith(MockitoJUnitRunner.class)
public class StatusUtilityTest {

	@Mock
	Status status;

	@InjectMocks
	private StatusUtility statusUtility;

	@Test
	public void getAggregatedResultShouldReturnOkayForEmptyParameter() {
		StatusType statusType = statusUtility.getAggregatedResult(Collections.emptyMap());
		assertEquals("Should return status OK.", StatusType.OK.toString(), statusType.toString());
	}

	@Test
	public void getAggregatedResultShouldReturnCritical() {
		Map<String, Status> results = new HashMap<>();
		when(status.getStatus())
				.thenReturn(StatusType.CRITICAL)
				.thenReturn(StatusType.UNKNOWN)
				.thenReturn(StatusType.WARNING)
				.thenReturn(StatusType.OK);

		results.put("CRITICAL", status);
		results.put("UNKNOWN", status);
		results.put("WARNING", status);
		results.put("OK", status);

		StatusType statusType = statusUtility.getAggregatedResult(results);
		assertEquals("Should return status CRITICAL.", StatusType.CRITICAL, statusType);
	}

	@Test
	public void getAggregatedResultShouldReturnOk() {
		Map<String, Status> results = Collections.emptyMap();
		StatusType statusType = statusUtility.getAggregatedResult(results);
		assertEquals("Should return status OK.", StatusType.OK, statusType);
	}

	@Test
	public void getAggregatedResultShouldReturnUnknown() {
		Map<String, Status> results = new HashMap<>();
		when(status.getStatus())
				.thenReturn(StatusType.UNKNOWN)
				.thenReturn(StatusType.WARNING);

		results.put("UNKNOWN", status);
		StatusType statusType = statusUtility.getAggregatedResult(results);
		assertEquals("Should return status Unknown.", StatusType.UNKNOWN, statusType);
	}
}
