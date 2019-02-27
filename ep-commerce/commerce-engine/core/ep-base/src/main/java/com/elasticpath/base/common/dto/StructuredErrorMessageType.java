/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.base.common.dto;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;

/**
 * Type safe extensible enumeration of structured error message types.
 */
public class StructuredErrorMessageType extends AbstractExtensibleEnum<StructuredErrorMessageType> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * Ordinal constant for ERROR.
	 */
	public static final int ERROR_ORDINAL = 1;

	/**
	 * Error message type.
	 */
	public static final StructuredErrorMessageType ERROR = new StructuredErrorMessageType(ERROR_ORDINAL, "error");

	/**
	 * Ordinal constant for WARNING.
	 */
	public static final int WARNING_ORDINAL = 2;

	/**
	 * WARNING message type.
	 */
	public static final StructuredErrorMessageType WARNING = new StructuredErrorMessageType(WARNING_ORDINAL, "warning");

	/**
	 * Ordinal constant for INFORMATION.
	 */
	public static final int INFORMATION_ORDINAL = 3;

	/**
	 * Information message type.
	 */
	public static final StructuredErrorMessageType INFORMATION = new StructuredErrorMessageType(INFORMATION_ORDINAL, "information");

	/**
	 * Ordinal constant for PROMOTION.
	 */
	public static final int PROMOTION_ORDINAL = 4;

	/**
	 * Ordinal constant for NEEDINFO.
	 */
	public static final int NEEDINFO_ORDINAL = 5;

	/**
	 * NEEDINFO message type.
	 */
	public static final StructuredErrorMessageType NEEDINFO = new StructuredErrorMessageType(NEEDINFO_ORDINAL, "needinfo");


	/**
	 * Promotion message type.
	 */
	public static final StructuredErrorMessageType PROMOTION = new StructuredErrorMessageType(PROMOTION_ORDINAL, "promotion");

	/**
	 * Construct a new structured error message type from the given name.
	 *
	 * @param ordinal the ordinal
	 * @param name    the name of structured error message type.
	 */
	protected StructuredErrorMessageType(final int ordinal, final String name) {
		super(ordinal, name, StructuredErrorMessageType.class);
	}

	@Override
	protected Class<StructuredErrorMessageType> getEnumType() {
		return StructuredErrorMessageType.class;
	}

	@Override
	public String getName() {
		return super.getName().toLowerCase();
	}
}
