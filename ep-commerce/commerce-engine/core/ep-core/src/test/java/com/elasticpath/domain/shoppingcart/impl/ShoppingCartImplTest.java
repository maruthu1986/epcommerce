/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.shoppingcart.impl;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.impl.UtilityImpl;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.GiftCertificateImpl;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.impl.CustomerAddressImpl;
import com.elasticpath.domain.customer.impl.CustomerAuthenticationImpl;
import com.elasticpath.domain.customer.impl.CustomerSessionImpl;
import com.elasticpath.domain.customer.impl.CustomerSessionMementoImpl;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.domain.misc.LocalizedPropertyValue;
import com.elasticpath.domain.misc.impl.BrandLocalizedPropertyValueImpl;
import com.elasticpath.domain.misc.impl.LocalizedPropertiesImpl;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shopper.impl.ShopperImpl;
import com.elasticpath.domain.shopper.impl.ShopperMementoImpl;
import com.elasticpath.domain.shoppingcart.DiscountRecord;
import com.elasticpath.domain.shoppingcart.ShippingPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.domain.tax.impl.TaxCategoryImpl;
import com.elasticpath.domain.tax.impl.TaxJurisdictionImpl;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.impl.InventoryDtoImpl;
import com.elasticpath.money.Money;
import com.elasticpath.money.StandardMoneyFormatter;
import com.elasticpath.plugin.payment.exceptions.PaymentProcessingException;
import com.elasticpath.service.catalog.impl.GiftCertificateServiceImpl;
import com.elasticpath.service.catalog.impl.ProductInventoryManagementServiceImpl;
import com.elasticpath.service.order.impl.AllocationServiceImpl;
import com.elasticpath.service.shoppingcart.impl.ItemPricing;
import com.elasticpath.service.tax.TaxCalculationResult;
import com.elasticpath.service.tax.adapter.TaxAddressAdapter;
import com.elasticpath.service.tax.impl.TaxCalculationResultImpl;
import com.elasticpath.settings.impl.SettingsServiceImpl;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;
import com.elasticpath.test.factory.TestCustomerSessionFactory;
import com.elasticpath.test.factory.TestShopperFactory;
import com.elasticpath.test.jmock.AbstractCatalogDataTestCase;

/**
 * Test <code>ShoppingCartImpl</code>.
 */
@SuppressWarnings({ "PMD.NonStaticInitializer", "PMD.ExcessiveClassLength", "PMD.ExcessiveImports", "PMD.TooManyMethods",
		"PMD.CouplingBetweenObjects", "PMD.AvoidDuplicateLiterals", "PMD.GodClass" })
public class ShoppingCartImplTest extends AbstractCatalogDataTestCase {

	private static final String CODE = "Code ";
	private static final int NUM_UNIQUE_RULES = 3;
	private static final Locale DEFAULT_LOCALE = Locale.CANADA;
	private static final String PRICE_2 = "10";
	private static final int INVALID_UID_2 = 987654;
	private static final int INVALID_UID_1 = -23;
	private static final int QTY_3 = 3;
	private static final int QTY_5 = 5;
	private static final long RULE_UID_1 = 1;
	private static final long RULE_UID_2 = 2;
	private static final long RULE_UID_3 = 3;

	private static final long RULE_ID = 123L;
	private static final long ACTION_ID = 456L;

	private ShoppingCartImpl shoppingCart;

	private long nextUid = 1;

	/**
	 * Prepare for tests.
	 *
	 * @throws Exception -- in case of any errors
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setUp() throws Exception {
		super.setUp();

		stubGetBean(ContextIdNames.CUSTOMER_AUTHENTICATION, CustomerAuthenticationImpl.class);
		stubGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);
		stubGetBean(ContextIdNames.SHOPPING_CART_MEMENTO, ShoppingCartMementoImpl.class);
		stubGetBean(ContextIdNames.TAX_CATEGORY, TaxCategoryImpl.class);
		stubGetBean(ContextIdNames.TAX_CALCULATION_RESULT, TaxCalculationResultImpl.class);
		stubGetBean(ContextIdNames.TAX_JURISDICTION, TaxJurisdictionImpl.class);

		stubGetBean(ContextIdNames.UTILITY, new UtilityImpl());
		stubGetBean("settingsService", new SettingsServiceImpl());

		TaxAddressAdapter adapter = new TaxAddressAdapter();
		stubGetBean(ContextIdNames.TAX_ADDRESS_ADAPTER, adapter);

		shoppingCart = new ShoppingCartImpl();

		initializeShoppingCart(shoppingCart);

		stubGetBean(ContextIdNames.GIFT_CERTIFICATE_SERVICE,
					new GiftCertificateServiceImpl() {
						@Override
						public BigDecimal getBalance(final GiftCertificate giftCertificate) {
							return giftCertificate.getPurchaseAmount();
						}
					});

		AllocationServiceImpl allocationServiceImpl = new AllocationServiceImpl();
		allocationServiceImpl.setProductInventoryManagementService(new ProductInventoryManagementServiceImpl());
		stubGetBean(ContextIdNames.ALLOCATION_SERVICE, allocationServiceImpl);
	}

	/**
	 * Initializes a shopping cart instance. Helps to initialize an instance of any shopping cart in the test case.
	 */
	private void initializeShoppingCart(final ShoppingCart shoppingCart) {
		final Shopper shopper = TestShopperFactory.getInstance().createNewShopperWithMemento();
		final CustomerSession customerSession = TestCustomerSessionFactory.getInstance().createNewCustomerSessionWithContext(shopper);
		customerSession.setCurrency(CURRENCY);
		customerSession.setLocale(DEFAULT_LOCALE);

		shoppingCart.setCustomerSession(customerSession);
		shoppingCart.setStore(getMockedStore());
	}

	@Override
	protected Product newProductImpl() {
		return new ProductImpl() {
			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getDisplayName(final Locale locale) {
				return "Test Display Name";
			}
		};
	}

	/**
	 * Get a list of cart items for testing a shopping cart.
	 *
	 * @param shoppingCart shopping cart
	 * @return a list of CartItems
	 */
	@SuppressWarnings("unchecked")
	public List<ShoppingItem> addCartItemsTo(final ShoppingCart shoppingCart) {
		List<ShoppingItem> cartItems = new ArrayList<>();
		ShoppingItem cartItem = new ShoppingItemImpl();
		cartItem.setUidPk(getUniqueUid());
		cartItem.setGuid("guid-" + cartItem.getUidPk());

		final ProductSkuImpl productSkuImpl = new ProductSkuImpl();
		productSkuImpl.initialize();
		long warehouseUid = shoppingCart.getStore().getWarehouse().getUidPk();
		InventoryDto inventoryDto = new InventoryDtoImpl();
		inventoryDto.setWarehouseUid(warehouseUid);
		productSkuImpl.setUidPk(this.getUniqueUid());
		productSkuImpl.setSkuCode(CODE + this.getUniqueUid());
		productSkuImpl.setGuid(productSkuImpl.getSkuCode() + "-guid");
		productSkuImpl.setImage("image");
		Product productImpl = newProductImpl();
		productSkuImpl.setProduct(productImpl);
		productSkuImpl.setStartDate(new Date());

		productImpl.setGuid("guid1");
		productImpl.addCategory(getCategory());

		final ProductType productType = context.mock(ProductType.class, "ProductType-" + getUniqueUid());
		context.checking(new Expectations() {
			{
				allowing(productType).getTaxCode();
				allowing(getBundleApportioningCalculator()).apportion(with(any(ItemPricing.class)), with(any(Map.class)));
				will(returnValue(Collections.emptyMap()));
				allowing(getDiscountApportioningCalculator()).calculateApportionedAmounts(with(any(BigDecimal.class)), with(any(Map.class)));
				will(returnValue(Collections.emptyMap()));
			}
		});
		mockProductSkuLookupByGuid(productSkuImpl.getGuid(), productSkuImpl);
		productImpl.setProductType(productType);

		productSkuImpl.setProduct(productImpl);
		productSkuImpl.getProduct().setAvailabilityCriteria(AvailabilityCriteria.ALWAYS_AVAILABLE);

		cartItem.setSkuGuid(productSkuImpl.getGuid());
		Price zeroPrice = new PriceImpl();
		zeroPrice.setCurrency(CURRENCY);
		zeroPrice.setListPrice(Money.valueOf(BigDecimal.ZERO, CURRENCY));
		zeroPrice.setSalePrice(Money.valueOf(BigDecimal.ZERO, CURRENCY));

		cartItem.setPrice(QTY_5, zeroPrice);

		shoppingCart.addCartItem(cartItem);
		cartItems.add(cartItem);

		Price price = new PriceImpl();
		price.setCurrency(CURRENCY);
		price.setListPrice(Money.valueOf(PRICE_2, CURRENCY));
		price.setSalePrice(Money.valueOf(PRICE_2, CURRENCY));
		cartItem = new ShoppingItemImpl();
		cartItem.setUidPk(getUniqueUid());
		cartItem.setGuid("guid-" + cartItem.getUidPk());
		final ProductSkuImpl productSkuImpl2 = new ProductSkuImpl();
		productSkuImpl2.initialize();
		productSkuImpl2.setProduct(productImpl);
		InventoryDto inventory2 = new InventoryDtoImpl();
		inventory2.setWarehouseUid(warehouseUid);
		inventory2.setSkuCode(productSkuImpl2.getSkuCode());
		productSkuImpl2.getProduct().setAvailabilityCriteria(AvailabilityCriteria.ALWAYS_AVAILABLE);
		productSkuImpl2.setUidPk(this.getUniqueUid());
		productSkuImpl2.setSkuCode(CODE + this.getUniqueUid());
		productSkuImpl2.setGuid(productSkuImpl2.getSkuCode() + "-guid");
		productSkuImpl2.setImage("image");
		productSkuImpl2.setProduct(getProduct());
		productSkuImpl2.setStartDate(new Date());

		mockProductSkuLookupByGuid(productSkuImpl2.getGuid(), productSkuImpl2);

		cartItem.setSkuGuid(productSkuImpl2.getGuid());
		cartItem.setPrice(QTY_3, price);
		shoppingCart.addCartItem(cartItem);
		cartItems.add(cartItem);

		return cartItems;
	}

	/**
	 * Test Get the cart items in the shopping cart.
	 */
	@Test
	public void testGetSetCartItems() {
		List<ShoppingItem> cartItems = addCartItemsTo(shoppingCart);

		assertEquals(cartItems, shoppingCart.getRootShoppingItems());
	}

	/**
	 * Test clearing the shopping cart.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testClearItems() {
		addCartItemsTo(shoppingCart);
		assertTrue(shoppingCart.getNumItems() > 0);

		shoppingCart.clearItems();

		assertEquals(0, shoppingCart.getNumItems());
	}

	/**
	 * Verifies that if a subtotal discount has been set previously, when the cart items are cleared, that the subtotal discount is also cleared.
	 */
	@Test
	public void testSubtotalDiscountIsClearedWhenItemsAreClearedFromCart() {
		ShoppingCartImpl shoppingCart = getShoppingCart();

		shoppingCart.setSubtotalDiscount(BigDecimal.TEN, RULE_ID, ACTION_ID);
		shoppingCart.clearItems();

		// Cart subtotal is now zero, so should the discount be.
		assertEquals(BigDecimal.ZERO, shoppingCart.getSubtotalDiscount());
	}

	/**
	 * Test Add an item to the cart.
	 */
	@Test
	public void testAddCartItem() {
		addCartItemsTo(shoppingCart);

		ShoppingItem newItem = new ShoppingItemImpl();
		final ProductSku productSku = this.getProductSku();
		productSku.setUidPk(this.getUniqueUid());
		productSku.setSkuCode(CODE + this.getUniqueUid());
		productSku.setGuid(productSku.getSkuCode() + "-guid");
		newItem.setSkuGuid(productSku.getGuid());
		mockProductSkuLookupByGuid(productSku.getGuid(), productSku);

		Price price = new PriceImpl();
		price.setCurrency(CURRENCY);
		price.setListPrice(Money.valueOf(PRICE_2, CURRENCY));
		price.setSalePrice(Money.valueOf(PRICE_2, CURRENCY));
		newItem.setPrice(1, price);
		shoppingCart.addCartItem(newItem);
		assertTrue(shoppingCart.getRootShoppingItems().contains(newItem));
	}

	/**
	 * Test Remove an item from the cart.
	 */
	@Test
	public void testRemoveCartItem() {
		List<ShoppingItem> cartItems = addCartItemsTo(shoppingCart);
		int numCartItemObjects = cartItems.size();

		// Check that invalid uids don't cause anything to be removed
		shoppingCart.removeCartItem(INVALID_UID_1);
		shoppingCart.removeCartItem(INVALID_UID_2);

		assertEquals(numCartItemObjects, shoppingCart.getRootShoppingItems().size());

		long skuUidToRemove = cartItems.get(0).getUidPk();
		shoppingCart.removeCartItem(skuUidToRemove);
		shoppingCart.removeCartItem(skuUidToRemove);

		assertEquals(numCartItemObjects - 1, shoppingCart.getRootShoppingItems().size());
	}

	/**
	 * Test Remove an item from the cart.
	 */
	@Test
	public void testRemoveIndependentCartItem() {
		List<ShoppingItem> cartItems = addCartItemsTo(shoppingCart);
		ShoppingItem primaryItem = cartItems.get(0);
		ShoppingItem dependentItem = cartItems.get(1);

		// Test non-dependent behaviour
		assertEquals(2, shoppingCart.getRootShoppingItems().size());
		shoppingCart.removeCartItem(primaryItem.getUidPk());

		assertEquals(1, shoppingCart.getRootShoppingItems().size());
		shoppingCart.removeCartItem(dependentItem.getUidPk());
		assertEquals(0, shoppingCart.getRootShoppingItems().size());
	}

	/**
	 * Test Return the number of items in the shopping cart.
	 */
	@Test
	public void testGetNumItems() {
		addCartItemsTo(shoppingCart);
		assertEquals(shoppingCart.getNumItems(), QTY_3 + QTY_5);
		shoppingCart.clearItems();
		assertEquals(shoppingCart.getNumItems(), 0);
	}

	/**
	 * Get the subtotal of all items in the cart.
	 */
	@Test
	public void testGetSubtotal() {
		mockSubtotalCalculatorForAllCartItems(shoppingCart, BigDecimal.TEN);

		assertNotNull(shoppingCart.getSubtotalMoney());
		assertTrue(BigDecimal.ZERO.compareTo(shoppingCart.getSubtotalMoney().getAmount()) < 0);
	}

	/**
	 * Tests that getTotal() retrieves the total properly.
	 */
	@Test
	public void testGetTotal() {
		mockSubtotalCalculatorForAllCartItems(shoppingCart, BigDecimal.TEN);

		assertNotNull(shoppingCart.getTotalMoney());
		assertTrue("Total should not be zero", BigDecimal.ZERO.compareTo(shoppingCart.getTotalMoney().getAmount()) < 0);
	}

	/**
	 * Tests that getTotal() retrieves the total properly.
	 */
	@Test
	public void testGetTotalBiggerGCAmount() {
		final ShoppingCartImpl shoppingCart = getShoppingCart();

		final StoreImpl store = new StoreImpl();
		shoppingCart.setStore(store);

		final BigDecimal giftCertificateAmount = shoppingCart.getTotal().add(BigDecimal.TEN);
		final GiftCertificate giftCertificate = mockGiftCertificate(store, giftCertificateAmount, shoppingCart.getCustomerSession().getCurrency());
		shoppingCart.applyGiftCertificate(giftCertificate);

		assertNotNull(shoppingCart.getTotalMoney());
		assertEquals("Total should be zero if we apply a GC with a balance greater than the total previously",
					 0, BigDecimal.ZERO.compareTo(shoppingCart.getTotalMoney().getAmount()));
	}

	private long getUniqueUid() {
		return nextUid++;
	}

	/**
	 * Test getShippingCost() method.
	 */
	@Test
	public void testGetShippingCost() {
		assertNotNull(this.shoppingCart.getShippingCost());
		assertEquals("Shipping cost should compare to zero.",
					 0, BigDecimal.ZERO.compareTo(this.shoppingCart.getShippingCost().getAmount()));
	}

	/**
	 * Test that if any item in the shopping cart is shippable then the shopping cart requires shipping.
	 */
	@Test
	public void testRequiresShipping() {
		ShoppingCart cart = shippableItemsCart(true);
		assertTrue(cart.requiresShipping());

		cart = shippableItemsCart(true, false);
		assertTrue(cart.requiresShipping());

		cart = shippableItemsCart(true, true, false);
		assertTrue(cart.requiresShipping());

		cart = shippableItemsCart(true, false, true);
		assertTrue(cart.requiresShipping());

		cart = shippableItemsCart(false, false, true);
		assertTrue(cart.requiresShipping());

		cart = shippableItemsCart(false, false, false);
		assertFalse(cart.requiresShipping());

		cart = shippableItemsCart();
		assertFalse(cart.requiresShipping());

	}

	private ShoppingCart shippableItemsCart(final boolean ... items) {
		final List<ShoppingItem> cartItems = new ArrayList<>();

		for (final boolean shippable : items) {
			final ShoppingItem item = context.mock(ShoppingItem.class, "ShoppingItem-" + getUniqueUid());
			context.checking(new Expectations() {
				{
					allowing(item).isShippable(getProductSkuLookup());
					will(returnValue(shippable));

					allowing(item).getChildren();
					will(returnValue(new ArrayList<>()));
				}
			});
			cartItems.add(item);
		}
		ShoppingCartImpl cart = new ShoppingCartImpl() {
			private static final long serialVersionUID = -8497118209873625320L;

			@Override
			public List<ShoppingItem> getRootShoppingItems() {
				return cartItems;
			}
		};
		return cart;
	}

// FIXME: Reimplement tests once the deprecated addAssociatedCartItem method is fixed (but remains deprecated)
//	/** Test for addAssociatied cart item -- not source product dependent. */
//	public void testAddAssociatedCartItem() {
//		setupTaxCalculationServiceReturnNull();
//
//		ShoppingCart shoppingCart = getShoppingCart();
//		ProductSku targetSku = this.getProductSku();
//		targetSku.setUidPk(1);
//		targetSku.setGuid("1");
//		Product targetProduct = this.getProduct();
//		targetProduct.addOrUpdateSku(targetSku);
//		targetProduct.setDefaultSku(targetSku);
//		targetSku.setProduct(targetProduct);
//
//		ProductAssociation productAssociation = new ProductAssociationImpl();
//		productAssociation.setTargetProduct(targetProduct);
//		productAssociation.setDefaultQuantity(QTY_3);
//		productAssociation.setSourceProductDependent(false);
//
//		ProductSku parentSku = this.getProductSku();
//		parentSku.setUidPk(2);
//		parentSku.setGuid("2");
//
//		CartItem parentCartItem = shoppingCart.addCartItem(parentSku, 1);
//		CartItem addedCartItem = shoppingCart.addAssociatedCartItem(productAssociation, parentCartItem);
//
//		assertEquals(addedCartItem.getProductSku(), targetSku);
//		assertEquals(QTY_3, addedCartItem.getQuantity());
//		assertTrue(parentCartItem.getDependentCartItems() == null || parentCartItem.getDependentCartItems().size() == 0);
//		assertNull(addedCartItem.getParentCartItem());
//
//		// Check that duplicate cart items are not created
//		int numCartItems = shoppingCart.getCartItems().size();
//		CartItem existingCartItem = shoppingCart.addAssociatedCartItem(productAssociation, parentCartItem);
//		assertEquals(numCartItems, shoppingCart.getCartItems().size());
//		assertEquals(QTY_3 * 2, existingCartItem.getQuantity());
//
//	}
//
//	/** Test for addCartItem -- test dependent cart item case. */
//	public void testAddAssociatedCartItem2() {
//		setupTaxCalculationServiceReturnNull();
//
//		ShoppingCart shoppingCart = getShoppingCart();
//		ProductSku targetSku = this.getProductSku();
//		Product targetProduct = this.getProduct();
//		targetProduct.addOrUpdateSku(targetSku);
//		targetProduct.setDefaultSku(targetSku);
//		targetSku.setProduct(targetProduct);
//
//		ProductAssociation productAssociation = new ProductAssociationImpl();
//		productAssociation.setTargetProduct(targetProduct);
//		productAssociation.setDefaultQuantity(QTY_3);
//		productAssociation.setSourceProductDependent(true);
//
//		ProductSku parentSku = this.getProductSku();
//		parentSku.setUidPk(1);
//		parentSku.setGuid("1");
//
//		CartItem parentCartItem = shoppingCart.addCartItem(parentSku, QTY_5);
//		CartItem addedCartItem = shoppingCart.addAssociatedCartItem(productAssociation, parentCartItem);
//
//
//		assertEquals(addedCartItem.getProductSku(), targetSku);
//		assertEquals(QTY_5, addedCartItem.getQuantity());
//		assertEquals(1, parentCartItem.getDependentCartItems().size());
//		assertEquals(parentCartItem, addedCartItem.getParentCartItem());
//
//		// Dependant cart items can have duplicates, IF parents are different.
//		// Quantity will be updated for dependent cart item to the new quantity of the parent cart item.
//		int numCartItems = shoppingCart.getCartItems().size();
//		CartItem existingCartItem = shoppingCart.addAssociatedCartItem(productAssociation, parentCartItem);
//		assertEquals(numCartItems, shoppingCart.getCartItems().size());
//		assertEquals(QTY_5, existingCartItem.getQuantity());
//	}

	/**
	 * Test getTotalWeight method.
	 */
	@Test
	public void testGetTotalWeight() {
		final BigDecimal weight1 = new BigDecimal("7.0");
		final int quantity1 = 3;

		final ProductSku shippableProductSku = new ProductSkuImpl();
		shippableProductSku.initialize();
		shippableProductSku.setShippable(true);
		shippableProductSku.setWeight(weight1);

		final ProductSku nonShippableProductSku = new ProductSkuImpl();
		nonShippableProductSku.initialize();
		nonShippableProductSku.setShippable(false);

		final ShoppingItem shippableItem = context.mock(ShoppingItem.class, "ShoppingItem (shippable)");
		final ShoppingItem nonShippableItem = context.mock(ShoppingItem.class, "ShoppingItem (non-shippable)");

		context.checking(new Expectations() {
			{
				allowing(shippableItem).getSkuGuid();
				will(returnValue(shippableProductSku.getGuid()));
				allowing(shippableItem).getQuantity();
				will(returnValue(quantity1));
				allowing(shippableItem).getChildren();
				will(returnValue(new ArrayList<>()));
				allowing(nonShippableItem).getChildren();
				will(returnValue(new ArrayList<>()));

				allowing(nonShippableItem).getSkuGuid();
				will(returnValue(nonShippableProductSku.getGuid()));
			}
		});

		mockProductSkuLookupByGuid(shippableProductSku.getGuid(), shippableProductSku);
		mockProductSkuLookupByGuid(nonShippableProductSku.getGuid(), nonShippableProductSku);

		final List<ShoppingItem> cartItems = new ArrayList<>();
		cartItems.add(shippableItem);
		cartItems.add(nonShippableItem);

		ShoppingCartImpl cart = new ShoppingCartImpl() {
			private static final long serialVersionUID = -9139751788820519457L;

			@Override
			public List<ShoppingItem> getRootShoppingItems() {
				return cartItems;
			}
		};

		assertEquals(new BigDecimal("21.0"), cart.getTotalWeight());
	}


	/**
	 * Test case for the code tracking the ids of the rules applied.
	 */
	@Test
	public void testAppliedRules() {
		ShoppingCartImpl shoppingCart = getShoppingCart();
		assertNotNull(shoppingCart.getPromotionRecordContainer().getAppliedRules());
		shoppingCart.ruleApplied(RULE_UID_1, 0, null, null, 0);
		shoppingCart.ruleApplied(RULE_UID_2, 0, null, null, 0);
		shoppingCart.ruleApplied(RULE_UID_2, 0, null, null, 0);
		shoppingCart.ruleApplied(RULE_UID_2, 0, null, null, 0);
		shoppingCart.ruleApplied(RULE_UID_3, 0, null, null, 0);
		assertEquals(NUM_UNIQUE_RULES, shoppingCart.getPromotionRecordContainer().getAppliedRules().size());
		Long appliedRuleUid = shoppingCart.getPromotionRecordContainer().getAppliedRules().iterator().next();
		assertNotNull(appliedRuleUid);
	}

	/**
	 * Verifies calling setSubtotalDiscount() with a lower discount than a previously set subtotal discount is not applied.
	 */
	@Test
	public void testSetSubtotalDiscountWithLowerDiscountThanExistingIsSuperceded() {
		mockSubtotalCalculatorForAllCartItems(shoppingCart, BigDecimal.TEN);

		BigDecimal highDiscount = new BigDecimal("6");
		BigDecimal lowDiscount = new BigDecimal("5");

		shoppingCart.setSubtotalDiscount(highDiscount, RULE_ID, ACTION_ID);
		shoppingCart.setSubtotalDiscount(lowDiscount, RULE_ID, ACTION_ID);

		assertEquals(highDiscount, shoppingCart.getSubtotalDiscount());
	}

	/**
	 * Verifies calling setSubtotalDiscount() with a higher discount than a previously set subtotal discount is applied.
	 */
	@Test
	public void testSetSubtotalDiscountWithHigherDiscountThanExistingIsApplied() {
		mockSubtotalCalculatorForAllCartItems(shoppingCart, BigDecimal.TEN);

		BigDecimal lowDiscount = new BigDecimal("5");
		BigDecimal highDiscount = new BigDecimal("6");

		shoppingCart.setSubtotalDiscount(lowDiscount, RULE_ID, ACTION_ID);
		shoppingCart.setSubtotalDiscount(highDiscount, RULE_ID, ACTION_ID);

		assertEquals(highDiscount, shoppingCart.getSubtotalDiscount());
	}

	/**
	 * Verifies calling setSubtotalDiscount() with a discount when the subtotal is zero does not apply the discount.
	 */
	@Test
	public void testSetSubtotalDiscountWithAZeroSubtotalDoesNotApply() {
		mockSubtotalCalculatorForAllCartItems(shoppingCart, BigDecimal.ZERO);

		final BigDecimal discount = BigDecimal.TEN;
		shoppingCart.setSubtotalDiscount(discount, RULE_ID, ACTION_ID);

		// Cart subtotal is zero, so can't set the subtotal discount above.
		assertEquals(BigDecimal.ZERO, shoppingCart.getSubtotalDiscount());
	}

	/**
	 * Verifies calling setSubtotalDiscount() with a discount higher than the subtotal is limited to the subtotal amount and doesn't exceed it.
	 */
	@Test
	public void testSetSubtotalDiscountToGreaterThanSubtotalIsLimitedToSubtotalAmount() {
		mockSubtotalCalculatorForAllCartItems(shoppingCart, BigDecimal.TEN);

		BigDecimal subtotal = shoppingCart.getSubtotal();
		BigDecimal hugeDiscount = subtotal.add(new BigDecimal("1000.00"));

		shoppingCart.setSubtotalDiscount(hugeDiscount, RULE_ID, ACTION_ID);

		assertEquals(subtotal, shoppingCart.getSubtotalDiscount());
	}

	/**
	 * Test method for apply gift certificate redeem.
	 */
	@Test
	public void testApplyGiftCertificateRedeem() {
		final ShoppingCart shoppingCart = getShoppingCart();
		final CustomerSession customerSession = shoppingCart.getCustomerSession();

		GiftCertificate giftCertificate = mockGiftCertificate(shoppingCart.getStore(),
															  BigDecimal.TEN,
															  customerSession.getCurrency());
		shoppingCart.applyGiftCertificate(giftCertificate);
		assertTrue(shoppingCart.getAppliedGiftCertificates().contains(giftCertificate));
	}

	/**
	 * Test method for apply gift certificate redeem.
	 */
	@Test
	public void testApplyGiftCertificateRedeemTwice() {
		final ShoppingCart shoppingCart = getShoppingCart();
		final CustomerSession customerSession = shoppingCart.getCustomerSession();

		GiftCertificate giftCertificate = mockGiftCertificate(shoppingCart.getStore(), BigDecimal.TEN, customerSession.getCurrency());
		shoppingCart.applyGiftCertificate(giftCertificate);
		assertTrue(shoppingCart.getAppliedGiftCertificates().contains(giftCertificate));
		assertEquals(shoppingCart.getAppliedGiftCertificates().size(), 1);

		shoppingCart.applyGiftCertificate(giftCertificate);
		assertTrue(shoppingCart.getAppliedGiftCertificates().contains(giftCertificate));
		assertEquals(shoppingCart.getAppliedGiftCertificates().size(), 1);
	}

	/**
	 * Test applying two gift certificates with the same amount.
	 */
	@Test
	public void testApplyTwoGiftCertificatesWithSameAmount() {
		final ShoppingCart shoppingCart = getShoppingCart();
		final CustomerSession customerSession = shoppingCart.getCustomerSession();

		final GiftCertificate giftCertificate1 = mockGiftCertificate(shoppingCart.getStore(), BigDecimal.TEN, customerSession.getCurrency());
		final GiftCertificate giftCertificate2 = mockGiftCertificate(shoppingCart.getStore(), BigDecimal.TEN, customerSession.getCurrency());

		shoppingCart.applyGiftCertificate(giftCertificate1);
		assertTrue(shoppingCart.getAppliedGiftCertificates().contains(giftCertificate1));
		assertEquals(1, shoppingCart.getAppliedGiftCertificates().size());

		shoppingCart.applyGiftCertificate(giftCertificate2);
		assertTrue(shoppingCart.getAppliedGiftCertificates().contains(giftCertificate2));
		assertTrue(shoppingCart.getAppliedGiftCertificates().contains(giftCertificate1));
		assertEquals("Two gift certificates should have been applied", 2, shoppingCart.getAppliedGiftCertificates().size());

	}

	/**
	 * Test method for apply gift certificate redeem.
	 */
	@Test
	public void testApplyGiftCertificateRedeemWithZeroBalance() {
		final BigDecimal balance = BigDecimal.ZERO;

		stubGetBean(ContextIdNames.GIFT_CERTIFICATE_SERVICE,
					new GiftCertificateServiceImpl() {
						@Override
						public BigDecimal getBalance(final GiftCertificate giftCertificate) {
							return balance;
						}
					});

		final ShoppingCart shoppingCart = getShoppingCart();
		final CustomerSession customerSession = shoppingCart.getCustomerSession();

		GiftCertificate giftCertificate = mockGiftCertificate(shoppingCart.getStore(), balance, customerSession.getCurrency());
		try {
			shoppingCart.applyGiftCertificate(giftCertificate);
			fail("Expected domain exception due to zero balance");
		} catch (PaymentProcessingException ppe) {
			// Success
			assertNotNull(ppe);
		}

	}

	/**
	 * Test method for apply gift certificate redeem.
	 */
	@Test
	public void testApplyGiftCertificateRedeemWrongCurrency() {
		final ShoppingCart shoppingCart = getShoppingCart();
		final CustomerSession customerSession = shoppingCart.getCustomerSession();

		Currency currency = Currency.getInstance(Locale.ITALY);
		assertThat("Shopping cart currency must not be the gift certificate currency", customerSession.getCurrency(),
				   Matchers.not(equalTo(currency)));
		GiftCertificate giftCertificate = mockGiftCertificate(shoppingCart.getStore(), BigDecimal.TEN, currency);

		try {
			shoppingCart.applyGiftCertificate(giftCertificate);
			fail("Expected domain exception due to mismatch currency code");
		} catch (PaymentProcessingException ppe) {
			// Success
			assertNotNull(ppe);
		}

	}

	/**
	 * Test method for apply gift certificate redeem.
	 */
	@Test
	public void testGetGiftCertificateDiscountWhenTotalIsLess() {
		ShoppingCartImpl shoppingCart = newShoppingCartForGiftCertificateTests(BigDecimal.ONE);

		shoppingCart.setStore(new StoreImpl());
		GiftCertificate giftCertificate = mockGiftCertificate(shoppingCart.getStore(), BigDecimal.TEN,
															  shoppingCart.getCustomerSession().getCurrency());
		shoppingCart.applyGiftCertificate(giftCertificate);

		assertEquals("The redeemed GC amount should be not more than the shopping cart total",
					 BigDecimal.ONE, shoppingCart.getGiftCertificateDiscount());
	}

	/**
	 * Test method for apply gift certificate redeem.
	 */
	@Test
	public void testGetGiftCertificateDiscountWhenTotalIsMore() {
		ShoppingCartImpl shoppingCart = newShoppingCartForGiftCertificateTests(new BigDecimal("15"));

		shoppingCart.setStore(new StoreImpl());

		GiftCertificate giftCertificate = mockGiftCertificate(shoppingCart.getStore(), BigDecimal.TEN,
															  shoppingCart.getCustomerSession().getCurrency());
		shoppingCart.applyGiftCertificate(giftCertificate);

		assertEquals("The redeemed GC amount should exactly its amount when total > GC amount",
					 BigDecimal.TEN, shoppingCart.getGiftCertificateDiscount());
	}


	private ShoppingCartImpl newShoppingCartForGiftCertificateTests(final BigDecimal totalBeforeRedeem) {
		final ShopperImpl shopper = new ShopperImpl();
		shopper.setShopperMemento(new ShopperMementoImpl());
		shopper.setUidPk(0L);

		final CustomerSession customerSession = new CustomerSessionImpl();
		customerSession.setCustomerSessionMemento(new CustomerSessionMementoImpl());
		customerSession.setShopper(shopper);
		customerSession.setCurrency(Currency.getInstance(Locale.CANADA));

		final ShoppingCartImpl shoppingCart = new ShoppingCartImpl() {
			private static final long serialVersionUID = 1955987907161856611L;

			@Override
			public BigDecimal getTotalBeforeRedeem() {
				return totalBeforeRedeem;
			}
		};
		shoppingCart.setCustomerSession(customerSession);
		return shoppingCart;
	}

	/**
	 * Test method for apply gift certificate redeem.
	 */
	@Test
	public void testApplyGiftCertificateRedeems() {
		final long uidPkGc1 = 1L;
		final long uidPkGc2 = 2L;
		final ShoppingCart shoppingCart = getShoppingCart();
		final CustomerSession customerSession = shoppingCart.getCustomerSession();

		final GiftCertificate giftCertificate = new GiftCertificateImpl();
		giftCertificate.setUidPk(uidPkGc1);
		giftCertificate.setPurchaseAmount(BigDecimal.TEN);
		giftCertificate.setCurrencyCode(customerSession.getCurrency().getCurrencyCode());
		giftCertificate.setStore(shoppingCart.getStore());

		shoppingCart.applyGiftCertificate(giftCertificate);

		final GiftCertificate giftCertificate2 = new GiftCertificateImpl();
		giftCertificate2.setUidPk(uidPkGc2);
		giftCertificate2.setPurchaseAmount(BigDecimal.ONE);
		giftCertificate2.setCurrencyCode(customerSession.getCurrency().getCurrencyCode());
		giftCertificate2.setStore(shoppingCart.getStore());

		stubGetBean(ContextIdNames.GIFT_CERTIFICATE_SERVICE,
					new GiftCertificateServiceImpl() {
						@Override
						public BigDecimal getBalance(final GiftCertificate giftCert) {
							if (giftCert.getUidPk() == uidPkGc1) {
								return giftCertificate.getPurchaseAmount();
							} else if (giftCert.getUidPk() == uidPkGc2) {
								return giftCertificate2.getPurchaseAmount();
							}
							return null;
						}
					});

		shoppingCart.applyGiftCertificate(giftCertificate2);

		assertEquals(shoppingCart.getAppliedGiftCertificates().size(), 2);
		assertThat(shoppingCart.getAppliedGiftCertificates(), containsInAnyOrder(giftCertificate, giftCertificate2));

	}

	/**
	 * Returns a newly created address.
	 *
	 * @return a newly created address
	 */
	protected Address getBillingAddress() {
		Address address = new CustomerAddressImpl();
		address.setFirstName("Joe");
		address.setLastName("Doe");
		address.setCountry("CA");
		address.setStreet1("1295 Charleston Road");
		address.setCity("Vancouver");
		address.setSubCountry("CA");
		address.setZipOrPostalCode("V5T 4H3");
		return address;
	}

	/**
	 * Tests isCartItemRemoved().
	 */
	@Test
	public void testIsCartItemRemoved() {
		ShoppingCartImpl shoppingCart = getShoppingCart();

		ShoppingItem cartItemToRemove = shoppingCart.getRootShoppingItems().get(0);
		ProductSku cartSkuToRemove = getCartSkus().get(0);

		assertEquals("Sanity Check", cartSkuToRemove.getGuid(), cartItemToRemove.getSkuGuid());

		assertFalse(shoppingCart.isCartItemRemoved(cartSkuToRemove.getSkuCode()));

		shoppingCart.removeCartItem(cartItemToRemove.getUidPk());

		assertTrue(shoppingCart.isCartItemRemoved(cartSkuToRemove.getSkuCode()));
	}

	/**
	 * Tests that a hasSubtotalDiscount() works as expected.
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void testHasSubtotalDiscountWithZeroValue() {
		mockSubtotalCalculatorForAllCartItems(shoppingCart, BigDecimal.ZERO);

		shoppingCart.setSubtotalDiscount(BigDecimal.ZERO.setScale(2), RULE_ID, ACTION_ID);
		assertFalse("The discount is 0 and therefore no discount exists", shoppingCart.hasSubtotalDiscount());
	}

	/**
	 * Tests that a hasSubtotalDiscount() cannot be set to null.
	 */
	@Test
	public void testSetSubtotalDiscountNullValue() {
		mockSubtotalCalculatorForAllCartItems(shoppingCart, BigDecimal.ZERO);

		try {
			shoppingCart.setSubtotalDiscount(null, RULE_ID, ACTION_ID);
			fail("should not be possible to set discount to null");
		} catch (EpServiceException exc) {
			assertNotNull(exc);
		}
		assertFalse("The discount was not and therefore no discount exists", shoppingCart.hasSubtotalDiscount());
		assertNotNull(shoppingCart.getSubtotalDiscount());
	}

	/**
	 * Tests that subtotal discount can be set to some value.
	 */
	@Test
	public void testSubtotalDiscountNonZeroValue() {
		mockSubtotalCalculatorForAllCartItems(shoppingCart, BigDecimal.TEN);

		assertTrue(BigDecimal.ZERO.compareTo(shoppingCart.getSubtotal()) < 0);

		shoppingCart.setSubtotalDiscount(BigDecimal.ONE, RULE_ID, ACTION_ID);
		assertTrue("The discount is 1 and therefore discount exists", shoppingCart.hasSubtotalDiscount());
	}

	/**
	 *
	 */
	@SuppressWarnings("unchecked")
	private TaxCalculationResult applyTaxCalculationResult(final ShoppingCartImpl shoppingCart) {
		final TaxCalculationResultImpl taxCalculationResult = new TaxCalculationResultImpl();
		final Currency currency = shoppingCart.getCustomerSession().getCurrency();
		taxCalculationResult.setDefaultCurrency(currency);
		taxCalculationResult.setBeforeTaxSubTotalWithoutDiscount(Money.valueOf(BigDecimal.TEN, currency));
		taxCalculationResult.setBeforeTaxSubTotal(Money.valueOf("9", currency));
		shoppingCart.setTaxCalculationResult(taxCalculationResult);

		return taxCalculationResult;
	}

	/**
	 * Tests the getAllItems() method for not being modifiable.
	 */
	@Test
	public void testGetAllItems() {
		assertTrue(shoppingCart.getAllShoppingItems().isEmpty());

		try {
			shoppingCart.getAllShoppingItems().clear();
			fail("The items collection should be unmodifiable");
		} catch (Exception exc) {
			assertNotNull(exc);
		}
	}

	/**
	 * Tests getLocalizedTaxMap() and that it returns sorted map of tax category name to tax value.
	 */
	@Test
	public void testLocalizedTaxMap() {
		shoppingCart.getCustomerSession().setLocale(Locale.UK);
		TaxCalculationResult result = applyTaxCalculationResult(shoppingCart);
		Money amount = Money.valueOf(BigDecimal.TEN, Currency.getInstance(Locale.UK));
		TaxCategory taxCategory = newTaxCategory("c123");
		result.addTaxValue(taxCategory, amount);
		TaxCategory taxCategory2 = newTaxCategory("b123");
		result.addTaxValue(taxCategory2, amount);

		TaxCategory taxCategory3 = newTaxCategory("a123");
		result.addTaxValue(taxCategory3, amount);

		Map<String, Money> localizedMap = shoppingCart.getLocalizedTaxMap();

		final Iterator<Entry<String, Money>> taxIterator = localizedMap.entrySet().iterator();

		assertEquals("a123", taxIterator.next().getKey());
		assertEquals("b123", taxIterator.next().getKey());
		assertEquals("c123", taxIterator.next().getKey());
	}

	/**
	 */
	private TaxCategory newTaxCategory(final String displayName) {
		TaxCategory taxCategory = new TaxCategoryImpl();
		LocalizedProperties localizedProperties = new LocalizedPropertiesImpl() {
			private static final long serialVersionUID = -1892978990192008917L;

			@Override
			protected LocalizedPropertyValue getNewLocalizedPropertyValue() {
				return new BrandLocalizedPropertyValueImpl(); // arbitrary implementation
			}
		};
		localizedProperties.setValue(TaxCategory.LOCALIZED_PROPERTY_DISPLAY_NAME, Locale.UK, displayName);

		taxCategory.setLocalizedProperties(localizedProperties);
		return taxCategory;
	}

// FIXME: When promotions are once again considered in the context of ShoppingItem, these tests must ensure that
	// cart promotions are cleared and refired when items in the cart change. We probably should also fix it so
	//that firing cart promotion rules does not also fire catalog promotion rules
//	/**
//	 * Tests that when a SKU is added to the cart, computed prices on all SKUs currently in the cart are cleared.
//	 */
//	public void testClearComputedPriceOnAllSKUsInTheCart() {
//		ShoppingCart cart = getEmptyShoppingCart();
//
//		ProductSku sku1 = getProductSku();
//		sku1.addCatalogPrice(getCatalog(), getPrice(Currency.getInstance(Locale.CANADA), new BigDecimal("80"), null));
//		cart.addCartItem(sku1, 1);
//
//		// set computed price
//		final Money computedPrice = Mnoey.createMoney(BigDecimal.TEN, Currency.getInstance(Locale.CANADA));
//
//		Price price = sku1.getCatalogSkuPrice(getCatalog(), Currency.getInstance(Locale.CANADA));
//		price.setComputedPriceIfLower(computedPrice);
//
//		ProductSku sku2 = getProductSku();
//		sku2.addCatalogPrice(getCatalog(), getPrice(Currency.getInstance(Locale.CANADA), new BigDecimal("90"), null));
//
//		// test computed price is 10
//		assertEquals(computedPrice.getAmount(), price.getComputedPrice().getAmount());
//		cart.addCartItem(sku2, 1);
//
//		// test after adding new product to shopping cart, the computed price has been cleared.
//		assertNull(price.getComputedPrice());
//	}
//
//	/**
//	 * Tests that when a multiSKU product is added to the cart, computed prices on all SKUs currently in the cart are cleared.
//	 */
//	public void testClearComputedPriceOnAllMultiSKUProductsInTheCart() {
//		ShoppingCart cart = getEmptyShoppingCart();
//
//		ProductSku sku1 = getProductSku();
//		sku1.addCatalogPrice(getCatalog(), getPrice(Currency.getInstance(Locale.CANADA), new BigDecimal("80"), null));
//		cart.addCartItem(sku1, 1);
//
//		// set computed price
//		final Money computedPrice = Money.valueOf(BigDecimal.TEN, Currency.getInstance(Locale.CANADA));
//
//		Price price = sku1.getCatalogSkuPrice(getCatalog(), Currency.getInstance(Locale.CANADA));
//		price.setComputedPriceIfLower(computedPrice);
//
//		ProductSku sku2 = getProductSku();
//		sku2.addCatalogPrice(getCatalog(), getPrice(Currency.getInstance(Locale.CANADA), new BigDecimal("90"), null));
//		sku2.setProduct(sku1.getProduct());
//
//		ProductSku sku3 = getProductSku();
//		sku3.addCatalogPrice(getCatalog(), getPrice(Currency.getInstance(Locale.CANADA), new BigDecimal("100"), null));
//
//		// test computed price is 10
//		assertEquals(computedPrice.getAmount(), price.getComputedPrice().getAmount());
//		cart.addCartItem(sku3, 1);
//
//		// test after adding new product to shopping cart, the computed price has been cleared.
//		assertNull(price.getComputedPrice());
//	}

	private GiftCertificate mockGiftCertificate(final Store store, final BigDecimal amount, final Currency currency) {
		final GiftCertificate certificate = context.mock(GiftCertificate.class, "GiftCertificate-" + getUniqueUid());
		context.checking(new Expectations() {
			{
				allowing(certificate).getStore();
				will(returnValue(store));
				allowing(certificate).getCurrencyCode();
				will(returnValue(currency.getCurrencyCode()));
				allowing(certificate).getPurchaseAmount();
				will(returnValue(amount));
			}
		});
		return certificate;
	}

	@Test
	public void testGetCartItemsWithNullItemsFromMemento() {
		shoppingCart.getShoppingCartMemento().setAllItems(null);
		Collection<ShoppingItem> allItems = shoppingCart.getAllShoppingItems();
		assertTrue("The returned list should be empty", allItems.isEmpty());
	}

	@Test
	public void testGetShoppingItemPricingSnapshot() {
		List<ShoppingItem> items = addCartItemsTo(shoppingCart);

		for (ShoppingItem item : items) {
			ShoppingItemPricingSnapshot itemSnapshot = shoppingCart.getShoppingItemPricingSnapshot(item);
			assertSame("For now, the item and snapshot should be one and the same", item, itemSnapshot);
		}
	}

	@Test(expected = EpServiceException.class)
	public void verifyGettingPricingSnapshotForUnknownShippingOptionThrows() throws Exception {
		shoppingCart.getShippingPricingSnapshot(createShippingOption("1"));
	}

	@Test
	public void verifyCanGetShippingSnapshotsForCorrespondingServiceLevels() throws Exception {
		final long ruleId = 1;
		final long actionId = 2;

		final String shippingOptionCode1 = "SHIP001";
		final String shippingOptionCode2 = "SHIP002";
		final String shippingOptionCode3 = "SHIP003";

		final ShippingOption shippingOption1 = createShippingOption(shippingOptionCode1);
		final ShippingOption shippingOption2 = createShippingOption(shippingOptionCode2);
		final ShippingOption shippingOption3 = createShippingOption(shippingOptionCode3);

		final Money zeroDollars = Money.valueOf(BigDecimal.ZERO, CURRENCY);
		final Money oneDollar = Money.valueOf(BigDecimal.ONE, CURRENCY);
		final Money nineDollars = Money.valueOf(new BigDecimal("9"), CURRENCY);
		final Money tenDollars = Money.valueOf(BigDecimal.TEN, CURRENCY);
		final Money ninetyDollars = Money.valueOf(new BigDecimal("90"), CURRENCY);
		final Money hundredDollars = Money.valueOf(new BigDecimal("100"), CURRENCY);

		// $0 list, null discount
		shoppingCart.setShippingListPrice(shippingOptionCode1, zeroDollars);

		// $10 list, $1 discount
		shoppingCart.setShippingListPrice(shippingOptionCode2, tenDollars);
		shoppingCart.setShippingDiscountIfLower(shippingOptionCode2, ruleId, actionId, oneDollar);

		// $100 list, $10 discount
		shoppingCart.setShippingListPrice(shippingOptionCode3, hundredDollars);
		shoppingCart.setShippingDiscountIfLower(shippingOptionCode3, ruleId, actionId, tenDollars);


		final ShippingPricingSnapshot shippingPricingSnapshot1 = shoppingCart.getShippingPricingSnapshot(shippingOption1);
		final ShippingPricingSnapshot shippingPricingSnapshot2 = shoppingCart.getShippingPricingSnapshot(shippingOption2);
		final ShippingPricingSnapshot shippingPricingSnapshot3 = shoppingCart.getShippingPricingSnapshot(shippingOption3);

		assertEquals(zeroDollars, shippingPricingSnapshot1.getShippingListPrice());
		assertEquals(zeroDollars, shippingPricingSnapshot1.getShippingPromotedPrice());
		assertEquals(zeroDollars, shippingPricingSnapshot1.getShippingDiscountAmount());

		assertEquals(tenDollars, shippingPricingSnapshot2.getShippingListPrice());
		assertEquals(nineDollars, shippingPricingSnapshot2.getShippingPromotedPrice());
		assertEquals(oneDollar, shippingPricingSnapshot2.getShippingDiscountAmount());

		assertEquals(hundredDollars, shippingPricingSnapshot3.getShippingListPrice());
		assertEquals(ninetyDollars, shippingPricingSnapshot3.getShippingPromotedPrice());
		assertEquals(tenDollars, shippingPricingSnapshot3.getShippingDiscountAmount());
	}

	@Test
	public void verifyShipmentDiscountOnlySetIfBetterThanExistingDiscount() throws Exception {
		final long ruleId = 1;
		final long actionId = 2;

		final Money zeroDollars = Money.valueOf(BigDecimal.ZERO, CURRENCY);
		final Money oneDollar = Money.valueOf(BigDecimal.ONE, CURRENCY);
		final Money tenDollars = Money.valueOf(BigDecimal.TEN, CURRENCY);

		final String shippingOptionCode = "SHIP001";

		final ShippingOption shippingOption = createShippingOption(shippingOptionCode);
		shoppingCart.setShippingListPrice(shippingOptionCode, zeroDollars);

		shoppingCart.setShippingDiscountIfLower(shippingOptionCode, ruleId, actionId, zeroDollars);
		assertEquals(zeroDollars, shoppingCart.getShippingPricingSnapshot(shippingOption).getShippingDiscountAmount());

		// Better discount, new value should be $10
		shoppingCart.setShippingDiscountIfLower(shippingOptionCode, ruleId, actionId, tenDollars);
		assertEquals(tenDollars, shoppingCart.getShippingPricingSnapshot(shippingOption).getShippingDiscountAmount());

		// Not as good discount, value should remain at $10
		shoppingCart.setShippingDiscountIfLower(shippingOptionCode, ruleId, actionId, oneDollar);
		assertEquals(tenDollars, shoppingCart.getShippingPricingSnapshot(shippingOption).getShippingDiscountAmount());
	}

	/**
	 * Shipping discounts that are valid and applied should create a new record.
	 */
	@Test
	public void verifyApplyShippingDiscountCreatesShippingDiscountRecordWhenBetterDiscount() {
		final long ruleId1 = 1L;
		final long ruleId2 = 2L;

		final String shippingOption = UUID.randomUUID().toString();

		final Money existingDiscount = Money.valueOf(BigDecimal.ONE, CURRENCY);
		shoppingCart.setShippingDiscountIfLower(shippingOption, ruleId1, ACTION_ID, existingDiscount);

		final Money betterDiscount = Money.valueOf(BigDecimal.TEN, CURRENCY);
		shoppingCart.setShippingDiscountIfLower(shippingOption, ruleId2, ACTION_ID, betterDiscount);

		final PromotionRecordContainerImpl promotionRecordContainer = (PromotionRecordContainerImpl) shoppingCart.getPromotionRecordContainer();

		final DiscountRecord discountRecord = promotionRecordContainer.getDiscountRecord(ruleId2, ACTION_ID);
		assertNotNull("Expected a discount record to be created when a better discount is applied", discountRecord);
		assertThat(discountRecord, instanceOf(ShippingDiscountRecordImpl.class));
	}


	/**
	 * Shipping discounts that are valid and applied should mark existing records for this shipping options as superseded.
	 */
	@Test
	public void verifyApplyShippingDiscountMarksExistingRecordsWithSameShippingOptionAsSupersededWhenBetterDiscount() {
		final long ruleId1 = 1L;
		final long ruleId2 = 2L;

		final String shippingOptionCode = UUID.randomUUID().toString();

		final Money existingDiscount = Money.valueOf(BigDecimal.ONE, CURRENCY);
		shoppingCart.setShippingDiscountIfLower(shippingOptionCode, ruleId1, ACTION_ID, existingDiscount);

		final Money betterDiscount = Money.valueOf(BigDecimal.TEN, CURRENCY);
		shoppingCart.setShippingDiscountIfLower(shippingOptionCode, ruleId2, ACTION_ID, betterDiscount);

		final PromotionRecordContainerImpl promotionRecordContainer = (PromotionRecordContainerImpl) shoppingCart.getPromotionRecordContainer();

		final DiscountRecord discountRecord = promotionRecordContainer.getDiscountRecord(ruleId1, ACTION_ID);
		assertTrue("Expected existing discount to be marked as superseded when a better discount is applied", discountRecord.isSuperceded());
	}

	/**
	 * Shipping discounts that are valid and applied should not mark existing records for other shipping options as superseded.
	 */
	@Test
	public void verifyApplyShippingDiscountDoesNotMarkExistingRecordsWithDifferentShippingOptionAsSuperseded() {
		final long ruleId1 = 1L;
		final long ruleId2 = 2L;

		final String shippingOptionCode1 = UUID.randomUUID().toString();
		final String shippingOptionCode2 = UUID.randomUUID().toString();

		final Money existingDiscount = Money.valueOf(BigDecimal.ONE, CURRENCY);
		shoppingCart.setShippingDiscountIfLower(shippingOptionCode1, ruleId1, ACTION_ID, existingDiscount);

		final Money betterDiscount = Money.valueOf(BigDecimal.TEN, CURRENCY);
		shoppingCart.setShippingDiscountIfLower(shippingOptionCode2, ruleId2, ACTION_ID, betterDiscount);

		final PromotionRecordContainerImpl promotionRecordContainer = (PromotionRecordContainerImpl) shoppingCart.getPromotionRecordContainer();

		final DiscountRecord discountRecord = promotionRecordContainer.getDiscountRecord(ruleId1, ACTION_ID);
		assertFalse("Expected existing discount not to be marked as superseded when a better discount is applied to a different shipping service "
							+ "level", discountRecord.isSuperceded());
	}

	/**
	 * Shipping discounts that are valid but not better than the existing discount should create a new record marked as superseded.
	 */
	@Test
	public void verifyApplyShippingDiscountCreatesSupersededShippingDiscountRecordWhenWorseDiscount() {
		final long ruleId1 = 1L;
		final long ruleId2 = 2L;

		final String shippingOptionCode = UUID.randomUUID().toString();

		final Money existingDiscount = Money.valueOf(BigDecimal.TEN, CURRENCY);
		shoppingCart.setShippingDiscountIfLower(shippingOptionCode, ruleId1, ACTION_ID, existingDiscount);

		final Money worseDiscount = Money.valueOf(BigDecimal.ONE, CURRENCY);
		shoppingCart.setShippingDiscountIfLower(shippingOptionCode, ruleId2, ACTION_ID, worseDiscount);

		final PromotionRecordContainerImpl promotionRecordContainer = (PromotionRecordContainerImpl) shoppingCart.getPromotionRecordContainer();

		final DiscountRecord discountRecord = promotionRecordContainer.getDiscountRecord(ruleId2, ACTION_ID);
		assertTrue("Expected new discount to be marked as superseded when a better discount is already present", discountRecord.isSuperceded());
	}

	@Test
	public void verifyApplyShippingDiscountNoOpsWhenSameShippingOptionAndDiscountAmount() throws Exception {
		final long ruleId = 1L;

		final String shippingOptionCode = UUID.randomUUID().toString();

		final Money existingDiscount = Money.valueOf(BigDecimal.TEN, CURRENCY);
		shoppingCart.setShippingDiscountIfLower(shippingOptionCode, ruleId, ACTION_ID, existingDiscount);

		shoppingCart.setShippingDiscountIfLower(shippingOptionCode, ruleId, ACTION_ID, existingDiscount);

		final PromotionRecordContainerImpl promotionRecordContainer = (PromotionRecordContainerImpl) shoppingCart.getPromotionRecordContainer();

		final DiscountRecord discountRecord = promotionRecordContainer.getDiscountRecord(ruleId, ACTION_ID);
		assertFalse("Expected discount not to be marked as superseded when the same discount is set a second time", discountRecord.isSuperceded());
	}

	@Test
	public void verifyGetShippingCostReturnsValueFromSnapshotOfSelectedShippingOption() throws Exception {
		final String shippingOptionCodeSelected = "SHIP001";
		final String shippingOptionCodeNotSelected = "SHIP002";

		final ShippingOption shippingOptionSelected = createShippingOption(shippingOptionCodeSelected);
		final ShippingOption shippingOptionLevelNotSelected = createShippingOption(shippingOptionCodeNotSelected);

		context.checking(new Expectations() {
			{
				allowing(shippingOptionSelected).getCode();
				will(returnValue(shippingOptionCodeSelected));

				allowing(shippingOptionLevelNotSelected).getCode();
				will(returnValue(shippingOptionCodeNotSelected));
			}
		});

		shoppingCart.addShoppingCartItem(createShippableShoppingItem());

		final Money oneDollar = Money.valueOf(BigDecimal.ONE, CURRENCY);
		final Money tenDollars = Money.valueOf(BigDecimal.TEN, CURRENCY);

		shoppingCart.setShippingListPrice(shippingOptionCodeSelected, oneDollar);
		shoppingCart.setShippingListPrice(shippingOptionCodeNotSelected, tenDollars);

		shoppingCart.setSelectedShippingOption(shippingOptionSelected);

		assertEquals("Expected shipping cost to equal the amount from the snapshot of the selected shipping option",
					 oneDollar, shoppingCart.getShippingCost());
	}

	@Test
	public void verifyGetShippingCostReturnsOverrideIfPresent() throws Exception {
		final String shippingOptionCodeSelected = "SHIP001";
		final ShippingOption shippingOptionSelected = createShippingOption(shippingOptionCodeSelected);

		context.checking(new Expectations() {
			{
				allowing(shippingOptionSelected).getCode();
				will(returnValue(shippingOptionCodeSelected));
			}
		});

		shoppingCart.addShoppingCartItem(createShippableShoppingItem());

		final Money oneDollar = Money.valueOf(BigDecimal.ONE, CURRENCY);
		final Money tenDollars = Money.valueOf(BigDecimal.TEN, CURRENCY);

		shoppingCart.setShippingListPrice(shippingOptionCodeSelected, oneDollar);

		shoppingCart.setSelectedShippingOption(shippingOptionSelected);

		shoppingCart.setShippingCostOverride(tenDollars.getAmount());

		assertEquals("Expected shipping cost to ignore the amount from the selected shipping option, and instead return the override value",
					 tenDollars, shoppingCart.getShippingCost());
	}

	@Test
	public void verifyGetShippingCostReturnsZeroWhenNotShippable() throws Exception {
		final ShoppingItem shoppingItem = createNonShippableShoppingItem();
		shoppingCart.addShoppingCartItem(shoppingItem);

		assertEquals("Expected a flat shipping cost of $0.00 when the cart does not contain any shippable items",
					 Money.valueOf(BigDecimal.ZERO, CURRENCY), shoppingCart.getShippingCost());
	}

	@Test
	public void verifyTotalIsZeroWhenDiscountGreaterThanSubtotal() throws Exception {
		final BigDecimal subtotalAmount = new BigDecimal("1.00");
		final BigDecimal subtotalDiscountAmount = new BigDecimal("10.00");
		final BigDecimal subtotalAfterDiscount = new BigDecimal("0.00");

		shoppingCart.setSubtotalDiscountOverride(subtotalDiscountAmount);

		mockSubtotalCalculatorForAllCartItems(shoppingCart, subtotalAmount);

		assertEquals("Expected total to be zero when discount greater than the subtotal", subtotalAfterDiscount, shoppingCart.getTotal());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void verifyExplicitlySetSubtotalAmountSubtractedFromTotal() throws Exception {
		final BigDecimal subtotalAmount = new BigDecimal("10.00");
		final BigDecimal subtotalDiscountAmount = new BigDecimal("1.00");
		final BigDecimal subtotalAfterDiscount = subtotalAmount.subtract(subtotalDiscountAmount);

		shoppingCart.setSubtotalDiscountOverride(subtotalDiscountAmount);

		mockSubtotalCalculatorForAllCartItems(shoppingCart, subtotalAmount);

		assertEquals("Expected discount to be subtracted from total", subtotalAfterDiscount, shoppingCart.getTotal());
	}

	@Test(expected = IllegalArgumentException.class)
	public void verifyExceptionThrownWhenNegativeShippingAmountSet() throws Exception {
		final BigDecimal shippingCostAmount = new BigDecimal("-1.00");

		shoppingCart.setShippingCostOverride(shippingCostAmount);
	}

	@Test(expected = IllegalArgumentException.class)
	public void verifyExceptionThrownWhenNegativeSubtotalDiscountOverrideSet() throws Exception {
		final BigDecimal subtotalDiscountAmount = new BigDecimal("-1.00");

		shoppingCart.setSubtotalDiscountOverride(subtotalDiscountAmount);
	}

	@Test(expected = IllegalArgumentException.class)
	public void verifyExceptionThrownWhenNegativeSubtotalDiscountSet() throws Exception {
		final BigDecimal subtotalDiscountAmount = new BigDecimal("-1.00");

		shoppingCart.setSubtotalDiscount(subtotalDiscountAmount, RULE_ID, ACTION_ID);
	}

	@Test
	public void verifyClearingShippingAddressClearsShippingOptions() {
		// Given we have a shopping cart with a shipping address and shipping options set up
		final ShoppingCartImpl shoppingCart = getShoppingCart();
		final Optional<ShippingOption> selectedShippingOption = shoppingCart.getSelectedShippingOption();

		assertNotNull("The shipping address should be set on the cart", shoppingCart.getShippingAddress());
		assertTrue("The shopping cart should have a shipping option selected", selectedShippingOption.isPresent());
		assertEquals("The shopping cart should have the specific shipping option selected", SELECTED_SHIPPING_OPTION_CODE,
					 selectedShippingOption.get().getCode());
		assertNotNull("The shipping price should be calculated whilst the shipping address is set",
					  shoppingCart.getShippingListPrice(SELECTED_SHIPPING_OPTION_CODE));

		// When we clear the shipping address
		shoppingCart.setShippingAddress(null);

		// Then the selected and available shipping options should be cleared as no longer valid
		assertFalse("Selected shipping option should be cleared after the shipping address has been cleared.",
					shoppingCart.getSelectedShippingOption().isPresent());
	}

	@Test
	public void verifySettingDifferentShippingAddressClearsShippingOptions() {
		// Given we have a shopping cart with a shipping address and shipping options set up
		final ShoppingCartImpl shoppingCart = getShoppingCart();
		final Optional<ShippingOption> selectedShippingOption = shoppingCart.getSelectedShippingOption();

		assertNotNull("The shipping address should be set on the cart", shoppingCart.getShippingAddress());
		assertTrue("The shopping cart should have a shipping option selected", selectedShippingOption.isPresent());
		assertEquals("The shopping cart should have the specific shipping option selected", SELECTED_SHIPPING_OPTION_CODE,
					 selectedShippingOption.get().getCode());
		assertNotNull("The shipping price should be calculated whilst the shipping address is set",
					  shoppingCart.getShippingListPrice(SELECTED_SHIPPING_OPTION_CODE));

		// When we set the shipping address to a different address
		final Address differentAddress = getAddress();
		differentAddress.setCountry("FR");
		shoppingCart.setShippingAddress(differentAddress);

		// Then the selected and available shipping options should be cleared as no longer valid
		assertFalse("Selected shipping option should be cleared after the shipping address has been cleared.",
				   shoppingCart.getSelectedShippingOption().isPresent());
	}

	protected ShippingOption createShippingOption(final String shippingOptionCode) {
		final ShippingOption shippingOption = context.mock(ShippingOption.class, "Shipping Option " + UUID.randomUUID().toString());

		context.checking(new Expectations() {
			{
				allowing(shippingOption).getCode();
				will(returnValue(shippingOptionCode));
			}
		});

		return shippingOption;
	}

	private ShoppingItem createShippableShoppingItem() {
		final ShoppingItem shoppingItem = createBasicMockShoppingItem();

		context.checking(new Expectations() {
			{
				allowing(shoppingItem).isShippable(getProductSkuLookup());
				will(returnValue(true));
			}
		});

		return shoppingItem;
	}

	private ShoppingItem createNonShippableShoppingItem() {
		final ShoppingItem shoppingItem = createBasicMockShoppingItem();

		context.checking(new Expectations() {
			{
				allowing(shoppingItem).isShippable(getProductSkuLookup());
				will(returnValue(false));
			}
		});

		return shoppingItem;
	}

	private ShoppingItem createBasicMockShoppingItem() {
		final String guid = UUID.randomUUID().toString();
		final String skuGuid = getProductSku().getGuid();

		final ShoppingItem shoppingItem = context.mock(ShoppingItem.class, "Shopping Item " + guid);

		context.checking(new Expectations() {
			{
				allowing(shoppingItem).getGuid();
				will(returnValue(guid));

				allowing(shoppingItem).getSkuGuid();
				will(returnValue(skuGuid));

				allowing(shoppingItem).getChildren();
				will(returnValue(new ArrayList<>()));
			}
		});

		return shoppingItem;
	}

}
