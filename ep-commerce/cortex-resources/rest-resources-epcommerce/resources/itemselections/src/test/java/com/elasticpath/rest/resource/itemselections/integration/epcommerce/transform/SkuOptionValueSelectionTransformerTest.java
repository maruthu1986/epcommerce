/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemselections.integration.epcommerce.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Test;

import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.domain.skuconfiguration.impl.SkuOptionValueImpl;
import com.elasticpath.rest.resource.itemselections.integration.ItemSelectionOptionValuesDto;
import com.elasticpath.rest.resource.itemselections.integration.epcommerce.wrapper.SkuOptionValueSelectionWrapper;
import com.elasticpath.rest.util.collection.CollectionUtil;

/**
 * Test class for {@link SkuOptionValueSelectionTransformer}.
 */
public class SkuOptionValueSelectionTransformerTest {

	private static final String CHOSEN_VALUE_CORRELATION_ID = "chosen_value_code";
	private static final String OPTION_VALUE_CORRELATION_ID = "option_value_code";
	private final SkuOptionValueSelectionTransformer transformer = new SkuOptionValueSelectionTransformer();

	/**
	 * Test transform to domain.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testTransformToDomain() {
		transformer.transformToDomain(null);
	}

	/**
	 * Test transform to dto.
	 */
	@Test
	public void testTransformToDto() {

		SkuOptionValue selectableOptionValue = new SkuOptionValueImpl();
		selectableOptionValue.setGuid(OPTION_VALUE_CORRELATION_ID);

		SkuOptionValue chosenOptionValue = new SkuOptionValueImpl();
		chosenOptionValue.setGuid(CHOSEN_VALUE_CORRELATION_ID);

		SkuOptionValueSelectionWrapper skuOptionValueSelectionWrapper =
				new SkuOptionValueSelectionWrapper(Collections.singleton(selectableOptionValue), chosenOptionValue);
		ItemSelectionOptionValuesDto dto = transformer.transformToEntity(skuOptionValueSelectionWrapper);

		assertTrue("Result selectable option value ids do not match expected values.",
				CollectionUtil.containsOnly(Collections.singleton(OPTION_VALUE_CORRELATION_ID), dto.getSelectableOptionValueCorrelationIds()));
		assertEquals("Chosen option value id does not match expected value.", CHOSEN_VALUE_CORRELATION_ID, dto.getChosenOptionValueCorrelationId());
	}

}
