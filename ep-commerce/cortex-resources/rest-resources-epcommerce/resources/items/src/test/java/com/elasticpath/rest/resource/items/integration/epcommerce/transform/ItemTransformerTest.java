/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.items.integration.epcommerce.transform;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Test;

import com.elasticpath.rest.definition.items.ItemEntity;

/**
 * Test class for {@link ItemTransformer}.
 */
public class ItemTransformerTest {

	private static final String ITEM_ID = "item_id";

	private final ItemTransformer itemTransformer = new ItemTransformer();

	/**
	 * Test transform to domain.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testTransformToDomain() {
		itemTransformer.transformToDomain(null);
	}

	/**
	 * Test transform to entity.
	 */
	@Test
	public void testTransformToEntity() {

		ItemEntity itemEntity = itemTransformer.transformToEntity(ITEM_ID, Locale.ENGLISH);

		assertEquals("Item ID does not match expected value.", ITEM_ID, itemEntity.getItemId());
	}
}
