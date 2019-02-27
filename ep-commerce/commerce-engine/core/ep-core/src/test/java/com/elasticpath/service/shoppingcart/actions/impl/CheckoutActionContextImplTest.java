/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.shoppingcart.actions.impl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.order.OrderPayment;

/**
 * Unit test for {@link CheckoutActionContextImpl}.
 */

@RunWith(MockitoJUnitRunner.class)
public class CheckoutActionContextImplTest {

	@Test
	public void testPreserveTransientOrderPaymentProperties() {
		final OrderPayment originalOrderPayment = mock(OrderPayment.class);
		final OrderPayment updatedOrderPayment = mock(OrderPayment.class);
		final OrderPayment someOtherOrderPayment = mock(OrderPayment.class);

		// Given
		when(originalOrderPayment.getUidPk()).thenReturn(1L);
		when(updatedOrderPayment.getUidPk()).thenReturn(1L);

		when(someOtherOrderPayment.getUidPk()).thenReturn(2L);

		CheckoutActionContextImpl checkoutActionContext = new CheckoutActionContextImpl(
				null,
				null,
				null,
				null,
				false,
				false,
				null
		);


		ArrayList<OrderPayment> orderPaymentList = new ArrayList<>();
		orderPaymentList.add(updatedOrderPayment);

		checkoutActionContext.setOrderPaymentList(orderPaymentList);

		// When
		checkoutActionContext.preserveTransientOrderPayment(Arrays.asList(originalOrderPayment, someOtherOrderPayment));

		// Then
		assertThat(checkoutActionContext.getOrderPaymentList())
			.asList()
			.containsExactly(updatedOrderPayment, someOtherOrderPayment);
	}
}
