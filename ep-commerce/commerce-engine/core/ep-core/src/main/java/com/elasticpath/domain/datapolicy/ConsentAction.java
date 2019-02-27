/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.domain.datapolicy;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;

/**
 * Customer consent action.
 */
public class ConsentAction extends AbstractExtensibleEnum<ConsentAction> {
	/**
	 * Ordinal constant for GRANTED.
	 */
	public static final int GRANTED_ORDINAL = 1;

	/**
	 * Ordinal constant for REVOKED.
	 */
	public static final int REVOKED_ORDINAL = 2;
	/**
	 * Represents "GRANTED" consent action.
	 */
	public static final ConsentAction GRANTED = new ConsentAction(GRANTED_ORDINAL, "GRANTED");
	/**
	 * Represents "REVOKED" consent action.
	 */
	public static final ConsentAction REVOKED = new ConsentAction(REVOKED_ORDINAL, "REVOKED");

	private static final long serialVersionUID = 5000000001L;

	/**
	 * Create an enum value for a enum type. Name is case-insensitive and will be stored in upper-case.
	 *
	 * @param ordinal the ordinal value
	 * @param name    the name value (this will be converted to upper-case).
	 */
	protected ConsentAction(final int ordinal, final String name) {
		super(ordinal, name, ConsentAction.class);
	}

	/**
	 * Find the enum value with the specified ordinal value.
	 *
	 * @param ordinal the ordinal value
	 * @return the enum value
	 */
	public static ConsentAction valueOf(final int ordinal) {
		return valueOf(ordinal, ConsentAction.class);
	}

	/**
	 * Find the enum value with the specified ordinal value.
	 *
	 * @param action the boolean action value to translate to enum
	 * @return the enum value
	 */
	public static ConsentAction valueOf(final boolean action) {
		if (action) {
			return GRANTED;
		}
		return REVOKED;
	}

	/**
	 * Find the enum value with the specified name value.
	 *
	 * @param name the name
	 * @return the enum value
	 */
	public static ConsentAction valueOf(final String name) {
		return valueOf(name, ConsentAction.class);
	}

	@Override
	protected Class<ConsentAction> getEnumType() {
		return ConsentAction.class;
	}
}
