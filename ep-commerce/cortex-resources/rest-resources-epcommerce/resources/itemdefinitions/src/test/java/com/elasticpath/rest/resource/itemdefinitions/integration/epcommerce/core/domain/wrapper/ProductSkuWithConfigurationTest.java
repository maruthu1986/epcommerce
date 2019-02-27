/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.integration.epcommerce.core.domain.wrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.jmock.MockeryFactory;


/**
 * Test for {@link ProductSkuWithConfiguration}.
 */
public class ProductSkuWithConfigurationTest {

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	/**
	 * Test equals.
	 */
	@Test
	public void testEquals() {
		ProductSku ps1 = context.mock(ProductSku.class, "ps1");
		ProductSku ps2 = context.mock(ProductSku.class, "ps2");

		String configurationCode = "ABCD";
		ProductSkuWithConfiguration pswc1 = new ProductSkuWithConfiguration(ps1, configurationCode);
		ProductSkuWithConfiguration pswc2 = new ProductSkuWithConfiguration(ps1, configurationCode);
		ProductSkuWithConfiguration pswc3 = new ProductSkuWithConfiguration(ps1, configurationCode);
		ProductSkuWithConfiguration pswc4 = new ProductSkuWithConfiguration(ps1, "BCDE");
		ProductSkuWithConfiguration pswc5 = new ProductSkuWithConfiguration(ps2, configurationCode);

		assertEquals(pswc1, pswc1);
		assertEquals(pswc2, pswc1);

		assertEquals(pswc1, pswc2);
		assertEquals(pswc2, pswc3);
		assertEquals(pswc1, pswc3);

		assertFalse(pswc1.equals(pswc4));
		assertFalse(pswc1.equals(pswc5));
	}

	/**
	 * Test hash code.
	 */
	@Test
	public void testHashCode() {
		ProductSku productSku = context.mock(ProductSku.class);
		String configurationCode = "configurationCode";

		ProductSkuWithConfiguration pswc = new ProductSkuWithConfiguration(productSku, configurationCode);
		int hashCode = new HashCodeBuilder().append(productSku)
				.append(configurationCode)
				.build();

		assertEquals(hashCode, pswc.hashCode());
	}
}
