/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.domain.impl;

import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.PriceSchedule;
import com.elasticpath.domain.catalog.PriceScheduleType;
import com.elasticpath.domain.catalog.PricingScheme;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.catalog.impl.PriceScheduleImpl;
import com.elasticpath.domain.catalog.impl.PricingSchemeImpl;
import com.elasticpath.domain.catalog.impl.ProductBundleImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.catalog.impl.ProductTypeImpl;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.domain.quantity.Quantity;
import com.elasticpath.domain.shoppingcart.DiscountRecord;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemRecurringPrice;
import com.elasticpath.domain.shoppingcart.impl.CartItem;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemRecurringPriceImpl;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemSimplePrice;
import com.elasticpath.domain.subscriptions.PaymentSchedule;
import com.elasticpath.domain.subscriptions.impl.PaymentScheduleImpl;
import com.elasticpath.money.Money;
import com.elasticpath.money.StandardMoneyFormatter;
import com.elasticpath.sellingchannel.ShoppingItemRecurringPriceAssembler;
import com.elasticpath.sellingchannel.impl.ShoppingItemRecurringPriceAssemblerImpl;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.catalog.SkuOptionService;
import com.elasticpath.service.pricing.impl.PaymentScheduleHelperImpl;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Tests the {@code AbstractShoppingItemImpl} class.
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.GodClass" })
public class AbstractShoppingItemImplTest {

	private static final String PRODUCT_SKU3 = "productSku3";
	private static final String PRODUCT_SKU = "productSku";
	private static final String PRODUCT_SKU2 = "productSku2";

	private static final Quantity MONTHLY_QTY = new Quantity(1, "month");
	private static final Quantity BI_MONTHLY_QTY = new Quantity(2, "month");
	private static final Quantity ANNUALLY_QTY = new Quantity(1, "year");
	private static final Quantity BI_ANNUALLY_QTY = new Quantity(2, "year");

	private static final Currency CURRENCY_CAD = Currency.getInstance(Locale.CANADA);

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;
	@Mock private ProductSkuLookup productSkuLookup;
	private ShoppingItemRecurringPriceAssemblerImpl recurringPriceAssembler;

	/**
	 * Set up required before each test.
	 */
	@Before
	public void setUp() {
		beanFactory = context.mock(BeanFactory.class);
		recurringPriceAssembler = new ShoppingItemRecurringPriceAssemblerImpl();
		recurringPriceAssembler.setBeanFactory(beanFactory);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.SHOPPING_ITEM_RECURRING_PRICE_ASSEMBLER, recurringPriceAssembler);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.SHOPPING_ITEM_RECURRING_PRICE, ShoppingItemRecurringPriceImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);

		final PaymentScheduleHelperImpl paymentScheduleHelper = getPaymentScheduleHelper();

		final SkuOptionService skuOptionService = context.mock(SkuOptionService.class);
		context.checking(new Expectations() {
			{
				allowing(skuOptionService).findOptionValueByKey("shoppingItemRecurringPrice 1"); will(returnValue(null));
				allowing(skuOptionService).findOptionValueByKey("shoppingItemRecurringPrice 2"); will(returnValue(null));
			}
		});
		paymentScheduleHelper.setSkuOptionService(skuOptionService);

		recurringPriceAssembler.setPaymentScheduleHelper(paymentScheduleHelper);

	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test method for ShoppingCartItemImpl.hashCode().
	 */
	@Test
	public void testHashCode() {
		final String guid1 = "guid1";
		ShoppingItem cartItemImpl = new ShoppingItemImpl();
		cartItemImpl.setGuid(guid1);

		final String guid2 = "guid2";
		ShoppingItem cartItemImpl2 = new ShoppingItemImpl();
		cartItemImpl2.setGuid(guid2);

		assertEquals(cartItemImpl.hashCode(), cartItemImpl.hashCode());

		assertNotSame(cartItemImpl.hashCode(), cartItemImpl2.hashCode());

		Map<ShoppingItem, String> testMap = new HashMap<>();

		testMap.put(cartItemImpl, "1");
		testMap.put(cartItemImpl2, "2");
		testMap.put(cartItemImpl, "4");

		assertEquals(Integer.parseInt("2"), testMap.size());
		assertEquals("4", testMap.get(cartItemImpl));
		assertEquals("2", testMap.get(cartItemImpl2));
	}

	/**
	 * Test for ShoppingCartItemImpl.equals().
	 */
	@SuppressWarnings("PMD.UseAssertEqualsInsteadOfAssertTrue")
	@Test
	public void testEquals() {
		final String guid1 = "guid1";
		ShoppingItem cartItemImpl = new ShoppingItemImpl();
		cartItemImpl.setGuid(guid1);

		final String guid2 = "guid2";
		ShoppingItem cartItemImpl2 = new ShoppingItemImpl();
		cartItemImpl2.setGuid(guid2);

		assertTrue(cartItemImpl.equals(cartItemImpl));
		assertTrue(cartItemImpl2.equals(cartItemImpl2));
		assertFalse(cartItemImpl.equals(cartItemImpl2));
	}

	/**
	 * Tests that two children with the same sku can be added as bundle items.
	 **/
	@Test
	public void testAddingTwoBundleItemsWithSameSku() {
		ShoppingItemImpl item = new ShoppingItemImpl();
		ProductSku itemSku = new ProductSkuImpl();
		itemSku.setProduct(new ProductBundleImpl());
		itemSku.setGuid("item-sku");
		item.setSkuGuid(itemSku.getGuid());
		ShoppingItemImpl child1 = new ShoppingItemImpl();
		ProductSku sku1 = new ProductSkuImpl();
		sku1.setSkuCode("skuA");
		sku1.setGuid(sku1.getSkuCode());
		child1.setSkuGuid(sku1.getGuid());
		child1.setBundleConstituent(true);

		ShoppingItemImpl child2 = new ShoppingItemImpl();
		ProductSku sku2 = new ProductSkuImpl();
		sku2.setSkuCode("skuA");
		sku2.setGuid(sku2.getSkuCode());
		child2.setSkuGuid(sku2.getGuid());
		child2.setBundleConstituent(true);

		item.addChildItem(child1);
		item.addChildItem(child2);

		givenProductSkuLookupWillFindSku(itemSku);
		givenProductSkuLookupWillFindSku(sku1);
		givenProductSkuLookupWillFindSku(sku2);

		assertEquals("Should be two separate items", 2, item
				.getBundleItems(productSkuLookup).size());
	}

	/** Test for testHasDependentCartItems(). */
	@Test
	public void testHasBundleItems() {
		ProductSku sku = new ProductSkuImpl();
		sku.setProduct(new ProductBundleImpl());
		sku.setGuid("guid");
		givenProductSkuLookupWillFindSku(sku);

		CartItem cartItemImpl = new ShoppingItemImpl();
		cartItemImpl.setSkuGuid(sku.getGuid());
		CartItem bundleConstituentCartItem = new ShoppingItemImpl();
		bundleConstituentCartItem.setBundleConstituent(true);
		assertFalse(cartItemImpl.hasBundleItems(productSkuLookup));
		cartItemImpl.addChildItem(bundleConstituentCartItem);
		assertTrue(cartItemImpl.hasBundleItems(productSkuLookup));
	}

	/**
	 * Bundle should be shippable if it's constituents all are.
	 */
	@Test
	public void testIsShippableFully() {
		ShoppingItem cartItem = mockBundleCartItem(true, PRODUCT_SKU);
		ShoppingItem bundleConstituentItem = mockCartItem(true, PRODUCT_SKU2);

		cartItem.addChildItem(bundleConstituentItem);

		assertTrue("Bundle should be shippable if its constituents all are.", cartItem.isShippable(productSkuLookup));
	}

	/**
	 * Test for bundle with one constituent shippable, one not. Bundle should be considered shippable.
	 */
	@Test
	public void testIsShippablePartial() {
		ShoppingItem cartItem = mockBundleCartItem(false, PRODUCT_SKU);
		ShoppingItem dependentCartItem = mockCartItem(true, PRODUCT_SKU2);
		ShoppingItem dependentItem2 = mockCartItem(false, PRODUCT_SKU3);

		cartItem.addChildItem(dependentCartItem);
		cartItem.addChildItem(dependentItem2);

		assertTrue("Bundle should be considered shippable if one constituent shippable, one not", cartItem.isShippable(productSkuLookup));
	}

	/**
	 * Test for bundle marked as shippable but constituents marked as unshippable.
	 */
	@Test
	public void testIsShippableBundleButNotConstituents() {
		ShoppingItem cartItem = mockBundleCartItem(true, PRODUCT_SKU);
		ShoppingItem dependentCartItem = mockCartItem(false, PRODUCT_SKU2);

		cartItem.addChildItem(dependentCartItem);

		assertFalse("bundle marked as shippable but constituents marked as unshippable should be unshippable.",
				cartItem.isShippable(productSkuLookup));
	}

	/**
	 * Test for shippable when neither the bundle nor the constituent are shippable.
	 */
	@Test
	public void testNoPartShippable() {
		ShoppingItem cartItem = mockBundleCartItem(false, PRODUCT_SKU);
		ShoppingItem dependentCartItem = mockCartItem(false, PRODUCT_SKU2);

		cartItem.addChildItem(dependentCartItem);

		assertFalse("Bundle cart item with self and constituents not shippable should not be shippable.",
				cartItem.isShippable(productSkuLookup));
	}


	private ShoppingItem mockBundleCartItem(final boolean shippable, final String name) {
		final ProductBundle bundle = context.mock(ProductBundle.class, name + "_bundle");
		final ProductSku productSku = context.mock(ProductSku.class, name);

		context.checking(new Expectations() { {
			String skuGuid = new RandomGuidImpl().toString();
			allowing(productSku).isShippable(); will(returnValue(shippable));
			allowing(productSku).setProduct(bundle);
			allowing(productSku).getProduct(); will(returnValue(bundle));
			allowing(productSku).getGuid(); will(returnValue(skuGuid));

			allowing(productSkuLookup).findByGuid(skuGuid); will(returnValue(productSku));
		} });

		ShoppingItem cartItem = new ShoppingItemImpl();
		productSku.setProduct(bundle);
		cartItem.setSkuGuid(productSku.getGuid());
		return cartItem;
	}

	private ShoppingItem mockCartItem(final boolean shippable, final String name) {
		final Product product = context.mock(Product.class, name + "_product");
		final ProductSku productSku = context.mock(ProductSku.class, name);

		context.checking(new Expectations() { {
			String skuGuid = new RandomGuidImpl().toString();
			allowing(productSku).isShippable(); will(returnValue(shippable));
			allowing(productSku).setProduct(product);
			allowing(productSku).getProduct(); will(returnValue(product));
			allowing(productSku).getGuid(); will(returnValue(skuGuid));

			allowing(productSkuLookup).findByGuid(skuGuid);
			will(returnValue(productSku));
		} });

		ShoppingItem cartItem = new ShoppingItemImpl();
		productSku.setProduct(product);
		cartItem.setSkuGuid(productSku.getGuid());
		return cartItem;
	}

	/**
	 * Test that the lowest a cartItem amount can be is zero (never negative),
	 * regardless of the discount amount.
	 */
	@Test
	public void testLowestAmountIsZero() {
		final BigDecimal lowestPrice = BigDecimal.ONE;
		final BigDecimal discount = BigDecimal.TEN;
		Currency currency = Currency.getInstance("CAD");
		AbstractShoppingItemImpl item = prepareShoppingItem(lowestPrice, currency, false);
		item.applyDiscount(discount, productSkuLookup);
		assertEquals("The cartItem amount should never be less than zero regardless of the discount amount",
				item.calculateItemTotal().compareTo(BigDecimal.ZERO), 0);
	}


	/**
	 *	test up of internal ShoppingItemRecurringPrice SET.
	 *	need to create at least two prices/x/x/x/ objects with no SimplePrice object in them
	 *	make sure to call setRecurringPrices to exercise the set dirty flag
	 */
	@Test
	public void testGetLowestPriceAndCalculateItemTotalWithRecurringPrices() {
		ShoppingItemRecurringPrice shoppingItemRecurringPrice1 = new ShoppingItemRecurringPriceImpl();
		shoppingItemRecurringPrice1.setPaymentScheduleName("shoppingItemRecurringPrice 1");
		shoppingItemRecurringPrice1.setPaymentFrequency(MONTHLY_QTY);
		shoppingItemRecurringPrice1.setScheduleDuration(ANNUALLY_QTY);
		shoppingItemRecurringPrice1.setSimplePrice(
				new ShoppingItemSimplePrice(new BigDecimal("10.99"), new BigDecimal("8.99"), new BigDecimal("7.99")));

		ShoppingItemRecurringPrice shoppingItemRecurringPrice2 = new ShoppingItemRecurringPriceImpl();
		shoppingItemRecurringPrice1.setPaymentScheduleName("shoppingItemRecurringPrice 2");
		shoppingItemRecurringPrice1.setPaymentFrequency(BI_MONTHLY_QTY);
		shoppingItemRecurringPrice1.setScheduleDuration(BI_ANNUALLY_QTY);
		shoppingItemRecurringPrice1.setSimplePrice(
				new ShoppingItemSimplePrice(new BigDecimal("7.77"), new BigDecimal("6.66"), new BigDecimal("5.55")));

		Set<ShoppingItemRecurringPrice> recurringPrices = new HashSet<>();

		recurringPrices.add(shoppingItemRecurringPrice1);
		recurringPrices.add(shoppingItemRecurringPrice2);

		AbstractShoppingItemImpl item = prepareShoppingItemWithRecurringPrice(recurringPrices, CURRENCY_CAD);

		assertEquals("0.00", item.getLowestUnitPrice().getAmount().toString());  //unit price SHOULD return 0.00 for recurring priced items
		assertEquals("0.00", item.calculateItemTotal().toString());  //unit price SHOULD return 0.00 for calculating the item total too
	}

	private AbstractShoppingItemImpl prepareShoppingItemWithRecurringPrice(final Set<ShoppingItemRecurringPrice> recurringPrices,
			final Currency currency) {
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE, PriceImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICING_SCHEME, PricingSchemeImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE_SCHEDULE, PriceScheduleImpl.class);

		AbstractShoppingItemImpl item = new ShoppingItemImpl() {  //no overriding of get prices
			private static final long serialVersionUID = -2100849016179282868L;

			@Override
			public Currency getCurrency() {
				return currency;
			}

			@Override
			protected ShoppingItemRecurringPriceAssembler getShoppingItemRecurringPriceAssembler() {
				return recurringPriceAssembler;
			}
		};
		item.setRecurringPrices(recurringPrices);

		return item;
	}


	/**
	 * Test that the amount is equal to the shoppingItem price multiplied by the quantity of the cart item
	 * minus the discount applied to the item.
	 */
	@Test
	public void testAmountCalculation() {
		final int quantity = 10;
		final int shoppingItemPrice = 20;
		final int discount = 1;
		final BigDecimal lowestPrice = new BigDecimal(shoppingItemPrice);
		Currency currency = Currency.getInstance("CAD");
		AbstractShoppingItemImpl item = prepareShoppingItem(lowestPrice, currency, false);
		item.applyDiscount(new BigDecimal(discount), productSkuLookup);
		item.setQuantity(quantity);
		assertEquals(shoppingItemPrice * quantity - discount, item.calculateItemTotal().intValue());
	}

	/**
	 * Test that the amount is equal to the shoppingItem price multiplied by the quantity of the cart item
	 * minus the discount applied to the item.
	 */
	@Test
	public void testAmountCalculationDiscountSkipped() {
		final int quantity = 10;
		final int shoppingItemPrice = 20;
		final int discount = 1;
		final BigDecimal lowestPrice = new BigDecimal(20);
		Currency currency = Currency.getInstance("CAD");
		AbstractShoppingItemImpl item = prepareShoppingItem(lowestPrice, currency, true);
		item.applyDiscount(new BigDecimal(discount), productSkuLookup);
		item.setQuantity(quantity);

		assertEquals(shoppingItemPrice * quantity, item.calculateItemTotal().intValue());
	}


	/**.*/
	@Test
	public void testSetNullFieldValues() {
		AbstractShoppingItemImpl item = new ShoppingItemImpl();
		item.mergeFieldValues(null);
		assertEquals(0, item.getItemData().size());
	}

	/**.*/
	@Test
	public void testSetTwoFieldValues() {
		AbstractShoppingItemImpl item = new ShoppingItemImpl();
		Map<String, String> values = new HashMap<>();
		values.put("key1", "value1");
		values.put("key2", "value2");
		item.mergeFieldValues(values);
		assertEquals(2, item.getItemData().size());
		assertEquals("value1", item.getFieldValue("key1"));
		assertEquals("value2", item.getFieldValue("key2"));
	}

	private AbstractShoppingItemImpl prepareShoppingItem(final BigDecimal lowestUnitPrice,
			final Currency currency, final boolean excludedFromDiscount) {
		final String skuGuid = "sku-guid";
		final ProductType productType = context.mock(ProductType.class);
		final Product product = context.mock(Product.class);
		final ProductSku productSku = context.mock(ProductSku.class);
		AbstractShoppingItemImpl item = new ShoppingItemImpl() {
			private static final long serialVersionUID = -4910510721160996614L;

			@Override
			public BigDecimal findLowestUnitPrice() {
				return lowestUnitPrice;
			}
		};
		item.setSkuGuid(skuGuid);
		item.setCurrency(currency);

		context.checking(new Expectations() { {
							allowing(productType).isExcludedFromDiscount(); will(returnValue(excludedFromDiscount));
							allowing(product).getProductType(); will(returnValue(productType));
							allowing(productSku).getProduct(); will(returnValue(product));
							allowing(productSku).getGuid(); will(returnValue(skuGuid));
							allowing(productSkuLookup).findByGuid(skuGuid); will(returnValue(productSku));
						} }
		);
		return item;
	}

	private PaymentScheduleHelperImpl getPaymentScheduleHelper() {
		final PaymentScheduleHelperImpl paymentScheduleHelper = new PaymentScheduleHelperImpl();
		paymentScheduleHelper.setBeanFactory(beanFactory);

		context.checking(new Expectations() {
			{
				allowing(beanFactory).getBean(ContextIdNames.PAYMENT_SCHEDULE); will(returnValue(new PaymentScheduleImpl()));
			}
		});

		return paymentScheduleHelper;
	}

	/**
	 * Tests the canReceiveCartPromotion() method, when the item is set as not discountable.
	 */
	@Test
	public void testCanReceiveCartPromotionNonDiscountable() {
		AbstractShoppingItemImpl shoppingItem = createShoppingItem(false);

		assertFalse(shoppingItem.canReceiveCartPromotion(productSkuLookup));
	}

	/**
	 * Tests the canReceiveCartPromotion() method, when the item is set as not discountable, and has a purchase-time price.
	 */
	@Test
	public void testCanReceiveCartPromotionNonDiscountablePurchaseTimePrice() {
		AbstractShoppingItemImpl shoppingItem = createShoppingItem(false);

		PriceImpl price = createSimplePrice(BigDecimal.TEN);
		shoppingItem.setPrice(1, price);
		assertFalse(shoppingItem.canReceiveCartPromotion(productSkuLookup));
	}

	/**
	 * Tests the canReceiveCartPromotion() method, when the item is set as not discountable, and has a recurring price.
	 */
	@Test
	public void testCanReceiveCartPromotionNonDiscountableRecurringPrice() {
		AbstractShoppingItemImpl shoppingItem = createShoppingItem(false);

		PriceImpl price = createSimplePrice(BigDecimal.ZERO);
		price.setPricingScheme(getRecurringScheme());
		shoppingItem.setPrice(1, price);
		assertFalse(shoppingItem.canReceiveCartPromotion(productSkuLookup));
	}

	/**
	 * Tests the canReceiveCartPromotion() method, when the item is set as not discountable, and has a mixed
	 * recurring and purchase-time price.
	 */
	@Test
	public void testCanReceiveCartPromotionNonDiscountableMixedPrice() {
		AbstractShoppingItemImpl shoppingItem = createShoppingItem(false);

		PriceImpl price = createSimplePrice(BigDecimal.TEN);
		price.setPricingScheme(getRecurringScheme());
		shoppingItem.setPrice(1, price);
		assertFalse(shoppingItem.canReceiveCartPromotion(productSkuLookup));
	}

	/**
	 * Tests the canReceiveCartPromotion() method, when the item is set as discountable.
	 */
	@Test
	public void testCanReceiveCartPromotionDiscountable() {
		AbstractShoppingItemImpl shoppingItem = createShoppingItem(true);

		assertTrue(shoppingItem.canReceiveCartPromotion(productSkuLookup));
	}

	/**
	 * Tests the canReceiveCartPromotion() method, when the item is set as discountable, and has a purchase-time price.
	 */
	@Test
	public void testCanReceiveCartPromotionDiscountablePurchaseTimePrice() {
		AbstractShoppingItemImpl shoppingItem = createShoppingItem(true);

		PriceImpl price = createSimplePrice(BigDecimal.TEN);
		shoppingItem.setPrice(1, price);
		assertTrue(shoppingItem.canReceiveCartPromotion(productSkuLookup));
	}

	/**
	 * Tests the canReceiveCartPromotion() method, when the item is set as discountable, and has a recurring price.
	 */
	@Test
	public void testCanReceiveCartPromotionDiscountableRecurringPrice() {
		AbstractShoppingItemImpl shoppingItem = createShoppingItem(true);

		PriceImpl price = createSimplePrice(BigDecimal.ZERO);
		price.setPricingScheme(getRecurringScheme());
		shoppingItem.setPrice(1, price);
		assertFalse(shoppingItem.canReceiveCartPromotion(productSkuLookup));
	}

	/**
	 * Tests the canReceiveCartPromotion() method, when the item is set as discountable, and has a mixed
	 * recurring and purchase-time price.
	 */
	@Test
	public void testCanReceiveCartPromotionDiscountableMixedPrice() {
		AbstractShoppingItemImpl shoppingItem = createShoppingItem(true);

		PriceImpl price = createSimplePrice(BigDecimal.TEN);
		price.setPricingScheme(getRecurringScheme());
		shoppingItem.setPrice(1, price);
		assertTrue(shoppingItem.canReceiveCartPromotion(productSkuLookup));
	}

	/**
	 * Tests the setFieldValue() method to check that the correct
	 * key and value is stored as item data.
	 */
	@Test
	public void testSetFieldValueForKeyAndValue() {
		final String key = "Record";
		final String value = "Test Data";
		final AbstractShoppingItemImpl shoppingItem = new ShoppingItemImpl();
		shoppingItem.setFieldValue(key, value);

		assertTrue("Expected shopping item to contain item data", shoppingItem.getItemData().containsKey(key));
		assertEquals(key, shoppingItem.getItemData().get(key).getKey());
		assertEquals(value, shoppingItem.getItemData().get(key).getValue());
	}

	/**
	 * Tests that setFieldValue() can successfully modify existing item data value.
	 */
	@Test
	public void testModifyValueOfExistingItemData() {
		final String key = "Record";
		final String oldValue = "Old Data";
		final String newValue = "New Data";
		final AbstractShoppingItemImpl shoppingItem = new ShoppingItemImpl();
		shoppingItem.setFieldValue(key, oldValue);

		assertEquals(oldValue, shoppingItem.getItemData().get(key).getValue());

		shoppingItem.setFieldValue(key, newValue);

		assertEquals(newValue, shoppingItem.getItemData().get(key).getValue());
	}

	/**
	 * Tests that setFieldValue() modifies existing item data
	 * instead of replacing it with a new item data with same key.
	 */
	@Test
	public void testExistingItemDataObjectModified() {
		final String key = "Record";
		final String oldValue = "Old Data";
		final String newValue = "New Data";
		final AbstractShoppingItemImpl shoppingItem = new ShoppingItemImpl();
		shoppingItem.setFieldValue(key, oldValue);

		AbstractItemData itemData = shoppingItem.getItemData().get(key);
		shoppingItem.setFieldValue(key, newValue);

		assertEquals(newValue, shoppingItem.getItemData().get(key).getValue());
		assertTrue("Expected setFieldValue to modify existing item data object", itemData == shoppingItem.getItemData().get(key));
	}

	@Test
	public void verifyClearDiscountRemovesAllDiscountRecords() throws Exception {
		// Given a Shopping Item whose price contains a discount record
		final PriceImpl price = createSimplePrice(BigDecimal.TEN);
		price.addDiscountRecord(context.mock(DiscountRecord.class));

		final AbstractShoppingItemImpl shoppingItem = createShoppingItem(true);
		shoppingItem.setPrice(1, price);

		// When discounts are cleared
		shoppingItem.clearDiscount();

		// Then the price does not contain any discount records
		assertThat(price.getDiscountRecords(), empty());
	}

	@Test
	public void verifyHasPriceTrueWhenHasLowestUnitPrice() throws Exception {
		final AbstractShoppingItemImpl shoppingItem = createShoppingItem(true);
		shoppingItem.setListUnitPriceInternal(BigDecimal.TEN);

		assertTrue("Expected hasPrice to return true when an internal unit price is set", shoppingItem.hasPrice());
	}

	@Test
	public void verifyHasPriceTrueWhenPriceExists() throws Exception {
		final AbstractShoppingItemImpl shoppingItem = createShoppingItem(true);
		shoppingItem.setPrice(1, createSimplePrice(BigDecimal.TEN));

		assertTrue("Expected hasPrice to return true when a Price is set", shoppingItem.hasPrice());
	}

	@Test
	public void verifyHasPriceTrueWhenPriceZeroDollars() throws Exception {
		final AbstractShoppingItemImpl shoppingItem = createShoppingItem(true);
		shoppingItem.setPrice(1, createSimplePrice(BigDecimal.ZERO));

		assertTrue("Expected hasPrice to return true when a Price is $0", shoppingItem.hasPrice());
	}

	@Test
	public void shouldReturnFalseWhenComparingSingleSkuShoppingItemWithAnotherItem() {
		ShoppingItem shoppingItem = createShoppingItemWithSkuAndOptionalNameFieldValues(PRODUCT_SKU, null);
		ShoppingItem comparisonItem = createShoppingItemWithSkuAndOptionalNameFieldValues(PRODUCT_SKU, null);

		ProductType productType = createProductTypeAndSetMultiSku(false);
		Product product = createProductOfProductType(productType);

		ProductSku itemSku = createItemSkuWithProductAndGuid(product, PRODUCT_SKU);

		givenProductSkuLookupWillFindSku(itemSku);

		assertFalse(shoppingItem.isMultiSku(productSkuLookup));
		assertEquals(shoppingItem.getSkuGuid(), comparisonItem.getSkuGuid());
		assertEquals(shoppingItem.getFields(), comparisonItem.getFields());

		assertFalse(shoppingItem.isSameMultiSkuItem(productSkuLookup, comparisonItem));
	}

	@Test
	public void shouldReturnFalseWhenComparingShoppingItemsWithDifferentSkus() {
		ShoppingItem shoppingItem = createShoppingItemWithSkuAndOptionalNameFieldValues(PRODUCT_SKU, null);
		ShoppingItem comparisonItem = createShoppingItemWithSkuAndOptionalNameFieldValues(PRODUCT_SKU2, null);

		ProductType productType = createProductTypeAndSetMultiSku(true);
		Product product = createProductOfProductType(productType);

		ProductSku itemSku = createItemSkuWithProductAndGuid(product, PRODUCT_SKU);

		givenProductSkuLookupWillFindSku(itemSku);

		assertTrue(shoppingItem.isMultiSku(productSkuLookup));
		assertNotEquals(shoppingItem.getSkuGuid(), comparisonItem.getSkuGuid());
		assertEquals(shoppingItem.getFields(), comparisonItem.getFields());

		assertFalse(shoppingItem.isSameMultiSkuItem(productSkuLookup, comparisonItem));
	}

	@Test
	public void shouldReturnFalseWhenComparingShoppingItemsWithSameSkusAndDifferentFields() {
		ShoppingItem shoppingItem = createShoppingItemWithSkuAndOptionalNameFieldValues(PRODUCT_SKU, "Batman");
		ShoppingItem comparisonItem = createShoppingItemWithSkuAndOptionalNameFieldValues(PRODUCT_SKU, "Robin");

		ProductType productType = createProductTypeAndSetMultiSku(true);
		Product product = createProductOfProductType(productType);

		ProductSku itemSku = createItemSkuWithProductAndGuid(product, PRODUCT_SKU);

		givenProductSkuLookupWillFindSku(itemSku);

		assertTrue(shoppingItem.isMultiSku(productSkuLookup));
		assertEquals(shoppingItem.getSkuGuid(), comparisonItem.getSkuGuid());
		assertNotEquals(shoppingItem.getFields(), comparisonItem.getFields());

		assertFalse(shoppingItem.isSameMultiSkuItem(productSkuLookup, comparisonItem));
	}

	@Test
	public void shouldReturnTrueWhenComparingShoppingItemsWithSameSkusAndSameFields() {
		ShoppingItem shoppingItem = createShoppingItemWithSkuAndOptionalNameFieldValues(PRODUCT_SKU, null);
		ShoppingItem comparisonItem = createShoppingItemWithSkuAndOptionalNameFieldValues(PRODUCT_SKU, null);

		ProductType productType = createProductTypeAndSetMultiSku(true);
		Product product = createProductOfProductType(productType);

		ProductSku itemSku = createItemSkuWithProductAndGuid(product, PRODUCT_SKU);

		givenProductSkuLookupWillFindSku(itemSku);

		assertTrue(shoppingItem.isMultiSku(productSkuLookup));
		assertEquals(shoppingItem.getSkuGuid(), comparisonItem.getSkuGuid());
		assertEquals(shoppingItem.getFields(), comparisonItem.getFields());

		assertTrue(shoppingItem.isSameMultiSkuItem(productSkuLookup, comparisonItem));
	}

	private PriceImpl createSimplePrice(final BigDecimal amount) {
		PriceImpl price = new PriceImpl();
		price.setListPrice(Money.valueOf(amount, Currency.getInstance(Locale.US)));
		return price;
	}

	private PricingScheme getRecurringScheme() {
		PricingScheme pricingScheme = new PricingSchemeImpl();
		PriceSchedule priceSchedule = new PriceScheduleImpl();
		priceSchedule.setType(PriceScheduleType.RECURRING);
		PaymentSchedule paymentSchedule = new PaymentScheduleImpl();
		paymentSchedule.setName("Monthly");
		priceSchedule.setPaymentSchedule(paymentSchedule);
		pricingScheme.setPriceForSchedule(priceSchedule, createSimplePrice(BigDecimal.TEN));
		return pricingScheme;
	}

	private AbstractShoppingItemImpl createShoppingItem(final boolean discountable) {
		AbstractShoppingItemImpl shoppingItem = new ShoppingItemImpl() {
			private static final long serialVersionUID = -6875611418637317084L;

			@Override
			public boolean isDiscountable(final ProductSkuLookup productSkuLookup) {
				return discountable;
			};
		};
		return shoppingItem;
	}

	private Product createProductOfProductType(final ProductType productType) {
		Product product = new ProductImpl();
		product.setProductType(productType);
		return product;
	}

	private ProductType createProductTypeAndSetMultiSku(final boolean isMultiSku) {
		ProductType productType = new ProductTypeImpl();
		productType.setMultiSku(isMultiSku);
		return productType;
	}

	private ProductSku createItemSkuWithProductAndGuid(final Product product, final String guid) {
		ProductSku productSku = new ProductSkuImpl();
		productSku.setProduct(product);
		productSku.setGuid(guid);
		return productSku;
	}

	private void givenProductSkuLookupWillFindSku(final ProductSku sku) {
		context.checking(new Expectations() {
			{
				allowing(productSkuLookup).findByGuid(sku.getGuid());
				will(returnValue(sku));
			}
		});
	}

	private ShoppingItem createShoppingItemWithSkuAndOptionalNameFieldValues(final String sku, final String nameFieldValue) {
		ShoppingItem shoppingItem = new ShoppingItemImpl();
		shoppingItem.setSkuGuid(sku);
		shoppingItem.setFieldValue("name", Objects.isNull(nameFieldValue) ? "Batman" : nameFieldValue);
		return shoppingItem;
	}
}
