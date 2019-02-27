/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */

package com.elasticpath.domain.cartmodifier;

import java.util.Set;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.CatalogObject;
import com.elasticpath.persistence.api.Entity;

/**
 * Groups CartItemModifiers.
 */
public interface CartItemModifierGroup extends Entity, CatalogObject {

	/**
	 * Get the code.
	 *
	 * @return the code
	 */
	String getCode();

	/**
	 * Set the code.
	 *
	 * @param code the code
	 */
	void setCode(String code);

	/**
	 * Get an unmodifiable set of CartItemModifierGroupLdf.
	 * <p/>
	 * You can use addCartItemModifierGroupLdf, removeCartItemModifierGroupLdf below to manage the collection.
	 *
	 * @return an unmodifiable set of CartItemModifierGroupLdf
	 */
	Set<CartItemModifierGroupLdf> getCartItemModifierGroupLdf();

	/**
	 * Get an unmodifiable set of CartItemModifierField.
	 * <p/>
	 * You can use addCartItemModifierField, removeCartItemModifierField below to manage the collection.
	 *
	 * @return an unmodifiable set of CartItemModifierGroupLdf
	 */
	Set<CartItemModifierField> getCartItemModifierFields();

	/**
	 * Get the catalog that this attribute belongs to (for catalog related attributes).
	 *
	 * @return the catalog
	 */
	@Override
	Catalog getCatalog();

	/**
	 * Set the catalog that this attribute type belongs to (for catalog related attributes).
	 *
	 * @param catalog the catalog to set
	 */
	@Override
	void setCatalog(Catalog catalog);

	/**
	 * Add a cartItemModifierField and enforce some business rules.
	 *
	 * @param cartItemModifierField the cart item modifier field to add
	 */
	void addCartItemModifierField(CartItemModifierField cartItemModifierField);

	/**
	 * Remove a cartItemModifierField.
	 *
	 * @param cartItemModifierField the cart item modifier field to add
	 */
	void removeCartItemModifierField(CartItemModifierField cartItemModifierField);

	/**
	 * Add a cartItemModifierGroupLdf and enforce some business rules.
	 *
	 * @param cartItemModifierGroupLdf the cart item modifier group ldf to add
	 */
	void addCartItemModifierGroupLdf(CartItemModifierGroupLdf cartItemModifierGroupLdf);

	/**
	 * Remove a cartItemModifierGroupLdf.
	 *
	 * @param cartItemModifierGroupLdf the cart item modifier group ldf to add
	 */
	void removeCartItemModifierGroupLdf(CartItemModifierGroupLdf cartItemModifierGroupLdf);

	/**
	 * Remove all cart item modifier groups LDF.
	 */
	void removeAllCartItemModifierGroupLdf();

	/**
	 * Remove all cart item modifier fields.
	 */
	void removeAllCartItemModifierFields();

	/**
	 * Gets the CartItemModifierGroupLdf by locale.
	 *
	 * @param language the language
	 * @return the CartItemModifierGroupLdf
	 */
	CartItemModifierGroupLdf getCartItemModifierGroupLdfByLocale(String language);

	/**
	 * Gets the CartItemModifierField by code.
	 *
	 * @param code the code
	 * @return the CartItemModifierField
	 */
	CartItemModifierField getCartItemModifierFieldByCode(String code);
}
