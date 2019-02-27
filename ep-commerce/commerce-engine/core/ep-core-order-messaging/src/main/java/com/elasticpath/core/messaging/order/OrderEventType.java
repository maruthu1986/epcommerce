/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.core.messaging.order;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;
import com.elasticpath.messaging.EventType;
import com.elasticpath.messaging.spi.EventTypeLookup;

/**
 * An enum representing order-based {@link EventType}s that are available in the platform.
 */
public class OrderEventType extends AbstractExtensibleEnum<OrderEventType> implements EventType {

	private static final long serialVersionUID = 4025398581967246417L;

	/** Ordinal constant for ORDER_CREATED. */
	public static final int ORDER_CREATED_ORDINAL = 0;

	/**
	 * Signals that an order has been created.
	 */
	public static final OrderEventType ORDER_CREATED = new OrderEventType(ORDER_CREATED_ORDINAL, "ORDER_CREATED");

	/** Ordinal constant for ORDER_SHIPMENT_CREATED. */
	public static final int ORDER_SHIPMENT_CREATED_ORDINAL = 1;

	/**
	 * Signals that an order shipment has been created.
	 */
	public static final OrderEventType ORDER_SHIPMENT_CREATED = new OrderEventType(ORDER_SHIPMENT_CREATED_ORDINAL, "ORDER_SHIPMENT_CREATED");

	/** Ordinal constant for ORDER_SHIPMENT_SHIPPED. */
	public static final int ORDER_SHIPMENT_SHIPPED_ORDINAL = 2;

	/**
	 * Signals that an order shipment has been shipped.
	 */
	public static final OrderEventType ORDER_SHIPMENT_SHIPPED = new OrderEventType(ORDER_SHIPMENT_SHIPPED_ORDINAL, "ORDER_SHIPMENT_SHIPPED");

	/** Ordinal constant for RESEND_ORDER_CONFIRMATION. */
	public static final int RESEND_ORDER_CONFIRMATION_ORDINAL = 3;

	/**
	 * Signals a request for the Order Confirmation to be re-sent.
	 */
	public static final OrderEventType RESEND_ORDER_CONFIRMATION = new OrderEventType(RESEND_ORDER_CONFIRMATION_ORDINAL, "RESEND_ORDER_CONFIRMATION");

	/** Ordinal constant for ORDER_SHIPMENT_RELEASE_FAILED. */
	public static final int ORDER_SHIPMENT_RELEASE_FAILED_ORDINAL = 4;

	/**
	 * Signals that an order shipment release has failed.
	 */
	public static final OrderEventType ORDER_SHIPMENT_RELEASE_FAILED = new OrderEventType(ORDER_SHIPMENT_RELEASE_FAILED_ORDINAL,
			"ORDER_SHIPMENT_RELEASE_FAILED");

	/** Ordinal constant for RETURN_CREATED. */
	public static final int RETURN_CREATED_ORDINAL = 5;

	/**
	 * Signals that a return has been created.
	 */
	public static final OrderEventType RETURN_CREATED = new OrderEventType(RETURN_CREATED_ORDINAL, "RETURN_CREATED");

	/** Ordinal constant for EXCHANGE_CREATED. */
	public static final int EXCHANGE_CREATED_ORDINAL = 6;

	/**
	 * Signals that an exchange has been created.
	 */
	public static final OrderEventType EXCHANGE_CREATED = new OrderEventType(EXCHANGE_CREATED_ORDINAL, "EXCHANGE_CREATED");

	/** Ordinal constant for RESEND_RETURN_EXCHANGE_NOTIFICATION. */
	public static final int RESEND_RETURN_EXCHANGE_NOTIFICATION_ORDINAL = 7;

	/**
	 * Signals that the Return/Exchange Notification should be resent.
	 */
	public static final OrderEventType RESEND_RETURN_EXCHANGE_NOTIFICATION = new OrderEventType(RESEND_RETURN_EXCHANGE_NOTIFICATION_ORDINAL,
			"RESEND_RETURN_EXCHANGE_NOTIFICATION");

	/** Ordinal constant for ORDER_HELD. */
	public static final int ORDER_HELD_ORDINAL = 8;

	/**
	 * Signals that an order has been held.
	 */
	public static final OrderEventType ORDER_HELD = new OrderEventType(ORDER_HELD_ORDINAL, "ORDER_HELD");

	/** Ordinal constant for ORDER_CANCELLED. */
	public static final int ORDER_CANCELLED_ORDINAL = 9;

	/**
	 * Signals that an order has been cancelled.
	 */
	public static final OrderEventType ORDER_CANCELLED = new OrderEventType(ORDER_CANCELLED_ORDINAL, "ORDER_CANCELLED");

	/** Ordinal constant for ORDER_RELEASED. */
	public static final int ORDER_RELEASED_ORDINAL = 10;

	/**
	 * Signals that an order has been released for fulfilment.
	 */
	public static final OrderEventType ORDER_RELEASED = new OrderEventType(ORDER_RELEASED_ORDINAL, "ORDER_RELEASED");

	/** Ordinal constant for ORDER_COMPLETED. */
	public static final int ORDER_COMPLETED_ORDINAL = 11;

	/**
	 * Signals that all order processing and fulfilment has completed.
	 */
	public static final OrderEventType ORDER_COMPLETED = new OrderEventType(ORDER_COMPLETED_ORDINAL, "ORDER_COMPLETED");

	/**
	 * Create an enum value for a enum type. Name is case-insensitive and will be stored in upper-case.
	 * 
	 * @param ordinal the ordinal value
	 * @param name the name value (this will be converted to upper-case).
	 */
	protected OrderEventType(final int ordinal, final String name) {
		super(ordinal, name, OrderEventType.class);
	}

	@Override
	protected Class<OrderEventType> getEnumType() {
		return OrderEventType.class;
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
	public static OrderEventType valueOf(final String name) {
		return valueOf(name, OrderEventType.class);
	}

	/**
	 * OrderEventType implementation of lookup interface.
	 */
	public static class OrderEventTypeLookup implements EventTypeLookup<OrderEventType> {

		@Override
		public OrderEventType lookup(final String name) {
			try {
				return OrderEventType.valueOf(name);
			} catch (IllegalArgumentException e) {
				return null;
			}
		}

	}

}
