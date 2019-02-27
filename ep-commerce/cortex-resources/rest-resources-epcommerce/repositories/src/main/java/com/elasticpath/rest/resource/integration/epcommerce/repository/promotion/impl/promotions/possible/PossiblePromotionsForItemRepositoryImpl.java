/*
 *  Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.promotions.possible;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.promotions.PromotionIdentifierUtil
		.buildPromotionIdentifier;

import io.reactivex.Observable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.promotions.PossiblePromotionsForItemIdentifier;
import com.elasticpath.rest.definition.promotions.PromotionIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRepository;

/**
 * Repository that implements reading possible item promotions data from external systems.
 *
 * @param <I>	extends PossiblePromotionsForItemIdentifier
 * @param <IE>	extends PromotionIdentifier
 */
@Component
public class PossiblePromotionsForItemRepositoryImpl<I extends PossiblePromotionsForItemIdentifier, IE extends PromotionIdentifier> implements
		LinksRepository<PossiblePromotionsForItemIdentifier, PromotionIdentifier> {

	private PromotionRepository promotionRepository;

	@Override
	public Observable<PromotionIdentifier> getElements(final PossiblePromotionsForItemIdentifier identifier) {
		ItemIdentifier itemIdentifier = identifier.getItem();
		String scope  = itemIdentifier.getItems().getScope().getValue();
		String skuCode = itemIdentifier.getItemId().getValue().get(ItemRepository.SKU_CODE_KEY);
		return promotionRepository.getPossiblePromotionsForItem(scope, skuCode)
				.map(possiblePromotion -> buildPromotionIdentifier(scope, possiblePromotion));
	}

	@Reference
	public void setPromotionRepository(final PromotionRepository promotionRepository) {
		this.promotionRepository = promotionRepository;
	}
}
