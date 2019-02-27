/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.plugin.tax.domain.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elasticpath.plugin.tax.builder.TaxExemptionBuilder;
import com.elasticpath.plugin.tax.domain.TaxExemption;

/**
 * Test cases for {@link TaxExemptionImpl}.
 */
public class TaxExemptionImplTest {

	private static final String TAX_EXEMPTION_ID = "4567828";
	private static final String TAX_EXEMPTION_DATA_KEY_1 = TaxExemptionBuilder.PREFIX + "data1-key";
	private static final String TAX_EXEMPTION_DATA_VALUE_1 = "data1-value";

	@Test
	public void testAddData() {
		TaxExemption taxExemption = new TaxExemptionImpl();
		taxExemption.setExemptionId(TAX_EXEMPTION_ID);
		taxExemption.addData(TAX_EXEMPTION_DATA_KEY_1, TAX_EXEMPTION_DATA_VALUE_1);
		assertEquals("The tax exemption data is added to the datamap.", TAX_EXEMPTION_DATA_VALUE_1, taxExemption.getData(TAX_EXEMPTION_DATA_KEY_1));
	}
}