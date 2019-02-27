/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.catalog;

import java.util.Collection;
import java.util.List;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;

/**
 * Service which retrieves {@link com.elasticpath.domain.catalog.Category}s by unique key.  Depending on the implementation, the Category may
 * or may not be retrieved from cache.
 */
public interface CategoryLookup {
	/**
	 * Load the category with the given UID.
	 *
	 * @param uidPk the category UID
	 * @param <C> the category subclass
	 * @return the category if UID exists, otherwise null
	 * @throws com.elasticpath.base.exception.EpServiceException - in case of any errors
	 */
	<C extends Category> C findByUid(long uidPk);

	/**
	 * Returns a list of <code>Category</code>s based on the given UIDs. <br>
	 *
	 * @param categoryUids a collection of category UIDs
	 * @param <C> the category subclass
	 * @return a list of <code>Category</code>s
	 */
	<C extends Category> List<C> findByUids(Collection<Long> categoryUids);

	/**
	 * Loads the category with the given guid.
	 *
	 * @param guid the category guid
	 * @param <C> the category subclass
	 * @return the category if UID exists, otherwise null
	 * @throws com.elasticpath.base.exception.EpServiceException - in case of any errors
	 */
	<C extends Category> C findByGuid(String guid);

	/**
	 * Retrieve the {@link Category} with the given category code in a particular catalog. <br>
	 * The returned Category could be either linked or non-linked depending on the catalog.
	 *
	 * @param code the category code
	 * @param catalog the catalog to search in
	 * @param <C> the category subclass
	 * @return the category with the given GUID
	 * @throws com.elasticpath.base.exception.EpServiceException in case of any error
	 */
	<C extends Category> C findByCategoryCodeAndCatalog(String code, Catalog catalog);

	/**
	 * Retrieves the {@link Category} with the given GUID in a particular catalog. <br>
	 * The returned Category could be either linked or non-linked depending on the catalog.
	 *
	 * @param categoryCode the category code
	 * @param catalogCode the catalog code to search in
	 * @param <C> the category subclass
	 * @return the category with the given GUID
	 * @throws com.elasticpath.base.exception.EpServiceException in case of any errors
	 */
	<C extends Category> C findByCategoryAndCatalogCode(String categoryCode, String catalogCode);

	/**
	 * Retrieve the {@link Category} with the given compound guid in a particular catalog. <br>
	 * The (legacy) compound guid combines a category code and a catalog code.
	 * The returned Category could be either linked or non-linked depending on the catalog. <br>
	 * Give a load tuner to tune the result or {@code null} to tune to the default.
	 *
	 * @param compoundGuid the compound GUID of the category
	 * @param <C> the category subclass
	 * @return the category with the given compound GUID
	 * @throws com.elasticpath.base.exception.EpServiceException in case of any error
	 */
	<C extends Category> C findByCompoundCategoryAndCatalogCodes(String compoundGuid);

	/**
	 * Given a parent category, returns the parent's child categories in sorted order.  If the
	 * category has no children, then this method returns an empty list.
	 *
	 * @param parent the parent category
	 * @param <C> the category subclass
	 * @return the parent's children
	 */
	<C extends Category> List<C> findChildren(Category parent);

	/**
	 * Given a child category, returns the child's parent.  If the child has no parent (i.e. it is a root category),
	 * then this method returns null;
	 *
	 * @param child the child category
	 * @param <C> the parent category's subclass
	 * @return the child's parent, or null if the child is a root category
	 */
	<C extends Category> C findParent(Category child);
}
