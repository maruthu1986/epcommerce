/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.tax.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.shoppingcart.PriceCalculator;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.service.catalog.ProductSkuLookup;

/**
 * Unit tests for discount apportioning code currently located in DefaultTaxCalculationServiceImpl.
 */
public class DiscountApportioningCalculatorTest {
	
	private static final String SKU_CODE = "SKU_CODE";

	private static final BigDecimal THIRD_OF_TEN = new BigDecimal("3.33");

	private static final BigDecimal FIVE = new BigDecimal("5.00");

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private static final String CAD = "CAD";
	
	private static final Currency CA_CURRENCY = Currency.getInstance(CAD);

	private static final String FOR_LINE_ITEM = "For lineItem";

	private static final BigDecimal ONE_HUNDRED = new BigDecimal("100.00");
	
	private static final BigDecimal FOURTY = new BigDecimal("40.00");
	
	private static final BigDecimal THIRTY = new BigDecimal("30.00");

	private static final BigDecimal TWENTY = new BigDecimal("20.00");

	private static final BigDecimal TEN = new BigDecimal("10.00");
	
	private static final BigDecimal ZERO_00 = BigDecimal.ZERO.setScale(2);

	private static final long UID1 = 1L;

	private static final BigDecimal TWO_CENTS = new BigDecimal(".02");

	private static final BigDecimal ONE_CENT = new BigDecimal(".01");

	private static final BigDecimal NINETY_NINE_CENTS = new BigDecimal(".99");

	private Map<String, BigDecimal> expectedDiscountMap;
	@Mock private ProductSkuLookup productSkuLookup;
	private DiscountApportioningCalculatorImpl discountCalc;

	/**
	 * Initial setup of common variables.
	 */
	@Before
	public void setUp() {
		discountCalc = new DiscountApportioningCalculatorImpl();
		discountCalc.setProductSkuLookup(productSkuLookup);
		expectedDiscountMap = new HashMap<>();
	}
	
	/**
	 * Apportioning of discounts to a single line item. The whole discount should be applied to the single item.
	 * 
	 * LineItems 		= $20
	 * Total Discount	= $10
	 * Expected			= $10
	 * 
	 */
	@Test
	public void testDiscountApportioningForSingleItem() {
		final Money discount = Money.valueOf(TEN, CA_CURRENCY);
		Map<ShoppingItem, ShoppingItemPricingSnapshot> lineItems = constructLineItemCollection(CA_CURRENCY, TWENTY);

		populateExpectedDiscountMap(TEN);
		final Map<String, BigDecimal> discountMap = discountCalc.apportionDiscountToShoppingItems(discount, lineItems);
		assertEquals(expectedDiscountMap.get(String.valueOf(UID1)), discountMap.get(String.valueOf(UID1)));
	}
	
	/**
	 * Apportion a discount proportionally across multiple items, simple case which doesn't require any elaborate
	 * rounding.
	 * LineItems 		= ($20, $30, $40, $10)
	 * Total Discount	= $10
	 * Expected			= ($2.00, $3.00, $4.00, $1.00)
	 */
	@Test
	public void testDiscountApportioningMultipleItems() {
		final Money discount = Money.valueOf(TEN, CA_CURRENCY);
		Map<ShoppingItem, ShoppingItemPricingSnapshot> lineItemPricingSnapshotMap =
				constructLineItemCollection(CA_CURRENCY, TWENTY, THIRTY, FOURTY, TEN);
		
		populateExpectedDiscountMap(new BigDecimal("2.00"), new BigDecimal("3.00"), new BigDecimal("4.00"), new BigDecimal("1.00"));
		
		assertDiscountsEqualExpected(discount, lineItemPricingSnapshotMap);
	}

	/**
	 * Attempt to apportion a zero discount. All discounts should be set to zero.
	 * LineItems 		= ($20, $30, $40, $10)
	 * Total Discount	= $0
	 * Expected			= ($0.00, $0.00, $0.00, $0.00)
	 */
	@Test
	public void testDiscountApportioningZeroDiscount() {
		final Money discount = Money.valueOf(BigDecimal.ZERO, CA_CURRENCY);
		Map<ShoppingItem, ShoppingItemPricingSnapshot> lineItemPricingSnapshotMap =
				constructLineItemCollection(CA_CURRENCY, TWENTY, THIRTY, FOURTY, TEN);
		
		populateExpectedDiscountMap(ZERO_00, ZERO_00, ZERO_00, ZERO_00); 
		
		assertDiscountsEqualExpected(discount, lineItemPricingSnapshotMap);
	}
	
	/**
	 * Apply a discount that requires picking an item to add an extra cent to due to rounding,
	 * but have the item which would normally receive the extra cent have price 0, so it goes 
	 * to the next available non-free item. Because we shouldn't make a free item non-free by
	 * rounding!
	 * LineItems 		= ($0, $10, $10, $10)
	 * Total Discount	= $10
	 * Expected			= ($0.00, $3.33, $3.33, $3.34)
	 * 
	 * All $10 items should get equals portion so (1/3)*$10=3.33 which leaves 1 cent leftover, which gets applied to the first non-zero item.
	 */
	@Test
	public void testDiscountApportioningRoundingLastItemFree() {
		final Money discount = Money.valueOf(TEN, CA_CURRENCY);
		Map<ShoppingItem, ShoppingItemPricingSnapshot> lineItemPricingSnapshotMap =
				constructLineItemCollection(CA_CURRENCY, BigDecimal.ZERO, TEN, TEN, TEN);
		
		populateExpectedDiscountMap(ZERO_00, THIRD_OF_TEN, THIRD_OF_TEN, new BigDecimal("3.34"));
		
		assertDiscountsEqualExpected(discount, lineItemPricingSnapshotMap);
	}
	
	/**
	 * Apportion a discount between multiple items, where the amounts cause a rounding
	 * error of .01
	 * All $10 items should get equals portion so (1/3)*$10=3.33 which leaves 1 cent leftover, which gets applied to the first non-zero item.
	 */
	@Test
	public void testDiscountApportioningMultipleItemsRoundingIssue() {
		final Money discount = Money.valueOf(TEN, CA_CURRENCY);
		Map<ShoppingItem, ShoppingItemPricingSnapshot> lineItemPricingSnapshotMap =
				constructLineItemCollection(CA_CURRENCY, TEN, TEN, TEN);
		
		populateExpectedDiscountMap(THIRD_OF_TEN, THIRD_OF_TEN, new BigDecimal("3.34"));
		
		assertDiscountsEqualExpected(discount, lineItemPricingSnapshotMap);
	}

	/**
	 * Make sure that if the discount is same as the total, the discounts are all the same as the item prices.
	 * 
	 * LineItems 		= ($20, $30, $40, $10)
	 * Total Discount	= $100
	 * Expected			= ($20, $30, $40, $10)
	 */
	@Test
	public void testDiscountSameAsCartTotal() {
		final Money discount = Money.valueOf(ONE_HUNDRED, CA_CURRENCY);
		Map<ShoppingItem, ShoppingItemPricingSnapshot> lineItemPricingSnapshotMap =
				constructLineItemCollection(CA_CURRENCY, TWENTY, THIRTY, FOURTY, TEN);

		populateExpectedDiscountMap(TWENTY, THIRTY, FOURTY, TEN);
		
		assertDiscountsEqualExpected(discount, lineItemPricingSnapshotMap);
	}
	
	/**
	 * Test a discount such that due to rounding the item discount sum will end up larger than the discount.
	 *
	 * Items should get same amount of discount, but the rounding will make the discount greater than the total discount, so one
	 * must receive one less cent.
	 */
	@Test
	public void testDiscountRoundingUpOverTotal() {
		final Money discount = Money.valueOf("9.99", CA_CURRENCY);
		Map<ShoppingItem, ShoppingItemPricingSnapshot> lineItemPricingSnapshotMap = constructLineItemCollection(CA_CURRENCY, TEN, TEN);
		
		populateExpectedDiscountMap(FIVE, new BigDecimal("4.99"));
		
		assertDiscountsEqualExpected(discount, lineItemPricingSnapshotMap);
	}
	
	
	/**
	 * Expects an exception when the discount apportioning method is given a discount larger than the
	 * total of the items, since that behaviour should never occur.
	 */
	@Test (expected = IllegalArgumentException.class)
	public void testDiscountLargerThanCartTotal() { 
		
		final Money discount = Money.valueOf("110.00", CA_CURRENCY);
		Map<ShoppingItem, ShoppingItemPricingSnapshot> lineItemPricingSnapshotMap =
				constructLineItemCollection(CA_CURRENCY, TWENTY, THIRTY, FOURTY, TEN);
		
		discountCalc.apportionDiscountToShoppingItems(discount, lineItemPricingSnapshotMap);
		fail("Expected EpNonConsistentDomainFieldException");
	}

	/**
	 * Test rounding where we have multiple items rounded up, and the first eligible item for taking the 
	 * error is smaller than the total of the rounding error. The small item should get it's discount set
	 * to zero, and the following item in the ordering should catch the rest of the rounding discount error.
	 */
	@Test
	public void testDiscountRoundingUpSmallLastItem() {
		final Money discount = Money.valueOf("59.95", CA_CURRENCY);
		Map<ShoppingItem, ShoppingItemPricingSnapshot> lineItemPricingSnapshotMap = constructLineItemCollection(CA_CURRENCY, 
				new BigDecimal("0.02"), TEN, TEN, TEN, TEN, TEN, TEN, TEN, TEN, TEN, TEN, TEN, TEN);
		
		// 9th element has Sku "9". This is higher than any other sku: 
		// "1", "8", "11", etc.
		// That's why 9th element gets the rounding error.
		populateExpectedDiscountMap(new BigDecimal("0.01"),  
				FIVE, FIVE, FIVE, FIVE, FIVE, FIVE, FIVE, new BigDecimal("4.94"), FIVE, FIVE, FIVE, FIVE);

		assertDiscountsEqualExpected(discount, lineItemPricingSnapshotMap);
	}	
	
	/** */
	@Test
	public void testDuplicatedSkus() {
		ShoppingItem item1 = mockLeafItem(1, "1", SKU_CODE);
		ShoppingItem item2 = mockLeafItem(1, "2", SKU_CODE);

		Money discount = Money.valueOf(TWO_CENTS, CA_CURRENCY);
		Map<ShoppingItem, ShoppingItemPricingSnapshot> lineItemPricingSnapshotMap = ImmutableMap.of(
				item1, mockLeafItemPricingSnapshot(discount),
				item2, mockLeafItemPricingSnapshot(discount)
		);


		final Map<String, BigDecimal> discountMap = discountCalc.apportionDiscountToShoppingItems(discount, lineItemPricingSnapshotMap);
		
		assertEquals(ONE_CENT, discountMap.get("1"));
		assertEquals(ONE_CENT, discountMap.get("2"));
	}
	
	/** */
	@Test
	public void testDiscountableShoppingItems() {
		ShoppingItem item1 = mockLeafItem(1, "1", SKU_CODE);
		ShoppingItem item2 = mockLeafItem(1, "2", SKU_CODE);

		Collection<ShoppingItem> lineItems = new ArrayList<>();

		lineItems.add(item1);
		lineItems.add(item2);
		Collection<ShoppingItem> items = discountCalc.getDiscountableShoppingItems(lineItems);
		assertEquals(2, items.size());
		assertTrue(items.contains(item1));
		assertTrue(items.contains(item2));
	}

	/** */
	@Test
	public void testNoDiscountableShoppingItems() {
		Collection<ShoppingItem> lineItems = new ArrayList<>();
		
		Collection<ShoppingItem> items = discountCalc.getDiscountableShoppingItems(lineItems);
		assertTrue(items.isEmpty());
	}
	
	/**
	 * Tests item selection from bundle.
	 * 
	 * The bundle has a single constituent.
	 */
	@Test
	public void testBundleDiscountableShoppingItems() {
		ShoppingItem constituent = mockLeafItem(1, "2", "SKU_CODE");
		
		final ArrayList<ShoppingItem> constituents = new ArrayList<>();
		constituents.add(0, constituent);
		
		ShoppingItem bundle = mockBundleItem(1, "1", "SKU_CODE", constituents);
		Collection<ShoppingItem> lineItems = new ArrayList<>();
		lineItems.add(bundle);
		
		Collection<ShoppingItem> items = discountCalc.getDiscountableShoppingItems(lineItems);
		assertEquals(1, items.size());
		assertTrue(items.contains(constituent));
	}
	
	/** */
	@Test
	public void testAdjustmentForEqualPortionAndProportion() {
		BigDecimal adjustment = discountCalc.calculateErrorAdjustment(BigDecimal.ONE, BigDecimal.ONE, ONE_CENT);
		assertEquals(0, BigDecimal.ZERO.compareTo(adjustment));
	}
	
	/** */
	@Test
	public void testAdjustmentLimitedByPortion() {
		BigDecimal adjustment = discountCalc.calculateErrorAdjustment(BigDecimal.ONE, ONE_CENT, BigDecimal.ONE);
		assertEquals(0, NINETY_NINE_CENTS.compareTo(adjustment));
	}

	//Begin helper methods

	/**
	 * Helper method which verifies that the discounts for the line items match the expected. Uses the lineitem product sku code as 
	 * key.
	 */
	private void assertDiscountsEqualExpected(final Money discount, final Map<ShoppingItem, ShoppingItemPricingSnapshot> lineItemPricingSnapshotMap) {
		final Map<String, BigDecimal> discountMap = discountCalc.apportionDiscountToShoppingItems(discount, lineItemPricingSnapshotMap);
		for (final Map.Entry<String, BigDecimal> entry : expectedDiscountMap.entrySet()) {
			assertEquals(FOR_LINE_ITEM + entry.getKey(), entry.getValue(), discountMap.get(entry.getKey()));
		}
	}
	
	private void populateExpectedDiscountMap(final BigDecimal ... amounts) {
		for (int i = 0; i < amounts.length; ++i) {
			expectedDiscountMap.put(String.valueOf(i + 1), amounts[i]);
		}
	}


	/**
	 * Construct a collection of lineItem mock objects, with the currency and amount specified, and uidpk equal to
	 * position in the amounts field counting from 1. 
	 */
	private Map<ShoppingItem, ShoppingItemPricingSnapshot> constructLineItemCollection(final Currency currency, final BigDecimal ... amounts) {
		final Map<ShoppingItem, ShoppingItemPricingSnapshot> lineItemPricingSnapshotMap = Maps.newHashMapWithExpectedSize(amounts.length);
		for (int i = 0; i < amounts.length; ++i) {
			BigDecimal amount = amounts[i];
			final ShoppingItem shoppingItem = mockLeafItem(i + 1, String.valueOf(i + 1), String.valueOf(i + 1));
			final ShoppingItemPricingSnapshot shoppingItemPricingSnapshot = mockLeafItemPricingSnapshot(Money.valueOf(amount, currency));
			lineItemPricingSnapshotMap.put(shoppingItem, shoppingItemPricingSnapshot);
		}
		return lineItemPricingSnapshotMap;
	}

	/**
	 * Mock up a line item to return the specified lineItemAmount when getAmountMoney() is called, 
	 * and the specified uid when getUidPk() is called.
	 * @param lineItemGuid TODO
	 * @param skuCode TODO
	 */
	private ShoppingItem mockLeafItem(final long uid, final String lineItemGuid, final String skuCode) {
		final ShoppingItem lineItemMock = mockAbstractItem(uid,
				lineItemGuid, skuCode, false);
		context.checking(new Expectations() {
			{
				allowing(lineItemMock).hasBundleItems(productSkuLookup);
				will(returnValue(false));
				allowing(lineItemMock).isDiscountable(productSkuLookup);
				will(returnValue(true));
				allowing(lineItemMock).hasPrice();
				will(returnValue(true));
			}
		});
		return lineItemMock;
	}

	private ShoppingItemPricingSnapshot mockLeafItemPricingSnapshot(final Money lineItemAmount) {
		final ShoppingItemPricingSnapshot pricingSnapshot =
				context.mock(ShoppingItemPricingSnapshot.class, "Item Pricing Snapshot " + UUID.randomUUID());

		context.checking(new Expectations() {
			{
				final PriceCalculator priceCalc = context.mock(PriceCalculator.class, "Price Calculator " + UUID.randomUUID());
				allowing(pricingSnapshot).getPriceCalc();
				will(returnValue(priceCalc));
				allowing(priceCalc).withCartDiscounts();
				will(returnValue(priceCalc));
				allowing(priceCalc).getAmount();
				will(returnValue(lineItemAmount.getAmount()));

				allowing(pricingSnapshot).getListUnitPrice();
				will(returnValue(lineItemAmount));

			}
		});

		return pricingSnapshot;
	}
	
	private ShoppingItem mockBundleItem(final long uid, final String lineItemGuid, final String skuCode,
										final List<ShoppingItem> constituents) {
		final ShoppingItem lineItemMock = mockAbstractItem(uid,
				lineItemGuid, skuCode, true);
		
		context.checking(new Expectations() {
			{
				allowing(lineItemMock).hasBundleItems(productSkuLookup);
				will(returnValue(true));

				allowing(lineItemMock).getBundleItems(productSkuLookup);
				will(returnValue(constituents));
				
				allowing(lineItemMock).isDiscountable(productSkuLookup);
				will(returnValue(true));

				allowing(lineItemMock).hasPrice();
				will(returnValue(true));
			}
		});

		return lineItemMock;
	}

	private ShoppingItem mockAbstractItem(final long uid, final String lineItemGuid, final String skuCode, final boolean isBundle) {
		final ShoppingItem lineItemMock = context.mock(ShoppingItem.class, "lineItem" + lineItemGuid);
		final ProductSku sku = new ProductSkuImpl();

		sku.initialize();
		sku.setSkuCode(skuCode);
		context.checking(new Expectations() {
			{
				atLeast(0).of(lineItemMock).getUidPk();
				will(returnValue(uid));
				
				atLeast(0).of(lineItemMock).getGuid();
				will(returnValue(lineItemGuid));
				
				atLeast(0).of(lineItemMock).getSkuGuid();
				will(returnValue(sku.getGuid()));
				
				allowing(lineItemMock).isBundle(productSkuLookup);
				will(returnValue(isBundle));

				allowing(productSkuLookup).findByGuid(sku.getGuid());
				will(returnValue(sku));
			}
		});
		return lineItemMock;
	}
	
}
