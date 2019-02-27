/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption.prototypes;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.SelectorRepository;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionInfoSelectorChoiceIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionInfoSelectorIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionInfoSelectorResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;
import com.elasticpath.rest.selector.SelectorChoice;

/**
 * Retrieves all choices for shipping option info selector.
 */
public class ChoicesShippingOptionInfoSelectorResourcePrototype implements ShippingOptionInfoSelectorResource.Choices {

	private final ShippingOptionInfoSelectorIdentifier shippingOptionInfoSelectorIdentifier;
	private final SelectorRepository<ShippingOptionInfoSelectorIdentifier, ShippingOptionInfoSelectorChoiceIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param shippingOptionInfoSelectorIdentifier	identifier
	 * @param repository				service
	 */
	@Inject
	public ChoicesShippingOptionInfoSelectorResourcePrototype(
		@RequestIdentifier final ShippingOptionInfoSelectorIdentifier shippingOptionInfoSelectorIdentifier,
		@ResourceRepository final SelectorRepository<ShippingOptionInfoSelectorIdentifier, ShippingOptionInfoSelectorChoiceIdentifier> repository) {
		this.shippingOptionInfoSelectorIdentifier = shippingOptionInfoSelectorIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<SelectorChoice> onChoices() {
		return repository.getChoices(shippingOptionInfoSelectorIdentifier);
	}
}
