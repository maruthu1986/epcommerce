/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.domain.discounts.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartImpl;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartMementoImpl;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.money.Money;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.rules.PromotionRuleExceptions;
import com.elasticpath.service.shoppingcart.ShippableItemPredicate;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;
import com.elasticpath.test.jmock.AbstractCatalogDataTestCase;

/**
 * Test cases for <code>ShoppingCartDiscountItemContainerImpl</code>.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class ShoppingCartDiscountItemContainerImplTest extends AbstractCatalogDataTestCase {

	private static final long ACTION_UID = 123L;
	private static final long RULE_UID = 456L;

	private static final BigDecimal BIG_D_30 = new BigDecimal(30);
	private static final BigDecimal BIG_D_20 = new BigDecimal(20);
	private static final BigDecimal BIG_D_10 = new BigDecimal("10.00");
	private static final String SKU_GUID = UUID.randomUUID().toString();
	private static final String SKU_CODE = "MySKU0001";

	private ShoppingCartDiscountItemContainerImpl container;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		final ProductSkuLookup productSkuLookup = getProductSkuLookup();

		container = new ShoppingCartDiscountItemContainerImpl();
		container.setProductSkuLookup(productSkuLookup);
		container.setCurrency(CURRENCY);
		container.setShoppingItemsSubtotalCalculator(getShoppingItemSubtotalCalculator());
		container.setShippableItemPredicate(new ShippableItemPredicate<>(productSkuLookup));

		stubGetBean(ContextIdNames.SHOPPING_CART_MEMENTO, ShoppingCartMementoImpl.class);
	}

	/**
	 * Test case for DiscountItemContainer.recordRuleApplied(ruleId) method.
	 */
	@Test
	public void testDiscountItemContainerRecordRuleApplied() {
		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		final long ruleId = 1L;
		context.checking(new Expectations() {
			{
				oneOf(shoppingCart).ruleApplied(ruleId, 0, null, null, 0);
			}
		});

		ShoppingCartDiscountItemContainerImpl container =
				new ShoppingCartDiscountItemContainerImpl();
		container.setShoppingCart(shoppingCart);
		container.recordRuleApplied(ruleId, 0L, null, null, 0);

	}

	/**
	 * Test case for DiscountItemContainer.applySubtotalDiscount(discountAmount) method.
	 */
	@Test
	public void testDiscountItemContainerApplySubtotalDiscount() {
		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		final BigDecimal discountAmount = BigDecimal.TEN;
		context.checking(new Expectations() {
			{
				oneOf(shoppingCart).setSubtotalDiscount(discountAmount, RULE_UID, ACTION_UID);
			}
		});

		ShoppingCartDiscountItemContainerImpl container =
				new ShoppingCartDiscountItemContainerImpl();
		container.setShoppingCart(shoppingCart);
		container.applySubtotalDiscount(discountAmount, RULE_UID, ACTION_UID);

	}

	/**
	 * Test case for DiscountItemContainer.getCatalog() method.
	 */
	@Test
	public void testDiscountItemGetCatalog() {
		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		final Store store = context.mock(Store.class);
		final Catalog catalog = context.mock(Catalog.class);
		context.checking(new Expectations() {
			{
				oneOf(shoppingCart).getStore();
				will(returnValue(store));

				oneOf(store).getCatalog();
				will(returnValue(catalog));
			}
		});

		ShoppingCartDiscountItemContainerImpl container =
				new ShoppingCartDiscountItemContainerImpl();
		container.setShoppingCart(shoppingCart);
		Catalog catalog2 = container.getCatalog();
		assertEquals("catalog returned does not match expectation", catalog, catalog2);
	}

	/**
	 * Test case for DiscountItemContainer.addCartItem(skuCode, numItems) method.

	 @Test
	 public void testDiscountItemContainerAddCartItem() {
	 final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
	 final Store store = context.mock(Store.class);
	 final String skuCode = "skuCode";
	 final int numItems = 2;
	 final ElasticPath elasticPath = context.mock(ElasticPath.class);
	 final ShoppingItem cartItem = context.mock(ShoppingItem.class);
	 final CartDirector director = context.mock(CartDirector.class);
	 final List<ShoppingItem> list = new ArrayList<ShoppingItem>();
	 list.add(cartItem);


	 context.checking(new Expectations() {
	 {
	 oneOf(shoppingCart).getCartItem(skuCode);
	 will(returnValue(null));
	 oneOf(shoppingCart).isCartItemRemoved(skuCode);
	 will(returnValue(false));

	 oneOf(shoppingCart).getStore();
	 will(returnValue(store));

	 oneOf(elasticPath).getBean("cartDirector");
	 will(returnValue(director));

	 oneOf(shoppingCart).getCustomerSession();
	 will(returnValue(null));

	 oneOf(shoppingCart).getAppliedRules();
	 will(returnValue(null));

	 //allowing(director).getElasticPath().getBeanFactory("shoppingItemDtoFactory");
	 }
	 });

	 ShoppingCartDiscountItemContainerImpl container =
	 new ShoppingCartDiscountItemContainerImpl() {
	 @Override
	 public ElasticPath getElasticPath() {
	 return elasticPath;
	 }
	 };
	 container.setShoppingCart(shoppingCart);
	 ShoppingItem item2 = container.addCartItem(skuCode, numItems);
	 Assert.assertEquals("cart item added does not match expected value", cartItem, item2);

	 }
	 */

	/**
	 * Test case for DiscountItemContainer.getPriceAmount(cartItem) method.
	 */
	@Test
	public void testDiscountItemGetPriceAmount() {
		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		final Money money = Money.valueOf(BIG_D_10, CURRENCY);

		final ShoppingItem cartItem = new ShoppingItemImpl();
		cartItem.setPrice(1, createPrice(money.getAmount(), money.getAmount(), CURRENCY, 1));

		container.setShoppingCart(shoppingCart);

		final BigDecimal actual = container.getPriceAmount(cartItem);
		assertEquals("price amount code does not match expected value", money.getAmount(), actual);
	}

	/**
	 * Test case for DiscountItemContainer.getPriceAmount(cartItem) method when a discount is set.
	 */
	@Test
	public void testGetPriceAmountWithDiscountApplied() {
		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		container.setShoppingCart(shoppingCart);
		final ShoppingItem cartItem = createDiscountableShoppingItem();

		cartItem.setPrice(1, createPrice(BIG_D_30, BIG_D_20, CURRENCY, 1));
		cartItem.applyDiscount(BigDecimal.TEN, getProductSkuLookup());

		assertEquals("container.getPriceAmount(cartItem) does not match expected value after discount applied",
				BigDecimal.TEN.compareTo(container.getPriceAmount(cartItem)), 0);
	}

	/**
	 * Test case for DiscountItemContainer.getPriceAmount(cartItem) method when no discount is set.
	 */
	@Test
	public void testGetPriceAmountWithNoDiscountApplied() {
		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		final ShoppingCartDiscountItemContainerImpl container = new ShoppingCartDiscountItemContainerImpl();
		container.setShoppingCart(shoppingCart);
		final ShoppingItem cartItem = createDiscountableShoppingItem();

		cartItem.setPrice(1, createPrice(BIG_D_30, BIG_D_20, CURRENCY, 1));

		assertEquals("container.getPriceAmount(cartItem) does not match expected value",
				BIG_D_20.compareTo(container.getPriceAmount(cartItem)), 0);
	}

	@Test
	public void verifyCalculateSubtotalOfDiscountableItemsAddsAmountsOfDiscountableItems() throws Exception {
		final ShoppingItem shoppingItem1 = createDiscountableShoppingItem();
		shoppingItem1.setPrice(1, createPrice(BIG_D_10, BIG_D_10, CURRENCY, 1));

		final ShoppingItem shoppingItem2 = createDiscountableShoppingItem();
		shoppingItem2.setPrice(1, createPrice(BIG_D_20, BIG_D_20, CURRENCY, 1));

		final BigDecimal expectedDiscountableAmount = BIG_D_30.setScale(CURRENCY.getDefaultFractionDigits());

		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		allowingShoppingCartWithAnyPredicateWillReturn(shoppingCart, shoppingItem1, shoppingItem2);

		createProductSkuFor(shoppingItem1);
		createProductSkuFor(shoppingItem2);

		container.setShoppingCart(shoppingCart);

		final BigDecimal actualDiscountableAmount = container.calculateSubtotalOfDiscountableItems();

		assertEquals("Expected the discount amount to be the sum of the total of all discountable items",
				expectedDiscountableAmount,
				actualDiscountableAmount);
	}

	@Test
	public void verifyCalculateSubtotalOfDiscountableItemsIncludesExistingDiscounts() throws Exception {
		final ShoppingItem shoppingItem = createDiscountableShoppingItem();
		shoppingItem.setPrice(1, createPrice(BIG_D_30, BIG_D_30, CURRENCY, 1));
		shoppingItem.applyDiscount(BIG_D_10, null);

		final BigDecimal expectedDiscountableAmount = BIG_D_20.setScale(CURRENCY.getDefaultFractionDigits());

		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		allowingShoppingCartWithAnyPredicateWillReturn(shoppingCart, shoppingItem);

		createProductSkuFor(shoppingItem);

		container.setShoppingCart(shoppingCart);

		final BigDecimal actualDiscountableAmount = container.calculateSubtotalOfDiscountableItems();

		assertEquals("Expected the discount amount to be the sum of the total of all discountable items",
				expectedDiscountableAmount,
				actualDiscountableAmount);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void verifyCalculateSubtotalOfShippableItemsDelegatesToCalculator() throws Exception {
		final BigDecimal expectedShippableSubtotal = BigDecimal.ONE.setScale(CURRENCY.getDefaultFractionDigits());

		// Deliberately use a different subtotal for shippable items as for all items to make sure the right subtotal is requested
		final ShoppingCart shoppingCart = getShoppingCart(expectedShippableSubtotal, BigDecimal.TEN);

		container.setShoppingCart(shoppingCart);

		final BigDecimal actualShippableSubtotal = container.calculateSubtotalOfShippableItems();
		assertEquals(expectedShippableSubtotal, actualShippableSubtotal);
	}

	@Test
	public void verifyCalculateSubtotalOfDiscountableItemsAddsAllQuantityUnits() throws Exception {
		final ShoppingItem shoppingItem = createDiscountableShoppingItem();
		shoppingItem.setPrice(2, createPrice(BIG_D_10, BIG_D_10, CURRENCY, 1));

		final BigDecimal expectedDiscountableAmount = BIG_D_20.setScale(CURRENCY.getDefaultFractionDigits());

		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		allowingShoppingCartWithAnyPredicateWillReturn(shoppingCart, shoppingItem);

		createProductSkuFor(shoppingItem);

		container.setShoppingCart(shoppingCart);

		final BigDecimal actualDiscountableAmount = container.calculateSubtotalOfDiscountableItems();

		assertEquals("Expected the discount amount to be the sum of the total of all units of discountable items",
				expectedDiscountableAmount,
				actualDiscountableAmount);
	}

	@Test
	public void verifyCalculateSubtotalOfDiscountableItemsIgnoresNonDiscountableItems() throws Exception {
		final ShoppingItem shoppingItem1 = createNonDiscountableShoppingItem();
		shoppingItem1.setPrice(1, createPrice(BIG_D_10, BIG_D_10, CURRENCY, 1));

		final BigDecimal expectedDiscountableAmount = BigDecimal.ZERO.setScale(CURRENCY.getDefaultFractionDigits());

		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		allowingShoppingCartWithAnyPredicateWillReturn(shoppingCart, shoppingItem1);


		container.setShoppingCart(shoppingCart);

		final BigDecimal actualDiscountableAmount = container.calculateSubtotalOfDiscountableItems();

		assertEquals("Expected the discount amount to exclude the total of all non-discountable items",
				expectedDiscountableAmount,
				actualDiscountableAmount);
	}

	@Test
	public void verifyCalculateSubtotalOfDiscountableItemsIgnoresExcludedItems() throws Exception {
		final String skuGuid1 = UUID.randomUUID().toString();
		final String skuGuid2 = UUID.randomUUID().toString();

		final ShoppingItem shoppingItem1 = createDiscountableShoppingItem();
		shoppingItem1.setPrice(1, createPrice(BIG_D_10, BIG_D_10, CURRENCY, 1));
		shoppingItem1.setSkuGuid(skuGuid1);

		final ShoppingItem shoppingItem2 = createDiscountableShoppingItem();
		shoppingItem2.setPrice(1, createPrice(BIG_D_20, BIG_D_20, CURRENCY, 1));
		shoppingItem2.setSkuGuid(skuGuid2);

		final BigDecimal expectedDiscountableAmount = BIG_D_10.setScale(CURRENCY.getDefaultFractionDigits());

		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		final PromotionRuleExceptions promotionRuleExceptions = context.mock(PromotionRuleExceptions.class);

		final Product product = context.mock(Product.class);
		final ProductSku sku1 = createProductSkuFor(shoppingItem1, product);
		final ProductSku sku2 = createProductSkuFor(shoppingItem2, product);

		allowingShoppingCartWithAnyPredicateWillReturn(shoppingCart, shoppingItem1, shoppingItem2);

		context.checking(new Expectations() {
			{
				final Store store = context.mock(Store.class);
				final Catalog catalog = context.mock(Catalog.class);

				allowing(shoppingCart).getStore();
				will(returnValue(store));

				allowing(store).getCatalog();
				will(returnValue(catalog));

				allowing(product).getCategories(catalog);
				will(returnValue(Collections.emptySet()));

				allowing(promotionRuleExceptions).isSkuExcluded(sku2);
				will(returnValue(true));

				allowing(promotionRuleExceptions).isSkuExcluded(sku1);
				will(returnValue(false));
				allowing(promotionRuleExceptions).isProductExcluded(with(any(Product.class)));
				will(returnValue(false));
				allowing(promotionRuleExceptions).isCategoryExcluded(with(any(Category.class)));
				will(returnValue(false));

			}
		});

		container.setShoppingCart(shoppingCart);

		final BigDecimal actualDiscountableAmount = container.calculateSubtotalOfDiscountableItemsExcluding(promotionRuleExceptions);

		assertEquals("Expected the discount amount to exclude the total of all items marked as exceptions",
				expectedDiscountableAmount,
				actualDiscountableAmount);
	}

	@Test
	public void verifyGetPrePromotionUnitPriceExcludesCartPromos() throws Exception {
		// Given a shopping item with List Price $30, Computed Price $25
		final ShoppingItem shoppingItem = createDiscountableShoppingItem();
		final BigDecimal computedPrice = new BigDecimal("25.00");
		final Price price = createPrice(BIG_D_30, BIG_D_30, CURRENCY, 1);
		price.setComputedPriceIfLower(Money.valueOf(computedPrice, CURRENCY));
		shoppingItem.setPrice(1, price);

		// And the shopping item has a Cart Promo Discount of $10
		shoppingItem.applyDiscount(BIG_D_10, null);

		// When I retrieve the pre-promotion unit price
		final BigDecimal prePromotionUnitPriceAmount = container.getPrePromotionUnitPriceAmount(shoppingItem);

		// Then the pre-promotion unit price is $25
		assertEquals("Pre-promotion unit price amount should contain catalogue promos but exclude cart promos",
				computedPrice, prePromotionUnitPriceAmount);
	}

	@Test
	public void verifyGetPrePromotionUnitPriceDoesNotMultiplyByQuantity() throws Exception {
		// Given a shopping item with List Price $30, Computed Price $25
		final ShoppingItem shoppingItem = createDiscountableShoppingItem();
		final BigDecimal computedPrice = new BigDecimal("25.00");
		final Price price = createPrice(BIG_D_30, BIG_D_30, CURRENCY, 1);
		price.setComputedPriceIfLower(Money.valueOf(computedPrice, CURRENCY));
		shoppingItem.setPrice(2, price);

		// And the shopping item has a quantity of 2
		shoppingItem.applyDiscount(BIG_D_10, null);

		// When I retrieve the pre-promotion unit price
		final BigDecimal prePromotionUnitPriceAmount = container.getPrePromotionUnitPriceAmount(shoppingItem);

		// Then the pre-promotion unit price is $25
		assertEquals("Pre-promotion unit price amount should contain catalogue promos but exclude cart promos",
				computedPrice, prePromotionUnitPriceAmount);
	}

	@Test
	public void verifyCartItemEligibleForPromotionFalseWhenNoSuchSku() throws Exception {
		final ShoppingItem discountableShoppingItem = createDiscountableShoppingItem();
		discountableShoppingItem.setSkuGuid(SKU_GUID);

		final PromotionRuleExceptions exceptions = context.mock(PromotionRuleExceptions.class);
		context.checking(new Expectations() {
			{
				allowing(getProductSkuLookup()).findByGuid(SKU_GUID);
				will(returnValue(null));
			}
		});

		final boolean eligible = container.cartItemEligibleForPromotion(discountableShoppingItem, exceptions);

		assertFalse("Cart item should not be eligible for promotions when no corresponding SKU is found", eligible);
	}

	@Test
	public void verifyCartItemEligibleForPromotionFalseWhenSkuExcluded() throws Exception {
		final ShoppingItem discountableShoppingItem = createDiscountableShoppingItem();
		discountableShoppingItem.setSkuGuid(SKU_GUID);

		final PromotionRuleExceptions exceptions = context.mock(PromotionRuleExceptions.class);
		final ProductSku sku = context.mock(ProductSku.class);
		context.checking(new Expectations() {
			{
				oneOf(exceptions).isSkuExcluded(sku);
				will(returnValue(true));
			}
		});
		mockProductSkuLookupByGuid(SKU_GUID, sku);

		final boolean eligible = container.cartItemEligibleForPromotion(discountableShoppingItem, exceptions);

		assertFalse("Cart item should not be eligible for promotions when its corresponding SKU has been excluded", eligible);
	}

	@Test
	public void verifyCartItemEligibleForPromotionFalseWhenProductExcluded() throws Exception {
		final ShoppingItem discountableShoppingItem = createDiscountableShoppingItem();

		final Product product = context.mock(Product.class);
		final ProductSku sku = createProductSkuFor(discountableShoppingItem, product);

		final PromotionRuleExceptions exceptions = context.mock(PromotionRuleExceptions.class);
		context.checking(new Expectations() {
			{
				oneOf(exceptions).isSkuExcluded(sku);
				will(returnValue(false));

				oneOf(exceptions).isProductExcluded(product);
				will(returnValue(true));
			}
		});

		final boolean eligible = container.cartItemEligibleForPromotion(discountableShoppingItem, exceptions);

		assertFalse("Cart item should not be eligible for promotions when its corresponding Product has been excluded", eligible);
	}

	@Test
	public void verifyCartItemEligibleForPromotionFalseWhenCategoryExcluded() throws Exception {
		final ShoppingItem discountableShoppingItem = createDiscountableShoppingItem();

		final Product product = context.mock(Product.class);
		final ProductSku sku = createProductSkuFor(discountableShoppingItem, product);

		final ShoppingCart cart = context.mock(ShoppingCart.class);

		final PromotionRuleExceptions exceptions = context.mock(PromotionRuleExceptions.class);
		context.checking(new Expectations() {
			{
				final Store store = context.mock(Store.class);
				final Catalog catalog = context.mock(Catalog.class);
				final Category category = context.mock(Category.class);
				final Set<Category> categories = ImmutableSet.of(category);

				allowing(cart).getStore();
				will(returnValue(store));

				allowing(store).getCatalog();
				will(returnValue(catalog));

				allowing(product).getCategories(catalog);
				will(returnValue(categories));

				oneOf(exceptions).isSkuExcluded(sku);
				will(returnValue(false));

				oneOf(exceptions).isProductExcluded(product);
				will(returnValue(false));

				oneOf(exceptions).isCategoryExcluded(category);
				will(returnValue(true));
			}
		});

		container.setShoppingCart(cart);

		final boolean eligible = container.cartItemEligibleForPromotion(discountableShoppingItem, exceptions);

		assertFalse("Cart item should not be eligible for promotions when its corresponding Category has been excluded", eligible);
	}

	@Test
	public void verifyCartItemEligibleForPromotionFalseWhenCartItemNotDiscountable() throws Exception {
		final ShoppingItem discountableShoppingItem = createNonDiscountableShoppingItem();
		createProductSkuFor(discountableShoppingItem);

		final boolean eligible = container.cartItemEligibleForPromotion(discountableShoppingItem, context.mock(PromotionRuleExceptions.class));

		assertFalse("Cart item should not be eligible for promotions when it is non-discountable", eligible);
	}

	@Test
	public void verifyGetLimitedUsageCodesDelegatesToPromotionItemContainer() throws Exception {
		final String ruleCode = "ID";
		final long ruleId = 1L;

		final Map<String, Long> expected = ImmutableMap.of(ruleCode, ruleId);

		final ShoppingCartImpl cart = new ShoppingCartImpl();
		cart.getMutablePromotionRecordContainer().addLimitedUsagePromotionRuleCode(ruleCode, ruleId);

		container.setShoppingCart(cart);

		final Map<String, Long> limitedUsagePromotionRuleCodes = container.getLimitedUsagePromotionRuleCodes();

		assertEquals(expected, limitedUsagePromotionRuleCodes);
	}

	@Test
	public void verifyPrePromotionPriceForShippingOptionIsObtainedFromShoppingCart() throws Exception {
		final String shippingOptionCode = "SHIP001";
		final ShippingOption shippingOption = context.mock(ShippingOption.class);

		context.checking(new Expectations() {
			{
				allowing(shippingOption).getCode();
				will(returnValue(shippingOptionCode));
			}
		});

		final Money shippingAmount = Money.valueOf(BigDecimal.TEN, CURRENCY);

		final ShoppingCartImpl cart = new ShoppingCartImpl();
		cart.setShippingListPrice(shippingOptionCode, shippingAmount);

		container.setShoppingCart(cart);

		final BigDecimal actualShippingAmount = container.getPrePromotionPriceAmount(shippingOption);

		assertEquals("Expected shipping amount to be retrieved from the cart's store of shipping list prices",
				shippingAmount.getAmount(), actualShippingAmount);
	}

	@Test
	public void verifyApplyShippingDiscountSetsToShoppingCart() throws Exception {
		final String expectedShippingOptionCode = "SHIP001";
		final ShippingOption shippingOption = context.mock(ShippingOption.class);
		final BigDecimal discount = BIG_D_10;

		context.checking(new Expectations() {
			{
				allowing(shippingOption).getCode();
				will(returnValue(expectedShippingOptionCode));
			}
		});

		final boolean[] delegateMethodInvoked = new boolean[1];
		final ShoppingCart cart = new ShoppingCartImpl() {
			private static final long serialVersionUID = -8366031895415261585L;

			@Override
			public void setShippingDiscountIfLower(
					final String actualShippingOptionCode,
					final long ruleId,
					final long actionId,
					final Money discountAmount) {
				assertSame(expectedShippingOptionCode, actualShippingOptionCode);
				assertEquals(RULE_UID, ruleId);
				assertEquals(ACTION_UID, actionId);
				assertEquals(Money.valueOf(discount, CURRENCY), discountAmount);
				delegateMethodInvoked[0] = true;
			}
		};
		container.setShoppingCart(cart);

		container.applyShippingOptionDiscount(shippingOption, RULE_UID, ACTION_UID, discount);

		assertTrue("Method ShoppingCart.setShippingDiscountIfLower never invoked", delegateMethodInvoked[0]);
	}

	@Test
	public void verifyGetLowestPricedShoppingItemForSkuReturnsEmptyOptionalWhenNoMatchingItemExists() {
		final ShoppingCart shoppingCart = new ShoppingCartImpl();
		final String productSkuGuid = UUID.randomUUID().toString();

		final ProductSkuLookup productSkuLookup = mock(ProductSkuLookup.class);
		final ProductSku productSku = createMockProductSku(productSkuGuid, "NONMATCHINGSKU001");

		final ShoppingItem nonMatchingShoppingItem = createShoppingItemForSku(productSkuGuid);

		when(productSkuLookup.findByGuids(Collections.singleton(productSkuGuid))).thenReturn(Collections.singletonList(productSku));

		mockProductSkuLookupByGuid(productSkuGuid, productSku);

		shoppingCart.addShoppingCartItem(nonMatchingShoppingItem);

		container.setShoppingCart(shoppingCart);

		assertThat(container.getLowestPricedShoppingItemForSku(SKU_CODE))
				.isEmpty();
	}

	@Test
	public void verifyGetLowestPricedShoppingItemForSkuReturnsShoppingItemWhenOneExists() {
		final ShoppingCart shoppingCart = new ShoppingCartImpl();

		final String nonMatchingSkuCode = "NONMATCHSKU001";
		final String matchingSkuCode = "MATCHSKU001";

		final String nonMatchingProductSkuGuid = UUID.randomUUID().toString();
		final String matchingProductSkuGuid = UUID.randomUUID().toString();

		final ProductSku matchingProductSku = createMockProductSku(matchingProductSkuGuid, matchingSkuCode);
		final ProductSku nonMatchingProductSku = createMockProductSku(nonMatchingProductSkuGuid, nonMatchingSkuCode);

		final ShoppingItem nonMatchingShoppingItem = createShoppingItemForSku(nonMatchingProductSkuGuid);
		final ShoppingItem matchingShoppingItem1 = createShoppingItemForSku(matchingProductSkuGuid);
		final ShoppingItem matchingShoppingItem2 = createShoppingItemForSku(matchingProductSkuGuid);

		final ProductSkuLookup productSkuLookup = mock(ProductSkuLookup.class);
		when(productSkuLookup.findByGuids(ImmutableList.of(matchingProductSkuGuid, nonMatchingProductSkuGuid)))
				.thenReturn(ImmutableList.of(matchingProductSku, nonMatchingProductSku));

		shoppingCart.addShoppingCartItem(nonMatchingShoppingItem);
		shoppingCart.addShoppingCartItem(matchingShoppingItem1);
		shoppingCart.addShoppingCartItem(matchingShoppingItem2);

		container = new ShoppingCartDiscountItemContainerImpl() {
			@Override
			public List<ShoppingItem> getItemsLowestToHighestPrice() {
				return ImmutableList.of(matchingShoppingItem2, nonMatchingShoppingItem, matchingShoppingItem1);
			}
		};

		container.setProductSkuLookup(getProductSkuLookup());
		container.setCurrency(CURRENCY);
		container.setShoppingItemsSubtotalCalculator(getShoppingItemSubtotalCalculator());
		container.setShippableItemPredicate(new ShippableItemPredicate<>(getProductSkuLookup()));
		container.setShoppingCart(shoppingCart);

		assertThat(container.getLowestPricedShoppingItemForSku(matchingSkuCode))
				.contains(matchingShoppingItem2);
	}

	/**
	 * Creates a new <code>Price</code> using the passed in data.
	 *
	 * @param listPrice listPrice
	 * @param salePrice salePrice
	 * @param currency currency
	 * @param quantity quantity
	 * @return the created Price
	 */
	private Price createPrice(final BigDecimal listPrice, final BigDecimal salePrice, final Currency currency, final int quantity) {
		final Price price = new PriceImpl();
		price.setCurrency(currency);
		price.setListPrice(Money.valueOf(listPrice, currency), quantity);
		price.setSalePrice(Money.valueOf(salePrice, currency), quantity);

		return price;
	}

	private ShoppingItem createDiscountableShoppingItem() {
		return createShoppingItemWithDiscountability(true);
	}

	private ShoppingItem createNonDiscountableShoppingItem() {
		return createShoppingItemWithDiscountability(false);
	}

	private ShoppingItem createShoppingItemWithDiscountability(final boolean discountability) {
		return new ShoppingItemImpl() {
			private static final long serialVersionUID = 4012495566232629310L;

			@Override
			public boolean isDiscountable(final ProductSkuLookup productSkuLookup) {
				return discountability;
			}
		};
	}

	private ProductSku createProductSkuFor(final ShoppingItem shoppingItem) {
		final String skuGuid = UUID.randomUUID().toString();

		final ProductSku productSku = context.mock(ProductSku.class, "SKU_" + skuGuid);

		shoppingItem.setSkuGuid(skuGuid);

		context.checking(new Expectations() {
			{
				allowing(productSku).getGuid();
				will(returnValue(skuGuid));
			}
		});

		mockProductSkuLookupByGuid(skuGuid, productSku);

		return productSku;
	}

	private ProductSku createProductSkuFor(final ShoppingItem shoppingItem, final Product product) {
		final ProductSku productSku = createProductSkuFor(shoppingItem);

		context.checking(new Expectations() {
			{
				allowing(productSku).getProduct();
				will(returnValue(product));
			}
		});

		return productSku;
	}

	@SuppressWarnings("unchecked")
	private void allowingShoppingCartWithAnyPredicateWillReturn(final ShoppingCart shoppingCart, final ShoppingItem... shoppingItems) {
		context.checking(new Expectations() {
			{
				allowing(shoppingCart).getShoppingItems(with(any(Predicate.class)));
				will(returnValue(Arrays.asList(shoppingItems)));
			}
		});
	}

	private ProductSku createMockProductSku(final String skuGuid, final String skuCode) {
		final ProductSku productSku = mock(ProductSku.class, "Product SKU " + skuCode);
		final Product product = mock(Product.class, "Product " + UUID.randomUUID().toString());
		final ProductType productType = mock(ProductType.class, "Product Type" + UUID.randomUUID().toString());

		mockProductSkuLookupByGuid(skuGuid, productSku);

		when(productSku.getGuid()).thenReturn(skuGuid);
		when(productSku.getSkuCode()).thenReturn(skuCode);
		when(productSku.getProduct()).thenReturn(product);

		when(product.getProductType()).thenReturn(productType);

		when(productType.isExcludedFromDiscount()).thenReturn(false);

		return productSku;
	}

	private ShoppingItem createShoppingItemForSku(final String skuGuid) {
		final ShoppingItem shoppingItem = new ShoppingItemImpl();
		shoppingItem.setSkuGuid(skuGuid);
		shoppingItem.setBundleConstituent(false);
		shoppingItem.setGuid(UUID.randomUUID().toString());

		return shoppingItem;
	}

}
