/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.datapolicy.job.impl;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.elasticpath.domain.datapolicy.DataPoint;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.service.datapolicy.DataPointService;
import com.elasticpath.service.datapolicy.DataPointValueService;
import com.elasticpath.service.datapolicy.impl.DataPointValue;
import com.elasticpath.service.datapolicy.job.DataPointValueJob;
import com.elasticpath.service.datapolicy.job.impl.filter.DataPointRetentionPeriodFilter;
import com.elasticpath.service.misc.TimeService;

/**
 * This job processes DataPoints and removes their associated values, which have been expired according to the attached data policy.
 * If a data point is attached to multiple data policies, its value could <b>ONLY</b> be removed when it has been
 * expired for all data policies. Additionally, a data point's associated value could not be removed if data point is
 * marked as not removable ({@link DataPoint#isRemovable()}.
 */
public class ExpiredDataPointValuesJobProcessorImpl implements DataPointValueJob {

	private static final Logger LOG = Logger.getLogger(ExpiredDataPointValuesJobProcessorImpl.class);
	private DataPointValueService dataPointValueService;
	private DataPointService dataPointService;
	private TimeService timeService;

	@Override
	public void process() {
		Map<String, Map<DataPoint, Set<DataPolicy>>> customerDataPointDataPolicyMap = dataPointService.findWithGrantedConsentsLatest();

		if (customerDataPointDataPolicyMap.isEmpty()) {
			LOG.debug("No data points for granted consents have been found to process.");
			return;
		}

		Map<String, Set<DataPoint>> customerDataPoints = customerDataPointDataPolicyMap
				.entrySet()
				.stream()
				.collect(Collectors.toMap(Map.Entry::getKey, value -> value.getValue().keySet()));

		Collection<DataPointValue> dataPointValues =
				dataPointValueService.getValues(customerDataPoints)
						.stream()
						.filter(DataPointValue::isPopulated)
						.filter(dpv ->
								getDataPointValuePredicate(customerDataPointDataPolicyMap.get(dpv.getCustomerGuid()))
										.test(dpv))
						.collect(Collectors.toSet());

		int removedValues = dataPointValueService.removeValues(dataPointValues);
		LOG.debug(String.format("%s data point values have been removed.", removedValues));
	}

	/**
	 * Get a {@link Predicate Predicate&lt;DataPointValue&gt;} for a given map of data points to data policies.
	 * Every customer has their own unique data point to data policies association, therefore
	 * new {@link DataPointRetentionPeriodFilter} required for every customer.
	 *
	 * @param dataPoints map of data points to data policies.
	 * @return predicate.
	 */
	private Predicate<DataPointValue> getDataPointValuePredicate(final Map<DataPoint, Set<DataPolicy>> dataPoints) {
		Date currentTime = timeService.getCurrentTime();

		Map<String, Set<DataPolicy>> customerDataPolicies = dataPoints.entrySet()
				.stream()
				.collect(Collectors.toMap(key -> key.getKey().getExpandedLocation(), Map.Entry::getValue));

		return new DataPointRetentionPeriodFilter(currentTime, customerDataPolicies);
	}

	/**
	 * Sets data point value service.
	 *
	 * @param dataPointValueService data point value service.
	 */
	public void setDataPointValueService(final DataPointValueService dataPointValueService) {
		this.dataPointValueService = dataPointValueService;
	}

	/**
	 * Sets data point service.
	 *
	 * @param dataPointService data point service.
	 */
	public void setDataPointService(final DataPointService dataPointService) {
		this.dataPointService = dataPointService;
	}

	/**
	 * Sets time service.
	 *
	 * @param timeService time service.
	 */
	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}
}
