/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.pricelistmanager.model.impl;

/**
 * Represents available base amount types.
 */
public enum BaseAmountType {
	
	/** Product type. */
	PRODUCT("PRODUCT"), //$NON-NLS-1$
	
	/** Sku type. */
	SKU("SKU"); //$NON-NLS-1$
	
	private final String type;
	
	/**
	 * Constructs base amount type.
	 * 
	 * @param type type
	 */
	BaseAmountType(final String type) {
		this.type = type;
	}

	/**
	 * Finds base amount type by string type representation.
	 * 
	 * @param type string representation of type
	 * @return base amount type
	 */
	public static BaseAmountType findByType(final String type) {
		for (BaseAmountType amountType : values()) {
			if (amountType.type.equals(type)) {
				return amountType;
			}
		}
		return null;
	}
	
	/**
	 * Gets string representation of type.
	 * 
	 * @return type
	 */
	public String getType() {
		return type;
	}

}
