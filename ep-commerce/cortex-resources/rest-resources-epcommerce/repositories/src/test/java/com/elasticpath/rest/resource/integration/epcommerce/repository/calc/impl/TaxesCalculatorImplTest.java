/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.calc.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.service.tax.TaxCalculationResult;

@RunWith(MockitoJUnitRunner.class)
public class TaxesCalculatorImplTest {

	private static final String STORE_CODE = "TEST_STORE";
	private static final String EXISTS_GUID = "existing guid";
	private static final String NOT_EXISTS_GUID = "non existing guid";

	@Mock
	private CartOrderRepository cartOrderRepository;

	@Mock
	private PricingSnapshotRepository pricingSnapshotRepository;

	@InjectMocks
	private TaxesCalculatorImpl calculator;

	@Test
	public void ensureTaxIsCalculatedBeforeTaxIsRead() {
		ShoppingCart shoppingCart = mock(ShoppingCart.class);
		when(cartOrderRepository.getEnrichedShoppingCartSingle(STORE_CODE, EXISTS_GUID, CartOrderRepository.FindCartOrder.BY_ORDER_GUID))
				.thenReturn(Single.just(shoppingCart));

		ShoppingCartTaxSnapshot taxSnapshot = mock(ShoppingCartTaxSnapshot.class);
		when(pricingSnapshotRepository.getShoppingCartTaxSnapshot(shoppingCart)).thenReturn(Single.just(taxSnapshot));

		TaxCalculationResult expectedTax = mock(TaxCalculationResult.class);
		when(taxSnapshot.getTaxCalculationResult()).thenReturn(expectedTax);

		calculator.calculateTax(STORE_CODE, EXISTS_GUID)
				.test()
				.assertNoErrors()
				.assertValue(expectedTax);
	}

	@Test
	public void ensureErrorPropagationOfFailedGetCartWhenCalculatingTax() {
		when(cartOrderRepository.getEnrichedShoppingCartSingle(STORE_CODE, NOT_EXISTS_GUID, CartOrderRepository.FindCartOrder.BY_ORDER_GUID))
				.thenReturn(Single.error(ResourceOperationFailure.notFound()));

		calculator.calculateTax(STORE_CODE, NOT_EXISTS_GUID)
				.test()
				.assertError(throwable -> ((ResourceOperationFailure) throwable).getResourceStatus().equals(ResourceStatus.NOT_FOUND));
	}
}

