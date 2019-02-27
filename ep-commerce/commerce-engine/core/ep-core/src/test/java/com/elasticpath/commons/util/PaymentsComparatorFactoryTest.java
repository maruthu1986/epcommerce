/*
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.commons.util;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.jmock.integration.junit4.JUnitRuleMockery;

import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.catalog.impl.GiftCertificateImpl;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.domain.order.impl.OrderPaymentImpl;
import com.elasticpath.plugin.payment.PaymentType;

/**
 * Test <code>PaymentsComparatorFactory</code>.
 */
public class PaymentsComparatorFactoryTest {

	private static final String GIFT_CERTIFICATE_CODE = "giftCertificateCode";
	private static final String TOKEN_DISPLAY_VALUE = "token-display-value";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/** Time displacement. **/
	private static final int TWO_SECONDS = 2000;
	private static final int FOUR_SECONDS = 4000;
	
	/** The amount of the OrderPayment. **/
	private static final double ORDER_AMOUNT = 19.99;

	/**
	 * Test that creates an OrderPaymentDateComparator, obtains it and
	 * compares two OrderPayments by order date. Returns a value of 0
	 * if the dates are the same, 1 if the first date is less than the second date
	 * and -1 if the second date is greater than the first date.
	 * @throws InterruptedException if the thread can't sleep
	 */
	@Test
	public void testGetOrderPaymentDateComparator() throws InterruptedException {
		
		//Create two order payments and initially set them with the same date and the same amount.
		OrderPayment orderOne = new OrderPaymentImpl();
		OrderPayment orderTwo = new OrderPaymentImpl();
		Date date = new Date();
		orderOne.setCreatedDate(date);
		orderTwo.setCreatedDate(date);
		orderOne.setAmount(new BigDecimal(ORDER_AMOUNT));
		orderTwo.setAmount(new BigDecimal(ORDER_AMOUNT));
		
		int returnedValue = PaymentsComparatorFactory.getOrderPaymentDateCompatator().compare(orderOne, orderTwo);
		assertEquals("Dates and amounts should match.", 0, returnedValue);
		
		orderTwo.setCreatedDate(new Date(System.currentTimeMillis() + TWO_SECONDS));
		returnedValue = PaymentsComparatorFactory.getOrderPaymentDateCompatator().compare(orderOne, orderTwo);
		assertEquals("Later date for second order payment should result in 1.", 1, returnedValue);
		
		orderOne.setCreatedDate(new Date(System.currentTimeMillis() + FOUR_SECONDS));
		returnedValue = PaymentsComparatorFactory.getOrderPaymentDateCompatator().compare(orderOne, orderTwo);
		assertEquals("Later date for second order payment should result in -1.", -1, returnedValue);
	}

	/**
	 * Ensure equal unknown payment type return zero.
	 */
	@Test
	public void ensureMatchingUnknownPaymentTypeReturnZero() {
		OrderPayment firstOrderPayment = createReturnAndExchangeOrderPayment();
		OrderPayment secondOrderPayment = createReturnAndExchangeOrderPayment();
		
		int returnedValue = PaymentsComparatorFactory.getPaymentSourceComparator().compare(firstOrderPayment, secondOrderPayment);
		assertEquals("Identical unknown payment types should be equal", 0, returnedValue);	
	}
	
	/**
	 * Ensure non matching unknown payment type order correctly.
	 */
	@Test
	public void ensureNonMatchingUnknownPaymentTypeOrderCorrectly() {
		OrderPayment firstOrderPayment = createReturnAndExchangeOrderPayment();
		OrderPayment secondOrderPayment = createGiftCertificateOrderPaymentWithTransactionType(OrderPayment.CAPTURE_TRANSACTION);
		
		int returnedValue = PaymentsComparatorFactory.getPaymentSourceComparator().compare(firstOrderPayment, secondOrderPayment);
		assertThat("First payment of type return and exchange payment should be first.", returnedValue, greaterThan(0));
		
		returnedValue = PaymentsComparatorFactory.getPaymentSourceComparator().compare(secondOrderPayment, firstOrderPayment);
		assertThat("Last payment of type return and exchange payment should be last.", returnedValue, lessThan(0));
	}

	/**
	 * Ensure unique list of payment token payments.
	 */
	@Test
	public void ensureUniqueListOfPaymentTokenPayments() {
		OrderPayment captureOrderPayment = createPaymentTokenOrderPaymentWithTransactionType(OrderPayment.CAPTURE_TRANSACTION);
		
		OrderPayment authOrderPayment = createPaymentTokenOrderPaymentWithTransactionType(OrderPayment.AUTHORIZATION_TRANSACTION);

		OrderPayment additionalAuthOrderPayment  = createPaymentTokenOrderPaymentWithTransactionType(OrderPayment.AUTHORIZATION_TRANSACTION);
		authOrderPayment.setDisplayValue("new-token-display-value");
		
		OrderPayment additionalCaptureOrderPayment = createPaymentTokenOrderPaymentWithTransactionType(OrderPayment.CAPTURE_TRANSACTION);
		
		List<OrderPayment> orderPayments = Arrays.asList(captureOrderPayment, 
				authOrderPayment, additionalAuthOrderPayment, additionalCaptureOrderPayment);
		List<OrderPayment> uniquePayments = PaymentsComparatorFactory.getListOfUniquePayments(OrderPayment.CAPTURE_TRANSACTION, orderPayments);
		assertEquals(1, uniquePayments.size());
		OrderPayment filteredPayment = uniquePayments.get(0);
		assertEquals("Transaction type should match.", filteredPayment.getTransactionType(), OrderPayment.CAPTURE_TRANSACTION);
		assertEquals("Token display value should match.", filteredPayment.getDisplayValue(), TOKEN_DISPLAY_VALUE);
	}

	/**
	 * Ensure unique list of gift certificate payments.
	 */
	@Test
	public void ensureUniqueListOfGiftCertificatePayments() {
		OrderPayment captureOrderPayment = createGiftCertificateOrderPaymentWithTransactionType(OrderPayment.CAPTURE_TRANSACTION);
		
		OrderPayment authOrderPayment = createGiftCertificateOrderPaymentWithTransactionType(OrderPayment.AUTHORIZATION_TRANSACTION);

		OrderPayment additionalAuthOrderPayment = createGiftCertificateOrderPaymentWithTransactionType(OrderPayment.AUTHORIZATION_TRANSACTION);
		
		OrderPayment additionalCaptureOrderPayment = createGiftCertificateOrderPaymentWithTransactionType(OrderPayment.CAPTURE_TRANSACTION);
		
		List<OrderPayment> orderPayments = Arrays.asList(captureOrderPayment, authOrderPayment, additionalAuthOrderPayment, 
				additionalCaptureOrderPayment);
		List<OrderPayment> uniquePayments = PaymentsComparatorFactory.getListOfUniquePayments(OrderPayment.CAPTURE_TRANSACTION, orderPayments);
		assertEquals(1, uniquePayments.size());
		OrderPayment filteredPayment = uniquePayments.get(0);
		assertEquals("Transaction type should match.", filteredPayment.getTransactionType(), OrderPayment.CAPTURE_TRANSACTION);
		assertEquals("Gift certificate codes should match.", filteredPayment.getGiftCertificate().getGiftCertificateCode(), GIFT_CERTIFICATE_CODE);
	}
	

	private OrderPayment createPaymentTokenOrderPaymentWithTransactionType(final String transactionType) {
		OrderPayment captureOrderPayment = new OrderPaymentImpl();
		captureOrderPayment.setPaymentMethod(PaymentType.PAYMENT_TOKEN);
		captureOrderPayment.setTransactionType(transactionType);
		captureOrderPayment.setDisplayValue(TOKEN_DISPLAY_VALUE);
		return captureOrderPayment;
	}
	
	private OrderPayment createGiftCertificateOrderPaymentWithTransactionType(final String transactionType) {
		OrderPayment orderPayment = new OrderPaymentImpl();
		orderPayment.setPaymentMethod(PaymentType.GIFT_CERTIFICATE);
		orderPayment.setTransactionType(transactionType);
		orderPayment.setGiftCertificate(createGiftCertificate());
		orderPayment.setStatus(OrderPaymentStatus.APPROVED);
		return orderPayment;
	}

	private GiftCertificate createGiftCertificate() {
		GiftCertificate giftCertificate = new GiftCertificateImpl();
		giftCertificate.setGiftCertificateCode(GIFT_CERTIFICATE_CODE);
		return giftCertificate;
	}

	private OrderPayment createReturnAndExchangeOrderPayment() {
		OrderPayment orderPayment = new OrderPaymentImpl();
		orderPayment.setPaymentMethod(PaymentType.RETURN_AND_EXCHANGE);
		orderPayment.setTransactionType(OrderPayment.CAPTURE_TRANSACTION);
		return orderPayment;
	}
}
