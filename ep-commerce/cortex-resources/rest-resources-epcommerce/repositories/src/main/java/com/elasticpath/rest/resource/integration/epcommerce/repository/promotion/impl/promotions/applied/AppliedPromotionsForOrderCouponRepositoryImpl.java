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
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForOrderCouponIdentifier;
import com.elasticpath.rest.definition.promotions.PromotionIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.coupon.CouponRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRepository;

/**
 * Repository that implements reading promotions for an order coupon.
 *
 * @param <I> extends AppliedPromotionsForOrderCouponIdentifier
 * @param <IE> extends PromotionIdentifier
 */
@Component
public class AppliedPromotionsForOrderCouponRepositoryImpl<I extends AppliedPromotionsForOrderCouponIdentifier, IE extends PromotionIdentifier>
	implements LinksRepository<AppliedPromotionsForOrderCouponIdentifier, PromotionIdentifier> {

	private CartOrderRepository cartOrderRepository;
	private PricingSnapshotRepository pricingSnapshotRepository;
	private PromotionRepository promotionRepository;
	private CouponRepository couponRepository;

	@Override
	public Observable<PromotionIdentifier> getElements(final AppliedPromotionsForOrderCouponIdentifier identifier) {
		String scope = identifier.getOrderCoupon().getOrder().getScope().getValue();
		String cartId = identifier.getOrderCoupon().getOrder().getOrderId().getValue();
		String couponId = identifier.getOrderCoupon().getCouponId().getValue();
		return cartOrderRepository.getEnrichedShoppingCartSingle(scope, cartId, CartOrderRepository.FindCartOrder.BY_ORDER_GUID)
				.flatMap(shoppingCart -> getAppliedPromotions(couponId, shoppingCart))
				.flatMapObservable(appliedPromotions -> buildPromotionIdentifiers(scope, appliedPromotions));
	}

	private Single<Collection<String>> getAppliedPromotions(final String couponId, final ShoppingCart shoppingCart) {
		return pricingSnapshotRepository.getShoppingCartPricingSnapshotSingle(shoppingCart)
				.flatMap(pricingSnapshot -> couponRepository.findByCouponCode(couponId)
						.map(coupon -> promotionRepository.getAppliedPromotionsForCoupon(pricingSnapshot, coupon)));
	}

	@Reference
	public void setCartOrderRepository(final CartOrderRepository cartOrderRepository) {
		this.cartOrderRepository = cartOrderRepository;
	}

	@Reference
	public void setPricingSnapshotRepository(final PricingSnapshotRepository pricingSnapshotRepository) {
		this.pricingSnapshotRepository = pricingSnapshotRepository;
	}

	@Reference
	public void setPromotionRepository(final PromotionRepository promotionRepository) {
		this.promotionRepository = promotionRepository;
	}

	@Reference
	public void setCouponRepository(final CouponRepository couponRepository) {
		this.couponRepository = couponRepository;
	}

}
