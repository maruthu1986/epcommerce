/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.domain.discounts.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.log4j.Logger;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.common.dto.sellingchannel.ShoppingItemDtoFactory;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.discounts.ShoppingCartDiscountItemContainer;
import com.elasticpath.domain.shoppingcart.PriceCalculator;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartImpl;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;
import com.elasticpath.money.Money;
import com.elasticpath.sellingchannel.ProductUnavailableException;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.rules.PromotionRuleExceptions;
import com.elasticpath.service.shoppingcart.ShoppingItemSubtotalCalculator;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * A shopping cart discount item container where the discounts can be applied
 * to. It also provides helper methods to get cart items and calculate subtotal
 * for discount calculation.
 */
// TODO: This class is legitimately God-like (in a bad way) and should be cut down to size.
@SuppressWarnings("PMD.GodClass")
public class ShoppingCartDiscountItemContainerImpl implements ShoppingCartDiscountItemContainer {

	private static final Logger LOG = Logger.getLogger(ShoppingCartDiscountItemContainerImpl.class);

	private Currency currency;
	private ShoppingCart cart;
	private CartDirector cartDirector;
	private ShoppingItemDtoFactory shoppingItemDtoFactory;
	private ProductSkuLookup productSkuLookup;
	private ShoppingItemSubtotalCalculator shoppingItemsSubtotalCalculator;
	private Predicate<ShoppingItem> shippableItemPredicate;

	@Override
	public void recordRuleApplied(final long ruleId, final long actionId,
			final ShoppingItem discountedItem, final BigDecimal discountAmount,
			final int quantityAppliedTo) {
		cart.ruleApplied(ruleId, actionId, discountedItem, discountAmount, quantityAppliedTo);
	}

	@Override
	public Catalog getCatalog() {
		return cart.getStore().getCatalog();
	}

	@Override
	public List<ShoppingItem> getItemsLowestToHighestPrice() {
		final Collection<? extends ShoppingItem> shoppingItems = cart.getShoppingItems(
				shoppingItem -> !shoppingItem.isBundleConstituent()
						&& getShoppingItemImpl(shoppingItem).canReceiveCartPromotion(getProductSkuLookup()));
		return sort(shoppingItems, new LowestToHighestPriceComparator());
	}

	@Override
	public Optional<ShoppingItem> getLowestPricedShoppingItemForSku(final String skuCode) {
		final List<ShoppingItem> cartItems = cart.getCartItems(skuCode);

		return getItemsLowestToHighestPrice().stream()
				.filter(cartItems::contains)
				.findFirst();
	}

	/**
	 * Sort the cart item based on price lowest to highest.
	 */
	private class LowestToHighestPriceComparator implements Comparator<ShoppingItem>, Serializable {

		private static final long serialVersionUID = 7462008040384400954L;

		@Override
		public int compare(final ShoppingItem cartItem1, final ShoppingItem cartItem2) {
			final BigDecimal cartItem1Amount = getPriceCalc(cartItem1).withCartDiscounts().getAmount();
			final BigDecimal cartItem2Amount = getPriceCalc(cartItem2).withCartDiscounts().getAmount();
			return cartItem1Amount.compareTo(cartItem2Amount);
		}
	}

	private List<ShoppingItem> sort(final Collection<? extends ShoppingItem> cartItems, final Comparator<ShoppingItem> comparator) {
		final List<ShoppingItem> sortedCartItems = new ArrayList<>();
		sortedCartItems.addAll(cartItems);
		Collections.sort(sortedCartItems, comparator);
		return sortedCartItems;
	}

	/**
	 * Gets the <code>ShoppingItem</code> calculated total price amount
	 * divided by total quantity,
	 * with discounts applied if any have been set.
	 *
	 * @param shoppingItem holds price, discount and quantity info.
	 *
	 * @return calculated price amount for the <code>ShoppingItem</code>.
	 *
	 * @see com.elasticpath.domain.shoppingcart.ShoppingItem#applyDiscount(java.math.BigDecimal, com.elasticpath.service.catalog.ProductSkuLookup)
	 *
	 * @see com.elasticpath.domain.impl.AbstractShoppingItemImpl#getTotal()
	 * @see com.elasticpath.domain.order.impl.OrderSkuImpl#getTotal()
	 */
	@Override
	public BigDecimal getPriceAmount(final ShoppingItem shoppingItem) {
		return getPriceCalc(shoppingItem).forUnitPrice().withCartDiscounts().getAmount();
	}

	/**
	 * Retrieves a Price Calculator for the given Shopping Item.
	 *
	 * @param shoppingItem the shopping item
	 * @return the corresponding price calculator
	 */
	protected PriceCalculator getPriceCalc(final ShoppingItem shoppingItem) {
		return getShoppingItemImpl(shoppingItem).getPriceCalc();
	}

	/**
	 * Casts a ShoppingItem to ShoppingItemImpl.
	 *
	 * @param shoppingItem the shopping item to recast
	 * @return the same {@link ShoppingItem}, cast to {@link ShoppingItemImpl}
	 * @throws IllegalArgumentException if {@code shoppingItem} is not of type {@link ShoppingItemImpl}
	 */
	private ShoppingItemImpl getShoppingItemImpl(final ShoppingItem shoppingItem) {
		if (!(shoppingItem instanceof ShoppingItemImpl)) {
			throw new IllegalArgumentException("Parameter must be a ShoppingItemImpl, as the getUnitPriceCalc method is (deliberately) not present "
														+ "on the ShoppingItem interface.  In future, the responsibility of tracking the currently-"
														+ "applied promotions may be moved from the ShoppingItemImpl class.  In the meantime, please "
														+ "consider creating a new adapter class that extends ShoppingItemImpl and delegates to an "
														+ "instance of your class " + shoppingItem.getClass().getName() + "].");
		}

		return (ShoppingItemImpl) shoppingItem;
	}

	/**
	 * Casts the shopping cart to ShoppingCartImpl.
	 *
	 * @return the current {@link ShoppingCart} instance, cast to {@link ShoppingCartImpl}
	 * @throws IllegalArgumentException if current shopping cart is not of type {@link ShoppingCartImpl}
	 */
	private ShoppingCartImpl getShoppingCartImpl() {
		if (!(getShoppingCart() instanceof ShoppingCartImpl)) {
			throw new IllegalArgumentException("The shopping cart member variable must be of type ShoppingCartImpl.  In the future, certain methods "
													+ "involving the application and calculation of promotional discounts may be moved from the "
													+ "ShoppingCartImpl class.  In the meantime, please ensure this container is delegating to an "
													+ "instance of ShoppingCartImpl or a subclass.");
		}

		return (ShoppingCartImpl) cart;
	}

	@Override
	public BigDecimal getPrePromotionUnitPriceAmount(final ShoppingItem shoppingItem) {
		return getPriceCalc(shoppingItem).forUnitPrice().getAmount();
	}

	@Override
	public ShoppingItem addCartItem(final String skuCode, final int numItems) {
		if (cart.isCartItemRemoved(skuCode)) {
			return null;
		} else {
			final ShoppingItemDto dto = getShoppingItemDtoFactory().createDto(skuCode, numItems);
			try {
				return getCartDirector().addItemToCart(getShoppingCart(), dto);
			} catch (final ProductUnavailableException e) {
				LOG.error(e.getMessage());
				return null;
			}
		}
	}

	@Override
	public BigDecimal getPrePromotionPriceAmount(final ShippingOption shippingOption) {
		return getShoppingCartImpl().getShippingListPrice(shippingOption.getCode()).getAmount();
	}

	@Override
	public void applySubtotalDiscount(final BigDecimal discountAmount, final long ruleId, final long actionId) {
		cart.setSubtotalDiscount(discountAmount, ruleId, actionId);
	}

	@Override
	public void applyShippingOptionDiscount(final ShippingOption shippingOption,
											final long ruleId,
											final long actionId,
											final BigDecimal discount) {
		getShoppingCartImpl().setShippingDiscountIfLower(shippingOption.getCode(), ruleId, actionId, Money.valueOf(discount, getCurrency()));
	}

	@Override
	public BigDecimal calculateSubtotalOfDiscountableItems() {
		return calculateSubtotalOfDiscountableItemsExcluding(null);
	}

	@Override
	public BigDecimal calculateSubtotalOfDiscountableItemsExcluding(final PromotionRuleExceptions promotionRuleExceptions) {
		return cart.getShoppingItems(shoppingItem -> !shoppingItem.isBundleConstituent()).stream()
				.filter(shoppingItem -> cartItemEligibleForPromotion(shoppingItem, promotionRuleExceptions))
				.map(shoppingItem -> getPriceCalc(shoppingItem).withCartDiscounts().getAmount())
				.reduce(BigDecimal.ZERO.setScale(currency.getDefaultFractionDigits()), BigDecimal::add);
	}

	@Override
	public BigDecimal calculateSubtotalOfShippableItems() {
		final Collection<ShoppingItem> shoppingItems = getShoppingCart().getApportionedLeafItems();

		final Stream<ShoppingItem> shippableShoppingItems = shoppingItems.stream().filter(getShippableItemPredicate());
		final Money shippableSubtotal = getShoppingItemsSubtotalCalculator().calculate(shippableShoppingItems, getShoppingCartImpl(), getCurrency());

		return shippableSubtotal.getAmount();
	}

	@Override
	public boolean cartItemEligibleForPromotion(final ShoppingItem cartItem, final PromotionRuleExceptions exceptions) {
		if (!cartItem.isDiscountable(getProductSkuLookup())) {
			return false;
		}

		final ProductSku cartItemSku = getProductSkuLookup().findByGuid(cartItem.getSkuGuid());

		if (cartItemSku == null) {
			return false;
		}

		return (exceptions == null)
			|| (!exceptions.isSkuExcluded(cartItemSku)
				&& !exceptions.isProductExcluded(cartItemSku.getProduct())
				&& !productIsInCategoryExcludedFromPromotion(cartItemSku.getProduct(), exceptions));
	}

	@Override
	public Map<String, Long> getLimitedUsagePromotionRuleCodes() {
		return getShoppingCartImpl().getPromotionRecordContainer().getLimitedUsagePromotionRuleCodes();
	}

	/**
	 * Checks whether a Product's categories match the exceptions to the promotion rule.
	 *
	 * @param product the product
	 * @param exceptions the exceptions
	 * @return true if the product's categories have been excluded
	 */
	protected boolean productIsInCategoryExcludedFromPromotion(final Product product, final PromotionRuleExceptions exceptions) {
		final Catalog catalog = getShoppingCart().getStore().getCatalog();

		for (final Category category : product.getCategories(catalog)) {
			if (exceptions.isCategoryExcluded(category)) {
				return true;
			}
		}

		return false;
	}

	public ShoppingCart getShoppingCart() {
		return cart;
	}

	/**
	 * Sets the shopping cart.
	 *
	 * @param cart the cart to set
	 */
	@Override
	public void setShoppingCart(final ShoppingCart cart) {
		this.cart = cart;
	}

	protected CartDirector getCartDirector() {
		return cartDirector;
	}

	public void setCartDirector(final CartDirector cartDirector) {
		this.cartDirector = cartDirector;
	}

	protected ShoppingItemDtoFactory getShoppingItemDtoFactory() {
		return shoppingItemDtoFactory;
	}

	public void setShoppingItemDtoFactory(final ShoppingItemDtoFactory shoppingItemDtoFactory) {
		this.shoppingItemDtoFactory = shoppingItemDtoFactory;
	}

	protected ProductSkuLookup getProductSkuLookup() {
		return productSkuLookup;
	}

	public void setProductSkuLookup(final ProductSkuLookup productSkuLookup) {
		this.productSkuLookup = productSkuLookup;
	}

	protected Currency getCurrency() {
		return currency;
	}

	@Override
	public void setCurrency(final Currency currency) {
		this.currency = currency;
	}

	protected ShoppingItemSubtotalCalculator getShoppingItemsSubtotalCalculator() {
		return this.shoppingItemsSubtotalCalculator;
	}

	public void setShoppingItemsSubtotalCalculator(final ShoppingItemSubtotalCalculator shoppingItemsSubtotalCalculator) {
		this.shoppingItemsSubtotalCalculator = shoppingItemsSubtotalCalculator;
	}

	protected Predicate<ShoppingItem> getShippableItemPredicate() {
		return this.shippableItemPredicate;
	}

	public void setShippableItemPredicate(final Predicate<ShoppingItem> shippableItemPredicate) {
		this.shippableItemPredicate = shippableItemPredicate;
	}
}
