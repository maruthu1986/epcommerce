/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.promotions.applied;

import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ErrorCheckPredicate.createErrorCheckPredicate;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionTestFactory
		.buildAppliedPromotionsForPurchaseCouponIdentifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForPurchaseCouponIdentifier;
import com.elasticpath.rest.definition.promotions.PurchaseCouponPromotionIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.coupon.CouponRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRepository;

/**
 * Test for {@link AppliedPromotionsForPurchaseCouponRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class AppliedPromotionsForPurchaseCouponRepositoryImplTest {

	private static final int NUM_OF_ID = 2;

	private final AppliedPromotionsForPurchaseCouponIdentifier identifier = buildAppliedPromotionsForPurchaseCouponIdentifier();

	@Mock
	private Coupon coupon;

	@Mock
	private Order order;

	@InjectMocks
	private AppliedPromotionsForPurchaseCouponRepositoryImpl<AppliedPromotionsForPurchaseCouponIdentifier,
			PurchaseCouponPromotionIdentifier> repository;

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private CouponRepository couponRepository;

	@Mock
	private PromotionRepository promotionRepository;

	@Test
	public void verifyGetElementsReturnNotFoundWhenOrderNotFound() {
		when(orderRepository.findByGuidAsSingle(ResourceTestConstants.SCOPE, ResourceTestConstants.PURCHASE_ID))
				.thenReturn(Single.error(ResourceOperationFailure.notFound(ResourceTestConstants.NOT_FOUND)));

		repository.getElements(identifier)
				.test()
				.assertError(createErrorCheckPredicate(ResourceTestConstants.NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyGetElementsReturnNotFoundWhenCouponRepositoryReturnsNotFound() {
		when(orderRepository.findByGuidAsSingle(ResourceTestConstants.SCOPE, ResourceTestConstants.PURCHASE_ID)).thenReturn(Single.just(order));
		when(couponRepository.findByCouponCode(ResourceTestConstants.COUPON_CODE))
				.thenReturn(Single.error(ResourceOperationFailure.notFound(ResourceTestConstants.NOT_FOUND)));

		repository.getElements(identifier)
				.test()
				.assertError(createErrorCheckPredicate(ResourceTestConstants.NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyGetElementsReturnEmptyWhenNoPromotionsFound() {
		when(orderRepository.findByGuidAsSingle(ResourceTestConstants.SCOPE, ResourceTestConstants.PURCHASE_ID)).thenReturn(Single.just(order));
		when(couponRepository.findByCouponCode(ResourceTestConstants.COUPON_CODE)).thenReturn(Single.just(coupon));
		when(promotionRepository.getAppliedPromotionsForCoupon(order, coupon)).thenReturn(Collections.emptyList());

		repository.getElements(identifier)
				.test()
				.assertNoErrors()
				.assertNoValues();
	}

	@Test
	public void verifyGetElementsReturnPurchaseCouponPromotionIdentifiers() {
		List<String> appliedPromotions = new ArrayList<>();
		for (int i = 0; i < NUM_OF_ID; i++) {
			appliedPromotions.add(String.valueOf(i));
		}

		when(orderRepository.findByGuidAsSingle(ResourceTestConstants.SCOPE, ResourceTestConstants.PURCHASE_ID)).thenReturn(Single.just(order));
		when(couponRepository.findByCouponCode(ResourceTestConstants.COUPON_CODE)).thenReturn(Single.just(coupon));
		when(promotionRepository.getAppliedPromotionsForCoupon(order, coupon)).thenReturn(appliedPromotions);

		repository.getElements(identifier)
				.test()
				.assertNoErrors()
				.assertValueAt(0, purchaseCouponPromotionIdentifier -> purchaseCouponPromotionIdentifier.getPromotionId().getValue().equals("0"))
				.assertValueAt(1, purchaseCouponPromotionIdentifier -> purchaseCouponPromotionIdentifier.getPromotionId().getValue().equals("1"));
	}
}
