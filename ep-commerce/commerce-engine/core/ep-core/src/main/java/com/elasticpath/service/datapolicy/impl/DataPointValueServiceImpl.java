/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.service.datapolicy.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import org.apache.commons.lang3.mutable.MutableInt;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.datapolicy.DataPoint;
import com.elasticpath.service.datapolicy.DataPointValueReader;
import com.elasticpath.service.datapolicy.DataPointValueRemover;
import com.elasticpath.service.datapolicy.DataPointValueService;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.search.IndexNotificationService;
import com.elasticpath.service.search.IndexType;

/**
 * The service class for reading and removing underlying data point values.
 */
public class DataPointValueServiceImpl extends AbstractEpPersistenceServiceImpl implements DataPointValueService {

	private IndexNotificationService indexNotificationService;

	private List<DataPointValueReader> dataPointValueReaders;
	private List<DataPointValueRemover> dataPointValueRemovers;

	@Override
	public int removeValues(final Collection<DataPointValue> dataPointValues) {

		Map<String, List<DataPointValue>> dpLocationToDataPointValues = new HashMap<>();

		MutableInt numOfRemovedValues = new MutableInt();

		dataPointValues.forEach(dpv -> {
				List<DataPointValue> values = dpLocationToDataPointValues.getOrDefault(dpv.getLocation(), new ArrayList<>());
				values.add(dpv);
				dpLocationToDataPointValues.put(dpv.getLocation(), values);
			});

		dpLocationToDataPointValues.forEach((location, dpvs) -> {
				DataPointValueRemover remover = findDataPointRemover(location);
				numOfRemovedValues.add(remover.removeValues(dpvs));
			});

		return numOfRemovedValues.intValue();
	}

	@Override
	public int removeValuesByQuery(final String query, final String listName, final Collection<Long> entityUidPKs, final IndexType indexType) {

		int numOfUpdatedRecords = getPersistenceEngine().executeQueryWithList(query, listName, entityUidPKs);

		if (indexType != null) {
			entityUidPKs.forEach(entityUidPk ->
				indexNotificationService.addNotificationForEntityIndexUpdate(indexType, entityUidPk)
			);
		}

		return numOfUpdatedRecords;

	}

	@Override
	public boolean validateKey(final String dataKey, final String namedValidationQuery) {
		List<Integer> result = getPersistenceEngine().retrieveByNamedQuery(namedValidationQuery, dataKey);

		return !result.isEmpty();
	}

	@Override
	public boolean validateKeyForLocation(final String dataLocation, final String dataKey) {

		DataPointValueReader reader = findDataPointReader(dataLocation);

		return reader.validateKey(dataKey);
	}

	@Override
	public Object getObject(final long uid) throws EpServiceException {
		return null;
	}

	@Override
	public Collection<DataPointValue> getValues(final Map<String, ? extends Collection<DataPoint>> customerGuidToDataPoints) {

		//need to transform the map into a suitable form to find a capable reader and perform the bulk read
		Map<String, Multimap<String, DataPoint>> custGuidToDpLocationsMap = new HashMap<>();

		customerGuidToDataPoints.forEach((customerGuid, dataPoints) -> {

			Multimap<String, DataPoint> dpLocationToPoints = custGuidToDpLocationsMap.
				getOrDefault(customerGuid, HashMultimap.create());

			dataPoints.forEach(dataPoint -> dpLocationToPoints.put(dataPoint.getDataLocation(), dataPoint));

			custGuidToDpLocationsMap.put(customerGuid, dpLocationToPoints);
		});

		Collection<DataPointValue> dataPointValues = new ArrayList<>();

		custGuidToDpLocationsMap.forEach((customerGuid, dpLocationToPoints) ->
			dpLocationToPoints.keySet().forEach((dpLocation) -> {
				DataPointValueReader reader = findDataPointReader(dpLocation);
				dataPointValues.addAll(reader.readValues(customerGuid, dpLocationToPoints.get(dpLocation)));
			})
		);

		return dataPointValues;
	}

	@Override
	public <E> Collection<Object[]> readValuesByQuery(final String query, final String listParameterName, final Collection<E> listValues,
		final Object[] parameters) {

		//not all queries require list parameter
		if (query.contains(":" + listParameterName)) {
			return getPersistenceEngine().retrieveWithList(query, listParameterName, listValues, parameters, 0, listValues.size());
		}

		return getPersistenceEngine().retrieve(query, parameters);
	}

	@Override
	public Map<String, Set<String>> getLocationAndSupportedFields() {

		Map<String, Set<String>> dataLocationWithSupportedFields = new TreeMap<>();

		this.dataPointValueReaders.forEach(reader ->
			dataLocationWithSupportedFields.put(reader.getSupportedLocation(), reader.getSupportedFields().keySet())
		);

		return dataLocationWithSupportedFields;
	}

	@Override
	public Collection<DataPointValue> getCustomerDataPointValuesForStoreByPolicyGuid(final String customerGuid, final String storeCode,
		final String dataPolicyGuid) {

		List<DataPoint> dataPoints = getPersistenceEngine().retrieveByNamedQuery(
			"FIND_DATA_POINTS_FOR_STORE_BY_CUSTOMER_AND_DATA_POLICY_GUIDS", customerGuid, storeCode, dataPolicyGuid);

		if (dataPoints.isEmpty()) {
			return Collections.emptyList();
		}

		//prepare a map with customer GUID and a collection of data points for the DataPointValueService
		Map<String, Collection<DataPoint>> customerGuidToDataPoints = new HashMap<>(1);
		customerGuidToDataPoints.put(customerGuid, dataPoints);

		return getValues(customerGuidToDataPoints);
	}

	private DataPointValueReader findDataPointReader(final String dataPointLocation) {
		return this.dataPointValueReaders.stream()
			.filter(dpvr -> dpvr.isApplicableTo(dataPointLocation))
			.reduce((first, second) -> second)
			.orElseThrow(() -> new EpSystemException("There is no applicable DataPointValueReader for location \n["
				+ dataPointLocation + "]"));

	}

	private DataPointValueRemover findDataPointRemover(final String dataPointLocation) {
		return this.dataPointValueRemovers.stream()
			.filter(dpvr -> dpvr.isApplicableTo(dataPointLocation))
			.reduce((first, second) -> second)
			.orElseThrow(() -> new EpSystemException("There is no applicable DataPointValueRemover for location \n[" + dataPointLocation + "]"));
	}

	public IndexNotificationService getIndexNotificationService() {
		return indexNotificationService;
	}

	public void setIndexNotificationService(final IndexNotificationService indexNotificationService) {
		this.indexNotificationService = indexNotificationService;
	}

	public void setDataPointValueReaders(final List<DataPointValueReader> dataPointValueReaders) {
		this.dataPointValueReaders = dataPointValueReaders;
	}

	public void setDataPointValueRemovers(final List<DataPointValueRemover> dataPointValueRemovers) {
		this.dataPointValueRemovers = dataPointValueRemovers;
	}
}
