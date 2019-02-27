/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.options.prototype;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionsIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionsResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Read operation for the option with lists each and every option.
 */
public class ReadOptionsImpl implements PurchaseLineItemOptionsResource.Read {

	private final PurchaseLineItemOptionsIdentifier optionsIdentifier;
	private final LinksRepository<PurchaseLineItemOptionsIdentifier, PurchaseLineItemOptionIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param optionsIdentifier options identifier
	 * @param repository repo for the options
	 */
	@Inject
	public ReadOptionsImpl(
			@RequestIdentifier final PurchaseLineItemOptionsIdentifier optionsIdentifier,
			@ResourceRepository final LinksRepository<PurchaseLineItemOptionsIdentifier, PurchaseLineItemOptionIdentifier> repository) {

		this.optionsIdentifier = optionsIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<PurchaseLineItemOptionIdentifier> onRead() {
		return repository.getElements(optionsIdentifier);
	}
}
