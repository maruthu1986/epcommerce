/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.service.catalogview;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.search.SfSearchLog;
import com.elasticpath.service.EpPersistenceService;

/**
 * A description of an interface that persists and retrieves SfSearchLog objects.
 */
public interface SfSearchLogService extends EpPersistenceService {
	/**
	 * Adds the given SfSearchLog.
	 *
	 * @param log the SfSearchLog to save
	 * @return the persisted instance of SfSearchLog
	 * @throws EpServiceException if there are any errors
	 */
	SfSearchLog add(SfSearchLog log) throws EpServiceException;

	/**
	 * Updates the given SfSearhLog.
	 *
	 * @param log the SfSearhLog to update
	 * @return the updated SfSearchLog instance
	 * @throws EpServiceException if there are any errors
	 */
	SfSearchLog update(SfSearchLog log) throws EpServiceException;

	/**
	 * Loads the SfSearchLog indicated by the given Uid.
	 *
	 * @param sfSearchLogUid the uid of the SfSearchLog to load
	 * @return the SfSearchLog with the specified uid if it exists
	 * @throws EpServiceException if there is an error or the uid does not exist
	 */
	SfSearchLog load(long sfSearchLogUid) throws EpServiceException;

	/**
	 * Gets the SfSearchLog indicated by the given Uid.
	 *
	 * @param sfSearchLogUid the uid of the SfSearchLog to load
	 * @return the SfSearchLog with the specified uid if it exists
	 * @throws EpServiceException if there is an error or the uid does not exist
	 */
	SfSearchLog get(long sfSearchLogUid) throws EpServiceException;
}
