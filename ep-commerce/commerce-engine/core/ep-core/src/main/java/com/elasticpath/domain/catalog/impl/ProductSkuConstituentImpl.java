/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.catalog.impl;

import java.util.Date;
import java.util.Locale;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductSkuConstituent;

/**
 * A bundle constituent item that represents a {@code ProductSku}.
 */
public class ProductSkuConstituentImpl implements ProductSkuConstituent {

	/** Serial version id. */
	private static final long serialVersionUID = 6300000001L;

	private ProductSku productSku;
	
	/**
	 * @return the SKU that this constituent represents.
	 */
	@Override
	public ProductSku getProductSku() {
		return this.productSku;
	}

	@Override
	public void setProductSku(final ProductSku sku) {
		this.productSku = sku;
	}

	/**
	 * @return the SKU code.
	 */
	@Override
	public String getCode() {
		return productSku.getSkuCode();
	}

	@Override
	public String getDisplayName(final Locale locale) {
		return productSku.getDisplayName(locale);
	}

	/**
	 * @return the parent product of the SKU this constituent represents.
	 */
	@Override
	public Product getProduct() {
		return productSku.getProduct();
	}

	/**
	 * @return false this constituent is not a product.
	 */
	@Override
	public boolean isProduct() {
		return false;
	}

	/**
	 * @return true this constituent is a product sku.
	 */
	@Override
	public boolean isProductSku() {
		return true;
	}
	
	/**
	 * @return false this constituent is not a bundle.
	 */
	@Override
	public boolean isBundle() {
		return getProduct() instanceof ProductBundle;
	}

	@Override
	public Date getEndDate() {
		return productSku.getEndDate();
	}

	@Override
	public Date getStartDate() {
		return productSku.getStartDate();
	}

}
