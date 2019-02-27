/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.shoppingcart;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.elasticpath.domain.EpDomain;
import com.elasticpath.domain.ShoppingItemContainer;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.StoreObject;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.plugin.tax.domain.TaxExemption;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * <code>ShoppingCart</code> represents a shopping cart of a <code>Customer</code>.
 */
public interface ShoppingCart extends EpDomain, StoreObject, ShoppingItemContainer<ShoppingItem> {

	/**
	 * Return the guid.
	 *
	 * @return the guid.
	 */
	String getGuid();

	/**
	 * Get the set of applied gift certificates.
	 *
	 * @return the giftCertificateRedeems
	 */
	Set<GiftCertificate> getAppliedGiftCertificates();

	/**
	 * Adds the given gift certificate to the set which will be redeemed / applied to this shopping cart.
	 *
	 * @param giftCertificate the gift certificate.
	 */
	void applyGiftCertificate(GiftCertificate giftCertificate);

	/**
	 * Return the <code>CustomerSession</code>. instance. Customer sessions track information about sessions where the customer may not be logged
	 * in.
	 *
	 * @return the <code>CustomerSession</code> instance
 	 * @deprecated {@link CustomerSession}s should be attached to {@link ShoppingCart}s through {@link com.elasticpath.domain.shopper.Shopper}
	 * Use ShoppingCart.
	 */
	@Deprecated
	CustomerSession getCustomerSession();

	/**
	 * Set the <code>CustomerSession</code>. instance. Customer sessions track information about sessions where the customer may not be logged in.
	 *
	 * @param customerSession the <code>CustomerSession</code> instance
	 * @deprecated {@link CustomerSession}s should be attached to {@link ShoppingCart}s through {@link Shopper}.
	 * To attach a {@link CustomerSession} to a {@link ShoppingCart}, use this.setShopper(customerSession.getShopper().
	 */
	@Deprecated
	void setCustomerSession(CustomerSession customerSession);

	/**
	 * Get the products in the shopping cart.
	 *
	 * @return the products in the shopping cart
	 */
	List<Product> getCartProducts();

	/**
	 * Add an item to the cart. If a cart item exists the the same SKU as the given cart item, then the existing cart item will be updated to reflect
	 * the sum of the previous cart item quantity and the quantity of the new cart item.
	 *
	 * @param cartItem the <code>CartItem</code> to add
	 * @return the added cart item.
	 * @deprecated Use CartDirector.addToCart.
	 */
	@Deprecated
	ShoppingItem addCartItem(ShoppingItem cartItem);

	/**
	 * Add an item to the cart. No update of the quantity will be done, nor will any checking of the isConfigurable flag be done.
	 * This method should only be used by CartDirector.
	 *
	 * @param cartItem the <code>CartItem</code> to add
	 * @return the added cart item
	 */
	ShoppingItem addShoppingCartItem(ShoppingItem cartItem);

	/**
	 * Remove an item from the cart.
	 *
	 * @param itemUid the uidPk of the <code>CartItem</code> to remove
	 */
	void removeCartItem(long itemUid);

	/**
	 * Remove an item from the cart.
	 *
	 * @param lineItemGuid the guid of the <code>CartItem</code> to remove
	 */
	void removeCartItem(String lineItemGuid);

	/**
	 * Remove zero or many items from the cart.
	 *
	 * @param lineItemGuid the guids of the <code>CartItem</code> instances to remove
	 */
	void removeCartItems(Collection<String> lineItemGuid);

	/**
	 * Get the all cart items by the sku code of its SKU.
	 *
	 * @param skuCode the sku code of the SKU in the cart item to be retrieved.
	 * @return the corresponding list of <code>ShoppingItem</code> found
	 */
	List<ShoppingItem> getCartItems(String skuCode);

	/**
	 * Get the all cart items by the guid of its SKU.
	 *
	 * @param skuGuid the guid of the SKU in the cart items to be retrieved.
	 * @return the corresponding list of <code>ShoppingItem</code> found
	 */
	List<ShoppingItem> getCartItemsBySkuGuid(String skuGuid);

	/**
	 * Indicates whether or not a shopping cart contains shopping items.
	 * @return true if the shopping cart contains no shopping items
	 */
	boolean isEmpty();

	/**
	 * Return the number of items in the shopping cart.
	 *
	 * @return the number of items
	 */
	int getNumItems();

	/**
	 * Applies a discount to the shopping cart subtotal.
	 *
	 * @param discountAmount the amount to discount the subtotal by as a BigInteger
	 * @param ruleId the id of the rule associated with the discount
	 * @param actionId the id of the rule action associated with the discount
	 */
	void setSubtotalDiscount(BigDecimal discountAmount, long ruleId, long actionId);

	/**
	 * Empties the shopping cart (e.g. after a checkout)
	 */
	void clearItems();

	/**
	 * Set the preferred billing address.
	 *
	 * @param address the <code>Address</code>
	 */
	void setBillingAddress(Address address);

	/**
	 * Get the preferred billing address.
	 *
	 * @return the preferred shipping address
	 */
	Address getBillingAddress();

	/**
	 * Set the preferred shipping address.
	 *
	 * @param address the <code>Address</code>
	 */
	void setShippingAddress(Address address);

	/**
	 * Get the preferred shipping address.
	 *
	 * @return the preferred shipping address
	 */
	Address getShippingAddress();

	/**
	 * Set a reference to the completed order for the items previously checked out.
	 *
	 * @param order the completed order
	 */
	void setCompletedOrder(Order order);

	/**
	 * Get a reference to the completed order for the items previously checked out.
	 *
	 * @return the completed Order, or null if no completed order has been set.
	 */
	Order getCompletedOrder();

	/**
	 * Get the currently selected shipping option if set.
	 *
	 * @return the selected shipping option, or {@link Optional#empty()} if not set.
	 */
	Optional<ShippingOption> getSelectedShippingOption();

	/**
	 * Sets the currently selected shipping option.
	 *
	 * @param selectedShippingOption the shipping option selected.
	 */
	void setSelectedShippingOption(ShippingOption selectedShippingOption);

	/**
	 * Clears the selected shipping option as well as any calculated shipping costs.
	 */
	void clearSelectedShippingOption();

	/**
	 * Get the totalWeight of items in <code>ShoppingCart</code>.
	 *
	 * @return totalWeight
	 */
	BigDecimal getTotalWeight();

	/**
	 * Returns true if the cart contains items that must be shipped to the customer.
	 *
	 * @return true if the cart contains items that must be shipped to the customer.
	 */
	boolean requiresShipping();

	/**
	 * Returns the cart item with the given id.
	 *
	 * @param cartItemId the cart item id
	 * @return the cart item with the given id
	 */
	ShoppingItem getCartItemById(long cartItemId);

	/**
	 * Returns the cart item which matches {@code cartItemGuid}.
	 *
	 * @param cartItemGuid The guid to find.
	 * @return The cart item.
	 */
	ShoppingItem getCartItemByGuid(String cartItemGuid);

	/**
	 * Gets the list of promotion codes successfully applied to the cart.
	 *
	 * @return the promotion codes
	 */
	Set<String> getPromotionCodes();

	/**
	 * Add a new promotion code to the list of promotion codes added to the shopping cart.
	 *
	 * @param promotionCode the promotion code to add to the list
	 * @return if code is a valid promotion code
	 */
	boolean applyPromotionCode(String promotionCode);

	/**
	 * Apply a set of promotion codes to the shopping cart.
	 *
	 * @param promotionCodes list of promotion codes
	 * @return true if promotion codes are applied
	 */
	boolean applyPromotionCodes(Collection<String> promotionCodes);

	/**
	 * Remove a promotion code from the list of promotion codes added to the shopping cart.
	 *
	 * @param promotionCode the promotion code to remove from the list
	 */
	void removePromotionCode(String promotionCode);

	/**
	 * /**
	 * Remove promotion codes.
	 * @param promotionCodes promotion codes to be removed
	 * @return true if promotion codes are removed
	 */
	boolean removePromotionCodes(Collection<String> promotionCodes);

	/**
	 * Indicates if the promotion or gift certificate code entered by the user is valid.
	 *
	 * @return true if the code is valid
	 */
	boolean isCodeValid();

	/**
	 * Set whether or not the promotion or gift certificate code entered by the user is valid.
	 *
	 * @param codeValid set to true if the code is valid
	 */
	void setCodeValid(boolean codeValid);

	/**
	 * Removes shipping and tax estimates from the shopping cart.
	 */
	void clearEstimates();

	/**
	 * Get the indicator of whether in the estimate shipping and taxes mode.
	 *
	 * @return true when estimating shipping and taxes; otherwise, false.
	 */
	boolean isEstimateMode();

	/**
	 * Set the indicator of whether in the estimate shipping and taxes mode. Disabling estimate mode cleards the billing and shipping addresses
	 * (because they may not be full, valid addresses), but tax and shipping calculations are not cleared. If you wish to clear the estimated
	 * calculations/values, then call clearEstimates().
	 *
	 * @param estimateMode true when estimating shipping and taxes; otherwise, false.
	 */
	void setEstimateMode(boolean estimateMode);

	/**
	 * Indicates that the given rule was applied by the promotion rule engine.
	 *
	 * @param ruleId the uidPk of the <code>Rule</code>
	 * @param actionId The action id of the rule.
	 * @param discountedItem The item that was discounted
	 * @param discountAmount The amount of the discount.
	 * @param quantityAppliedTo The item quantity that the discount was applied to.
	 */
	void ruleApplied(long ruleId, long actionId, ShoppingItem discountedItem, BigDecimal discountAmount, int quantityAppliedTo);

	/**
	 * Retrieves the tax exemption that has been applied to the cart.
	 * @return A TaxExemption, or null if this cart is not tax exempt
	 */
	TaxExemption getTaxExemption();

	/**
	 * Sets the tax exemption applied to the cart.
	 * @param exemption the tax exemption
	 */
	void setTaxExemption(TaxExemption exemption);

	/**
	 * Gets the cmUserUID.
	 *
	 * @return CmUserUID the cmUser's uid
	 */
	Long getCmUserUID();

	/**
	 * Sets the CmUserUID.
	 *
	 * @param cmUserUID the cmUser's uid
	 */
	void setCmUserUID(Long cmUserUID);

	/**
	 * Set shipping cost. Basically shipping cost is a calculated value. This setter is required for exchange order.
	 *
	 * @param shippingCost the shipping cost
	 */
	void setShippingCostOverride(BigDecimal shippingCost);

	/**
	 * Explicitly set the subtotal discount amount.
	 *
	 * @param subtotalDiscount the subtotal discount amount
	 */
	void setSubtotalDiscountOverride(BigDecimal subtotalDiscount);

	/**
	 * Returns true is this shopping cart will be used for checking out exchange order, false
	 * for ordinary shopping cart.
	 *
	 * @return true if this cart is exchange shopping cart, false otherwise.
	 */
	boolean isExchangeOrderShoppingCart();

	/**
	 * Sets the flag depending if this shopping cart will be used for checking out exchange order,
	 * or for ordinary shopping cart.
	 *
	 * @param isExchangeOrderShoppingCart exchange shopping cart flag.
	 */
	void setExchangeOrderShoppingCart(boolean isExchangeOrderShoppingCart);

	/**
	 * Checks if a cart item with specific SKU code was previously removed.
	 *
	 * @param skuCode the SKU code to check
	 * @return true if a cart item with this SKU code was removed
	 */
	boolean isCartItemRemoved(String skuCode);

	/**
	 * Add a new limited usage promotion rule code to the list of rule codes checked against the shopping cart.
	 *
	 * @param ruleCode the rule code to add to the list
	 * @param ruleId the id of the rule the promotion code belongs to
	 */
	void applyLimitedUsagePromotionRuleCode(String ruleCode, long ruleId);

	/**
	 * Remove a rule code from the list of limited use promotion rule codes added to the shopping cart.
	 *
	 * @param ruleCode the code to remove from the list
	 */
	void removeLimitedUsagePromotionRuleCode(String ruleCode);

	/**
	 * Get the leaf items which have had prices apportioned to them, if they are bundle constituents.
	 *
	 * @return a collection of leaf shopping items with apportioned prices.
	 */
	Collection<ShoppingItem> getApportionedLeafItems();

	/**
	 * Returns if cart has limited usage coupon code applied for the given rule id.
	 * @param ruleId the rule id to check against
	 * @return true if lucc exists in the cart for the rule
	 */
	boolean hasLUCCForRule(long ruleId);

	/**
	 * Gets the {@link Shopper} for this ShoppingCart.
	 *
	 * @return Shopper
	 */
	Shopper getShopper();

	/**
	 * Sets the {@link Shopper} for this ShoppingCart.
	 *
	 * @param shopper the {@link Shopper}.
	 */
	void setShopper(Shopper shopper);


	/**
	 * Returns the shopping cart merged state notification.
	 *
	 * @return true if the cart was recently merged and a notification is desired, false otherwise
	 */
	boolean isMergedNotification();

	/**
	 * Set shopping cart merged state notification.
	 *
	 * @param merged use true if the cart was recently merged and a notification is desired, false otherwise
	 */
	void setMergedNotification(boolean merged);

	/**
	 * Indicates whether it has recurring prices shopping items in the cart.
	 *
	 * @return true or false.
	 */
	boolean hasRecurringPricedShoppingItems();

	/**
	 * Indicates whether a product has been moved from the favorites to shopping cart without a price tier of 1.
	 *
	 * @return true the first time the shopping cart is viewed with an item moved from the favorites without a tier 1 price.
	 */
	boolean hasItemWithNoTierOneFromWishList();

	/**
	 * Sets the flag which indicates that the shopping cart has an item added from favorites without a tier 1 price.
	 * @param itemWithNoTierOneFromWishList set to true if the shopping cart has an item from favorites that has no tier 1 price, false otherise.
	 */
	void setItemWithNoTierOneFromWishList(boolean itemWithNoTierOneFromWishList);

	/**
	 * Accepts a ShoppingCartVisitor and passes it to this cart's child ShoppingItems.
	 *
	 * @param visitor The visitor.
	 */
	void accept(ShoppingCartVisitor visitor);

	/**
	 * Returns the date when the object was last modified.
	 *
	 * @return the date when the object was last modified
	 */
	Date getLastModifiedDate();

	/**
	 * Change cart's state so it is no longer associated
	 * with the shopper. All deactivated carts and respective orders
	 * will be removed in a batch job.
	 */
	void deactivateCart();

	/**
	 * Check whether a cart is active.
	 *
	 * @return true if active.
	 */
	boolean isActive();

	/**
	 * Looks up the ProductSku related to each ShoppingItem in the cart and returns a map associating the two.
	 *
	 * @return a map of ShoppingItems in the cart, associated to the related ProductSku
	 */
	Map<ShoppingItem, ProductSku> getShoppingItemProductSkuMap();
}
