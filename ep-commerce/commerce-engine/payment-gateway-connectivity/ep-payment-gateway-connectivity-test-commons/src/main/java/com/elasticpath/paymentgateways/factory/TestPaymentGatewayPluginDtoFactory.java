/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.paymentgateways.factory;

import java.math.BigDecimal;

import com.elasticpath.plugin.payment.dto.AddressDto;
import com.elasticpath.plugin.payment.dto.OrderPaymentDto;
import com.elasticpath.plugin.payment.dto.impl.AddressDtoImpl;
import com.elasticpath.plugin.payment.dto.impl.OrderPaymentDtoImpl;

/**
 * Factory to create dtos used in the payment gateway plugin converter for transaction request and responses.
 */
public final class TestPaymentGatewayPluginDtoFactory {

	private TestPaymentGatewayPluginDtoFactory() {
		// Static class
	}

	/**
	 * Creates a test billing {@link AddressDto} with test parameters.
	 * 
	 * @return created test billing {@link AddressDto}
	 */
	public static AddressDto createTestBillingAddress() {
		AddressDtoImpl testBillingAddress = new AddressDtoImpl();
		testBillingAddress.setCity("Beverly Hills");
		testBillingAddress.setCountry("US");
		testBillingAddress.setFirstName("John");
		testBillingAddress.setLastName("Doe");
		testBillingAddress.setZipOrPostalCode("90210");
		testBillingAddress.setSubCountry("CA");
		testBillingAddress.setStreet1("123 Main Street");
		return testBillingAddress;
	}
	
	/**
	 * Creates a test {@link OrderPaymentDto} with test parameters.
	 *
	 * @return created test {@link OrderPaymentDto}
	 */
	public static OrderPaymentDto createTestOrderPayment() {
		OrderPaymentDto testOrderPayment = new OrderPaymentDtoImpl();
		testOrderPayment.setAmount(BigDecimal.TEN);
		testOrderPayment.setCurrencyCode("USD");
		testOrderPayment.setReferenceId("12345");
		return testOrderPayment;
	}
}
