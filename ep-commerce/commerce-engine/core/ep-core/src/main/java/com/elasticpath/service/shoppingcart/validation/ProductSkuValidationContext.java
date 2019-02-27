/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation;

import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.store.Store;

/**
 * Validation context for validating product sku.
 */
public interface ProductSkuValidationContext {

	/**
	 * Getter for product sku.
	 * @return the product sku
	 */
	ProductSku getProductSku();

	/**
	 * Setter for product sku.
	 * @param productSku the product sku
	 */
	void setProductSku(ProductSku productSku);

	/**
	 * Getter for parent product sku.
	 * @return the parent product sku
	 */
	ProductSku getParentProductSku();

	/**
	 * Setter for parent product sku.
	 * @param parentProductSku the parent product sku
	 */
	void setParentProductSku(ProductSku parentProductSku);

	/**
	 * Getter for store.
	 * @return the store.
	 */
	Store getStore();

	/**
	 * Setter for store.
	 * @param store the store.
	 */
	void setStore(Store store);

	/**
	 * Getter for shopper.
	 * @return the shopper.
	 */
	Shopper getShopper();

	/**
	 * Setter for shopper.
	 * @param shopper the shopper.
	 */
	void setShopper(Shopper shopper);

	/**
	 * Getter for the promoted price for the sku.
	 * @return the price with promotions applied
	 */
	Price getPromotedPrice();

	/**
	 * Setter for the promoted price for the sku.
	 * @param promotedPrice the price with promotions applied
	 */
	void setPromotedPrice(Price promotedPrice);
}
