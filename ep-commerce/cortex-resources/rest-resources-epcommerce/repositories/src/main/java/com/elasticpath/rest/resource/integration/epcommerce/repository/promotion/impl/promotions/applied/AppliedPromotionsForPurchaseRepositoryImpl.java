/*
 *  Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.promotions.applied;

import java.util.Collection;

import io.reactivex.Observable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForPurchaseIdentifier;
import com.elasticpath.rest.definition.promotions.PurchasePromotionIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRepository;

/**
 * Repository that implements reading appliedpromotios for purchase.
 *
 * @param <I> extends AppliedPromotionsForPurchaseIdentifier
 * @param <IE> extends PurchasePromotionIdentifier
 */
@Component
public class AppliedPromotionsForPurchaseRepositoryImpl<I extends AppliedPromotionsForPurchaseIdentifier, IE extends PurchasePromotionIdentifier>
		implements LinksRepository<AppliedPromotionsForPurchaseIdentifier, PurchasePromotionIdentifier> {

	private OrderRepository orderRepository;
	private PromotionRepository promotionRepository;

	@Override
	public Observable<PurchasePromotionIdentifier> getElements(final AppliedPromotionsForPurchaseIdentifier identifier) {
		String scope = identifier.getPurchase().getPurchases().getScope().getValue();
		String purchaseId = identifier.getPurchase().getPurchaseId().getValue();
		PurchaseIdentifier purchaseIdentifier = identifier.getPurchase();
		return orderRepository.findByGuidAsSingle(scope, purchaseId)
				.map(order -> promotionRepository.getAppliedPromotionsForPurchase(order))
				.flatMapObservable(appliedPromotions -> buildPurchasePromotionIdentifiers(purchaseIdentifier, appliedPromotions));
	}

	private Observable<PurchasePromotionIdentifier> buildPurchasePromotionIdentifiers(final PurchaseIdentifier purchaseIdentifier,
																					  final Collection<String> appliedPromotions) {
		return Observable.fromIterable(appliedPromotions)
				.map(appliedPromotion -> buildPurchasePromotionIdentifier(purchaseIdentifier, appliedPromotion));
	}

	private PurchasePromotionIdentifier buildPurchasePromotionIdentifier(final PurchaseIdentifier purchaseIdentifier,
																		 final String appliedPromotion) {
		return PurchasePromotionIdentifier.builder()
				.withPromotionId(StringIdentifier.of(appliedPromotion))
				.withPurchase(purchaseIdentifier)
				.build();
	}

	@Reference
	public void setOrderRepository(final OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	@Reference
	public void setPromotionRepository(final PromotionRepository promotionRepository) {
		this.promotionRepository = promotionRepository;
	}

}
