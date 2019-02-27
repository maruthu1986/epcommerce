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
import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForCartLineItemIdentifier;
import com.elasticpath.rest.definition.promotions.PromotionIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRepository;

/**
 * Repository that implements reading promotions for cart lineitem.
 *
 * @param <I>	extends AppliedPromotionsForCartLineItemIdentifier
 * @param <LI>	extends PromotionIdentifier
 */
@Component
public class AppliedPromotionsForCartLineItemRepositoryImpl<I extends AppliedPromotionsForCartLineItemIdentifier, LI extends PromotionIdentifier>
		implements LinksRepository<AppliedPromotionsForCartLineItemIdentifier, PromotionIdentifier> {

	private PromotionRepository promotionRepository;
	private CartOrderRepository cartOrderRepository;
	private PricingSnapshotRepository pricingSnapshotRepository;

	@Override
	public Observable<PromotionIdentifier> getElements(final AppliedPromotionsForCartLineItemIdentifier identifier) {
		LineItemIdentifier lineItemIdentifier = identifier.getLineItem();
		String lineItemId = lineItemIdentifier.getLineItemId().getValue();
		CartIdentifier cartIdentifier = lineItemIdentifier.getLineItems().getCart();
		String scope = cartIdentifier.getScope().getValue();
		String cartId = cartIdentifier.getCartId().getValue();
		return cartOrderRepository.getEnrichedShoppingCartSingle(scope, cartId, CartOrderRepository.FindCartOrder.BY_CART_GUID)
				.flatMap(shoppingCart -> getAppliedPromotionIds(lineItemId, shoppingCart))
				.flatMapObservable(appliedPromotions -> buildPromotionIdentifiers(scope, appliedPromotions));
	}

	private Single<Collection<String>> getAppliedPromotionIds(final String lineItemId, final ShoppingCart shoppingCart) {
		return pricingSnapshotRepository.getShoppingCartPricingSnapshotSingle(shoppingCart)
				.map(pricingSnapshot -> promotionRepository.getAppliedCartLineitemPromotions(shoppingCart, pricingSnapshot, lineItemId));
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
