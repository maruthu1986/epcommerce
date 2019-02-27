/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.items.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;
import com.elasticpath.rest.resource.items.integration.epcommerce.transform.ItemTransformer;

/**
 * Test class for {@link ItemLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ItemLookupStrategyImplTest {

	private static final String STORE_CODE = "storeCode1";
	private static final String ITEM_ID = "configuration1";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ItemTransformer mockTransformer;

	@Mock
	private ProductSkuRepository mockProductSkuRepository;

	@InjectMocks
	private ItemLookupStrategyImpl strategy;


	/**
	 * Tests the return of an {@link ItemEntity} for valid product sku.
	 */
	@Test
	public void testGetValidProductSku() {
		ItemEntity expectedItemEntity = ItemEntity.builder()
				.withItemId(ITEM_ID)
				.build();

		when(mockTransformer.transformToEntity(any(String.class))).thenReturn(expectedItemEntity);
		when(mockProductSkuRepository.isProductSkuExist(ITEM_ID))
			.thenReturn(ExecutionResultFactory.createReadOK(Boolean.TRUE));

		ExecutionResult<ItemEntity> itemResult = strategy.getItem(STORE_CODE, ITEM_ID);

		assertTrue(itemResult.isSuccessful());
		assertEquals(expectedItemEntity, itemResult.getData());
	}

	@Test
	public void shouldFailWhenItemIdIsInvalid() {
		String invalidItemId = "InvalidItemId==";

		when(mockProductSkuRepository.isProductSkuExist(invalidItemId))
			.thenReturn(ExecutionResultFactory.createNotFound("Item not found"));

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		strategy.getItem(STORE_CODE, invalidItemId);
	}

	@Test
	public void shouldFailWhenProductSkuDoesNotExist() {
		when(mockProductSkuRepository.isProductSkuExist(ITEM_ID))
			.thenReturn(ExecutionResultFactory.createReadOK(Boolean.FALSE));

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		strategy.getItem(STORE_CODE, ITEM_ID);
	}
}
