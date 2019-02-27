/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.billingaddress.prototype;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.rest.definition.controls.InfoEntity;
import com.elasticpath.rest.definition.orders.BillingaddressInfoIdentifier;
import com.elasticpath.rest.definition.orders.BillingaddressInfoResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Prototype for the the Billing address INFO read operation.
 */
public class ReadAddressInfoPrototype implements BillingaddressInfoResource.Read {

	private static final String BILLING_ADDRESS_INFO_NAME = "billing-address-info";

	private final BillingaddressInfoIdentifier billingaddressInfoIdentifier;

	/**
	 * Constructor.
	 *
	 * @param billingaddressInfoIdentifier billingaddressInfoIdentifier
	 */
	@Inject
	public ReadAddressInfoPrototype(@RequestIdentifier final BillingaddressInfoIdentifier billingaddressInfoIdentifier) {
		this.billingaddressInfoIdentifier = billingaddressInfoIdentifier;
	}

	@Override
	public Single<InfoEntity> onRead() {
		return Single.just(InfoEntity.builder()
				.withName(BILLING_ADDRESS_INFO_NAME)
				.withInfoId(billingaddressInfoIdentifier.getOrder().getOrderId().getValue())
				.build());
	}
}
