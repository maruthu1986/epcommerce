/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.paymentgateways.cybersource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.paymentgateways.cybersource.impl.CyberSourceTestSubscriberFactoryImpl;
import com.elasticpath.paymentgateways.cybersource.provider.CybersourceConfigurationProvider;
import com.elasticpath.paymentgateways.factory.TestPaymentGatewayPluginDtoFactory;
import com.elasticpath.plugin.payment.dto.MoneyDto;
import com.elasticpath.plugin.payment.dto.OrderPaymentDto;
import com.elasticpath.plugin.payment.dto.TokenPaymentMethod;
import com.elasticpath.plugin.payment.dto.impl.MoneyDtoImpl;
import com.elasticpath.plugin.payment.dto.impl.OrderPaymentDtoImpl;
import com.elasticpath.plugin.payment.dto.impl.TokenPaymentMethodImpl;
import com.elasticpath.plugin.payment.exceptions.AuthorizedAmountExceededException;
import com.elasticpath.plugin.payment.exceptions.CardErrorException;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionResponse;
import com.elasticpath.plugin.payment.transaction.CaptureTransactionRequest;
import com.elasticpath.plugin.payment.transaction.CaptureTransactionResponse;
import com.elasticpath.plugin.payment.transaction.impl.AuthorizationTransactionRequestImpl;
import com.elasticpath.plugin.payment.transaction.impl.CaptureTransactionRequestImpl;

/**
 * A collection of integration tests against the Cybersource payment gateway's tokenized payment capability.
 */
public class CybersourcePaymentGatewayTokenizedPaymentsTest {
	private static final String INVALID_SUBSCRIBER_ID = "invalidSubscriberId";
	private static final BigDecimal TEST_AMOUNT = new BigDecimal("20.00");
	private static final String TEST_REFERENCE_ID = "12345";
	private static final String TEST_CURRENCY_CODE = "USD";
	
	private FakeCybersourceTokenPaymentGatewayPluginImpl cybersourceTokenPaymentGatewayPlugin;
	private String testSubscriberId;
	
	/**
	 * Sets up common elements of a test case.
	 */
	@Before 
	public void testSetUp() {
		Map<String, String> configurationValues = CybersourceConfigurationProvider.getProvider()
				.getConfigurationMap();
		cybersourceTokenPaymentGatewayPlugin = new FakeCybersourceTokenPaymentGatewayPluginImpl();
		cybersourceTokenPaymentGatewayPlugin.setConfigurationValues(configurationValues);
		cybersourceTokenPaymentGatewayPlugin.setCertificatePathPrefix(".");

		CyberSourceTestSubscriberFactory cyberSourceTestSubscriberFactory = new CyberSourceTestSubscriberFactoryImpl();
		testSubscriberId = cyberSourceTestSubscriberFactory.createBillableSubscriber();
	}

	/**
	 * Tests a successful pre-authorization of a purchase amount with a tokenized payment method.
	 */
	@Test
	public void testSuccessfulPreAuthorizationOfPurchaseAmount() {
		MoneyDto testPurchaseAmount = new MoneyDtoImpl(TEST_AMOUNT, TEST_CURRENCY_CODE);
		
		AuthorizationTransactionResponse preAuthorizationResponse = authorizePurchaseAmount(testPurchaseAmount, testSubscriberId);
		
		assertNotNull("A preauthorization response should have been returned", preAuthorizationResponse);
		assertEquals("The preauthorized money in the transaction response should be the same as expected ", testPurchaseAmount, 
				preAuthorizationResponse.getMoney());
	}

	/**
	 * Tests a successful capture of a purchase amount with a tokenized payment method.
	 */
	@Test
	public void testSuccessfulCaptureOfPurchaseAmount() {
		MoneyDto testPurchaseAmount = new MoneyDtoImpl(TEST_AMOUNT, TEST_CURRENCY_CODE);
		
		AuthorizationTransactionResponse authorizationResponse = authorizePurchaseAmount(testPurchaseAmount, testSubscriberId);
		
		CaptureTransactionResponse captureResponse = captureFromAuthorizationResponse(authorizationResponse);
		assertEquals("The captured money in the transaction response should be the same as amount authorized", testPurchaseAmount, 
				captureResponse.getMoney());
	}

	/**
	 * Tests a successful reverse of a pre authorization completed using a tokenized payment method.
	 */
	@Test
	public void testSuccessfulReverseOfPreAuthorizedPurchaseAmount() {
		MoneyDto testPurchaseAmount = new MoneyDtoImpl(TEST_AMOUNT, TEST_CURRENCY_CODE);
		
		AuthorizationTransactionResponse authorizationResponse = authorizePurchaseAmount(testPurchaseAmount, testSubscriberId);
		
		OrderPaymentDto testOrderPayment = TestPaymentGatewayPluginDtoFactory.createTestOrderPayment();
		testOrderPayment.setAmount(authorizationResponse.getMoney().getAmount());
		testOrderPayment.setCurrencyCode(authorizationResponse.getMoney().getCurrencyCode());
		testOrderPayment.setRequestToken(authorizationResponse.getRequestToken());
		testOrderPayment.setAuthorizationCode(authorizationResponse.getAuthorizationCode());
		
		cybersourceTokenPaymentGatewayPlugin.reversePreAuthorization(testOrderPayment);
	}
	
	/**
	 * Tests a successful void of a captured purchase amount using a tokenized payment method.
	 */
	@Test
	public void testSuccessfulVoidOfCapturedPurchaseAmount() {
		MoneyDto testPurchaseAmount = new MoneyDtoImpl(TEST_AMOUNT, TEST_CURRENCY_CODE);
		
		AuthorizationTransactionResponse authorizationResponse = authorizePurchaseAmount(testPurchaseAmount, testSubscriberId);
		CaptureTransactionResponse captureResponse = captureFromAuthorizationResponse(authorizationResponse);
		
		
		OrderPaymentDto testOrderPayment = TestPaymentGatewayPluginDtoFactory.createTestOrderPayment();
		testOrderPayment.setRequestToken(authorizationResponse.getRequestToken());
		testOrderPayment.setAuthorizationCode(authorizationResponse.getAuthorizationCode());
		testOrderPayment.setAmount(captureResponse.getMoney().getAmount());
		
		cybersourceTokenPaymentGatewayPlugin.voidCaptureOrCredit(testOrderPayment);
	}
	
	/**
	 * Test a successful refund of a purchase amount using a tokenkized payment method.
	 */
	@Test
	public void testSuccessfulRefundOfPurchaseAmount() {
		MoneyDto testPurchaseAmount = new MoneyDtoImpl(TEST_AMOUNT, TEST_CURRENCY_CODE);
		
		AuthorizationTransactionResponse authorizationResponse = authorizePurchaseAmount(testPurchaseAmount, testSubscriberId);
		CaptureTransactionResponse captureResponse = captureFromAuthorizationResponse(authorizationResponse);
		
		MoneyDto captureMoney = captureResponse.getMoney();

		OrderPaymentDto testOrderPayment = new OrderPaymentDtoImpl();
		testOrderPayment.setRequestToken(captureResponse.getRequestToken());
		testOrderPayment.setAuthorizationCode(captureResponse.getAuthorizationCode());
		testOrderPayment.setAmount(captureMoney.getAmount());
		testOrderPayment.setCurrencyCode(captureMoney.getCurrencyCode());
		testOrderPayment.setReferenceId(TEST_REFERENCE_ID);
		
		cybersourceTokenPaymentGatewayPlugin.refund(testOrderPayment, null);
	}
	
	/**
	 * Test capturing more than authorized throws an {@link AuthorizedAmountExceededException}.
	 */
	@Test(expected = AuthorizedAmountExceededException.class)
	public void testCapturingMoreThanAuthorized() {
		MoneyDto testPurchaseAmount = new MoneyDtoImpl(TEST_AMOUNT, TEST_CURRENCY_CODE);
		
		AuthorizationTransactionResponse authorizationResponse = authorizePurchaseAmount(testPurchaseAmount, testSubscriberId);
		CaptureTransactionRequest captureRequest = createCaptureRequestFromAuthorizationResponse(authorizationResponse);
		
		MoneyDto captureMoney = new MoneyDtoImpl(TEST_AMOUNT.add(new BigDecimal("0.01")), TEST_CURRENCY_CODE);
		captureRequest.setMoney(captureMoney);
		
		cybersourceTokenPaymentGatewayPlugin.capture(captureRequest);
	}
	
	/**
	 * Test preauthorizing a purchase amount with an invalid subscriber id.
	 */
	@Test(expected = CardErrorException.class)
	public void testPreAuthorizationOfPurchaseAmountWithInvalidSubscriberId() {
		MoneyDto testPurchaseAmount = new MoneyDtoImpl(TEST_AMOUNT, TEST_CURRENCY_CODE);
		
		authorizePurchaseAmount(testPurchaseAmount, INVALID_SUBSCRIBER_ID);
	}
	
	/**
	 * Test successfully recapturing a previously refunded transaction. The authorization is still valid so
	 * a merchant should successfully be able to recapture the amount refunded.
	 */
	@Test
	public void testSuccessfullyRecapturingAPreviouslyRefundedTransaction() {
		MoneyDto testPurchaseAmount = new MoneyDtoImpl(TEST_AMOUNT, TEST_CURRENCY_CODE);
		
		AuthorizationTransactionResponse authorizationResponse = authorizePurchaseAmount(testPurchaseAmount, testSubscriberId);
		
		CaptureTransactionResponse captureResponse = captureFromAuthorizationResponse(authorizationResponse);
		
		MoneyDto captureMoney = captureResponse.getMoney();
		
		OrderPaymentDto testOrderPayment = new OrderPaymentDtoImpl();
		testOrderPayment.setRequestToken(captureResponse.getRequestToken());
		testOrderPayment.setAuthorizationCode(captureResponse.getAuthorizationCode());
		testOrderPayment.setAmount(captureMoney.getAmount());
		testOrderPayment.setCurrencyCode(captureMoney.getCurrencyCode());
		testOrderPayment.setReferenceId(TEST_REFERENCE_ID);
		
		cybersourceTokenPaymentGatewayPlugin.refund(testOrderPayment, null);
		
		CaptureTransactionResponse postRefundCaptureResponse = captureFromAuthorizationResponse(authorizationResponse);
		assertEquals("The captured money in the transaction response should be the same as amount authorized", testPurchaseAmount, 
				postRefundCaptureResponse.getMoney());
	}

	private AuthorizationTransactionResponse authorizePurchaseAmount(final MoneyDto testPurchaseAmount, final String testSubscriberId) {
		TokenPaymentMethod tokenizedPaymentMethod = new TokenPaymentMethodImpl(testSubscriberId);
		
		AuthorizationTransactionRequestImpl authorizationRequest = new AuthorizationTransactionRequestImpl(tokenizedPaymentMethod, 
				testPurchaseAmount, TEST_REFERENCE_ID);
		return cybersourceTokenPaymentGatewayPlugin.preAuthorize(authorizationRequest);
	}
	
	private CaptureTransactionResponse captureFromAuthorizationResponse(final AuthorizationTransactionResponse authorizationResponse) {
		CaptureTransactionRequest captureRequest = createCaptureRequestFromAuthorizationResponse(authorizationResponse);
		return cybersourceTokenPaymentGatewayPlugin.capture(captureRequest);
	}
	
	private CaptureTransactionRequest createCaptureRequestFromAuthorizationResponse(final AuthorizationTransactionResponse authorizationResponse) {
		CaptureTransactionRequest captureRequest = new CaptureTransactionRequestImpl();
		captureRequest.setAuthorizationCode(authorizationResponse.getAuthorizationCode());
		captureRequest.setMoney(authorizationResponse.getMoney());
		captureRequest.setReferenceId(TEST_REFERENCE_ID);
		captureRequest.setRequestToken(authorizationResponse.getRequestToken());
		return captureRequest;
	}
}
