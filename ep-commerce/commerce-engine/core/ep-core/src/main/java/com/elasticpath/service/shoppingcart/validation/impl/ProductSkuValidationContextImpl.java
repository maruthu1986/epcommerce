/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.shoppingcart.validation.ProductSkuValidationContext;

/**
 * Implements {@link ProductSkuValidationContext}.
 */
public class ProductSkuValidationContextImpl implements ProductSkuValidationContext {

	private ProductSku productSku;
	private ProductSku parentProductSku;
	private Store store;
	private Shopper shopper;
	private Price promotedPrice;

	@Override
	public ProductSku getProductSku() {
		return productSku;
	}

	@Override
	public void setProductSku(final ProductSku productSku) {
		this.productSku = productSku;
	}

	@Override
	public ProductSku getParentProductSku() {
		return parentProductSku;
	}

	@Override
	public void setParentProductSku(final ProductSku parentProductSku) {
		this.parentProductSku = parentProductSku;
	}

	@Override
	public Store getStore() {
		return store;
	}

	@Override
	public void setStore(final Store store) {
		this.store = store;
	}

	@Override
	public Shopper getShopper() {
		return shopper;
	}

	@Override
	public void setShopper(final Shopper shopper) {
		this.shopper = shopper;
	}

	@Override
	public Price getPromotedPrice() {
		return promotedPrice;
	}

	@Override
	public void setPromotedPrice(final Price promotedPrice) {
		this.promotedPrice = promotedPrice;
	}
}