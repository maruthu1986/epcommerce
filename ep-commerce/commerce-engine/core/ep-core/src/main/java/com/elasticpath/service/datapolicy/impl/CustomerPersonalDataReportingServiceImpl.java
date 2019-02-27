/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.service.datapolicy.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.datapolicy.DataPoint;
import com.elasticpath.service.datapolicy.CustomerPersonalDataReportingService;
import com.elasticpath.service.datapolicy.DataPointValueService;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;

/**
 * This service provides data for the "Customer Personal Data" CM report.
 */
public class CustomerPersonalDataReportingServiceImpl extends AbstractEpPersistenceServiceImpl implements CustomerPersonalDataReportingService {

	private DataPointValueService dataPointValueService;

	@Override
	public Collection<Object[]> getData(final String storeCode, final String userId) {

		List<Object[]> customerWithDataPoints = getPersistenceEngine()
			.retrieveByNamedQuery("CUSTOMER_AND_DATA_POINT_BY_STORE_AND_USER_ID", userId, storeCode);

		if (customerWithDataPoints.isEmpty()) {
			return Collections.emptyList();
		}

		Customer customer = (Customer) customerWithDataPoints.get(0)[0];

		String customerFullName = customer.getFullName();
		//prepare a map with customer GUID and a collection of data points for the DataPointValueService
		Multimap<String, DataPoint> customerGuidToDataPoints = HashMultimap.create();

		customerWithDataPoints.forEach(customerWithDataPoint -> {
				DataPoint dataPoint = (DataPoint) customerWithDataPoint[1];
				customerGuidToDataPoints.put(customer.getGuid(), dataPoint);
			}
		);

		Collection<DataPointValue> dataPointValues = dataPointValueService.getValues(customerGuidToDataPoints.asMap());

		/*
		 The "Customer Personal Data" report needs the following data:

		row["Customer Full Name"] = dataPointValue[0];
		row["Data Point Name"] = dataPointValue[1];
		row["Data Point Value"] = dataPointValue[2];
		row["Created"] = dataPointValue[3];
		row["Last Updated"] = dataPointValue[4];
		*/

		//finally, package everything that is required for the report
		List<Object[]> reportData = new ArrayList<>();

		dataPointValues.forEach(dataPointValue -> {
			Object[] reportRow = new Object[]{customerFullName, dataPointValue.getDataPointName(), dataPointValue.getValue(),
				dataPointValue.getCreatedDate(), dataPointValue.getLastModifiedDate()};

			reportData.add(reportRow);

		});

		return reportData;
	}

	@Override
	public Object getObject(final long uid) throws EpServiceException {
		return null;
	}

	public void setDataPointValueService(final DataPointValueService dataPointValueService) {
		this.dataPointValueService = dataPointValueService;
	}
}
