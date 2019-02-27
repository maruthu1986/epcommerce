/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.payment.gateway.impl;

import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.plugin.payment.dto.AddressDto;
import com.elasticpath.plugin.payment.dto.MoneyDto;
import com.elasticpath.plugin.payment.dto.impl.AddressDtoImpl;
import com.elasticpath.plugin.payment.dto.impl.MoneyDtoImpl;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionRequest;
import com.elasticpath.plugin.payment.transaction.PaymentTransactionResponse;
import com.elasticpath.plugin.payment.transaction.impl.AuthorizationTransactionRequestImpl;
import com.elasticpath.service.payment.PaymentServiceException;


/**
 * Test for {@link ExchangePaymentGatewayPluginImpl}.
 */
public class ExchangePaymentGatewayPluginImplTest {
	private ExchangePaymentGatewayPluginImpl exchangePaymentGatewayPlugin;
	
	/**
	 * Initialize object under test.
	 */
	@Before
	public void initializeObjectUnderTest() { 
		exchangePaymentGatewayPlugin = new ExchangePaymentGatewayPluginImpl();
	}
	
	/**
	 * Ensure successful pre authorization with one dollar.
	 * This requirement has been present in code since 2007. The justification for this amount is that historically, one dollar ($1) is a 
	 * common amount to verify that a credit card is valid.  
	 */
	@Test
	public void ensureSuccessfulPreAuthorizationWithOneDollar() {
		AuthorizationTransactionRequest request = createTestAuthorizationRequest(BigDecimal.ONE);
		PaymentTransactionResponse response = exchangePaymentGatewayPlugin.preAuthorize(request,
				getValidBillingAddress(), null);
		assertPaymentIsAuthorized(response);
	}

	
	/**
	 * Ensure failing pre authorization with one penny above.
	 */
	@Test(expected = PaymentServiceException.class)
	public void ensureFailingPreAuthorizationWithOnePennyAbove() {
		AuthorizationTransactionRequest request = createTestAuthorizationRequest(new BigDecimal("1.01"));
		exchangePaymentGatewayPlugin.preAuthorize(request, getValidBillingAddress(), null);
	}

	/**
	 * Ensure failing pre authorization with one penny below.
	 */
	@Test(expected = PaymentServiceException.class)
	public void ensureFailingPreAuthorizationWithOnePennyBelow() {
		AuthorizationTransactionRequest request = createTestAuthorizationRequest(new BigDecimal("0.99"));
		exchangePaymentGatewayPlugin.preAuthorize(request, getValidBillingAddress(), null);
	}

	private AuthorizationTransactionRequest createTestAuthorizationRequest(final BigDecimal amount) {
		AuthorizationTransactionRequest request = new AuthorizationTransactionRequestImpl();
		MoneyDto money = new MoneyDtoImpl();
		money.setAmount(amount);
		money.setCurrencyCode("USD");
		request.setMoney(money);
		return request;
	}
	
	private void assertPaymentIsAuthorized(final PaymentTransactionResponse response) {
		assertNotNull(response.getReferenceId());
		assertNotNull(response.getAuthorizationCode());
	}
	
	private AddressDto getValidBillingAddress() {
		return new AddressDtoImpl();
	}
	
}
