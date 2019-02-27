/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.shoppingcart.impl;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.shoppingcart.DiscountRecord;
import com.elasticpath.domain.shoppingcart.MutablePromotionRecordContainer;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartImpl;
import com.elasticpath.money.Money;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.rules.EpRuleEngine;
import com.elasticpath.service.shipping.transformers.PricedShippableItemContainerTransformer;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItemContainer;
import com.elasticpath.shipping.connectivity.dto.ShippingCalculationResult;
import com.elasticpath.shipping.connectivity.dto.ShippingCalculationResult.ErrorInformation;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;
import com.elasticpath.shipping.connectivity.service.ShippingCalculationService;

/**
 * Methods for getting a pricing snapshot.
 */
@SuppressWarnings("PMD.GodClass") // TODO this is a legitimate god class. Recommend refactoring as part of cart concurrency/perf initiative.
public class PricingSnapshotServiceImpl implements PricingSnapshotService {
	private static final Logger LOG = Logger.getLogger(PricingSnapshotServiceImpl.class);

	private EpRuleEngine ruleEngine;
	private BeanFactory beanFactory;
	private CartDirector cartDirector;
	private ProductSkuLookup productSkuLookup;
	private ShippingCalculationService shippingCalculationService;

	private Predicate<Throwable> shippingOptionResultExceptionLogPredicate;
	private PricedShippableItemContainerTransformer pricedShippableItemContainerTransformer;

	@Override
	public ShoppingCartPricingSnapshot getPricingSnapshotForCart(final ShoppingCart shoppingCart) {
		if (shoppingCart == null) {
			throw new IllegalArgumentException("ShoppingCart parameter must not be null.");
		}

		if (shoppingCart instanceof ShoppingCartImpl) {
			fireRules((ShoppingCartImpl) shoppingCart);

			return (ShoppingCartPricingSnapshot) shoppingCart;
		}

		throw new IllegalArgumentException(
				"This implementation expects ShoppingCart to be a ShoppingCartImpl.  "
						+ "Unexpected ShoppingCart type: [" + shoppingCart.getClass() + "]");
	}

	@Override
	public ShoppingItemPricingSnapshot getPricingSnapshotForOrderSku(final OrderSku orderSku) {
		return (ShoppingItemPricingSnapshot) orderSku;
	}

	/**
	 * <p>Forces the shopping cart to apply promotion rules. Promotion rules will usually be applied by the cart automatically as required.
	 * However, it is sometimes necessary to force the cart to fire rules when the cart is loaded without a state change.</p>
	 * <p>In case of exclusive tax calculation, the order discount is deducted before-tax; however, in case of inclusive tax calculation,
	 * the order discount is deducted after-tax.</p>
	 *
	 * @param shoppingCart the shopping cart
	 */
	protected void fireRules(final ShoppingCartImpl shoppingCart) {
		if (shoppingCart.isExchangeOrderShoppingCart()) {
			calculatePrePromotionShippingPrices(shoppingCart, shoppingCart);
		} else {
			shoppingCart.clearPromotions();

			final MutablePromotionRecordContainer promotionRecordContainer = shoppingCart.getMutablePromotionRecordContainer();
			promotionRecordContainer.clear();

			// Ensure catalog promotions are applied
			getCartDirector().refresh(shoppingCart);
			storeCatalogPromotionRecords(shoppingCart, shoppingCart, promotionRecordContainer);

			getRuleEngine().fireOrderPromotionRules(shoppingCart, shoppingCart.getCustomerSession());

			calculatePrePromotionShippingPrices(shoppingCart, shoppingCart);

			getRuleEngine().fireOrderPromotionSubtotalRules(shoppingCart, shoppingCart.getCustomerSession());

			// Discount Record Container contains two separate collections that denote rule application:
			// 1. The set of rules that were applicable to this cart/order (includes rules that were superseded by better promos)
			// 2. The set of rules that are flagged as Limited Usage, which have not exceeded the usage allocation, **even if not applicable to this
			// cart/order**.
			//
			// The limited usage rules are set on the cart from within a Rule Condition (LimitedUsagePromotionConditionImpl), but _other_ conditions
			// within the same rule may not match the cart, which means the rule should not actually apply.
			//
			// This block of code looks through the limited usage rules collection and removes anything that was never actually applied.
			//
			// This entire scenario is all rather non-obvious - if you weren't familiar with the effects of LimitedUsagePromotionConditionImpl (and
			// why would you be?), this will be quite confusing. This is definitely a candidate for refactoring.  Perhaps this complexity could be
			// concealed within the DiscountRecordContainer itself?
			for (final Map.Entry<String, Long> limitedUsageRuleEntry : promotionRecordContainer.getLimitedUsagePromotionRuleCodes().entrySet()) {
				if (!promotionRecordContainer.getAppliedRules().contains(limitedUsageRuleEntry.getValue())) {
					promotionRecordContainer.removeLimitedUsagePromotionRuleCode(limitedUsageRuleEntry.getKey());
				}
			}
		}

	}

	/**
	 * For each cart item, retrieves any and all {@link DiscountRecord} corresponding to a catalog promo (i.e. those stored on the
	 * {@link com.elasticpath.domain.catalog.Price Price}), and sets them to the given
	 * {@link MutablePromotionRecordContainer PromotionRecordContainer}.
	 *
	 * @param shoppingCart             the shopping cart
	 * @param cartPricingSnapshot      the pricing snapshot corresponding to the shopping cart
	 * @param promotionRecordContainer the promotion record container onto which the catalog discount records should be stored
	 */
	private void storeCatalogPromotionRecords(final ShoppingCart shoppingCart,
											  final ShoppingCartPricingSnapshot cartPricingSnapshot,
											  final MutablePromotionRecordContainer promotionRecordContainer) {
		for (final ShoppingItem shoppingItem : shoppingCart.getAllShoppingItems()) {
			final ShoppingItemPricingSnapshot shoppingItemPricingSnapshot = cartPricingSnapshot.getShoppingItemPricingSnapshot(shoppingItem);
			final Collection<DiscountRecord> discountRecords = shoppingItemPricingSnapshot.getPrice().getDiscountRecords();
			for (final DiscountRecord discountRecord : discountRecords) {
				promotionRecordContainer.addDiscountRecord(discountRecord);
			}
		}
	}

	/**
	 * Calculates the cost of each shipping option prior to applying shipping discounts.
	 *
	 * @param shoppingCart                the shopping cart
	 * @param shoppingCartPricingSnapshot the shopping cart pricing snapshot
	 */
	protected void calculatePrePromotionShippingPrices(final ShoppingCartImpl shoppingCart,
													   final ShoppingCartPricingSnapshot shoppingCartPricingSnapshot) {
		final List<ShippingOption> shippingOptionsRequested = getPricedShippingOptions(shoppingCart, shoppingCartPricingSnapshot);

		// Clear the current pricing before populating them with the latest retrieved prices if we were successful in retrieving them
		shoppingCart.clearShippingListPrices();

		// If shipping options were returned then we process them otherwise they weren't required so we can skip over them
		if (CollectionUtils.isNotEmpty(shippingOptionsRequested)) {
			shippingOptionsRequested.stream()
					.filter(shippingOption -> shippingOption.getShippingCost().isPresent())
					.forEach(shippingOption -> shoppingCart.setShippingListPrice(shippingOption.getCode(), shippingOption.getShippingCost().get()));
		}
	}

	/**
	 * Gets the priced shipping options for the given cart. Currently it calls
	 * {@link #getPricedShippingOptionsForAddress(ShoppingCart, ShoppingCartPricingSnapshot)} only if the shipping address has been set
	 * on the shopping cart, otherwise it returns {@link Optional#empty()}.
	 *
	 * @param shoppingCart                the shopping cart to get the priced shipping options for.
	 * @param shoppingCartPricingSnapshot the shopping cart pricing snapshot
	 * @return a list of priced shipping options if available, or {@link Optional#empty()} if shipping options are not available and this is deemed
	 * not fatal to the overall price calculation by {@link #handleShippingOptionsNotAvailable(ShoppingCart, ShippingCalculationResult)} .
	 */
	protected List<ShippingOption> getPricedShippingOptions(final ShoppingCart shoppingCart,
															final ShoppingCartPricingSnapshot shoppingCartPricingSnapshot) {

		if (shoppingCart.getShippingAddress() != null) {
			return getPricedShippingOptionsForAddress(shoppingCart, shoppingCartPricingSnapshot);
		}

		return emptyList();
	}

	/**
	 * Gets the priced shipping options for the given cart where the shipping address has been set on the shopping cart.
	 * If there was a problem retrieving the shipping options it delegates to
	 * {@link #handleShippingOptionsNotAvailable(ShoppingCart, ShippingCalculationResult)} to resolve the situation,
	 * see that method for more information.
	 *
	 * @param shoppingCart                the shopping cart to get the priced shipping options for.
	 * @param shoppingCartPricingSnapshot the shopping cart pricing snapshot.
	 * @return a list of priced shipping options
	 * not fatal to the overall price calculation by {@link #handleShippingOptionsNotAvailable(ShoppingCart, ShippingCalculationResult)}
	 */
	protected List<ShippingOption> getPricedShippingOptionsForAddress(final ShoppingCart shoppingCart,
																	  final ShoppingCartPricingSnapshot shoppingCartPricingSnapshot) {

		final PricedShippableItemContainer<?> pricedShippableItemContainer =
				getPricedShippableItemContainerTransformer().apply(shoppingCart, shoppingCartPricingSnapshot);

		final ShippingCalculationResult shippingOptionResult = getShippingCalculationService().getPricedShippingOptions(pricedShippableItemContainer);

		List<ShippingOption> shippingOptions;

		if (shippingOptionResult.isSuccessful()) {
			shippingOptions = shippingOptionResult.getAvailableShippingOptions();
		} else {
			shippingOptions = handleShippingOptionsNotAvailable(shoppingCart, shippingOptionResult);
		}

		return shippingOptions;
	}

	/**
	 * Handles the case where shipping options are not currently available.
	 * <p>
	 * By default if no shipping option is currently selected in the shopping cart then we just log an error and continue as this allows
	 * cart pricing to be returned when no shipping option has currently been selected even if no shipping options are currently available.
	 * <p>
	 * If there is a currently selected shipping option in the shopping cart, then we throw an
	 * {@link com.elasticpath.shipping.connectivity.exceptions.ShippingOptionServiceException} since we cannot calculate
	 * the price of the selected shipping option, so we have no choice.
	 *
	 * @param shoppingCart              the shopping cart to get the currently selected shipping option.
	 * @param shippingCalculationResult the unsuccessful shipping option result
	 * @return an empty list if there is not currently a selected shipping option on the cart.
	 */
	protected List<ShippingOption> handleShippingOptionsNotAvailable(final ShoppingCart shoppingCart,
																	 final ShippingCalculationResult shippingCalculationResult) {

		// Check if we should throw an exception or swallow and log an error instead
		// We will continue only if we don't have a selected shipping option, otherwise we will throw an exception
		final Optional<ShippingOption> selectedShippingOption = shoppingCart.getSelectedShippingOption();
		if (selectedShippingOption.isPresent()) {
			final String errorMessage = format("Shipping option '%s' is currently selected but no shipping option prices are currently available.",
					selectedShippingOption.get().getCode());
			selectedShippingOption.ifPresent(shippingOption -> shippingCalculationResult.throwException(
					errorMessage,
					singletonList(new StructuredErrorMessage(
							"shippingoptions.unavailable",
							errorMessage,
							ImmutableMap.of(
									"cart-id", shoppingCart.getGuid(),
									"shipping-option", selectedShippingOption.get().getCode())))));
		}

		final String logMessage = "Unable to retrieve available shipping options and prices for populating ShoppingCartPricingSnapshot. "
				+ "No shipping option is currently selected though, so we can still continue.";

		shippingCalculationResult.logError(LOG, logMessage, isErrorCauseLogged(shippingCalculationResult.getErrorInformation().orElse(null)));

		return emptyList();
	}

	/**
	 * Method which returns whether any available {@link Throwable} cause should be included in the error log message issued by
	 * {@link #handleShippingOptionsNotAvailable(ShoppingCart, ShippingCalculationResult)}.
	 * <p>
	 * Delegates to {@link #isErrorCauseLogged(Throwable)} for the final answer if there is a {@link Throwable} available for checking, otherwise
	 * returns {@code false}.
	 *
	 * @param errorInformation the optional error information attached to the {@link ShippingCalculationResult}.
	 * @return {@code true} if the cause's stacktrace should be included in the log message; {@code false} otherwise.
	 * @see #isErrorCauseLogged(Throwable) for the method that is delegated to.
	 */
	protected boolean isErrorCauseLogged(final ErrorInformation errorInformation) {
		if (errorInformation == null) {
			return false;
		}

		final Throwable cause = errorInformation.getCause().orElse(null);
		if (cause == null) {
			return false;
		}

		return isErrorCauseLogged(cause);
	}

	/**
	 * Method which returns whether the {@link Throwable} cause should be included in the error log message issued by
	 * {@link #handleShippingOptionsNotAvailable(ShoppingCart, ShippingCalculationResult)}.
	 * <p>
	 * For example certain exceptions such as {@link java.net.ConnectException} don't provide any extra information if the stack trace is logged
	 * but bloats logs so is worth suppressing stack traces for those exception types.
	 * <p>
	 * This method simply delegates to the {@link #getShippingOptionResultExceptionLogPredicate()} for its result.
	 *
	 * @param cause the non-null cause of the error.
	 * @return {@code #getShippingOptionResultExceptionLogPredicate()#test(Throwable)}.
	 * @see com.elasticpath.service.shipping.predicates.DefaultShippingOptionResultExceptionLogPredicateImpl for the default implementation.
	 */
	protected boolean isErrorCauseLogged(final Throwable cause) {
		return getShippingOptionResultExceptionLogPredicate().test(cause);
	}

	/**
	 * Calculate the discount for shipment total.
	 *
	 * @param discountByShoppingItem map of discounts per shopping item
	 * @param shoppingItems          the shopping items
	 * @param splitShipmentMode      whether this is a split shipment
	 * @param cartSubtotalDiscount   the cart subtotal discount
	 * @return the discount for shipment total
	 */
	protected Money getDiscountForShipment(final Map<String, BigDecimal> discountByShoppingItem, final List<OrderSku> shoppingItems,
										   final boolean splitShipmentMode, final Money cartSubtotalDiscount) {
		if (!splitShipmentMode) {
			return cartSubtotalDiscount;
		}
		BigDecimal discount = BigDecimal.ZERO;
		for (OrderSku sku : shoppingItems) {
			if (discountByShoppingItem.containsKey(sku.getGuid())) {
				discount = discount.add(discountByShoppingItem.get(sku.getGuid()));
			}
		}
		return Money.valueOf(discount, cartSubtotalDiscount.getCurrency());
	}

	protected EpRuleEngine getRuleEngine() {
		return ruleEngine;
	}

	public void setRuleEngine(final EpRuleEngine ruleEngine) {
		this.ruleEngine = ruleEngine;
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	protected ProductSkuLookup getProductSkuLookup() {
		return productSkuLookup;
	}

	public void setProductSkuLookup(final ProductSkuLookup productSkuLookup) {
		this.productSkuLookup = productSkuLookup;
	}

	protected CartDirector getCartDirector() {
		return cartDirector;
	}

	public void setCartDirector(final CartDirector cartDirector) {
		this.cartDirector = cartDirector;
	}

	private Predicate<Throwable> getShippingOptionResultExceptionLogPredicate() {
		return this.shippingOptionResultExceptionLogPredicate;
	}

	public void setShippingOptionResultExceptionLogPredicate(final Predicate<Throwable> shippingOptionResultExceptionLogPredicate) {
		this.shippingOptionResultExceptionLogPredicate = shippingOptionResultExceptionLogPredicate;
	}

	protected ShippingCalculationService getShippingCalculationService() {
		return this.shippingCalculationService;
	}

	public void setShippingCalculationService(final ShippingCalculationService shippingCalculationService) {
		this.shippingCalculationService = shippingCalculationService;
	}

	protected PricedShippableItemContainerTransformer getPricedShippableItemContainerTransformer() {
		return this.pricedShippableItemContainerTransformer;
	}

	public void setPricedShippableItemContainerTransformer(final PricedShippableItemContainerTransformer pricedShippableItemContainerTransformer) {
		this.pricedShippableItemContainerTransformer = pricedShippableItemContainerTransformer;
	}
}
