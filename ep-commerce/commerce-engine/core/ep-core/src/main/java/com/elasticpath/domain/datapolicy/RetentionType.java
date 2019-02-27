/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.domain.datapolicy;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;

/**
 * Data policy retention type.
 */
public class RetentionType extends AbstractExtensibleEnum<RetentionType> {

	/**
	 * Ordinal constant for FROM_CREATION_DATE.
	 */
	public static final int FROM_CREATION_DATE_ORDINAL = 0;
	/**
	 * Ordinal constant for FROM_LAST_UPDATE.
	 */
	public static final int FROM_LAST_UPDATE_ORDINAL = 1;
	/**
	 * String name constant for FROM_CREATION_DATE.
	 */
	public static final String FROM_CREATION_DATE_VALUE = "FROM_CREATION_DATE";
	/**
	 * Retention type that represents "From creation date" retention type.
	 */
	public static final RetentionType FROM_CREATION_DATE = new RetentionType(FROM_CREATION_DATE_ORDINAL, FROM_CREATION_DATE_VALUE);
	/**
	 * String name constant for FROM_LAST_UPDATE.
	 */
	public static final String FROM_LAST_UPDATE_VALUE = "FROM_LAST_UPDATE";
	/**
	 * Retention type that represents "From last update" retention type.
	 */
	public static final RetentionType FROM_LAST_UPDATE = new RetentionType(FROM_LAST_UPDATE_ORDINAL, FROM_LAST_UPDATE_VALUE);

	private static final long serialVersionUID = 5000000001L;

	/**
	 * Create an enum value for a enum type. Name is case-insensitive and will be stored in upper-case.
	 *
	 * @param ordinal the ordinal value
	 * @param name    the name value (this will be converted to upper-case).
	 */
	protected RetentionType(final int ordinal, final String name) {
		super(ordinal, name, RetentionType.class);
	}

	/**
	 * Find the enum value with the specified ordinal value.
	 *
	 * @param ordinal the ordinal value
	 * @return the enum value
	 */
	public static RetentionType valueOf(final int ordinal) {
		return valueOf(ordinal, RetentionType.class);
	}

	/**
	 * Find the enum value with the specified name.
	 *
	 * @param name the name
	 * @return the enum value
	 */
	public static RetentionType valueOf(final String name) {
		return valueOf(name, RetentionType.class);
	}


	@Override
	protected Class<RetentionType> getEnumType() {
		return RetentionType.class;
	}

}
