/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.sellingchannel.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.InvalidProductStructureException;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.catalog.impl.PriceTierImpl;
import com.elasticpath.domain.catalog.impl.ProductTypeImpl;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;

/**
 * Tests for ShoppingItemFactoryImpl.
 */
public class ShoppingItemFactoryImplTest {

	public static final String SKU_GUID = "sku-guid";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * Test that if a ProductSku has a null Product, an InvalidProductStructureException will be thrown.
	 */
	@Test
	public void testCreateShoppingItemWithNoProductCode() {
		ProductSku productSku = mock(ProductSku.class);
		Price price = mock(Price.class);
		when(productSku.getProduct()).thenReturn(null);

		ShoppingItemFactoryImpl factory = new ShoppingItemFactoryImpl();

		assertThatThrownBy(() -> factory.createShoppingItem(productSku, price, 1, 0, null))
				.isInstanceOf(InvalidProductStructureException.class);
	}

	/**
	 * Test that if ProductSku.Product has an empty ProductCode, an InvalidProductStructureException will be thrown.
	 */
	@Test()
	public void testCreateShoppingItemWithEmptyProductCode() {
		ProductSku productSku = mock(ProductSku.class, RETURNS_DEEP_STUBS);
		Price price = mock(Price.class);

		when(productSku.getProduct().getCode()).thenReturn(null);

		ShoppingItemFactoryImpl factory = new ShoppingItemFactoryImpl();

		assertThatThrownBy(() -> factory.createShoppingItem(productSku, price, 1, 0, null))
				.isInstanceOf(InvalidProductStructureException.class);
	}

	/**
	 * Test that GCs get a special delegate.
	 */
	@Test
	public void testCreateGiftCertificateShoppingItem() {
		final ProductSku productSku = context.mock(ProductSku.class);
		final Product product = context.mock(Product.class);
		final ProductType productType = createGiftCertificateProductType();
		Price price = new PriceImpl();
		price.addOrUpdatePriceTier(new PriceTierImpl());
		final BeanFactory beanFactory = context.mock(BeanFactory.class);

		context.checking(new Expectations() {
			{
				allowing(productSku).getProduct();
				will(returnValue(product));
				allowing(productSku).getGuid();
				will(returnValue(SKU_GUID));
				allowing(product).getProductType();
				will(returnValue(productType));
				allowing(beanFactory).getBean(ContextIdNames.SHOPPING_ITEM);
				will(returnValue(new ShoppingItemImpl()));
				allowing(product).getMinOrderQty();
				will(returnValue(1));
			}
		});

		ShoppingItemFactoryImpl factory = new ShoppingItemFactoryImpl();
		factory.setBeanFactory(beanFactory);
		assertTrue(productSku.getProduct().getProductType().isGiftCertificate());
	}

	/**
	 * Test that Bundles get a special delegate that says its configurable.
	 */
	@Test
	public void testCreateBundleShoppingItem() {
		final ProductSku productSku = context.mock(ProductSku.class);
		final ProductBundle bundle = context.mock(ProductBundle.class);
		final ProductType productType = createGiftCertificateProductType();
		Price price = new PriceImpl();
		price.addOrUpdatePriceTier(new PriceTierImpl());
		final BeanFactory beanFactory = context.mock(BeanFactory.class);

		context.checking(new Expectations() {
			{
				allowing(productSku).getProduct();
				will(returnValue(bundle));
				allowing(productSku).getGuid();
				will(returnValue(SKU_GUID));
				allowing(bundle).getProductType();
				will(returnValue(productType));
				allowing(beanFactory).getBean(ContextIdNames.SHOPPING_ITEM);
				will(returnValue(new ShoppingItemImpl()));
				allowing(bundle).getMinOrderQty();
				will(returnValue(1));
			}
		});

		ShoppingItemFactoryImpl factory = new ShoppingItemFactoryImpl();
		factory.setBeanFactory(beanFactory);
		assertTrue(productSku.getProduct().getProductType().isGiftCertificate());
	}

	/** */
	@Test
	public void testCreateShoppingItemWithOrderingSet() {
		final BeanFactory mockBeanFactory = context.mock(BeanFactory.class);
		context.checking(new Expectations() {
			{
				oneOf(mockBeanFactory).getBean(ContextIdNames.SHOPPING_ITEM);
				will(returnValue(new ShoppingItemImpl()));
			}
		});

		int anyOrdering = 2;
		ShoppingItemFactoryImpl factory = new ShoppingItemFactoryImpl() {
			@Override
			protected void sanityCheck(final ProductSku sku, final Price price) {  /*do nothing*/ }

			@Override
			protected int getMinQuantity(final ProductSku sku) {
				return 1;
			}
		};
		factory.setBeanFactory(mockBeanFactory);

		ShoppingItem item = factory.createShoppingItem(null, null, 1, anyOrdering, null);

		assertEquals(anyOrdering, item.getOrdering());
	}

	/**
	 * Tests that gift certificates create and set a gift certificate delegate and that passing fields to the method results in the fields being set
	 * on the cart item.
	 */
	@Test
	public void testCopyFields() {
		ShoppingItemFactoryImpl factory = new ShoppingItemFactoryImpl();

		final Product product = context.mock(Product.class);

		final BeanFactory beanFactory = context.mock(BeanFactory.class);
		factory.setBeanFactory(beanFactory);

		final ProductType productType = context.mock(ProductType.class);

		final Price price = context.mock(Price.class);

		final ShoppingItem shoppingItem = context.mock(ShoppingItem.class);
		final ProductSku productSku = context.mock(ProductSku.class);

		final Map<String, String> itemFields = new HashMap<>();
		itemFields.put("field1", "value1");
		itemFields.put("field2", "value2");

		context.checking(new Expectations() {
			{
				final String skuGuid = new RandomGuidImpl().toString();

				allowing(productSku).getProduct(); will(returnValue(product));
				allowing(productSku).getGuid();
				will(returnValue(skuGuid));
				oneOf(product).getCode(); will(returnValue("ProductA"));
				allowing(beanFactory).getBean("shoppingItem"); will(returnValue(shoppingItem));
				allowing(product).getProductType();	will(returnValue(productType));
				allowing(productType).getName(); will(returnValue("Gift Certificates"));
				allowing(shoppingItem).setSkuGuid(skuGuid);
				allowing(shoppingItem).setPrice(1, price);
				allowing(shoppingItem).setOrdering(0);
				allowing(product).getMinOrderQty();	will(returnValue(1));
				oneOf(shoppingItem).mergeFieldValues(itemFields);
			}
		});

		ShoppingItem returnedShoppingItem = factory.createShoppingItem(productSku, price, 1, 0, itemFields);

		assertEquals("Returned shoppingCartItem should be the one we created", shoppingItem, returnedShoppingItem);

	}

	private ProductType createGiftCertificateProductType() {
		ProductType productType = new ProductTypeImpl();
		productType.setName(GiftCertificate.KEY_PRODUCT_TYPE);
		return productType;
	}

}
