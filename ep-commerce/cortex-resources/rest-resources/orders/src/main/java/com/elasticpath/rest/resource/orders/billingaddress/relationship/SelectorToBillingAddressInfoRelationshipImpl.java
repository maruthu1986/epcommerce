/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.billingaddress.relationship;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.orders.BillingaddressInfoIdentifier;
import com.elasticpath.rest.definition.orders.BillingaddressInfoSelectorIdentifier;
import com.elasticpath.rest.definition.orders.SelectorForBillingaddressInfoRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Billing address selector to billing address info link.
 */
public class SelectorToBillingAddressInfoRelationshipImpl implements SelectorForBillingaddressInfoRelationship.LinkFrom {

	private final BillingaddressInfoSelectorIdentifier selectorIdentifier;

	/**
	 * Constructor.
	 *
	 * @param selectorIdentifier selectorIdentifier
	 */
	@Inject
	public SelectorToBillingAddressInfoRelationshipImpl(@RequestIdentifier final BillingaddressInfoSelectorIdentifier selectorIdentifier) {
		this.selectorIdentifier = selectorIdentifier;
	}

	@Override
	public Observable<BillingaddressInfoIdentifier> onLinkFrom() {
		return Observable.just(selectorIdentifier.getBillingaddressInfo());
	}
}
