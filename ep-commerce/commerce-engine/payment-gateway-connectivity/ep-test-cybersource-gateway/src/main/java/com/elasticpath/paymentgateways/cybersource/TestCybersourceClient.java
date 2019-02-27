/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.paymentgateways.cybersource;

import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.AUTHORIZATION_CODE;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.CC_AUTH_REVERSAL_SERVICE_AUTH_REQUEST_ID;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.CC_AUTH_REVERSAL_SERVICE_RUN;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.CC_AUTH_SERVICE_RUN;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.CC_CAPTURE_SERVICE_AUTH_REQUEST_ID;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.CC_CAPTURE_SERVICE_RUN;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.CC_CREDIT_SERVICE_RUN;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.MERCHANT_REFERENCE_CODE;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.PAY_SUBSCRIPTION_CREATE_SERVICE_RUN;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.PURCHASE_TOTALS_CURRENCY;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.PURCHASE_TOTALS_GRAND_TOTAL_AMOUNT;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.RECURRING_SUBSCRIPTION_INFO_SUBSCRIPTION_ID;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.REQUEST_ID;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceResponseConstants.AUTH_ALREADY_REVERSED_RESPONSE_CODE;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceResponseConstants.AUTH_AMOUNT_EXCEEDED_RESPONSE_CODE;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceResponseConstants.CC_AUTH_REPLY_AMOUNT;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceResponseConstants.CC_AUTH_REVERSAL_REPLY_AMOUNT;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceResponseConstants.CC_CAPTURE_REPLY_AMOUNT;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceResponseConstants.DECISION;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceResponseConstants.DECISION_ACCEPT;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceResponseConstants.DECISION_REJECT;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceResponseConstants.INVALID_DATA_RESPONSE_CODE;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceResponseConstants.REASON_CODE;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceResponseConstants.SUBSCRIPTION_REPLY_ID;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceResponseConstants.SUCCESSFUL_RESPONSE_CODE;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

import com.elasticpath.plugin.payment.dto.MoneyDto;
import com.elasticpath.plugin.payment.exceptions.PaymentGatewayException;

/**
 * Test Cybersource client to execute common transactions against a cybersource gateway.
 */
@SuppressWarnings("checkstyle:constantname")
public final class TestCybersourceClient {

	private static final String CYBERSOURCE_CLIENT_EXCEPTION = "Cybersource client exception: ";

	private static final Random generator = new Random();
	private static final Map<String, String> authorizations = new HashMap<>();
	private static final Set<String> subscriptions = new HashSet<>();

	/**
	 * Constructor.
	 */
	private TestCybersourceClient() {
		// Do not allow instantiations
	}
	
	/**
	 * Run a Cybersource transaction.
	 * 
	 * @param request a Map of request key value pairs
	 * @param cybersourceProperties the gateway properties
	 * @return a result Map of key value pairs
	 */
	public static Map<String, String> runTransaction(final Map<String, String> request, final Properties cybersourceProperties) {

		if (cybersourceProperties.isEmpty() || cybersourceProperties.containsValue("")) {
			throw new PaymentGatewayException(CYBERSOURCE_CLIENT_EXCEPTION);
		}

		if (request.containsKey(CC_AUTH_SERVICE_RUN)) {
			return runAuthService(request);
		}

		if (request.containsKey(CC_CAPTURE_SERVICE_RUN)) {
			return runCaptureService(request);
		}

		if (request.containsKey(CC_CREDIT_SERVICE_RUN)) {
			return runCreditService(request);
		}

		if (request.containsKey(PAY_SUBSCRIPTION_CREATE_SERVICE_RUN)) {
			return runSubscriptionService(request);
		}

		if (request.containsKey(CC_AUTH_REVERSAL_SERVICE_RUN)) {
			return runAuthReversalService(request);
		}

		throw new UnsupportedOperationException("Unknown request. " + request);
	}

	private static Map<String, String> runSubscriptionService(final Map<String, String> request) {
		Map<String, String> response;
		String subscriptionId = String.valueOf(generator.nextInt(Integer.MAX_VALUE));
		response = new ImmutableMap.Builder<String, String>()
			.put(MERCHANT_REFERENCE_CODE, request.get(MERCHANT_REFERENCE_CODE))
			.put(REASON_CODE, SUCCESSFUL_RESPONSE_CODE)
			.put(DECISION, DECISION_ACCEPT)
			.put(SUBSCRIPTION_REPLY_ID, subscriptionId)
			.build();
		subscriptions.add(subscriptionId);
		return response;
	}

	private static Map<String, String> runCreditService(final Map<String, String> request) {
		Map<String, String> response;
		if (request.containsKey(CC_CAPTURE_SERVICE_AUTH_REQUEST_ID)) {
			String requestId = request.get(CC_CAPTURE_SERVICE_AUTH_REQUEST_ID);
			if (authorizations.containsKey(requestId)) {
				response = ImmutableMap.of(REQUEST_ID, requestId, DECISION, DECISION_ACCEPT);
			} else {
				response = ImmutableMap.of(DECISION, DECISION_REJECT, REASON_CODE, INVALID_DATA_RESPONSE_CODE);
			}
		} else {
			response = ImmutableMap.of(DECISION, DECISION_ACCEPT);
		}

		return response;
	}

	private static Map<String, String> runCaptureService(final Map<String, String> request) {
		final String authRequestId = request.get(CC_CAPTURE_SERVICE_AUTH_REQUEST_ID);
		if (!authorizations.containsKey(authRequestId)) {
			return new ImmutableMap.Builder<String, String>()
				.put(MERCHANT_REFERENCE_CODE, request.get(MERCHANT_REFERENCE_CODE))
				.put(DECISION, DECISION_REJECT)
				.put(REQUEST_ID, request.get(REQUEST_ID))
				.put(REASON_CODE, AUTH_ALREADY_REVERSED_RESPONSE_CODE)
				.build();
		}

		final BigDecimal authAmount = new BigDecimal(authorizations.get(authRequestId));
		final BigDecimal captureAmount = new BigDecimal(request.get(PURCHASE_TOTALS_GRAND_TOTAL_AMOUNT));
		if (captureAmount.compareTo(authAmount) > 0) {
			return new ImmutableMap.Builder<String, String>()
				.put(MERCHANT_REFERENCE_CODE, request.get(MERCHANT_REFERENCE_CODE))
				.put(DECISION, DECISION_REJECT)
				.put(REQUEST_ID, request.get(REQUEST_ID))
				.put(REASON_CODE, AUTH_AMOUNT_EXCEEDED_RESPONSE_CODE)
				.build();
		}

		return new ImmutableMap.Builder<String, String>()
			.put(MERCHANT_REFERENCE_CODE, request.get(MERCHANT_REFERENCE_CODE))
			.put(PURCHASE_TOTALS_CURRENCY, request.get(PURCHASE_TOTALS_CURRENCY))
			.put(CC_CAPTURE_REPLY_AMOUNT, request.get(PURCHASE_TOTALS_GRAND_TOTAL_AMOUNT))
			.put(REASON_CODE, SUCCESSFUL_RESPONSE_CODE)
			.put(REQUEST_ID, request.get(REQUEST_ID))
			.put(DECISION, DECISION_ACCEPT)
			.build();
	}

	private static Map<String, String> runAuthService(final Map<String, String> request) {
		Map<String, String> response;

		if (request.containsKey(RECURRING_SUBSCRIPTION_INFO_SUBSCRIPTION_ID)
			&& !subscriptions.contains(request.get(RECURRING_SUBSCRIPTION_INFO_SUBSCRIPTION_ID))) {
				response = ImmutableMap.of(DECISION, DECISION_REJECT, REASON_CODE, INVALID_DATA_RESPONSE_CODE);
		} else {
			String requestID = String.valueOf(generator.nextInt(Integer.MAX_VALUE));
			response = new ImmutableMap.Builder<String, String>()
				.put(MERCHANT_REFERENCE_CODE, request.get(MERCHANT_REFERENCE_CODE))
				.put(CC_AUTH_REPLY_AMOUNT, request.get(PURCHASE_TOTALS_GRAND_TOTAL_AMOUNT))
				.put(PURCHASE_TOTALS_CURRENCY, request.get(PURCHASE_TOTALS_CURRENCY))
				.put(REASON_CODE, SUCCESSFUL_RESPONSE_CODE)
				.put(REQUEST_ID, requestID)
				.put(DECISION, DECISION_ACCEPT)
				.put(AUTHORIZATION_CODE, "888888")
				.build();
			authorizations.put(requestID, request.get(PURCHASE_TOTALS_GRAND_TOTAL_AMOUNT));
		}
		return response;
	}

	private static Map<String, String> runAuthReversalService(final Map<String, String> request) {
		Map<String, String> response;

		final String authRequestId = request.get(CC_AUTH_REVERSAL_SERVICE_AUTH_REQUEST_ID);
		final BigDecimal authAmount = new BigDecimal(authorizations.get(authRequestId));
		final BigDecimal reverseAmount = new BigDecimal(request.get(PURCHASE_TOTALS_GRAND_TOTAL_AMOUNT));
		if (reverseAmount.compareTo(authAmount) > 0) {
			response = new ImmutableMap.Builder<String, String>()
				.put(MERCHANT_REFERENCE_CODE, request.get(MERCHANT_REFERENCE_CODE))
				.put(DECISION, DECISION_REJECT)
				.put(REQUEST_ID, request.get(REQUEST_ID))
				.put(REASON_CODE, AUTH_AMOUNT_EXCEEDED_RESPONSE_CODE)
				.build();
		} else {
			response = new ImmutableMap.Builder<String, String>()
				.put(MERCHANT_REFERENCE_CODE, request.get(MERCHANT_REFERENCE_CODE))
				.put(PURCHASE_TOTALS_CURRENCY, request.get(PURCHASE_TOTALS_CURRENCY))
				.put(REASON_CODE, SUCCESSFUL_RESPONSE_CODE)
				.put(REQUEST_ID, request.get(REQUEST_ID))
				.put(DECISION, DECISION_ACCEPT)
				.put(CC_AUTH_REVERSAL_REPLY_AMOUNT, request.get(PURCHASE_TOTALS_GRAND_TOTAL_AMOUNT))
				.build();
			authorizations.remove(authRequestId);
		}

		return response;

	}

	/**
	 * Perform an authorization reversal for the given transaction ID and monetary total. Requires the transaction ID
	 * from a previous authorization.
	 *
	 * @param transactionId the transaction id that will be reversed
	 * @param amount the monetary amount to reverse
	 * @param cybersourceProperties the gateway properties
	 */
	public static void authorizationReversal(final String transactionId, final MoneyDto amount, final Properties cybersourceProperties) {
		final HashMap<String, String> request = new HashMap<>();

		request.put(CC_AUTH_REVERSAL_SERVICE_RUN, Boolean.TRUE.toString());

		request.put(CC_AUTH_REVERSAL_SERVICE_AUTH_REQUEST_ID, transactionId);
		request.put(REQUEST_ID, transactionId);
		request.put(PURCHASE_TOTALS_CURRENCY, amount.getCurrencyCode());
		request.put(PURCHASE_TOTALS_GRAND_TOTAL_AMOUNT, amount.getAmount().toString());
		request.put(MERCHANT_REFERENCE_CODE, "doesn't matter");

		Map<String, String> reply = runTransaction(request, cybersourceProperties);

		final String decision = reply.get(DECISION);
		if (!decision.equalsIgnoreCase(DECISION_ACCEPT)) {
			throw new AuthorizationReversalException(reply);
		}
	}

}
