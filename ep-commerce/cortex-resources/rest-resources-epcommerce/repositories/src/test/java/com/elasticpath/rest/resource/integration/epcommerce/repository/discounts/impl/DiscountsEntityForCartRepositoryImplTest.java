/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.integration.epcommerce.repository.discounts.impl;

import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartImpl;
import com.elasticpath.money.Money;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.discounts.DiscountEntity;
import com.elasticpath.rest.definition.discounts.DiscountForCartIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerSessionRepository;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;

/**
 * Test for {@link DiscountsEntityForCartRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class DiscountsEntityForCartRepositoryImplTest {

	private static final String SCOPE = "SCOPE";
	private static final String CART_ID = "CART_ID";
	private static final String INVALID = "INVALID";
	private static final Money MONEY_TEN = Money.valueOf(10, Currency.getInstance("CAD"));

	@Mock
	private CartOrderRepository cartOrderRepository;
	@Mock
	private CustomerSessionRepository customerSessionRepository;
	@Mock
	private CustomerSession customerSession;
	@Mock
	private MoneyTransformer moneyTransformer;
	@Mock
	private PricingSnapshotRepository pricingSnapshotRepository;
	@Mock
	private ShoppingCartPricingSnapshot shoppingCartPricingSnapshot;

	@InjectMocks
	private DiscountsEntityForCartRepositoryImpl<DiscountEntity, DiscountForCartIdentifier> discountRepository;

	@Before
	public void initialize() {
		when(customerSessionRepository.findOrCreateCustomerSessionAsSingle())
				.thenReturn(Single.just(customerSession));
	}

	@Test
	public void findOneProducesCorrectDiscountEntityWhenValidCartExists() {

		ShoppingCart shoppingCart = createShoppingCart();

		when(cartOrderRepository.getEnrichedShoppingCartSingle(SCOPE, CART_ID, CartOrderRepository.FindCartOrder.BY_CART_GUID))
				.thenReturn(Single.just(shoppingCart));

		when(pricingSnapshotRepository.getShoppingCartPricingSnapshotSingle(shoppingCart))
				.thenReturn(Single.just(shoppingCartPricingSnapshot));

		when(shoppingCartPricingSnapshot.getSubtotalDiscountMoney()).thenReturn(MONEY_TEN);
		when(customerSession.getLocale()).thenReturn(Locale.CANADA);

		when(moneyTransformer.transformToEntity(MONEY_TEN, Locale.CANADA))
				.thenReturn(CostEntity.builder().withAmount(BigDecimal.TEN).withCurrency("CAD").withDisplay("CANADA").build());

		discountRepository.findOne(createDiscountForCartIdentifier(CART_ID, SCOPE))
				.test()
				.assertNoErrors()
				.assertValue(discountEntity -> discountEntity.getCartId().equals(CART_ID))
				.assertValue(discountEntity -> discountEntity.getDiscount().size() == 1);
	}

	@Test
	public void findOneProducesErrorWhenNoValidCartExists() {

		when(cartOrderRepository.getEnrichedShoppingCartSingle(INVALID, INVALID, CartOrderRepository.FindCartOrder.BY_CART_GUID))
				.thenReturn(Single.error(ResourceOperationFailure.notFound()));

		discountRepository.findOne(createDiscountForCartIdentifier(INVALID, INVALID))
				.test()
				.assertNotComplete()
				.assertError(ResourceOperationFailure.class);
	}

	private DiscountForCartIdentifier createDiscountForCartIdentifier(final String cartId, final String scope) {
		return DiscountForCartIdentifier.builder()
				.withCart(createCartIdentifer(cartId, scope))
				.build();
	}

	private CartIdentifier createCartIdentifer(final String cartId, final String scope) {
		return CartIdentifier.builder()
				.withCartId(StringIdentifier.of(cartId))
				.withScope(StringIdentifier.of(scope))
				.build();
	}

	private ShoppingCart createShoppingCart() {
		return new ShoppingCartImpl();
	}
}