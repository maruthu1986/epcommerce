/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.event;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.PhysicalOrderShipment;

/**
 * The helper on the <code>OrderEvent</code>.
 * Help to generate the event details to track the order changes.
 *
 */
public interface OrderEventHelper {

	/**
	 * Log the event when order placed.
	 *
	 * @param order the new order
	 */
	void logOrderPlaced(Order order);

	/**
	 * Log the event when order canceled.
	 *
	 * @param order the order been canceled
	 */
	void logOrderCanceled(Order order);

	/**
	 * Log the event when order shipment released.
	 *
	 * @param order the order
	 * @param shipment the order shipment
	 */
	void logOrderShipmentReleased(Order order, OrderShipment shipment);

	/**
	 * Log the event when order shipment canceled.
	 *
	 * @param order the order
	 * @param shipment the order shipment
	 */
	void logOrderShipmentCanceled(Order order, OrderShipment shipment);

	/**
	 * Log the event when notes added.
	 *
	 * @param order the order
	 * @param note the note
	 */
	void logOrderNote(Order order, String note);

	/**
	 * Log the event when payment captured.
	 *
	 * @param order the order
	 * @param orderPayment the order payment
	 */
	void logOrderPaymentCaptured(Order order, OrderPayment orderPayment);

	/**
	 * Log the event when payment refund.
	 *
	 * @param order the order
	 * @param orderPayment the order payment
	 */
	void logOrderPaymentRefund(Order order, OrderPayment orderPayment);

	/**
	 * Log the event when new sku added.
	 *
	 * @param shipment the order shipment
	 * @param orderSku the sku be added
	 */
	void logOrderSkuAdded(OrderShipment shipment, OrderSku orderSku);

	/**
	 * Log the event when new sku removed.
	 *
	 * @param shipment the order shipment
	 * @param orderSku the sku be removed
	 */
	void logOrderSkuRemoved(OrderShipment shipment, OrderSku orderSku);

	/**
	 * Log the event when sku moved to other shipments.
	 *
	 * @param shipment the shipment
	 * @param orderSku the sku be moved
	 */
	void logOrderSkuMoved(OrderShipment shipment, OrderSku orderSku);

	/**
	 * Log the event when sku quantity changed.
	 *
	 * @param shipment the order shipment
	 * @param orderSku the sku be changed
	 * @param quantity quantity changed on the order sku.
	 */
	void logOrderSkuQuantityChanged(OrderShipment shipment, OrderSku orderSku, int quantity);

	/**
	 * Log the event when shipping method changed.
	 *
	 * @param order the order
	 * @param shipment the shipment.
	 */
	void logOrderShipmentMethodChanged(Order order, PhysicalOrderShipment shipment);

	/**
	 * Log the event when shipping address changed.
	 *
	 * @param order the order
	 * @param shipment the shipment.
	 */
	void logOrderShipmentAddressChanged(Order order, PhysicalOrderShipment shipment);

	/**
	 * Log the event when order is put on hold.
	 *
	 * @param order the order
	 */
	void logOrderOnHold(Order order);

	/**
	 * Log the event when order hold is released.
	 *
	 * @param order the order
	 */
	void logOrderHoldReleased(Order order);

	/**
	 * Log the event when the order is released for fulfilment.
	 *
	 * @param order the order
	 */
	void logOrderReleasedForFulfilment(Order order);

	/**
	 * Log the event when order return is created.
	 *
	 * @param order the order
	 * @param orderReturn the order return
	 */
	void logOrderReturnCreated(Order order, OrderReturn orderReturn);

	/**
	 * Log the event when order exchange is created.
	 *
	 * @param order the order
	 * @param orderExchange the order return
	 */
	void logOrderExchangeCreated(Order order, OrderReturn orderExchange);

	/**
	 * Log the event when receive the return item.
	 *
	 * @param order the order
	 * @param orderReturn the order return
	 */
	void logOrderReturnReceived(Order order, OrderReturn orderReturn);

	/**
	 * Log the event when the order return is changed.
	 *
	 * @param order the order
	 * @param orderReturn the order return
	 */
	void logOrderReturnChanged(Order order, OrderReturn orderReturn);

	/**
	 * Log the event when the order return is canceled.
	 *
	 * @param order the order
	 * @param orderReturn the order return
	 */
	void logOrderReturnCanceled(Order order, OrderReturn orderReturn);

	/**
	 * Log the event when the order return is completed.
	 *
	 * @param order the order
	 * @param orderReturn the order return
	 */
	void logOrderReturnCompleted(Order order, OrderReturn orderReturn);

	/**
	 * Log the event when exchange order is created.
	 *
	 * @param order the exchange order
	 */
	void logOrderExchangeCreated(Order order);

	/**
	 * Log the event when the exchange order is canceled.
	 *
	 * @param order the exchange order
	 * @param orderExchange the orderExchange
	 */
	void logOrderExchangeCanceled(Order order, OrderReturn orderExchange);

	/**
	 * Log the event when the exchange order is completed.
	 *
	 * @param order the exchange order
	 * @param orderExchange the orderExchange
	 */
	void logOrderExchangeCompleted(Order order, OrderReturn orderExchange);

}
