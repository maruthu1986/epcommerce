/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.core.messaging.changeset;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;
import com.elasticpath.messaging.EventType;
import com.elasticpath.messaging.spi.EventTypeLookup;

/**
 * An enum representing ChangeSet-based {@link EventType}s that are available in the platform.
 */
public class ChangeSetEventType extends AbstractExtensibleEnum<ChangeSetEventType> implements EventType {

	private static final long serialVersionUID = 4025398581967246417L;

	/**
	 * Ordinal constant for CHANGE_SET_READY_FOR_PUBLISH.
	 */
	public static final int CHANGE_SET_READY_FOR_PUBLISH_ORDINAL = 0;

	/**
	 * Signals that a Change Set is ready for publish.
	 */
	public static final ChangeSetEventType CHANGE_SET_READY_FOR_PUBLISH = new ChangeSetEventType(CHANGE_SET_READY_FOR_PUBLISH_ORDINAL,
																								 "CHANGE_SET_READY_FOR_PUBLISH");

	/**
	 * Ordinal constant for CHANGE_SET_PUBLISHED.
	 */
	public static final int CHANGE_SET_PUBLISHED_ORDINAL = 100;

	/**
	 * Signals that a Change Set was published successfully.
	 */
	public static final ChangeSetEventType CHANGE_SET_PUBLISHED = new ChangeSetEventType(CHANGE_SET_PUBLISHED_ORDINAL,
																						 "CHANGE_SET_PUBLISHED");

	/**
	 * Ordinal constant for CHANGE_SET_PUBLISH_FAILED.
	 */
	public static final int CHANGE_SET_PUBLISH_FAILED_ORDINAL = 101;

	/**
	 * Signals that a Change Set publishing failed.
	 */
	public static final ChangeSetEventType CHANGE_SET_PUBLISH_FAILED = new ChangeSetEventType(CHANGE_SET_PUBLISH_FAILED_ORDINAL,
																							  "CHANGE_SET_PUBLISH_FAILED");

	/**
	 * Create an enum value for a enum type. Name is case-insensitive and will be stored in upper-case.
	 * 
	 * @param ordinal the ordinal value
	 * @param name the name value (this will be converted to upper-case).
	 */
	public ChangeSetEventType(final int ordinal, final String name) {
		super(ordinal, name, ChangeSetEventType.class);
	}

	@Override
	protected Class<ChangeSetEventType> getEnumType() {
		return ChangeSetEventType.class;
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
	public static ChangeSetEventType valueOf(final String name) {
		return valueOf(name, ChangeSetEventType.class);
	}

	/**
	 * ChangeSetEventType implementation of lookup interface.
	 */
	public static class ChangeSetEventTypeLookup implements EventTypeLookup<ChangeSetEventType> {

		@Override
		public ChangeSetEventType lookup(final String name) {
			try {
				return ChangeSetEventType.valueOf(name);
			} catch (IllegalArgumentException e) {
				return null;
			}
		}

	}

}
