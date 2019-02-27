/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.catalog.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.catalog.ProductBundleService;

/**
 * Implementation of ProductBundleService to work with {@link ProductBundle}s.
 */
public class ProductBundleServiceImpl extends ProductServiceImpl implements ProductBundleService {

	private PersistenceEngine persistenceEngine;

	@Override
	public ProductBundle findBundleByConstituentGuid(final String guid) {
		return getProductBundleDao().findByBundleConstituentGuid(guid);
	}

	@Override
	public List<ProductBundle> findByProduct(final String productCode) {
		return getProductBundleDao().findByProduct(productCode);
	}

	@Override
	public List<ProductBundle> getProductBundles() {
		return getProductBundleDao().getProductBundles();
	}

	@Override
	public List<ProductBundle> findByGuids(final List<String> guids) {
		return getProductBundleDao().findByGuids(guids);
	}

	@Override
	public List<ProductBundle> findByGuids(final List<String> guids, final ProductLoadTuner productLoadTuner) {
		return getProductBundleDao().findByGuids(guids, productLoadTuner);
	}

	@Override
	public ProductBundle findByGuidWithFetchGroupLoadTuner(final String guid, final FetchGroupLoadTuner fetchGroupLoadTuner) {
		final Product product = getProductBundleDao().findByGuid(guid, fetchGroupLoadTuner);
		if (product instanceof ProductBundle) {
			return (ProductBundle) product;
		}
		return null;
	}

	@Override
	public boolean bundleExistsWithGuid(final String guid) {
		return getProductBundleDao().bundleExistsWithGuid(guid);
	}

	@Override
	public String findBundleCodeBySkuCode(final String skuCode) {
		return getProductBundleDao().findBundleCodeBySkuCode(skuCode);
	}

	@Override
	public Set<ProductBundle> findAllProductBundlesContaining(final Product product) {
		final Set<ProductBundle> bundles = new HashSet<>();
		populate(bundles, product);
		return bundles;
	}

	private void populate(final Set<ProductBundle> bundles, final Product product) {
		final Collection<ProductBundle> parents = findProductBundlesContaining(product);
		for (final ProductBundle parent : parents) {
			if (!bundles.contains(parent)) {
				bundles.add(parent);
				populate(bundles, parent);
			}
		}
	}

	@Override
	public Collection<ProductBundle> findProductBundlesContaining(final Product product) {
		final Set<ProductBundle> bundles = new HashSet<>();
		final String productCode = product.getCode();

		bundles.addAll(getProductBundleDao().findByProduct(productCode));
		bundles.addAll(getProductBundleDao().findByProductSkusOfProduct(productCode));

		return bundles;
	}

	@Override
	public Set<Long> findProductBundleUidsContainingProduct(final Product product) {
		List<Object[]> ids = findProductBundleIdsContainingProduct(product);

		return extractUids(ids);
	}

	/**
	 * Finds id tuples of ProductBundles that directly contain the given product or any of the product's skus
	 * as a BundleConstituent.
	 *
	 * Each tuple is an 2 element Object array containing the ProductBundle's id as the first element and the
	 * bundle's product code as the second element.
	 *
	 * @param product the product
	 * @return a list of [uidpk, code] tuples
	 */
	private List<Object[]> findProductBundleIdsContainingProduct(final Product product) {
		final List<Object[]> bundleProductIds = findProductBundleUidsContainingProductCode(product.getCode());

		final List<Object[]> bundleProductSkuIds = getPersistenceEngine().retrieveByNamedQueryWithList(
				"FIND_BUNDLE_IDS_BY_CONSTITUENT_PRODUCT_SKUS", "list", new ArrayList<>(product.getProductSkus().keySet()));

		List<Object[]> result = new ArrayList<>();
		result.addAll(bundleProductIds);
		result.addAll(bundleProductSkuIds);

		return result;
	}

	/**
	 * Given a list of id tuples of the form [uidpk, code], returns a corresponding Set of uidpks.
	 *
	 * @param idTuples the tuples
	 * @return the uidpks extracted from the list of tuples
	 */
	private Set<Long> extractUids(final List<Object[]> idTuples) {
		HashSet<Long> uids = new HashSet<>(idTuples.size() * 2);
		for (Object[] ids : idTuples) {
			uids.add((Long) ids[0]);
		}

		return uids;
	}

	/**
	 * Given a list of id tuples of the form [uidpk, code], returns a corresponding Set of codes.
	 *
	 * @param idTuples the tuples
	 * @return the codes extracted from the list of tuples
	 */
	private Set<String> extractCodes(final List<Object[]> idTuples) {
		HashSet<String> codes = new HashSet<>(idTuples.size() * 2);
		for (Object[] ids : idTuples) {
			codes.add((String) ids[1]);
		}

		return codes;
	}

	/**
	 * Returns a list of bundle id tuples of the form (uidpk, productCode) of bundles that contain the given product code as a constituent.
	 *
	 * @param productCode the product code
	 * @return the tuple list
	 */
	private List<Object[]> findProductBundleUidsContainingProductCode(final String productCode) {
		return getPersistenceEngine().retrieveByNamedQuery(
					"FIND_BUNDLE_IDS_BY_CONSTITUENT_PRODUCT_CODE", productCode);
	}

	@Override
	public Set<Long> findAllProductBundleUidsContainingProduct(final Product product) {
		//  Determine the ids of the ProductBundles that directly contain the given product
		List<Object []> directBundleIds = findProductBundleIdsContainingProduct(product);
		Set<Long> bundleUids = extractUids(directBundleIds);
		Set<String> bundleCodes = extractCodes(directBundleIds);

		//  Recursively loop through and add the ids of ProductBundles that contain any
		//  ProductBundles that contain our product.
		while (!bundleCodes.isEmpty()) {
			Set<String> newBundleCodes = new HashSet<>();

			for (String bundleCode : bundleCodes) {
				List<Object[]> indirectBundleIds = findProductBundleUidsContainingProductCode(bundleCode);
				for (Object[] indirectBundleId : indirectBundleIds) {
					if (!bundleUids.contains(indirectBundleId[0])) {
						bundleUids.add((Long) indirectBundleId[0]);
						newBundleCodes.add((String) indirectBundleId[1]);
					}
				}
			}

			bundleCodes = newBundleCodes;
		}
		
		return bundleUids;
	}

	@Override
	protected ProductBundleService getProductBundleService() {
		return this;
	}

	protected PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}

	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}
}
