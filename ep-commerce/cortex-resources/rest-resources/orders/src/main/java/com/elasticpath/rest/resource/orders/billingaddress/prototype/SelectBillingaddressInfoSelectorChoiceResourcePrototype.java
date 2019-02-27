/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.billingaddress.prototype;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.SelectorRepository;
import com.elasticpath.rest.definition.orders.BillingaddressInfoSelectorChoiceIdentifier;
import com.elasticpath.rest.definition.orders.BillingaddressInfoSelectorChoiceResource;
import com.elasticpath.rest.definition.orders.BillingaddressInfoSelectorIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;
import com.elasticpath.rest.selector.SelectResult;

/**
 * CREATE operation on the {@link BillingaddressInfoSelectorChoiceResource} for selecting a choice and promoting it to be chosen.
 */
public class SelectBillingaddressInfoSelectorChoiceResourcePrototype implements BillingaddressInfoSelectorChoiceResource.SelectWithResult {

	private final BillingaddressInfoSelectorChoiceIdentifier billingaddressInfoSelectorChoiceIdentifier;

	private final SelectorRepository<BillingaddressInfoSelectorIdentifier, BillingaddressInfoSelectorChoiceIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param billingaddressInfoSelectorChoiceIdentifier billingaddressInfoSelectorChoiceIdentifier
	 * @param repository           the repository
	 */
	@Inject
	public SelectBillingaddressInfoSelectorChoiceResourcePrototype(
			@ResourceRepository final SelectorRepository<BillingaddressInfoSelectorIdentifier, BillingaddressInfoSelectorChoiceIdentifier> repository,
			@RequestIdentifier final BillingaddressInfoSelectorChoiceIdentifier billingaddressInfoSelectorChoiceIdentifier) {

		this.billingaddressInfoSelectorChoiceIdentifier = billingaddressInfoSelectorChoiceIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<SelectResult<BillingaddressInfoSelectorIdentifier>> onSelectWithResult() {
		return repository.selectChoice(billingaddressInfoSelectorChoiceIdentifier);
	}
}
