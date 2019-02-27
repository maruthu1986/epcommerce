/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.promotions.possible;


import io.reactivex.Observable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.promotions.PossiblePromotionsForItemIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRepository;

/**
 * Repository for that checks if there are possible promotions for an item.
 *
 * @param <I> extends ItemIdentifier.
 * @param <IE> extends PossiblePromotionsForItemIdentifier.
 */
@Component
public class PossiblePromotionsForItemIdentifierRepositoryImpl<I extends ItemIdentifier, IE extends PossiblePromotionsForItemIdentifier> implements
		LinksRepository<ItemIdentifier, PossiblePromotionsForItemIdentifier> {

	private PromotionRepository promotionRepository;

	@Override
	public Observable<PossiblePromotionsForItemIdentifier> getElements(final ItemIdentifier identifier) {
		String scope = identifier.getItems().getScope().getValue();
		String skuCode = identifier.getItemId().getValue().get(ItemRepository.SKU_CODE_KEY);
		return promotionRepository.itemHasPossiblePromotions(scope, skuCode)
				.flatMapObservable(exists ->
						exists ? Observable.just(PossiblePromotionsForItemIdentifier.builder().withItem(identifier).build()) : Observable.empty());
	}

	@Reference
	public void setPromotionRepository(final PromotionRepository promotionRepository) {
		this.promotionRepository = promotionRepository;
	}
}
