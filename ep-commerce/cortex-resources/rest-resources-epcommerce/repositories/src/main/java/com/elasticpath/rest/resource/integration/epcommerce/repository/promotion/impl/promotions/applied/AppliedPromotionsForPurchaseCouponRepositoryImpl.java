/*
 *  Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.promotions.applied;

import java.util.Collection;

import io.reactivex.Observable;
import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.order.Order;
import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.coupons.PurchaseCouponIdentifier;
import com.elasticpath.rest.definition.coupons.PurchaseCouponListIdentifier;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForPurchaseCouponIdentifier;
import com.elasticpath.rest.definition.promotions.PurchaseCouponPromotionIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.coupon.CouponRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRepository;

/**
 * Repository that implements reading applied promotions for purchase coupon.
 *
 * @param <I>	extends AppliedPromotionsForPurchaseCouponIdentifier
 * @param <IE>	extends PurchaseCouponPromotionIdentifier
 */
@Component
public class AppliedPromotionsForPurchaseCouponRepositoryImpl<I extends AppliedPromotionsForPurchaseCouponIdentifier,
		IE extends PurchaseCouponPromotionIdentifier> implements LinksRepository<AppliedPromotionsForPurchaseCouponIdentifier,
		PurchaseCouponPromotionIdentifier> {

	private OrderRepository orderRepository;
	private CouponRepository couponRepository;
	private PromotionRepository promotionRepository;

	@Override
	public Observable<PurchaseCouponPromotionIdentifier> getElements(final AppliedPromotionsForPurchaseCouponIdentifier identifier) {
		PurchaseCouponIdentifier purchaseCouponIdentifier = identifier.getPurchaseCoupon();
		String couponId = purchaseCouponIdentifier.getCouponId().getValue();
		PurchaseCouponListIdentifier purchaseCouponListIdentifier = purchaseCouponIdentifier.getPurchaseCouponList();
		String scope = purchaseCouponListIdentifier.getPurchase().getPurchases().getScope().getValue();
		String purchaseId = purchaseCouponListIdentifier.getPurchase().getPurchaseId().getValue();
		return orderRepository.findByGuidAsSingle(scope, purchaseId)
				.flatMap(order -> getAppliedPromotions(couponId, order))
				.flatMapObservable(appliedPromotions -> buildPurchaseCouponPromotionIdentifiers(purchaseCouponIdentifier, appliedPromotions));
	}

	private Single<Collection<String>> getAppliedPromotions(final String couponId, final Order order) {
		return couponRepository.findByCouponCode(couponId)
			.map(coupon -> promotionRepository.getAppliedPromotionsForCoupon(order, coupon));
	}

	private Observable<PurchaseCouponPromotionIdentifier> buildPurchaseCouponPromotionIdentifiers(
			final PurchaseCouponIdentifier purchaseCouponIdentifier,
			final Collection<String> appliedPromotions) {
		return Observable.fromIterable(appliedPromotions)
				.map(appliedPromotion -> buildPurchaseCouponPromotionIdentifier(purchaseCouponIdentifier, appliedPromotion));
	}

	private PurchaseCouponPromotionIdentifier buildPurchaseCouponPromotionIdentifier(final PurchaseCouponIdentifier purchaseCouponIdentifier,
																					 final String appliedPromotion) {
		return PurchaseCouponPromotionIdentifier.builder()
				.withPromotionId(StringIdentifier.of(appliedPromotion))
				.withPurchaseCoupon(purchaseCouponIdentifier)
				.build();
	}

	@Reference
	public void setOrderRepository(final OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	@Reference
	public void setCouponRepository(final CouponRepository couponRepository) {
		this.couponRepository = couponRepository;
	}

	@Reference
	public void setPromotionRepository(final PromotionRepository promotionRepository) {
		this.promotionRepository = promotionRepository;
	}

}
