/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.calc.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Currency;

import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.shoppingcart.PriceCalculator;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;

@RunWith(MockitoJUnitRunner.class)
public class TotalsCalculatorImplTest {

	private static final String STORE_CODE = "TEST_STORE";
	private static final String EXISTS_GUID = "exists guid";
	private static final String NOT_EXISTS_GUID = "not exists guid";
	private static final String LINE_ITEM_GUID = "line item guid";
	private static final Money ZERO_CAD = Money.valueOf(BigDecimal.ZERO, Currency.getInstance("CAD"));
	private static final Money TEN_CAD = Money.valueOf(BigDecimal.TEN, Currency.getInstance("CAD"));
	public static final String OPERATION_SUCCESS = "Operation should have been successful.";
	public static final String EXPECTED_MONEY = "Expected money value does not match.";

	@Mock
	private ShoppingCart shoppingCart;
	@Mock
	private ShoppingCartPricingSnapshot cartPricingSnapshot;
	@Mock
	private ShoppingCartTaxSnapshot cartTaxSnapshot;
	@Mock
	private CartOrderRepository cartOrderRepository;
	@Mock
	private PricingSnapshotRepository pricingSnapshotRepository;

	@InjectMocks
	private TotalsCalculatorImpl calculator;

	@Test
	public void ensureTotalIsCalculatedBeforeTotalIsReadForShoppingCartWithNoSubtotalDiscount() {
		when(cartOrderRepository.getEnrichedShoppingCartSingle(STORE_CODE, EXISTS_GUID, CartOrderRepository.FindCartOrder.BY_CART_GUID))
				.thenReturn(Single.just(shoppingCart));
		when(pricingSnapshotRepository.getShoppingCartPricingSnapshotSingle(shoppingCart))
				.thenReturn(Single.just(cartPricingSnapshot));

		when(cartPricingSnapshot.getSubtotalMoney()).thenReturn(TEN_CAD);

		calculator.calculateTotalForShoppingCart(STORE_CODE, EXISTS_GUID)
				.test()
				.assertNoErrors()
				.assertValue(TEN_CAD);
	}

	@Test
	public void ensureErrorPropagationOfFailedGetShoppingCartWhenCalculatingShoppingCartTotal() {
		when(cartOrderRepository.getEnrichedShoppingCartSingle(STORE_CODE, NOT_EXISTS_GUID, CartOrderRepository.FindCartOrder.BY_CART_GUID))
				.thenReturn(Single.error(ResourceOperationFailure.notFound()));

		calculator.calculateTotalForShoppingCart(STORE_CODE, NOT_EXISTS_GUID)
				.test()
				.assertError(ResourceOperationFailure.class);
	}

	@Test
	public void ensureTotalIsCalculatedBeforeTotalIsReadForCartOrder() {
		when(cartOrderRepository.getEnrichedShoppingCartSingle(STORE_CODE, EXISTS_GUID, CartOrderRepository.FindCartOrder.BY_ORDER_GUID))
				.thenReturn(Single.just(shoppingCart));
		when(pricingSnapshotRepository.getShoppingCartTaxSnapshot(shoppingCart)).thenReturn(Single.just(cartTaxSnapshot));
		when(cartTaxSnapshot.getTotalMoney()).thenReturn(ZERO_CAD);

		calculator.calculateTotalForCartOrder(STORE_CODE, EXISTS_GUID)
				.test()
				.assertNoErrors()
				.assertValue(ZERO_CAD);
	}

	@Test
	public void ensureErrorPropagationOfFailedGetCartWhenCalculatingCartOrderTotal() {
		when(cartOrderRepository.getEnrichedShoppingCartSingle(STORE_CODE, NOT_EXISTS_GUID, CartOrderRepository.FindCartOrder.BY_ORDER_GUID))
				.thenReturn(Single.error(ResourceOperationFailure.notFound()));

		calculator.calculateTotalForCartOrder(STORE_CODE, NOT_EXISTS_GUID)
				.test()
				.assertError(ResourceOperationFailure.class);
	}

	@Test
	public void testCalculateTotalForShoppingCart() {
		when(cartOrderRepository.getEnrichedShoppingCartSingle(STORE_CODE, EXISTS_GUID, CartOrderRepository.FindCartOrder.BY_CART_GUID))
				.thenReturn(Single.just(shoppingCart));
		when(pricingSnapshotRepository.getShoppingCartPricingSnapshotSingle(shoppingCart))
				.thenReturn(Single.just(cartPricingSnapshot));

		when(cartPricingSnapshot.getSubtotalMoney()).thenReturn(TEN_CAD);

		calculator.calculateTotalForShoppingCart(STORE_CODE, EXISTS_GUID)
				.test()
				.assertNoErrors()
				.assertValue(TEN_CAD);
	}

	@Test
	public void testCalculateSubTotalWithoutTaxForCartOrder() {
		when(cartOrderRepository.getEnrichedShoppingCart(STORE_CODE, EXISTS_GUID, CartOrderRepository.FindCartOrder.BY_ORDER_GUID))
				.thenReturn(ExecutionResultFactory.createReadOK(shoppingCart));
		when(pricingSnapshotRepository.getShoppingCartPricingSnapshot(shoppingCart))
				.thenReturn(ExecutionResultFactory.createReadOK(cartPricingSnapshot));
		when(cartPricingSnapshot.getSubtotalMoney()).thenReturn(TEN_CAD);

		ExecutionResult<Money> result = calculator.calculateSubTotalForCartOrder(STORE_CODE, EXISTS_GUID);

		assertThat(result.getResourceStatus())
				.as(OPERATION_SUCCESS)
				.isEqualTo(ResourceStatus.READ_OK);
		assertThat(result.getData())
				.as(EXPECTED_MONEY)
				.isEqualTo(TEN_CAD);
	}

	@Test
	public void testCalculateTotalForLineItem() {
		ShoppingCart shoppingCart = mock(ShoppingCart.class);
		when(cartOrderRepository.getEnrichedShoppingCartSingle(STORE_CODE, EXISTS_GUID, CartOrderRepository.FindCartOrder.BY_CART_GUID))
				.thenReturn(Single.just(shoppingCart));
		when(pricingSnapshotRepository.getShoppingCartPricingSnapshotSingle(shoppingCart))
				.thenReturn(Single.just(cartPricingSnapshot));

		ShoppingItem mockShoppingItem = mockGetShoppingItem(shoppingCart);
		mockShoppingItemExpectations(mockShoppingItem, ZERO_CAD);

		calculator.calculateTotalForLineItem(STORE_CODE, EXISTS_GUID, LINE_ITEM_GUID)
				.test()
				.assertNoErrors()
				.assertValue(ZERO_CAD);
	}

	private ShoppingItem mockGetShoppingItem(final ShoppingCart mockShoppingCart) {
		ShoppingItem mockShoppingItem = mock(ShoppingItem.class);
		when(mockShoppingCart.getCartItemByGuid(LINE_ITEM_GUID)).thenReturn(mockShoppingItem);
		return mockShoppingItem;
	}

	private Price mockShoppingItemExpectations(final ShoppingItem mockShoppingItem, final Money mockPurchasePrice) {
		Price mockLineItemPrice = mock(Price.class);
		final PriceCalculator stubbedPriceCalculator = mock(PriceCalculator.class, Mockito.RETURNS_DEEP_STUBS);
		final ShoppingItemPricingSnapshot shoppingItemPricingSnapshot = mock(ShoppingItemPricingSnapshot.class);
		when(cartPricingSnapshot.getShoppingItemPricingSnapshot(mockShoppingItem)).thenReturn(shoppingItemPricingSnapshot);
		when(shoppingItemPricingSnapshot.getPriceCalc()).thenReturn(stubbedPriceCalculator);
		when(stubbedPriceCalculator.withCartDiscounts().getMoney()).thenReturn(mockPurchasePrice);
		return mockLineItemPrice;
	}

}