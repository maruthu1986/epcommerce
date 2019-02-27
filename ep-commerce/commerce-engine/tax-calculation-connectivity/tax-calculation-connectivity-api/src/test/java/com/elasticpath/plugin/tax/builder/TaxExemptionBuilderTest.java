/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.plugin.tax.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.elasticpath.plugin.tax.domain.TaxExemption;

/**
 * Test cases for {@link com.elasticpath.plugin.tax.domain.impl.TaxExemptionImpl}.
 */
public class TaxExemptionBuilderTest {

	public static final String INVALID_KEY = "asdf.asdf";
	public static final String KEY1 = TaxExemptionBuilder.PREFIX + "key1";
	public static final String KEY2 = TaxExemptionBuilder.PREFIX + "key2";

	@Test
	public void testBuildWithDataFields() {
		Map<String, String> dataMap = new HashMap<>();
		dataMap.put(INVALID_KEY, "value1");
		dataMap.put(KEY1, "value2");
		dataMap.put("abc" + TaxExemptionBuilder.PREFIX + "key2", "value3");
		dataMap.put(KEY2, "value4");

		TaxExemption taxExemption = TaxExemptionBuilder.newBuilder()
														.withDataFields(dataMap)
														.build();

		assertEquals("There are only 2 tax exexmption data values", 2, taxExemption.getAllData().size());
		assertEquals("key1 is a valid tax exemption data key", "value2", taxExemption.getData("key1"));
		assertEquals("key2 is a valid tax exemption data key", "value4", taxExemption.getData("key2"));
		assertNull(taxExemption.getData(INVALID_KEY));
		assertNull(taxExemption.getData(KEY1));
		assertNull(taxExemption.getData(KEY2));
	}
}
