/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers.impl;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.shipping.transformers.visitors.ShippableItemContainerPopulatorVisitor;
import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.ShippableItemContainer;
import com.elasticpath.shipping.connectivity.dto.builder.Builder;
import com.elasticpath.shipping.connectivity.dto.builder.populators.BaseShippableItemContainerBuilderPopulator;

/**
 * Unit test class for transformers that extend {@link BaseShippableItemContainerTransformerImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class BaseShippableItemContainerTransformerImplTest {

	@Mock
	private ShoppingCart shoppingCart;

	@Mock
	private TestShippableItemContainerBuilder builder;

	@Mock
	private TestShippableItemContainerBuilderPopulator populator;

	@Mock
	private TestShippableItemContainer expectedResult;

	@Mock
	private TestShippableItem shippableItem1;

	@Mock
	private TestShippableItem shippableItem2;

	@Mock
	private ShippableItemContainerPopulatorVisitor<ShippableItem, BaseShippableItemContainerBuilderPopulator> baseVisitor1;

	@Mock
	private ShippableItemContainerPopulatorVisitor<ShippableItem, BaseShippableItemContainerBuilderPopulator> baseVisitor2;

	@Mock
	private ShippableItemContainerPopulatorVisitor<TestShippableItem, TestShippableItemContainerBuilderPopulator> itemSpecificVisitor1;

	@Mock
	private ShippableItemContainerPopulatorVisitor<TestShippableItem, TestShippableItemContainerBuilderPopulator> itemSpecificVisitor2;

	private List<ShippableItemContainerPopulatorVisitor<ShippableItem, BaseShippableItemContainerBuilderPopulator>> baseVisitors;
	private List<ShippableItemContainerPopulatorVisitor<TestShippableItem, TestShippableItemContainerBuilderPopulator>> itemSpecificVisitors;

	private List<TestShippableItem> shippableItems;

	private BaseShippableItemContainerTransformerImpl<TestShippableItemContainer,
													  TestShippableItem,
													  TestShippableItemContainerBuilderPopulator,
													  TestShippableItemContainerBuilder> objectUnderTest;

	@Before
	public void setUp() {
		shippableItems = asList(shippableItem1, shippableItem2);
		baseVisitors = asList(baseVisitor1, baseVisitor2);
		itemSpecificVisitors = asList(itemSpecificVisitor1, itemSpecificVisitor2);

		objectUnderTest = new BaseShippableItemContainerTransformerImpl<>();
		objectUnderTest.setSupplier(() -> builder);
		objectUnderTest.setBaseVisitors(baseVisitors);
		objectUnderTest.setItemSpecificVisitors(itemSpecificVisitors);

		when(builder.getPopulator()).thenReturn(populator);
		when(builder.build()).thenReturn(expectedResult);
	}

	protected List<ShippableItemContainerPopulatorVisitor<ShippableItem, BaseShippableItemContainerBuilderPopulator>> createBaseVisitors() {
		return asList(baseVisitor1, baseVisitor2);
	}

	@Test
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void verifyApplyVisitsAllPopulatorsBeforeBuild() {
		final TestShippableItemContainer actualResult = objectUnderTest.apply(shoppingCart, shippableItems.stream());

		final List<Object> collaborators = new ArrayList<>(baseVisitors);
		collaborators.addAll(itemSpecificVisitors);
		collaborators.add(builder);

		final InOrder inOrder = inOrder(collaborators.toArray(new Object[collaborators.size()]));

		// Validate that the order the collaborators are invoked is:
		// - The base visitors invoked in order
		// - Followed by the item-specific visitors invoked in order
		// - Followed by builder.build()

		baseVisitors.forEach(visitor -> inOrder.verify(visitor).accept(shoppingCart, (Collection) shippableItems, populator));
		itemSpecificVisitors.forEach(visitor -> inOrder.verify(visitor).accept(shoppingCart, shippableItems, populator));
		inOrder.verify(builder).build();

		// Make sure the expected built result is returned
		assertThat(actualResult).isSameAs(expectedResult);
	}

	@Test
	public void verifyApplyThrowsNPEWhenGivenNullShoppingCart() {
		assertThatExceptionOfType(NullPointerException.class)
				.isThrownBy(() -> objectUnderTest.apply(null, shippableItems.stream()))
				.withMessage("Shopping Cart is required.");
	}

	@Test
	public void verifyApplyThrowsNPEWhenGivenNullShippableItems() {
		assertThatExceptionOfType(NullPointerException.class)
				.isThrownBy(() -> objectUnderTest.apply(shoppingCart, null))
				.withMessage("Shippable Items Stream is required, but can be empty.");
	}

	/**
	 * Arbitrary extension of {@link ShippableItem} to demonstrate {@link BaseShippableItemContainerTransformerImpl} can work with any extension. 
	 */
	private interface TestShippableItem extends ShippableItem {
	}

	/**
	 * Arbitrary extension of {@link ShippableItemContainer} to demonstrate {@link BaseShippableItemContainerTransformerImpl} can work with any
	 * extension. 
	 */
	private interface TestShippableItemContainer extends ShippableItemContainer<TestShippableItem> {
	}

	/**
	 * Arbitrary builder interface to demonstrate {@link BaseShippableItemContainerTransformerImpl} can work with any populator interface.
	 */
	private interface TestShippableItemContainerBuilder extends Builder<TestShippableItemContainer, TestShippableItemContainerBuilderPopulator> {
	}

	/**
	 * Arbitrary extension of {@link BaseShippableItemContainerBuilderPopulator} to demonstrate {@link BaseShippableItemContainerTransformerImpl}
	 * can work with any extension.
	 */
	private interface TestShippableItemContainerBuilderPopulator extends BaseShippableItemContainerBuilderPopulator {
	}
}
