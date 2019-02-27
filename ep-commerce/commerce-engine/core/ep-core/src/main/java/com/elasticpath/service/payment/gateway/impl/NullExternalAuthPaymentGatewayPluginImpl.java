/*
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.payment.gateway.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.plugin.payment.capabilities.CaptureCapability;
import com.elasticpath.plugin.payment.capabilities.DirectPostAuthCapability;
import com.elasticpath.plugin.payment.capabilities.DirectPostTokenAcquireCapability;
import com.elasticpath.plugin.payment.capabilities.FinalizeShipmentCapability;
import com.elasticpath.plugin.payment.capabilities.RefundCapability;
import com.elasticpath.plugin.payment.capabilities.ReversePreAuthorizationCapability;
import com.elasticpath.plugin.payment.capabilities.SaleCapability;
import com.elasticpath.plugin.payment.capabilities.TokenAuthorizationCapability;
import com.elasticpath.plugin.payment.capabilities.VoidCaptureCapability;
import com.elasticpath.plugin.payment.dto.AddressDto;
import com.elasticpath.plugin.payment.dto.OrderPaymentDto;
import com.elasticpath.plugin.payment.dto.OrderShipmentDto;
import com.elasticpath.plugin.payment.dto.PayerAuthenticationEnrollmentResultDto;
import com.elasticpath.plugin.payment.dto.PaymentOptionFormDescriptor;
import com.elasticpath.plugin.payment.dto.PaymentOptionFormDescriptorBuilder;
import com.elasticpath.plugin.payment.dto.PaymentOptionFormFieldTypeEnum;
import com.elasticpath.plugin.payment.dto.PaymentOptionFormRowDescriptor;
import com.elasticpath.plugin.payment.dto.PaymentOptionFormRowDescriptorBuilder;
import com.elasticpath.plugin.payment.dto.ShoppingCartDto;
import com.elasticpath.plugin.payment.dto.impl.OrderPaymentDtoImpl;
import com.elasticpath.plugin.payment.dto.impl.PayerAuthenticationEnrollmentResultDtoImpl;
import com.elasticpath.plugin.payment.exceptions.CardErrorException;
import com.elasticpath.plugin.payment.exceptions.CardExpiredException;
import com.elasticpath.plugin.payment.spi.AbstractCreditCardPaymentGatewayPluginSPI;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionRequest;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionResponse;
import com.elasticpath.plugin.payment.transaction.CaptureTransactionRequest;
import com.elasticpath.plugin.payment.transaction.CaptureTransactionResponse;
import com.elasticpath.plugin.payment.transaction.TokenAcquireTransactionRequest;
import com.elasticpath.plugin.payment.transaction.TokenAcquireTransactionResponse;
import com.elasticpath.plugin.payment.transaction.impl.AuthorizationTransactionResponseImpl;
import com.elasticpath.plugin.payment.transaction.impl.CaptureTransactionResponseImpl;
import com.elasticpath.plugin.payment.transaction.impl.TokenAcquireTransactionResponseImpl;

/**
 * Implementation of the "null" payment gateway which does not connect to any external service and is used mainly for testing.
 */
@SuppressWarnings("PMD.AvoidThrowingNullPointerException")
public class NullExternalAuthPaymentGatewayPluginImpl extends AbstractCreditCardPaymentGatewayPluginSPI
		implements DirectPostAuthCapability, DirectPostTokenAcquireCapability, TokenAuthorizationCapability,
		RefundCapability, VoidCaptureCapability, ReversePreAuthorizationCapability, CaptureCapability, SaleCapability, FinalizeShipmentCapability {

	/**
	 * Serial version id.
	 */
	public static final long serialVersionUID = 5000000001L;

	private static final String NULL_AUTH_CODE_PREFIX = "AuthCode";
	private static final String NULL_REQUEST_TOKEN_PREFIX = "RequestToken";
	private static final String NULL_REFERENCE_ID_PREFIX = "RefId";
	private static final String NULL_REFUND_ID_PREFIX = "RefundId";

	private static final int MAX_NAME_LENGTH = 60;
	private static final int MAX_CARDNUMBER_LENGTH = 16;
	private static final int MAX_SECURITYCODE_LENGTH = 4;
	private static final int MIN_SECURITYCODE_LENGTH = 3;
	private static final int MASKED_CARD_NUMBER_INDEX = 12;

	private static final String FIELD_CARD_TYPE = "cardType";
	private static final String FIELD_CARD_HOLDER = "cardHolder";
	private static final String FIELD_CARD_NUMBER = "cardNumber";
	private static final String FIELD_EXPIRY_MONTH = "expiryMonth";
	private static final String FIELD_EXPIRY_YEAR = "expiryYear";
	private static final String FIELD_CVV_CODE = "cvvCode";

	private static boolean captureFailFlag;
	private static boolean reversePreAuthorizeFailFlag;
	private static boolean saleFailFlag;

	private static final String GATEWAY_TYPE = "paymentGatewayExternalAuthNull";

	private final Random randomGenerator = new Random(System.currentTimeMillis());

	@Override
	public String getPluginType() {
		return GATEWAY_TYPE;
	}

	@Override
	public PaymentOptionFormDescriptor buildExternalAuthRequest(final AuthorizationTransactionRequest authorizationTransactionRequest,
																final AddressDto billingAddress, final OrderShipmentDto shipment,
																final String redirectExternalAuthUrl, final String finishExternalAuthUrl,
																final String cancelExternalAuthUrl) {
		if (authorizationTransactionRequest == null) {
			throw new NullPointerException("authorizationTransactionRequest is null");
		}
		if (billingAddress == null) {
			throw new NullPointerException("billingAddress is null");
		}
		if (finishExternalAuthUrl == null) {
			throw new NullPointerException("finishExternalAuthUrl is null");
		}
		if (cancelExternalAuthUrl == null) {
			throw new NullPointerException("cancelExternalAuthUrl is null");
		}
		final String amount = authorizationTransactionRequest.getMoney().getAmount().setScale(2, BigDecimal.ROUND_FLOOR).toString();
		final String currencyCode = authorizationTransactionRequest.getMoney().getCurrencyCode().toLowerCase();

		// Card type selection row
		PaymentOptionFormRowDescriptor newCardCardTypeRow = new PaymentOptionFormRowDescriptorBuilder("billingandreview.paymentmethod")
				.withSpecialField(FIELD_CARD_TYPE, true, PaymentOptionFormFieldTypeEnum.SPECIAL_CARD_TYPE, null)
				.build();

		// Cardholder name row
		PaymentOptionFormRowDescriptor newCardCardholderRow = new PaymentOptionFormRowDescriptorBuilder("billingandreview.cardholdername")
				.withTextField(FIELD_CARD_HOLDER, true, true, MAX_NAME_LENGTH)
				.build();

		// Card number row
		PaymentOptionFormRowDescriptor newCardCardNumberRow = new PaymentOptionFormRowDescriptorBuilder("billingandreview.cardnumber")
				.withTextField(FIELD_CARD_NUMBER, false, true, MAX_CARDNUMBER_LENGTH)
				.build();

		// Expiration date row
		PaymentOptionFormRowDescriptor newCardExpiryRow = new PaymentOptionFormRowDescriptorBuilder("billingandreview.expirationdate")
				.withSpecialField(FIELD_EXPIRY_MONTH, true, PaymentOptionFormFieldTypeEnum.SPECIAL_EXPIRY, "MM")
				.withSpecialField(FIELD_EXPIRY_YEAR, true, PaymentOptionFormFieldTypeEnum.SPECIAL_EXPIRY, "yyyy")
				.build();

		// Security code row
		PaymentOptionFormRowDescriptor newCardSecurityCardRow = new PaymentOptionFormRowDescriptorBuilder("billingandreview.securitycode")
				.withTextField(FIELD_CVV_CODE, false, true, MIN_SECURITYCODE_LENGTH, MAX_SECURITYCODE_LENGTH)
				.build();

		// Create form descriptor
		return new PaymentOptionFormDescriptorBuilder("billingandreview.newCreditCard", PaymentType.PAYMENT_TOKEN)
				.withVisibleRow(newCardCardTypeRow)
				.withVisibleRow(newCardCardholderRow)
				.withVisibleRow(newCardCardNumberRow)
				.withVisibleRow(newCardExpiryRow)
				.withVisibleRow(newCardSecurityCardRow)
				.withHiddenField("amount", amount)
				.withHiddenField("currencyCode", currencyCode)
				.withPostAction(finishExternalAuthUrl)
				.build();
	}

	@Override
	public OrderPaymentDto handleExternalAuthResponse(final Map<String, String> responseMap) {
		if (responseMap == null) {
			throw new NullPointerException("responseMap is null");
		}

		final OrderPaymentDto orderPayment = new OrderPaymentDtoImpl();
		orderPayment.setAuthorizationCode(getAuthCode());
		orderPayment.setRequestToken(getRequestToken());
		orderPayment.setEmail(responseMap.get("email"));
		orderPayment.setAmount(new BigDecimal(responseMap.get("amount")));
		orderPayment.setCurrencyCode(responseMap.get("currencyCode"));
		return orderPayment;
	}

	@Override
	public PaymentOptionFormDescriptor buildExternalTokenAcquireRequest(final TokenAcquireTransactionRequest tokenAcquireTransactionRequest,
			final AddressDto billingAddress, final String finishExternalTokenAcquireUrl, final String cancelExternalTokenAcquireUrl) {
		if (tokenAcquireTransactionRequest == null) {
			throw new NullPointerException("tokenAcquireTransactionRequest is null");
		}
		if (billingAddress == null) {
			throw new NullPointerException("billingAddress is null");
		}
		if (finishExternalTokenAcquireUrl == null) {
			throw new NullPointerException("finishExternalTokenAcquireUrl is null");
		}
		if (cancelExternalTokenAcquireUrl == null) {
			throw new NullPointerException("cancelExternalTokenAcquireUrl is null");
		}

		// Card type selection row
		PaymentOptionFormRowDescriptor newCardCardTypeRow = new PaymentOptionFormRowDescriptorBuilder("billingandreview.paymentmethod")
				.withSpecialField(FIELD_CARD_TYPE, true, PaymentOptionFormFieldTypeEnum.SPECIAL_CARD_TYPE, null)
				.build();

		// Cardholder name row
		PaymentOptionFormRowDescriptor newCardCardholderRow = new PaymentOptionFormRowDescriptorBuilder("billingandreview.cardholdername")
				.withTextField(FIELD_CARD_HOLDER, true, true, MAX_NAME_LENGTH)
				.build();

		// Card number row
		PaymentOptionFormRowDescriptor newCardCardNumberRow = new PaymentOptionFormRowDescriptorBuilder("billingandreview.cardnumber")
				.withTextField(FIELD_CARD_NUMBER, false, true, MAX_CARDNUMBER_LENGTH)
				.build();

		// Expiration date row
		PaymentOptionFormRowDescriptor newCardExpiryRow = new PaymentOptionFormRowDescriptorBuilder("billingandreview.expirationdate")
				.withSpecialField(FIELD_EXPIRY_MONTH, true, PaymentOptionFormFieldTypeEnum.SPECIAL_EXPIRY, "MM")
				.withSpecialField(FIELD_EXPIRY_YEAR, true, PaymentOptionFormFieldTypeEnum.SPECIAL_EXPIRY, "yyyy")
				.build();

		// Security code row
		PaymentOptionFormRowDescriptor newCardSecurityCardRow = new PaymentOptionFormRowDescriptorBuilder("billingandreview.securitycode")
				.withTextField(FIELD_CVV_CODE, false, true, MIN_SECURITYCODE_LENGTH, MAX_SECURITYCODE_LENGTH)
				.build();

		// Create form descriptor
		return new PaymentOptionFormDescriptorBuilder("billingandreview.newCreditCard", PaymentType.PAYMENT_TOKEN)
				.withVisibleRow(newCardCardTypeRow)
				.withVisibleRow(newCardCardholderRow)
				.withVisibleRow(newCardCardNumberRow)
				.withVisibleRow(newCardExpiryRow)
				.withVisibleRow(newCardSecurityCardRow)
				.withHiddenField("token", UUID.randomUUID().toString())
				.withPostAction(finishExternalTokenAcquireUrl)
				.build();
	}

	@Override
	public TokenAcquireTransactionResponse handleExternalTokenAcquireResponse(final Map<String, String> responseMap) {
		TokenAcquireTransactionResponse tokenAcquireTransactionResponse = new TokenAcquireTransactionResponseImpl();
		tokenAcquireTransactionResponse.setPaymentToken(responseMap.get("token"));
		tokenAcquireTransactionResponse.setDisplayValue("xxxx-xxxx-xxxx-" + responseMap.get(FIELD_CARD_NUMBER).substring(MASKED_CARD_NUMBER_INDEX)
				+ " " + responseMap.get(FIELD_EXPIRY_MONTH) + "-" + responseMap.get(FIELD_EXPIRY_YEAR));
		return null;
	}

	@Override
	public AuthorizationTransactionResponse preAuthorize(final AuthorizationTransactionRequest authorizationTransactionRequest) {
		AuthorizationTransactionResponse response = new AuthorizationTransactionResponseImpl();
		response.setAuthorizationCode(getAuthCode());
		response.setRequestToken(getRequestToken());
		response.setReferenceId(getReferenceId());
		response.setMoney(authorizationTransactionRequest.getMoney());
		return response;
	}

	/**
	 * Reverse a previous pre-authorization. This can only be executed on Visas using the "Vital" processor and authorizations cannot be reversed
	 * using the test server and card info because the auth codes are not valid (Cybersource).
	 * 
	 * @param payment the payment that was previously pre-authorized
	 */
	@Override
	public void reversePreAuthorization(final OrderPaymentDto payment) {
		if (reversePreAuthorizeFailFlag) {
			throw new CardExpiredException("Card has expired meanwhile");
		}

		payment.setReferenceId(getReferenceId());
	}

	/**
	 * Void a previous capture or credit. Can usually only be executed on the same day of the original transaction.
	 * 
	 * @param payment the payment to be voided
	 */
	@Override
	public void voidCaptureOrCredit(final OrderPaymentDto payment) {
		payment.setReferenceId(getReferenceId());
	}

	@Override
	public CaptureTransactionResponse capture(final CaptureTransactionRequest captureTransactionRequest) {
		if (captureFailFlag) {
			throw new CardErrorException("credit card reported stolen");
		}
		CaptureTransactionResponse response = new CaptureTransactionResponseImpl();
		response.setReferenceId(getReferenceId());
		return response;
	}

	@Override
	public void sale(final OrderPaymentDto payment, final AddressDto billingAddress, final OrderShipmentDto shipment) {
		if (saleFailFlag) {
			throw new CardErrorException("credit card reported stolen");
		}
		payment.setAuthorizationCode(getAuthCode());
		payment.setReferenceId(getReferenceId());
	}

	/**
	 * Refunds a previous capture.
	 * 
	 * @param payment the payment to be refunded
	 * @param billingAddress the billing address
	 */
	@Override
	public void refund(final OrderPaymentDto payment, final AddressDto billingAddress) {
		payment.setReferenceId(getReferenceId());
		payment.setAuthorizationCode(NULL_REFUND_ID_PREFIX + randomGenerator.nextInt());
	}

	@Override
	public List<String> getSupportedCardTypes() {
		return new ArrayList<>(getMapOfCardTypesToCardCodes().keySet());
	}
	
	private BiMap<String, String> getMapOfCardTypesToCardCodes() {
		final BiMap<String, String> map = HashBiMap.create();
		map.put("Visa", "V");
		map.put("MasterCard", "M");
		map.put("American Express", "AE");
		return map;
	}
	
	private String getAuthCode() {
		return NULL_AUTH_CODE_PREFIX + randomGenerator.nextInt();
	}

	private String getRequestToken() {
		return NULL_REQUEST_TOKEN_PREFIX + randomGenerator.nextInt();
	}

	private String getReferenceId() {
		return NULL_REFERENCE_ID_PREFIX + randomGenerator.nextInt();
	}

	/**
	 * Sets whether to fail on capture.
	 * 
	 * @param captureFailFlag whether to fail
	 */
	public static void setFailOnCapture(final boolean captureFailFlag) {
		NullExternalAuthPaymentGatewayPluginImpl.captureFailFlag = captureFailFlag;
	}

	/**
	 * Sets whether to fail on reverse pre-authorize.
	 * 
	 * @param reversePreAuthorizeFailFlag whether to fail
	 */
	public static void setFailOnReversePreAuthorization(final boolean reversePreAuthorizeFailFlag) {
		NullExternalAuthPaymentGatewayPluginImpl.reversePreAuthorizeFailFlag = reversePreAuthorizeFailFlag;
	}

	/**
	 * Sets whether to fail on sale.
	 * 
	 * @param saleFailFlag whether to fail
	 */
	public static void setFailOnSale(final boolean saleFailFlag) {
		NullExternalAuthPaymentGatewayPluginImpl.saleFailFlag = saleFailFlag;
	}

	@Override
	public PayerAuthenticationEnrollmentResultDto checkEnrollment(final ShoppingCartDto shoppingCart, final OrderPaymentDto payment) {
		return new PayerAuthenticationEnrollmentResultDtoImpl();
	}

	@Override
	public Collection<String> getConfigurationParameters() {
		return new ArrayList<>();
	}

	@Override
	public void finalizeShipment(final OrderShipmentDto orderShipment) {
		// Do nothing
	}

	@Override
	public String getCardTypeInternalCode(final String cardType) {
		return getMapOfCardTypesToCardCodes().get(cardType);
	}
}
