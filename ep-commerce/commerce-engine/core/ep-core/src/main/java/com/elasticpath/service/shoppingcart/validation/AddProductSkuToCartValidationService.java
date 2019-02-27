/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.store.Store;

/**
 * Service for validating whether items can be added to the cart.
 */
public interface AddProductSkuToCartValidationService extends Validator<ProductSkuValidationContext> {

	/**
	 * builds validation context.
	 * @param productSku product sku
	 * @param parentProductSku the parent product sku
	 * @param store store
	 * @param shopper shopper
	 * @return context
	 */
	ProductSkuValidationContext buildContext(ProductSku productSku, ProductSku parentProductSku, Store store,
			Shopper shopper);

}
