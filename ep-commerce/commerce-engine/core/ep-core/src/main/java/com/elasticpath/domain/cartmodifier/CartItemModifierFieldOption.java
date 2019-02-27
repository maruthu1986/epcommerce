/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */

package com.elasticpath.domain.cartmodifier;

import java.util.Set;

import com.elasticpath.persistence.api.Persistable;

/**
 * Options for CartItemModifierField.
 */
public interface CartItemModifierFieldOption extends Persistable, Comparable<CartItemModifierFieldOption> {

	/**
	 * Get the value.
	 *
	 * @return the value
	 */
	String getValue();

	/**
	 * Set the value.
	 *
	 * @param value the value
	 */
	void setValue(String value);

	/**
	 * Get the ordering.
	 *
	 * @return the ordering
	 */
	int getOrdering();

	/**
	 * Set the ordering.
	 *
	 * @param ordering the ordering
	 */
	void setOrdering(int ordering);

	/**
	 * Get the cart item modifier field options localized fields.
	 *
	 * @return the cart item modifier field options localized fields
	 */
	Set<CartItemModifierFieldOptionLdf> getCartItemModifierFieldOptionsLdf();

	/**
	 * Add a cartItemModifierFieldOptionLdf and enforce some business rules.
	 *
	 * @param cartItemModifierFieldOptionLdf the cart item modifier field option LDF
	 */
	void addCartItemModifierFieldOptionLdf(CartItemModifierFieldOptionLdf cartItemModifierFieldOptionLdf);

	/**
	 * Remove a cartItemModifierFieldOptionLdf.
	 *
	 * @param cartItemModifierFieldOptionLdf the cart item modifier field option LDF
	 */
	void removeCartItemModifierFieldOptionLdf(CartItemModifierFieldOptionLdf cartItemModifierFieldOptionLdf);

	/**
	 * Gets the CartItemModifierFieldOptionLdf by locale.
	 *
	 * @param locale the locale
	 * @return the CartItemModifierFieldOptionLdf
	 */
	CartItemModifierFieldOptionLdf getCartItemModifierFieldOptionsLdfByLocale(String locale);
}
