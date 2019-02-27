/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.destinationinfo.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.SelectorRepository;
import com.elasticpath.rest.definition.shipmentdetails.DestinationInfoSelectorChoiceIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.DestinationInfoSelectorChoiceResource;
import com.elasticpath.rest.definition.shipmentdetails.DestinationInfoSelectorIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;
import com.elasticpath.rest.selector.SelectResult;

/**
 * Select a destination info choice.
 */
public class SelectDestinationInfoSelectorChoicePrototype implements DestinationInfoSelectorChoiceResource.SelectWithResult {

	private final DestinationInfoSelectorChoiceIdentifier destinationInfoSelectorChoiceIdentifier;
	private final SelectorRepository<DestinationInfoSelectorIdentifier, DestinationInfoSelectorChoiceIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param destinationInfoSelectorChoiceIdentifier	identifier
	 * @param repository								repository
	 */
	@Inject
	public SelectDestinationInfoSelectorChoicePrototype(
			@RequestIdentifier final DestinationInfoSelectorChoiceIdentifier destinationInfoSelectorChoiceIdentifier,
			@ResourceRepository final SelectorRepository<DestinationInfoSelectorIdentifier, DestinationInfoSelectorChoiceIdentifier> repository) {
		this.destinationInfoSelectorChoiceIdentifier = destinationInfoSelectorChoiceIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<SelectResult<DestinationInfoSelectorIdentifier>> onSelectWithResult() {
		return repository.selectChoice(destinationInfoSelectorChoiceIdentifier);
	}
}
