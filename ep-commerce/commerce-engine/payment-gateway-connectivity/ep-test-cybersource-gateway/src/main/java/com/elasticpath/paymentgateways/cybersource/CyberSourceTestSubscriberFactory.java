/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.paymentgateways.cybersource;

import java.util.Properties;

import com.elasticpath.plugin.payment.dto.AddressDto;

/**
 * Creates different types of CyberSource subscribers for use in testing.
 */
public interface CyberSourceTestSubscriberFactory {
	/**
	 * Creates a subscriber that can be billed.
	 * @return the ID of the subscriber that was created
	 */
	String createBillableSubscriber();

	/**
	 * Create a Cybersource subscriber.
	 *
	 * @param billingAddress the billing address
	 * @param currencyCode the currency code
	 * @param cybersourceProperties the gateway properties
	 * @return a subscriber id which represents a token that can be used to replace sensitive
	 * data required for a transaction (i.e credit card information,billing address, etc).
	 */
	String createBillableSubscriber(AddressDto billingAddress, String currencyCode, Properties cybersourceProperties);

}
