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
import com.elasticpath.service.shipping.ShippableItemPricing;
import com.elasticpath.service.shipping.transformers.visitors.PricedShippableItemPopulatorVisitor;
import com.elasticpath.service.shipping.transformers.visitors.ShippableItemPopulatorVisitor;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItem;
import com.elasticpath.shipping.connectivity.dto.builder.PricedShippableItemBuilder;
import com.elasticpath.shipping.connectivity.dto.builder.populators.PricedShippableItemBuilderPopulator;

/**
 * Unit test for {@link PricedShippableItemTransformerImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PricedShippableItemTransformerImplTest {

	@Mock
	private ShoppingItem shoppingItem;

	@Mock
	private ShippableItemPricing shippableItemPricing;

	@Mock
	private PricedShippableItemBuilder builder;

	@Mock
	private PricedShippableItem expectedResult;

	@Mock
	private PricedShippableItemBuilderPopulator populator;

	@Mock
	private ShippableItemPopulatorVisitor unpricedVisitor1;

	@Mock
	private ShippableItemPopulatorVisitor unpricedVisitor2;

	@Mock
	private PricedShippableItemPopulatorVisitor pricedVisitor1;

	@Mock
	private PricedShippableItemPopulatorVisitor pricedVisitor2;

	private PricedShippableItemTransformerImpl objectUnderTest;

	@Before
	public void setUp() {
		objectUnderTest = new PricedShippableItemTransformerImpl();

		objectUnderTest.setSupplier(() -> builder);
		objectUnderTest.setUnpricedVisitors(asList(unpricedVisitor1, unpricedVisitor2));
		objectUnderTest.setPricedVisitors(asList(pricedVisitor1, pricedVisitor2));

		when(builder.getPopulator()).thenReturn(populator);
		when(builder.build()).thenReturn(expectedResult);
	}

	@Test
	public void verifyAllVisitorsInvokedInOrder() {
		// When the transformer is invoked
		final PricedShippableItem actualResult = objectUnderTest.apply(shoppingItem, shippableItemPricing);

		// Then it invokes all visitors in order, first the unpriced ones, then the priced ones
		final InOrder inOrder = Mockito.inOrder(unpricedVisitor1, unpricedVisitor2, pricedVisitor1, pricedVisitor2, builder);

		inOrder.verify(unpricedVisitor1).accept(shoppingItem, populator);
		inOrder.verify(unpricedVisitor2).accept(shoppingItem, populator);
		inOrder.verify(pricedVisitor1).accept(shoppingItem, shippableItemPricing, populator);
		inOrder.verify(pricedVisitor2).accept(shoppingItem, shippableItemPricing, populator);

		// Calls build()
		inOrder.verify(builder).build();

		// And returns the expected result
		assertThat(actualResult).isSameAs(expectedResult);
	}

	@Test
	public void verifyNullShoppingItemReturnsNPE() {
		assertThatExceptionOfType(NullPointerException.class)
				.isThrownBy(() -> objectUnderTest.apply(null, shippableItemPricing))
				.withMessage("ShoppingItem is required.");
	}

	@Test
	public void verifyNullPricingSnapshotReturnsNPE() {
		assertThatExceptionOfType(NullPointerException.class)
				.isThrownBy(() -> objectUnderTest.apply(shoppingItem, null))
				.withMessage("No pricing provided for ShoppingItem");
	}
}
