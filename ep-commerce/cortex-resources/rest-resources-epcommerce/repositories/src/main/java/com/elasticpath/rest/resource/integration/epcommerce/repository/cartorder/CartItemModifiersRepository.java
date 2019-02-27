/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */

package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder;

import java.util.List;
import java.util.Map;

import io.reactivex.Single;

import com.elasticpath.domain.cartmodifier.CartItemModifierField;
import com.elasticpath.domain.cartmodifier.CartItemModifierFieldOption;
import com.elasticpath.domain.cartmodifier.CartItemModifierGroup;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.carts.LineItemConfigurationEntity;

/**
 * CartItemModifier Repository layer for integration with resource and backend service.
 */
public interface CartItemModifiersRepository {

	/**
	 * This method retrieves the cart item modifier group corresponding to the given code.
	 *
	 * @param code the code for the cart item modifier group to retrieve
	 * @return the cart item modifier group identified by the code
	 */
	ExecutionResult<CartItemModifierGroup> findCartItemModifierGroupByCode(String code);

	/**
	 * This method retrieves the cart item modifier field corresponding to the given group code and field code.
	 *
	 * @param cartItemModifierFieldCode the code for the cart item modifier field to retrieve
	 * @param cartItemModifierGroupCode the code for the cart item modifier Group  from which the field need to be retrieved
	 * @return the cart item modifier field identified by the code
	 */
	ExecutionResult<CartItemModifierField> findCartItemModifierFieldBy(String cartItemModifierFieldCode, String cartItemModifierGroupCode);

	/**
	 * This method retrieves the cart item modifier field Option corresponding to the group code, field code and option value.
	 *
	 * @param cartItemModifierOptionValue the option value for which cartItemModifierOptionValue to retrieve
	 * @param cartItemModifierFieldCode   the code for the cart item modifier field from which the option value to retrieve
	 * @param cartItemModifierGroupCode   the code for the cart item modifier Group  from which the field need to be retrieved
	 * @return the cart item modifier field identified by the code
	 */
	ExecutionResult<CartItemModifierFieldOption> findCartItemModifierFieldOptionBy(String cartItemModifierOptionValue,
		String cartItemModifierFieldCode, String cartItemModifierGroupCode);

	/**
	 * Find modifier values for a given cart line item.
	 * The returned map will contain entries for all applicable modifier fields (including those with empty values).
	 *
	 *
	 * @param cartId the shopping cart ID.
	 * @param shoppingItemGuid the shopping cart line item GUID
	 * @return map of fields to values
	 */
	Single<Map<CartItemModifierField, String>> findCartItemModifierValues(String cartId, String shoppingItemGuid);

	/**
	 * Find modifier values for a given purchase line item.
	 * The returned map will contain entries for all applicable modifier fields (including those with empty values).
	 *
	 * @param storeCode            the store code
	 * @param purchaseGuid         the purchase GUID
	 * @param purchaseLineItemGuid the purchase line item GUID
	 * @return map of fields to values
	 */
	Single<Map<CartItemModifierField, String>> findPurchaseItemModifierValues(String storeCode, String purchaseGuid,
		String purchaseLineItemGuid);

	/**
	 * Get the LineItemConfigurationEntity for an item.
	 *
	 * @param itemId the item id
	 * @return LineItemConfigurationEntity
	 */
	Single<LineItemConfigurationEntity> getConfiguration(String itemId);

	/**
	 * Find cart item modifier by product.
	 *
	 * @param product the product
	 * @return the list of cart item modifier.
	 */
	List<CartItemModifierField> findCartItemModifiersByProduct(Product product);

	/**
	 * Find missing required fields in given shopping item.
	 *
	 * @param shoppingItem the shopping item.
	 * @return the list of cart item modifier fields which are missing.
	 */
	List<String> findMissingRequiredFieldCodesByShoppingItem(ShoppingItem shoppingItem);

}
