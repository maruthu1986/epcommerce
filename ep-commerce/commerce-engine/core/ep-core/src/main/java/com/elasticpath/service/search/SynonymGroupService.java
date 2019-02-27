/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.service.search;

import java.util.Collection;
import java.util.Locale;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.search.SynonymGroup;
import com.elasticpath.service.EpPersistenceService;

/**
 * Provides synonym-group related services.
 */
public interface SynonymGroupService extends EpPersistenceService {

	/**
	 * Saves or updates a given {@link SynonymGroup}.
	 *
	 * @param synonymGroup the {@link SynonymGroup} to save or update
	 * @return SynonymGroup the updated SynonymGroup
	 * @throws EpServiceException in case of any errors
	 */
	SynonymGroup saveOrUpdate(SynonymGroup synonymGroup) throws EpServiceException;

	/**
	 * Deletes a {@link SynonymGroup}.
	 *
	 * @param synonymGroup the {@link SynonymGroup} to remove
	 * @throws EpServiceException in case of any errors
	 */
	void remove(SynonymGroup synonymGroup) throws EpServiceException;

	/**
	 * Gets a {@link SynonymGroup} with the given UID. Return null if no matching records exist.
	 *
	 * @param synonymGroupUid the {@link SynonymGroup} UID
	 * @return the {@link SynonymGroup} with the attributes populated if the UID exists, otherwise null
	 * @throws EpServiceException in case of any errors
	 */
	SynonymGroup getSynonymGroup(long synonymGroupUid) throws EpServiceException;

	/**
	 * Gets a collection of {@link SynonymGroup}s for a given catalog UID.
	 *
	 * @param catalogUid the catalog UID
	 * @return a collection of {@link SynonymGroup}
	 * @throws EpServiceException in case of any errors
	 */
	Collection<SynonymGroup> findAllSynonymGroupForCatalog(long catalogUid) throws EpServiceException;

	/**
	 * Returns whether the given concept term exists within the given catalog/locale.
	 *
	 * @param conceptTerm the concept term to check
	 * @param catalog the catalog to check in
	 * @param locale the language to check in
	 * @return whether the given concept term exists within the given catalog/locale
	 */
	boolean conceptTermExists(String conceptTerm, Catalog catalog, Locale locale);
}
