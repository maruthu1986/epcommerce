/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.items.transform;

import static com.elasticpath.rest.test.AssertResourceState.assertResourceState;
import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.resource.items.constant.ItemsResourceConstants;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.ItemsUriBuilder;
import com.elasticpath.rest.schema.uri.ItemsUriBuilderFactory;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Test class for {@link ItemTransformer}.
 */
public final class ItemTransformerTest {

	private static final String RESOURCE_SERVER_NAME = "resourceServerName";
	private static final String SCOPE = "scope";
	private static final String ITEM_ID = "item-id";
	private static final String SELF_URI = URIUtil.format(RESOURCE_SERVER_NAME, SCOPE, ITEM_ID);

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final ItemsUriBuilderFactory mockItemUriBuilderFactory = context.mock(ItemsUriBuilderFactory.class);
	private final ItemsUriBuilder mockItemUriBuilder = context.mock(ItemsUriBuilder.class);
	private final ItemTransformer transformer = new ItemTransformer(mockItemUriBuilderFactory);

	/**
	 * Constructor.
	 */
	public ItemTransformerTest() {

		context.checking(new Expectations() {
			{
				allowing(mockItemUriBuilderFactory).get();
				will(returnValue(mockItemUriBuilder));
				allowing(mockItemUriBuilder).setItemId(ITEM_ID);
				will(returnValue(mockItemUriBuilder));
				allowing(mockItemUriBuilder).setScope(SCOPE);
				will(returnValue(mockItemUriBuilder));
				allowing(mockItemUriBuilder).build();
				will(returnValue(SELF_URI));
			}
		});
	}

	/**
	 * Tests the representation's self.
	 */
	@Test
	public void testSelf() {
		ItemEntity itemEntity = createItemEntity();
		ResourceState<ItemEntity> resultRepresentation = transformer.transform(SCOPE, itemEntity);

		assertResourceState(resultRepresentation)
			.self(SelfFactory.createSelf(SELF_URI))
			.resourceInfoMaxAge(ItemsResourceConstants.DEFAULT_MAX_AGE);
	}

	/**
	 * Tests the representation's item ID field.
	 */
	@Test
	public void testItemId() {
		ItemEntity itemEntity = createItemEntity();
		ResourceState<ItemEntity> resultRepresentation = transformer.transform(SCOPE, itemEntity);
		assertEquals(ITEM_ID, resultRepresentation.getEntity().getItemId());
	}

	/**
	 * Tests the representation's scope field.
	 */
	@Test
	public void testScope() {
		ItemEntity itemEntity = createItemEntity();
		ResourceState<ItemEntity> resultRepresentation = transformer.transform(SCOPE, itemEntity);
		assertEquals(SCOPE, resultRepresentation.getScope());
	}


	private ItemEntity createItemEntity() {
		return ItemEntity.builder()
				.withItemId(ITEM_ID)
				.build();
	}
}
