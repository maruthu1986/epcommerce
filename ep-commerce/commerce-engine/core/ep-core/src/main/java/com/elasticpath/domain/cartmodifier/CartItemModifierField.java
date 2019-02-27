/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.domain.cartmodifier;

import java.util.Set;

import com.elasticpath.persistence.api.Entity;

/**
 * Fields for CartItemModifier.
 */
public interface CartItemModifierField extends Entity, Comparable<CartItemModifierField> {

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
	 * Get is required.
	 *
	 * @return the required
	 */
	boolean isRequired();

	/**
	 * Set the is required.
	 *
	 * @param required the required
	 */
	void setRequired(boolean required);

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
	 * Get the max size.
	 *
	 * @return the max size
	 */
	Integer getMaxSize();

	/**
	 * Set the max size.
	 *
	 * @param maxSize the max size
	 */
	void setMaxSize(Integer maxSize);

	/**
	 * Get the field type.
	 *
	 * @return the field type
	 */
	CartItemModifierType getFieldType();

	/**
	 * Set the field type.
	 *
	 * @param fieldType the field type
	 */
	void setFieldType(CartItemModifierType fieldType);

	/**
	 * Get an unmodifiable the cart item modifier localized fields.
	 * <p/>
	 * Use the addCartItemModifierFieldLdf, removeCartItemModifierFieldLdf methods to manage the collection.
	 *
	 * @return the cart item modifier localized fields
	 */
	Set<CartItemModifierFieldLdf> getCartItemModifierFieldsLdf();

	/**
	 * Get an unmodifiable the cart item modifier options.
	 * <p/>
	 * Use the addCartItemModifierFieldOption, removeCartItemModifierFieldOption methods to manage the collection.
	 *
	 * @return the cart item modifier options
	 */
	Set<CartItemModifierFieldOption> getCartItemModifierFieldOptions();

	/**
	 * Add a cartItemModifierFieldLdf and enforce some business rules.
	 *
	 * @param cartItemModifierFieldLdf the cart item modifier field LDF
	 */
	void addCartItemModifierFieldLdf(CartItemModifierFieldLdf cartItemModifierFieldLdf);

	/**
	 * Remove a cartItemModifierFieldLdf.
	 *
	 * @param cartItemModifierFieldLdf the cart item modifier field LDF
	 */
	void removeCartItemModifierFieldLdf(CartItemModifierFieldLdf cartItemModifierFieldLdf);

	/**
	 * Add a cartItemModifierFieldOption and enforce some business rules.
	 *
	 * @param cartItemModifierFieldOption the cart item modifier field option
	 */
	void addCartItemModifierFieldOption(CartItemModifierFieldOption cartItemModifierFieldOption);

	/**
	 * Remove a cartItemModifierFieldOption.
	 *
	 * @param cartItemModifierFieldOption the cart item modifier field option
	 */
	void removeCartItemModifierFieldOption(CartItemModifierFieldOption cartItemModifierFieldOption);

	/**
	 * Gets CartItemModifierFieldLdf by language.
	 *
	 * @param language the locale
	 * @return the CartItemModifierFieldLdf
	 */
	CartItemModifierFieldLdf findCartItemModifierFieldLdfByLocale(String language);

	/**
	 * Gets CartItemModifierFieldOption by value.
	 *
	 * @param value the value
	 * @return the CartItemModifierFieldOption
	 */
	CartItemModifierFieldOption findCartItemModifierFieldOptionByValue(String value);
}

