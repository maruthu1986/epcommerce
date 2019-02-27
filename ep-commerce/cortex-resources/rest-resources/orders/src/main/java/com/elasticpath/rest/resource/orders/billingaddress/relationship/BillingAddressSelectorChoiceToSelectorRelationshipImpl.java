/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.billingaddress.relationship;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.orders.BillingaddressInfoSelectorChoiceBillingaddressInfoSelectorRelationship;
import com.elasticpath.rest.definition.orders.BillingaddressInfoSelectorChoiceIdentifier;
import com.elasticpath.rest.definition.orders.BillingaddressInfoSelectorIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Adds a selector link in choice.
 */
public class BillingAddressSelectorChoiceToSelectorRelationshipImpl implements
		BillingaddressInfoSelectorChoiceBillingaddressInfoSelectorRelationship.LinkTo {

	private final BillingaddressInfoSelectorIdentifier billingaddressInfoSelectorIdentifier;

	/**
	 * Constructor.
	 *
	 * @param billingaddressInfoSelectorChoiceIdentifier	identifier
	 */
	@Inject
	public BillingAddressSelectorChoiceToSelectorRelationshipImpl(
			@RequestIdentifier final BillingaddressInfoSelectorChoiceIdentifier billingaddressInfoSelectorChoiceIdentifier) {
		this.billingaddressInfoSelectorIdentifier = billingaddressInfoSelectorChoiceIdentifier.getBillingaddressInfoSelector();
	}

	@Override
	public Observable<BillingaddressInfoSelectorIdentifier> onLinkTo() {
		return Observable.just(billingaddressInfoSelectorIdentifier);
	}
}
