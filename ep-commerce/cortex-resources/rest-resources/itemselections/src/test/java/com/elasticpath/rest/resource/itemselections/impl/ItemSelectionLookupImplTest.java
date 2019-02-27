/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemselections.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.apache.commons.lang3.StringUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.controls.SelectorEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.itemselections.integration.ItemSelectionLookupStrategy;
import com.elasticpath.rest.resource.itemselections.integration.ItemSelectionOptionValuesDto;
import com.elasticpath.rest.resource.itemselections.transform.ItemSelectionOptionValuesTransformer;
import com.elasticpath.rest.schema.ResourceState;

/**
 * The test for {@link ItemSelectionLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class ItemSelectionLookupImplTest {

	private static final String OPERATION_SHOULD_BE_SUCCESSFUL = "Operation should be successful";
	private static final String DECODED_OPTION_ID = "optionId";
	private static final String DECODED_VALUE_ID = "valueCode";
	private static final String ITEM_ID = "itemId";
	private static final String OPTION_ID = Base32Util.encode(DECODED_OPTION_ID);
	private static final String VALUE_ID = Base32Util.encode(DECODED_VALUE_ID);
	private static final String SCOPE = "scope";

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	private ItemSelectionLookupStrategy mockItemSelectionLookupStrategy;
	@Mock
	private ItemSelectionOptionValuesTransformer mockOptionValuesTransformer;

	@InjectMocks
	private ItemSelectionLookupImpl itemSelectionLookup;


	/**
	 * Test get selected option choice for item id with a selected value.
	 */
	@Test
	public void testGetSelectedOptionChoiceForItemId() {
		ExecutionResult<String> lookupResult = ExecutionResultFactory.createReadOK(DECODED_VALUE_ID);

		when(mockItemSelectionLookupStrategy.findSelectedOptionValueForOption(SCOPE, ITEM_ID, DECODED_OPTION_ID))
				.thenReturn(lookupResult);

		ExecutionResult<String> selectedOptionValueResult = itemSelectionLookup.getSelectedOptionChoiceForItemId(SCOPE, ITEM_ID, OPTION_ID);

		assertTrue(OPERATION_SHOULD_BE_SUCCESSFUL, selectedOptionValueResult.isSuccessful());
		assertEquals("Value ID does not match expected value id.", VALUE_ID, selectedOptionValueResult.getData());
	}

	/**
	 * Test get selected option choice for item id without a selected value.
	 */
	@Test
	public void testGetSelectedOptionChoiceForItemIdWithNoExistingSelection() {
		ExecutionResult<String> lookupResult = ExecutionResultFactory.createReadOK(null);

		when(mockItemSelectionLookupStrategy.findSelectedOptionValueForOption(SCOPE, ITEM_ID, DECODED_OPTION_ID))
				.thenReturn(lookupResult);

		ExecutionResult<String> selectedOptionValueResult = itemSelectionLookup.getSelectedOptionChoiceForItemId(SCOPE, ITEM_ID, OPTION_ID);

		assertTrue(OPERATION_SHOULD_BE_SUCCESSFUL, selectedOptionValueResult.isSuccessful());
		assertNull("Result data should be null", selectedOptionValueResult.getData());
	}

	/**
	 * Test get selected option choice for item id with stategy failure.
	 */
	@Test
	public void testGetSelectedOptionChoiceForItemIdWithStategyFailure() {
		ExecutionResult<String> lookupResult = ExecutionResultFactory.createNotFound("Not found for test");

		when(mockItemSelectionLookupStrategy.findSelectedOptionValueForOption(SCOPE, ITEM_ID, DECODED_OPTION_ID))
				.thenReturn(lookupResult);
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		itemSelectionLookup.getSelectedOptionChoiceForItemId(SCOPE, ITEM_ID, OPTION_ID);
	}

	/**
	 * Test get option value selector.
	 */
	@Test
	public void testGetOptionValueSelector() {
		ItemSelectionOptionValuesDto dto = ResourceTypeFactory.createResourceEntity(ItemSelectionOptionValuesDto.class);
		ResourceState<SelectorEntity> selector = ResourceState.Builder.create(SelectorEntity.builder().build()).build();
		ExecutionResult<ItemSelectionOptionValuesDto> lookupResult = ExecutionResultFactory.createReadOK(dto);

		when(mockOptionValuesTransformer.transformToRepresentation(dto, SCOPE, ITEM_ID, OPTION_ID))
				.thenReturn(selector);
		when(mockItemSelectionLookupStrategy.findOptionValueSelections(SCOPE, ITEM_ID, DECODED_OPTION_ID))
				.thenReturn(lookupResult);

		ExecutionResult<ResourceState<SelectorEntity>> result = itemSelectionLookup.getOptionValueSelector(SCOPE, ITEM_ID, OPTION_ID);

		assertTrue(OPERATION_SHOULD_BE_SUCCESSFUL, result.isSuccessful());
		assertEquals("Result selector does not match expected value.", selector, result.getData());
	}

	/**
	 * Test get option value selector when selections not found.
	 */
	@Test
	public void testGetOptionValueSelectorWhenSelectionsNotFound() {
		ExecutionResult<ItemSelectionOptionValuesDto> lookupResult = ExecutionResultFactory.createNotFound(StringUtils.EMPTY);

		when(mockItemSelectionLookupStrategy.findOptionValueSelections(SCOPE, ITEM_ID, DECODED_OPTION_ID))
				.thenReturn(lookupResult);
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		itemSelectionLookup.getOptionValueSelector(SCOPE, ITEM_ID, OPTION_ID);
	}
}
