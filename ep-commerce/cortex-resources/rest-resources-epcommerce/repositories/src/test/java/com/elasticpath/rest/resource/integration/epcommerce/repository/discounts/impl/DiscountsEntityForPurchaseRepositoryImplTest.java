/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.integration.epcommerce.repository.discounts.impl;

import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.impl.OrderImpl;
import com.elasticpath.money.Money;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.discounts.DiscountEntity;
import com.elasticpath.rest.definition.discounts.DiscountForPurchaseIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.definition.purchases.PurchasesIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;

/**
 * Test for {@link DiscountsEntityForPurchaseRepositoryImplTest}.
 */
@RunWith(MockitoJUnitRunner.class)
public class DiscountsEntityForPurchaseRepositoryImplTest {

	private static final String PURCHASE_ID = "PURCHASE_ID";
	private static final String SCOPE = "SCOPE";
	private static final String INVALID = "INVALID";

	@Mock
	private OrderRepository orderRepository;
	@Mock
	private MoneyTransformer moneyTransformer;

	@InjectMocks
	private DiscountsEntityForPurchaseRepositoryImpl<DiscountEntity, DiscountForPurchaseIdentifier> discountRepository;

	@Test
	public void findOneProducesCorrectDiscountEntityWhenValidPurchaseExists() {

		when(orderRepository.findByGuidAsSingle(SCOPE, PURCHASE_ID))
				.thenReturn(Single.just(getOrder()));

		when(moneyTransformer.transformToEntity(Money.valueOf(0, Currency.getInstance("CAD")), Locale.CANADA))
				.thenReturn(CostEntity.builder().withAmount(BigDecimal.ZERO).withCurrency("CAD").withDisplay("CANADA").build());

		discountRepository.findOne(createDiscountForPurchaseIdentifier(SCOPE, PURCHASE_ID))
				.test()
				.assertNoErrors()
				.assertValue(discountEntity -> discountEntity.getDiscount().size() == 1);
	}

	@Test
	public void findOneProducesErrorWhenNoValidPurchaseExists() {

		when(orderRepository.findByGuidAsSingle(INVALID, INVALID))
				.thenReturn(Single.error(ResourceOperationFailure.notFound()));

		discountRepository.findOne(createDiscountForPurchaseIdentifier(INVALID, INVALID))
				.test()
				.assertNotComplete()
				.assertError(ResourceOperationFailure.class);
	}

	private DiscountForPurchaseIdentifier createDiscountForPurchaseIdentifier(final String scope, final String purchaseId) {
		return DiscountForPurchaseIdentifier.builder()
				.withPurchase(createPurchaseIdentifier(scope, purchaseId))
				.build();
	}

	private PurchaseIdentifier createPurchaseIdentifier(final String scope, final String purchaseId) {
		return PurchaseIdentifier.builder()
				.withPurchaseId(StringIdentifier.of(purchaseId))
				.withPurchases(PurchasesIdentifier.builder()
						.withScope(StringIdentifier.of(scope))
						.build())
				.build();
	}

	private Order getOrder() {
		Order order = new OrderImpl();
		order.setStoreCode(SCOPE);
		order.setCartOrderGuid(PURCHASE_ID);
		order.setCurrency(Currency.getInstance("CAD"));
		order.setLocale(Locale.CANADA);
		return order;
	}
}