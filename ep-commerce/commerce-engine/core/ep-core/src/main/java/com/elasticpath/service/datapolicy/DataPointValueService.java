/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.service.datapolicy;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.elasticpath.domain.datapolicy.DataPoint;
import com.elasticpath.service.datapolicy.impl.DataPointValue;
import com.elasticpath.service.search.IndexType;

/**
 * The data point value service performs 3 basic operations:
 *
 * 1. read db values
 * 2. remove db values
 * 3. validates data point keys.
 */
public interface DataPointValueService {
	/**
	 * Remove data point values.
	 * Once identified, it is not required to pass customer GUID because
	 * each {@link DataPointValue} has the <strong>uidPk</strong> field that corresponds to the same field of the underlying
	 * data point value.
	 *
	 * @param dataPointValues the collection of {@link DataPointValue}s to be removed.
	 * @return the number of removed data point values
	 */
	int removeValues(Collection<DataPointValue> dataPointValues);

	/**
	 * Remove data point values by executing a dynamic query.
	 *
	 * @param query the dynamic query to be executed.
	 * @param listName the list name
	 * @param entityUidPKs the collection of entity UidPKs.
	 * @param indexType the optional index type required for triggering SOLR indexing.
	 * @return the number of updated records.
	 */
	int removeValuesByQuery(String query, String listName, Collection<Long> entityUidPKs, IndexType indexType);

	/**
	 * Validates data point key against the db. It is required for "vertical" and dynamic data structures like
	 * customer profile values, order data etc. The attributes are stored as column values and can be
	 * added/removed via CM.
	 *
	 * @param dataKey the data point key (the db entity attribute) to be validated.
	 * @param namedValidationQuery the named query used for key validation.
	 * @return true if key exists in the db
	 */
	boolean validateKey(String dataKey, String namedValidationQuery);


	/**
	 * Validate data point key for a given location.
	 *
	 * @param dataLocation the data point location.
	 * @param dataKey the data point key.
	 * @return true if the key is valid.
	 */
	boolean validateKeyForLocation(String dataLocation, String dataKey);

	/**
	 * Return a list of customer data point values for the given list of data points.
	 *
	 * @param customerGuidToDataPoints the map with customer GUID-to-set of data points
	 * @return the list of customer data point values
	 */
	Collection<DataPointValue> getValues(Map<String, ? extends Collection<DataPoint>> customerGuidToDataPoints);

	/**
	 * Get data point values by executing a given dynamic query.
	 *
	 * @param query the dynamic query to be executed.
	 * @param listParameterName the list parameter name
	 * @param listValues the list values.
	 * @param parameters the parametesr.
	 * @param <E> the type
	 * @return the list of {@link Object[]} representing data values.
	 */
	<E> Collection<Object[]> readValuesByQuery(String query, String listParameterName, Collection<E> listValues, Object[] parameters);

	/**
	 * Provide the list of data point locations and their supported fields
	 * so it can be used in CM GUI while creating data points.
	 *
	 * @return the map with data point locations and their supported fields(if any)
	 */
	Map<String, Set<String>> getLocationAndSupportedFields();

	/**
	 * Retrieve customer data point values for the selected policy and store.
	 *
	 * @param customerGuid the customer guid
	 * @param storeCode the store code
	 * @param dataPolicyGuid data policy guid
	 * @return the list of customer data points
	 */
	Collection<DataPointValue> getCustomerDataPointValuesForStoreByPolicyGuid(String customerGuid, String storeCode, String dataPolicyGuid);
}
