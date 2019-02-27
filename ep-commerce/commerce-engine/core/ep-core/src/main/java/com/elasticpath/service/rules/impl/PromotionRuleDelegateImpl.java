/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.rules.impl;

import static java.lang.String.format;
import static java.util.Collections.singletonList;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import org.apache.log4j.Logger;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceTier;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerGroup;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.discounts.DiscountItemContainer;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.domain.rules.CouponUsageType;
import com.elasticpath.domain.rules.RuleParameterNumItemsQuantifier;
import com.elasticpath.domain.shoppingcart.DiscountRecord;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.impl.CatalogItemDiscountRecordImpl;
import com.elasticpath.money.Money;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.rules.CouponConfigService;
import com.elasticpath.service.rules.CouponUsageService;
import com.elasticpath.service.rules.PromotionRuleDelegate;
import com.elasticpath.service.rules.PromotionRuleExceptions;
import com.elasticpath.service.rules.RuleService;
import com.elasticpath.service.shipping.ShippingOptionResult;
import com.elasticpath.service.shipping.ShippingOptionService;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * This interface provides helper methods that can be invoked from Drools code to make queries on the system. The intent of this
 * interface/implementation is to move as much logic as possible out of the rule code so that the drools code is as simple as possible.
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.GodClass" })
public class PromotionRuleDelegateImpl implements PromotionRuleDelegate {

	private static final Logger LOG = Logger.getLogger(PromotionRuleDelegateImpl.class);

	private static final String PERCENT_DIVISOR = "100";

	private static final int CALCULATION_SCALE = 2;

	private static final String ANY_BRAND_CODE = "ANY";

	private RuleService ruleService;
	private CouponUsageService couponUsageService;
	private CouponConfigService couponConfigService;
	private ProductSkuLookup productSkuLookup;
	private ProductService productService;
	private ShippingOptionService shippingOptionService;

	private BeanFactory beanFactory;

	@Override
	public boolean catalogProductInCategory(final Product product, final boolean isIn, final String compoundCategoryGuid, final String exceptionStr) {
		boolean isInCategoryAndNotExcluded = false;

		if (compoundCategoryGuid != null && !product.getProductType().isExcludedFromDiscount()) {
			isInCategoryAndNotExcluded = isProductInCategory(product, compoundCategoryGuid)
							&& !isProductExcludedFromRule(product, getPromotionRuleExceptions(exceptionStr));
		}
		if (!isIn) {
			return !isInCategoryAndNotExcluded;
		}
		return isInCategoryAndNotExcluded;
	}

	/**
	 * Package private purely for test to override.
	 * @param product the product
	 * @param ruleExceptions the rule exception
	 * @return true if product is excluded from rule
	 */
	boolean isProductExcludedFromRule(final Product product, final PromotionRuleExceptions ruleExceptions) {
		return ruleExceptions.isProductExcluded(product);
	}

	/**
	 * Package private purely for test to override.
	 *
	 * @param product              the product
	 * @param compoundCategoryGuid the compound category guid
	 * @return true if product is in category
	 */
	boolean isProductInCategory(final Product product, final String compoundCategoryGuid) {
		return getProductService().isInCategory(product, compoundCategoryGuid);
	}

	/**
	 * Get a <code>PromotionRuleException</code> object populated with the given exception string.
	 *
	 * @param exceptionStr the exception string passed in by the rule
	 * @return the populated <code>PromotionRuleExceptions</code> object
	 */
	PromotionRuleExceptions getPromotionRuleExceptions(final String exceptionStr) {
		PromotionRuleExceptions promotionRuleExceptions = beanFactory.getBean(ContextIdNames.PROMOTION_RULE_EXCEPTIONS);
		promotionRuleExceptions.populateFromExceptionStr(exceptionStr);
		return promotionRuleExceptions;
	}

	@Override
	public boolean catalogProductIs(final Product product, final boolean isProduct, final String productCode, final String exceptionStr) {

		boolean productIdMatches = false;

		if (productCode != null && !product.getProductType().isExcludedFromDiscount()) {
			productIdMatches = product.getCode().equals(productCode);
		}
		if (!isProduct) {
			return !productIdMatches;
		}
		return productIdMatches;
	}

	/**
	 * Returns true if the given product is/is not of the specified brand.
	 *
	 * @param product the product whose condition is to be checked
	 * @param isBrand set to true to check that the brand is the one with the specified Id, or false to check that it is not the brand with the
	 *            specified id.
	 * @param brandCode the code of the brand to check for
	 * @param exceptionStr exceptions to this rule element; to be used to populate the PromotionRuleExceptions.
	 * @return true if the product is of the specified brand
	 */
	@Override
	public boolean catalogBrandIs(final Product product, final boolean isBrand, final String brandCode, final String exceptionStr) {

		boolean brandMatches = false;

		if (product.getBrand() != null && !product.getProductType().isExcludedFromDiscount()) {
			brandMatches = brandCode.equalsIgnoreCase(ANY_BRAND_CODE) || product.getBrand().getCode().equals(brandCode);
		}
		if (!isBrand) {
			return !brandMatches;
		}
		return brandMatches;
	}

	/**
	 * Checks if the currency of a shopping cart matches the specified currency code.
	 *
	 * @param shoppingCart The shopping cart to check
	 * @param currencyCode The currency code, e.g. CAD
	 * @return true if the cart currency code matches the supplied code
	 */
	@Override
	public boolean cartCurrencyMatches(final ShoppingCart shoppingCart, final String currencyCode) {
		final CustomerSession customerSession = shoppingCart.getCustomerSession();

		return customerSession.getCurrency().getCurrencyCode().equals(currencyCode);
	}

	/**
	 * Checks if the given <code>ShoppingCart</code> contains the specified <code>quantity</code> of the Product indicated by the given
	 * <code>skuCode</code>.
	 *
	 * @param shoppingCart the shopping cart to check
	 * @param skuCode the SKU code that must be in the cart
	 * @param numItemsQuantifier the <code>String</code> number-of-items quantifier; indicates whether the quantity specified is a minimum quantity
	 *            or an exact quantity
	 * @param quantity the quantity of the SKU
	 * @return true if there are <code>quantity</code> items with <code>skuCode</code> in the cart
	 */
	@Override
	public boolean cartContainsSku(final ShoppingCart shoppingCart, final String skuCode, final String numItemsQuantifier, final int quantity) {
		final ProductSku targetSku = getProductSkuLookup().findBySkuCode(skuCode);

		if (targetSku == null) {
			return false;
		}

		final int qualifyingQuantity = shoppingCart.getAllShoppingItems().stream()
				.filter(shoppingItem -> !shoppingItem.isBundleConstituent())
				.filter(shoppingItem -> shoppingItem.getSkuGuid().equals(targetSku.getGuid()))
				.mapToInt(ShoppingItem::getQuantity)
				.sum();

		if (numItemsQuantifier.equalsIgnoreCase(RuleParameterNumItemsQuantifier.AT_LEAST.toString()) && qualifyingQuantity >= quantity) {
			return true;
		} else if (numItemsQuantifier.equalsIgnoreCase(RuleParameterNumItemsQuantifier.EXACTLY.toString()) && qualifyingQuantity == quantity) {
			return true;
		}

		return false;
	}

	@Override
	public boolean cartContainsAnySku(final ShoppingCart shoppingCart, final DiscountItemContainer discountItemContainer,
									  final String numItemsQuantifier, final int quantity, final String exceptionStr) {
		final int qualifyingQuantity = shoppingCart.getAllShoppingItems().stream()
				.filter(shoppingItem -> !shoppingItem.isBundleConstituent())
				.filter(shoppingItem ->
						// The current item qualifies if the item does not fall under any of the rule's SKU exceptions
						discountItemContainer.cartItemEligibleForPromotion(shoppingItem, getPromotionRuleExceptions(exceptionStr)))
				.mapToInt(ShoppingItem::getQuantity)
				.sum();

		if (numItemsQuantifier.equalsIgnoreCase(RuleParameterNumItemsQuantifier.AT_LEAST.toString()) && qualifyingQuantity >= quantity) {
			return true;
		} else if (numItemsQuantifier.equalsIgnoreCase(RuleParameterNumItemsQuantifier.EXACTLY.toString()) && qualifyingQuantity == quantity) {
			return true;
		}

		return false;
	}

	/**
	 * Checks if the given <code>ShoppingCart</code> contains the specified <code>quantity</code> of the Product indicated by the given
	 * <code>productUid</code>.
	 *
	 * @param shoppingCart the shopping cart to check
	 * @param productCode the code of the product that must be in the cart
	 * @param numItemsQuantifier the <code>String</code> number-of-items quantifier; indicates whether the quantity specified is a minimum quantity
	 *            or an exact quantity
	 * @param quantity the quantity of the product
	 * @param exceptionStr exceptions to this rule element; to be used to populate the PromotionRuleExceptions.
	 * @return true if there are <code>quantity</code> items with <code>productUid</code> in the cart
	 */
	@Override
	public boolean cartContainsProduct(final ShoppingCart shoppingCart, final String productCode, final String numItemsQuantifier, final int quantity,
			final String exceptionStr) {
		final PromotionRuleExceptions promotionRuleExceptions = getPromotionRuleExceptions(exceptionStr);

		final int qualifyingQuantity = shoppingCart.getAllShoppingItems().stream()
				.filter(shoppingItem -> !shoppingItem.isBundleConstituent())
				.filter(shoppingItem -> isShoppingItemQualifiedForQuantityMatch(productCode, promotionRuleExceptions, shoppingItem))
				.mapToInt(ShoppingItem::getQuantity)
				.sum();

		if (numItemsQuantifier.equalsIgnoreCase(RuleParameterNumItemsQuantifier.AT_LEAST.toString()) && qualifyingQuantity >= quantity) {
			return true;
		} else if (numItemsQuantifier.equalsIgnoreCase(RuleParameterNumItemsQuantifier.EXACTLY.toString()) && qualifyingQuantity == quantity) {
			return true;
		}

		return false;
	}

	/**
	 * The current item qualifies if the item matches the rule's product and if the item does not fall under any of the rule's SKU
	 * exceptions.
	 *
	 * @param productCode             Product code to match the item against
	 * @param promotionRuleExceptions exceptions to evaluate against
	 * @param shoppingItem            the shopping item to verify for qualification
	 * @return true if the ShoppingItem matches the product code and if the item does not fall under any of the rule's SKU
	 * exceptions.
	 */
	protected boolean isShoppingItemQualifiedForQuantityMatch(final String productCode, final PromotionRuleExceptions promotionRuleExceptions,
															  final ShoppingItem shoppingItem) {

		final ProductSku currItemSku = getProductSkuLookup().findByGuid(shoppingItem.getSkuGuid());
		return currItemSku.getProduct().getCode().equals(productCode)
				&& !promotionRuleExceptions.isSkuExcluded(currItemSku);
	}

	@Override
	public boolean cartContainsItemsOfCategory(final ShoppingCart shoppingCart,
												final DiscountItemContainer discountItemContainer,
												final String compoundCategoryGuid,
												final String numItemsQuantifier,
												final int numItems,
											   final String exceptionStr) {
		final int qualifyingQuantity = shoppingCart.getAllShoppingItems().stream()
				.filter(shoppingItem -> !shoppingItem.isBundleConstituent())
				.filter(shoppingItem -> isShoppingItemQualifiedForCategoryMatch(discountItemContainer, compoundCategoryGuid, exceptionStr, shoppingItem))
				.mapToInt(ShoppingItem::getQuantity)
				.sum();

		if (numItemsQuantifier.equalsIgnoreCase(RuleParameterNumItemsQuantifier.AT_LEAST.toString()) && qualifyingQuantity >= numItems) {
			return true;
		} else if (numItemsQuantifier.equalsIgnoreCase(RuleParameterNumItemsQuantifier.EXACTLY.toString()) && qualifyingQuantity == numItems) {
			return true;
		}

		return false;
	}

	/**
	 * The current item qualifies if the item's category matches the rule's category and if the item does not fall under any of the
	 * rule's exceptions.
	 *
	 * @param discountItemContainer a discount Item Container
	 * @param compoundCategoryGuid  Compound category GUID
	 * @param exceptionStr          Exception string
	 * @param shoppingItem          Shopping item to be evaluated
	 * @return true if the item's category matches the rule's category and if the item does not fall under any of the
	 * rule's exceptions.
	 */
	protected boolean isShoppingItemQualifiedForCategoryMatch(final DiscountItemContainer discountItemContainer, final String compoundCategoryGuid,
															  final String exceptionStr, final ShoppingItem shoppingItem) {

		// The current item qualifies if the item's category matches the rule's category and if the item does not fall under any of the
		// rule's exceptions
		final ProductSku cartItemSku = getProductSkuLookup().findByGuid(shoppingItem.getSkuGuid());
		return catalogProductInCategory(cartItemSku.getProduct(), true, compoundCategoryGuid, exceptionStr)
				&& discountItemContainer.cartItemEligibleForPromotion(shoppingItem, getPromotionRuleExceptions(exceptionStr));
	}

	/**
	 * Applies a discount to the specified price.
	 *
	 * @param ruleId the ID of the rule that applied this discount
	 * @param actionId the ID of the rule action that applied this discount
	 * @param discountAmount the amount of the discount
	 * @param price the <code>Price</code> to be discounted
	 */
	protected void discountPriceByAmount(final long ruleId, final long actionId, final BigDecimal discountAmount, final Price price) {
		for (PriceTier priceTier : price.getPriceTiers().values()) {
			final BigDecimal prePromotionAmount = priceTier.getPrePromotionPrice();

			final BigDecimal discountedAmount = prePromotionAmount.subtract(discountAmount);

			final DiscountRecord discountRecord =
					createDiscountRecord(ruleId, actionId, prePromotionAmount, discountAmount, discountedAmount, price);

			priceTier.setComputedPriceIfLower(discountedAmount);
			priceTier.addDiscountRecord(discountRecord);
		}
	}

	/**
	 * @param ruleId the ID of the rule that applied this discount
	 * @param actionId the ID of the rule action that applied this discount
	 * @param discountPercent BigDecimal value for percent to discount
	 * @param price to apply discount
	 */
	protected void discountPriceByPercent(final long ruleId, final long actionId, final BigDecimal discountPercent, final Price price) {
		for (PriceTier priceTier : price.getPriceTiers().values()) {
			final BigDecimal prePromotionAmount = priceTier.getPrePromotionPrice();

			final BigDecimal discountAmount = prePromotionAmount.multiply(discountPercent);

			final BigDecimal discountedAmount = prePromotionAmount.subtract(discountAmount);

			final DiscountRecord discountRecord =
					createDiscountRecord(ruleId, actionId, prePromotionAmount, discountAmount, discountedAmount, price);

			priceTier.setComputedPriceIfLower(discountedAmount);
			priceTier.addDiscountRecord(discountRecord);
		}
	}

	/**
	 * Factory method that returns a new {@link DiscountRecord} instance representing the computed price to be applied.
	 *
	 * @param ruleId the ID of the rule that applied this discount
	 * @param actionId the ID of the rule action that applied this discount
	 * @param prePromotionPriceMoney the pre-promotion amount value
	 * @param discountAmount the amount discounted
	 * @param discountedPriceMoney the post-promotion amount value; equivalent to <code>prePromotionPriceMoney.subtract(discountMoney)</code>
	 * @param price the price on which the computed price will be set
	 * @return a new {@link DiscountRecord} instance
	 */
	protected DiscountRecord createDiscountRecord(
			final long ruleId,
			final long actionId,
			final BigDecimal prePromotionPriceMoney,
			final BigDecimal discountAmount,
			final BigDecimal discountedPriceMoney,
			final Price price) {
		return new CatalogItemDiscountRecordImpl(ruleId, actionId, discountAmount);
	}

	/**
	 * @param discountPercent {@link BigDecimal}
	 * @return amount with scale set
	 */
	protected BigDecimal setDiscountPercentScale(final BigDecimal discountPercent) {
		return discountPercent.setScale(CALCULATION_SCALE, BigDecimal.ROUND_HALF_UP).divide(
				new BigDecimal(PERCENT_DIVISOR), CALCULATION_SCALE, BigDecimal.ROUND_HALF_UP);
	}

	@Override
	public void applyCatalogCurrencyDiscountPercent(final long ruleId, final long actionId, final Object listOfPrices,
													final String cartCurrencyCode, final String ruleCurrencyCode, final String percent) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("applyCatalogCurrencyDiscountPercent rule" + ruleId + " prices " + listOfPrices + "cartCurrency "
					+ cartCurrencyCode + " ruleCurrency " + ruleCurrencyCode + " percent " + percent);
		}
		if (!cartCurrencyCode.equals(ruleCurrencyCode)) {
			return;
		}

		@SuppressWarnings("unchecked")
		List<Price> prices = (List<Price>) listOfPrices;
		BigDecimal discountPercent = setDiscountPercentScale(new BigDecimal(percent));

		// Apply the discount to the product
		for (Price price : prices) {
			discountPriceByPercent(ruleId, actionId, discountPercent, price);
		}
	}

	@Override
	public void applyCatalogCurrencyDiscountAmount(final long ruleId, final long actionId, final Object listOfPrices,
													final String cartCurrencyCode, final String ruleCurrencyCode, final String amount) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("applyCatalogCurrencyDiscountAmount rule" + ruleId + " prices " + listOfPrices + "cartCurrency "
					+ cartCurrencyCode + " ruleCurrency " + ruleCurrencyCode + " amount " + amount);
		}
		if (!cartCurrencyCode.equals(ruleCurrencyCode)) {
			return;
		}

		@SuppressWarnings("unchecked")
		List<Price> prices = (List<Price>) listOfPrices;
		final Money discount = stringAmountToMoney(amount, Currency.getInstance(cartCurrencyCode));
		// Apply the discount to the product
		for (Price price : prices) {
			discountPriceByAmount(ruleId, actionId, discount.getAmount(), price);
		}
	}

	@Override
	public boolean cartSubtotalAtLeast(final DiscountItemContainer discountItemContainer, final String amount, final String exceptionStr) {
		final PromotionRuleExceptions promotionRuleExceptions = getPromotionRuleExceptions(exceptionStr);
		final BigDecimal subTotal = discountItemContainer.calculateSubtotalOfDiscountableItemsExcluding(promotionRuleExceptions);
		return subTotal.compareTo(new BigDecimal(amount)) > -1;
	}

	@Override
	public void applyShippingDiscountAmount(final ShoppingCart shoppingCart, final DiscountItemContainer discountItemContainer, final long ruleId,
											final long actionId, final String amount, final String shippingLevelCode, final Currency currency) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("applyShippingDiscountAmount rule" + ruleId + " actionId: " + actionId + " shippingLevelCode: " + shippingLevelCode
					+ " amount " + amount);
		}
		BigDecimal amountBigDecimal = new BigDecimal(amount);
		ShippingDiscountCalculator discountCalculator = new FixedAmountBasedShippingDiscountCalculator(amountBigDecimal);
		applyShippingDiscount(shippingLevelCode, shoppingCart, discountItemContainer, ruleId, actionId, discountCalculator, currency);
	}

	@Override
	public void applyShippingDiscountPercent(final ShoppingCart shoppingCart, final DiscountItemContainer discountItemContainer, final long ruleId,
											 final long actionId, final String percent, final String shippingOptionCode, final Currency currency) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("applyShippingDiscountPercent rule" + ruleId + " actionId: " + actionId + " shippingOptionCode: " + shippingOptionCode
					+ " percent " + percent);
		}

		BigDecimal discountPercent = new BigDecimal(percent);
		discountPercent = setDiscountPercentScale(discountPercent);
		ShippingDiscountCalculator discountCalculator = new PercentBasedShippingDiscountCalculator(discountPercent, getProductSkuLookup());
		applyShippingDiscount(shippingOptionCode, shoppingCart, discountItemContainer, ruleId, actionId, discountCalculator, currency);
	}

	/**
	 * <p>
	 * Applying a shipping discount involves setting the discount on the appropriate
	 * shipping option (ShippingOption) and recording which rule was applied.
	 * </p>
	 * <p>
	 * Promotions that applied to shipping options are recorded in 2 places:
	 * <ul>
	 *     <li>
	 *         Iff the promotion was applied to the shipping option that is
	 *         currently selected on the shopping cart then that promotion will
	 *         be recorded as an applied rule on the shopping cart.
	 *     </li>
	 *     <li>
	 *         The promotion will always be recorded on the associated
	 *         shipping option regardless of what is selected on the
	 *         shopping cart.
	 *     </li>
	 * </ul>
	 * </p>
	 * @param shippingOptionCode The shipping option ({@link ShippingOption}) to apply the discount to.
	 * @param shoppingCart The current shoppers shopping cart.
	 * @param discountItemContainer the discount item container
	 * @param ruleId The applied rule id.
	 * @param actionId The applied rule action.
	 * @param discountCalculator The discount calculator.
	 * @param currency The discount's currency.
	 */
	private void applyShippingDiscount(final String shippingOptionCode,
									   final ShoppingCart shoppingCart,
									   final DiscountItemContainer discountItemContainer,
									   final long ruleId,
									   final long actionId,
									   final ShippingDiscountCalculator discountCalculator,
									   final Currency currency) {
		final ShippingOptionResult shippingOptionResult = getShippingOptionService().getShippingOptions(shoppingCart);
		final String errorMessage = format("Unable to get available shipping options for the given cart with guid '%s'. "
						+ "Unable to attempt to apply shipping discount.",
				shoppingCart.getGuid());
		shippingOptionResult.throwExceptionIfUnsuccessful(
				errorMessage,
				singletonList(
						new StructuredErrorMessage(
								"shippingoptions.unavailable",
								errorMessage,
								ImmutableMap.of(
										"cart-id", shoppingCart.getGuid(),
										"rule-id", Long.toString(ruleId),
										"action-id", Long.toString(actionId),
										"shipping-option", shippingOptionCode,
										"currency", currency.getCurrencyCode())
						)
				));

		shippingOptionResult.getAvailableShippingOptions().stream()
				.filter(shippingOption -> shippingOptionCode.equals(shippingOption.getCode()))
				.findFirst()
				.ifPresent(shippingOption -> {
			final Money discount = discountCalculator.calculateDiscount(shippingOption, shoppingCart, discountItemContainer, currency);
			discountItemContainer.applyShippingOptionDiscount(shippingOption, ruleId, actionId, discount.getAmount());
		});
	}

	/**
	 * Calculates shipping discounts.
	 */
	private interface ShippingDiscountCalculator {
		/**
		 * Calculates shipping discounts.
		 * @param shippingOption The shipping option.
		 * @param shoppingCart The shopping cart.
		 * @param discountItemContainer the discount item container
		 * @param currency The currency.
		 * @return Money - The discount.  May be zero.
		 */
		Money calculateDiscount(ShippingOption shippingOption, ShoppingCart shoppingCart, DiscountItemContainer discountItemContainer,
								Currency currency);
	}

	/**
	 * Calculates fixed amount shipping discounts (ie. $10 off).
	 */
	private static class FixedAmountBasedShippingDiscountCalculator implements ShippingDiscountCalculator {
		private final BigDecimal discountAmount;

		/**
		 * Creates a calculator.
		 * @param discountAmount The fixed amount to discount.
		 */
		FixedAmountBasedShippingDiscountCalculator(final BigDecimal discountAmount) {
			this.discountAmount = discountAmount;
		}

		@Override
		public Money calculateDiscount(final ShippingOption shippingOption,
									   final ShoppingCart shoppingCart,
									   final DiscountItemContainer discountItemContainer,
									   final Currency currency) {
			return Money.valueOf(discountAmount, currency);
		}
	}

	/**
	 * A percentage based shipping discount calculator (ie. 15% off).
	 */
	private static class PercentBasedShippingDiscountCalculator implements ShippingDiscountCalculator {
		private final BigDecimal discountPercent;
		private final ProductSkuLookup productSkuLookup;

		/**
		 * Creates a calculator.
		 * @param discountPercent The discount percent.
		 * @param productSkuLookup The product sku lookup.
		 */
		PercentBasedShippingDiscountCalculator(final BigDecimal discountPercent, final ProductSkuLookup productSkuLookup) {
			this.discountPercent = discountPercent;
			this.productSkuLookup = productSkuLookup;
		}

		@Override
		public Money calculateDiscount(final ShippingOption shippingOption,
									   final ShoppingCart shoppingCart,
									   final DiscountItemContainer discountItemContainer,
									   final Currency currency) {
			BigDecimal originalShippingCost = discountItemContainer.getPrePromotionPriceAmount(shippingOption);
			BigDecimal discountAmount = originalShippingCost.multiply(discountPercent);
			return Money.valueOf(discountAmount, currency);
		}
	}

	/**
	 * Checks that the current date is between the specified dates.
	 *
	 * @param startDateString the start date represented as a long (milliseconds) in a string. 0 means no start date restriction on the date range.
	 * @param endDateString the end date represented as a long (milliseconds) in a string. 0 means no end date. Longs are not supported by Drools so
	 *            the dates are passed as strings.
	 * @return true if the current date is between the specified dates
	 */
	@Override
	public boolean checkDateRange(final String startDateString, final String endDateString) {
		long startDate = Long.parseLong(startDateString);
		long endDate = Long.parseLong(endDateString);

		long currentDate = new Date().getTime();
		if (currentDate < startDate) {
			return false;
		} else if (endDate != 0 && currentDate > endDate) {
			return false;
		}
		return true;
	}

	/**
	 * Checks whether the state of the rule is ACTIVE or DISABLED.
	 *
	 * @param state the <code>long</code> state value of the promotion rule; 1 if ACTIVE, 0 is DISABLED
	 * @return true if the state is ACTIVE, false if it is DISABLED
	 */
	@Override
	public boolean checkEnabled(final String state) {
		return "true".equalsIgnoreCase(state);
	}

	@Override
	public boolean customerInGroup(final CustomerSession customerSession, final long customerGroup) {
		Customer customer = customerSession.getShopper().getCustomer();
		if (customer == null) {
			return false;
		}

		for (CustomerGroup currCustomerGroup : customer.getCustomerGroups()) {
			if (currCustomerGroup.getUidPk() == customerGroup) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isExistingCustomer(final Customer customer) {
		boolean isExistingCustomer = false;
		if (customer != null && customer.getUidPk() > 0 && !customer.isAnonymous()) {
			isExistingCustomer = true;
		}
		return isExistingCustomer;
	}

	/**
	 * Helper method to create a <code>Money</code> object from a string amount and currency.
	 *
	 * @param amount the amount to set as the value of the <code>Money</code> object
	 * @param currency the currency
	 * @return a new initialized <code>Money</code> object
	 */
	private Money stringAmountToMoney(final String amount, final Currency currency) {
		return Money.valueOf(amount, currency);
	}

	/**
	 * Sets the rule service.
	 *
	 * @param ruleService the rule service
	 */
	public void setRuleService(final RuleService ruleService) {
		this.ruleService = ruleService;
	}

	@Override
	public boolean checkLimitedUsagePromotion(final ShoppingCart shoppingCart, final DiscountItemContainer discountItemContainer,
												final String allowedLimitParm, final String ruleCode, final long ruleId) {
		if (!discountItemContainer.getLimitedUsagePromotionRuleCodes().containsKey(ruleCode)) {
			try {
				if (Long.parseLong(allowedLimitParm) > ruleService.findLupByRuleCode(ruleCode)) {
					shoppingCart.applyLimitedUsagePromotionRuleCode(ruleCode, ruleId);
				} else {
					return false;
				}

			} catch (NumberFormatException pe) {
				LOG.debug(" Could not parse allowedLimit.");
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks the cart for a limited use coupon code.
	 *
	 * @param shoppingCart the cart to check
	 * @param ruleId the id of the rule that requires a coupon code
	 * @return true if there is a valid coupon code in the cart
	 */
	@Override
	public boolean cartHasValidLimitedUseCouponCode(final ShoppingCart shoppingCart, final long ruleId) {
		return shoppingCart.hasLUCCForRule(ruleId);
	}

	@Override
	public int calculateAvailableDiscountQuantity(final ShoppingCart shoppingCart, final long ruleId,
			final int discountQuantityPerCoupon) {
		final String ruleCode = ruleService.findRuleCodeById(ruleId);
		String customerEmailAddress;
		if (shoppingCart.getShopper().getCustomer() == null) {
			customerEmailAddress = null;
		} else {
			customerEmailAddress = shoppingCart.getShopper().getCustomer().getEmail();
		}

		if (!isCouponDrawdownValid(ruleCode, customerEmailAddress)) {
			return discountQuantityPerCoupon;
		}

		Collection<CouponUsage> couponUses = couponUsageService.findByRuleCodeAndEmail(ruleCode, customerEmailAddress);

		// There may be no couponUsage yet because the coupon hasn't been used by this customer.
		if (couponUses.isEmpty()) {
			return discountQuantityPerCoupon;
		}
		Set<String> shoppingCartPromoCodes = shoppingCart.getPromotionCodes();
		int couponUsesRemaining = 0;
		for (CouponUsage couponUsage : couponUses) {
			if (shoppingCartPromoCodes.contains(couponUsage.getCoupon().getCouponCode())) {
				int usesRemaining = couponUsage.getCoupon().getCouponConfig().getUsageLimit() - couponUsage.getUseCount();
				if (CouponUsageType.LIMIT_PER_COUPON.equals(couponUsage.getCoupon().getCouponConfig().getUsageType())
						|| couponUsage.getCoupon().getCouponConfig().isMultiUsePerOrder()) {
					couponUsesRemaining += usesRemaining;
				} else {
					if (usesRemaining > 0) {
						couponUsesRemaining++;
					}
				}
			}
		}

		return discountQuantityPerCoupon * couponUsesRemaining;
	}

	/**
	 * Check whether a drawdown of the quantity based on coupon availability is valid.
	 *
	 * @param ruleCode the code of the rule to check
	 * @param customerEmailAddress the customer email address
	 * @return true if coupon usage can multiply the discount quantity
	 */
	protected boolean isCouponDrawdownValid(final String ruleCode, final String customerEmailAddress) {
		CouponConfig couponConfig = couponConfigService.findByRuleCode(ruleCode);
		return couponConfig != null
				&& !CouponUsageType.LIMIT_PER_COUPON.equals(couponConfig.getUsageType())
				&& !couponConfig.isUnlimited()
				&& customerEmailAddress != null;
	}


	@Override
	public void assignCouponToCustomer(final ShoppingCart shoppingCart, final long ruleId) {
		// Mark the rule as applied so that the CheckoutService gets it in the ruleApplied list.
		shoppingCart.ruleApplied(ruleId, 0, null, null, 0);
	}

	/**
	 *
	 * @param couponUsageService The coupon usage service to set.
	 */
	public void setCouponUsageService(final CouponUsageService couponUsageService) {
		this.couponUsageService = couponUsageService;
	}

	/**
	 *
	 * @param couponConfigService the couponConfigService to set
	 */
	public void setCouponConfigService(final CouponConfigService couponConfigService) {
		this.couponConfigService = couponConfigService;
	}

	protected ProductSkuLookup getProductSkuLookup() {
		return productSkuLookup;
	}

	public void setProductSkuLookup(final ProductSkuLookup productSkuLookup) {
		this.productSkuLookup = productSkuLookup;
	}

	protected ProductService getProductService() {
		return productService;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public void setProductService(final ProductService productService) {
		this.productService = productService;
	}

	protected ShippingOptionService getShippingOptionService() {
		return this.shippingOptionService;
	}

	public void setShippingOptionService(final ShippingOptionService shippingOptionService) {
		this.shippingOptionService = shippingOptionService;
	}
}
