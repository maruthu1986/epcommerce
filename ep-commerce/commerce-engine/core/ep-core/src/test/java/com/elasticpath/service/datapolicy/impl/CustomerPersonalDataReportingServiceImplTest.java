/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.service.datapolicy.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.datapolicy.DataPoint;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.datapolicy.DataPointValueService;

@RunWith(MockitoJUnitRunner.class)
public class CustomerPersonalDataReportingServiceImplTest {

	private static final long TEN_SECONDS =  10 * 1000;
	private static final String STORE_CODE = "store code";
	private static final String USER_ID = "harry.potter@elasticpath.com";
	private static final String CUSTOMER_FULL_NAME = "Full Name";
	private static final String CUSTOMER_GUID = "cust-guid";

	private static final String QUERY = "CUSTOMER_AND_DATA_POINT_BY_STORE_AND_USER_ID";

	@Mock
	private DataPointValueService dataPointValueService;

	@Mock
	private PersistenceEngine persistenceEngine;

	@Mock
	private Customer customer;

	@Mock
	private DataPointValue dataPointValue;

	@Mock
	private DataPoint dataPoint;

	@InjectMocks
	private CustomerPersonalDataReportingServiceImpl service;

	@Test
	public void shouldReturnEmptyListWhenCustomerConsentIsNotFound() {

		when(persistenceEngine.retrieveByNamedQuery(QUERY, USER_ID, STORE_CODE)).thenReturn(Collections.emptyList());

		Collection<Object[]> actualResult = service.getData(STORE_CODE, USER_ID);

		assertThat(actualResult)
			.isEmpty();

		verify(persistenceEngine).retrieveByNamedQuery(QUERY, USER_ID, STORE_CODE);
	}

	//The data points may live in many data policies but the report must show only unique ones.
	@Test
	public void shouldReturnArrayWithUniqueReportData() {

		long now = System.currentTimeMillis();

		String dpName = "Data Point Name";
		String dPValue = "Data Point Value";

		Date createdDate = new Date(now);
		Date lastModifiedDate = new Date(now + TEN_SECONDS);

		Map<String, Collection<DataPoint>> customerGuidToDataPoints = new HashMap<>(1);
		customerGuidToDataPoints.put(CUSTOMER_GUID, Collections.singleton(dataPoint));

		when(customer.getFullName()).thenReturn(CUSTOMER_FULL_NAME);
		when(customer.getGuid()).thenReturn(CUSTOMER_GUID);

		when(dataPointValue.getDataPointName()).thenReturn(dpName);
		when(dataPointValue.getValue()).thenReturn(dPValue);
		when(dataPointValue.getCreatedDate()).thenReturn(createdDate);
		when(dataPointValue.getLastModifiedDate()).thenReturn(lastModifiedDate);

		when(persistenceEngine.retrieveByNamedQuery(QUERY, USER_ID, STORE_CODE)).thenReturn(Collections.singletonList(new Object[]{customer,
			dataPoint}
		));

		when(dataPointValueService.getValues(eq(customerGuidToDataPoints))).thenReturn(Collections.singletonList(dataPointValue));

		Collection<Object[]> actualResult = service.getData(STORE_CODE, USER_ID);

		Object[] expectedData = new Object[]{CUSTOMER_FULL_NAME, dpName, dPValue, createdDate, lastModifiedDate};

		assertThat(actualResult).hasSize(1);

		assertThat(actualResult)
			.contains(expectedData);

		verify(dataPointValueService).getValues(any());

		verify(persistenceEngine).retrieveByNamedQuery(QUERY, USER_ID, STORE_CODE);

	}
}
