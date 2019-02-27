/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */

package com.elasticpath.domain.event.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.catalog.impl.GiftCertificateImpl;
import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.domain.customer.impl.PaymentTokenImpl;
import com.elasticpath.domain.event.EventOriginator;
import com.elasticpath.domain.event.OrderEventPaymentDetailFormatter;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderEvent;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.order.impl.OrderEventImpl;
import com.elasticpath.domain.order.impl.OrderImpl;
import com.elasticpath.domain.order.impl.OrderPaymentImpl;
import com.elasticpath.domain.order.impl.PhysicalOrderShipmentImpl;
import com.elasticpath.money.Money;
import com.elasticpath.money.MoneyFormatter;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test for {@link OrderEventHelperImpl}.
 */
@SuppressWarnings({ "PMD.TooManyMethods" })
public class OrderEventHelperImplTest {
	private static final String TEST_FORMATTED_AMOUNT = "formatted amount";

	private static final String TEST_TOKEN_DISPLAY_VALUE = "testTokenDisplayValue";

	private static final String TEST_GIFT_CERTIFICATE_CODE = "testGiftCertificateCode";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private OrderEventHelperImpl orderEventHelper;
	private final BeanFactory beanFactory = context.mock(BeanFactory.class);
	private final MoneyFormatter moneyFormatter = context.mock(MoneyFormatter.class);
	private final TimeService timeService = context.mock(TimeService.class);
	private final PricingSnapshotService pricingSnapshotService = context.mock(PricingSnapshotService.class);
	private final Order order = context.mock(Order.class);
	private OrderEvent orderEvent;
	private final EventOriginator scapegoat = new EventOriginatorImpl();

	/**
	 * Sets up mock bean factory, sets up OrderEventHelper with mock services, and mocks an Order for testing.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		orderEvent = new OrderEventImpl();

		BeanFactoryExpectationsFactory bfef = new BeanFactoryExpectationsFactory(context, beanFactory);
		bfef.allowingBeanFactoryGetBean(ContextIdNames.ORDER_EVENT, orderEvent);
		bfef.allowingBeanFactoryGetBean(ContextIdNames.EVENT_ORIGINATOR_HELPER, new EventOriginatorHelperImpl());
		bfef.allowingBeanFactoryGetBean(ContextIdNames.EVENT_ORIGINATOR, new EventOriginatorImpl());

		context.checking(new Expectations() {
			{
				allowing(order).getModifiedBy();
				will(returnValue(scapegoat));
				allowing(order).getLocale();
				will(returnValue(Locale.CANADA));

				allowing(timeService).getCurrentTime();
				will(returnValue(new Date()));

				allowing(moneyFormatter).formatCurrency(with(any(Money.class)), with(any(Locale.class)));
				will(returnValue(TEST_FORMATTED_AMOUNT));
			}
		});

		orderEventHelper = new OrderEventHelperImpl();
		orderEventHelper.setBeanFactory(beanFactory);
		orderEventHelper.setMoneyFormatter(moneyFormatter);
		orderEventHelper.setPricingSnapshotService(pricingSnapshotService);
		orderEventHelper.setTimeService(timeService);

		Map<PaymentType, OrderEventPaymentDetailFormatter> formatterMap = new HashMap<>();
		formatterMap.put(PaymentType.GIFT_CERTIFICATE, new OrderEventGiftCertificateDetailsFormatter());
		formatterMap.put(PaymentType.PAYMENT_TOKEN, new OrderEventPaymentTokenDetailsFormatter());
		orderEventHelper.setFormatterMap(formatterMap);
	}

	/**
	 * Test logging when order shipment method is changed.
	 */
	@Test
	public void testLogOrderShipmentMethodChangedWithCarrierCode() {
		// Given
		final String shippingOptionName = "Sample Shipping Option";
		final String shippingOptionCode = "ship_option_1";
		final String carrierCode = "carrier_1";
		PhysicalOrderShipment physicalShipment = new PhysicalOrderShipmentImpl();
		physicalShipment.setShippingOptionName(shippingOptionName);
		physicalShipment.setShippingOptionCode(shippingOptionCode);
		physicalShipment.setCarrierCode(carrierCode);

		// Expectations
		context.checking(new Expectations() {
			{
				oneOf(order).addOrderEvent(orderEvent);
			}
		});

		// When
		orderEventHelper.logOrderShipmentMethodChanged(order, physicalShipment);

		// Then
		assertTrue("Message should contain shippingOption name", orderEvent.getNote().contains(shippingOptionName));
		assertTrue("Message should contain shippingOption code", orderEvent.getNote().contains("Shipping Option Code: " + shippingOptionCode));
		assertTrue("Message should contain shippingOption carrier code", orderEvent.getNote().contains("Carrier Code: " + carrierCode));
	}

	/**
	 * Test logging when order shipment method is changed and no carrier code specified.
	 */
	@Test
	public void testLogOrderShipmentMethodChangedWithNoCarrierCode() {
		// Given
		final String shippingOptionCode = "ship_option_1";
		PhysicalOrderShipment physicalShipment = new PhysicalOrderShipmentImpl();
		physicalShipment.setShippingOptionCode(shippingOptionCode);

		// Expectations
		context.checking(new Expectations() {
			{
				oneOf(order).addOrderEvent(orderEvent);
			}
		});

		// When
		orderEventHelper.logOrderShipmentMethodChanged(order, physicalShipment);

		// Then
		assertFalse("Message should not contain carrier code in message as not specified", orderEvent.getNote().contains("carrier code:"));
	}

	/**
	 * Ensure logging of order payment refund with gift certificate has correct format.
	 */
	@Test
	public void ensureLoggingOfOrderPaymentRefundWithGiftCertificateHasCorrectFormat() {
		Order order = createBlankOrderWithLocale();
		orderEventHelper.logOrderPaymentRefund(order, createGiftCertificateRefundOrderPayment());
		OrderEvent refundOrderEvent = getSingleOrderEventFromOrder(order);
		String refundEventNote = refundOrderEvent.getNote();
		assertNoteIsRefund(refundEventNote);
		assertThatNoteIsForGiftCertificate(refundEventNote);
		assertThatNoteHasFormattedAmount(refundEventNote);
	}

	/**
	 * Ensure logging of order payment refund with payment token has correct format.
	 */
	@Test
	public void ensureLoggingOfOrderPaymentRefundWithPaymentTokenHasCorrectFormat() {
		Order order = createBlankOrderWithLocale();
		orderEventHelper.logOrderPaymentRefund(order, createPaymentTokenRefundOrderPayment());
		OrderEvent refundOrderEvent = getSingleOrderEventFromOrder(order);
		String refundEventNote = refundOrderEvent.getNote();
		assertNoteIsRefund(refundEventNote);
		assertThatNoteIsForPaymentToken(refundEventNote);
		assertThatNoteHasFormattedAmount(refundEventNote);
	}

	/**
	 * Ensure logging of order payment capture with payment token has correct format.
	 */
	@Test
	public void ensureLoggingOfOrderPaymentCaptureWithPaymentTokenHasCorrectFormat() {
		Order order = createBlankOrderWithLocale();
		orderEventHelper.logOrderPaymentCaptured(order, createPaymentTokenCaptureOrderPayment());
		OrderEvent captureOrderEvent = getSingleOrderEventFromOrder(order);
		String captureEventNote = captureOrderEvent.getNote();
		assertThatNoteIsCapture(captureEventNote);
		assertThatNoteIsForPaymentToken(captureEventNote);
		assertThatNoteHasFormattedAmount(captureEventNote);
	}

	/**
	 * Ensure logging of order payment capture with gift certificate has correct format.
	 */
	@Test
	public void ensureLoggingOfOrderPaymentCaptureWithGiftCertificateHasCorrectFormat() {
		Order order = createBlankOrderWithLocale();
		orderEventHelper.logOrderPaymentCaptured(order, createGiftCertificateCaptureOrderPayment());
		OrderEvent refundOrderEvent = getSingleOrderEventFromOrder(order);
		String captureEventNote = refundOrderEvent.getNote();
		assertThatNoteIsCapture(captureEventNote);
		assertThatNoteIsForGiftCertificate(captureEventNote);
		assertThatNoteHasFormattedAmount(captureEventNote);
	}

	@Test
	public void verifyLoggingOfOrderReleasedForFulfilmentHasCorrectFormat() throws Exception {
		final Order order = createBlankOrderWithLocale();
		order.setModifiedBy(scapegoat);

		orderEventHelper.logOrderReleasedForFulfilment(order);

		final OrderEvent orderReleasedEvent = getSingleOrderEventFromOrder(order);

		assertEquals("Unexpected event title", "Order Released for Fulfilment", orderReleasedEvent.getTitle());
		assertEquals("Unexpected event note", "Order is released for fulfilment.", orderReleasedEvent.getNote());
	}

	private OrderPayment createGiftCertificateRefundOrderPayment() {
		OrderPayment orderPayment = createRefundOrderPayment();
		addGiftCertificateToOrderPayment(orderPayment);
		return orderPayment;
	}

	private OrderPayment createPaymentTokenRefundOrderPayment() {
		OrderPayment orderPayment = createRefundOrderPayment();
		addPaymentTokenToOrderPayment(orderPayment);
		return orderPayment;
	}

	private OrderPayment createPaymentTokenCaptureOrderPayment() {
		OrderPayment orderPayment = createCaptureOrderPayment();
		addPaymentTokenToOrderPayment(orderPayment);
		return orderPayment;
	}

	private OrderPayment createGiftCertificateCaptureOrderPayment() {
		OrderPayment orderPayment = createCaptureOrderPayment();
		addGiftCertificateToOrderPayment(orderPayment);
		return orderPayment;
	}

	private void addGiftCertificateToOrderPayment(final OrderPayment orderPayment) {
		orderPayment.setPaymentMethod(PaymentType.GIFT_CERTIFICATE);
		GiftCertificate giftCertificate = new GiftCertificateImpl();
		giftCertificate.setGiftCertificateCode(TEST_GIFT_CERTIFICATE_CODE);
		orderPayment.setGiftCertificate(giftCertificate);
	}

	private void addPaymentTokenToOrderPayment(final OrderPayment orderPayment) {
		orderPayment.setPaymentMethod(PaymentType.PAYMENT_TOKEN);
		PaymentToken paymentToken = new PaymentTokenImpl.TokenBuilder()
				.withDisplayValue(TEST_TOKEN_DISPLAY_VALUE)
				.build();
		orderPayment.usePaymentToken(paymentToken);
	}

	private OrderPayment createRefundOrderPayment() {
		OrderPayment orderPayment = createApprovedOrderPayment();
		orderPayment.setTransactionType(OrderPayment.CREDIT_TRANSACTION);
		return orderPayment;
	}

	private OrderPayment createCaptureOrderPayment() {
		OrderPayment orderPayment = createApprovedOrderPayment();
		orderPayment.setTransactionType(OrderPayment.CAPTURE_TRANSACTION);
		return orderPayment;
	}

	private OrderPayment createApprovedOrderPayment() {
		OrderPayment orderPayment = new OrderPaymentImpl();
		orderPayment.setCurrencyCode("USD");
		orderPayment.setStatus(OrderPaymentStatus.APPROVED);
		orderPayment.setAmount(BigDecimal.TEN);
		return orderPayment;
	}

	private Order createBlankOrderWithLocale() {
		Order order = new OrderImpl();
		order.setLocale(Locale.US);
		return order;
	}

	private OrderEvent getSingleOrderEventFromOrder(final Order order) {
		Set<OrderEvent> orderEvents = order.getOrderEvents();
		assertEquals("Only one order event should be added.", 1, orderEvents.size());
		return orderEvents.iterator().next();
	}

	private void assertNoteIsRefund(final String refundEventNote) {
		assertThat("Note should be a refund.", refundEventNote, containsString("Refund"));
	}

	private void assertThatNoteIsCapture(final String captureEventNote) {
		assertThat("Note should be a capture.", captureEventNote, containsString("Capture"));
	}

	private void assertThatNoteIsForPaymentToken(final String refundEventNote) {
		assertThat("Payment type should be included in note.", refundEventNote, containsString(PaymentType.PAYMENT_TOKEN.getName()));
		assertThat("Token display value should be included in note.", refundEventNote, containsString(TEST_TOKEN_DISPLAY_VALUE));
	}

	private void assertThatNoteIsForGiftCertificate(final String refundEventNote) {
		assertThat("Payment type should be included in note.", refundEventNote, containsString(PaymentType.GIFT_CERTIFICATE.getName()));
	}

	private void assertThatNoteHasFormattedAmount(final String refundEventNote) {
		assertThat("Payment amount should be included in note.", refundEventNote, containsString(TEST_FORMATTED_AMOUNT));
	}
}
