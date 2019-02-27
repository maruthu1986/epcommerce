/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.core.messaging.cmuser;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;
import com.elasticpath.messaging.EventType;
import com.elasticpath.messaging.spi.EventTypeLookup;

/**
 * An enum representing CM User-based {@link EventType}s.
 */
public class CmUserEventType extends AbstractExtensibleEnum<CmUserEventType> implements EventType {

	private static final long serialVersionUID = 4025398581967246417L;

	/** Ordinal constant for CM_USER_CREATED. */
	public static final int CM_USER_CREATED_ORDINAL = 0;

	/**
	 * Signals that a CM User has been created.
	 */
	public static final CmUserEventType CM_USER_CREATED = new CmUserEventType(CM_USER_CREATED_ORDINAL, "CM_USER_CREATED");

	/** Ordinal constant for PASSWORD_RESET. */
	public static final int PASSWORD_RESET_ORDINAL = 1;

	/**
	 * Signals that a CM User's password was reset.
	 */
	public static final CmUserEventType PASSWORD_RESET = new CmUserEventType(PASSWORD_RESET_ORDINAL, "PASSWORD_RESET");

	/** Ordinal constant for PASSWORD_CHANGED. */
	public static final int PASSWORD_CHANGED_ORDINAL = 2;

	/**
	 * Signals that a CM User's password was manually changed.
	 */
	public static final CmUserEventType PASSWORD_CHANGED = new CmUserEventType(PASSWORD_CHANGED_ORDINAL, "PASSWORD_CHANGED");

	/**
	 * Create an enum value for a enum type. Name is case-insensitive and will be stored in upper-case.
	 * 
	 * @param ordinal the ordinal value
	 * @param name the name value (this will be converted to upper-case).
	 */
	protected CmUserEventType(final int ordinal, final String name) {
		super(ordinal, name, CmUserEventType.class);
	}

	@Override
	protected Class<CmUserEventType> getEnumType() {
		return CmUserEventType.class;
	}

	@JsonIgnore
	@Override
	public int getOrdinal() {
		return super.getOrdinal();
	}

	/**
	 * Find the enum value with the specified name.
	 * 
	 * @param name the name
	 * @return the enum value
	 */
	public static CmUserEventType valueOf(final String name) {
		return valueOf(name, CmUserEventType.class);
	}

	/**
	 * CmUserEventType implementation of lookup interface.
	 */
	public static class CmUserEventTypeLookup implements EventTypeLookup<CmUserEventType> {

		@Override
		public CmUserEventType lookup(final String name) {
			try {
				return CmUserEventType.valueOf(name);
			} catch (IllegalArgumentException e) {
				return null;
			}
		}

	}

}
