/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.integration.epcommerce.transform;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionOptionValueEntity;

/**
 * Test class for {@link SkuOptionValueTransformer}.
 */
public class SkuOptionValueTransformerTest {

	private static final String VALUE_KEY = "value_key";
	private static final String VALUE_DISPLAY_NAME = "value display name";

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final SkuOptionValueTransformer transformer = new SkuOptionValueTransformer();


	/**
	 * Test transform to domain.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testTransformToDomain() {
		transformer.transformToDomain(null);
	}

	/**
	 * Test transform sku option to item definition option dto.
	 */
	@Test
	public void testTransformSkuOptionToDto() {
		final SkuOptionValue mockSkuOptionValue = context.mock(SkuOptionValue.class);

		context.checking(new Expectations() {
			{
				allowing(mockSkuOptionValue).getDisplayName(Locale.ENGLISH, true);
				will(returnValue(VALUE_DISPLAY_NAME));

				allowing(mockSkuOptionValue).getOptionValueKey();
				will(returnValue(VALUE_KEY));
			}
		});

		ItemDefinitionOptionValueEntity resultDto = transformer.transformToEntity(mockSkuOptionValue, Locale.ENGLISH);

		ItemDefinitionOptionValueEntity expectedDto = ItemDefinitionOptionValueEntity.builder()
				.withDisplayName(VALUE_DISPLAY_NAME)
				.withName(VALUE_KEY)
				.build();

		assertEquals(expectedDto, resultDto);
	}
}
