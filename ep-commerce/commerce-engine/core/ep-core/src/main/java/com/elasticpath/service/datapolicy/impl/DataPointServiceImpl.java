/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.datapolicy.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Sets;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.DuplicateKeyException;
import com.elasticpath.domain.datapolicy.DataPoint;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.service.datapolicy.DataPointService;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;

/**
 * Perform CRUD operations with DataPoint entity.
 */
public class DataPointServiceImpl extends AbstractEpPersistenceServiceImpl implements DataPointService {

	@Override
	public DataPoint save(final DataPoint dataPoint) throws DuplicateKeyException {
		getPersistenceEngine().save(dataPoint);
		return dataPoint;
	}

	@Override
	public List<DataPoint> list() throws EpServiceException {
		return getPersistenceEngine().retrieveByNamedQuery("DATAPOINT_SELECT_ALL");
	}

	@Override
	public DataPoint load(final long dataPointUid) throws EpServiceException {
		DataPoint dataPoint;
		if (dataPointUid <= 0) {
			dataPoint = getBean(ContextIdNames.DATA_POINT);
		} else {
			dataPoint = getPersistentBeanFinder().load(ContextIdNames.DATA_POINT, dataPointUid);
		}
		return dataPoint;
	}

	@Override
	public DataPoint get(final long dataPointUid) throws EpServiceException {
		DataPoint dataPoint;
		if (dataPointUid <= 0) {
			dataPoint = getBean(ContextIdNames.DATA_POINT);
		} else {
			dataPoint = getPersistentBeanFinder().get(ContextIdNames.DATA_POINT, dataPointUid);
		}
		return dataPoint;
	}

	@Override
	public Object getObject(final long uid) throws EpServiceException {
		return get(uid);
	}

	@Override
	public DataPoint findByGuid(final String guid) throws EpServiceException {
		final List<DataPoint> dataPoints = getPersistenceEngine().retrieveByNamedQuery("DATAPOINT_FIND_BY_GUID", guid);
		if (dataPoints.isEmpty()) {
			return null;
		}
		if (dataPoints.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate guid:" + guid);
		}
		return dataPoints.get(0);
	}

	@Override
	public DataPoint findByName(final String name) throws EpServiceException {
		final List<DataPoint> dataPoints = getPersistenceEngine().retrieveByNamedQuery("DATAPOINT_FIND_BY_NAME", name);
		if (dataPoints.isEmpty()) {
			return null;
		}
		if (dataPoints.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate names:" + name);
		}
		return dataPoints.get(0);
	}

	@Override
	public DataPoint findByDataLocationAndDataKey(final String dataLocation, final String dataKey) throws EpServiceException {
		final List<DataPoint> dataPoints = getPersistenceEngine().retrieveByNamedQuery("DATAPOINT_FIND_BY_LOCATION_AND_KEY",
				dataLocation, dataKey);
		if (dataPoints.isEmpty()) {
			return null;
		}
		if (dataPoints.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate dataLocation-dataKey combination:" + dataLocation + "-" + dataKey);
		}
		return dataPoints.get(0);
	}

	@Override
	public List<DataPoint> findByGuids(final List<String> guids) throws EpServiceException {
		final List<DataPoint> dataPoints = getPersistenceEngine().retrieveByNamedQueryWithList("DATAPOINT_FIND_BY_GUIDS", "list", guids);
		if (dataPoints.isEmpty()) {
			return null;
		}
		return dataPoints;
	}

	@Override
	public Map<String, Map<DataPoint, Set<DataPolicy>>> findWithRevokedConsentsLatest() {
		List<Object[]> customerDataPointGroups =
				getPersistenceEngine()
						.retrieveByNamedQuery("DATAPOINTS_FIND_WITH_REVOKED_CONSENTS_LATEST");

		return convertDataPointGroupsRawData(customerDataPointGroups);
	}

	@Override
	public Map<String, Map<DataPoint, Set<DataPolicy>>> findWithGrantedConsentsLatest() {
		List<Object[]> customerDataPointGroups =
				getPersistenceEngine()
						.retrieveByNamedQuery("DATAPOINTS_FIND_WITH_GRANTED_CONSENTS_LATEST");

		return convertDataPointGroupsRawData(customerDataPointGroups);
	}

	private Map<String, Map<DataPoint, Set<DataPolicy>>> convertDataPointGroupsRawData(final List<Object[]> rawData) {
		Map<String, Map<DataPoint, Set<DataPolicy>>> customerDataPoints = new HashMap<>();

		for (Object[] customerDataPointGroup : rawData) {
			String customerGuid = (String) customerDataPointGroup[0];
			DataPoint dataPoint = (DataPoint) customerDataPointGroup[1];
			DataPolicy dataPolicy = (DataPolicy) customerDataPointGroup[2];

			Map<DataPoint, Set<DataPolicy>> dataPointMap = new HashMap<>();
			dataPointMap.put(dataPoint, Collections.singleton(dataPolicy));

			customerDataPoints.merge(customerGuid, dataPointMap,
					(map1, map2) ->
							Stream.of(map1, map2)
									.map(Map::entrySet)
									.flatMap(Collection::stream)
									.collect(mergingCollector()));
		}

		return customerDataPoints;
	}

	/**
	 * Get merging {@link Collector} for {@link Stream Stream&lt;Map.Entry&lt;K, S extends Set&gt;&gt;}.
	 * Resulting collector groups <code>Map.Entry</code> by their key and reduces sets by merging them.
	 *
	 * @param <K> the type of resulting map key.
	 * @param <S> the type of resulting map value.
	 * @return merging collector.
	 */
	private <K, S> Collector<Map.Entry<K, Set<S>>, ?, Map<K, Set<S>>> mergingCollector() {
		return Collectors.groupingBy(Map.Entry::getKey,
				Collectors.reducing(Collections.emptySet(), Map.Entry::getValue, Sets::union));
	}

	@Override
	public List<DataPoint> findUniqueRemovableForDataPolicyAndCustomer(final String dataPolicyGuid, final String customerGuid)
		throws EpServiceException {

		return getPersistenceEngine().retrieveByNamedQuery("FIND_REMOVABLE_UNIQUE_POLICY_DATA_POINTS_FOR_CUSTOMER",
			dataPolicyGuid, customerGuid);
	}
}
