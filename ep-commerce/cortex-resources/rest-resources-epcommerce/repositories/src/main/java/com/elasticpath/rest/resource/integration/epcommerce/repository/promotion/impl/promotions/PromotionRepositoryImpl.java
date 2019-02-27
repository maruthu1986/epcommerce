/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.promotions;

import java.util.Collection;
import java.util.Collections;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.Single;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.rules.AppliedRule;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.shoppingcart.PromotionRecordContainer;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.definition.promotions.PromotionEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.price.PriceRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRuleMatcher;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.promotions.applied.AppliedPromotionTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.rules.awares.AppliedPromotionRuleAwareOrderAdapter;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.rules.awares.AppliedPromotionRuleAwareShippingOptionAdapter;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.rules.awares
		.AppliedPromotionRuleAwareShoppingCartPricingSnapshotAdapter;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.rules.predicates.AppliedShippingRulePredicate;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.rules.predicates.CartLineItemRulePredicate;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.rules.predicates.CartRulePredicate;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.rules.predicates.CouponAppliedRulePredicate;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.rules.predicates.CouponRulePredicate;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.rules.predicates.PurchaseAppliedRulePredicate;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;
import com.elasticpath.rest.util.collection.CollectionUtil;
import com.elasticpath.service.rules.RuleService;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * The facade for Coupon related operations.
 */
@Singleton
@Named("promotionRepository")
public class PromotionRepositoryImpl implements PromotionRepository {

	private static final Logger LOG = LoggerFactory.getLogger(PromotionRepositoryImpl.class);
	/**
	 * Error message when rule not found.
	 */
	public static final String RULE_NOT_FOUND = "Rule not found with id = '%s'";
	/**
	 * Error message when server error occured.
	 */
	public static final String SERVER_ERROR_OCCURED = "Server error occurred when searching for promotion id = '%s'";

	private final RuleService ruleService;
	private final PromotionRuleMatcher<Long, Rule> cartPromotionRuleMatcher;
	private final PromotionRuleMatcher<AppliedRule, AppliedRule> orderPromotionRuleMatcher;
	private final PriceRepository priceRepository;
	private final ReactiveAdapter reactiveAdapter;
	private final OrderRepository orderRepository;
	private final AppliedPromotionTransformer appliedPromotionTransformer;

	/**
	 * Look ma', a Constructor!
	 *
	 * @param ruleService                 rule service
	 * @param cartPromotionRuleMatcher    the cart promotion rule matcher
	 * @param orderPromotionRuleMatcher   the order promotion rule matcher
	 * @param priceRepository             the price repository
	 * @param reactiveAdapter             the reactive adapter
	 * @param orderRepository             the order repository
	 * @param appliedPromotionTransformer applied promotion transformer
	 */
	@Inject
	public PromotionRepositoryImpl(
			@Named("ruleService")
			final RuleService ruleService,
			@Named("cartPromotionRuleMatcher")
			final PromotionRuleMatcher<Long, Rule> cartPromotionRuleMatcher,
			@Named("orderPromotionRuleMatcher")
			final PromotionRuleMatcher<AppliedRule, AppliedRule> orderPromotionRuleMatcher,
			@Named("priceRepository")
			final PriceRepository priceRepository,
			@Named("reactiveAdapter")
			final ReactiveAdapter reactiveAdapter,
			@Named("orderRepository")
			final OrderRepository orderRepository,
			@Named("appliedPromotionTransformer")
			final AppliedPromotionTransformer appliedPromotionTransformer) {

		this.ruleService = ruleService;
		this.cartPromotionRuleMatcher = cartPromotionRuleMatcher;
		this.orderPromotionRuleMatcher = orderPromotionRuleMatcher;
		this.priceRepository = priceRepository;
		this.reactiveAdapter = reactiveAdapter;
		this.orderRepository = orderRepository;
		this.appliedPromotionTransformer = appliedPromotionTransformer;
	}

	@Override
	@CacheResult
	public Collection<String> getAppliedShippingPromotions(
			final ShoppingCartPricingSnapshot pricingSnapshot, final ShippingOption shippingOption) {
		AppliedShippingRulePredicate rulePredicate = new AppliedShippingRulePredicate(pricingSnapshot, shippingOption);
		AppliedPromotionRuleAwareShippingOptionAdapter shippingOptionAdapter
				= new AppliedPromotionRuleAwareShippingOptionAdapter(pricingSnapshot, shippingOption);
		return cartPromotionRuleMatcher.findMatchingAppliedRules(shippingOptionAdapter, rulePredicate);
	}

	@Override
	@CacheResult
	public Collection<String> getAppliedPromotionsForCoupon(final ShoppingCartPricingSnapshot pricingSnapshot, final Coupon coupon) {
		CouponRulePredicate rulePredicate = new CouponRulePredicate(coupon);
		AppliedPromotionRuleAwareShoppingCartPricingSnapshotAdapter ruleAwareShoppingCart =
				new AppliedPromotionRuleAwareShoppingCartPricingSnapshotAdapter(pricingSnapshot);
		return cartPromotionRuleMatcher.findMatchingAppliedRules(ruleAwareShoppingCart, rulePredicate);
	}

	@Override
	@CacheResult
	public Collection<String> getAppliedPromotionsForCoupon(final Order order, final Coupon coupon) {
		CouponAppliedRulePredicate rulePredicate = new CouponAppliedRulePredicate(coupon);
		AppliedPromotionRuleAwareOrderAdapter ruleAwareOrder = new AppliedPromotionRuleAwareOrderAdapter(order);
		return orderPromotionRuleMatcher.findMatchingAppliedRules(ruleAwareOrder, rulePredicate);
	}

	@Override
	@CacheResult
	public Collection<String> getAppliedPromotionsForPurchase(final Order order) {
		PurchaseAppliedRulePredicate rulePredicate = new PurchaseAppliedRulePredicate();
		AppliedPromotionRuleAwareOrderAdapter ruleAwareOrder = new AppliedPromotionRuleAwareOrderAdapter(order);
		return orderPromotionRuleMatcher.findMatchingAppliedRules(ruleAwareOrder, rulePredicate);
	}

	@Override
	@CacheResult
	public Single<Rule> findByPromotionId(final String promotionId) {
		return reactiveAdapter.fromServiceAsSingle(() -> ruleService.findByRuleCode(promotionId), String.format(RULE_NOT_FOUND, promotionId))
				.onErrorResumeNext(throwable -> getError(promotionId, throwable));
	}

	private Single<Rule> getError(final String promotionId, final Throwable throwable) {
		if (throwable instanceof EpServiceException) {
			LOG.warn("An exception occurred when searching for a Rule by promotion id: " + promotionId, throwable);
			return Single.error(ResourceOperationFailure.serverError(String.format(SERVER_ERROR_OCCURED, promotionId)));
		}
		return Single.error(throwable);
	}

	@Override
	@CacheResult
	public Observable<String> getAppliedPromotionsForItem(final String storeCode, final String skuCode) {
		return priceRepository.getLowestPriceRules(storeCode, skuCode)
				.flatMapObservable(ruleIds -> Observable.fromIterable(ruleService.findCodesByUids(ruleIds)));
	}

	@Override
	@CacheResult
	public Collection<String> getAppliedCartLineitemPromotions(final ShoppingCart shoppingCart,
																final ShoppingCartPricingSnapshot pricingSnapshot,
																final String lineItemId) {
		PromotionRecordContainer promotionRecordContainer = pricingSnapshot.getPromotionRecordContainer();
		Collection<Long> appliedRuleIds = promotionRecordContainer.getAppliedRulesByLineItem(lineItemId);

		if (CollectionUtil.isEmpty(appliedRuleIds)) {
			return Collections.emptyList();
		}

		CartLineItemRulePredicate rulePredicate = new CartLineItemRulePredicate(appliedRuleIds);
		AppliedPromotionRuleAwareShoppingCartPricingSnapshotAdapter ruleAwareShoppingCart =
				new AppliedPromotionRuleAwareShoppingCartPricingSnapshotAdapter(pricingSnapshot);
		return cartPromotionRuleMatcher.findMatchingAppliedRules(ruleAwareShoppingCart, rulePredicate);
	}

	@Override
	@CacheResult
	public Collection<String> getAppliedCartPromotions(final ShoppingCartPricingSnapshot pricingSnapshot) {
		CartRulePredicate rulePredicate = new CartRulePredicate();
		AppliedPromotionRuleAwareShoppingCartPricingSnapshotAdapter ruleAwareShoppingCart =
				new AppliedPromotionRuleAwareShoppingCartPricingSnapshotAdapter(pricingSnapshot);
		return cartPromotionRuleMatcher.findMatchingAppliedRules(ruleAwareShoppingCart, rulePredicate);
	}

	@Override
	@CacheResult
	public Single<PromotionEntity> getPromotionEntity(final String scope, final String purchaseId, final String promotionId) {
		return orderRepository.findByGuidAsSingle(scope, purchaseId)
				.flatMap(order -> getPromotionEntity(order, promotionId));
	}

	private Single<PromotionEntity> getPromotionEntity(final Order order, final String promotionId) {
		for (AppliedRule appliedRule : order.getAppliedRules()) {
			if (appliedRule.getGuid().equals(promotionId)) {
				return Single.just(appliedPromotionTransformer.transformToEntity(appliedRule));
			}
		}
		return Single.error(ResourceOperationFailure.notFound());
	}

	@Override
	public Single<Boolean> itemHasPossiblePromotions(final String scope, final String skuCode) {
		return Single.just(false);
	}

	@Override
	public Single<Boolean> cartHasPossiblePromotions(final String scope, final String cartId) {
		return Single.just(false);
	}

	@Override
	public Observable<String> getPossiblePromotionsForItem(final String scope, final String skuCode) {
		return Observable.error(new ResourceOperationFailure("", null, ResourceStatus.NOT_IMPLEMENTED));
	}

	@Override
	public Observable<String> getPossiblePromotionsForCart(final String scope, final String cartId) {
		return Observable.error(new ResourceOperationFailure("", null, ResourceStatus.NOT_IMPLEMENTED));
	}
}
