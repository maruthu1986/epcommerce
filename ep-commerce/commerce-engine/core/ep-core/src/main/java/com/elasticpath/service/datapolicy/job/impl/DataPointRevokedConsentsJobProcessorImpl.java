/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.datapolicy.job.impl;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.elasticpath.domain.datapolicy.DataPoint;
import com.elasticpath.service.datapolicy.DataPointService;
import com.elasticpath.service.datapolicy.DataPointValueService;
import com.elasticpath.service.datapolicy.impl.DataPointValue;
import com.elasticpath.service.datapolicy.job.DataPointValueJob;

/**
 * This job processes DataPoints and removes their associated values, when customer consent has been revoked.
 * If a data point is attached to multiple data policies, its value could <b>ONLY</b> be removed when all associated
 * consents have been revoked. Additionally, a data point's associated value could not be removed if data point is
 * marked as not removable ({@link DataPoint#isRemovable()}.
 */
public class DataPointRevokedConsentsJobProcessorImpl implements DataPointValueJob {

	private static final Logger LOG = Logger.getLogger(DataPointRevokedConsentsJobProcessorImpl.class);
	private DataPointValueService dataPointValueService;
	private DataPointService dataPointService;

	@Override
	public void process() {
		Map<String, Set<DataPoint>> customerDataPoints = dataPointService.findWithRevokedConsentsLatest()
				.entrySet()
				.stream()
				.collect(Collectors.toMap(Map.Entry::getKey, value -> value.getValue().keySet()));

		if (customerDataPoints.isEmpty()) {
			LOG.debug("No data points for revoked consents have been found to process.");
			return;
		}

		Collection<DataPointValue> dataPointValues = dataPointValueService
				.getValues(customerDataPoints)
				.stream()
				.filter(DataPointValue::isPopulated)
				.collect(Collectors.toSet());

		int removedValues = dataPointValueService.removeValues(dataPointValues);
		LOG.debug(String.format("%s data point values have been removed.", removedValues));
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
}
