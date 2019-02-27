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

import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionValueEntity;

/**
 * Test the behaviour of {@link SkuOptionValueTransformer}.
 */
public class SkuOptionValueTransformerTest {

	private static final Locale LOCALE = Locale.FRENCH;
	private static final String OPTION_VALUE_GUID = "optionValue";
	private static final String OPTION_VALUE_DISPLAY_NAME = "nom d'affichage valeur sur option";

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
	 * Test transform to entity sku option value locale.
	 */
	@Test
	public void testTransformToEntitySkuOptionValueLocale() {
		final SkuOptionValue mockSkuOptionValue = context.mock(SkuOptionValue.class);

		context.checking(new Expectations() {
			{
				allowing(mockSkuOptionValue).getOptionValueKey();
				will(returnValue(OPTION_VALUE_GUID));
				allowing(mockSkuOptionValue).getDisplayName(LOCALE, true);
				will(returnValue(OPTION_VALUE_DISPLAY_NAME));
			}
		});

		PurchaseLineItemOptionValueEntity optionValueDto = transformer.transformToEntity(mockSkuOptionValue, LOCALE);
		assertEquals("The Dto should include a name", OPTION_VALUE_GUID, optionValueDto.getName());
		assertEquals("The Dto should include a display name", OPTION_VALUE_DISPLAY_NAME, optionValueDto.getDisplayName());
	}

}
