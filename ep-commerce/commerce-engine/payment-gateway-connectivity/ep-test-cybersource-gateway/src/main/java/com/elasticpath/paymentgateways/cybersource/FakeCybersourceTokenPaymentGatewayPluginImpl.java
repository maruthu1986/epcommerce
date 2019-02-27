/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.paymentgateways.cybersource;

import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.CC_AUTH_SERVICE_RUN;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.MERCHANT_ID;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.MERCHANT_REFERENCE_CODE;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.PURCHASE_TOTALS_CURRENCY;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.PURCHASE_TOTALS_GRAND_TOTAL_AMOUNT;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.RECURRING_SUBSCRIPTION_INFO_SUBSCRIPTION_ID;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceResponseConstants.CC_AUTH_REPLY_AMOUNT;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceResponseConstants.REQUEST_ID;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceResponseConstants.REQUEST_TOKEN;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.elasticpath.plugin.payment.PaymentGatewayType;
import com.elasticpath.plugin.payment.capabilities.TokenAuthorizationCapability;
import com.elasticpath.plugin.payment.dto.MoneyDto;
import com.elasticpath.plugin.payment.dto.TokenPaymentMethod;
import com.elasticpath.plugin.payment.dto.impl.MoneyDtoImpl;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionRequest;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionResponse;

/**
 * Cybersource tokenized payment gateway implementation.
 *
 */
public class FakeCybersourceTokenPaymentGatewayPluginImpl extends AbstractFakeCybersourcePaymentGatewayPluginImpl
			implements TokenAuthorizationCapability {
	private static final long serialVersionUID = 1L;

	@Override
	public String getPluginType() {
		return "paymentGatewayCyberSourceToken";
	}

	@Override
	public PaymentGatewayType getPaymentGatewayType() {
		return PaymentGatewayType.CREDITCARD;
	}
	
	@Override
	public AuthorizationTransactionResponse preAuthorize(final AuthorizationTransactionRequest authorizationTransactionRequest) {
		final HashMap<String, String> request = new HashMap<>();
		
		request.put(CC_AUTH_SERVICE_RUN, "true");
		request.put("businessRules_ignoreAVSResult", "true");

		request.put(MERCHANT_ID, getConfigurationValues().get(MERCHANT_ID));
		request.put(MERCHANT_REFERENCE_CODE, authorizationTransactionRequest.getReferenceId());
		
		MoneyDto money = authorizationTransactionRequest.getMoney();
		request.put(PURCHASE_TOTALS_CURRENCY, money.getCurrencyCode());

		request.put(PURCHASE_TOTALS_GRAND_TOTAL_AMOUNT,	convertToString(money.getAmount()));

		TokenPaymentMethod paymentMethod = (TokenPaymentMethod) authorizationTransactionRequest.getPaymentMethod();
		request.put(RECURRING_SUBSCRIPTION_INFO_SUBSCRIPTION_ID, paymentMethod.getValue());
		
		Map<String, String> transactionReply = runTransaction(paymentMethod, request);
		
		/*
		 * if the authorization was successful, obtain the request id and request token
		 * for the follow-on capture later.
		 */
		String authorizationCode = transactionReply.get(REQUEST_ID);
		String requestToken = transactionReply.get(REQUEST_TOKEN);
		
		String authorizationAmount = transactionReply.get(CC_AUTH_REPLY_AMOUNT);
		String authorizationCurrrency = transactionReply.get(PURCHASE_TOTALS_CURRENCY);
		
		return createAuthorizationResponse(null, authorizationCode, requestToken, null, new MoneyDtoImpl(new BigDecimal(authorizationAmount),
				authorizationCurrrency));
	}
}
