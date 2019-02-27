/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.shoppingcart.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.Transient;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.coupon.specifications.PotentialCouponUse;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.domain.rules.CouponUsageType;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.domain.shipping.evaluator.impl.ShoppingCartShipmentTypeEvaluator;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.DiscountRecord;
import com.elasticpath.domain.shoppingcart.MutablePromotionRecordContainer;
import com.elasticpath.domain.shoppingcart.PromotionRecordContainer;
import com.elasticpath.domain.shoppingcart.ShippingPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartMemento;
import com.elasticpath.domain.shoppingcart.ShoppingCartMementoHolder;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCartVisitor;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItemTaxSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingList;
import com.elasticpath.domain.specifications.Specification;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.money.Money;
import com.elasticpath.plugin.payment.exceptions.GiftCertificateCurrencyMismatchException;
import com.elasticpath.plugin.payment.exceptions.GiftCertificateZeroBalanceException;
import com.elasticpath.plugin.tax.builder.TaxExemptionBuilder;
import com.elasticpath.plugin.tax.domain.TaxExemption;
import com.elasticpath.sellingchannel.ShoppingItemFactory;
import com.elasticpath.service.catalog.GiftCertificateService;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.order.impl.ShoppingItemHasRecurringPricePredicate;
import com.elasticpath.service.pricing.PriceLookupService;
import com.elasticpath.service.rules.CouponService;
import com.elasticpath.service.rules.CouponUsageService;
import com.elasticpath.service.rules.RuleService;
import com.elasticpath.service.shoppingcart.OrderSkuFactory;
import com.elasticpath.service.shoppingcart.ShoppingItemSubtotalCalculator;
import com.elasticpath.service.tax.DiscountApportioningCalculator;
import com.elasticpath.service.tax.TaxCalculationResult;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * A Shopping Cart contains both transient and persistable data. Since some persistence layers, upon merge or update, will return a brand new
 * instance of the object being merged or updated with the database records, we don't want to lose the transient data every time we do a save or
 * update. Therefore we introduce with this implementation the concept of a ShoppingCartMemento, which represents the persistable portion of the
 * ShoppingCart. When the shopping cart is saved to the database, in reality we only have to save the Memento portion and leave the transient fields
 * in the class instance, simply re-injecting the Memento portion that is returned from the persistence layer. Note: The guid of a shopping cart is
 * actually the guid of the customer session it belongs to.
 */
@SuppressWarnings({ "PMD.TooManyFields", "PMD.ExcessiveClassLength", "PMD.TooManyMethods",
					"PMD.ExcessivePublicCount", "PMD.CouplingBetweenObjects", "PMD.ExcessiveImports", "PMD.GodClass" })
public class ShoppingCartImpl extends AbstractEpDomainImpl implements ShoppingCart, ShoppingCartPricingSnapshot,
		ShoppingCartTaxSnapshot, ShoppingCartMementoHolder {

	private static final long serialVersionUID = 5000000002L;

	private static final Logger LOG = Logger.getLogger(ShoppingCartImpl.class.getName());

	private CustomerSession customerSession;

	private BigDecimal subtotalDiscount = BigDecimal.ZERO;

	private Address shippingAddress;

	private Address billingAddress;

	private Order completedOrder;

	private ShippingOption selectedShippingOption;

	/** The map of rule id to promotion codes added to the cart. */
	private final Map<Long, Set<String>> promotionCodes = new HashMap<>();

	/** Specifies if the gift certificate or promotion code entered is valid. */
	private boolean codeValid = true;

	private boolean estimateMode;

	private final Set<GiftCertificate> appliedGiftCertificates = new HashSet<>();

	private BigDecimal appliedGiftCertificateTotal = BigDecimal.ZERO;

	private TaxCalculationResult taxCalculationResult;

	private TaxExemption taxExemption;

	private Long cmUserUID;

	private BigDecimal shippingCostOverride;

	private boolean exchangeOrderShoppingCart;

	private ShoppingCartMemento shoppingCartMemento;

	private transient GiftCertificateService giftCertificateService;

	private transient RuleService ruleService;

	private final Map<String, ShippingPricing> shippingPricingMap = new HashMap<>();

	/**
	 * Holds references to cart items manually removed by customers. Used by promotion rules so that a free cart item could be removed manually by
	 * customer. Used also by templates (viewCart.vm)
	 */
	private final Collection<String> removedCartItemSkus = new HashSet<>();

	private transient CouponUsageService couponUsageService;
	private transient CouponService couponService;
	private transient ProductSkuLookup productSkuLookup;
	private transient DiscountApportioningCalculator discountCalculator;

	private Shopper shopper;
	private Store store;

	@Transient
	private final PromotionRecordContainerImpl promotionRecordContainer;

	private boolean mergedNotification;

	private boolean itemWithNoTierOneFromWishList;

	private transient ShoppingItemSubtotalCalculator shoppingItemSubtotalCalculator;
	private transient Predicate<ShoppingItem> shippableItemPredicate;

	private final ShoppingItemHasRecurringPricePredicate shoppingItemHasRecurringPricePredicate;

	/**
	 * Default Constructor.
	 */
	@SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
	public ShoppingCartImpl() {
		super();
		promotionRecordContainer = createPromotionRecordContainer();
		shoppingItemHasRecurringPricePredicate = new ShoppingItemHasRecurringPricePredicate();
	}

	/**
	 * Gets the cmUserUID.
	 *
	 * @return CmUserUID the cmUser's uid
	 */
	@Override
	public Long getCmUserUID() {
		return cmUserUID;
	}

	/**
	 * Sets the CmUserUID.
	 *
	 * @param cmUserUID the cmUser's uid
	 */
	@Override
	public void setCmUserUID(final Long cmUserUID) {
		this.cmUserUID = cmUserUID;
	}

	/**
	 * Get the applied gift certificate.
	 *
	 * @return the appliedGiftCertificates
	 */
	@Override
	public Set<GiftCertificate> getAppliedGiftCertificates() {
		return appliedGiftCertificates;
	}

	/**
	 * Get the gift certificate service.
	 *
	 * @return GiftCertificateService
	 */
	protected GiftCertificateService getGiftCertificateService() {
		// Note that the service field must be transient and included in ShoppingCartImplIntegrationTest.testSerialization().
		if (giftCertificateService == null) {
			giftCertificateService = this.getBean(ContextIdNames.GIFT_CERTIFICATE_SERVICE);
		}
		return giftCertificateService;
	}

	/**
	 * Get the rule service.
	 *
	 * @return RuleService
	 */
	protected RuleService getRuleService() {
		// Note that the service field must be transient and included in ShoppingCartImplIntegrationTest.testSerialization().
		if (ruleService == null) {
			ruleService = this.getBean(ContextIdNames.RULE_SERVICE);
		}
		return ruleService;
	}

	/**
	 * Get the map of shipping options to shipping pricing.
	 *
	 * @return the map of shipping options to shipping pricing
	 */
	protected Map<String, ShippingPricing> getShippingPricingMap() {
		return shippingPricingMap;
	}

	/**
	 * Add the gift certificate to the set which will be redeemed.
	 *
	 * @param giftCertificate the gift certificate to be redeemed.
	 * @throws {@link com.elasticpath.domain.EpDomainException} when the currency mismatch or balance is zero.
	 */
	@Override
	public void applyGiftCertificate(final GiftCertificate giftCertificate) {
		if (giftCertificate == null || !ObjectUtils.equals(giftCertificate.getStore(), getStore())) {
			return;
		}
		if (!getCustomerSession().getCurrency().getCurrencyCode().equals(giftCertificate.getCurrencyCode())) {
			throw new GiftCertificateCurrencyMismatchException("Currency mismatch, the current shopping cart can't accept this gift certificate.");
		}

		if (getGiftCertificateService().getBalance(giftCertificate).compareTo(BigDecimal.ZERO) <= 0) {
			throw new GiftCertificateZeroBalanceException("This gift certificate has a zero balance.");
		}
		if (!getAppliedGiftCertificates().contains(giftCertificate)) {
			getAppliedGiftCertificates().add(giftCertificate);
		}

		// Calculate the gift certificate values.
		calculateAppliedGcTotal();

	}

	private void calculateAppliedGcTotal() {
		appliedGiftCertificateTotal = BigDecimal.ZERO;
		for (GiftCertificate appliedGc : getAppliedGiftCertificates()) {
			appliedGiftCertificateTotal = appliedGiftCertificateTotal.add(getGiftCertificateService().getBalance(appliedGc));
		}
	}

	/**
	 * Return the <code>CustomerSession</code>. instance. Customer sessions track information about sessions where the customer may not be logged
	 * in.
	 *
	 * @return the <code>CustomerSession</code> instance
	 * @deprecated
	 */
	@Override
	@Deprecated
	public CustomerSession getCustomerSession() {
		return customerSession;
	}

	/**
	 * Set the <code>CustomerSession</code>. instance. Customer sessions track information about sessions where the customer may not be logged in.
	 *
	 * @param customerSession the <code>CustomerSession</code> instance
	 * @deprecated
	 */
	@Override
	@Deprecated
	public void setCustomerSession(final CustomerSession customerSession) {
		this.customerSession = customerSession;
		if (customerSession != null) {
			setShopper(customerSession.getShopper());
		}
	}

	/**
	 * Returns a stream of all shopping items from all depths of the shopping item tree, including dependent items, bundles, and bundle
	 * constituents.
	 *
	 * @return a stream of shopping items
	 * @see #getRootShoppingItems()
	 * @see #getAllShoppingItems()
	 */
	protected Stream<ShoppingItem> getAllShoppingItemsStream() {
		return flatten(getRootShoppingItems(), item -> item.getChildren().stream());
	}

	@Override
	public Collection<ShoppingItem> getAllShoppingItems() {
		return getAllShoppingItemsStream()
				.collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
	}

	@Override
	public Collection<ShoppingItem> getShoppingItems(final Predicate<ShoppingItem> shoppingItemPredicate) {
		return getAllShoppingItemsStream()
				.filter(shoppingItemPredicate)
				.collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
	}

	/**
	 * Flattens the tree of shopping items into a single stream.
	 *
	 * @param items a collection of {@link ShoppingItem}
	 * @param itemTraversalStreamFunction the function used to traverse the sub-items of each shopping item
	 * @return a Stream of shopping items
	 */
	protected Stream<ShoppingItem> flatten(final Collection<? extends ShoppingItem> items,
										   final Function<ShoppingItem, Stream<? extends ShoppingItem>> itemTraversalStreamFunction) {

		return Stream.concat(items.stream(), flattenInternal(items, itemTraversalStreamFunction));
	}

	/**
	 * <p>Recursive method that traverses a collection of shopping items via the provided function.</p>
	 * <p>Callers should <em>not</em> call this directly, but instead call {@link #flatten(Collection, Function)}.</p>
	 *
	 * @param items a a collection of {@link ShoppingItem}
	 * @param itemTraversalStreamFunction the function used to traverse the sub-items of each shopping item
	 * @return a Stream of shopping items
	 */
	protected Stream<ShoppingItem> flattenInternal(final Collection<? extends ShoppingItem> items,
												   final Function<ShoppingItem, Stream<? extends ShoppingItem>> itemTraversalStreamFunction) {
		final Stream<ShoppingItem> childrenStream = items.stream().flatMap(itemTraversalStreamFunction);

		final Stream<ShoppingItem> recursiveDescendantsStream = items.stream().flatMap(
				item -> flattenInternal(itemTraversalStreamFunction.apply(item)
												.collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList)),
										itemTraversalStreamFunction));

		return Stream.concat(childrenStream, recursiveDescendantsStream);
	}

	/**
	 * Get the products in the shopping cart.
	 *
	 * @return the products in the shopping cart
	 */
	@Override
	public List<Product> getCartProducts() {
		List<Product> cartProducts = new ArrayList<>();
		for (Map.Entry<ShoppingItem, ProductSku> mapEntry : getShoppingItemProductSkuMap().entrySet()) {
			ProductSku productSku = mapEntry.getValue();
			cartProducts.add(productSku.getProduct());
		}
		return cartProducts;
	}

	/**
	 *
	 * @deprecated use addShoppingCartItem instead
	 */
	@Override
	@Deprecated
	public ShoppingItem addCartItem(final ShoppingItem cartItem) {
		return addShoppingCartItem(cartItem);
	}

	@Override
	public ShoppingItem addShoppingCartItem(final ShoppingItem cartItem) {
		if (isCartItem(cartItem)) {
			((CartItem) cartItem).setCartUid(getShoppingCartMemento().getUidPk());
		}
		getCartMementoItems(getShoppingCartMemento()).add(cartItem);

		// if we get a cart item manually added by a customer we should remove it from the list of manually removed cart items
		ProductSku cartItemSku = getProductSkuLookup().findByGuid(cartItem.getSkuGuid());
		getRemovedCartItemSkus().remove(cartItemSku.getSkuCode());

		// should clear the shipping and tax estimation.
		estimateMode = false;

		return cartItem;
	}

	private boolean isCartItem(final ShoppingItem cartItem) {
		return cartItem instanceof CartItem;
	}

	/**
	 * Remove an item from the cart.
	 *
	 * @param itemUid the uidPk of the <code>CartItem</code> to remove
	 */
	@Override
	public void removeCartItem(final long itemUid) {
		final Optional<? extends ShoppingItem> optionalShoppingItem = getAllShoppingItemsStream()
				.filter(shoppingItem -> shoppingItem.getUidPk() == itemUid)
				.findAny();

		optionalShoppingItem.ifPresent(shoppingItem -> internalRemoveCartItem(shoppingItem, true));

		// should clear the shipping and tax estimation.
		estimateMode = false;
	}

	/**
	 * Remove an item from the cart.
	 *
	 * @param lineItemGuid the guid of the <code>CartItem</code> to remove
	 */
	@Override
	public void removeCartItem(final String lineItemGuid) {
		final Optional<? extends ShoppingItem> optionalShoppingItem = getAllShoppingItemsStream()
				.filter(shoppingItem -> shoppingItem.getGuid().equals(lineItemGuid))
				.findAny();

		optionalShoppingItem.ifPresent(shoppingItem -> internalRemoveCartItem(shoppingItem, true));

		// should clear the shipping and tax estimation.
		estimateMode = false;
	}

	@Override
	public void removeCartItems(final Collection<String> lineItemGuids) {
		getAllShoppingItemsStream()
				.filter(shoppingItem -> lineItemGuids.contains(shoppingItem.getGuid()))
				.forEach(shoppingItem -> internalRemoveCartItem(shoppingItem, true));

		// should clear the shipping and tax estimation.
		estimateMode = false;
	}

	/**
	 * Get the cart items that have been removed from the shopping cart.
	 *
	 * @return the removed cart items in the shopping cart
	 */
	protected Collection<String> getRemovedCartItemSkus() {
		return removedCartItemSkus;
	}

	private void internalRemoveCartItem(final ShoppingItem currItem, final boolean addToRemovedList) {
		// Remove any cross-referenced dependent items
		final List<ShoppingItem> cartItems = getCartMementoItems(getShoppingCartMemento());
		for (ShoppingItem item : cartItems) {
			item.getChildren().remove(currItem);
		}
		// cleanup warranties and other dependent cart items
		for (ShoppingItem item : currItem.getChildren()) {
			cartItems.remove(item);
		}
		// TOD0: write test to determine if JPA will delete dependent items, when the parent is deleted
		currItem.getChildren().clear();
		cartItems.remove(currItem);

		if (addToRemovedList) {
			// saves the removed cart item so that it does not get back if a promotion has a rule to add a free product
			ProductSku sku = getProductSkuLookup().findByGuid(currItem.getSkuGuid());
			getRemovedCartItemSkus().add(sku.getSkuCode());
		}
	}

	@Override
	public boolean isEmpty() {
		return getRootShoppingItems().isEmpty();
	}

	@Override
	public int getNumItems() {
		return getAllShoppingItemsStream()
				.filter(shoppingItem -> !shoppingItem.isBundleConstituent())
				.mapToInt(ShoppingItem::getQuantity)
				.sum();
	}

	@Override
	public Money getSubtotalMoney() {
		return getShoppingItemSubtotalCalculator().calculate(getApportionedLeafItems(), this, getCustomerSession().getCurrency());
	}

	@Override
	public BigDecimal getSubtotal() {
		return getSubtotalMoney().getAmount();
	}

	/**
	 * Get the sub total of all items in the cart after shipping, promotions, etc.
	 *
	 * @return a <code>Money</code> object representing the total
	 */
	@Override
	public Money getTotalMoney() {
		return Money.valueOf(getTotal(), getCustomerSession().getCurrency());
	}

	/**
	 * Get the sub total of all items in the cart after shipping, promotions, etc.
	 *
	 * @return a <code>BigDecimal</code> object representing the total
	 */
	@Override
	public BigDecimal getTotal() {
		final BigDecimal totalBeforeRedeem = getTotalBeforeRedeem();
		final BigDecimal zero = BigDecimal.ZERO.setScale(totalBeforeRedeem.scale());

		BigDecimal total = zero;
		if (totalBeforeRedeem.compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal redeemAmount = getGiftCertificateDiscount();
			total = totalBeforeRedeem.subtract(redeemAmount);

			if (total.compareTo(BigDecimal.ZERO) < 0) {
				return zero;
			}
		}

		return total;
	}

	@Override
	public ShoppingCartPricingSnapshot getShoppingCartPricingSnapshot() {
		return this;
	}

	@Override
	public ShoppingItemTaxSnapshot getShoppingItemTaxSnapshot(final ShoppingItem item) {
		return (ShoppingItemTaxSnapshot) item;
	}

	/**
	 * The total amount before gift certificate redemption.
	 *
	 * @return total amount before gift certificate redemption
	 */
	BigDecimal getTotalBeforeRedeem() {
		BigDecimal subtotal = getSubtotal();
		BigDecimal shippingCost = getShippingCost().getAmount();

		if (getTaxCalculationResult().isTaxInclusive()) {
			return subtotal.subtract(getSubtotalDiscount()).add(shippingCost);
		}
		BigDecimal totalTaxes = getTaxCalculationResult().getTotalTaxes().getAmount();
		return subtotal.subtract(getSubtotalDiscount()).add(shippingCost).add(totalTaxes);
	}

	/**
	 * Applies a discount to the shopping cart subtotal.
	 *
	 * @param discountAmount the amount to discount the subtotal by as a BigInteger
	 * @param ruleId The rule which caused this subtotal discount.
	 * @param actionId The rule action which caused this subtotal discount.
	 */
	@Override
	public void setSubtotalDiscount(final BigDecimal discountAmount, final long ruleId, final long actionId) {

		if (discountAmount == null) {
			throw new EpServiceException("Cannot set discount to NULL");
		}

		if (discountAmount.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("Can not set a negative subtotal discount");
		}

		// Don't set the discount to a smaller value than the previously existing discount
		if (getSubtotalDiscount().compareTo(discountAmount) > 0) {
			markDiscountSuperceded(discountAmount, ruleId, actionId);

			return;
		}

		BigDecimal actualDiscountAmount = discountAmount;
		// Prevent clients from setting the discount greater than the subtotal
		final BigDecimal subtotal = getSubtotal();
		if (actualDiscountAmount.compareTo(subtotal) > 0) {
			actualDiscountAmount = subtotal;
			LOG.warn("Attempt to set subtotal discount greater than subtotal");

			if (actualDiscountAmount.compareTo(BigDecimal.ZERO) == 0) {
				// Mark as superceded.
				markDiscountSuperceded(discountAmount, ruleId, actionId);
				return;
			}
		}

		if (!isExchangeOrderShoppingCart()) {
			recordDiscount(ruleId, actionId, actualDiscountAmount);
		}

		subtotalDiscount = actualDiscountAmount;
	}

	private void recordDiscount(final long ruleId, final long actionId,
			final BigDecimal actualDiscountAmount) {
		SubtotalDiscountRecordImpl subtotalDiscountRecordImpl = new SubtotalDiscountRecordImpl(ruleId, actionId, actualDiscountAmount);

		supersedePreviousDiscountRecordOfType(SubtotalDiscountRecordImpl.class);

		promotionRecordContainer.addDiscountRecord(subtotalDiscountRecordImpl);
	}

	private void markDiscountSuperceded(final BigDecimal discountAmount,
			final long ruleId, final long actionId) {
		SubtotalDiscountRecordImpl subtotalDiscountRecordImpl = new SubtotalDiscountRecordImpl(ruleId, actionId, discountAmount);
		subtotalDiscountRecordImpl.setSuperceded(true);
		promotionRecordContainer.addDiscountRecord(subtotalDiscountRecordImpl);
	}

	private void supersedePreviousDiscountRecordOfType(final Class<?> clazz) {
		// Mark any previous subtotal discount record as superceded because we are overriding it.
		// Note that drools appears to do much of the conflict resolution for us.
		// However, this code allows the DiscountRecords to follow what the code itself does.
		for (DiscountRecord discountRecord : promotionRecordContainer.getAllDiscountRecords()) {
			if (clazz.isInstance(discountRecord)) {
				supersedeDiscountRecord(discountRecord);
			}
		}
	}

	private void supersedePreviousDiscountRecordsForShippingOption(final String shippingOptionCode) {
		for (DiscountRecord existingDiscountRecord : promotionRecordContainer.getAllDiscountRecords()) {
			if (existingDiscountRecord instanceof ShippingDiscountRecordImpl) {
				final ShippingDiscountRecordImpl existingShippingDiscountRecord = (ShippingDiscountRecordImpl) existingDiscountRecord;
				if (existingShippingDiscountRecord.getShippingOptionCode().equals(shippingOptionCode)) {
					supersedeDiscountRecord(existingShippingDiscountRecord);
				}
			}
		}
	}

	private void supersedeDiscountRecord(final DiscountRecord discountRecord) {
		if (discountRecord instanceof AbstractDiscountRecordImpl) {
			((AbstractDiscountRecordImpl) discountRecord).setSuperceded(true);
		}
	}

	/**
	 * Factory method to create a new {@link PromotionRecordContainerImpl} instance.
	 *
	 * @return a new {@link PromotionRecordContainerImpl} instance
	 */
	protected PromotionRecordContainerImpl createPromotionRecordContainer() {
		return new PromotionRecordContainerImpl(this);
	}

	@Override
	public PromotionRecordContainer getPromotionRecordContainer() {
		return promotionRecordContainer;
	}

	/**
	 * Returns a mutable version of the PromotionRecordContainer.
	 *
	 * @return a mutable version of the PromotionRecordContainer
	 */
	public MutablePromotionRecordContainer getMutablePromotionRecordContainer() {
		return promotionRecordContainer;
	}

	/**
	 * Get the discount to the shopping cart subtotal.
	 *
	 * @return the amount discounted from the subtotal
	 */
	@Override
	public BigDecimal getSubtotalDiscount() {
		return subtotalDiscount;
	}

	/**
	 * Get the amount discounted from the order subtotal.
	 *
	 * @return the order subtotal discount as a <code>Money</code> object
	 */
	@Override
	public Money getSubtotalDiscountMoney() {
		return createMoney(getSubtotalDiscount());
	}

	/**
	 * Get the amount redeemed from gift certificate.
	 *
	 * @return the gift certificate discounted from the total
	 */
	@Override
	public BigDecimal getGiftCertificateDiscount() {
		BigDecimal totalBeforeRedeem = getTotalBeforeRedeem();
		if (appliedGiftCertificateTotal.compareTo(totalBeforeRedeem) > 0) {
			return totalBeforeRedeem;
		}
		return appliedGiftCertificateTotal;
	}

	/**
	 * Get the amount redeemed from gift certificate.
	 *
	 * @return the gift certificate discount as a <code>Money</code> object
	 */
	@Override
	public Money getGiftCertificateDiscountMoney() {
		return createMoney(getGiftCertificateDiscount());
	}

	/**
	 * Returns true if an order subtotal discount has been applied.
	 *
	 * @return true if an order subtotal discount has been applied
	 */
	@Override
	public boolean hasSubtotalDiscount() {
		return BigDecimal.ZERO.compareTo(getSubtotalDiscount()) < 0;
	}

	/**
	 * Clears promotions.
	 */
	public void clearPromotions() {
		// Clear promotion information
		promotionRecordContainer.clear();
		shippingPricingMap.clear();

		// Clear cart subtotal discount
		subtotalDiscount = BigDecimal.ZERO;

		// Clear discounts associated with any items that may be in the cart
		getAllShoppingItemsStream().forEach(ShoppingItem::clearDiscount);
	}

	/**
	 * Empties the shopping cart (e.g. after a checkout)
	 */
	@Override
	public void clearItems() {
		taxCalculationResult = getNewTaxCalculationResult();
		getCartMementoItems(getShoppingCartMemento()).clear();
		promotionCodes.clear();
		clearPromotions();
		getAppliedGiftCertificates().clear();
		appliedGiftCertificateTotal = BigDecimal.ZERO;
		shippingPricingMap.clear();
	}

	private TaxCalculationResult getNewTaxCalculationResult() {
		TaxCalculationResult taxCalculationResult = getBean(ContextIdNames.TAX_CALCULATION_RESULT);
		taxCalculationResult.initialize(getCustomerSession().getCurrency());
		return taxCalculationResult;
	}

	/**
	 * Removes shipping and tax estimates from the shopping cart.
	 */
	@Override
	public void clearEstimates() {
		if (!estimateMode) {
			estimateMode = false;
			setShippingAddress(null);
			setBillingAddress(null);
			clearSelectedShippingOption();
		}
	}

	/**
	 * Set the shipping address.
	 *
	 * @param address the <code>Address</code>
	 */
	@Override
	public void setShippingAddress(final Address address) {
		// Clears available and selected shipping options if the address has changed since they need to be recalculated
		if (!Objects.equal(shippingAddress, address)) {
			clearSelectedShippingOption();
		}
		shippingAddress = address;
	}

	/**
	 * Get the shipping address.
	 *
	 * @return the preferred shipping address
	 */
	@Override
	public Address getShippingAddress() {
		return shippingAddress;
	}

	/**
	 * Set the billing address.
	 *
	 * @param address the <code>Address</code>
	 */
	@Override
	public void setBillingAddress(final Address address) {
		billingAddress = address;
	}

	/**
	 * Get the billing address.
	 *
	 * @return the billing address
	 */
	@Override
	public Address getBillingAddress() {
		return billingAddress;
	}

	/**
	 * Set a reference to the completed order for the items previously checked out.
	 *
	 * @param order the completed order
	 */
	@Override
	public void setCompletedOrder(final Order order) {
		completedOrder = order;
	}

	/**
	 * Get a reference to the completed order for the items previously checked out.
	 *
	 * @return the completed Order, or null if no completed order has been set.
	 */
	@Override
	public Order getCompletedOrder() {
		return completedOrder;
	}

	/**
	 * Return the shippingCost of the <code>ShoppingCart</code>.
	 *
	 * @return the shippingCost of the <code>ShoppingCart</code>
	 */
	@Override
	public Money getShippingCost() {
		if (shippingCostOverride != null) {
			return createMoney(shippingCostOverride);
		}

		final Optional<ShippingOption> optSelectedShippingOption = getSelectedShippingOption();
		if (requiresShipping() && optSelectedShippingOption.isPresent()) {
			// Return the shippingCost in cart for the selected shipping option
			return getShippingPricingSnapshot(optSelectedShippingOption.get()).getShippingPromotedPrice();
		}

		return createMoney(BigDecimal.ZERO);
	}

	@Override
	public Optional<ShippingOption> getSelectedShippingOption() {
		return Optional.ofNullable(selectedShippingOption);
	}

	@Override
	public void setSelectedShippingOption(final ShippingOption selectedShippingOption) {
		this.selectedShippingOption = selectedShippingOption;
	}

	/**
	 * Resets the selected shipping option to {code null}.
	 */
	@Override
	public void clearSelectedShippingOption() {
		setSelectedShippingOption(null);
	}

	/**
	 * Get the totalWeight of items in <code>ShoppingCart</code>.
	 *
	 * @return totalWeight
	 */

	@Override
	public BigDecimal getTotalWeight() {
		return getShoppingItemProductSkuMap().entrySet().stream()
				.filter(entry -> entry.getValue().isShippable())
				.filter(entry -> entry.getValue().getWeight() != null)
				.filter(entry -> entry.getValue().getWeight().compareTo(BigDecimal.ZERO) > 0)
				.map(entry -> getWeight(entry.getValue(), entry.getKey().getQuantity()))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	/**
	 * Calculates the weight for a certain quantity of a given product SKU.
	 *
	 * @param productSku the SKU
	 * @param quantity the quantity of the SKU to calculate
	 * @return the weight
	 */
	protected BigDecimal getWeight(final ProductSku productSku, final int quantity) {
		return productSku.getWeight().multiply(BigDecimal.valueOf(quantity));
	}

	/**
	 * Returns true if the cart contains items that must be shipped to the customer.
	 *
	 * @return true if the cart contains items that must be shipped to the customer.
	 */
	@Override
	public boolean requiresShipping() {
		return getAllShoppingItemsStream()
				.anyMatch(shoppingItem -> shoppingItem.isShippable(getProductSkuLookup()));
	}

	/**
	 * Get the all cart items by the code of its SKU.
	 *
	 * @param skuCode the code of the SKU in the cart item to be retrieved.
	 * @return the corresponding list of <code>ShoppingItem</code> found
	 */
	@Override
	public List<ShoppingItem> getCartItems(final String skuCode) {
		return getShoppingItemProductSkuMap().entrySet().stream()
				.filter(entry -> entry.getValue().getSkuCode().equals(skuCode))
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());
	}

	@Override
	public List<ShoppingItem> getCartItemsBySkuGuid(final String skuGuid) {
		return getAllShoppingItemsStream()
				.filter(shoppingItem -> shoppingItem.getSkuGuid().equals(skuGuid))
				.collect(Collectors.toList());
	}

	/**
	 * Return true if the "inclusive" tax calculation method is in use; otherwise false. This is based on the shippingAddress. If there is no
	 * taxJurisdiction set, return false by default.
	 *
	 * @return true if the "inclusive" tax calculation method is in use; otherwise false.
	 */
	@Override
	public boolean isInclusiveTaxCalculationInUse() {
		return getTaxCalculationResult().isTaxInclusive();
	}

	/**
	 * Return the <code>TaxCategory</code> -> tax value (<code>Money</code>) map for this <code>ShoppingCart</code>.
	 *
	 * @return the <code>TaxCategory</code> -> tax value (<code>Money</code>) map. Never <code>null</code>.
	 */

	@Override
	public Map<TaxCategory, Money> getTaxMap() {
		Map<TaxCategory, Money> taxMap = getTaxCalculationResult().getTaxMap();

		if (null == taxMap) {
			taxMap = Collections.emptyMap();
		}
		return taxMap;
	}

	/**
	 * Return the localized tax category name -> tax value (<code>Money</code>) map for this <code>ShoppingCart</code>.
	 *
	 * @return the localized tax category name -> tax value (<code>Money</code>) map.
	 */

	@Override
	public Map<String, Money> getLocalizedTaxMap() {
		final Map<TaxCategory, Money> taxMap = getTaxMap();

		final Map<String, Money> sortedMap = new TreeMap<>();
		for (final Map.Entry<TaxCategory, Money> taxCategoryEntry : taxMap.entrySet()) {
			sortedMap.put(taxCategoryEntry.getKey().getDisplayName(getCustomerSession().getLocale()), taxCategoryEntry.getValue());
		}
		return sortedMap;
	}

	/**
	 * Return the before-tax subtotal.
	 *
	 * @return the before-tax subtotal.
	 */
	@Override
	public Money getBeforeTaxSubTotal() {
		return getShoppingItemSubtotalCalculator().calculate(getApportionedLeafItems(), this, getCustomerSession().getCurrency());
	}

	/**
	 * Return the before-tax shippingCost.
	 *
	 * @return the before-tax shippingCost.
	 */
	@Override
	public Money getBeforeTaxShippingCost() {
		return getTaxCalculationResult().getBeforeTaxShippingCost();
	}

	/**
	 * Return the before-tax total.
	 *
	 * @return the before-tax total.
	 */
	@Override
	public Money getBeforeTaxTotal() {
		Money total = createMoney(getTotalBeforeRedeem());

		return total.subtract(getTaxCalculationResult().getTotalTaxes());
	}

	/**
	 * For electronic shipments we use the billing address, but
	 * will fall back to the shipping address if we don't have a billing address.
	 * @return Address tax address for electronic
	 */
	public Address getElectronicTaxAddress() {
		Address address = getBillingAddress();
		if (address == null) {
			address = getShippingAddress();
		}
		return address;
	}

	private void flattenOrderSkuTree(final Collection<? extends ShoppingItem> rootItems, final Collection<ShoppingItem> leafCollection) {
		flatten(rootItems, item -> item.getChildren().stream())
				.filter(shoppingItem -> !shoppingItem.isBundle(getProductSkuLookup()))
				.forEach(leafCollection::add);
	}

	/**
	 * Returns the cart item with the given id.
	 *
	 * @param cartItemId the cart item id
	 * @return the cart item with the given id
	 */
	@Override
	public ShoppingItem getCartItemById(final long cartItemId) {
		return getAllShoppingItemsStream()
				.filter(shoppingItem -> shoppingItem.getUidPk() == cartItemId)
				.findAny()
				.orElse(null);
	}

	/**
	 *
	 * @deprecated call {{@link #getShoppingItemByGuid(String)} instead.
	 */
	@Override
	@Deprecated
	public ShoppingItem getCartItemByGuid(final String cartItemGuid) {
		return getShoppingItemByGuid(cartItemGuid);
	}

	@Override
	public ShoppingItem getShoppingItemByGuid(final String itemGuid) {
		return getAllShoppingItemsStream()
				.filter(shoppingItem -> shoppingItem.getGuid().equals(itemGuid))
				.findAny()
				.orElse(null);
	}

	/**
	 * Indicates that the given rule was applied by the promotion rule engine.
	 *
	 * @param ruleId the uidPk of the <code>Rule</code>
	 * @param actionId the id of the action
	 * @param discountedItem The item that was discounted
	 * @param discountAmount The amount of the discount.
	 * @param quantityAppliedTo The quantity of the item that the discount was applied to.
	 */
	@Override
	public void ruleApplied(final long ruleId, final long actionId,
			final ShoppingItem discountedItem, final BigDecimal discountAmount,
			final int quantityAppliedTo) {
		DiscountRecord discountRecord = promotionRecordContainer.getDiscountRecord(ruleId, actionId);
		if (discountRecord == null) {
			discountRecord = new ItemDiscountRecordImpl(discountedItem, ruleId, actionId, discountAmount, quantityAppliedTo);
			promotionRecordContainer.addDiscountRecord(discountRecord);
		} else if (discountRecord instanceof ItemDiscountRecordImpl) {
			ItemDiscountRecordImpl itemDiscountRecord = (ItemDiscountRecordImpl) discountRecord;
			increaseItemDiscountRecordQuantity(itemDiscountRecord, quantityAppliedTo);
		}
	}

	private void increaseItemDiscountRecordQuantity(final ItemDiscountRecordImpl itemDiscountRecord, final int quantity) {
		itemDiscountRecord.setQuantityAppliedTo(itemDiscountRecord.getQuantityAppliedTo() + quantity);
	}

	/**
	 * Sets the list price (i.e. regular price, prior to promotions) of a shipping option.
	 *
	 * @param shippingOptionCode the code corresponding to the shipping option for which pricing should be set
	 * @param listPrice the list price
	 */
	public void setShippingListPrice(final String shippingOptionCode, final Money listPrice) {
		final ShippingPricing shippingPricing = findShippingPricing(shippingOptionCode);
		shippingPricing.setListPrice(listPrice);
	}

	/**
	 * Clears any stored list prices for shipping options.
	 */
	public void clearShippingListPrices() {
		getShippingPricingMap().clear();
	}

	/**
	 * Sets the shipping discount for a specified shipping option, if lower than the existing discount.
	 *
	 * @param shippingOptionCode the shipping option
	 * @param ruleId the ID of the rule that triggered this shipping discount
	 * @param actionId the ID of the action that triggered this shipping discount
	 * @param discountAmount the amount of the shipping discount
	 */
	public void setShippingDiscountIfLower(final String shippingOptionCode,
										   final long ruleId,
										   final long actionId,
										   final Money discountAmount) {
		final DiscountRecord discountRecord = new ShippingDiscountRecordImpl(shippingOptionCode, ruleId, actionId, discountAmount.getAmount());
		final DiscountRecord existingDiscountRecord = promotionRecordContainer.getDiscountRecord(ruleId, actionId);

		if (discountRecord.equals(existingDiscountRecord)) {
			return;
		}

		final ShippingPricing shippingPricing = findShippingPricing(shippingOptionCode);

		if (shippingPricing.getDiscountAmount() == null || shippingPricing.getDiscountAmount().compareTo(discountAmount) < 0) {
			shippingPricing.setDiscountAmount(discountAmount);
			supersedePreviousDiscountRecordsForShippingOption(shippingOptionCode);
		} else {
			supersedeDiscountRecord(discountRecord);
		}

		promotionRecordContainer.addDiscountRecord(discountRecord);
	}

	/**
	 * Finds a {@link ShippingPricing} corresponding to the given {@link ShippingOption#getCode() Shipping Option code}.  If one can not
	 * be found,  a new instance will be created and returned.
	 *
	 * @param shippingOptionCode the code corresponding to the shipping option for which pricing should be found
	 * @return a {@link ShippingPricing} instance; never {@code null}
	 */
	private ShippingPricing findShippingPricing(final String shippingOptionCode) {
		ShippingPricing shippingPricing = getShippingPricingMap().get(shippingOptionCode);

		if (shippingPricing == null) {
			shippingPricing = new ShippingPricing(shippingOptionCode);
			getShippingPricingMap().put(shippingOptionCode, shippingPricing);
		}

		return shippingPricing;
	}

	/**
	 * Returns the currently-set list price for the given {@link ShippingOption#getCode() Shipping Option code}.
	 *
	 * @param shippingOptionCode the code corresponding to the shipping option for which pricing should be found
	 * @return the list price
	 * @throws com.elasticpath.base.exception.EpServiceException if pricing for the corresponding shipping option does not exist
	 */
	public Money getShippingListPrice(final String shippingOptionCode) {
		final ShippingPricing shippingPricing = getShippingPricingMap().get(shippingOptionCode);

		if (shippingPricing == null) {
			throw new EpServiceException("Pricing not available for Shipping Option [" + shippingOptionCode + "]");
		}

		return shippingPricing.getListPrice();
	}

	/**
	 * Gets the list of promotion codes added to the cart.
	 *
	 * @return the promotion codes
	 */
	@Override
	public Set<String> getPromotionCodes() {
		Set<String> allCodes = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		for (Set<String> codes : promotionCodes.values()) {
			allCodes.addAll(codes);
		}
		return Collections.unmodifiableSet(allCodes);
	}


	/**
	 * Add a new coupon code (aka. promotion code) to the shopping cart.
	 *
	 * @param promotionCode the coupon code (aka. promotion code) to add to the cart
	 * @return true if promotion code is valid
	 */
	@Override
	public boolean applyPromotionCode(final String promotionCode) {
		boolean isPromotionCodeApplied = false;
		if (StringUtils.isNotBlank(promotionCode)) {
			// Since the coupon code (aka. promotion code) could potentially
			// have been entered directly by a user on the front end we scrub
			// The value here with a database lookup before proceeding.
			Coupon coupon = getCouponService().findByCouponCode(promotionCode);
			isPromotionCodeApplied = applyCoupon(coupon);
		}

		return isPromotionCodeApplied;
	}

	@Override
	public boolean applyPromotionCodes(final Collection<String> promotionCodesToApply) {
		if (promotionCodesToApply == null || promotionCodesToApply.isEmpty()) {
			return false;
		}

		final Map<String, Coupon> couponCodeToCoupon = getCouponService().findCouponsForCodes(promotionCodesToApply);

		final Map<String, Rule> couponCodeToLimitedUseRule = getRuleService().getLimitedUseRulesByPromotionCodes(promotionCodesToApply);

		final String storeCode = getStore().getCode();
		final String customerEmailAddress = getCustomerEmailAddress();

		Rule limitedUseRule;
		String couponCode;
		PotentialCouponUse potentialCouponUse;

		boolean areCodesApplied = false;
		for (Coupon coupon : couponCodeToCoupon.values()) {
			potentialCouponUse = new PotentialCouponUse(coupon, storeCode, customerEmailAddress);

			if (getValidCouponUseSpecification().isSatisfiedBy(potentialCouponUse).isSuccess()) {

				couponCode = coupon.getCouponCode();
				limitedUseRule = couponCodeToLimitedUseRule.get(couponCode);

				if (limitedUseRule.hasLimitedUseCondition()) {
					createCouponUsageForCouponWithLimitedUse(coupon);
				}

				cacheCouponCode(limitedUseRule.getUidPk(), couponCode);

				areCodesApplied = true;
			}
		}

		if (areCodesApplied) {
			estimateMode = false;
		}

		return areCodesApplied;
	}

	private boolean applyCoupon(final Coupon coupon) {
		if (coupon == null) {
			return false;
		}

		final String couponCode = coupon.getCouponCode();
		if (getPromotionCodes().contains(couponCode)) {
			return true;
		}

		PotentialCouponUse potentialCouponUse = new PotentialCouponUse(coupon, getStore().getCode(), getCustomerEmailAddress());
		if (!getValidCouponUseSpecification().isSatisfiedBy(potentialCouponUse).isSuccess()) {
			return false;
		}

		final Rule limitedUseRule = getRuleService().getLimitedUseRule(couponCode);

		if (limitedUseRule.hasLimitedUseCondition()) {
			createCouponUsageForCouponWithLimitedUse(coupon);
		}

		cacheCouponCode(limitedUseRule.getUidPk(), couponCode);

		// should clear the shipping and tax estimation.
		estimateMode = false;

		return true;
	}

	private void cacheCouponCode(final long ruleId, final String couponCode) {

		Set<String> codes = promotionCodes.get(ruleId);
		if (codes == null) {
			codes = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
			promotionCodes.put(ruleId, codes);
		}
		codes.add(couponCode);
	}

	/**
	 * Remove a coupon code (aka. promotion code) from the shopping cart.
	 *
	 * @param promotionCode the coupon code (aka. promotion code) to remove from the cart.
	 */
	@Override
	public void removePromotionCode(final String promotionCode) {
		if (!StringUtils.isEmpty(promotionCode)) {
			// Since the coupon code (aka. promotion code) could potentially
			// have been entered directly by a user on the front end we scrub
			// The value here with a database lookup before proceeding.
			Coupon coupon = getCouponService().findByCouponCode(promotionCode);
			if (coupon != null) {
				removeCoupon(coupon);
			}
		}
	}

	@Override
	public boolean removePromotionCodes(final Collection<String> promotionCodesToRemove) {
		if (!promotionCodesToRemove.isEmpty()) {

			boolean codesRemoved = false;

			for (final Map.Entry<Long, Set<String>> promotionCodeEntry : promotionCodes.entrySet()) {
				final Set<String> codes = promotionCodeEntry.getValue();

				if (codes.removeAll(promotionCodesToRemove)) { //will remove 0 or more coupon codes
					codesRemoved = true;
					if (codes.isEmpty()) {
						promotionCodes.remove(promotionCodeEntry.getKey());
					}
					break;
				}
			}

			if (codesRemoved) {
				// should clear the shipping and tax estimation.
				estimateMode = false;
				return true;
			}

			return false;
		}

		return false;
	}

	private void removeCoupon(final Coupon coupon) {
		for (final Map.Entry<Long, Set<String>> promotionCodeEntry : promotionCodes.entrySet()) {
			Set<String> codes = promotionCodeEntry.getValue();
			if (codes.remove(coupon.getCouponCode())) {
				if (codes.isEmpty()) {
					promotionCodes.remove(promotionCodeEntry.getKey());
				}
				break;
			}
		}

		// should clear the shipping and tax estimation.
		estimateMode = false;
	}

	private void createCouponUsageForCouponWithLimitedUse(final Coupon coupon) {

		final String customerEmailAddress = getCustomerEmailAddress();

		final CouponConfig couponConfig = coupon.getCouponConfig();

		if (!isCustomerAnonymous() && isCouponUsageTypeLimitPerAnyUser(couponConfig)) {

			final CouponUsage couponUsage = getCouponUsageService().findByCodeAndType(couponConfig, coupon.getCouponCode(), customerEmailAddress);

			//the order of conditions is correct
			if (couponUsage == null && isCouponUsageValid(customerEmailAddress, coupon, couponUsage)) {

				// So we have a valid promotion code so now we want to
				// ensure we have a coupon usage record (with a use set to 0)
				// if required.

				CouponUsage newCouponUsage = getBean(ContextIdNames.COUPON_USAGE);
				newCouponUsage.setCustomerEmailAddress(customerEmailAddress);
				newCouponUsage.setCoupon(coupon);
				newCouponUsage.setUseCount(0);

				getCouponUsageService().add(newCouponUsage);
			}
		}
	}

	private boolean isCouponUsageTypeLimitPerAnyUser(final CouponConfig couponConfig) {
		return couponConfig.getUsageType().equals(CouponUsageType.LIMIT_PER_ANY_USER);
	}

	private boolean isCouponUsageValid(final String customerEmailAddress, final Coupon coupon, final CouponUsage couponUsage) {

		return getCouponUsageService().isValidCouponUsage(customerEmailAddress, coupon, couponUsage);
	}

	/**
	 *
	 * @return The email address from the customer session.
	 */
	String getCustomerEmailAddress() {
		if (getShopper().getCustomer() == null) {
			return null;
		}
		return getShopper().getCustomer().getEmail();
	}

	/**
	 * Determines if the customer is anonymous.
	 * @return true if the customer is anonymous
	 */
	boolean isCustomerAnonymous() {
		return getShopper().getCustomer() == null || getShopper().getCustomer().isAnonymous();
	}

	/**
	 * Indicates if the promotion or gift certificate code entered by the user is valid.
	 *
	 * @return true if the code is valid
	 */
	@Override
	public boolean isCodeValid() {
		return codeValid;
	}

	/**
	 * Set whether or not the promotion or gift certificate code entered by the user is valid.
	 *
	 * @param codeValid set to true if the code is valid
	 */
	@Override
	public void setCodeValid(final boolean codeValid) {
		this.codeValid = codeValid;
	}

	/**
	 * Get the indicator of whether in the estimate shipping and taxes mode.
	 *
	 * @return true when estimating shipping and taxes; otherwise, false.
	 */
	@Override
	public boolean isEstimateMode() {
		return estimateMode;
	}

	/**
	 * Set the indicator of whether in the estimate shipping and taxes mode. Disabling estimate mode clears the billing, shipping addresses
	 * (because they may not be full, valid addresses) and tax exemption, but tax and shipping calculations are not cleared.
	 * If you wish to clear the estimated calculations/values, then call clearEstimates().
	 *
	 * @param estimateMode true when estimating shipping and taxes; otherwise, false.
	 */
	@Override
	public void setEstimateMode(final boolean estimateMode) {
		this.estimateMode = estimateMode;
		if (!estimateMode) {
			setShippingAddress(null);
			setBillingAddress(null);
		}
	}

	/**
	 * Return the guid.
	 *
	 * @return the guid.
	 */
	@Override
	public String getGuid() {
		return getShoppingCartMemento().getGuid();
	}

	/**
	 * Retrieves the tax calculation result.
	 *
	 * @return the current tax values
	 */
	@Override
	public TaxCalculationResult getTaxCalculationResult() {
		if (taxCalculationResult == null) {
			taxCalculationResult = getNewTaxCalculationResult();
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("TaxCalculationResult: " + taxCalculationResult);
		}
		return taxCalculationResult;
	}

	/**
	 * Sets the tax calculation result.
	 *
	 * @param taxCalculationResult the tax calculation result to set
	 */
	public void setTaxCalculationResult(final TaxCalculationResult taxCalculationResult) {
		this.taxCalculationResult = taxCalculationResult;
	}

	@Override
	public TaxExemption getTaxExemption() {
		return taxExemption;
	}

	@Override
	public void setTaxExemption(final TaxExemption exemption) {
		this.taxExemption = exemption;
	}

	@Override
	public void setShippingCostOverride(final BigDecimal shippingCostOverride) {
		if (shippingCostOverride.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("Can not set a negative shipping cost");
		}

		this.shippingCostOverride = shippingCostOverride;
	}

	@Override
	public void setSubtotalDiscountOverride(final BigDecimal subtotalDiscount) {
		if (subtotalDiscount.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("Can not set a negative subtotal discount");
		}

		this.subtotalDiscount = subtotalDiscount;
	}

	@Override
	public boolean isExchangeOrderShoppingCart() {
		return exchangeOrderShoppingCart;
	}

	@Override
	public void setExchangeOrderShoppingCart(final boolean isExchangeOrderShoppingCart) {
		exchangeOrderShoppingCart = isExchangeOrderShoppingCart;
	}

	/**
	 * Gets the {@link Store} this object belongs to.
	 *
	 * @return the {@link Store}
	 */
	@Override
	public Store getStore() {
		return store;
	}

	/**
	 * Sets the {@link Store} this object belongs to.
	 *
	 * @param store the {@link Store} to set
	 */
	@Override
	public void setStore(final Store store) {
		this.store = store;
	}

	/**
	 * Gets the persistent shopping cart object.
	 *
	 * @return the <code>ShoppingCartMemento</code> for this cart
	 */
	@Override
	public ShoppingCartMemento getShoppingCartMemento() {
		if (shoppingCartMemento == null) {
			shoppingCartMemento = getBean(ContextIdNames.SHOPPING_CART_MEMENTO);

			if (shopper != null) {
				shoppingCartMemento.setShopper(shopper);
			}

		}
		return shoppingCartMemento;
	}

	/**
	 * @param shoppingCartMemento the <code>ShoppingCartMemento</code> to set
	 */
	@Override
	public void setShoppingCartMemento(final ShoppingCartMemento shoppingCartMemento) {
		this.shoppingCartMemento = shoppingCartMemento;
	}

	/**
	 * Checks if a specific cart item SKU code was previously removed.
	 *
	 * @param skuCode the SKU code to check
	 * @return true if the cart item was removed
	 */
	@Override
	public boolean isCartItemRemoved(final String skuCode) {
		return getRemovedCartItemSkus().contains(skuCode);
	}

	/**
	 * Add a new limited usage promotion code to the list of promotion codes added to the shopping cart.
	 *
	 * @param ruleCode the promotion code to add to the list
	 * @param ruleId the id of the rule that owns the promo code.
	 */
	@Override
	public void applyLimitedUsagePromotionRuleCode(final String ruleCode, final long ruleId) {
		if (!StringUtils.isEmpty(ruleCode)) {
			promotionRecordContainer.addLimitedUsagePromotionRuleCode(ruleCode, ruleId);
		}
	}

	/**
	 * Remove a rule code from the list of promotion rule codes added to the shopping cart.
	 *
	 * @param ruleCode the code to remove from the list
	 */
	@Override
	public void removeLimitedUsagePromotionRuleCode(final String ruleCode) {
		if (!Strings.isNullOrEmpty(ruleCode)) {
			promotionRecordContainer.removeLimitedUsagePromotionRuleCode(ruleCode);
		}
	}

	/**
	 * @return the cartItemFactory
	 */
	public ShoppingItemFactory getCartItemFactory() {
		return getBean("cartItemFactory");
	}

	/**
	 * @return the priceLookupService
	 */
	public PriceLookupService getPriceLookupService() {
		return getBean("priceLookupService");
	}

	@Override
	public List<ShoppingItem> getRootShoppingItems() {
		return getCartMementoItems(getShoppingCartMemento()).stream()
				.sorted(Comparator.comparing(ShoppingItem::getOrdering))
				.collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
	}

	/**
	 * Get the leaf items which have had prices apportioned to them, if they are bundle constituents.
	 *
	 * @return a collection of leaf shopping items with apportioned prices.
	 */
	@Override
	public Collection<ShoppingItem> getApportionedLeafItems() {
		OrderSkuFactory orderSkuFactory = getBean(ContextIdNames.ORDER_SKU_FACTORY);
		Collection<OrderSku> rootItems = orderSkuFactory.createOrderSkus(this.getShoppingItems(shoppingItem -> !shoppingItem.isBundleConstituent()),
				this, getCustomerSession().getLocale());
		Collection<ShoppingItem> leafItems = new ArrayList<>();
		flattenOrderSkuTree(rootItems, leafItems);
		return leafItems;
	}

	private List<ShoppingItem> getCartMementoItems(final ShoppingList memento) {
		List<ShoppingItem> items = MoreObjects.firstNonNull(memento.getAllItems(), ImmutableList.<ShoppingItem>of());

		for (ShoppingItem item : items) {
			if (isCartItem(item)) {
				((CartItem) item).setCartUid(memento.getUidPk());
			}
		}
		return items;
	}

	/**
	 * Create a new money bean using the shopping cart currency.
	 */
	private Money createMoney(final BigDecimal amount) {
		return Money.valueOf(amount, getCustomerSession().getCurrency());
	}

	/**
	 * Get the coupon usage service.
	 *
	 * @return the coupon usage service
	 */
	protected CouponUsageService getCouponUsageService() {
		// Note that the service field must be transient and included in ShoppingCartImplIntegrationTest.testSerialization().
		if (couponUsageService == null) {
			couponUsageService = this.getBean(ContextIdNames.COUPON_USAGE_SERVICE);
		}
		return couponUsageService;
	}

	/**
	 * Get the coupon usage service.
	 *
	 * @return the coupon usage service
	 */
	protected CouponService getCouponService() {
		// Note that the service field must be transient and included in ShoppingCartImplIntegrationTest.testSerialization().
		if (couponService == null) {
			couponService = this.getBean(ContextIdNames.COUPON_SERVICE);
		}
		return couponService;
	}

	@Override
	public boolean hasLUCCForRule(final long ruleId) {
		return promotionCodes.containsKey(ruleId);
	}

	@Override
	public void setShopper(final Shopper shopper) {
		if (shopper == null) {
			throw new EpServiceException("Shopper should not be null.");
		}
		this.shopper = shopper;

		if (getTaxExemption() == null && shopper.getCustomer() != null) {
			String taxExemptionId = shopper.getCustomer().getTaxExemptionId();
			if (taxExemptionId != null) {
				setTaxExemption(TaxExemptionBuilder
						.newBuilder()
						.withTaxExemptionId(taxExemptionId)
						.build());
			}

		}
		getShoppingCartMemento().setShopper(shopper);
	}

	@Override
	public Shopper getShopper() {
		return shopper;
	}

	@Override
	public boolean isMergedNotification() {
		return mergedNotification;
	}

	@Override
	public boolean hasRecurringPricedShoppingItems() {
		return getAllShoppingItemsStream()
				.map(this::getShoppingItemPricingSnapshot)
				.anyMatch(getShoppingItemHasRecurringPricePredicate());
	}

	@Override
	public void setMergedNotification(final boolean merged) {
		mergedNotification = merged;
	}

	protected ShoppingItemHasRecurringPricePredicate getShoppingItemHasRecurringPricePredicate() {
		return shoppingItemHasRecurringPricePredicate;
	}

	@Override
	@Transient
	public boolean hasItemWithNoTierOneFromWishList() {
		return itemWithNoTierOneFromWishList;
	}

	@Override
	public void setItemWithNoTierOneFromWishList(
			final boolean itemWithNoTierOneFromWishList) {
		this.itemWithNoTierOneFromWishList = itemWithNoTierOneFromWishList;

	}

	@Override
	@Transient
	public void accept(final ShoppingCartVisitor visitor) {
		visitor.visit(this);

		getAllShoppingItems().forEach(
				shoppingItem -> visitor.visit(shoppingItem, getShoppingItemPricingSnapshot(shoppingItem)));
	}

	@Override
	public Set<ShipmentType> getShipmentTypes() {
		final ShoppingCartShipmentTypeEvaluator evaluator = getBean(ContextIdNames.SHOPPING_CART_SHIPMENT_TYPE_EVALUATOR);

		getAllShoppingItemsStream()
				.filter(shoppingItem -> !shoppingItem.isBundle(getProductSkuLookup()))
				.forEach(shoppingItem -> shoppingItem.accept(evaluator, getProductSkuLookup()));

		return evaluator.getShipmentTypes();
	}

	@Override
	public Date getLastModifiedDate() {
		return getShoppingCartMemento().getLastModifiedDate();
	}

	@Override
	public ShoppingItemPricingSnapshot getShoppingItemPricingSnapshot(final ShoppingItem item) {
		return (ShoppingItemPricingSnapshot) item;
	}

	@Override
	public ShippingPricingSnapshot getShippingPricingSnapshot(final ShippingOption shippingOption) {
		final ShippingPricing shippingPricing = getShippingPricingMap().get(shippingOption.getCode());

		if (shippingPricing == null) {
			throw new EpServiceException("Pricing not available for Shipping Option [" + shippingOption.getCode() + "]");
		}

		return new ImmutableShippingPricingSnapshot(shippingPricing, customerSession.getCurrency());
	}

	@Override
	public void deactivateCart() {
		getShoppingCartMemento().setStatus(ShoppingCartStatus.INACTIVE);
	}

	@Override
	public boolean isActive() {
		return ShoppingCartStatus.ACTIVE.equals(getShoppingCartMemento().getStatus());
	}

	@Override
	public Map<ShoppingItem, ProductSku> getShoppingItemProductSkuMap() {
		final List<String> productSkuGuids = getAllShoppingItemsStream()
				.map(ShoppingItem::getSkuGuid)
				.distinct()
				.collect(Collectors.toList());

		final Map<String, ProductSku> productSkuMap = getProductSkuLookup().findByGuids(productSkuGuids).stream()
				.collect(Collectors.toMap(ProductSku::getGuid, productSku -> productSku));

		return getAllShoppingItemsStream()
				.collect(Collectors.toMap(item -> item, item -> productSkuMap.get(item.getSkuGuid())));
	}

	/**
	 * Lazy loads the product sku lookup.
	 * @return a product sku lookup
	 */
	protected ProductSkuLookup getProductSkuLookup() {
		if (productSkuLookup == null) {
			productSkuLookup = getBean(ContextIdNames.PRODUCT_SKU_LOOKUP);
		}
		return productSkuLookup;
	}

	/**
	 * Lazy loads the discount calculator.
	 * @return a discount calculator
	 */
	protected DiscountApportioningCalculator getDiscountCalculator() {
		if (discountCalculator == null) {
			discountCalculator = getBean(ContextIdNames.DISCOUNT_APPORTIONING_CALCULATOR);
		}
		return discountCalculator;
	}

	/**
	 * Get coupon validation for potential coupon use specification.
	 *
	 * @return specification for potential coupon use.
	 */
	protected Specification<PotentialCouponUse> getValidCouponUseSpecification() {
		return this.getBean(ContextIdNames.VALID_COUPON_USE_SPEC);
	}

	/**
	 * Get the customer's business number, or null if no customer.
	 *
	 * @return the customer's business number
	 */
	public String getCustomerBusinessNumber() {
		if (getShopper().getCustomer() == null) {
			return null;
		}
		return getShopper().getCustomer().getBusinessNumber();
	}

	/**
	 * Return the subtotal amount of all shippable items.
	 *
	 * @param shoppingItems the items for which the subtotal should be calculated
	 * @return the subtotal
	 */
	protected Money getSubtotalOfShippableItems(final Collection<ShoppingItem> shoppingItems) {
		final Stream<ShoppingItem> shippableShoppingItems = shoppingItems.stream().filter(getShippableItemPredicate());
		return getShoppingItemSubtotalCalculator().calculate(shippableShoppingItems, this, getCustomerSession().getCurrency());
	}

	/**
	 * Returns the shopping item subtotal calculator in use.
	 *
	 * @return the shopping item subtotal calculator in use, never {@code null}.
	 */
	protected ShoppingItemSubtotalCalculator getShoppingItemSubtotalCalculator() {
		if (this.shoppingItemSubtotalCalculator == null) {
			this.shoppingItemSubtotalCalculator = getBean(ContextIdNames.SHOPPING_ITEM_SUBTOTAL_CALCULATOR);
		}
		return this.shoppingItemSubtotalCalculator;
	}

	/**
	 * Returns predicate matching shippable items only.
	 *
	 * @return the predicate
	 */
	protected Predicate<ShoppingItem> getShippableItemPredicate() {
		if (this.shippableItemPredicate == null) {
			this.shippableItemPredicate = getBean(ContextIdNames.SHIPPABLE_ITEM_PREDICATE);
		}
		return this.shippableItemPredicate;
	}

	/**
	 * Represents the pricing for a particular shipping option.
	 */
	protected static class ShippingPricing implements Serializable {

		private static final long serialVersionUID = 6610873674008111850L;

		private final String shippingOptionCode;

		private Money listPrice;
		private Money discountAmount;

		/**
		 * Constructor.
		 *
		 * @param shippingOptionCode the shipping option code
		 */
		public ShippingPricing(final String shippingOptionCode) {
			this.shippingOptionCode = shippingOptionCode;
		}

		public String getShippingOptionCode() {
			return shippingOptionCode;
		}

		public Money getListPrice() {
			return listPrice;
		}

		public void setListPrice(final Money listPrice) {
			this.listPrice = listPrice;
		}

		public Money getDiscountAmount() {
			return discountAmount;
		}

		public void setDiscountAmount(final Money discountAmount) {
			this.discountAmount = discountAmount;
		}
	}

	/**
	 * Immutable implementation of ShippingPricingSnapshot.
	 */
	protected static class ImmutableShippingPricingSnapshot implements ShippingPricingSnapshot {

		private static final long serialVersionUID = 2772152345243059996L;

		private final Money shippingListPrice;
		private final Money shippingPromotedPrice;
		private final Money shippingDiscountAmount;

		/**
		 * Constructor.
		 *
		 * @param shippingPricing the shipping pricing details
		 * @param currency the currency
		 */
		public ImmutableShippingPricingSnapshot(final ShippingPricing shippingPricing, final Currency currency) {
			shippingListPrice = shippingPricing.getListPrice();

			if (shippingPricing.getDiscountAmount() == null) {
				shippingDiscountAmount = Money.valueOf(BigDecimal.ZERO, currency);
			} else {
				shippingDiscountAmount = shippingPricing.getDiscountAmount();
			}

			shippingPromotedPrice = shippingListPrice.subtract(shippingDiscountAmount);
		}

		@Override
		public Money getShippingListPrice() {
			return shippingListPrice;
		}

		@Override
		public Money getShippingPromotedPrice() {
			return shippingPromotedPrice;
		}

		@Override
		public Money getShippingDiscountAmount() {
			return shippingDiscountAmount;
		}

	}

}

