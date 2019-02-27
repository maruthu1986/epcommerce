/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.commons.pagination;

import java.util.List;

/**
 * Interface for finding the model objects to paginate.
 *
 * @param <T> the model class this paginator works with
 */
public interface PaginatorLocator<T> {

	/**
	 * Finds elements with the specified criteria.
	 *
	 * @param unpopulatedPage the page to be returned
	 * @param objectId the id of an object relevant to the criteria
	 * @return the elements found for the specified criteria. Must not return null.
	 */
	List<T> findItems(Page<T> unpopulatedPage, String objectId);

	/**
	 * Get the total number of items being paginated.
	 *
	 * @param objectId the id of an object relevant to the criteria
	 * @return the total items of type T
	 */
	long getTotalItems(String objectId);
}
