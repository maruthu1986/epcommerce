/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.datapolicy;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.exception.DuplicateKeyException;
import com.elasticpath.domain.datapolicy.DataPoint;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.service.EpPersistenceService;

/**
 * Perform CRUD operations with DataPoint entity.
 */
public interface DataPointService extends EpPersistenceService {

	/**
	 * Saves the given DataPoint.
	 *
	 * @param dataPoint the datapoint to add.
	 * @return the persisted instance of datapoint.
	 * @throws DuplicateKeyException - if trying to add an existing datapoint.
	 */
	DataPoint save(DataPoint dataPoint) throws DuplicateKeyException;

	/**
	 * List all datapoints stored in the database.
	 *
	 * @return a list of datapoints.
	 * @throws EpServiceException - in case of any errors.
	 */
	List<DataPoint> list() throws EpServiceException;


	/**
	 * Load the datapoint with the given UID. Throw an unrecoverable exception if there is no matching database row.
	 *
	 * @param dataPointUid the datapoint UID
	 * @return the datapoint if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	DataPoint load(long dataPointUid) throws EpServiceException;

	/**
	 * Load the dataPoint with the given UID. Throw an unrecoverable exception if there is no matching database row.
	 *
	 * @param dataPointUid the datapoint UID.
	 * @return the datapoint if UID exists, otherwise null.
	 * @throws EpServiceException - in case of any errors.
	 */
	DataPoint get(long dataPointUid) throws EpServiceException;

	/**
	 * Retrieve the datapoint with the given guid.
	 *
	 * @param guid the guid of the datapoint.
	 * @return the datapoint with the given guid.
	 * @throws EpServiceException - in case of any errors.
	 */
	DataPoint findByGuid(String guid) throws EpServiceException;

	/**
	 * Retrieve the datapoint with the given name.
	 *
	 * @param name the name of the datapoint.
	 * @return the data point with the given name.
	 * @throws EpServiceException - in case of any errors.
	 */
	DataPoint findByName(String name) throws EpServiceException;

	/**
	 * Retrieve the datapoint with the given data location and data key.
	 *
	 * @param dataLocation the data location value.
	 * @param dataKey      the data key value.
	 * @return the data point with the given data location and data key.
	 * @throws EpServiceException - in case of any errors.
	 */
	DataPoint findByDataLocationAndDataKey(String dataLocation, String dataKey) throws EpServiceException;

	/**
	 * Retrieve the list of datapoints with the given guids.
	 *
	 * @param guids the guids of the datapoint.
	 * @return the datapoints with the given guids.
	 * @throws EpServiceException - in case of any errors.
	 */
	List<DataPoint> findByGuids(List<String> guids) throws EpServiceException;

	/**
	 * Retrieves removable data points for {@link ConsentAction#REVOKED} consents and which are not present
	 * in scope of any {@link ConsentAction#GRANTED} consent for particular customer.
	 *
	 * Data point could exist in scope of multiple data policies, thus it is important to know all
	 * applicable data policies for particular data point. However, customer might or might not give their
	 * consent to any of these data policies, thus data point -> data policy relationship is unique
	 * for every customer.
	 *
	 * @return Map of customerGuids to map of data points to set of data policies.
	 */
	Map<String, Map<DataPoint, Set<DataPolicy>>> findWithRevokedConsentsLatest();

	/**
	 * Retrieves removable data points for {@link ConsentAction#GRANTED} consents no matter whether or not they are
	 * present in scope of any {@link ConsentAction#GRANTED} consent for particular customer.
	 *
	 * Data point could exist in scope of multiple data policies, thus it is important to know all
	 * applicable data policies for particular data point. However, customer might or might not give their
	 * consent to any of these data policies, thus data point -> data policy relationship is unique
	 * for every customer.
	 *
	 * @return Map of customerGuids to map of data points to set of data policies.
	 */
	Map<String, Map<DataPoint, Set<DataPolicy>>> findWithGrantedConsentsLatest();

	/**
	 * Find unique removable data points for given policy and customer.
	 *
	 * @param dataPolicyGuid the data policy GUID.
	 * @param customerGuid the customer GUID
	 * @return the list of unique data points.
	 * @throws EpServiceException - in case of any errors.
	 */
	List<DataPoint> findUniqueRemovableForDataPolicyAndCustomer(String dataPolicyGuid, String customerGuid) throws EpServiceException;
}
