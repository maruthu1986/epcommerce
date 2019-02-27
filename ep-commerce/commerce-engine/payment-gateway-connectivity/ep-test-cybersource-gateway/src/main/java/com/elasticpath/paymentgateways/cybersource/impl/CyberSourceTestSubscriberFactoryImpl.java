/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.paymentgateways.cybersource.impl;

import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.BILL_TO_CITY;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.BILL_TO_COUNTRY;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.BILL_TO_FIRSTNAME;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.BILL_TO_LASTNAME;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.BILL_TO_POSTALCODE;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.BILL_TO_STATE;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.BILL_TO_STREET1;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.BUSINESS_RULES_IGNORE_AVS_RESULT;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.MERCHANT_REFERENCE_CODE;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.PAY_SUBSCRIPTION_CREATE_SERVICE_RUN;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.PURCHASE_TOTALS_CURRENCY;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.RECURRING_SUBSCRIPTION_INFO_FREQUENCY;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceResponseConstants.SUBSCRIPTION_REPLY_ID;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.elasticpath.paymentgateways.cybersource.CyberSourceTestSubscriberFactory;
import com.elasticpath.paymentgateways.cybersource.TestCybersourceClient;
import com.elasticpath.paymentgateways.cybersource.provider.CybersourceConfigurationProvider;
import com.elasticpath.paymentgateways.factory.TestPaymentGatewayPluginDtoFactory;
import com.elasticpath.plugin.payment.dto.AddressDto;

/**
 * Implementation of {@link CyberSourceTestSubscriberFactory} that creates subscribers using the {@link TestCybersourceClient}.
 */
public class CyberSourceTestSubscriberFactoryImpl implements CyberSourceTestSubscriberFactory {
	@Override
	public String createBillableSubscriber() {
		AddressDto billingAddress = TestPaymentGatewayPluginDtoFactory.createTestBillingAddress();
		Properties configurationProperties = CybersourceConfigurationProvider.getProvider().getConfigurationProperties();

		return createBillableSubscriber(billingAddress, "USD", configurationProperties);
	}

	@Override
	public String createBillableSubscriber(final AddressDto billingAddress, final String currencyCode, final Properties cybersourceProperties) {
		HashMap<String, String> subscriptionRequest = new HashMap<>();

		subscriptionRequest.put(PAY_SUBSCRIPTION_CREATE_SERVICE_RUN, "true");
		subscriptionRequest.put(BUSINESS_RULES_IGNORE_AVS_RESULT, "true");
		subscriptionRequest.put(BILL_TO_CITY, billingAddress.getCity());
		subscriptionRequest.put(BILL_TO_COUNTRY, billingAddress.getCountry());
		subscriptionRequest.put(BILL_TO_FIRSTNAME, billingAddress.getFirstName());
		subscriptionRequest.put(BILL_TO_LASTNAME, billingAddress.getLastName());
		subscriptionRequest.put(BILL_TO_POSTALCODE, billingAddress.getZipOrPostalCode());

		subscriptionRequest.put(BILL_TO_STATE, billingAddress.getSubCountry());
		subscriptionRequest.put(BILL_TO_STREET1, billingAddress.getStreet1());
		subscriptionRequest.put(PURCHASE_TOTALS_CURRENCY, currencyCode);

		subscriptionRequest.put(RECURRING_SUBSCRIPTION_INFO_FREQUENCY, "on-demand");
		subscriptionRequest.put(MERCHANT_REFERENCE_CODE, "not important");

		Map<String, String> subscriptionTransactionResponse = TestCybersourceClient.runTransaction(subscriptionRequest, cybersourceProperties);

		return subscriptionTransactionResponse.get(SUBSCRIPTION_REPLY_ID);
	}
}
