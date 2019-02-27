/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.domain.datapolicy;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;

/**
 * Data policy state.
 */
public class DataPolicyState extends AbstractExtensibleEnum<DataPolicyState> {
	/**
	 * Ordinal constant for DRAFT.
	 */
	public static final int DRAFT_ORDINAL = 0;

	/**
	 * Ordinal constant for ACTIVE.
	 */
	public static final int ACTIVE_ORDINAL = 1;

	/**
	 * Ordinal constant for DISABLED.
	 */
	public static final int DISABLED_ORDINAL = 2;

	/**
	 * Name constant for DRAFT.
	 */
	public static final String DRAFT_NAME = "DRAFT";

	/**
	 * Name constant for ACTIVE.
	 */
	public static final String ACTIVE_NAME = "ACTIVE";

	/**
	 * Name constant for DISABLED.
	 */
	public static final String DISABLED_NAME = "DISABLED";
	/**
	 * Data policy state that represents "In progress" data policy state.
	 */
	public static final DataPolicyState DRAFT = new DataPolicyState(DRAFT_ORDINAL, DRAFT_NAME);
	/**
	 * Data policy state that represents "Active" data policy state.
	 */
	public static final DataPolicyState ACTIVE = new DataPolicyState(ACTIVE_ORDINAL, ACTIVE_NAME);
	/**
	 * Data policy state that represents "Disabled" data policy state.
	 */
	public static final DataPolicyState DISABLED = new DataPolicyState(DISABLED_ORDINAL, DISABLED_NAME);

	private static final long serialVersionUID = 5000000001L;

	/**
	 * Create an enum value for a enum type. Name is case-insensitive and will be stored in upper-case.
	 *
	 * @param ordinal the ordinal value
	 * @param name    the name value (this will be converted to upper-case).
	 */
	protected DataPolicyState(final int ordinal, final String name) {
		super(ordinal, name, DataPolicyState.class);
	}

	/**
	 * Find the enum value with the specified ordinal value.
	 *
	 * @param ordinal the ordinal value
	 * @return the enum value
	 */
	public static DataPolicyState valueOf(final int ordinal) {
		return valueOf(ordinal, DataPolicyState.class);
	}

	/**
	 * Find the enum value with the specified name value.
	 *
	 * @param name the name
	 * @return the enum value
	 */
	public static DataPolicyState valueOf(final String name) {
		return valueOf(name, DataPolicyState.class);
	}

	@Override
	protected Class<DataPolicyState> getEnumType() {
		return DataPolicyState.class;
	}
}
