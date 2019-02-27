/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.price.impl;

import io.reactivex.Observable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.prices.PriceForItemIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.price.PriceRepository;

/**
 * Link repository that adds price link if the item has a price.
 *
 * @param <I>  extends ItemIdentifier
 * @param <LI> extends PriceForItemIdentifier
 */
@Component
public class PriceToItemRepositoryImpl<I extends ItemIdentifier, LI extends PriceForItemIdentifier>
		implements LinksRepository<ItemIdentifier, PriceForItemIdentifier> {

	@Reference
	private PriceRepository priceRepository;

	@Override
	public Observable<PriceForItemIdentifier> getElements(final ItemIdentifier identifier) {
		String skuCode = identifier.getItemId().getValue().get(ItemRepository.SKU_CODE_KEY);
		String scope = identifier.getItems().getScope().getValue();
		return priceRepository.priceExists(scope, skuCode)
				.flatMapObservable(exists ->
						exists ? Observable.just(PriceForItemIdentifier.builder().withItem(identifier).build()) : Observable.empty());
	}
}
