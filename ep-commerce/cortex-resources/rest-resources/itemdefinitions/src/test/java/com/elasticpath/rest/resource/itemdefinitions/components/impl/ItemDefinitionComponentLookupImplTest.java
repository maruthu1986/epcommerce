/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.components.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionComponentEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.itemdefinitions.components.integration.ItemDefinitionComponentLookupStrategy;
import com.elasticpath.rest.resource.itemdefinitions.components.transform.ItemDefinitionComponentTransformer;
import com.elasticpath.rest.resource.itemdefinitions.options.integration.ItemDefinitionOptionLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.util.collection.CollectionUtil;

/**
 * Tests for {@link ItemDefinitionComponentLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class ItemDefinitionComponentLookupImplTest {

	private static final String SCOPE = "scope";
	private static final String ITEM_ID = "skuCode";
	private static final String DECODED_COMPONENT_ID = "componentCode";
	private static final String COMPONENT_ID = Base32Util.encode(DECODED_COMPONENT_ID);
	private static final String ITEMDEFINITION_URI = "/itemdefinitionuri";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ItemDefinitionOptionLookupStrategy mockItemDefinitionOptionLookupStrategy;
	@Mock
	private ItemDefinitionComponentLookupStrategy mockItemDefinitionComponentLookupStrategy;
	@Mock
	private ItemDefinitionComponentTransformer mockTransformer;

	@InjectMocks
	private ItemDefinitionComponentLookupImpl lookup;

	/**
	 * Tests get list of components.
	 */
	@Test
	public void testGetListOfComponents() {

		when(mockItemDefinitionComponentLookupStrategy.findComponentIds(SCOPE, ITEM_ID))
				.thenReturn(ExecutionResultFactory.<Collection<String>>createReadOK(Collections.singleton(DECODED_COMPONENT_ID)));

		ExecutionResult<Collection<String>> idsResult = lookup.findComponentIds(SCOPE, ITEM_ID);
		assertTrue(idsResult.isSuccessful());
		assertTrue(CollectionUtil.areSame(Collections.singleton(COMPONENT_ID), idsResult.getData()));
	}

	/**
	 * Tests get list of components.
	 */
	@Test
	public void testGetListOfComponentsWithFailure() {

		when(mockItemDefinitionComponentLookupStrategy.findComponentIds(SCOPE, ITEM_ID))
				.thenReturn(ExecutionResultFactory.<Collection<String>>createNotFound("not found"));

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lookup.findComponentIds(SCOPE, ITEM_ID);
	}

	/**
	 * Test the behaviour of get component.
	 */
	@Test
	public void testGetComponent() {
		ItemDefinitionComponentEntity itemDefinitionComponentEntity = ItemDefinitionComponentEntity.builder()
				.withStandaloneItemId(ITEM_ID)
				.build();
		ResourceState<ItemDefinitionComponentEntity> representation = ResourceState.Builder.create(itemDefinitionComponentEntity).build();
		when(mockItemDefinitionComponentLookupStrategy.findComponentById(SCOPE, ITEM_ID, DECODED_COMPONENT_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(itemDefinitionComponentEntity));
		when(mockItemDefinitionOptionLookupStrategy.findOptionIds(SCOPE, ITEM_ID))
				.thenReturn(ExecutionResultFactory.<Collection<String>>createReadOK(Collections.singleton(DECODED_COMPONENT_ID)));
		when(mockTransformer.transformToRepresentation(SCOPE, ITEMDEFINITION_URI, ITEM_ID, COMPONENT_ID, true,
				itemDefinitionComponentEntity)).thenReturn(representation);

		ExecutionResult<ResourceState<ItemDefinitionComponentEntity>> result = lookup.getComponent(SCOPE, ITEMDEFINITION_URI, ITEM_ID,
				COMPONENT_ID);

		assertTrue("The operation should have been successful", result.isSuccessful());
		assertEquals("The result should be the transformed representation", representation, result.getData());
	}

	/**
	 * Test the behaviour of get component with failure.
	 */
	@Test
	public void testGetComponentWithFailure() {
		when(mockItemDefinitionComponentLookupStrategy.findComponentById(SCOPE, ITEM_ID, DECODED_COMPONENT_ID))
				.thenReturn(ExecutionResultFactory.<ItemDefinitionComponentEntity>createNotFound());

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lookup.getComponent(SCOPE, ITEMDEFINITION_URI, ITEM_ID,
				COMPONENT_ID);

		verifyZeroInteractions(mockItemDefinitionOptionLookupStrategy);
		verifyZeroInteractions(mockTransformer);
	}

	/**
	 * Test the behaviour of get component with options failure.
	 */
	@Test
	public void testGetComponentWithOptionsFailure() {
		ItemDefinitionComponentEntity itemDefinitionComponentEntity = ItemDefinitionComponentEntity.builder()
				.withStandaloneItemId(ITEM_ID)
				.build();
		ResourceState<ItemDefinitionComponentEntity> representation = ResourceState.Builder.create(itemDefinitionComponentEntity).build();
		when(mockItemDefinitionComponentLookupStrategy.findComponentById(SCOPE, ITEM_ID, DECODED_COMPONENT_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(itemDefinitionComponentEntity));
		when(mockItemDefinitionOptionLookupStrategy.findOptionIds(SCOPE, ITEM_ID))
				.thenReturn(ExecutionResultFactory.<Collection<String>>createReadOK(Collections.<String>emptyList()));
		when(mockTransformer.transformToRepresentation(SCOPE, ITEMDEFINITION_URI, ITEM_ID, COMPONENT_ID, false,
				itemDefinitionComponentEntity)).thenReturn(representation);

		ExecutionResult<ResourceState<ItemDefinitionComponentEntity>> result = lookup.getComponent(SCOPE, ITEMDEFINITION_URI, ITEM_ID,
				COMPONENT_ID);

		assertTrue("The operation should have been successful", result.isSuccessful());
		assertEquals("The result should be the transformed representation", representation, result.getData());
	}

	/**
	 * Test the behaviour of get component with no options.
	 */
	@Test
	public void testGetComponentWithNoOptions() {
		ItemDefinitionComponentEntity itemDefinitionComponentEntity = ItemDefinitionComponentEntity.builder()
				.withStandaloneItemId(ITEM_ID)
				.build();
		ResourceState<ItemDefinitionComponentEntity> representation = ResourceState.Builder.create(itemDefinitionComponentEntity).build();
		when(mockItemDefinitionComponentLookupStrategy.findComponentById(SCOPE, ITEM_ID, DECODED_COMPONENT_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(itemDefinitionComponentEntity));
		when(mockItemDefinitionOptionLookupStrategy.findOptionIds(SCOPE, ITEM_ID))
				.thenReturn(ExecutionResultFactory.<Collection<String>>createReadOK(Collections.<String>emptySet()));
		when(mockTransformer.transformToRepresentation(SCOPE, ITEMDEFINITION_URI, ITEM_ID, COMPONENT_ID, false,
				itemDefinitionComponentEntity)).thenReturn(representation);

		ExecutionResult<ResourceState<ItemDefinitionComponentEntity>> result = lookup.getComponent(SCOPE, ITEMDEFINITION_URI, ITEM_ID,
				COMPONENT_ID);

		assertTrue("The operation should have been successful", result.isSuccessful());
		assertEquals("The result should be the transformed representation", representation, result.getData());
	}
}
