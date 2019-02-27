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
import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.promotions.PossiblePromotionsForCartIdentifier;
import com.elasticpath.rest.definition.promotions.PromotionIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRepository;

/**
 * Repository for reading possible line item promotions data from external systems.
 *
 * @param <I>	extends PossiblePromotionsForCartIdentifier
 * @param <IE>	extends PromotionIdentifier
 */
@Component
public class PossiblePromotionsForCartRepositoryImpl<I extends PossiblePromotionsForCartIdentifier, IE extends PromotionIdentifier> implements
		LinksRepository<PossiblePromotionsForCartIdentifier, PromotionIdentifier> {

	private PromotionRepository promotionRepository;

	@Override
	public Observable<PromotionIdentifier> getElements(final PossiblePromotionsForCartIdentifier identifier) {
		CartIdentifier cartIdentifier = identifier.getCart();
		String scope = cartIdentifier.getScope().getValue();
		String cartId = cartIdentifier.getCartId().getValue();
		return promotionRepository.getPossiblePromotionsForCart(scope, cartId)
				.map(possiblePromotions -> buildPromotionIdentifier(scope, possiblePromotions));
	}

	@Reference
	public void setPromotionRepository(final PromotionRepository promotionRepository) {
		this.promotionRepository = promotionRepository;
	}
}
