/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.items.integration.epcommerce.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;
import com.elasticpath.rest.resource.items.integration.ItemLookupStrategy;
import com.elasticpath.rest.resource.items.integration.epcommerce.transform.ItemTransformer;

/**
 * Item Lookup from ep core.
 */
@Singleton
@Named("itemLookupStrategy")
public class ItemLookupStrategyImpl implements ItemLookupStrategy {

	private final ItemTransformer itemTransformer;
	private final ProductSkuRepository productSkuRepository;

	/**
	 * Constructor.
	 *
	 * @param itemTransformer the item transformer
	 * @param productSkuRepository the product sku repository
	 */
	@Inject
	ItemLookupStrategyImpl(
			@Named("itemTransformer")
			final ItemTransformer itemTransformer,
			@Named("productSkuRepository")
			final ProductSkuRepository productSkuRepository) {

		this.itemTransformer = itemTransformer;
		this.productSkuRepository = productSkuRepository;
	}

	@Override
	public ExecutionResult<ItemEntity> getItem(final String scope, final String itemId) {
		Boolean isProductSkuExist = Assign.ifSuccessful(productSkuRepository.isProductSkuExist(itemId));
		Ensure.isTrue(isProductSkuExist, ExecutionResultFactory.createNotFound("Item not found."));

		ItemEntity itemEntity = itemTransformer.transformToEntity(itemId);
		return ExecutionResultFactory.createReadOK(itemEntity);
	}
}
