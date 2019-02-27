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

import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionOptionEntity;

/**
 * Test class for {@link SkuOptionTransformer}.
 */
public class SkuOptionTransformerTest {

	private static final String OPTION_CODE = "option_code";
	private static final String OPTION_KEY = "option_key";
	private static final String OPTION_DISPLAY_NAME = "option_display_name";
	private static final String OPTION_VALUE_KEY = "option_value_key";

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final SkuOptionTransformer transformer = new SkuOptionTransformer();


	/**
	 * Test transform to domain.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testTransformToDomain() {
		transformer.transformToDomain(null);
	}

	/**
	 * Test transform sku option value to item definition option dto.
	 */
	@Test
	public void testTransformSkuOptionValueToDto() {
		final SkuOptionValue mockSkuOptionValue = context.mock(SkuOptionValue.class);
		final SkuOption mockSkuOption = context.mock(SkuOption.class);

		context.checking(new Expectations() {
			{
				allowing(mockSkuOptionValue).getOptionValueKey();
				will(returnValue(OPTION_VALUE_KEY));

				allowing(mockSkuOptionValue).getSkuOption();
				will(returnValue(mockSkuOption));

				allowing(mockSkuOption).getDisplayName(Locale.ENGLISH, true);
				will(returnValue(OPTION_DISPLAY_NAME));

				allowing(mockSkuOption).getOptionKey();
				will(returnValue(OPTION_KEY));

				allowing(mockSkuOption).getGuid();
				will(returnValue(OPTION_CODE));
			}
		});

		ItemDefinitionOptionEntity resultDto = transformer.transformToEntity(mockSkuOptionValue, Locale.ENGLISH);
		ItemDefinitionOptionEntity expectedDto = ItemDefinitionOptionEntity.builder()
				.withDisplayName(OPTION_DISPLAY_NAME)
				.withName(OPTION_KEY)
				.withOptionValueId(OPTION_VALUE_KEY)
				.withOptionId(OPTION_CODE)
				.build();

		assertEquals(expectedDto, resultDto);
	}
}
