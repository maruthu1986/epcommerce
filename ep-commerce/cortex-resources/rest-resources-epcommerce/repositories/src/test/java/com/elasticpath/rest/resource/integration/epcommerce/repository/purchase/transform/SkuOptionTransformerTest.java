/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.transform;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionEntity;


/**
 * Test {@link SkuOptionTransformer}.
 */
public class SkuOptionTransformerTest {

	private static final Locale LOCALE = Locale.GERMAN;
	private static final String OPTION_GUID = "option";
	private static final String OPTION_VALUE_GUID = "optionValue";
	private static final String OPTION_DISPLAY_NAME = "Option Anzeigename";

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
	 * Test transform to entity.
	 */
	@Test
	public void testTransformToEntity() {
		final SkuOptionValue mockSkuOptionValue = context.mock(SkuOptionValue.class);
		final SkuOption mockSkuOption = context.mock(SkuOption.class);

		context.checking(new Expectations() {
			{
				allowing(mockSkuOptionValue).getSkuOption();
				will(returnValue(mockSkuOption));
				allowing(mockSkuOption).getOptionKey();
				will(returnValue(OPTION_GUID));
				allowing(mockSkuOption).getDisplayName(LOCALE, true);
				will(returnValue(OPTION_DISPLAY_NAME));
				allowing(mockSkuOptionValue).getOptionValueKey();
				will(returnValue(OPTION_VALUE_GUID));
				allowing(mockSkuOption).getGuid();
				will(returnValue(OPTION_GUID));
			}
		});

		PurchaseLineItemOptionEntity optionDto = transformer.transformToEntity(mockSkuOptionValue, LOCALE);
		assertEquals("The dto should include the name", OPTION_GUID, optionDto.getName());
		assertEquals("The dto should include the display name", OPTION_DISPLAY_NAME, optionDto.getDisplayName());
		assertEquals("The dto should include the selected value correlation id", OPTION_VALUE_GUID, optionDto.getSelectedValueId());
		assertEquals("The dto should include the option correlation id", OPTION_GUID, optionDto.getOptionId());
	}

}
