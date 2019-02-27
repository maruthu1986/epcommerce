/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.promotions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.rules.AppliedRule;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.impl.AppliedRuleImpl;
import com.elasticpath.domain.shoppingcart.PromotionRecordContainer;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.promotions.PromotionEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.price.PriceRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRuleMatcher;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.promotions.applied.AppliedPromotionTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl.ReactiveAdapterImpl;
import com.elasticpath.service.rules.RuleService;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * Unit Tests for {@link PromotionRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PromotionRepositoryImplTest {

	private static final String EXPECTED_RULE_CODE = "testRuleCode";
	private static final Long EXPECTED_RULE_ID = 0L;
	private static final String EXPECTED_PROMOTION_ID = "testPromotionId";
	private static final String ITEM_ID = "ITEM_ID";

	private static final String PROMOTION_ID_THAT_EXISTS = "promoExists";
	private static final String PROMOTION_ID_THAT_NOT_EXISTS = "promoNotExists";
	private static final String PURCHASE_ID = "purchaseId";
	private static final String SCOPE = "mobee";

	@Mock
	private Order order;
	@Mock
	private AppliedRuleImpl appliedRule;
	@Mock
	private PromotionEntity promotionEntity;
	@Mock
	private OrderRepository orderRepository;
	@Mock
	private AppliedPromotionTransformer appliedPromotionTransformer;
	@Mock
	private RuleService ruleService;
	@Mock
	private PromotionRuleMatcher<Long, Rule> cartPromotionRuleMatcher;
	@Mock
	private PromotionRuleMatcher<AppliedRule, AppliedRule> orderPromotionRuleMatcher;
	@Mock
	private PriceRepository priceRepository;
	private PromotionRepositoryImpl promotionRepository;
	@Mock
	private ShoppingCart mockShoppingCart;
	@Mock
	private ShoppingCartPricingSnapshot pricingSnapshot;
	@InjectMocks
	private ReactiveAdapterImpl reactiveAdapter;

	@Before
	public void setUp() {
		promotionRepository =
				new PromotionRepositoryImpl(ruleService, cartPromotionRuleMatcher, orderPromotionRuleMatcher, priceRepository, reactiveAdapter,
						orderRepository, appliedPromotionTransformer);
	}

	@Test
	public void testGetAppliedPromotionsForCouponWithShoppingCart() {
		Coupon mockCoupon = mock(Coupon.class);
		Collection<String> expectedRules = new ArrayList<>();
		when(cartPromotionRuleMatcher.findMatchingAppliedRules(any(), any())).thenReturn(expectedRules);

		Collection<String> appliedPromos
				= promotionRepository.getAppliedPromotionsForCoupon(pricingSnapshot, mockCoupon);

		assertThat(appliedPromos).isEqualTo(expectedRules);
	}

	@Test
	public void testGetAppliedPromotionsForCouponWithOrder() {
		Order mockOrder = mock(Order.class);
		Coupon mockCoupon = mock(Coupon.class);
		Collection<String> expectedRules = new ArrayList<>();
		when(orderPromotionRuleMatcher.findMatchingAppliedRules(any(), any())).thenReturn(expectedRules);

		Collection<String> appliedPromos = promotionRepository.getAppliedPromotionsForCoupon(mockOrder, mockCoupon);

		assertThat(appliedPromos).isEqualTo(expectedRules);
	}


	@Test
	public void testGetAppliedPromotionsForPurchase() {
		Order mockOrder = mock(Order.class);
		Collection<String> expectedRules = new ArrayList<>();
		when(orderPromotionRuleMatcher.findMatchingAppliedRules(any(), any())).thenReturn(expectedRules);

		Collection<String> appliedPromos = promotionRepository.getAppliedPromotionsForPurchase(mockOrder);

		assertThat(appliedPromos).isEqualTo(expectedRules);
	}

	@Test
	public void testGetAppliedCartLineitemPromotionsReturnsPromotionIds() {
		String lineItemId = "testLineItemId";
		when(cartPromotionRuleMatcher.findMatchingAppliedRules(any(), any())).thenReturn(Collections.singletonList(EXPECTED_PROMOTION_ID));
		mockLineItemAppliedRules(lineItemId);

		Collection<String> appliedCartPromotions =
				promotionRepository.getAppliedCartLineitemPromotions(mockShoppingCart, pricingSnapshot, lineItemId);

		assertThat(appliedCartPromotions).containsExactly(EXPECTED_PROMOTION_ID);
	}

	@Test
	public void testGetAppliedCartPromotionsReturnsPromotionIds() {
		when(cartPromotionRuleMatcher.findMatchingAppliedRules(any(), any())).thenReturn(Collections.singletonList(EXPECTED_PROMOTION_ID));

		Collection<String> appliedCartPromotions = promotionRepository.getAppliedCartPromotions(pricingSnapshot);

		assertThat(appliedCartPromotions).containsExactly(EXPECTED_PROMOTION_ID);
	}

	@Test
	public void testSingleAppliedShippingPromotion() {
		ShoppingCartPricingSnapshot shoppingCartPricingSnapshot = mock(ShoppingCartPricingSnapshot.class);
		ShippingOption shippingOption = mock(ShippingOption.class);
		when(cartPromotionRuleMatcher.findMatchingAppliedRules(any(), any())).thenReturn(Collections.singletonList(EXPECTED_RULE_CODE));
		Collection<String> appliedShippingPromotions
				= promotionRepository.getAppliedShippingPromotions(shoppingCartPricingSnapshot, shippingOption);
		assertThat(appliedShippingPromotions).contains(EXPECTED_RULE_CODE);
	}

	@Test
	public void testNoAppliedShippingPromotions() {
		ShoppingCartPricingSnapshot shoppingCartPricingSnapshot = mock(ShoppingCartPricingSnapshot.class);
		ShippingOption shippingOption = mock(ShippingOption.class);
		when(cartPromotionRuleMatcher.findMatchingAppliedRules(any(), any())).thenReturn(Collections.emptyList());
		Collection<String> appliedShippingPromotions
				= promotionRepository.getAppliedShippingPromotions(shoppingCartPricingSnapshot, shippingOption);
		assertThat(appliedShippingPromotions).doesNotContain(EXPECTED_RULE_CODE);
	}

	@Test
	public void testGetAppliedItemPromotionsWhenPromotionApplied() {
		String scope = "mobee";

		HashSet<Long> rules = new HashSet<>();
		rules.add(EXPECTED_RULE_ID);
		when(priceRepository.getLowestPriceRules(eq(scope), eq(ITEM_ID))).thenReturn(Single.just(rules));
		when(ruleService.findCodesByUids(rules)).thenReturn(Collections.singletonList(EXPECTED_PROMOTION_ID));

		promotionRepository.getAppliedPromotionsForItem(scope, ITEM_ID)
				.test()
				.assertNoErrors()
				.assertValue(EXPECTED_PROMOTION_ID);
	}

	@Test
	public void testGetAppliedItemPromotionsWhenNoPromotionApplied() {
		String scope = "mobee";
		when(priceRepository.getLowestPriceRules(eq(scope), eq(ITEM_ID)))
				.thenReturn(Single.just(Collections.emptySet()));

		promotionRepository.getAppliedPromotionsForItem(scope, ITEM_ID)
				.test()
				.assertNoErrors()
				.assertNoValues();
	}

	private void mockLineItemAppliedRules(final String lineItemId) {
		PromotionRecordContainer mockPromotionRecordContainer = mock(PromotionRecordContainer.class);
		when(pricingSnapshot.getPromotionRecordContainer()).thenReturn(mockPromotionRecordContainer);
		Collection<Long> ruleIds = Collections.singletonList(EXPECTED_RULE_ID);
		when(mockPromotionRecordContainer.getAppliedRulesByLineItem(lineItemId)).thenReturn(ruleIds);
	}

	private void mockGetPromotionDependencies() {
		when(orderRepository.findByGuidAsSingle(SCOPE, PURCHASE_ID)).thenReturn(Single.just(order));
		when(appliedPromotionTransformer.transformToEntity(appliedRule)).thenReturn(promotionEntity);
		when(appliedRule.getGuid()).thenReturn(PROMOTION_ID_THAT_EXISTS);

		Set<AppliedRule> appliedRules = new HashSet<>();
		appliedRules.add(appliedRule);
		when(order.getAppliedRules()).thenReturn(appliedRules);
	}

	@Test
	public void testFindOneWithPromotionIdThatExists() {
		mockGetPromotionDependencies();
		promotionRepository.getPromotionEntity(SCOPE, PURCHASE_ID, PROMOTION_ID_THAT_EXISTS)
				.test()
				.assertNoErrors()
				.assertValue(promotionEntity);
	}

	@Test
	public void testFindOneWithPromotionIdThatDoesNotExist() {
		mockGetPromotionDependencies();
		promotionRepository.getPromotionEntity(SCOPE, PURCHASE_ID, PROMOTION_ID_THAT_NOT_EXISTS)
				.test()
				.assertError(ResourceOperationFailure.notFound());
	}
}
