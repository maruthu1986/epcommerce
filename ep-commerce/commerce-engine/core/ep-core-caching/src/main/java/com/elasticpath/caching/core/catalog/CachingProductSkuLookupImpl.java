/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.caching.core.catalog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.cache.Cache;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.catalog.ProductLookup;
import com.elasticpath.service.catalog.ProductSkuLookup;

/**
 * A cached version of the {@link com.elasticpath.service.catalog.ProductSkuLookup} interface.
 */
public class CachingProductSkuLookupImpl implements ProductSkuLookup {
	private static final Logger LOG = Logger.getLogger(CachingProductSkuLookupImpl.class);

	private Cache<Long, Long> uidToProductCache;
	private Cache<String, Long> guidToProductCache;
	private Cache<String, Long> skuCodeToProductCache;
	private Cache<String, Boolean> skuCodeToExistenceStatusCache;
	private ProductSkuLookup fallbackProductSkuLookup;
	private ProductLookup productLookup;

	@Override
	@SuppressWarnings("unchecked")
	public <P extends ProductSku> P findByUid(final long uidPk) throws EpServiceException {
		Long productUid = getUidToProductCache().get(uidPk);
		if (productUid == null) {
			ProductSku productSku = getFallbackProductSkuLookup().findByUid(uidPk);
			if (productSku == null) {
				LOG.warn("Could not load product sku with uidPk from fallback sku lookup" + uidPk);
				return null;
			}
			cacheSkuIds(productSku.getProduct());

			return (P) productSku;
		} else {
			Product product = getProductLookup().findByUid(productUid);
			if (product == null) {
				LOG.warn("No product found for productUid " + productUid);
				return null;
			}
			return (P) getProductSku(product, uidPk);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <P extends ProductSku> List<P> findByUids(final Collection<Long> uidpks) throws EpServiceException {
		ArrayList<P> productSkus = new ArrayList<>();
		for (Long skuUid : uidpks) {
			ProductSku sku = findByUid(skuUid);
			if (sku == null) {
				continue;
			}
			productSkus.add((P) sku);
		}

		return productSkus;
	}

	private ProductSku getProductSku(final Product product, final long skuUid) {
		for (ProductSku sku : product.getProductSkus().values()) {
			if (sku.getUidPk() == skuUid) {
				return sku;
			}
		}

		LOG.warn("No product sku found for product [" + product.getCode() + "] skuUid " + skuUid);
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <P extends ProductSku> P findByGuid(final String guid) throws EpServiceException {
		Long productUid = getGuidToProductCache().get(guid);
		if (productUid == null) {
			ProductSku productSku = getFallbackProductSkuLookup().findByGuid(guid);
			if (productSku == null) {
				LOG.warn("Could not load product sku with guid from fallback sku lookup [" + guid + "]");
				return null;
			}
			cacheSkuIds(productSku.getProduct());

			return (P) productSku;
		} else {
			Product product = getProductLookup().findByUid(productUid);
			if (product == null) {
				LOG.warn("No product found for productUid " + productUid);
				return null;
			}
			return (P) product.getSkuByGuid(guid);
		}
	}

	/**
	 * Note that this is not especially efficient, and should be revisited if perf data suggests that
	 * loading skus one at a time instead of all at once is causing real-world issues.
	 *
	 * @param guids the sku guids.
	 * @param <P> the genericized ProductSku sub-class that this finder will return
	 * @return the sku with the given guid, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <P extends ProductSku> List<P> findByGuids(final Collection<String> guids) throws EpServiceException {
		ArrayList<P> productSkus = new ArrayList<>();
		for (String guid : guids) {
			ProductSku sku = findByGuid(guid);
			if (sku == null) {
				continue;
			}
			productSkus.add((P) sku);
		}

		return productSkus;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <P extends ProductSku> P findBySkuCode(final String skuCode) throws EpServiceException {
		Long productUid = getSkuCodeToProductCache().get(skuCode);
		if (productUid == null) {
			ProductSku productSku = getFallbackProductSkuLookup().findBySkuCode(skuCode);
			if (productSku == null) {
				LOG.warn("Could not load product sku with sku code from fallback sku lookup [" + skuCode + "]");
				return null;
			}
			cacheSkuIds(productSku.getProduct());

			return (P) productSku;
		} else {
			Product product = getProductLookup().findByUid(productUid);
			if (product == null) {
				LOG.warn("No product found for productUid " + productUid);
				return null;
			}
			return (P) product.getSkuByCode(skuCode);
		}
	}

	/**
	 * Note that this is not especially efficient, and should be revisited if perf data suggests that
	 * loading skus one at a time instead of all at once is causing real-world issues.
	 *
	 * @param skuCodes the sku code.
	 * @param <P> The ProductSku implementation sub-class
	 * @return the matching product skus
	 * @throws EpServiceException if anything goes wrong
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <P extends ProductSku> List<P> findBySkuCodes(final Collection<String> skuCodes) throws EpServiceException {
		ArrayList<P> productSkus = new ArrayList<>();
		for (String skuCode : skuCodes) {
			ProductSku sku = findBySkuCode(skuCode);
			if (sku == null) {
				continue;
			}
			productSkus.add((P) sku);
		}

		return productSkus;
	}

	@Override
	public Boolean isProductSkuExist(final String skuCode) {

		Boolean isProductSkuExistStatus = getSkuCodeToExistenceStatusCache().get(skuCode);
		if (isProductSkuExistStatus == null) {
			isProductSkuExistStatus = getFallbackProductSkuLookup().isProductSkuExist(skuCode);
			getSkuCodeToExistenceStatusCache().put(skuCode, isProductSkuExistStatus);
		}
		return isProductSkuExistStatus;
	}

	/**
	 * Adds all the product's skus' ids into the sku-id-to-product-id caches.
	 *
	 * @param product the product whose skus' ids to add to cache
	 */
	protected void cacheSkuIds(final Product product) {
		for (ProductSku sku : product.getProductSkus().values()) {
			cacheSkuIds(sku);
		}
	}

	/**
	 * Adds the given sku's ids into the sku-id-to-product-id caches.
	 * @param sku the sku whose ids to add to cache
	 */
	protected void cacheSkuIds(final ProductSku sku) {
		final long productUid = sku.getProduct().getUidPk();

		getUidToProductCache().put(sku.getUidPk(), productUid);
		getGuidToProductCache().put(sku.getGuid(), productUid);
		getSkuCodeToProductCache().put(sku.getSkuCode(), productUid);
	}

	public void setProductLookup(final ProductLookup productLookup) {
		this.productLookup = productLookup;
	}

	protected ProductLookup getProductLookup() {
		return productLookup;
	}

	public void setUidToProductCache(final Cache<Long, Long> productUidCache) {
		this.uidToProductCache = productUidCache;
	}

	protected Cache<Long, Long> getUidToProductCache() {
		return uidToProductCache;
	}

	public void setFallbackProductSkuLookup(final ProductSkuLookup fallbackProductSkuLookup) {
		this.fallbackProductSkuLookup = fallbackProductSkuLookup;
	}

	protected ProductSkuLookup getFallbackProductSkuLookup() {
		return fallbackProductSkuLookup;
	}

	public void setGuidToProductCache(final Cache<String, Long> guidToProductCache) {
		this.guidToProductCache = guidToProductCache;
	}

	protected Cache<String, Long> getGuidToProductCache() {
		return guidToProductCache;
	}

	public void setSkuCodeToProductCache(final Cache<String, Long> skuCodeToProductCache) {
		this.skuCodeToProductCache = skuCodeToProductCache;
	}

	protected Cache<String, Long> getSkuCodeToProductCache() {
		return skuCodeToProductCache;
	}

	public Cache<String, Boolean> getSkuCodeToExistenceStatusCache() {
		return skuCodeToExistenceStatusCache;
	}

	public void setSkuCodeToExistenceStatusCache(final Cache<String, Boolean> skuCodeToExistenceStatus) {
		this.skuCodeToExistenceStatusCache = skuCodeToExistenceStatus;
	}
}
