/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemselections.command.impl;


import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.itemselections.ItemSelectionWriter;
import com.elasticpath.rest.resource.itemselections.command.SelectItemDefinitionOptionValueChoiceCommand;
import com.elasticpath.rest.resource.itemselections.command.impl.SelectItemDefinitionOptionValueChoiceCommandImpl.BuilderImpl;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.ItemsUriBuilder;
import com.elasticpath.rest.schema.uri.ItemsUriBuilderFactory;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 * Tests {@link SelectItemDefinitionOptionValueChoiceCommandImpl}.
 */
public final class SelectItemDefinitionOptionValueChoiceCommandImplTest {

	private static final String NEW_CONFIG_ID = "NEW CONFIG ID";
	private static final String VALUE_ID = "VALUE ID";
	private static final String OPTIONS_ID = "OPTIONS_ID";
	private static final String ITEMS_URI = "ItemsUri";
	private static final String ITEM_ID = "ITEM ID";
	private static final String SCOPE = "scope";

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final ItemSelectionWriter mockWriter = context.mock(ItemSelectionWriter.class);
	private final ItemsUriBuilderFactory mockItemsUriBuilderFactory = context.mock(ItemsUriBuilderFactory.class);


	/**
	 * Sets up the tests.
	 */
	@Before
	public void setUp() {

		final ItemsUriBuilder mockItemsUriBuilder = context.mock(ItemsUriBuilder.class);
		context.checking(new Expectations() {
			{
				allowing(mockItemsUriBuilderFactory).get();
				will(returnValue(mockItemsUriBuilder));

				allowing(mockItemsUriBuilder).setItemId(NEW_CONFIG_ID);
				will(returnValue(mockItemsUriBuilder));

				allowing(mockItemsUriBuilder).setScope(SCOPE);
				will(returnValue(mockItemsUriBuilder));

				allowing(mockItemsUriBuilder).build();
				will(returnValue(ITEMS_URI));
			}
		});
	}


	/**
	 * Tests the execute method.
	 */
	@Test
	public void testExecute() {

		SelectItemDefinitionOptionValueChoiceCommand command = createCommand();

		context.checking(new Expectations() {
			{
				allowing(mockWriter).saveConfigurationSelection(SCOPE, ITEM_ID, OPTIONS_ID, VALUE_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(NEW_CONFIG_ID)));
			}
		});

		ExecutionResult<ResourceState<ResourceEntity>> executionResult = command.execute();

		assertTrue("Operation should be succcesful.", executionResult.isSuccessful());
		assertEquals("Self URI does not match expected value.", ITEMS_URI, ResourceStateUtil.getSelfUri(executionResult.getData()));
	}

	/**
	 * Test execute on writer error.
	 */
	@Test
	public void testExecuteOnWriterError() {
		SelectItemDefinitionOptionValueChoiceCommand command = createCommand();

		context.checking(new Expectations() {
			{
				allowing(mockWriter).saveConfigurationSelection(SCOPE, ITEM_ID, OPTIONS_ID, VALUE_ID);
				will(returnValue(ExecutionResultFactory.createNotFound()));
			}
		});
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));
		command.execute();
	}


	private SelectItemDefinitionOptionValueChoiceCommand createCommand() {

		SelectItemDefinitionOptionValueChoiceCommandImpl command = new SelectItemDefinitionOptionValueChoiceCommandImpl(mockWriter,
				mockItemsUriBuilderFactory);

		SelectItemDefinitionOptionValueChoiceCommandImpl.BuilderImpl builder = new BuilderImpl(command);

		return builder.setScope(SCOPE)
				.setItemId(ITEM_ID)
				.setOptionId(OPTIONS_ID)
				.setValueId(VALUE_ID)
				.build();
	}
}
