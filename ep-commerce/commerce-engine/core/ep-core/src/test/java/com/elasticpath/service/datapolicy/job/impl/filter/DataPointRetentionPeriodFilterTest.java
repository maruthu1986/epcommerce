/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.datapolicy.job.impl.filter;

import static com.elasticpath.service.datapolicy.job.impl.DataPointTestUtil.DATAPOINT_KEY_1;
import static com.elasticpath.service.datapolicy.job.impl.DataPointTestUtil.buildDataPoint;
import static com.elasticpath.service.datapolicy.job.impl.DataPointTestUtil.buildDataPointValue;
import static com.elasticpath.service.datapolicy.job.impl.DataPointTestUtil.buildDataPolicy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.datapolicy.DataPoint;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.domain.datapolicy.DataPolicyState;
import com.elasticpath.domain.datapolicy.RetentionType;
import com.elasticpath.service.datapolicy.impl.DataPointValue;

/**
 * Tests for {@link DataPointRetentionPeriodFilter}.
 */
@RunWith(MockitoJUnitRunner.class)
public class DataPointRetentionPeriodFilterTest {

	private static final Integer DEFAULT_RETENTION_PERIOD_DAYS = 2;
	private static final Integer ONE_RETENTION_PERIOD_DAYS = 1;
	private static final Integer ZERO_RETENTION_PERIOD_DAYS = 0;

	private DataPoint dataPoint;

	@Before
	public void setup() {
		dataPoint = buildDataPoint(true, DATAPOINT_KEY_1);
	}

	@Test
	public void verifyThrowsExceptionWhenNullDateUsedToCreateFilter() {
		assertThatThrownBy(() ->
				new DataPointRetentionPeriodFilter(null, Collections.emptyMap())).isInstanceOf(NullPointerException.class);
	}

	@Test
	public void verifyTestReturnsFalseWhenEmptyMapUsedToCreateFilter() {
		Predicate<DataPointValue> filter = new DataPointRetentionPeriodFilter(new Date(), Collections.emptyMap());

		boolean test = filter.test(buildDataPointValue(new Date(), null, DATAPOINT_KEY_1));

		assertThat(test)
				.isFalse();
	}

	@Test
	public void verifyTestReturnsFalseWhenDataPointValueDateDoesNotComplyDataPolicyRetentionPeriod() {
		Map<String, Set<DataPolicy>> dataPointMap = new HashMap<>();
		Set<DataPolicy> dataPolicies = new HashSet<>();
		dataPolicies.add(buildDataPolicy(RetentionType.FROM_CREATION_DATE, DEFAULT_RETENTION_PERIOD_DAYS));
		dataPointMap.put(dataPoint.getExpandedLocation(), dataPolicies);

		Predicate<DataPointValue> filter = new DataPointRetentionPeriodFilter(new Date(), dataPointMap);

		boolean test = filter.test(buildDataPointValue(new Date(), null, DATAPOINT_KEY_1));

		assertThat(test)
				.isFalse();
	}

	@Test
	public void verifyTestReturnsFalseWhenDataPointValueDateDoesNotComplyAllDataPoliciesRetentionPeriods() {
		Map<String, Set<DataPolicy>> dataPointMap = new HashMap<>();
		Set<DataPolicy> dataPolicies = new HashSet<>();
		dataPolicies.add(buildDataPolicy(RetentionType.FROM_CREATION_DATE, ZERO_RETENTION_PERIOD_DAYS));
		dataPolicies.add(buildDataPolicy(RetentionType.FROM_LAST_UPDATE, DEFAULT_RETENTION_PERIOD_DAYS));
		dataPointMap.put(dataPoint.getExpandedLocation(), dataPolicies);

		Predicate<DataPointValue> filter = new DataPointRetentionPeriodFilter(new Date(), dataPointMap);

		boolean test = filter.test(buildDataPointValue(new Date(), null, DATAPOINT_KEY_1));

		assertThat(test)
				.isFalse();
	}

	@Test
	public void verifyTestReturnsTrueWhenDataPointValueDateCompliesAllDataPoliciesRetentionPeriods() {
		Map<String, Set<DataPolicy>> dataPointMap = new HashMap<>();
		Set<DataPolicy> dataPolicies = new HashSet<>();
		dataPolicies.add(buildDataPolicy(RetentionType.FROM_CREATION_DATE, ZERO_RETENTION_PERIOD_DAYS));
		dataPolicies.add(buildDataPolicy(RetentionType.FROM_LAST_UPDATE, ZERO_RETENTION_PERIOD_DAYS));
		dataPolicies.add(buildDataPolicy(RetentionType.FROM_CREATION_DATE, ZERO_RETENTION_PERIOD_DAYS));
		dataPolicies.add(buildDataPolicy(RetentionType.FROM_LAST_UPDATE, ZERO_RETENTION_PERIOD_DAYS));
		dataPointMap.put(dataPoint.getExpandedLocation(), dataPolicies);

		Predicate<DataPointValue> filter = new DataPointRetentionPeriodFilter(new Date(), dataPointMap);

		boolean test = filter.test(buildDataPointValue(new Date(), null, DATAPOINT_KEY_1));

		assertThat(test)
				.as("Should return true only when comply all retention periods and types.")
				.isTrue();
	}

	@Test
	public void verifyReturnsFalseForDisabledPolicyAndFromLastUpdateRetentionTypeWhenEndDateIsLessThanLastModifiedDate() {
		Map<String, Set<DataPolicy>> dataPointMap = new HashMap<>();
		Set<DataPolicy> dataPolicies = new HashSet<>();

		DataPolicy dataPolicy = buildDataPolicy(RetentionType.FROM_LAST_UPDATE, DEFAULT_RETENTION_PERIOD_DAYS);
		dataPolicy.setState(DataPolicyState.DISABLED);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		dataPolicy.setEndDate(calendar.getTime());

		dataPolicies.add(dataPolicy);
		dataPointMap.put(dataPoint.getExpandedLocation(), dataPolicies);

		Predicate<DataPointValue> filter = new DataPointRetentionPeriodFilter(new Date(), dataPointMap);

		boolean test = filter.test(buildDataPointValue(new Date(), null, DATAPOINT_KEY_1));

		assertThat(test)
				.as("Should be false for disabled data policy when end date less than last modified date and retention period is not expired.")
				.isFalse();
	}

	@Test
	public void verifyReturnsFalseForDisabledPolicyAndFromLastUpdateRetentionTypeWhenLastModifiedDateIsLessThanEndDate() {
		Map<String, Set<DataPolicy>> dataPointMap = new HashMap<>();
		Set<DataPolicy> dataPolicies = new HashSet<>();

		DataPolicy dataPolicy = buildDataPolicy(RetentionType.FROM_LAST_UPDATE, DEFAULT_RETENTION_PERIOD_DAYS);
		dataPolicy.setState(DataPolicyState.DISABLED);
		dataPolicy.setEndDate(new Date());

		dataPolicies.add(dataPolicy);
		dataPointMap.put(dataPoint.getExpandedLocation(), dataPolicies);

		Predicate<DataPointValue> filter = new DataPointRetentionPeriodFilter(new Date(), dataPointMap);

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		boolean test = filter.test(buildDataPointValue(calendar.getTime(), null, DATAPOINT_KEY_1));

		assertThat(test)
				.as("Should be false for disabled data policy when last modified date less than end date and retention period is not expired.")
				.isFalse();
	}

	@Test
	public void verifyReturnsTrueForDisabledPolicyAndFromLastUpdateRetentionTypeWhenEndDateIsLessThanLastModifiedDate() {
		Map<String, Set<DataPolicy>> dataPointMap = new HashMap<>();
		Set<DataPolicy> dataPolicies = new HashSet<>();

		DataPolicy dataPolicy = buildDataPolicy(RetentionType.FROM_LAST_UPDATE, ONE_RETENTION_PERIOD_DAYS);
		dataPolicy.setState(DataPolicyState.DISABLED);
		Date lastModifiedDate = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		dataPolicy.setEndDate(calendar.getTime());

		dataPolicies.add(dataPolicy);
		dataPointMap.put(dataPoint.getExpandedLocation(), dataPolicies);

		Predicate<DataPointValue> filter = new DataPointRetentionPeriodFilter(new Date(), dataPointMap);

		boolean test = filter.test(buildDataPointValue(lastModifiedDate, null, DATAPOINT_KEY_1));

		assertThat(test)
				.as("Should be true for disabled data policy when end date less than last modified date and retention period is expired.")
				.isTrue();
	}

	@Test
	public void verifyReturnsTrueForDisabledPolicyAndFromLastUpdateRetentionTypeWhenLastModifiedDateIsLessThanEndDate() {
		Map<String, Set<DataPolicy>> dataPointMap = new HashMap<>();
		Set<DataPolicy> dataPolicies = new HashSet<>();

		DataPolicy dataPolicy = buildDataPolicy(RetentionType.FROM_LAST_UPDATE, ONE_RETENTION_PERIOD_DAYS);
		dataPolicy.setState(DataPolicyState.DISABLED);
		dataPolicy.setEndDate(new Date());

		dataPolicies.add(dataPolicy);
		dataPointMap.put(dataPoint.getExpandedLocation(), dataPolicies);

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -1);

		Predicate<DataPointValue> filter = new DataPointRetentionPeriodFilter(new Date(), dataPointMap);

		boolean test = filter.test(buildDataPointValue(calendar.getTime(), null, DATAPOINT_KEY_1));

		assertThat(test)
				.as("Should be true for disabled data policy when last modified date less than end date and retention period is not expired.")
				.isTrue();
	}
}