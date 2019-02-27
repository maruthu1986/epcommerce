/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder;

import io.reactivex.Completable;
import io.reactivex.Observable;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.rest.advise.Message;
import com.elasticpath.rest.definition.carts.LineItemEntity;

/**
 * Validation Service for items added to cart.
 */
public interface AddToCartAdvisorService {

	/**
	 * Check if the item is purchasable.
	 *
	 * @param scope  scope
	 * @param itemId item id
	 * @return the structured error message, if any.
	 */
	Observable<Message> validateItemPurchasable(String scope, String itemId);

	/**
	 * Check if the item is purchasable.
	 *
	 * @param scope  scope
	 * @param cartId the cart ID.
	 * @param itemId item id
	 * @return the structured error message, if any.
	 */
	Observable<Message> validateItemPurchasable(String scope, String cartId, String itemId);

	/**
	 * Validate if an item is purchasable.
	 *
	 * @param scope      the scope
	 * @param cartId the cart ID.
	 * @param productSku the product sku
	 * @param parentProductSku the parent product sku
	 * @return the structured error message, if any.
	 */
	Observable<Message> validateItemPurchasable(String scope, String cartId, ProductSku productSku, ProductSku parentProductSku);

	/**
	 * Validate quantity.
	 *
	 * @param lineItemEntity line item entity
	 * @return the structured error message, if any.
	 */
	Completable validateLineItemEntity(LineItemEntity lineItemEntity);
}
