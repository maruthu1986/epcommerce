/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.orderprocessing.ordershipment.messaging;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.language.Simple;
import org.apache.camel.spi.DataFormat;

import com.elasticpath.core.messaging.order.OrderEventType;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.messaging.EventMessage;
import com.elasticpath.messaging.EventMessagePredicate;
import com.elasticpath.messaging.EventMessagePublisher;
import com.elasticpath.messaging.factory.EventMessageFactory;
import com.elasticpath.service.order.OrderService;

/**
 * Route that responds to an ORDER_CREATED event message and creates an ORDER_SHIPMENT_CREATED message per shipment.
 */
public class OrderShipmentCreationMessagePublisherRouteBuilder extends RouteBuilder {

	private Endpoint incomingEndpoint;

	private String transactionPropagationRef;

	private EventMessagePredicate eventMessagePredicateFilter;

	private OrderService orderService;

	private DataFormat eventMessageDataFormat;

	private EventMessageFactory eventMessageFactory;

	private EventMessagePublisher eventMessagePublisher;

	private String routeId;

	@Override
	public void configure() throws Exception {
		from(getIncomingEndpoint())
				.routeId(getRouteId())
				.transacted(getTransactionPropagationRef())
				.log(LoggingLevel.DEBUG, getClass().getName(), "Received message ${body}")

				.unmarshal(getEventMessageDataFormat())

				.filter().method(getEventMessagePredicateFilter())
					.log(LoggingLevel.DEBUG, getClass().getName(), "Preparing ORDER_SHIPMENT_CREATED event messages for Order [${body.guid}]")

					.bean(getOrderService(), "findOrderByOrderNumber(${body.guid})")

					.split().simple("${body.allShipments}")
						.log(LoggingLevel.DEBUG, getClass().getName(),
							"Preparing ORDER_SHIPMENT_CREATED event message for Shipment [${body.shipmentNumber}]")

						.bean(getOrderShipmentCreatedEventMessageFactory(), "createEventMessage")
						.bean(getEventMessagePublisher());
	}

	/**
	 * Factory method that returns a new OrderShipmentCreatedEventMessageFactory instance.
	 * 
	 * @return a new OrderShipmentCreatedEventMessageFactory instance
	 */
	protected Object getOrderShipmentCreatedEventMessageFactory() {
		return new OrderShipmentCreatedEventMessageFactory();
	}

	/**
	 * Creates a new Event Message with event type {@link OrderEventType#ORDER_SHIPMENT_CREATED}.
	 */
	protected class OrderShipmentCreatedEventMessageFactory {

		/**
		 * Creates a new Event Message with event type {@link OrderEventType#ORDER_SHIPMENT_CREATED}.
		 *
		 * @param shipmentNumber the shipment number
		 * @param shipmentType the shipment type
		 * @param orderGuid the order guid
		 * @return a new {@link EventMessage}
		 */
		public EventMessage createEventMessage(
				@Simple("${body.shipmentNumber}") final String shipmentNumber,
				@Simple("${body.orderShipmentType}") final ShipmentType shipmentType,
				@Simple("${body.order.guid}") final String orderGuid) {
			final Map<String, Object> data = new HashMap<>(2);
			data.put("orderGuid", orderGuid);
			data.put("shipmentType", shipmentType.toString());

			return getEventMessageFactory().createEventMessage(OrderEventType.ORDER_SHIPMENT_CREATED, shipmentNumber, data);
		}

	}

	public void setIncomingEndpoint(final Endpoint incomingEndpoint) {
		this.incomingEndpoint = incomingEndpoint;
	}

	protected Endpoint getIncomingEndpoint() {
		return incomingEndpoint;
	}

	/**
	 * Sets the registry reference ID (Spring bean name) of the transaction policy to use in this route. May be {@code null} if the default
	 * transaction (PROPAGATION_REQUIRED) is sufficient.
	 * 
	 * @param transactionPropagationRef the reference to the transaction policy bean, or {@code null} to use the default transaction policy
	 */
	public void setTransactionPropagationRef(final String transactionPropagationRef) {
		this.transactionPropagationRef = transactionPropagationRef;
	}

	protected String getTransactionPropagationRef() {
		return transactionPropagationRef;
	}

	public void setEventMessagePredicateFilter(final EventMessagePredicate eventMessagePredicateFilter) {
		this.eventMessagePredicateFilter = eventMessagePredicateFilter;
	}

	protected EventMessagePredicate getEventMessagePredicateFilter() {
		return eventMessagePredicateFilter;
	}

	public void setOrderService(final OrderService orderService) {
		this.orderService = orderService;
	}

	protected OrderService getOrderService() {
		return orderService;
	}

	public void setEventMessageDataFormat(final DataFormat eventMessageDataFormat) {
		this.eventMessageDataFormat = eventMessageDataFormat;
	}

	protected DataFormat getEventMessageDataFormat() {
		return eventMessageDataFormat;
	}

	public void setEventMessageFactory(final EventMessageFactory eventMessageFactory) {
		this.eventMessageFactory = eventMessageFactory;
	}

	protected EventMessageFactory getEventMessageFactory() {
		return eventMessageFactory;
	}

	public void setEventMessagePublisher(final EventMessagePublisher eventMessagePublisher) {
		this.eventMessagePublisher = eventMessagePublisher;
	}

	protected EventMessagePublisher getEventMessagePublisher() {
		return eventMessagePublisher;
	}

	protected String getRouteId() {
		return routeId;
	}

	public void setRouteId(final String routeId) {
		this.routeId = routeId;
	}

}
