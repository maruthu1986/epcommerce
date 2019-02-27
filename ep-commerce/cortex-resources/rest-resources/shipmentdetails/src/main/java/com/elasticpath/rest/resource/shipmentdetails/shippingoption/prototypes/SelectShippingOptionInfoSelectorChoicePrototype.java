/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.SelectorRepository;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionInfoSelectorChoiceIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionInfoSelectorChoiceResource;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionInfoSelectorIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceService;
import com.elasticpath.rest.selector.SelectResult;

/**
 * Select a shipping option info selector choice.
 */
public class SelectShippingOptionInfoSelectorChoicePrototype implements ShippingOptionInfoSelectorChoiceResource.SelectWithResult {

	private final ShippingOptionInfoSelectorChoiceIdentifier shippingOptionInfoSelectorChoiceIdentifier;
	private final SelectorRepository<ShippingOptionInfoSelectorIdentifier, ShippingOptionInfoSelectorChoiceIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param shippingOptionInfoSelectorChoiceIdentifier	identifier
	 * @param repository									repository
	 */
	@Inject
	public SelectShippingOptionInfoSelectorChoicePrototype(
			@RequestIdentifier final ShippingOptionInfoSelectorChoiceIdentifier shippingOptionInfoSelectorChoiceIdentifier,
			@ResourceService final SelectorRepository<ShippingOptionInfoSelectorIdentifier, ShippingOptionInfoSelectorChoiceIdentifier> repository) {
		this.shippingOptionInfoSelectorChoiceIdentifier = shippingOptionInfoSelectorChoiceIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<SelectResult<ShippingOptionInfoSelectorIdentifier>> onSelectWithResult() {
		return repository.selectChoice(shippingOptionInfoSelectorChoiceIdentifier);
	}
}
