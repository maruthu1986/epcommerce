/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.catalogview.impl;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalog.CategoryLookup;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalogview.ProductViewService;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.service.catalogview.StoreProductService;

/**
 * Represents a default implementation of <code>ProductViewService</code>.
 */
public class ProductViewServiceImpl implements ProductViewService {

	private StoreProductService storeProductService;

	private ProductService productService;

	private StoreConfig storeConfig;

	private CategoryLookup categoryLookup;

	/**
	 * Returns the product with the given product code. Return null if no matching product exists. You can give a product load tuner to fine control
	 * what data get populated of the returned product.
	 * <p>
	 * By giving a shopping cart, promotion rules will be applied to the returned product.
	 * 
	 * @param productCode the product code.
	 * @param shoppingCart the shopping cart, give <code>null</code> if you don't have it.
	 * @param loadProductAssociations true if product associations should be loaded for each product
	 * @return the product if a product with the given code exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public StoreProduct getProduct(
			final String productCode, final ShoppingCart shoppingCart, final boolean loadProductAssociations) throws EpServiceException {
		return getProduct(productCode, shoppingCart.getStore(), loadProductAssociations);
	}
	
	@Override
	public StoreProduct getProduct(final String productCode, final Store store, final boolean loadProductAssociations) throws EpServiceException {
		final long productUid = productService.findUidById(productCode);
		if (productUid == 0L) {
			return null;
		}

		return storeProductService.getProductForStore(productUid, store, loadProductAssociations);
	}

	/**
	 * Gets the product's parent folder with fetched fields which by default are omitted (like parent folder).
	 *
	 * @param product the product which category has to be returned
	 * @return the product's parent category
	 */
	@Override
	public Category getProductCategory(final Product product) {
		Catalog catalog = storeConfig.getStore().getCatalog();
		Category productCategory = product.getDefaultCategory(catalog);
		return getCategoryLookup().findByUid(productCategory.getUidPk());
	}

	/**
	 * Sets the <code>StoreProductService</code>.
	 * 
	 * @param storeProductService the product retrieve strategy
	 */
	public void setStoreProductService(final StoreProductService storeProductService) {
		this.storeProductService = storeProductService;
	}

	/**
	 * Sets the product service.
	 * 
	 * @param productService the product service
	 */
	public void setProductService(final ProductService productService) {
		this.productService = productService;
	}

	public void setCategoryLookup(final CategoryLookup categoryLookup) {
		this.categoryLookup = categoryLookup;
	}

	protected CategoryLookup getCategoryLookup() {
		return categoryLookup;
	}

	/**
	 * Sets the store configuration that provides context for the actions 
	 * of this service.
	 * 
	 * @param storeConfig the store configuration.
	 */
	public void setStoreConfig(final StoreConfig storeConfig) {
		this.storeConfig = storeConfig;
	}
}
