/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.datapolicy.job.impl;

import static com.elasticpath.service.datapolicy.job.impl.DataPointTestUtil.DATAPOINT_KEY_1;
import static com.elasticpath.service.datapolicy.job.impl.DataPointTestUtil.DATAPOINT_KEY_2;
import static com.elasticpath.service.datapolicy.job.impl.DataPointTestUtil.buildCustomer;
import static com.elasticpath.service.datapolicy.job.impl.DataPointTestUtil.buildCustomerConsent;
import static com.elasticpath.service.datapolicy.job.impl.DataPointTestUtil.buildDataPoint;
import static com.elasticpath.service.datapolicy.job.impl.DataPointTestUtil.buildDataPolicy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.datapolicy.ConsentAction;
import com.elasticpath.domain.datapolicy.CustomerConsent;
import com.elasticpath.domain.datapolicy.DataPoint;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.domain.datapolicy.RetentionType;
import com.elasticpath.service.datapolicy.DataPointService;
import com.elasticpath.service.datapolicy.DataPointValueService;
import com.elasticpath.service.datapolicy.job.DataPointValueJob;

/**
 * Tests for {@link DataPointRevokedConsentsJobProcessorImpl}.
 */
@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class DataPointRevokedConsentsJobProcessorImplTest {

	private static final Integer ZERO_DAYS_RETENTION_PERIOD = 0;
	@InjectMocks
	private final DataPointValueJob dataPointValueJob = new DataPointRevokedConsentsJobProcessorImpl();
	@Mock
	private DataPointValueService dataPointValueService;
	@Mock
	private DataPointService dataPointService;
	@Captor
	private ArgumentCaptor<Map<String, ? extends Collection<DataPoint>>> dataPointValueServiceCaptor;

	@Test
	public void verifyNothingHappensWhenNoRevokedConsents() {
		when(dataPointService.findWithRevokedConsentsLatest()).thenReturn(Collections.emptyMap());

		dataPointValueJob.process();

		verifyZeroInteractions(dataPointValueService);
	}

	@Test
	public void verifyJobRemovesAllDataPolicyDataPointValuesForRevokedConsents() {
		Customer customer = buildCustomer();
		CustomerConsent customerConsent = buildCustomerConsent(ConsentAction.REVOKED);
		customerConsent.setCustomerGuid(customer.getGuid());

		DataPolicy dataPolicy = buildDataPolicy(RetentionType.FROM_CREATION_DATE, ZERO_DAYS_RETENTION_PERIOD);

		dataPolicy.getDataPoints().add(buildDataPoint(true, DATAPOINT_KEY_1));
		dataPolicy.getDataPoints().add(buildDataPoint(true, DATAPOINT_KEY_2));

		customerConsent.setDataPolicy(dataPolicy);

		Map<String, Map<DataPoint, Set<DataPolicy>>> customerDataPointDataPolicyMap = new HashMap<>();
		Map<DataPoint, Set<DataPolicy>> dataPointSetMap = new HashMap<>();
		dataPointSetMap.put(dataPolicy.getDataPoints().get(0), Sets.newSet(dataPolicy));
		dataPointSetMap.put(dataPolicy.getDataPoints().get(1), Sets.newSet(dataPolicy));
		customerDataPointDataPolicyMap.put(customerConsent.getCustomerGuid(), dataPointSetMap);

		when(dataPointService.findWithRevokedConsentsLatest()).thenReturn(customerDataPointDataPolicyMap);

		dataPointValueJob.process();

		verify(dataPointValueService).getValues(dataPointValueServiceCaptor.capture());
		verify(dataPointValueService).removeValues(any());

		Map<String, ? extends Collection<DataPoint>> value = dataPointValueServiceCaptor.getValue();


		assertThat(value)
				.hasSize(1);
		assertThat(value.get(customer.getGuid()))
				.hasSize(2);
	}

}