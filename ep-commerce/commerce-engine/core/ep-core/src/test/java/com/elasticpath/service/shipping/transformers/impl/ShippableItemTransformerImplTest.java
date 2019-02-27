/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers.impl;

import static java.util.Arrays.asList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.shipping.transformers.visitors.ShippableItemPopulatorVisitor;
import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.builder.ShippableItemBuilder;
import com.elasticpath.shipping.connectivity.dto.builder.populators.ShippableItemBuilderPopulator;

/**
 * Unit test for {@link ShippableItemTransformerImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShippableItemTransformerImplTest {

	@Mock
	private ShoppingItem shoppingItem;

	@Mock
	private ShippableItemBuilder builder;

	@Mock
	private ShippableItem expectedResult;

	@Mock
	private ShippableItemBuilderPopulator populator;

	@Mock
	private ShippableItemPopulatorVisitor visitor1;

	@Mock
	private ShippableItemPopulatorVisitor visitor2;

	private ShippableItemTransformerImpl objectUnderTest;

	@Before
	public void setUp() {
		objectUnderTest = new ShippableItemTransformerImpl();

		objectUnderTest.setSupplier(() -> builder);
		objectUnderTest.setVisitors(asList(visitor1, visitor2));

		when(builder.getPopulator()).thenReturn(populator);
		when(builder.build()).thenReturn(expectedResult);
	}

	@Test
	public void verifyAllVisitorsInvokedInOrder() {
		// When the transformer is invoked
		final ShippableItem actualResult = objectUnderTest.apply(shoppingItem);

		// Then it invokes all visitors in order
		final InOrder inOrder = Mockito.inOrder(visitor1, visitor2, builder);

		inOrder.verify(visitor1).accept(shoppingItem, populator);
		inOrder.verify(visitor2).accept(shoppingItem, populator);

		// Calls build()
		inOrder.verify(builder).build();

		// And returns the expected result
		assertThat(actualResult).isSameAs(expectedResult);
	}

	@Test
	public void verifyNullShoppingItemReturnsNPE() {
		assertThatExceptionOfType(NullPointerException.class)
				.isThrownBy(() -> objectUnderTest.apply(null))
				.withMessage("ShoppingItem is required.");
	}
}
