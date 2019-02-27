/*
 *  Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.promotions.applied;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.promotions.PromotionIdentifierUtil
		.buildPromotionIdentifiers;

import java.util.Collection;

import io.reactivex.Observable;
import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForCartIdentifier;
import com.elasticpath.rest.definition.promotions.PromotionIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRepository;

/**
 * Repository for reading promotions for a cart.
 *
 * @param <I>	extends AppliedPromotionsForCartIdentifier
 * @param <IE>	extends PromotionIdentifier
 */
@Component
public class AppliedPromotionsForCartRepositoryImpl<I extends AppliedPromotionsForCartIdentifier, IE extends PromotionIdentifier> implements
		LinksRepository<AppliedPromotionsForCartIdentifier, PromotionIdentifier> {

	private PromotionRepository promotionRepository;
	private CartOrderRepository cartOrderRepository;
	private PricingSnapshotRepository pricingSnapshotRepository;

	@Override
	public Observable<PromotionIdentifier> getElements(final AppliedPromotionsForCartIdentifier identifier) {
		CartIdentifier cartIdentifier = identifier.getCart();
		String cartId = cartIdentifier.getCartId().getValue();
		String scope = cartIdentifier.getScope().getValue();
		return cartOrderRepository.getEnrichedShoppingCartSingle(scope, cartId, CartOrderRepository.FindCartOrder.BY_CART_GUID)
				.flatMap(this::getPromotionIdentifiersFromShoppingCart)
				.flatMapObservable(appliedPromotions -> buildPromotionIdentifiers(scope, appliedPromotions));
	}

	private Single<Collection<String>> getPromotionIdentifiersFromShoppingCart(final ShoppingCart shoppingCart) {
		return pricingSnapshotRepository.getShoppingCartPricingSnapshotSingle(shoppingCart)
				.map(pricingSnapshot -> promotionRepository.getAppliedCartPromotions(pricingSnapshot));
	}

	@Reference
	public void setPromotionRepository(final PromotionRepository promotionRepository) {
		this.promotionRepository = promotionRepository;
	}

	@Reference
	public void setCartOrderRepository(final CartOrderRepository cartOrderRepository) {
		this.cartOrderRepository = cartOrderRepository;
	}

	@Reference
	public void setPricingSnapshotRepository(final PricingSnapshotRepository pricingSnapshotRepository) {
		this.pricingSnapshotRepository = pricingSnapshotRepository;
	}

}
