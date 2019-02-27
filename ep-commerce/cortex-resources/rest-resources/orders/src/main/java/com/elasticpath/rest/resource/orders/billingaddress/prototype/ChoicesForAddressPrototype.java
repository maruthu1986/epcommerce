/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.billingaddress.prototype;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.SelectorRepository;
import com.elasticpath.rest.definition.orders.BillingaddressInfoSelectorChoiceIdentifier;
import com.elasticpath.rest.definition.orders.BillingaddressInfoSelectorIdentifier;
import com.elasticpath.rest.definition.orders.BillingaddressInfoSelectorResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;
import com.elasticpath.rest.selector.SelectorChoice;

/**
 * READ operation on the {@link BillingaddressInfoSelectorResource} creating the choices links.
 */
public class ChoicesForAddressPrototype implements BillingaddressInfoSelectorResource.Choices {


	private final BillingaddressInfoSelectorIdentifier billingaddressInfoSelectorIdentifier;

	private final SelectorRepository<BillingaddressInfoSelectorIdentifier, BillingaddressInfoSelectorChoiceIdentifier> repository;
	
	/**
	 * Constructor.
	 *
	 * @param repository        repository
	 * @param billingaddressInfoSelectorIdentifier billingaddressInfoSelectorIdentifier
	 */
	@Inject
	public ChoicesForAddressPrototype(
			@ResourceRepository final SelectorRepository<BillingaddressInfoSelectorIdentifier, BillingaddressInfoSelectorChoiceIdentifier> repository,
			@RequestIdentifier final BillingaddressInfoSelectorIdentifier billingaddressInfoSelectorIdentifier) {
		this.repository = repository;
		this.billingaddressInfoSelectorIdentifier = billingaddressInfoSelectorIdentifier;
	}

	@Override
	public Observable<SelectorChoice> onChoices() {
		return repository.getChoices(billingaddressInfoSelectorIdentifier);
	}


}
