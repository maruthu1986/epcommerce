/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers.impl;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Currency;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.service.shipping.ShippableItemsPricing;
import com.elasticpath.service.shipping.transformers.BaseShippableItemContainerTransformer;
import com.elasticpath.service.shipping.transformers.PricedShippableItemsTransformer;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItem;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItemContainer;

/**
 * Tests of {@link PricedShippableItemContainerTransformerImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PricedShippableItemContainerTransformerImplTest {
	private static final Currency CURRENCY = Currency.getInstance("CAD");
	private static final Money SUBTOTAL_DISCOUNT = Money.valueOf("5", CURRENCY);

	@Mock
	private ShoppingCart shoppingCart;

	@Mock
	private Shopper shopper;

	@Mock
	private ShoppingCartPricingSnapshot shoppingCartPricingSnapshot;

	@Mock
	private ShoppingItemPricingSnapshot shoppingItemPricingSnapshot;

	@Mock
	private ShoppingItem shoppingItem1;

	@Mock
	private ShoppingItem shoppingItem2;

	@Mock
	private ShippableItemsPricing shippableItemsPricing;

	@Mock
	private Stream<PricedShippableItem> shippableItemStream;

	@Mock
	private PricedShippableItemContainer<PricedShippableItem> expectedResult;

	@Mock
	private Predicate<ShoppingItem> shippableItemPredicate;

	@Mock
	private PricedShippableItemsTransformer shippableItemsTransformer;

	@Mock
	private BaseShippableItemContainerTransformer<PricedShippableItemContainer<PricedShippableItem>, PricedShippableItem> baseTransformer;

	private Collection<ShoppingItem> apportionedLeafItems;

	@InjectMocks
	private PricedShippableItemContainerTransformerImpl objectUnderTest;

	@Before
	public void setUp() {
		apportionedLeafItems = asList(shoppingItem1, shoppingItem2);

		when(shoppingCart.getApportionedLeafItems()).thenReturn(apportionedLeafItems);
		when(shoppingCart.getShopper()).thenReturn(shopper);
		when(shopper.getCurrency()).thenReturn(CURRENCY);
		when(shoppingCartPricingSnapshot.getSubtotalDiscountMoney()).thenReturn(SUBTOTAL_DISCOUNT);
	}

	@Test
	public void verifyApplyInvokesBaseTransformerCorrectly() {
		// We want to fake out the createShippableItemsPricing() factory method as that's tested in a separate test
		// The factory method could be exposed as a separate collaborator to make testing easier, but seems overkill here,
		// so instead we'll fake just the factory method to avoid testing that method here

		// Given the faked factory method returns a mock ShippableItemsPricing
		final PricedShippableItemContainerTransformerImpl objectUnderTestWithFake = spy(objectUnderTest);
		doReturn(shippableItemsPricing).when(objectUnderTestWithFake).createShippableItemsPricing(shoppingCart, shoppingCartPricingSnapshot);

		// And the items transformer is passed in the correct apportioned leaf items and the faked ShippableItemsPricing return a Stream
		when(shippableItemsTransformer.apply(apportionedLeafItems, shippableItemsPricing)).thenReturn(shippableItemStream);

		// And finally when the baseTransformer is called with the cart and that stream return the expected result
		when(baseTransformer.apply(shoppingCart, shippableItemStream)).thenReturn(expectedResult);

		// When we call the method under test
		final PricedShippableItemContainer<PricedShippableItem> actualResult = objectUnderTestWithFake.apply(shoppingCart,
																											 shoppingCartPricingSnapshot);

		// Verify the result returned is the one returned from the mocked baseTransformer as that will validate the chain above was called correctly
		assertThat(actualResult).isSameAs(expectedResult);
	}

	@Test
	public void verifyCreateShippableItemsPricingFactoryMethod() {
		// As the ShippableItemsPricing being created contains a Function that calls a method on the objectUnderTest
		// we'll spy on it to ensure it's called correctly
		final PricedShippableItemContainerTransformerImpl objectUnderTestSpy = spy(objectUnderTest);

		// Invoke the method under test
		final ShippableItemsPricing actualResult = objectUnderTestSpy.createShippableItemsPricing(shoppingCart, shoppingCartPricingSnapshot);

		// Verify that the result is not null and contains the correct fields
		assertThat(actualResult).isNotNull();

		assertThat(actualResult.getCurrency()).isEqualTo(CURRENCY);
		assertThat(actualResult.getSubtotalDiscount()).isEqualTo(SUBTOTAL_DISCOUNT);
		assertThat(actualResult.getShippableItemPredicate()).isEqualTo(Optional.of(shippableItemPredicate));

		// The pricing function is harder to verify since it calls a method on the objectUnderTest
		final Function<ShoppingItem, ShoppingItemPricingSnapshot> pricingFunction = actualResult.getShoppingItemPricingFunction();

		// So first verify the expected method hasn't been called yet
		verify(objectUnderTestSpy, never()).getShoppingItemPricingSnapshot(any(ShoppingItem.class), any(ShoppingCartPricingSnapshot.class));

		// Then invoke the pricing function
		pricingFunction.apply(shoppingItem1);

		// And finally verify the expected method has been called with the correct parameters
		verify(objectUnderTestSpy).getShoppingItemPricingSnapshot(shoppingItem1, shoppingCartPricingSnapshot);

		// The method invoked is tested itself for correctness in a separate test below
	}

	@Test
	public void verifyGetShoppingItemPricingSnapshot() {
		when(shoppingCartPricingSnapshot.getShoppingItemPricingSnapshot(shoppingItem1)).thenReturn(shoppingItemPricingSnapshot);

		final ShoppingItemPricingSnapshot actualItemPricingSnapshot
				= objectUnderTest.getShoppingItemPricingSnapshot(shoppingItem1, shoppingCartPricingSnapshot);

		verify(shoppingCartPricingSnapshot).getShoppingItemPricingSnapshot(shoppingItem1);
		assertThat(actualItemPricingSnapshot).isSameAs(shoppingItemPricingSnapshot);
	}
}