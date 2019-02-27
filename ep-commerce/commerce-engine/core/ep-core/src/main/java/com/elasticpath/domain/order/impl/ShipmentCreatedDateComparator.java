/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.domain.order.impl;

import java.io.Serializable;
import java.util.Comparator;

import com.google.common.collect.Ordering;

import com.elasticpath.domain.order.OrderShipment;

/**
 * Compares two {@link com.elasticpath.domain.order.OrderShipment}s based on their created date.
 *
 * Null is considered less than non-null in the case of both the shipment itself as well as the created date field.
 */
public class ShipmentCreatedDateComparator implements Comparator<OrderShipment>, Serializable {

	private static final long serialVersionUID = 3324309923608063314L;

	@Override
	public int compare(final OrderShipment shipment1, final OrderShipment shipment2) {
		return Ordering.natural().nullsFirst()
			.onResultOf(OrderShipment::getCreatedDate).nullsFirst()
			.compare(shipment1, shipment2);
	}
}
