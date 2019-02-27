/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.datapolicy.job.impl;

import static com.elasticpath.service.datapolicy.job.impl.DataPointTestUtil.DATAPOINT_KEY_1;
import static com.elasticpath.service.datapolicy.job.impl.DataPointTestUtil.DATAPOINT_KEY_2;
import static com.elasticpath.service.datapolicy.job.impl.DataPointTestUtil.DATAPOINT_KEY_3;
import static com.elasticpath.service.datapolicy.job.impl.DataPointTestUtil.buildCustomer;
import static com.elasticpath.service.datapolicy.job.impl.DataPointTestUtil.buildCustomerConsent;
import static com.elasticpath.service.datapolicy.job.impl.DataPointTestUtil.buildDataPoint;
import static com.elasticpath.service.datapolicy.job.impl.DataPointTestUtil.buildDataPointValue;
import static com.elasticpath.service.datapolicy.job.impl.DataPointTestUtil.buildDataPolicy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
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
import com.elasticpath.service.datapolicy.impl.DataPointValue;
import com.elasticpath.service.datapolicy.job.DataPointValueJob;
import com.elasticpath.service.misc.TimeService;

/**
 * Tests for {@link ExpiredDataPointValuesJobProcessorImpl}.
 */
@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class ExpiredDataPointValuesJobProcessorImplTest {

	private static final Integer ZERO_DAYS_RETENTION_PERIOD = 0;
	private static final Integer DEFAULT_DAYS_RETENTION_PERIOD = 2;
	@InjectMocks
	private final DataPointValueJob dataPointValueJob = new ExpiredDataPointValuesJobProcessorImpl();
	@Mock
	private DataPointValueService dataPointValueService;
	@Mock
	private DataPointService dataPointService;
	@Mock
	private TimeService timeService;
	@Captor
	private ArgumentCaptor<Map<String, ? extends Collection<DataPoint>>> dataPointValueServiceCaptor;

	@Before
	public void setup() {
		when(timeService.getCurrentTime()).thenReturn(new Date());
	}

	@Test
	public void verifyNothingHappensWhenNoGrantedConsents() {
		when(dataPointService.findWithGrantedConsentsLatest()).thenReturn(Collections.emptyMap());

		dataPointValueJob.process();

		verifyZeroInteractions(dataPointValueService);
	}

	@Test
	public void verifyJobRemovesAllExpiredDataPointValues() {
		Customer customer = buildCustomer();
		CustomerConsent customerConsent = buildCustomerConsent(ConsentAction.GRANTED);
		customerConsent.setCustomerGuid(customer.getGuid());

		DataPolicy dataPolicy = buildDataPolicy(RetentionType.FROM_CREATION_DATE, ZERO_DAYS_RETENTION_PERIOD);

		dataPolicy.getDataPoints().add(buildDataPoint(true, DATAPOINT_KEY_1));

		customerConsent.setDataPolicy(dataPolicy);

		Map<String, Map<DataPoint, Set<DataPolicy>>> customerDataPointDataPolicyMap = new HashMap<>();
		Map<DataPoint, Set<DataPolicy>> dataPointSetMap = new HashMap<>();
		dataPointSetMap.put(dataPolicy.getDataPoints().get(0), Sets.newSet(dataPolicy));
		customerDataPointDataPolicyMap.put(customerConsent.getCustomerGuid(), dataPointSetMap);

		when(dataPointService.findWithGrantedConsentsLatest()).thenReturn(customerDataPointDataPolicyMap);
		DataPointValue dataPointValue = buildDataPointValue(new Date(), customerConsent.getCustomerGuid(), DATAPOINT_KEY_1);
		when(dataPointValueService.getValues(anyMap())).thenReturn(Collections.singletonList(dataPointValue));

		dataPointValueJob.process();

		verify(dataPointValueService).getValues(dataPointValueServiceCaptor.capture());
		verify(dataPointValueService).removeValues(any());
		verify(timeService).getCurrentTime();

		Map<String, ? extends Collection<DataPoint>> value = dataPointValueServiceCaptor.getValue();

		assertThat(value)
				.hasSize(1);
		assertThat(value.get(customer.getGuid()))
				.hasSize(1);
	}

	@Test
	public void verifyJobRemovesAllExpiredAndRemovableDataPointValues() {
		Customer customer = buildCustomer();
		CustomerConsent customerConsent = buildCustomerConsent(ConsentAction.GRANTED);
		customerConsent.setCustomerGuid(customer.getGuid());

		DataPolicy dataPolicy = buildDataPolicy(RetentionType.FROM_CREATION_DATE, ZERO_DAYS_RETENTION_PERIOD);

		dataPolicy.getDataPoints().add(buildDataPoint(true, DATAPOINT_KEY_1));

		customerConsent.setDataPolicy(dataPolicy);

		Map<String, Map<DataPoint, Set<DataPolicy>>> customerDataPointDataPolicyMap = new HashMap<>();
		Map<DataPoint, Set<DataPolicy>> dataPointSetMap = new HashMap<>();
		dataPointSetMap.put(dataPolicy.getDataPoints().get(0), Sets.newSet(dataPolicy));
		customerDataPointDataPolicyMap.put(customerConsent.getCustomerGuid(), dataPointSetMap);

		when(dataPointService.findWithGrantedConsentsLatest()).thenReturn(customerDataPointDataPolicyMap);
		DataPointValue dataPointValue = buildDataPointValue(new Date(), customerConsent.getCustomerGuid(), DATAPOINT_KEY_1);
		when(dataPointValueService.getValues(anyMap())).thenReturn(Collections.singletonList(dataPointValue));

		dataPointValueJob.process();

		verify(dataPointValueService).getValues(dataPointValueServiceCaptor.capture());
		verify(dataPointValueService).removeValues(any());
		verify(timeService).getCurrentTime();

		Map<String, ? extends Collection<DataPoint>> value = dataPointValueServiceCaptor.getValue();

		assertThat(value)
				.hasSize(1);
		assertThat(value.get(customer.getGuid()))
				.hasSize(1);
	}

	@Test
	public void verifyJobRemovesDataPointsWhichComplyAllDataPolicies() {
		Customer customer = buildCustomer();

		CustomerConsent customerConsent1 = buildCustomerConsent(ConsentAction.GRANTED);
		customerConsent1.setCustomerGuid(customer.getGuid());
		CustomerConsent customerConsent2 = buildCustomerConsent(ConsentAction.GRANTED);
		customerConsent2.setCustomerGuid(customer.getGuid());

		DataPoint dataPoint1 = buildDataPoint(true, DATAPOINT_KEY_1);
		DataPoint dataPoint2 = buildDataPoint(true, DATAPOINT_KEY_2);
		DataPoint dataPoint3 = buildDataPoint(true, DATAPOINT_KEY_3);

		DataPolicy dataPolicy1 = buildDataPolicy(RetentionType.FROM_CREATION_DATE, DEFAULT_DAYS_RETENTION_PERIOD);
		DataPolicy dataPolicy2 = buildDataPolicy(RetentionType.FROM_CREATION_DATE, ZERO_DAYS_RETENTION_PERIOD);

		dataPolicy1.getDataPoints().add(dataPoint1);
		dataPolicy1.getDataPoints().add(dataPoint2);

		dataPolicy2.getDataPoints().add(dataPoint1);
		dataPolicy2.getDataPoints().add(dataPoint3);

		customerConsent1.setDataPolicy(dataPolicy1);
		customerConsent2.setDataPolicy(dataPolicy2);

		Map<String, Map<DataPoint, Set<DataPolicy>>> customerDataPointDataPolicyMap = new HashMap<>();
		Map<DataPoint, Set<DataPolicy>> dataPointSetMap1 = new HashMap<>();
		dataPointSetMap1.put(dataPolicy1.getDataPoints().get(0), Sets.newSet(dataPolicy1));
		dataPointSetMap1.put(dataPolicy1.getDataPoints().get(1), Sets.newSet(dataPolicy1));
		customerDataPointDataPolicyMap.put(customerConsent1.getCustomerGuid(), dataPointSetMap1);

		Map<DataPoint, Set<DataPolicy>> dataPointSetMap2 = new HashMap<>();
		dataPointSetMap2.put(dataPolicy1.getDataPoints().get(0), Sets.newSet(dataPolicy2));
		dataPointSetMap2.put(dataPolicy1.getDataPoints().get(1), Sets.newSet(dataPolicy2));
		customerDataPointDataPolicyMap.put(customerConsent1.getCustomerGuid(), dataPointSetMap2);

		when(dataPointService.findWithGrantedConsentsLatest()).thenReturn(customerDataPointDataPolicyMap);
		DataPointValue dataPointValue1 = buildDataPointValue(new Date(), customerConsent1.getCustomerGuid(), DATAPOINT_KEY_1);
		DataPointValue dataPointValue2 = buildDataPointValue(new Date(), customerConsent1.getCustomerGuid(), DATAPOINT_KEY_2);
		DataPointValue dataPointValue3 = buildDataPointValue(new Date(), customerConsent2.getCustomerGuid(), DATAPOINT_KEY_1);
		DataPointValue dataPointValue4 = buildDataPointValue(new Date(), customerConsent2.getCustomerGuid(), DATAPOINT_KEY_3);
		when(dataPointValueService.getValues(anyMap())).thenReturn(Arrays.asList(dataPointValue1,
				dataPointValue2, dataPointValue3, dataPointValue4));

		dataPointValueJob.process();

		verify(dataPointValueService).getValues(dataPointValueServiceCaptor.capture());
		verify(dataPointValueService).removeValues(any());
		final int wantedNumberOfInvocations = 4;
		verify(timeService, times(wantedNumberOfInvocations)).getCurrentTime();

		Map<String, ? extends Collection<DataPoint>> value = dataPointValueServiceCaptor.getValue();

		assertThat(value)
				.hasSize(1);

		final int expectedDataPointCollectionSize = 2;

		assertThat(value.get(customer.getGuid()))
				.hasSize(expectedDataPointCollectionSize);
	}
}