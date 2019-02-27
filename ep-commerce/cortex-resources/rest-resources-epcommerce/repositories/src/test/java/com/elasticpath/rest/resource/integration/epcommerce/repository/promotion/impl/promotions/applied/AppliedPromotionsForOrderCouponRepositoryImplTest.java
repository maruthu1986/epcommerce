/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.promotions.applied;

import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ErrorCheckPredicate.createErrorCheckPredicate;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionTestFactory
		.buildAppliedPromotionsForOrderCouponIdentifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForOrderCouponIdentifier;
import com.elasticpath.rest.definition.promotions.PromotionIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.coupon.CouponRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRepository;

/**
 * Test for {@link AppliedPromotionsForOrderCouponRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class AppliedPromotionsForOrderCouponRepositoryImplTest {

	private static final int NUM_OF_ID = 2;

	private final AppliedPromotionsForOrderCouponIdentifier identifier = buildAppliedPromotionsForOrderCouponIdentifier();

	@Mock
	private ShoppingCart shoppingCart;

	@Mock
	private ShoppingCartPricingSnapshot shoppingCartPricingSnapshot;

	@Mock
	private Coupon coupon;

	@InjectMocks
	private AppliedPromotionsForOrderCouponRepositoryImpl<AppliedPromotionsForOrderCouponIdentifier, PromotionIdentifier> repository;

	@Mock
	private CartOrderRepository cartOrderRepository;

	@Mock
	private PricingSnapshotRepository pricingSnapshotRepository;

	@Mock
	private CouponRepository couponRepository;

	@Mock
	private PromotionRepository promotionRepository;

	@Test
	public void verifyGetElementsReturnNotFoundWhenShoppingCartNotFound() {
		when(cartOrderRepository.getEnrichedShoppingCartSingle(ResourceTestConstants.SCOPE, ResourceTestConstants.ORDER_ID,
				CartOrderRepository.FindCartOrder.BY_ORDER_GUID))
				.thenReturn(Single.error(ResourceOperationFailure.notFound(ResourceTestConstants.NOT_FOUND)));

		repository.getElements(identifier)
				.test()
				.assertError(createErrorCheckPredicate(ResourceTestConstants.NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyGetElementsReturnNotFoundWhenPricingSnapshotNotFound() {
		when(cartOrderRepository.getEnrichedShoppingCartSingle(ResourceTestConstants.SCOPE, ResourceTestConstants.ORDER_ID,
				CartOrderRepository.FindCartOrder.BY_ORDER_GUID)).thenReturn(Single.just(shoppingCart));
		when(pricingSnapshotRepository.getShoppingCartPricingSnapshotSingle(shoppingCart))
				.thenReturn(Single.error(ResourceOperationFailure.notFound(ResourceTestConstants.NOT_FOUND)));

		repository.getElements(identifier)
				.test()
				.assertError(createErrorCheckPredicate(ResourceTestConstants.NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyGetElementsReturnNotFoundWhenCouponNotFound() {
		when(cartOrderRepository.getEnrichedShoppingCartSingle(ResourceTestConstants.SCOPE, ResourceTestConstants.ORDER_ID,
				CartOrderRepository.FindCartOrder.BY_ORDER_GUID)).thenReturn(Single.just(shoppingCart));
		when(pricingSnapshotRepository.getShoppingCartPricingSnapshotSingle(shoppingCart))
				.thenReturn(Single.just(shoppingCartPricingSnapshot));
		when(couponRepository.findByCouponCode(ResourceTestConstants.COUPON_CODE))
				.thenReturn(Single.error(ResourceOperationFailure.notFound(ResourceTestConstants.NOT_FOUND)));

		repository.getElements(identifier)
				.test()
				.assertError(createErrorCheckPredicate(ResourceTestConstants.NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyGetElementsReturnEmptyWhenNoPromotionsAreFound() {
		when(cartOrderRepository.getEnrichedShoppingCartSingle(ResourceTestConstants.SCOPE, ResourceTestConstants.ORDER_ID,
				CartOrderRepository.FindCartOrder.BY_ORDER_GUID)).thenReturn(Single.just(shoppingCart));
		when(pricingSnapshotRepository.getShoppingCartPricingSnapshotSingle(shoppingCart))
				.thenReturn(Single.just(shoppingCartPricingSnapshot));
		when(couponRepository.findByCouponCode(ResourceTestConstants.COUPON_CODE)).thenReturn(Single.just(coupon));
		when(promotionRepository.getAppliedPromotionsForCoupon(shoppingCartPricingSnapshot, coupon)).thenReturn(Collections.emptyList());

		repository.getElements(identifier)
				.test()
				.assertNoErrors()
				.assertNoValues();
	}

	@Test
	public void verifyGetElementsReturnPromotionIdentifiers() {
		List<String> appliedPromotions = new ArrayList<>();
		for (int i = 0; i < NUM_OF_ID; i++) {
			appliedPromotions.add(String.valueOf(i));
		}

		when(cartOrderRepository.getEnrichedShoppingCartSingle(ResourceTestConstants.SCOPE, ResourceTestConstants.ORDER_ID,
				CartOrderRepository.FindCartOrder.BY_ORDER_GUID)).thenReturn(Single.just(shoppingCart));
		when(pricingSnapshotRepository.getShoppingCartPricingSnapshotSingle(shoppingCart))
				.thenReturn(Single.just(shoppingCartPricingSnapshot));
		when(couponRepository.findByCouponCode(ResourceTestConstants.COUPON_CODE)).thenReturn(Single.just(coupon));
		when(promotionRepository.getAppliedPromotionsForCoupon(shoppingCartPricingSnapshot, coupon)).thenReturn(appliedPromotions);

		repository.getElements(identifier)
				.test()
				.assertNoErrors()
				.assertValueAt(0, promotionIdentifier -> promotionIdentifier.getPromotionId().getValue().equals("0"))
				.assertValueAt(1, promotionIdentifier -> promotionIdentifier.getPromotionId().getValue().equals("1"));
	}
}
