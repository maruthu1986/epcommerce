/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.order.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.plugin.payment.PaymentType;

/**
 * Test cases for <code>OrderPaymentImpl</code>.
 */
public class OrderPaymentImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private static final String TEST_STRING = "testString";

	private static final String MONTH_JULY = "07";
	private static final String YEAR_2008 = "2008";

	private OrderPaymentImpl orderPaymentImpl;


	/**
	 * Prepare for the tests.
	 * @throws Exception on failure
	 */
	@Before
	public void setUp() throws Exception {
		orderPaymentImpl = new OrderPaymentImpl();
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderImpl.getCreatedDate()'.
	 */
	@Test
	public void testGetSetCreatedDate() {
		Date testDate = new Date();
		orderPaymentImpl.setCreatedDate(testDate);
		assertEquals(testDate, orderPaymentImpl.getCreatedDate());
	}

	/**
	 * Test method for getting/setting the amount.
	 */
	@Test
	public void testGetSetAmount() {
		BigDecimal amount = BigDecimal.ZERO;
		orderPaymentImpl.setAmount(amount);
		assertSame(amount, orderPaymentImpl.getAmount());
	}

	/**
	 * Tests trivial getting and setting of several String fields.
	 */
	@Test
	public void testGetSetStrings() {
		OrderPaymentImpl payment;

		payment = new OrderPaymentImpl();
		payment.setPaymentMethod(PaymentType.PAYMENT_TOKEN);
		assertEquals(PaymentType.PAYMENT_TOKEN, payment.getPaymentMethod());

		payment = new OrderPaymentImpl();
		payment.setAuthorizationCode("authorizationCode");
		assertEquals("authorizationCode", payment.getAuthorizationCode());

		payment = new OrderPaymentImpl();
		payment.setReferenceId("referenceId");
		assertEquals("referenceId", payment.getReferenceId());

		payment = new OrderPaymentImpl();
		payment.setRequestToken("requestToken");
		assertEquals("requestToken", payment.getRequestToken());
	}

	/**
	 * Test method for 'com.elasticpath.domain.order.impl.OrderPaymentImpl.getTransactionType()'.
	 */
	@Test
	public void testGetSetTransactionType() {
		orderPaymentImpl.setTransactionType(OrderPayment.AUTHORIZATION_TRANSACTION);
		assertSame(OrderPayment.AUTHORIZATION_TRANSACTION, orderPaymentImpl.getTransactionType());
	}

	/**
	 * Test method for 'com.elasticpath.domain.order.impl.OrderPaymentImpl.getStatus()'.
	 */
	@Test
	public void testGetSetStatus() {
		orderPaymentImpl.setStatus(OrderPaymentStatus.APPROVED);
		assertSame(OrderPaymentStatus.APPROVED, orderPaymentImpl.getStatus());
	}

	/**
	 * Test method for 'com.elasticpath.domain.order.impl.OrderPaymentImpl.getIpAddress & setIpAddress'.
	 */
	@Test
	public void testGetSetIpAddress() {
		orderPaymentImpl.setIpAddress(TEST_STRING);
		assertSame(TEST_STRING, orderPaymentImpl.getIpAddress());
	}
	
	/**
	 * Test method for getting/setting the orderShipment.
	 */
	@Test
	public void testGetSetOrderShipment() {
		OrderShipment orderShipment = new PhysicalOrderShipmentImpl();
		orderPaymentImpl.setOrderShipment(orderShipment);
		assertSame(orderShipment, orderPaymentImpl.getOrderShipment());
	}

	/**
	 * Test method for 'com.elasticpath.domain.order.impl.OrderPaymentImpl.isActual()'.
	 */
	@Test
	public void testIsActual() {
		assertFalse(orderPaymentImpl.isActual(""));
		assertFalse(orderPaymentImpl.isActual(null));
		assertTrue(orderPaymentImpl.isActual(" "));
		assertTrue(orderPaymentImpl.isActual("aoeuuue "));
		assertTrue(orderPaymentImpl.isActual("eouoeu"));

		assertTrue(StringUtils.isEmpty(""));
		assertTrue(StringUtils.isEmpty(null));
		assertFalse(StringUtils.isEmpty(" "));
		assertFalse(StringUtils.isEmpty("aoeuuue "));
		assertFalse(StringUtils.isEmpty("eouoeu"));
	}
	
	/**
	 * Test that createDate() parses correctly.
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testCreateDate() {
		OrderPaymentImpl payment = new OrderPaymentImpl();
		Date createdDate = payment.createDate(MONTH_JULY, YEAR_2008);
		//The created date's month will be zero-based
		assertEquals(Integer.parseInt(MONTH_JULY), createdDate.getMonth() + 1);
		//The created date's year will be 1900-based
		final int yearBase = 1900;
		assertEquals(Integer.parseInt(YEAR_2008), createdDate.getYear() + yearBase);
	}
	
	/**
	 * Test that if either the month or the year are blank strings,
	 * the start date will be null.
	 */
	@Test
	public void testCreateDateNull() {
		OrderPaymentImpl payment = new OrderPaymentImpl();
		assertNull(payment.createDate(MONTH_JULY, StringUtils.EMPTY));
		assertNull(payment.createDate(StringUtils.EMPTY, YEAR_2008));
		assertNull(payment.createDate(StringUtils.EMPTY, StringUtils.EMPTY));
	}
}