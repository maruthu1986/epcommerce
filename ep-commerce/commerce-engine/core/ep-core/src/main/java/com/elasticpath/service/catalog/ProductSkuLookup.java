/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.catalog;

import java.util.Collection;
import java.util.List;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.catalog.ProductSku;

/**
 * Service which retrieves {@link ProductSku}s by unique key.  Depending on the implementation, the ProductSku may
 * or may not be retrieved from cache.
 */
public interface ProductSkuLookup {
	/**
	 * Finds the product sku with the given uid, and returns it.
	 *
	 * @param uidpk the product sku's primary key
	 * @param <P> the genericized ProductSku sub-class that this finder will return
	 * @return the sku that matches the given primary key, otherwise null
	 * @throws com.elasticpath.base.exception.EpServiceException - in case of any errors
	 */
	<P extends ProductSku> P findByUid(long uidpk) throws EpServiceException;

	/**
	 * Finds the product skus with the given uids, and returns them.
	 *
	 * @param uidpks the product skus' primary keys
	 * @param <P> the genericized ProductSku sub-class that this finder will return
	 * @return the skus that match the given primary keys, otherwise null
	 * @throws com.elasticpath.base.exception.EpServiceException - in case of any errors
	 */
	<P extends ProductSku> List<P> findByUids(Collection<Long> uidpks) throws EpServiceException;

	/**
	 * Find the product skus with the given guids.  Note that the sku code is not the same as the guid.
	 *
	 * @param guid the sku guid.
	 * @param <P> the genericized ProductSku sub-class that this finder will return
	 * @return the sku with the given guid, otherwise null
	 * @throws com.elasticpath.base.exception.EpServiceException - in case of any errors
	 */
	<P extends ProductSku> P findByGuid(String guid) throws EpServiceException;

	/**
	 * Find the product sku with the given guid.  Note that the sku code is not the same as the guid.
	 *
	 * @param guids the sku guids.
	 * @param <P> the genericized ProductSku sub-class that this finder will return
	 * @return the sku with the given guid, otherwise null
	 * @throws com.elasticpath.base.exception.EpServiceException - in case of any errors
	 */
	<P extends ProductSku> List<P> findByGuids(Collection<String> guids) throws EpServiceException;

	/**
	 * Find the product sku with the given sku code.  Note that the sku code is not the same as the guid.
	 *
	 * @param skuCode the sku code.
	 * @param <P> the genericized ProductSku sub-class that this finder will return
	 * @return the sku that matches the given code, otherwise null
	 * @throws com.elasticpath.base.exception.EpServiceException - in case of any errors
	 */
	<P extends ProductSku> P findBySkuCode(String skuCode) throws EpServiceException;

	/**
	 * Find the product skus with the given sku codes.  Note that the sku code is not the same as the guid.
	 *
	 * @param skuCodes the sku codes.
	 * @param <P> the genericized ProductSku sub-class that this finder will return
	 * @return the skus that match the given code, otherwise null
	 * @throws com.elasticpath.base.exception.EpServiceException - in case of any errors
	 */
	<P extends ProductSku> List<P> findBySkuCodes(Collection<String> skuCodes) throws EpServiceException;

	/**
	 * Verifies whether SKU exists for given item id.
	 *
	 * @param skuCode sku code
	 * @return true if product sku exists
	 */
	Boolean isProductSkuExist(String skuCode);
}
