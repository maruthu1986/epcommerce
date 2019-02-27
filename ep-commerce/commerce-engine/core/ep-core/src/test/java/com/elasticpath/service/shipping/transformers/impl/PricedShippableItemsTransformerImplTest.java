/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers.impl;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.service.shipping.ShippableItemPricing;
import com.elasticpath.service.shipping.ShippableItemsPricing;
import com.elasticpath.service.shipping.transformers.PricedShippableItemTransformer;
import com.elasticpath.service.tax.DiscountApportioningCalculator;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItem;

/**
 * Unit test for {@link PricedShippableItemsTransformerImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PricedShippableItemsTransformerImplTest {
	private static final Currency CURRENCY = Currency.getInstance("CAD");

	private static final String SHOPPING_ITEM_1_GUID = "shoppingItemGuid_1";
	private static final int SHOPPING_ITEM_1_QUANTITY = 5;
	private static final Money SHOPPING_ITEM_1_SUBTOTAL_DISCOUNT = Money.valueOf(BigDecimal.ONE, CURRENCY);


	private static final Money NON_ZERO_SUBTOTAL_DISCOUNT = Money.valueOf(BigDecimal.TEN, CURRENCY);

	@Mock
	private ShoppingItem shoppingItem1;

	@Mock
	private ShoppingItem shoppingItem2;

	@Mock
	private ShoppingItemPricingSnapshot shoppingItemPricingSnapshot1;

	@Mock
	private ShoppingItemPricingSnapshot shoppingItemPricingSnapshot2;

	@Mock
	private ShippableItemsPricing shippableItemsPricing;

	@Mock
	private ShippableItemPricing shippableItemPricing1;

	@Mock
	private ShippableItemPricing shippableItemPricing2;

	@Mock
	private Function<ShoppingItem, ShoppingItemPricingSnapshot> itemPricingFunction;

	@Mock
	private Predicate<ShoppingItem> shippableItemPredicate;

	@Mock
	private PricedShippableItem pricedShippableItem1;

	@Mock
	private PricedShippableItem pricedShippableItem2;

	@Mock
	private PricedShippableItemTransformer pricedShippableItemTransformer;

	@Mock
	private DiscountApportioningCalculator discountApportioningCalculator;

	private List<ShoppingItem> allShoppingItems;

	@InjectMocks
	private PricedShippableItemsTransformerImpl objectUnderTest;

	@Before
	public void setUp() {
		allShoppingItems = asList(shoppingItem1, shoppingItem2);

		when(shoppingItem1.getGuid()).thenReturn(SHOPPING_ITEM_1_GUID);
		when(shoppingItem1.getQuantity()).thenReturn(SHOPPING_ITEM_1_QUANTITY);

		when(shippableItemsPricing.getCurrency()).thenReturn(CURRENCY);

		when(shippableItemsPricing.getShoppingItemPricingFunction()).thenReturn(itemPricingFunction);
		when(itemPricingFunction.apply(shoppingItem1)).thenReturn(shoppingItemPricingSnapshot1);
		when(itemPricingFunction.apply(shoppingItem2)).thenReturn(shoppingItemPricingSnapshot2);
	}

	@Test
	public void verifyApplyApportionsOverAllShoppingItemsButTransformsJustFilteredItems() {
		// Given the input ShippableItemsPricing has a Predicate which filters out one of the two ShoppingItems in the cart
		when(shippableItemsPricing.getShippableItemPredicate()).thenReturn(Optional.of(shippableItemPredicate));
		when(shippableItemPredicate.test(shoppingItem1)).thenReturn(false);
		when(shippableItemPredicate.test(shoppingItem2)).thenReturn(true);

		// And we fake out a couple of methods that are tested in a separate tests, so we don't want to double test
		final PricedShippableItemsTransformerImpl objectUnderTestWithFake = spy(objectUnderTest);

		// Fake 1: apportionSubtotalDiscount()
		final String arbitraryShoppingItemGuid = "anythingSinceWeAreTellingApportionSubtotalDiscountToReturnThisMapAndJustVerifyingWeAreReturningIt";
		final BigDecimal arbitraryDiscount = BigDecimal.ONE;

		final Map<String, BigDecimal> apportionedSubtotalMap = ImmutableMap.of(arbitraryShoppingItemGuid, arbitraryDiscount);

		doReturn(apportionedSubtotalMap).when(objectUnderTestWithFake).apportionSubtotalDiscount(allShoppingItems, shippableItemsPricing);

		doReturn(shippableItemPricing2).when(objectUnderTestWithFake)
				.createShippableItemPricing(shoppingItem2, shippableItemsPricing, apportionedSubtotalMap, CURRENCY);

		// And the delegated individual PricedShippableItem transformer returns the expected PricedShippableItem for only the shippable ShoppingItem
		when(pricedShippableItemTransformer.apply(shoppingItem2, shippableItemPricing2)).thenReturn(pricedShippableItem2);

		// When we call the method under test
		final Stream<PricedShippableItem> actualResult = objectUnderTestWithFake.apply(allShoppingItems, shippableItemsPricing);

		// Then the resultant stream contains just one PricedShippableItem, the PricedShippableItem mocked for the shippable shopping item
		assertThat(actualResult).containsExactly(pricedShippableItem2);
	}

	@Test
	public void verifyApplyDoesNotFilterShoppingItemsIfNoPredicateProvided() {
		// Given the input ShippableItemsPricing does not have a Predicate to filter the two ShoppingItems in the cart
		when(shippableItemsPricing.getShippableItemPredicate()).thenReturn(Optional.empty());

		// And we fake out a couple of methods that are tested in a separate tests, so we don't want to double test
		final PricedShippableItemsTransformerImpl objectUnderTestWithFake = spy(objectUnderTest);

		// Fake 1: apportionSubtotalDiscount()
		final String arbitraryShoppingItemGuid = "anythingSinceWeAreTellingApportionSubtotalDiscountToReturnThisMapAndJustVerifyingWeAreReturningIt";
		final BigDecimal arbitraryDiscount = BigDecimal.ONE;

		final Map<String, BigDecimal> apportionedSubtotalMap = ImmutableMap.of(arbitraryShoppingItemGuid, arbitraryDiscount);

		doReturn(apportionedSubtotalMap).when(objectUnderTestWithFake).apportionSubtotalDiscount(allShoppingItems, shippableItemsPricing);

		doReturn(shippableItemPricing1).when(objectUnderTestWithFake)
				.createShippableItemPricing(shoppingItem1, shippableItemsPricing, apportionedSubtotalMap, CURRENCY);
		doReturn(shippableItemPricing2).when(objectUnderTestWithFake)
				.createShippableItemPricing(shoppingItem2, shippableItemsPricing, apportionedSubtotalMap, CURRENCY);

		// And the delegated individual PricedShippableItem transformer returns the expected PricedShippableItems for both ShoppingItems
		when(pricedShippableItemTransformer.apply(shoppingItem1, shippableItemPricing1)).thenReturn(pricedShippableItem1);
		when(pricedShippableItemTransformer.apply(shoppingItem2, shippableItemPricing2)).thenReturn(pricedShippableItem2);

		// When we call the method under test
		final Stream<PricedShippableItem> actualResult = objectUnderTestWithFake.apply(allShoppingItems, shippableItemsPricing);

		// Then the resultant stream contains just two PricedShippableItems, one for each Shopping Item as no filtering should take place
		assertThat(actualResult).containsExactlyInAnyOrder(pricedShippableItem1, pricedShippableItem2);
	}

	@Test
	public void verifyApplyThrowsNPEWhenNullShoppingItemCollectionReceived() {
		assertThatExceptionOfType(NullPointerException.class)
				.isThrownBy(() -> objectUnderTest.apply(null, shippableItemsPricing))
				.withMessage("ShoppingItem collection is required, even if empty.");
	}

	@Test
	public void verifyApplyThrowsNPEWhenNullShippableItemsPricingReceived() {
		assertThatExceptionOfType(NullPointerException.class)
				.isThrownBy(() -> objectUnderTest.apply(allShoppingItems, null))
				.withMessage("ShippableItemsPricing is required.");
	}

	@Test
	public void verifyApplyThrowsNPEWhenNullCurrencyReceived() {
		when(shippableItemsPricing.getCurrency()).thenReturn(null);

		assertThatExceptionOfType(NullPointerException.class)
				.isThrownBy(() -> objectUnderTest.apply(allShoppingItems, shippableItemsPricing))
				.withMessage("The Currency must be provided on the ShippableItemsPricing object.");
	}

	@Test
	public void verifyApportionSubtotalDiscountDelegatesToCalculatorCorrectly() {
		// Given a non-zero subtotal discount
		when(shippableItemsPricing.getSubtotalDiscount()).thenReturn(NON_ZERO_SUBTOTAL_DISCOUNT);

		// And an arbitrary apportioned subtotal map that we tell the mocked calculator to return
		final String arbitraryShoppingItemGuid = "anythingSinceWeAreTellingDiscountCalculatorToReturnThisMapAndJustVerifyingWeAreReturningIt";
		final BigDecimal arbitraryDiscount = BigDecimal.ONE;

		final Map<String, BigDecimal> expectedApportionedSubtotalMap = ImmutableMap.of(arbitraryShoppingItemGuid, arbitraryDiscount);

		// And a pricing snapshot map that we expect to be constructed
		final Map<ShoppingItem, ShoppingItemPricingSnapshot> expectedPricingSnapshotMap
				= ImmutableMap.of(shoppingItem1, shoppingItemPricingSnapshot1,
								  shoppingItem2, shoppingItemPricingSnapshot2);

		// And passed to the calculator
		when(discountApportioningCalculator.apportionDiscountToShoppingItems(NON_ZERO_SUBTOTAL_DISCOUNT, expectedPricingSnapshotMap))
				.thenReturn(expectedApportionedSubtotalMap);

		// When the method under test is invoked
		final Map<String, BigDecimal> actualApportionedSubtotalMap
				= objectUnderTest.apportionSubtotalDiscount(allShoppingItems, shippableItemsPricing);

		// Verify the calculator is called with the expected parameters
		verify(discountApportioningCalculator).apportionDiscountToShoppingItems(NON_ZERO_SUBTOTAL_DISCOUNT, expectedPricingSnapshotMap);

		// And the method under test returns the value returned by the calculator
		assertThat(actualApportionedSubtotalMap).isEqualTo(expectedApportionedSubtotalMap);
	}

	@Test
	public void verifyApportionSubtotalDiscountReturnsEmptyMapWhenNoSubtotalDiscount() {
		when(shippableItemsPricing.getSubtotalDiscount()).thenReturn(null);

		final Map<String, BigDecimal> actualApportionedSubtotalMap
				= objectUnderTest.apportionSubtotalDiscount(allShoppingItems, shippableItemsPricing);

		assertEmptyApportionedSubtotalMap(actualApportionedSubtotalMap);
	}

	@Test
	public void verifyApportionSubtotalDiscountReturnsEmptyMapWhenZeroSubtotalDiscount() {
		when(shippableItemsPricing.getSubtotalDiscount()).thenReturn(Money.zero(CURRENCY));

		final Map<String, BigDecimal> actualApportionedSubtotalMap
				= objectUnderTest.apportionSubtotalDiscount(allShoppingItems, shippableItemsPricing);

		assertEmptyApportionedSubtotalMap(actualApportionedSubtotalMap);
	}

	private void assertEmptyApportionedSubtotalMap(final Map<String, BigDecimal> actualApportionedSubtotalMap) {
		assertThat(actualApportionedSubtotalMap).isNotNull();
		assertThat(actualApportionedSubtotalMap).isEmpty();

		// Verify we did get the subtotal discount
		verify(shippableItemsPricing).getSubtotalDiscount();

		// But after seeing that it's zero we didn't do any further processing of the shopping items or call the calculator
		allShoppingItems.forEach(Mockito::verifyZeroInteractions);
		verify(shippableItemsPricing, never()).getShoppingItemPricingFunction();
		verifyZeroInteractions(discountApportioningCalculator);
	}

	@Test
	public void verifyCreateShippableItemPricingFactoryMethod() {
		final Map<String, BigDecimal> apportionedSubTotalDiscounts = ImmutableMap.of(SHOPPING_ITEM_1_GUID,
																					 SHOPPING_ITEM_1_SUBTOTAL_DISCOUNT.getAmount());

		final ShippableItemPricing actualResult = objectUnderTest.createShippableItemPricing(shoppingItem1, shippableItemsPricing,
																							 apportionedSubTotalDiscounts, CURRENCY);

		// Verify that the result is not null and contains the correct fields
		assertThat(actualResult).isNotNull();

		assertThat(actualResult.getShoppingItemPricingSnapshot()).isSameAs(shoppingItemPricingSnapshot1);

		assertThat(actualResult.getApportionedItemSubtotalDiscount()).isEqualTo(SHOPPING_ITEM_1_SUBTOTAL_DISCOUNT);

		final Money expectedUnitSubtotalDiscount = SHOPPING_ITEM_1_SUBTOTAL_DISCOUNT.divide(SHOPPING_ITEM_1_QUANTITY);
		assertThat(actualResult.getApportionedItemSubtotalUnitDiscount()).isEqualTo(expectedUnitSubtotalDiscount);
	}

	@Test
	public void verifyGetApportionedItemSubtotalDiscountReturnsCorrectMatchedDiscount() {
		final Map<String, BigDecimal> discountMap = ImmutableMap.of(SHOPPING_ITEM_1_GUID, SHOPPING_ITEM_1_SUBTOTAL_DISCOUNT.getAmount());

		final Money actualSubTotalDiscount = objectUnderTest.getApportionedItemSubtotalDiscount(SHOPPING_ITEM_1_GUID, discountMap, CURRENCY);

		assertThat(actualSubTotalDiscount).isNotNull();
		assertThat(actualSubTotalDiscount).isEqualTo(SHOPPING_ITEM_1_SUBTOTAL_DISCOUNT);
	}

	@Test
	public void verifyGetApportionedItemSubtotalDiscountReturnsZeroWhenNoDiscountMatched() {
		final String arbitraryShoppingItemGuid = "anythingAsMapIsEmpty";

		final Money actualSubTotalDiscount = objectUnderTest.getApportionedItemSubtotalDiscount(arbitraryShoppingItemGuid, emptyMap(), CURRENCY);

		assertThat(actualSubTotalDiscount).isNotNull();
		assertThat(actualSubTotalDiscount).isEqualTo(Money.zero(CURRENCY));
	}
}