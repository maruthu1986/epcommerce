/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.billingaddress.relationship;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.addresses.AddressIdentifier;
import com.elasticpath.rest.definition.orders.BillingAddressInfoToBillingAddressRelationship;
import com.elasticpath.rest.definition.orders.BillingaddressInfoIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Billing address info to billing address link.
 */
public class BillingAddressInfoToBillingAddressRelationshipImpl implements BillingAddressInfoToBillingAddressRelationship.LinkTo {

	private final BillingaddressInfoIdentifier billingaddressInfoIdentifier;

	private final LinksRepository<BillingaddressInfoIdentifier, AddressIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param billingaddressInfoIdentifier billingaddressInfoIdentifier
	 * @param repository                   repository
	 */
	@Inject
	public BillingAddressInfoToBillingAddressRelationshipImpl(
			@RequestIdentifier final BillingaddressInfoIdentifier billingaddressInfoIdentifier,
			@ResourceRepository final LinksRepository<BillingaddressInfoIdentifier, AddressIdentifier> repository) {
		this.billingaddressInfoIdentifier = billingaddressInfoIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<AddressIdentifier> onLinkTo() {
		return repository.getElements(billingaddressInfoIdentifier);
	}
}
