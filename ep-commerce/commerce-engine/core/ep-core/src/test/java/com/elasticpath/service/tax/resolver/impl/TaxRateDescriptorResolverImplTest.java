/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.service.tax.resolver.impl;


import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;

import com.elasticpath.domain.tax.TaxRegion;

/**
 * Test case for {@link TaxRateDescriptorResolverImpl}.
 */
public class TaxRateDescriptorResolverImplTest {

	private final TaxRateDescriptorResolverImpl resolver = new TaxRateDescriptorResolverImpl();
	
	private final Mockery context = new JUnit4Mockery();
	
	/**
	 * Test that tax rates are properly converted from percentages to decimals.
	 */
	@Test
	public void testGetDecimalTaxRate() {
		final BigDecimal taxPercentage = new BigDecimal("7.5");
		BigDecimal taxDecimal = new BigDecimal("0.0750000000");
		final TaxRegion mockTaxRegion = context.mock(TaxRegion.class);
		context.checking(new Expectations() {
			{
				oneOf(mockTaxRegion).getTaxRate(with("someTaxCode"));
				will(returnValue(taxPercentage));
			}
		});
		assertEquals(taxDecimal, resolver.getDecimalTaxRate(mockTaxRegion, "someTaxCode"));
	}
}
