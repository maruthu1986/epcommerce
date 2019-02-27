/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemselections.transform;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.common.selector.SelectorResourceStateBuilder;
import com.elasticpath.rest.common.selector.SingleSelectorResourceStateBuilder;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.controls.SelectorEntity;
import com.elasticpath.rest.definition.itemdefinitions.ItemdefinitionsMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Options;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Values;
import com.elasticpath.rest.resource.itemselections.integration.ItemSelectionOptionValuesDto;
import com.elasticpath.rest.resource.itemselections.rel.ItemSelectionsResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.common.selector.SelectorRepresentationRels;
import com.elasticpath.rest.schema.uri.ItemDefinitionsOptionUriBuilder;
import com.elasticpath.rest.schema.uri.ItemDefinitionsOptionUriBuilderFactory;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Test class for {@link ItemSelectionOptionValuesTransformer}.
 */
public final class ItemSelectionOptionValuesTransformerTest {

	private static final String ITEM_DEFINITIONS_OPTION_URI = "/item_definitions_option_uri";
	private static final String ITEM_ID = "item_id";
	private static final String SCOPE = "scope";
	private static final String OPTION_ID = "option_id";
	private static final String CHOSEN_VALUE_ID = "chosen_value_id";
	private static final String VALUE_ID = "value_id";
	private static final String ITEM_SELECTIONS = "item_selections";

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final ItemDefinitionsOptionUriBuilderFactory mockItemDefinitionsOptionUriBuilderFactory =
			context.mock(ItemDefinitionsOptionUriBuilderFactory.class);
	private final ItemSelectionOptionValuesTransformer transformer =
			new ItemSelectionOptionValuesTransformer(ITEM_SELECTIONS, mockItemDefinitionsOptionUriBuilderFactory);


	/**
	 * Initialize mock classes.
	 */
	@Before
	public void setUp() {
		final ItemDefinitionsOptionUriBuilder mockItemDefinitionsOptionUriBuilder =
				context.mock(ItemDefinitionsOptionUriBuilder.class);

		context.checking(new Expectations() {
			{
				allowing(mockItemDefinitionsOptionUriBuilderFactory).get();
				will(returnValue(mockItemDefinitionsOptionUriBuilder));

				allowing(mockItemDefinitionsOptionUriBuilder).setScope(SCOPE);
				will(returnValue(mockItemDefinitionsOptionUriBuilder));

				allowing(mockItemDefinitionsOptionUriBuilder).setItemId(ITEM_ID);
				will(returnValue(mockItemDefinitionsOptionUriBuilder));

				allowing(mockItemDefinitionsOptionUriBuilder).setOptionId(OPTION_ID);
				will(returnValue(mockItemDefinitionsOptionUriBuilder));

				allowing(mockItemDefinitionsOptionUriBuilder).build();
				will(returnValue(ITEM_DEFINITIONS_OPTION_URI));
			}
		});
	}

	/**
	 * Test transform to representation.
	 */
	@Test
	public void testTransformToRepresentation() {
		ItemSelectionOptionValuesDto dto = createDto();
		ResourceState<SelectorEntity> selector = transformer.transformToRepresentation(dto, SCOPE, ITEM_ID, OPTION_ID);
		ResourceState<SelectorEntity> expectedSelector = createExpectedSelector();

		assertEquals("Selector does not match expected value.", expectedSelector, selector);
	}

	private ResourceState<SelectorEntity> createExpectedSelector() {
		SelectorResourceStateBuilder selectorBuilder = new SingleSelectorResourceStateBuilder();

		String optionUri = URIUtil.format(ITEM_SELECTIONS, SCOPE, ITEM_ID, Options.URI_PART, OPTION_ID);
		String selectorUri = URIUtil.format(optionUri, Selector.URI_PART);
		String choiceUri = URIUtil.format(optionUri, Values.URI_PART, Base32Util.encode(VALUE_ID), Selector.URI_PART);
		String chosenUri = URIUtil.format(optionUri, Values.URI_PART, Base32Util.encode(CHOSEN_VALUE_ID), Selector.URI_PART);

		ResourceLink choiceLink = ResourceLinkFactory.createUriType(choiceUri, CollectionsMediaTypes.LINKS.id());
		ResourceLink chosenLink = ResourceLinkFactory.createUriType(chosenUri, CollectionsMediaTypes.LINKS.id());
		ResourceLink itemDefinitionsOptionLink = ResourceLinkFactory.create(ITEM_DEFINITIONS_OPTION_URI,
				ItemdefinitionsMediaTypes.ITEM_DEFINITION_OPTION.id(), ItemSelectionsResourceRels.OPTION_REL, SelectorRepresentationRels.SELECTOR);

		selectorBuilder.setName(ItemSelectionsResourceRels.SELECTOR_NAME)
				.setSelfUri(selectorUri)
				.addChoice(chosenLink)
				.addChoice(choiceLink)
				.addLink(itemDefinitionsOptionLink)
				.setSelection(chosenLink);

		return selectorBuilder.build();
	}

	private ItemSelectionOptionValuesDto createDto() {
		ItemSelectionOptionValuesDto dto = ResourceTypeFactory.createResourceEntity(ItemSelectionOptionValuesDto.class);

		dto.setSelectableOptionValueCorrelationIds(Arrays.asList(CHOSEN_VALUE_ID, VALUE_ID))
				.setChosenOptionValueCorrelationId(CHOSEN_VALUE_ID);

		return dto;
	}
}
