/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.orderprocessing.ordershipment.messaging;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.spi.DataFormat;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.core.messaging.order.OrderEventType;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.messaging.EventMessage;
import com.elasticpath.messaging.EventMessagePredicate;
import com.elasticpath.messaging.EventMessagePublisher;
import com.elasticpath.messaging.camel.test.support.TransactionPolicyRegistryManager;
import com.elasticpath.messaging.factory.EventMessageFactory;
import com.elasticpath.messaging.impl.EventMessageImpl;
import com.elasticpath.service.order.OrderService;

/**
 * Test class for {@link OrderShipmentCreationMessagePublisherRouteBuilder}.
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases") // TODO remove this suppression; the PMD rule should be revoked
public class OrderShipmentCreationMessagePublisherRouteBuilderTest extends CamelTestSupport {

	private static final long SECONDS_TO_WAIT = 2L;
	private static final String ROUTE_ID = "TEST_ROUTE_ID";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private Endpoint incomingEndpoint;

	private final DataFormat eventMessageDataFormat = context.mock(DataFormat.class, "event message data format");
	private final EventMessageFactory eventMessageFactory = context.mock(EventMessageFactory.class);
	private final EventMessagePredicate eventMessagePredicateFilter = context.mock(EventMessagePredicate.class);
	private final EventMessagePublisher eventMessagePublisher = context.mock(EventMessagePublisher.class);
	private final OrderService orderService = context.mock(OrderService.class);

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();

		incomingEndpoint = getMandatoryEndpoint("direct:incoming");

		final OrderShipmentCreationMessagePublisherRouteBuilder routeBuilder = new OrderShipmentCreationMessagePublisherRouteBuilder();
		routeBuilder.setRouteId(ROUTE_ID);
		routeBuilder.setIncomingEndpoint(incomingEndpoint);
		routeBuilder.setOrderService(orderService);
		routeBuilder.setEventMessageDataFormat(eventMessageDataFormat);
		routeBuilder.setEventMessageFactory(eventMessageFactory);
		routeBuilder.setEventMessagePredicateFilter(eventMessagePredicateFilter);
		routeBuilder.setEventMessagePublisher(eventMessagePublisher);

		context().addRoutes(routeBuilder);
	}

	@Override
	protected JndiRegistry createRegistry() throws Exception {
		final JndiRegistry registry = super.createRegistry();

		new TransactionPolicyRegistryManager(registry).registerDefaultTransactionPolicy();

		return registry;
	}

	@Test
	public void verifyNoMessagesProducedWhenIncomingMessageIsOfIncompatibleType() throws Exception {
		final String orderGuid = "12345";
		final String orderCreatedJsonMessage = "{ \"guid\":\"" + orderGuid + "\", \"eventType\":\"INCOMPATIBLE_TYPE\" }";

		final EventMessage inputEventMessage = givenInboundJsonMessageIsUnmarshalledToEventMessage(orderGuid);

		context.checking(new Expectations() {
			{
				oneOf(eventMessagePredicateFilter).apply(inputEventMessage);
				will(returnValue(false));

				never(eventMessagePublisher).publish(with(any(EventMessage.class)));
			}
		});

		whenOrderCreatedMessageIsSent(orderCreatedJsonMessage);
	}

	@Test
	public void verifyMessageIsCreatedForEachOrderShipment() throws Exception {
		final String orderGuid = "12345";

		final String orderCreatedJsonMessage = "{ \"guid\":\"" + orderGuid + "\", \"eventType\":\"ORDER_CREATED\" }";

		final EventMessage incomingEventMessage = givenInboundJsonMessageIsUnmarshalledToEventMessage(orderGuid);
		givenEventMessageIsOfCompatibleType(incomingEventMessage);

		final Order order = givenFindOrderByGuidReturnsOrder(orderGuid);

		final OrderShipment orderShipment1 = createOrderShipment(ShipmentType.PHYSICAL);
		final OrderShipment orderShipment2 = createOrderShipment(ShipmentType.ELECTRONIC);
		givenOrderContainsOrderShipments(order, orderShipment1, orderShipment2);

		final EventMessage shipmentCreatedEventMessage1 = givenOrderShipmentIsConvertedToEventMessage(orderShipment1);
		final EventMessage shipmentCreatedEventMessage2 = givenOrderShipmentIsConvertedToEventMessage(orderShipment2);

		// Then
		context.checking(new Expectations() {
			{
				oneOf(eventMessagePublisher).publish(shipmentCreatedEventMessage1);
				oneOf(eventMessagePublisher).publish(shipmentCreatedEventMessage2);
			}
		});

		whenOrderCreatedMessageIsSent(orderCreatedJsonMessage);
	}

	private Order givenFindOrderByGuidReturnsOrder(final String orderGuid) {
		final Order order = context.mock(Order.class);

		context.checking(new Expectations() {
			{
				allowing(orderService).findOrderByOrderNumber(orderGuid);
				will(returnValue(order));

				allowing(order).getGuid();
				will(returnValue(orderGuid));

				allowing(order).getOrderNumber();
				will(returnValue(orderGuid));
			}
		});

		return order;
	}

	private OrderShipment createOrderShipment(final ShipmentType shipmentType) {
		final String shipmentNumber = "orderShipment-" + shipmentType;
		final OrderShipment orderShipment = context.mock(OrderShipment.class, shipmentNumber);

		context.checking(new Expectations() {
			{
				allowing(orderShipment).getOrderShipmentType();
				will(returnValue(shipmentType));

				allowing(orderShipment).getShipmentNumber();
				will(returnValue(shipmentNumber));
			}
		});

		return orderShipment;
	}

	private void givenOrderContainsOrderShipments(final Order order, final OrderShipment... orderShipments) {
		context.checking(new Expectations() {
			{
				allowing(order).getAllShipments();
				will(returnValue(Arrays.asList(orderShipments)));

				for (final OrderShipment orderShipment : orderShipments) {
					allowing(orderShipment).getOrder();
					will(returnValue(order));
				}
			}
		});
	}

	private EventMessage givenOrderShipmentIsConvertedToEventMessage(final OrderShipment orderShipment) {
		final Map<String, Object> data = new HashMap<>();
		data.put("orderGuid", orderShipment.getOrder().getGuid());
		data.put("shipmentType", orderShipment.getOrderShipmentType().toString());

		final EventMessage eventMessage = new EventMessageImpl(OrderEventType.ORDER_SHIPMENT_CREATED, orderShipment.getShipmentNumber(), data);

		context.checking(new Expectations() {
			{
				allowing(eventMessageFactory).createEventMessage(OrderEventType.ORDER_SHIPMENT_CREATED, orderShipment.getShipmentNumber(), data);
				will(returnValue(eventMessage));
			}
		});

		return eventMessage;
	}

	private EventMessage givenInboundJsonMessageIsUnmarshalledToEventMessage(final String orderGuid) throws Exception {
		final EventMessage eventMessage = new EventMessageImpl(OrderEventType.ORDER_CREATED, orderGuid);

		context.checking(new Expectations() {
			{
				allowing(eventMessageDataFormat).unmarshal(with(any(Exchange.class)), with(any(InputStream.class)));
				will(returnValue(eventMessage));
			}
		});

		return eventMessage;
	}

	private void givenEventMessageIsOfCompatibleType(final EventMessage eventMessage) {
		context.checking(new Expectations() {
			{
				allowing(eventMessagePredicateFilter).apply(eventMessage);
				will(returnValue(true));
			}
		});
	}

	private void whenOrderCreatedMessageIsSent(final String orderCreatedJsonMessage) {
		final NotifyBuilder notifyBuilder = new NotifyBuilder(context())
				.from(incomingEndpoint.getEndpointUri())
				.whenDone(1)
				.create();

		template().sendBody(incomingEndpoint, orderCreatedJsonMessage);

		assertTrue("Did not receive message(s) on outgoing endpoint within " + SECONDS_TO_WAIT + " seconds",
				   notifyBuilder.matches(SECONDS_TO_WAIT, TimeUnit.SECONDS));
	}

}