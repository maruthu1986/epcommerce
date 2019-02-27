/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.catalog;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;

/**
 *	Service for working with {@link ProductBundle}s.
 */
public interface ProductBundleService extends ProductService {

	/**
	 * Find all {@link ProductBundle} which has the given product directly as constituent.
	 * Does not count nested structures.
	 * @param productCode GUID code of the product.
	 * @return list of {@link ProductBundle}s found. Empty list if none.
	 */
	List<ProductBundle> findByProduct(String productCode);

	/**
	 * Find the {@link ProductBundle} with the given guid, i.e. product code, with
	 * a fetch group load tuner.
	 * @param guid the product code
	 * @param fetchGroupLoadTuner the fetch group load tuner
	 * @return the product that matches the given guid (code), otherwise null
	 */
	ProductBundle findByGuidWithFetchGroupLoadTuner(String guid, FetchGroupLoadTuner fetchGroupLoadTuner);

	/**
	 * Returns a list of <code>ProductBundles</code> based on the given GUIDs. If a given product GUID is not found, it
	 * won't be included in the return list.
	 * Consider using a {@link ProductLoadTuner} to control what fields are loaded.
	 *
	 * @param guids the list of GUIDs that needs to find
	 * @return a list of <code>ProductBundle</code>s
	 */
	List<ProductBundle> findByGuids(List<String> guids);

	/**
	 * Return a <code>ProductBundle</code> based on the guid of a constituent. This is the
	 * guid of the <code>BundleConstituent</code> not of the <code>Product</code>
	 *
	 * @param guid the guid for a bundle constituent
	 * @return the bundle which contains this constituent.
	 */
	ProductBundle findBundleByConstituentGuid(String guid);

	/**
	 * Returns a list of <code>ProductBundles</code> based on the given GUIDs. If a given product GUID is not found, it
	 * won't be included in the return list.
	 *
	 * @param guids the list of GUIDs that needs to find
	 * @param productLoadTuner the load tuner, give <code>null</code> to populate all related data
	 * @return a list of <code>ProductBundle</code>s
	 */
	List<ProductBundle> findByGuids(List<String> guids, ProductLoadTuner productLoadTuner);

	/**
	 * @return a list of the product bundles in the system
	 */
	List<ProductBundle> getProductBundles();

	/**
	 * Determines whether a Product Bundle exists for the given GUID.
	 *
	 * @param guid the guid to check.
	 * @return True if the Product Bundle exists, false otherwise.
	 */
	boolean bundleExistsWithGuid(String guid);

	/**
	 * Find the product code of a bundle with the given bundle sku code.
	 *
	 * @param skuCode the sku code
	 * @return the product code of the bundle
	 */
	String findBundleCodeBySkuCode(String skuCode);

	/**
	 * Returns a collection of product bundles in which the product with the given code exists directly (not nested).
	 * @param product the product
	 * @return the product bundles
	 */
	Collection<ProductBundle> findProductBundlesContaining(Product product);

	/**
	 * Returns a collection of all product bundles in which the product with the given code exists (including nested).
	 * @param product Product
	 * @return the product bundles
	 */
	Collection<ProductBundle> findAllProductBundlesContaining(Product product);

	/**
	 * Returns a collection of the uidpks of all product bundles in which the product with the given code, or any of the product skus
	 * exist directly (i.e. not nested in another bundle).
	 *
	 * @param product Product
	 * @return the product bundle uidpks
	 */
	Set<Long> findProductBundleUidsContainingProduct(Product product);

	/**
	 * Returns a collection of the uidpks of all product bundles in which the product with the given code, or any of the product skus
	 * exist directly or indirectly (i.e. nested in another bundle).
	 *
	 * @param product Product
	 * @return the product bundle uidpks
	 */
	Set<Long> findAllProductBundleUidsContainingProduct(Product product);
}
