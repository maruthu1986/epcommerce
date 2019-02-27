/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.core.messaging.customer;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;
import com.elasticpath.messaging.EventType;
import com.elasticpath.messaging.spi.EventTypeLookup;

/**
 * An enum representing CUSTOMER-based {@link EventType}s.
 */
public class CustomerEventType extends AbstractExtensibleEnum<CustomerEventType> implements EventType {

	private static final long serialVersionUID = 4025398581967246417L;

	/** Ordinal constant for CUSTOMER_CREATED. */
	public static final int CUSTOMER_REGISTERED_ORDINAL = 0;

	/**
	 * Signals that a customer has registered.
	 */
	public static final CustomerEventType CUSTOMER_REGISTERED = new CustomerEventType(CUSTOMER_REGISTERED_ORDINAL, "CUSTOMER_REGISTERED");

	/** Ordinal constant for PASSWORD_CHANGED. */
	public static final int PASSWORD_CHANGED_ORDINAL = 1;

	/**
	 * Signals that a password was changed.
	 */
	public static final CustomerEventType PASSWORD_CHANGED = new CustomerEventType(PASSWORD_CHANGED_ORDINAL, "PASSWORD_CHANGED");

	/** Ordinal constant for PASSWORD_FORGOTTEN. */
	public static final int PASSWORD_FORGOTTEN_ORDINAL = 2;

	/**
	 * Signals that a password was forgotten.
	 */
	public static final CustomerEventType PASSWORD_FORGOTTEN = new CustomerEventType(PASSWORD_FORGOTTEN_ORDINAL, "PASSWORD_FORGOTTEN");

	/** Ordinal constant for ANONYMOUS_CUSTOMER_REGISTERED. */
	public static final int ANONYMOUS_CUSTOMER_REGISTERED_ORDINAL = 3;

	/**
	 * Signals that an anonymous customer has become a registered user.
	 */
	public static final CustomerEventType ANONYMOUS_CUSTOMER_REGISTERED = new CustomerEventType(ANONYMOUS_CUSTOMER_REGISTERED_ORDINAL,
			"ANONYMOUS_CUSTOMER_REGISTERED");

	/** Ordinal constant for WISH_LIST_SHARED. */
	public static final int WISH_LIST_SHARED_ORDINAL = 4;

	/**
	 * Signals that a favorites has been shared.
	 */
	public static final CustomerEventType WISH_LIST_SHARED = new CustomerEventType(WISH_LIST_SHARED_ORDINAL, "WISH_LIST_SHARED");

	/**
	 * Create an enum value for a enum type. Name is case-insensitive and will be stored in upper-case.
	 * 
	 * @param ordinal the ordinal value
	 * @param name the name value (this will be converted to upper-case).
	 */
	protected CustomerEventType(final int ordinal, final String name) {
		super(ordinal, name, CustomerEventType.class);
	}

	@Override
	protected Class<CustomerEventType> getEnumType() {
		return CustomerEventType.class;
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
	public static CustomerEventType valueOf(final String name) {
		return valueOf(name, CustomerEventType.class);
	}

	/**
	 * CustomerEventType implementation of lookup interface.
	 */
	public static class CustomerEventTypeLookup implements EventTypeLookup<CustomerEventType> {

		@Override
		public CustomerEventType lookup(final String name) {
			try {
				return CustomerEventType.valueOf(name);
			} catch (IllegalArgumentException e) {
				return null;
			}
		}

	}

}
