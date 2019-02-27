/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.tools.sync.merge.configuration.impl;

import org.apache.log4j.Logger;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.catalog.CategoryLookup;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;

/**
 *
 * The category locator class.
 *
 */
public class CategoryLocatorImpl extends AbstractEntityLocator {

	private static final Logger LOG = Logger.getLogger(CategoryLocatorImpl.class);

	private CategoryLookup categoryLookup;
	private CategoryService categoryService;

	@Override
	public boolean isResponsibleFor(final Class<?> clazz) {
		return Category.class.isAssignableFrom(clazz);
	}

	@Override
	public Persistable locatePersistence(final String guid, final Class<?> clazz)
			throws SyncToolConfigurationException {

		Persistable category = null;
		try {
			category = getCategoryLookup().findByGuid(guid);
		} catch (final EpServiceException e) {
			LOG.warn("Can't find category for GUID: [" + guid + "]", e);
		}
		return category;
	}

	@Override
	public boolean entityExists(final String guid, final Class<?> clazz) {
		return categoryService.isGuidInUse(guid);
	}

	@Override
	public Persistable locatePersistenceForSorting(final String guid, final Class<?> clazz) throws SyncToolConfigurationException {
		try {
			return getCategoryLookup().findByGuid(guid);
		} catch (final EpServiceException e) {
			LOG.warn("Can't find category for GUID: [" + guid + "]", e);
		}
		return null;
	}

	@Override
	public Persistable locatePersistentReference(final String guid, final Class<?> clazz) throws SyncToolConfigurationException {
		try {
			return getCategoryLookup().findByGuid(guid);
		} catch (final EpServiceException e) {
			LOG.warn("Can't find category for GUID: [" + guid + "]", e);
		}
		return null;
	}

	protected CategoryLookup getCategoryLookup() {
		return categoryLookup;
	}

	public void setCategoryLookup(final CategoryLookup categoryLookup) {
		this.categoryLookup = categoryLookup;
	}

	/**
	 * @param categoryService the categoryService to set
	 */
	public void setCategoryService(final CategoryService categoryService) {
		this.categoryService = categoryService;
	}

}
