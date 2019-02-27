/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.orderprocessing.giftcertificate.messaging;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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

import com.elasticpath.core.messaging.giftcertificate.GiftCertificateEventType;
import com.elasticpath.core.messaging.order.OrderEventType;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.impl.OrderSkuImpl;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.messaging.EventMessage;
import com.elasticpath.messaging.EventMessagePredicate;
import com.elasticpath.messaging.EventMessagePublisher;
import com.elasticpath.messaging.EventType;
import com.elasticpath.messaging.camel.test.support.TransactionPolicyRegistryManager;
import com.elasticpath.messaging.factory.EventMessageFactory;
import com.elasticpath.messaging.impl.EventMessageImpl;
import com.elasticpath.service.order.OrderService;

/**
 * Test class for {@link com.elasticpath.orderprocessing.giftcertificate.messaging.GiftCertificateCreationMessagePublisherRouteBuilder}.
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")//PMD warning doesn't make sense
public class GiftCertificateCreationMessagePublisherRouteBuilderTest extends CamelTestSupport {

	private static final long SECONDS_TO_WAIT = 2L;
	private static final String ROUTE_ID = "TEST_ROUTE_ID";

	private static final String ORDER_SHIPMENT_NUMBER = "12345-1";
	private static final ShipmentType SHIPMENT_TYPE = ShipmentType.ELECTRONIC;
	private static final String ORDER_GUID = "12345";

	private static final String ORDER_SHIPMENT_CREATED_JSON_MESSAGE = "{ \"guid\":\"" + ORDER_SHIPMENT_NUMBER
			+ "\", \"eventType\":\"ORDER_SHIPMENT_CREATED\", \"data\":{\"shipmentType\":\"" + SHIPMENT_TYPE + "\"} }";

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

		final GiftCertificateCreationMessagePublisherRouteBuilder routeBuilder = new GiftCertificateCreationMessagePublisherRouteBuilder();
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
	public void verifyNoMessagesProducedWhenOrderShipmentContainsNoGiftCertificates() throws Exception {
		final EventMessage inputEventMessage = givenInboundJsonMessageIsUnmarshalledToEventMessage(ORDER_SHIPMENT_NUMBER, SHIPMENT_TYPE.toString(),
																								   ORDER_GUID);

		givenEventMessageIsOfCompatibleType(inputEventMessage);

		final OrderShipment orderShipment = createMockOrderShipment();
		givenOrderServiceFindsOrderShipmentForShipmentNumber(ORDER_SHIPMENT_NUMBER, SHIPMENT_TYPE, orderShipment);

		final OrderSku nonGiftCertificateSku = new OrderSkuImpl();
		nonGiftCertificateSku.setFieldValue(GiftCertificate.KEY_GUID, null);

		givenOrderShipmentContainsSkus(orderShipment, nonGiftCertificateSku);

		context.checking(new Expectations() {
			{
				never(eventMessagePublisher).publish(with(any(EventMessage.class)));
			}
		});

		whenOrderShipmentCreatedMessageIsSent(ORDER_SHIPMENT_CREATED_JSON_MESSAGE);
	}

	@Test
	public void verifyNoMessagesProducedWhenIncomingMessageIsOfIncompatibleType() throws Exception {
		final String orderCreatedJsonMessage = "{ \"guid\":\"" + ORDER_SHIPMENT_NUMBER + "\", \"eventType\":\"INCOMPATIBLE_TYPE\" }";

		final EventMessage inputEventMessage = givenInboundJsonMessageIsUnmarshalledToEventMessage(ORDER_SHIPMENT_NUMBER, SHIPMENT_TYPE.toString(),
																								   null);

		context.checking(new Expectations() {
			{
				oneOf(eventMessagePredicateFilter).apply(inputEventMessage);
				will(returnValue(false));

				never(eventMessagePublisher).publish(with(any(EventMessage.class)));
			}
		});

		whenOrderShipmentCreatedMessageIsSent(orderCreatedJsonMessage);
	}

	@Test
	public void verifyMessageIsCreatedForEachGiftCertificate() throws Exception {
		final EventMessage inputEventMessage = givenInboundJsonMessageIsUnmarshalledToEventMessage(ORDER_SHIPMENT_NUMBER,
																								   SHIPMENT_TYPE.toString(),
																								   null);

		givenEventMessageIsOfCompatibleType(inputEventMessage);

		final OrderShipment orderShipment = createMockOrderShipment();
		givenOrderServiceFindsOrderShipmentForShipmentNumber(ORDER_SHIPMENT_NUMBER, SHIPMENT_TYPE, orderShipment);

		final String skuGuid1 = "GiftCertSku-1";
		final String giftCertGuid1 = "GIFTCERT001";
		final String skuGuid2 = "GiftCertSku-2";
		final String giftCertGuid2 = "GIFTCERT002";

		final OrderSku giftCertificateSku1 = createOrderSku(skuGuid1);
		final OrderSku giftCertificateSku2 = createOrderSku(skuGuid2);
		final OrderSku nonGiftCertificateSku = createOrderSku("nonGiftCertSku");

		giftCertificateSku1.setFieldValue(GiftCertificate.KEY_GUID, giftCertGuid1);
		giftCertificateSku2.setFieldValue(GiftCertificate.KEY_GUID, giftCertGuid2);

		givenOrderShipmentContainsSkus(orderShipment, giftCertificateSku1, giftCertificateSku2, nonGiftCertificateSku);

		final Map<String, Object> data1 = createGiftCertificateCreatedEventMessageDataMap(ORDER_GUID, ORDER_SHIPMENT_NUMBER,
																						  SHIPMENT_TYPE.toString(), skuGuid1);
		final Map<String, Object> data2 = createGiftCertificateCreatedEventMessageDataMap(ORDER_GUID, ORDER_SHIPMENT_NUMBER,
																						  SHIPMENT_TYPE.toString(), skuGuid2);

		final EventMessage giftCertificateCreatedEventMessage1 = expectEventMessageFactoryCreatesEventMessage(
				GiftCertificateEventType.GIFT_CERTIFICATE_CREATED, giftCertGuid1, data1);
		final EventMessage giftCertificateCreatedEventMessage2 = expectEventMessageFactoryCreatesEventMessage(
				GiftCertificateEventType.GIFT_CERTIFICATE_CREATED, giftCertGuid2, data2);

		// Then
		context.checking(new Expectations() {
			{
				oneOf(eventMessagePublisher).publish(giftCertificateCreatedEventMessage1);
				oneOf(eventMessagePublisher).publish(giftCertificateCreatedEventMessage2);
			}
		});

		whenOrderShipmentCreatedMessageIsSent(ORDER_SHIPMENT_CREATED_JSON_MESSAGE);
	}

	private Map<String, Object> createGiftCertificateCreatedEventMessageDataMap(final String orderGuid, final String
			orderShipmentNumber, final String orderShipmentType, final String orderSkuGuid) {
		final Map<String, Object> data1 = new HashMap<>(4);
		data1.put("orderGuid", orderGuid);
		data1.put("shipmentNumber", orderShipmentNumber);
		data1.put("shipmentType", orderShipmentType);
		data1.put("orderSkuGuid", orderSkuGuid);
		return data1;
	}

	private EventMessage givenInboundJsonMessageIsUnmarshalledToEventMessage(final String orderShipmentNumber,
																			 final String shipmentType, final String orderGuid) throws Exception {
		final Map<String, Object> data = new HashMap<>(2);
		data.put("shipmentType", shipmentType);
		data.put("orderGuid", orderGuid);

		final EventMessage eventMessage = new EventMessageImpl(OrderEventType.ORDER_SHIPMENT_CREATED, orderShipmentNumber, data);

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

	private OrderShipment createMockOrderShipment() {
		final OrderShipment orderShipment = context.mock(OrderShipment.class);
		context.checking(new Expectations() {
			{
				final Order order = context.mock(Order.class);

				allowing(orderShipment).getShipmentNumber();
				will(returnValue(ORDER_SHIPMENT_NUMBER));

				allowing(orderShipment).getOrderShipmentType();
				will(returnValue(SHIPMENT_TYPE));

				allowing(orderShipment).getOrder();
				will(returnValue(order));

				allowing(order).getGuid();
				will(returnValue(ORDER_GUID));
			}
		});

		return orderShipment;
	}

	private void givenOrderServiceFindsOrderShipmentForShipmentNumber(final String orderShipmentNumber, final ShipmentType shipmentType,
																	  final OrderShipment orderShipment) {
		context.checking(new Expectations() {
			{
				allowing(orderService).findOrderShipment(orderShipmentNumber, shipmentType);
				will(returnValue(orderShipment));
			}
		});
	}

	private OrderSku createOrderSku(final String orderSkuGuid) {
		final OrderSku giftCertificateOrderSku = new OrderSkuImpl();
		giftCertificateOrderSku.setGuid(orderSkuGuid);
		return giftCertificateOrderSku;
	}

	private void givenOrderShipmentContainsSkus(final OrderShipment orderShipment, final OrderSku... orderSkus) {
		context.checking(new Expectations() {
			{
				allowing(orderShipment).getShipmentOrderSkus();
				will(returnValue(new HashSet<>(Arrays.asList(orderSkus))));
			}
		});

		for (final OrderSku orderSku : orderSkus) {
			orderSku.setShipment(orderShipment);
		}
	}

	private EventMessage expectEventMessageFactoryCreatesEventMessage(final EventType eventType, final String guid, final Map<String, Object> data) {
		final EventMessage eventMessage = new EventMessageImpl(eventType, guid, data);

		context.checking(new Expectations() {
			{
				oneOf(eventMessageFactory).createEventMessage(eventType, guid, data);
				will(returnValue(eventMessage));
			}
		});

		return eventMessage;
	}

	private void whenOrderShipmentCreatedMessageIsSent(final String orderShipmentCreatedJsonMessage) {
		final NotifyBuilder notifyBuilder = new NotifyBuilder(context())
				.from(incomingEndpoint.getEndpointUri())
				.whenDone(1)
				.create();

		template().sendBody(incomingEndpoint, orderShipmentCreatedJsonMessage);

		assertTrue("Did not receive message(s) on outgoing endpoint within " + SECONDS_TO_WAIT + " seconds",
				   notifyBuilder.matches(SECONDS_TO_WAIT, TimeUnit.SECONDS));
	}

}
