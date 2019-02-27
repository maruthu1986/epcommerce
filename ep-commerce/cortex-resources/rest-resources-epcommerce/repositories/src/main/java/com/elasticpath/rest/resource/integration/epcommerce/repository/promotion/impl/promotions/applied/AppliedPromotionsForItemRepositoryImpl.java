/*
 *  Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.promotions.applied;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.promotions.PromotionIdentifierUtil
		.buildPromotionIdentifier;

import io.reactivex.Observable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForItemIdentifier;
import com.elasticpath.rest.definition.promotions.PromotionIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRepository;

/**
 * Repository for reading promotions for an item.
 *
 * @param <I>	extends AppliedPromotionsForItemIdentifier
 * @param <IE>	extends PromotionIdentifier
 */
@Component
public class AppliedPromotionsForItemRepositoryImpl<I extends AppliedPromotionsForItemIdentifier, IE extends PromotionIdentifier> implements
		LinksRepository<AppliedPromotionsForItemIdentifier, PromotionIdentifier> {

	private PromotionRepository promotionRepository;

	@Override
	public Observable<PromotionIdentifier> getElements(final AppliedPromotionsForItemIdentifier identifier) {
		String itemId = identifier.getItem().getItemId().getValue().get(ItemRepository.SKU_CODE_KEY);
		String scope = identifier.getItem().getItems().getScope().getValue();
		return promotionRepository.getAppliedPromotionsForItem(scope, itemId)
						.map(appliedPromotion -> buildPromotionIdentifier(scope, appliedPromotion));
	}

	@Reference
	public void setPromotionRepository(final PromotionRepository promotionRepository) {
		this.promotionRepository = promotionRepository;
	}
}
