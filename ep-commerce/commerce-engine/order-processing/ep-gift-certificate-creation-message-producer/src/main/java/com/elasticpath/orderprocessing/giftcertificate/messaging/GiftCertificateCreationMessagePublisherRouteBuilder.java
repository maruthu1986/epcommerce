/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.orderprocessing.giftcertificate.messaging;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.language.Simple;
import org.apache.camel.spi.DataFormat;

import com.elasticpath.core.messaging.giftcertificate.GiftCertificateEventType;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.messaging.EventMessage;
import com.elasticpath.messaging.EventMessagePredicate;
import com.elasticpath.messaging.EventMessagePublisher;
import com.elasticpath.messaging.factory.EventMessageFactory;
import com.elasticpath.service.order.OrderService;

/**
 * Route that responds to an {@code ORDER_SHIPMENT_CREATED} event message and creates a {@code GIFT_CERTIFICATE_CREATED} message per Gift Certificate.
 */
public class GiftCertificateCreationMessagePublisherRouteBuilder extends RouteBuilder {

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
				.log(LoggingLevel.DEBUG, getClass().getName(), "Preparing GIFT_CERTIFICATE_CREATED event messages for Order Shipment [${body.guid}]")

				.bean(getOrderShipmentRetriever())

				.split().simple("${body.shipmentOrderSkus}")
					.filter().simple("${body.getFieldValue('" + GiftCertificate.KEY_GUID + "')} != null")

						.bean(getGiftCertificateCreatedEventMessageFactory(), "createEventMessage")
						.log(LoggingLevel.DEBUG, getClass().getName(), "Publishing GIFT_CERTIFICATE_CREATED event message ${body}")
						.bean(getEventMessagePublisher());
	}

	/**
	 * Factory method that returns a new {@link OrderShipmentRetriever} instance.
	 *
	 * @return a new {@link OrderShipmentRetriever} instance.
	 */
	protected Object getOrderShipmentRetriever() {
		return new OrderShipmentRetriever();
	}

	/**
	 * Retrieves Order Shipments by Shipment Number and Shipment Type.
	 */
	protected class OrderShipmentRetriever {

		/**
		 * Retrieves an Order Shipment by Shipment Number and Shipment Type.
		 * 
		 * @param shipmentNumber the shipment number
		 * @param shipmentType the shipment type
		 * @return an Order Shipment
		 */
		public OrderShipment findOrderShipment(
				@Simple("${body.guid}") final String shipmentNumber,
				@Simple("${body.data[shipmentType]}") final String shipmentType) {
			return getOrderService().findOrderShipment(shipmentNumber, ShipmentType.valueOf(shipmentType));
		}
	}

	/**
	 * Factory method that returns a new GiftCertificateCreatedEventMessageFactory instance.
	 *
	 * @return a new OrderShipmentCreatedEventMessageFactory instance
	 */
	protected Object getGiftCertificateCreatedEventMessageFactory() {
		return new GiftCertificateCreatedEventMessageFactory();
	}

	/**
	 * Creates Event Messages with event type {@link GiftCertificateEventType#GIFT_CERTIFICATE_CREATED}.
	 */
	protected class GiftCertificateCreatedEventMessageFactory {
		/**
		 * Creates a new Event Message with event type {@link GiftCertificateEventType#GIFT_CERTIFICATE_CREATED}.
		 *
		 * @param giftCertificateGuid the Gift Certificate GUID
		 * @param orderGuid the Order guid
		 * @param shipmentNumber the shipment number
		 * @param shipmentType the shipment type
		 * @param orderSkuGuid the Order SKU GUID
		 * @return a new {@link com.elasticpath.messaging.EventMessage}
		 */
		public EventMessage createEventMessage(
				@Simple("${body.getFieldValue('" + GiftCertificate.KEY_GUID + "')}") final String giftCertificateGuid,
				@Simple("${body.shipment.order.guid}") final String orderGuid,
				@Simple("${body.shipment.shipmentNumber}") final String shipmentNumber,
				@Simple("${body.shipment.orderShipmentType}") final ShipmentType shipmentType,
				@Simple("${body.guid}") final String orderSkuGuid) {
			final Map<String, Object> data = new HashMap<>(4);
			data.put("orderGuid", orderGuid);
			data.put("shipmentNumber", shipmentNumber);
			data.put("shipmentType", shipmentType.toString());
			data.put("orderSkuGuid", orderSkuGuid);

			return getEventMessageFactory().createEventMessage(GiftCertificateEventType.GIFT_CERTIFICATE_CREATED, giftCertificateGuid, data);
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

