/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.price.impl;

import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ErrorCheckPredicate.createErrorCheckPredicate;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.CART_ID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.LINE_ITEM_ID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.NOT_FOUND;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SCOPE;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.impl.ShoppingCartRepositoryImpl.LINEITEM_WAS_NOT_FOUND;

import java.util.Currency;

import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.shoppingcart.PriceCalculator;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.prices.CartLineItemPriceEntity;
import com.elasticpath.rest.definition.prices.PriceForCartLineItemIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.IdentifierTestFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl.ReactiveAdapterImpl;

/**
 * Test for {@link PriceForCartLineItemEntityRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PriceForCartLineItemEntityRepositoryImplTest {

	private final PriceForCartLineItemIdentifier priceForCartLineItemIdentifier =
			IdentifierTestFactory.buildPriceForCartLineItemIdentifier(SCOPE, CART_ID, LINE_ITEM_ID);

	@Mock
	private ShoppingCart shoppingCart;

	@Mock
	private ShoppingCartPricingSnapshot shoppingCartPricingSnapshot;

	@Mock
	private ShoppingItemPricingSnapshot shoppingItemPricingSnapshot;

	@Mock
	private CostEntity purchaseCostEntity;

	@Mock
	private CostEntity listCostEntity;

	@Mock
	private PriceCalculator priceCalculator;

	@InjectMocks
	private ReactiveAdapterImpl reactiveAdapter;

	@InjectMocks
	private PriceForCartLineItemEntityRepositoryImpl<CartLineItemPriceEntity, PriceForCartLineItemIdentifier> repository;

	@Mock
	private MoneyWrapperTransformer moneyWrapperTransformer;

	@Mock
	private CartOrderRepository cartOrderRepository;

	@Mock
	private PricingSnapshotRepository pricingSnapshotRepository;

	@Before
	public void setUp() {
		repository.setReactiveAdapter(reactiveAdapter);
	}

	@Test
	public void verifyFindOneReturnsNotFoundWhenShoppingCartIsNotFound() {
		when(cartOrderRepository.getEnrichedShoppingCartSingle(
				SCOPE, CART_ID, CartOrderRepository.FindCartOrder.BY_CART_GUID))
				.thenReturn(Single.error(ResourceOperationFailure.notFound(NOT_FOUND)));

		repository.findOne(priceForCartLineItemIdentifier)
				.test()
				.assertError(createErrorCheckPredicate(NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyFindOneReturnsNotFoundWhenPricingSnapshotNotFound() {
		when(cartOrderRepository.getEnrichedShoppingCartSingle(
				SCOPE, CART_ID, CartOrderRepository.FindCartOrder.BY_CART_GUID))
				.thenReturn(Single.just(shoppingCart));
		when(pricingSnapshotRepository.getShoppingCartPricingSnapshotSingle(shoppingCart))
				.thenReturn(Single.error(ResourceOperationFailure.notFound(NOT_FOUND)));

		repository.findOne(priceForCartLineItemIdentifier)
				.test()
				.assertError(createErrorCheckPredicate(NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyFindOneReturnsNotFoundWhenLineItemIdNotFound() {
		when(cartOrderRepository.getEnrichedShoppingCartSingle(
				SCOPE, CART_ID, CartOrderRepository.FindCartOrder.BY_CART_GUID))
				.thenReturn(Single.just(shoppingCart));
		when(pricingSnapshotRepository.getShoppingCartPricingSnapshotSingle(shoppingCart))
				.thenReturn(Single.just(shoppingCartPricingSnapshot));
		when(shoppingCartPricingSnapshot.getShoppingItemPricingSnapshot(shoppingCart.getShoppingItemByGuid(LINE_ITEM_ID)))
				.thenReturn(null);

		repository.findOne(priceForCartLineItemIdentifier)
				.test()
				.assertError(createErrorCheckPredicate(String.format(LINEITEM_WAS_NOT_FOUND, LINE_ITEM_ID), ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyFindOneReturnsCartLineItemPriceEntity() {
		Money listPrice = Money.valueOf(1, Currency.getInstance("USD"));
		Money purchasePrice = Money.valueOf(1, Currency.getInstance("USD"));
		CartLineItemPriceEntity cartLineItemPriceEntity = CartLineItemPriceEntity.builder()
				.addingPurchasePrice(purchaseCostEntity)
				.addingListPrice(listCostEntity)
				.build();

		when(cartOrderRepository.getEnrichedShoppingCartSingle(
				SCOPE, CART_ID, CartOrderRepository.FindCartOrder.BY_CART_GUID))
				.thenReturn(Single.just(shoppingCart));
		when(pricingSnapshotRepository.getShoppingCartPricingSnapshotSingle(shoppingCart))
				.thenReturn(Single.just(shoppingCartPricingSnapshot));
		when(shoppingCartPricingSnapshot.getShoppingItemPricingSnapshot(shoppingCart.getShoppingItemByGuid(LINE_ITEM_ID)))
				.thenReturn(shoppingItemPricingSnapshot);
		mockListAndPurchasePrice(listPrice, purchasePrice);
		mockMoneyWrapperTransformer(listPrice, purchasePrice, cartLineItemPriceEntity);

		repository.findOne(priceForCartLineItemIdentifier)
				.test()
				.assertNoErrors()
				.assertValue(cartLineItemPriceEntity);
	}

	private void mockListAndPurchasePrice(final Money listPrice, final Money purchasePrice) {
		when(shoppingItemPricingSnapshot.getPriceCalc()).thenReturn(priceCalculator);
		when(priceCalculator.forUnitPrice()).thenReturn(priceCalculator);
		when(shoppingItemPricingSnapshot.getListUnitPrice()).thenReturn(listPrice);
		when(priceCalculator.withCartDiscounts()).thenReturn(priceCalculator);
		when(priceCalculator.getMoney()).thenReturn(purchasePrice);
	}

	private void mockMoneyWrapperTransformer(final Money listPrice, final Money purchasePrice,
											 final CartLineItemPriceEntity cartLineItemPriceEntity) {
		MoneyWrapper moneyWrapper = new MoneyWrapper();
		moneyWrapper.setListPrice(listPrice);
		moneyWrapper.setPurchasePrice(purchasePrice);
		when(moneyWrapperTransformer.transformToEntity(moneyWrapper)).thenReturn(cartLineItemPriceEntity);
	}
}
