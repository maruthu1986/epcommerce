/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.product.option.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.domain.skuconfiguration.impl.SkuOptionImpl;
import com.elasticpath.domain.skuconfiguration.impl.SkuOptionValueImpl;
import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.product.option.SkuOptionRepository;
import com.elasticpath.service.catalog.SkuOptionService;

/**
 * Tests for {@link SkuOptionRepositoryImpl}.
 */
public class SkuOptionEntityRepositoryImplTest {

	private static final String SKU_OPTION_NAME_KEY = "SKU_OPTION_NAME_KEY";
	private static final String SKU_OPTION_VALUE_KEY = "SKU_OPTION_VALUE_KEY";
	private static final String ALTERNATE_SKU_OPTION_NAME_KEY = "ALTERNATE_SKU_OPTION_NAME_KEY";

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final SkuOptionService skuOptionService = context.mock(SkuOptionService.class);

	private final SkuOptionRepository skuOptionRepository = new SkuOptionRepositoryImpl(skuOptionService);

	/**
	 * Test find sku option by value key with a successful result.
	 */
	@Test
	public void testFindSkuOptionByValueKey() {
		SkuOption skuOption = createSkuOption(SKU_OPTION_NAME_KEY);
		SkuOptionValue expectedSkuOptionValue = createSkuOptionValue(SKU_OPTION_VALUE_KEY, skuOption);

		shouldFindOptionValueByKeyWithResult(SKU_OPTION_VALUE_KEY, expectedSkuOptionValue);

		ExecutionResult<SkuOptionValue> result = skuOptionRepository.findSkuOptionValueByKey(SKU_OPTION_NAME_KEY, SKU_OPTION_VALUE_KEY);

		assertTrue("This should be a successful operation.", result.isSuccessful());
		assertEquals("The resulting data should contain the expected skuOptionValue.", expectedSkuOptionValue, result.getData());
	}

	/**
	 * Test find sku option by value key when no sku option value found.
	 */
	@Test
	public void testFindSkuOptionByValueKeyWhenNoSkuOptionValueFound() {
		shouldFindOptionValueByKeyWithResult(SKU_OPTION_VALUE_KEY, null);

		ExecutionResult<SkuOptionValue> result = skuOptionRepository.findSkuOptionValueByKey(SKU_OPTION_NAME_KEY, SKU_OPTION_VALUE_KEY);

		assertTrue("This should result in failure.", result.isFailure());
		assertEquals("The result status should be as expected.", ResourceStatus.NOT_FOUND, result.getResourceStatus());
	}

	/**
	 * Test find sku option by value key when cannot match option value keys.
	 */
	@Test
	public void testFindSkuOptionByValueKeyWhenCannotMatchOptionValueKeys() {
		SkuOption skuOption = createSkuOption(ALTERNATE_SKU_OPTION_NAME_KEY);
		SkuOptionValue expectedSkuOptionValue = createSkuOptionValue(SKU_OPTION_VALUE_KEY, skuOption);

		shouldFindOptionValueByKeyWithResult(SKU_OPTION_VALUE_KEY, expectedSkuOptionValue);

		ExecutionResult<SkuOptionValue> result = skuOptionRepository.findSkuOptionValueByKey(SKU_OPTION_NAME_KEY, SKU_OPTION_VALUE_KEY);

		assertTrue("This should result in failure.", result.isFailure());
		assertEquals("The result status should be as expected.", ResourceStatus.NOT_FOUND, result.getResourceStatus());
	}

	private void shouldFindOptionValueByKeyWithResult(final String skuOptionValueKey, final SkuOptionValue result) {
		context.checking(new Expectations() {
			{
				oneOf(skuOptionService).findOptionValueByKey(skuOptionValueKey);
				will(returnValue(result));
			}
		});
	}

	private SkuOption createSkuOption(final String skuOptionKeyName) {
		SkuOption skuOption = new SkuOptionImpl();
		skuOption.setOptionKey(skuOptionKeyName);
		return skuOption;
	}

	private SkuOptionValue createSkuOptionValue(final String optionValueKey, final SkuOption skuOption) {
		SkuOptionValue skuOptionValue = new SkuOptionValueImpl();
		skuOptionValue.setOptionValueKey(optionValueKey);
		skuOptionValue.setSkuOption(skuOption);
		return skuOptionValue;
	}

}
