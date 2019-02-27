/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemselections.impl;


import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.elasticpath.rest.ResourceStatus;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.itemselections.integration.ItemSelectionWriterStrategy;
import com.elasticpath.rest.id.util.Base32Util;

/**
 * Tests the {@link ItemSelectionWriterImpl} class.
 */
public final class ItemSelectionWriterImplTest {

	private static final String NEW_CONFIG_CODE = "new config code";
	private static final String SCOPE = "scope";
	private static final String DECODED_VALUE_ID = "VALUE_CODE";
	private static final String DECODED_OPTION_ID = "OPTION_CODE";
	private static final String ITEM_ID = "ITEM_ID";
	private static final String OPTION_ID = Base32Util.encode(DECODED_OPTION_ID);
	private static final String VALUE_ID = Base32Util.encode(DECODED_VALUE_ID);

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final ItemSelectionWriterStrategy mockItemSelectionWriterStrategy = context.mock(ItemSelectionWriterStrategy.class);
	private final ItemSelectionWriterImpl writer = new ItemSelectionWriterImpl(mockItemSelectionWriterStrategy);


	/**
	 * Tests saving the selection.
	 */
	@Test
	public void testSaveSelection() {

		context.checking(new Expectations() {
			{
				allowing(mockItemSelectionWriterStrategy)
						.saveItemConfiguration(SCOPE, ITEM_ID, DECODED_OPTION_ID, DECODED_VALUE_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(NEW_CONFIG_CODE)));
			}
		});

		ExecutionResult<String> saveConfigurationSelection =
				writer.saveConfigurationSelection(SCOPE, ITEM_ID, OPTION_ID, VALUE_ID);

		assertTrue("Operation should be successful.", saveConfigurationSelection.isSuccessful());
		assertEquals("Selection does not match expected value.", NEW_CONFIG_CODE, saveConfigurationSelection.getData());

	}


	/**
	 * Tests saving the selection will result in failure..
	 */
	@Test
	public void testSaveSelectionWithFailure() {

		context.checking(new Expectations() {
			{
				allowing(mockItemSelectionWriterStrategy)
						.saveItemConfiguration(SCOPE, ITEM_ID, DECODED_OPTION_ID, DECODED_VALUE_ID);
				will(returnValue(ExecutionResultFactory.createNotFound("not found")));
			}
		});

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));
		writer.saveConfigurationSelection(SCOPE, ITEM_ID, OPTION_ID, VALUE_ID);
	}
}
