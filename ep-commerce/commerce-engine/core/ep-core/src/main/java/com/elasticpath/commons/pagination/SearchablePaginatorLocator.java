/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.commons.pagination;

import java.io.Serializable;
import java.util.List;

/**
 * A variant of PaginatorLocator which passes the search criteria in.
 *
 * @param <T>
 */
public interface SearchablePaginatorLocator<T> extends Serializable {
	/**
	 * Finds elements with the specified criteria.
	 *
	 * @param unpopulatedPage the page to be returned
	 * @param objectId The object id passed in the config to PaginatorImpl.
	 * @param searchCriteria The search criteria to use.
	 * @return the elements found for the specified criteria. Must not return null.
	 */
	List<T> findItems(Page<T> unpopulatedPage, String objectId, List<SearchCriterion> searchCriteria);

	/**
	 *
	 * @param objectId The object id passed in the config to PaginatorImpl.
	 * @param searchCriteria The search criteria to use.
	 * @return the total items of type T
	 */
	long getTotalItems(List<SearchCriterion> searchCriteria, String objectId);
}
