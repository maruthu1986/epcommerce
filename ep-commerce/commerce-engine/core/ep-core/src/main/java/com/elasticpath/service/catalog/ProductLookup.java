/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.catalog;

import java.util.Collection;
import java.util.List;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.catalog.Product;

/**
 * Service which retrieves Products by unique key.  Depending on the implementation, the Product may
 * or may not be retrieved from cache.
 */
public interface ProductLookup {
	/**
	 * Finds the product with the given uid, and returns it.
	 *
	 * @param uidpk the product's primary key
	 * @param <P> the genericized Product sub-class that this finder will return
	 * @return the product that matches the given primary key, otherwise null
	 * @throws com.elasticpath.base.exception.EpServiceException - in case of any errors
	 */
	<P extends Product> P findByUid(long uidpk) throws EpServiceException;

	/**
	 * Finds the products with the given uids, and returns it.
	 *
	 * @param uidPks the products' primary keys
	 * @param <P> the genericized Product sub-class that this finder will return
	 * @return the products that match the given primary keys, otherwise an empty list
	 * @throws com.elasticpath.base.exception.EpServiceException - in case of any errors
	 */
	<P extends Product> List<P> findByUids(Collection<Long> uidPks) throws EpServiceException;

	/**
	 * Find the product with the given guid, for product, i.e. product code.
	 *
	 * @param guid the product code.
	 * @param <P> the genericized Product sub-class that this finder will return
	 * @return the product that matches the given guid (code), otherwise null
	 * @throws com.elasticpath.base.exception.EpServiceException - in case of any errors
	 */
	<P extends Product> P findByGuid(String guid) throws EpServiceException;
}
