/*
 * Copyright (c) Elastic Path Software Inc., 2011.
 */
package com.elasticpath.domain.shoppingcart.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.order.impl.ShoppingItemHasRecurringPricePredicate;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test <code>ShipmentTypeShoppingCartVisitor</code>.
 */
public class ShipmentTypeShoppingCartVisitorTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	@Mock private BeanFactory beanFactory;
	@Mock private ProductSkuLookup productSkuLookup;
	private BeanFactoryExpectationsFactory expectationsFactory;

	private ShoppingItem itemWithChildren;

	private ShoppingItem electronicItem;

	private ShoppingItem physicalItem;

	private ShoppingItem serviceItem;

	private ProductSku productSkuIsNotShippable;

	private ProductSku productSkuIsShippable;

	/**
	 * Setup instance variables for each test. <br>
	 * This ensures the mockery is initialized each time and the tests are independent.
	 *
	 * @throws Exception If an exception occurs.
	 */
	@Before
	public void setUp() throws Exception {
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRODUCT_SKU_LOOKUP, productSkuLookup);

		productSkuIsNotShippable = new ProductSkuImpl();
		productSkuIsNotShippable.setGuid("nonShippableSku");
		productSkuIsNotShippable.setShippable(false);

		productSkuIsShippable = new ProductSkuImpl();
		productSkuIsShippable.setGuid("shippableSku");
		productSkuIsShippable.setShippable(true);

		electronicItem = new ShoppingItemImpl() {
			private static final long serialVersionUID = 1L;

			@Override
			public Price getPrice() {
				return null;
			}

			@Override
			public boolean isBundle(final ProductSkuLookup productSkuLookup) {
				return false;
			}
		};
		electronicItem.setSkuGuid(productSkuIsNotShippable.getGuid());

		physicalItem = new ShoppingItemImpl() {
			private static final long serialVersionUID = 1L;

			@Override
			public Price getPrice() {
				return null;
			}

			@Override
			public boolean isBundle(final ProductSkuLookup productSkuLookup) {
				return false;
			}
		};
		physicalItem.setSkuGuid(productSkuIsShippable.getGuid());

		itemWithChildren = new ShoppingItemImpl() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isBundle(final ProductSkuLookup productSkuLookup) {
				return true;
			}

			@Override
			public List<ShoppingItem> getChildren() {
				return Collections.singletonList(physicalItem);
			}
		};

		serviceItem = new ShoppingItemImpl() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isBundle(final ProductSkuLookup productSkuLookup) {
				return false;
			}
		};

		context.checking(new Expectations() { {
			allowing(productSkuLookup).findByGuid(productSkuIsNotShippable.getGuid());
			will(returnValue(productSkuIsNotShippable));

			allowing(productSkuLookup).findByGuid(productSkuIsShippable.getGuid());
			will(returnValue(productSkuIsShippable));
		} });
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * The shopping cart contains... service item
	 */
	@Test
	public void testServiceItem() {
		final ShoppingItemHasRecurringPricePredicate hasRecurringPrice = new ShoppingItemHasRecurringPricePredicate() {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean test(final ShoppingItemPricingSnapshot shoppingItemPricingSnapshot) {
				return true;
			}
		};

		ShoppingCart shoppingCart = new ShoppingCartImpl() {
			private static final long serialVersionUID = 1L;

			@Override
			public List<ShoppingItem> getRootShoppingItems() {
				return Arrays.asList(serviceItem);
			}
		};

		ShipmentTypeShoppingCartVisitor visitor = new ShipmentTypeShoppingCartVisitor(hasRecurringPrice, productSkuLookup);
		shoppingCart.accept(visitor);

		assertEquals(1, visitor.getServiceSkus().size());
		assertTrue(visitor.getServiceSkus().contains(serviceItem));
	}

	/**
	 * The shopping cart contains... electronic item bundle physical item
	 */
	@Test
	public void testElectronicAndBundleWithPhysical() {
		ShoppingCart shoppingCart = new ShoppingCartImpl() {
			private static final long serialVersionUID = 1L;

			@Override
			public List<ShoppingItem> getRootShoppingItems() {
				return Arrays.asList(electronicItem, itemWithChildren);
			}
		};

		ShipmentTypeShoppingCartVisitor visitor = new ShipmentTypeShoppingCartVisitor(new ShoppingItemHasRecurringPricePredicate(), productSkuLookup);
		shoppingCart.accept(visitor);

		assertEquals(1, visitor.getElectronicSkus().size());
		assertTrue(visitor.getElectronicSkus().contains(electronicItem));

		assertEquals(1, visitor.getPhysicalSkus().size());
		assertTrue(visitor.getPhysicalSkus().contains(physicalItem));
	}
}
