/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.catalog.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.dao.ProductDao;
import com.elasticpath.service.catalog.ProductLookup;
import com.elasticpath.service.catalog.ProductSkuLookup;

/**
 * Default implementation of {@link com.elasticpath.service.catalog.ProductSkuLookup} that loads ProductSkus by querying the persistence engine
 * for the corresponding product and loading that Product from a {@link com.elasticpath.service.catalog.ProductLookup}.
 *
 * The ProductLookup used to load the Product should normally by persistent (e.g. {@link ProductLookupImpl})
 * instead of cached.
 */
public class ProductSkuLookupImpl implements ProductSkuLookup {
	private static final Logger LOG = Logger.getLogger(ProductSkuLookupImpl.class);
	private static final String DUPLICATE_GUID_ERR_MSG = "Inconsistent data -- duplicate guid:";

	private PersistenceEngine persistenceEngine;
	private ProductLookup productLookup;
	private ProductDao productDao;

	@Override
	@SuppressWarnings("unchecked")
	public <P extends ProductSku> P findByUid(final long uidpk) throws EpServiceException {
		Long productUid = getProductDao().findUidBySkuUid(uidpk);
		if (productUid == null) {
			return null;
		}

		final Product product = getProductLookup().findByUid(productUid);
		if (product == null) {
			LOG.warn("Product with uidpk {" + productUid + "} was not found");
			return null;
		}

		return (P) getSku(product, uidpk);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <P extends ProductSku> List<P> findByUids(final Collection<Long> uidpks) throws EpServiceException {
		List<Long> productUids = getProductDao().findUidsBySkuUids(uidpks);
		if (productUids.isEmpty()) {
			return Collections.emptyList();
		}

		final List<Product> products = getProductLookup().findByUids(productUids);
		if (products.isEmpty()) {
			return Collections.emptyList();
		}

		List<ProductSku> skus = new ArrayList<>(uidpks.size());
		for (Long skuUid : uidpks) {
			for (Product product : products) {
				for (ProductSku sku : product.getProductSkus().values()) {
					if (sku.getUidPk() == skuUid) {
						skus.add(sku);
					}
				}
			}
		}

		return (List<P>) skus;
	}

	/**
	 * This finder runs in uses the shared persistence session (EntityManager), but sets the ignoreChanges() flag
	 * on the query.  This avoids flushing the session when the query is run, which in turn avoids major
	 * re-entrant flush headaches when this method is triggered by OpenJPA-annotated callbacks in OrderSkuImpl
	 * in the middle of a flush().
	 *
	 * @param guid the guid.
	 * @param <P> the product sku sub-class
	 * @return the product sku
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <P extends ProductSku> P findByGuid(final String guid) {
		Long productUid = getProductDao().findUidBySkuGuid(guid);
		if (productUid == null) {
			return null;
		}

		final Product product = getProductLookup().findByUid(productUid);
		if (product == null) {
			LOG.warn("Product with uidpk {" + productUid + "} was not found");
			return null;
		}

		return (P) product.getSkuByGuid(guid);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <P extends ProductSku> List<P> findByGuids(final Collection<String> guids) throws EpServiceException {
		List<Long> productUids = getProductDao().findUidsBySkuGuids(guids);
		if (productUids.isEmpty()) {
			return Collections.emptyList();
		}

		final List<Product> products = getProductLookup().findByUids(productUids);
		final List<P> skus = new ArrayList<>(guids.size());
		for (String guid : guids) {
			for (Product product : products) {
				ProductSku sku = product.getSkuByGuid(guid);
				if (sku != null) {
					skus.add((P) sku);
				}
			}
		}

		return skus;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <P extends ProductSku> P findBySkuCode(final String skuCode) throws EpServiceException {
		final Long productUid = getProductDao().findUidBySkuCode(skuCode);
		if (productUid == null) {
			return null;
		}

		final Product product = getProductLookup().findByUid(productUid);
		if (product == null) {
			LOG.warn("Product with uidpk {" + productUid + "} was not found");
			return null;
		}

		return (P) product.getSkuByCode(skuCode);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <P extends ProductSku> List<P> findBySkuCodes(final Collection<String> skuCodes) throws EpServiceException {
		List<Long> productUids = getProductDao().findUidsBySkuCodes(skuCodes);
		if (productUids.isEmpty()) {
			return Collections.emptyList();
		}

		final List<Product> products = getProductLookup().findByUids(productUids);
		final List<P> skus = new ArrayList<>(skuCodes.size());
		for (String skuCode : skuCodes) {
			for (Product product : products) {
				ProductSku sku = product.getSkuByCode(skuCode);
				if (sku != null) {
					skus.add((P) sku);
				}
			}
		}

		return skus;
	}

	@Override
	public Boolean isProductSkuExist(final String skuCode) {
		final List<String> skus = getPersistenceEngine().retrieveByNamedQuery("PRODUCT_SKU_GUID_SELECT_BY_GUID", skuCode);
		final int size = skus.size();
		if (size > 1) {
			throw new EpServiceException(DUPLICATE_GUID_ERR_MSG + skuCode);
		}
		return size == 1;
	}

	/**
	 * Given a product and a sku uid, returns the sku within the product with the given uidpk.
	 *
	 * @param product the product
	 * @param skuUid the sku uid to search for
	 * @return the matching sku
	 */
	protected ProductSku getSku(final Product product, final long skuUid) {
		for (ProductSku sku : product.getProductSkus().values()) {
			if (sku.getUidPk() == skuUid) {
				return sku;
			}
		}

		return null;
	}

	public void setProductLookup(final ProductLookup productLookup) {
		this.productLookup = productLookup;
	}

	protected ProductLookup getProductLookup() {
		return productLookup;
	}

	protected ProductDao getProductDao() {
		return productDao;
	}

	public void setProductDao(final ProductDao productDao) {
		this.productDao = productDao;
	}

	public PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}

	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}
}
