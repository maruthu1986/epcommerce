/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemselections.command.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.resource.itemselections.ItemSelectionLookup;
import com.elasticpath.rest.resource.itemselections.command.ReadItemSelectionChoiceCommand;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.schema.uri.ItemDefinitionsOptionValueUriBuilder;
import com.elasticpath.rest.schema.uri.ItemDefinitionsOptionValueUriBuilderFactory;
import com.elasticpath.rest.test.AssertSelf;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Tests the {@link ReadItemSelectionChoiceCommandImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class ReadItemSelectionChoiceCommandImplTest {

	private static final int THREE_LINKS = 3;
	private static final String RESOURCE_SERVER_NAME = "itemselections";
	private static final String SCOPE = "testscope";
	private static final String ITEM_ID = Base32Util.encode("itemId");
	private static final String OPTION_ID = Base32Util.encode("optionId");
	private static final String VALUE_ID = Base32Util.encode("valueId");
	private static final String ITEM_DEFINITION_URI = "/itemdefs/option/value/id";

	@Mock
	private ItemSelectionLookup mockItemSelectionLookup;
	@Mock
	private ItemDefinitionsOptionValueUriBuilderFactory mockUriBuilderFactory;
	@Mock
	private ItemDefinitionsOptionValueUriBuilder mockUriBuilder;

	/**
	 * Tests reading a shipping address choice.
	 */
	@Test
	public void testReadChoice() {

		when(mockItemSelectionLookup.getSelectedOptionChoiceForItemId(SCOPE, ITEM_ID, OPTION_ID))
				.thenReturn(ExecutionResultFactory.createReadOK("differentValueId"));

		ReadItemSelectionChoiceCommand command = createReadItemSelectionChoiceCommand();
		ExecutionResult<ResourceState<LinksEntity>> result = command.execute();

		String expectedUri = URIUtil.format(RESOURCE_SERVER_NAME, SCOPE, ITEM_ID, "options", OPTION_ID, "values", VALUE_ID, "selector");
		assertTrue(result.isSuccessful());
		ResourceState<LinksEntity> resultRepresentation = result.getData();
		assertEquals(THREE_LINKS, resultRepresentation.getLinks().size());
		AssertSelf.assertSelf(resultRepresentation.getSelf())
			.uri(expectedUri);
	}


	/**
	 * Tests reading a shipping address chosen choice.
	 */
	@Test
	public void testReadChosenChoice() {

		when(mockItemSelectionLookup.getSelectedOptionChoiceForItemId(SCOPE, ITEM_ID, OPTION_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(VALUE_ID));

		ReadItemSelectionChoiceCommand command = createReadItemSelectionChoiceCommand();
		ExecutionResult<ResourceState<LinksEntity>> result = command.execute();

		assertTrue(result.isSuccessful());

		ResourceState<LinksEntity> resultRepresentation = result.getData();
		assertEquals(2, resultRepresentation.getLinks().size());
	}

	private ReadItemSelectionChoiceCommand createReadItemSelectionChoiceCommand() {
		when(mockUriBuilderFactory.get()).thenReturn(mockUriBuilder);
		when(mockUriBuilder.setScope(SCOPE)).thenReturn(mockUriBuilder);
		when(mockUriBuilder.setItemId(ITEM_ID)).thenReturn(mockUriBuilder);
		when(mockUriBuilder.setOptionId(OPTION_ID)).thenReturn(mockUriBuilder);
		when(mockUriBuilder.setValueId(VALUE_ID)).thenReturn(mockUriBuilder);
		when(mockUriBuilder.build()).thenReturn(ITEM_DEFINITION_URI);

		ReadItemSelectionChoiceCommandImpl command = new ReadItemSelectionChoiceCommandImpl(
			RESOURCE_SERVER_NAME, mockUriBuilderFactory, mockItemSelectionLookup);

		ReadItemSelectionChoiceCommandImpl.BuilderImpl commandBuilder = new ReadItemSelectionChoiceCommandImpl.BuilderImpl(command);

		commandBuilder.setItemId(ITEM_ID)
				.setOptionId(OPTION_ID)
				.setValueId(VALUE_ID)
				.setScope(SCOPE);

		return commandBuilder.build();
	}
}
