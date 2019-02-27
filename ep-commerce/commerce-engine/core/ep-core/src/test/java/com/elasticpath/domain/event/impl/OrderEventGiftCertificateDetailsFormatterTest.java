/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.domain.event.impl;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.catalog.impl.GiftCertificateImpl;
import com.elasticpath.domain.event.OrderEventPaymentDetailFormatter;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.impl.OrderPaymentImpl;



/**
 * Test for {@link OrderEventGiftCertificateDetailsFormatter}.
 */
public class OrderEventGiftCertificateDetailsFormatterTest {
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * Ensure correct format for gift certificate.
	 */
	@Test
	public void ensureCorrectFormatForGiftCertificate() {
		GiftCertificate giftCertificate = new GiftCertificateImpl();
		String testGiftCertificateCode = "testGiftCerficateCode";
		giftCertificate.setGiftCertificateCode(testGiftCertificateCode);
		OrderPayment orderPayment = new OrderPaymentImpl();
		orderPayment.setGiftCertificate(giftCertificate);
		OrderEventPaymentDetailFormatter formatter = new OrderEventGiftCertificateDetailsFormatter();

		formatter.formatPaymentDetails(orderPayment);
	}

}
