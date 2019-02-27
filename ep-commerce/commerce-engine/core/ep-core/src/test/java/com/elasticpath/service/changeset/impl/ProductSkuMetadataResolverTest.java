/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.changeset.impl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.domain.objectgroup.impl.BusinessObjectDescriptorImpl;
import com.elasticpath.service.catalog.ProductSkuLookup;

/**
 * Test that ProductSkuMetadataResolver resolves metadata as expected.
 */
public class ProductSkuMetadataResolverTest {

	private ProductSkuMetadataResolver resolver;

	private ProductSkuLookup skuLookup;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * Set up required for each test.
	 */
	@Before
	public void setUp()  {
		resolver = new ProductSkuMetadataResolver();
		skuLookup = context.mock(ProductSkuLookup.class);
		resolver.setSkuLookup(skuLookup);
	}

	/**
	 * Test validation behaves as required.
	 */
	@Test
	public void testIsValidResolverForObjectType() {
		assertTrue("Sku should be valid", resolver.isValidResolverForObjectType("Product SKU"));
		assertFalse("Null should not be valid", resolver.isValidResolverForObjectType(null));
		assertFalse("Empty string should not be valid", resolver.isValidResolverForObjectType(StringUtils.EMPTY));
		assertFalse("A different object string should not be valid", resolver.isValidResolverForObjectType("Product"));
		assertFalse("Arbitrary string should not be valid", resolver.isValidResolverForObjectType("anything"));
	}

	/**
	 * Test name metadata for a sku is retrieved by the sku service.
	 */
	@Test
	public void testResolveMetaDataSku() {
		final String skuGuid = "SKUGUID";
		final BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		objectDescriptor.setObjectIdentifier(skuGuid);
		objectDescriptor.setObjectType("Product SKU");

		final String productName = "Amazing Product";
		final String skuConfigName = "red, large";
		final String expectedName = productName + " - " + skuConfigName;

		final ProductSku sku = context.mock(ProductSku.class);
		final Product product = context.mock(Product.class);
		final Catalog catalog = context.mock(Catalog.class);
		final Locale defaultLocale = Locale.GERMAN;

		context.checking(new Expectations() {
			{
				oneOf(skuLookup).findByGuid(skuGuid); will(returnValue(sku));
				oneOf(catalog).getDefaultLocale(); will(returnValue(defaultLocale));
				oneOf(sku).getProduct(); will(returnValue(product));
				oneOf(sku).getDisplayName(defaultLocale); will(returnValue(skuConfigName));
				oneOf(product).getMasterCatalog(); will(returnValue(catalog));
				oneOf(product).getDisplayName(defaultLocale); will(returnValue(productName));
				oneOf(product).hasMultipleSkus(); will(returnValue(true));
			}
		});

		Map<String, String> metadata = resolver.resolveMetaData(objectDescriptor);
		assertEquals("There should be metadata returned", 1, metadata.size());
		assertEquals("The sku name should be as expected", expectedName, metadata.get("objectName"));
	}

	/**
	 * Test no metadata returned if the query returns no results (e.g. sku does not exist). 
	 */
	@Test
	public void testResolveMetaDataNonExistentSku() {
		final String skuGuid = "NOSUCHGUID";
		final BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		objectDescriptor.setObjectIdentifier(skuGuid);
		objectDescriptor.setObjectType("Product SKU");

		context.checking(new Expectations() {
			{
				oneOf(skuLookup).findByGuid(skuGuid); will(returnValue(null));
			}
		});

		Map<String, String> metadata = resolver.resolveMetaData(objectDescriptor);
		assertEquals("There should be no metadata returned", 0, metadata.size());
	}

	/**
	 * Test that no metadata is returned when the object is not a valid type.
	 */
	@Test
	public void testResolveMetaDataForNonSkuObject() {
		final BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		objectDescriptor.setObjectIdentifier("WHATEVER");
		objectDescriptor.setObjectType("Product");
		Map<String, String> metadata = resolver.resolveMetaData(objectDescriptor);
		assertEquals("There should be no metadata returned", 0, metadata.size());
	}

}
