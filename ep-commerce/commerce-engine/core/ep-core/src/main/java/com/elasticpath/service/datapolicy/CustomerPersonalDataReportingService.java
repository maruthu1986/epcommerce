/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.service.datapolicy;

import java.util.Collection;

/**
 * CM reporting service for providing customer's data point values.
 */
public interface CustomerPersonalDataReportingService {

	/**
	 * Return report data. All fields are mandatory.
	 * The method is called by CM's CustomerPersonalDataService service.
	 *
	 * @param storeCode the store code
	 * @param userId the customer UidPk
	 * @return the list of rows with all data points belonging to the specified customer.
	 */
	Collection<Object[]> getData(String storeCode, String userId);
}
