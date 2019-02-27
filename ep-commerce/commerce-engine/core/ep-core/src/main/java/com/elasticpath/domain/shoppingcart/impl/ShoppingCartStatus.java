/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.domain.shoppingcart.impl;

import java.util.Collection;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;

/**
 * Represents the shopping cart status.
 *
 */
public class ShoppingCartStatus extends AbstractExtensibleEnum<ShoppingCartStatus> {

	private static final long serialVersionUID = 1L;

	/**
	 * The <code>ShoppingCartStatus</code> ordinal for active shopping cart.
	 */
	public static final int ACTIVE_ORDINAL = 1;
	/**
	 * The <code>ShoppingCartStatus</code> instance for active shopping cart.
	 */
	public static final ShoppingCartStatus ACTIVE = new ShoppingCartStatus(ACTIVE_ORDINAL, "ACTIVE",
			ShoppingCartStatus.class, "shoppingCartStatus.active");

	/**
	 * The <code>ShoppingCartStatus</code> ordinal for inactive shopping cart.
	 */
	public static final int INACTIVE_ORDINAL = 2;
	/**
	 * The <code>ShoppingCartStatus</code> instance for inactive shopping cart.
	 */
	public static final ShoppingCartStatus INACTIVE = new ShoppingCartStatus(INACTIVE_ORDINAL, "INACTIVE",
			ShoppingCartStatus.class, "shoppingCartStatus.inactive");

	private final String propertyKey;

	/**
	 * Constructor for this extensible enum.
	 * @param ordinal the ordinal value
	 * @param name the named value
	 * @param klass the Class Type
	 * @param propertyKey the property key for the enumarated value
	 * */
	public ShoppingCartStatus(final int ordinal, final String name, final Class<ShoppingCartStatus> klass, final String propertyKey) {
		super(ordinal, name, klass);
		this.propertyKey = propertyKey;
	}

	/**
	 * Find the enum value with the specified ordinal value.
	 * @param ordinal the ordinal value
	 * @return the enum value
	 */
	public static ShoppingCartStatus valueOf(final int ordinal) {
		return valueOf(ordinal, ShoppingCartStatus.class);
	}

	/**
	 * Find the enum value with the specified name.
	 * @param name the name
	 * @return the enum value
	 */
	public static ShoppingCartStatus valueOf(final String name) {
		return valueOf(name, ShoppingCartStatus.class);
	}

	/**
	 * Find the enum value with the specified name.
	 * @param name the name
	 * @return the enum value
	 */
	public static ShoppingCartStatus fromString(final String name) {
		return valueOf(name, ShoppingCartStatus.class);
	}

	/**
	 * Find all enum values for a particular enum type.
	 * @return the enum values
	 */
	public static Collection<ShoppingCartStatus> values() {
		return values(ShoppingCartStatus.class);
	}

	@Override
	protected Class<ShoppingCartStatus> getEnumType() {
		return ShoppingCartStatus.class;
	}

	@Override
	public String toString() {
		return getName();
	}

	/**
	 * Get the localization property key.
	 * 
	 * @return the localized property key
	 */
	public String getPropertyKey() {
		return propertyKey;
	}
}
