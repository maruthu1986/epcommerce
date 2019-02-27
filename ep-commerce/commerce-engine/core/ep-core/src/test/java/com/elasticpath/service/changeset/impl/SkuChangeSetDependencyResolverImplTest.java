/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.changeset.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.catalog.impl.ProductTypeImpl;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.domain.objectgroup.impl.BusinessObjectDescriptorImpl;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.service.catalog.ProductSkuLookup;

/**
 * The unit test class for sku change set dependency resolver.
 */
public class SkuChangeSetDependencyResolverImplTest {

	private static final String GUID = "Guid";
	private static final String SKU_OPTION_VALUE = "SkuOptionValue";
	private static final int DEPENDENT_OBJECT_SIZE = 2;
	private final SkuChangeSetDependencyResolverImpl resolver = new SkuChangeSetDependencyResolverImpl();

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ProductSku productSku;

	/**
	 * Setup the tests with a new productsku.
	 */
	@Before
	public void setUp() {
		productSku = createProductSku();
	}

	/**
	 * Test getting the sku when given the businessObjectDescriptor.
	 */
	@Test
	public void testGetObject() {
		BusinessObjectDescriptor businessObjectDescriptor = new BusinessObjectDescriptorImpl();
		businessObjectDescriptor.setObjectIdentifier(GUID);
		final ProductSkuLookup productSkuLookup = context.mock(ProductSkuLookup.class);
		context.checking(new Expectations() { {
			oneOf(productSkuLookup).findByGuid(GUID);
			will(returnValue(productSku));
		} });

		resolver.setProductSkuLookup(productSkuLookup);

		ProductSku notFetched = resolver.getObject(businessObjectDescriptor, Product.class);
		assertNull("Non-Sku object should not be returned.", notFetched);

		ProductSku skuFetched = resolver.getObject(businessObjectDescriptor, ProductSku.class);
		assertEquals("The sku should be returned.", productSku, skuFetched);
	}

	/**
	 * Test getting change set dependency for sku.
	 */
	@Test
	public void testGetChangeSetDependency() {
		Object obj = new Object();
		Set<?> dependencies = resolver.getChangeSetDependency(obj);
		assertTrue("Non-Sku object should not be processed", dependencies.isEmpty());

		dependencies = resolver.getChangeSetDependency(productSku);
		assertEquals("The product is not found in the dependency list of the sku", productSku.getProduct(), dependencies.iterator().next());
	}

	/**
	 * Test for no sku options.
	 */
	@Test
	public void testGetDependencyWithoutOptions() {
		productSku.setOptionValueMap(new HashMap<>());

		Set<?> dependencies = resolver.getChangeSetDependency(productSku);
		assertEquals("The number of dependents is wrong", 1, dependencies.size());
	}

	/**
	 * Test that sku options are dependent objects of product sku's, and that the dependency list can contain both products and sku options.
	 */
	@Test
	public void testGetDependencyForSkuOptions() {
		final SkuOption skuOption = context.mock(SkuOption.class);
		final SkuOptionValue skuOptionValue = context.mock(SkuOptionValue.class);

		context.checking(new Expectations() { {
			oneOf(skuOptionValue).getSkuOption(); will(returnValue(skuOption));
		} });

		HashMap<String, SkuOptionValue> optionValueMap = new HashMap<>();
		optionValueMap.put(SKU_OPTION_VALUE, skuOptionValue);
		productSku.setOptionValueMap(optionValueMap);

		Set<?> dependencies = resolver.getChangeSetDependency(productSku);
		assertEquals("The number of dependents is wrong", DEPENDENT_OBJECT_SIZE, dependencies.size());
		assertTrue("Sku Option is not in dependency list", dependencies.contains(skuOption));
		assertTrue("Product is not in dependency list", dependencies.contains(productSku.getProduct()));
		assertFalse("Sku Option value should not be a dependency", dependencies.contains(skuOptionValue));
	}

	private ProductSku createProductSku() {
		final ProductType productType = new ProductTypeImpl();
		productType.setMultiSku(true);

		final Product product = new ProductImpl();
		product.setProductType(productType);

		final ProductSku sku = new ProductSkuImpl();
		sku.setProduct(product);

		return sku;
	}
}
