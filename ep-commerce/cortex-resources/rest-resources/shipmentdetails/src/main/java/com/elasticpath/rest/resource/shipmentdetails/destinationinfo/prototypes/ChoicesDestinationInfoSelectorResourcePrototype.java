/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.destinationinfo.prototypes;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.SelectorRepository;
import com.elasticpath.rest.definition.shipmentdetails.DestinationInfoSelectorChoiceIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.DestinationInfoSelectorIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.DestinationInfoSelectorResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;
import com.elasticpath.rest.selector.SelectorChoice;

/**
 * Retrieve all destination info choices.
 */
public class ChoicesDestinationInfoSelectorResourcePrototype implements DestinationInfoSelectorResource.Choices {

	private final DestinationInfoSelectorIdentifier destinationInfoSelectorIdentifier;
	private final SelectorRepository<DestinationInfoSelectorIdentifier, DestinationInfoSelectorChoiceIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param destinationInfoSelectorIdentifier		identifier
	 * @param repository							repository
	 */
	@Inject
	public ChoicesDestinationInfoSelectorResourcePrototype(
			@RequestIdentifier final DestinationInfoSelectorIdentifier destinationInfoSelectorIdentifier,
			@ResourceRepository final SelectorRepository<DestinationInfoSelectorIdentifier, DestinationInfoSelectorChoiceIdentifier> repository) {
		this.destinationInfoSelectorIdentifier = destinationInfoSelectorIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<SelectorChoice> onChoices() {
		return repository.getChoices(destinationInfoSelectorIdentifier);
	}
}
