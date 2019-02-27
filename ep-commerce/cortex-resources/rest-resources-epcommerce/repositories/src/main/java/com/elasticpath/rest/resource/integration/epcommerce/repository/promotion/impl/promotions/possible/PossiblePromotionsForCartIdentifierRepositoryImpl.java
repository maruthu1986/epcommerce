/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.promotions.possible;

import io.reactivex.Observable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.promotions.PossiblePromotionsForCartIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRepository;

/**
 * Repository that checks if there are possible promotions for a cart.
 *
 * @param <I> extends CartIdentifier
 * @param <IE> extends PossiblePromotionsForCartIdentifier
 */
@Component
public class PossiblePromotionsForCartIdentifierRepositoryImpl<I extends CartIdentifier, IE extends PossiblePromotionsForCartIdentifier> implements
		LinksRepository<CartIdentifier, PossiblePromotionsForCartIdentifier> {

	private PromotionRepository promotionRepository;

	@Override
	public Observable<PossiblePromotionsForCartIdentifier> getElements(final CartIdentifier identifier) {
		String scope = identifier.getScope().getValue();
		String cartId = identifier.getCartId().getValue();
		return promotionRepository.cartHasPossiblePromotions(scope, cartId)
				.flatMapObservable(exists ->
						exists ? Observable.just(PossiblePromotionsForCartIdentifier.builder().withCart(identifier).build()) : Observable.empty());
	}

	@Reference
	public void setPromotionRepository(final PromotionRepository promotionRepository) {
		this.promotionRepository = promotionRepository;
	}
}
