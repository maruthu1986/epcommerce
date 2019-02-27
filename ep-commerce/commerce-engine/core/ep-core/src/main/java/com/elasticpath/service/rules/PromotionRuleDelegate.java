/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.rules;

import java.util.Currency;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.discounts.DiscountItemContainer;
import com.elasticpath.domain.shoppingcart.ShoppingCart;

/**
 * This interface provides helper methods that can be invoked from Drools code to make queries on
 * the system. The intent of this interface/implementation is to move as much logic as possible
 * out of the rule code so that the drools code is as simple as possible. Note: If rules require a
 * helper method that would be useful to other domain objects, move the helper/convenience method
 * to the domain model rather than implementing it here.
 */
public interface PromotionRuleDelegate {

	/**
	 * Checks if the given product is in the category with the specified categoryID.
	 *
	 * @param product the product
	 * @param isIn set to true to specify that the product is in the category, false to require that it isn't in the category
	 * @param categoryCode the code of the category to check if the product is in it
	 * @param exceptionStr exceptions to this rule element; to be used to populate the PromotionRuleExceptions.
	 * @return true if the product is in the category or one of its children.
	 */
	boolean catalogProductInCategory(Product product, boolean isIn, String categoryCode, String exceptionStr);

	/**
	 * Returns true if the given product is/is not the product with the specified Id.
	 *
	 * @param product the product whose condition is to be checked
	 * @param isProduct set to true to check that the product is the one with the specified Id, or false to check that it is not the product with the
	 *            specified id.
	 * @param productCode the code of the product to check for
	 * @param exceptionStr exceptions to this rule element; to be used to populate the PromotionRuleExceptions.
	 * @return true if the product is in the category or one of its children.
	 */
	boolean catalogProductIs(Product product, boolean isProduct, String productCode, String exceptionStr);

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
	boolean catalogBrandIs(Product product, boolean isBrand, String brandCode, String exceptionStr);

	/**
	 * Checks if the currency of a shopping cart matches the specified currency code.
	 *
	 * @param shoppingCart The shopping cart to check
	 * @param currencyCode The currency code, e.g. CAD
	 * @return true if the cart currency code matches the supplied code
	 */
	boolean cartCurrencyMatches(ShoppingCart shoppingCart, String currencyCode);

	/**
	 * Checks if the shopping cart subtotal is at least equal to the specified amount.
	 *
	 * @param discountItemContainer the discount item container
	 * @param amount the amount the shopping cart must exceed
	 * @param exceptionStr exceptions to this rule element; to be used to populate the PromotionRuleExceptions.
	 * @return true if the shopping cart subtotal is greater than or equal to the specified amount
	 */
	boolean cartSubtotalAtLeast(DiscountItemContainer discountItemContainer, String amount, String exceptionStr);

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
	boolean cartContainsSku(ShoppingCart shoppingCart, String skuCode, String numItemsQuantifier, int quantity);

	/**
	 * Checks if the given <code>ShoppingCart</code> contains the specified <code>quantity</code> of products with ANY SKU.
	 *
	 * @param shoppingCart the shopping cart to check
	 * @param discountItemContainer the discount item container
	 * @param numItemsQuantifier the <code>String</code> number-of-items quantifier; indicates whether the quantity specified is a minimum quantity
	 *            or an exact quantity
	 * @param quantity the quantity of the SKU
	 * @param exceptionStr exceptions to this rule element; to be used to populate the PromotionRuleExceptions.
	 * @return true if there are <code>quantity</code> items with <code>skuCode</code> in the cart
	 */
	boolean cartContainsAnySku(ShoppingCart shoppingCart, DiscountItemContainer discountItemContainer, String numItemsQuantifier, int quantity,
								String exceptionStr);

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
	 * @return true if there are <code>quantity</code> items with <code>productId</code> in the cart
	 */
	boolean cartContainsProduct(ShoppingCart shoppingCart, String productCode, String numItemsQuantifier, int quantity,
			String exceptionStr);

	/**
	 * Checks if the given <code>ShoppingCart</code> contains the specified <code>numItems</code> of products in the Category indicated by the
	 * given <code>categoryUid</code>.
	 *
	 * @param shoppingCart the shopping cart to check
	 * @param discountItemContainer the discount item container
	 * @param categoryCode the category code that must be in the cart
	 * @param numItemsQuantifier the <code>String</code> number-of-items quantifier; indicates whether the quantity specified is a minimum quantity
	 *            or an exact quantity
	 * @param numItems the quantity of products required to be in the category
	 * @param exceptionStr exceptions to this rule element; to be used to populate the PromotionRuleExceptions.
	 * @return true if there are <code>numItems</code> items in the category indicated by <code>categoryUid</code> in the shopping cart
	 */
	boolean cartContainsItemsOfCategory(ShoppingCart shoppingCart, DiscountItemContainer discountItemContainer, String categoryCode,
										String numItemsQuantifier, int numItems, String exceptionStr);

	/**
	 * Reduces the shipping cost of the shopping cart by the specified amount. (If the specified shipping method is in use)
	 *
	 * @param shoppingCart the shopping cart that the shipping discount is to be applied to
	 * @param discountItemContainer the discount item container
	 * @param ruleId the id of the rule executing this action
	 * @param actionId the id of the action providing the discount
	 * @param amount the amount by which to reduce the shipping cost
	 * @param shippingOptionCode the code of shipping option that this discount applies to
	 * @param currency the currency
	 */
	void applyShippingDiscountAmount(ShoppingCart shoppingCart, DiscountItemContainer discountItemContainer, long ruleId, long actionId,
									 String amount, String shippingOptionCode, Currency currency);

	/**
	 * Reduces the shipping cost of the shopping cart by the specified percent. (If the specified shipping method is in use)
	 *
	 * @param shoppingCart the shopping cart that the shipping discount is to be applied to
	 * @param discountItemContainer the discount item container
	 * @param ruleId the id of the rule executing this action
	 * @param actionId the id of the action providing the discount
	 * @param percent the percent by which to reduce the shipping cost (String value from 1 to 100)
	 * @param shippingOptionCode the code of shipping option that this discount applies to
	 * @param currency the currency
	 */
	void applyShippingDiscountPercent(ShoppingCart shoppingCart, DiscountItemContainer discountItemContainer, long ruleId, long actionId,
									  String percent, String shippingOptionCode, Currency currency);

	/**
	 * Checks that the current date is between the specified dates.
	 *
	 * @param startDateString the start date represented as a long (milliseconds) in a string. 0 means no start date restriction on the date range.
	 * @param endDateString the end date represented as a long (milliseconds) in a string. 0 means no end date. Longs are not supported by Drools so
	 *            the dates are passed as strings.
	 * @return true if the current date is between the specified dates
	 */
	boolean checkDateRange(String startDateString, String endDateString);

	/**
	 * Checks whether the state of the rule is ACTIVE or DISABLED.
	 *
	 * @param state the <code>long</code> state value of the promotion rule; 1 if ACTIVE, 0 is DISABLED
	 * @return true if the state is ACTIVE, false if it is DISABLED
	 */
	boolean checkEnabled(String state);

	/**
	 * Checks if the customer is in the specified customer group.
	 *
	 * @param customerSession the customer session containing a reference to the customer
	 * @param customerGroup the customer group that the customer must belong to
	 * @return true if the customer belongs to the specified group
	 */
	boolean customerInGroup(CustomerSession customerSession, long customerGroup);

	/**
	 * Checks if the customer already has an account in the system.
	 *
	 * @param customer the customer
	 * @return true if the customer already has an account in the system.
	 */
	boolean isExistingCustomer(Customer customer);

	/**
	 * Reduces the price of a catalog item by the specified percent.
	 *
	 * @param ruleId the id of the rule executing this action
	 * @param actionId the id of the rule action
	 * @param listOfPrices the list of prices to apply discount to
	 * @param cartCurrencyCode The currency code of the shopping cart
	 * @param ruleCurrencyCode The currency code of the rule
	 * @param percent the amount by which the price is to be reduced
	 */
	void applyCatalogCurrencyDiscountPercent(long ruleId, long actionId, Object listOfPrices,
											String cartCurrencyCode, String ruleCurrencyCode, String percent);

	/**
	 * Reduces the price of a catalog item by the specified amount.
	 *
	 * @param ruleId the id of the rule executing this action
	 * @param actionId the id of the rule action
	 * @param listOfPrices the list of prices to apply discount to
	 * @param cartCurrencyCode The currency code of the shopping cart
	 * @param ruleCurrencyCode The currency code of the rule
	 * @param amount the amount by which the price is to be reduced
	 */
	void applyCatalogCurrencyDiscountAmount(long ruleId, long actionId, Object listOfPrices,
											String cartCurrencyCode, String ruleCurrencyCode, String amount);

	/**
	 * Checks Limited Usage Promotion.
	 *
	 * @param shoppingCart the shopping cart to check for the promo code
	 * @param discountItemContainer the discount item container
	 * @param allowedLimit the limit of the promotion
	 * @param limitedUsagePromotionId the promotion ID
	 * @param ruleId the id of the rule being applied
	 * @return true if the rule is passed, else false
	 */
	boolean checkLimitedUsagePromotion(ShoppingCart shoppingCart, DiscountItemContainer discountItemContainer, String allowedLimit,
										String limitedUsagePromotionId, long ruleId);

	/**
	 * Checks the cart for a limited use coupon code.
	 *
	 * @param shoppingCart the cart to check
	 * @param ruleId the id of the rule that requires a coupon code
	 * @return true if there is a valid coupon code in the cart
	 */
	boolean cartHasValidLimitedUseCouponCode(ShoppingCart shoppingCart, long ruleId);

	/**
	 * Marks the rule as applied so that the checkout service can actually apply it.
	 * @param shoppingCart The cart to get the customer from.
	 * @param ruleId The id of the rule that fired to make this action happen.
	 */
	void assignCouponToCustomer(ShoppingCart shoppingCart, long ruleId);

	/**
	 * Calculate number of free skus for the given rule and cart by multiplying the number from the
	 * parameter by the number of available coupon usages.
	 *
	 * @param shoppingCart the cart to get the rule code and customer from
	 * @param ruleId  the id of the rule that fired
	 * @param numSkus the number of skus from the action parameter
	 * @return the calculated number of skus
	 */
	int calculateAvailableDiscountQuantity(ShoppingCart shoppingCart, long ruleId, int numSkus);
}
