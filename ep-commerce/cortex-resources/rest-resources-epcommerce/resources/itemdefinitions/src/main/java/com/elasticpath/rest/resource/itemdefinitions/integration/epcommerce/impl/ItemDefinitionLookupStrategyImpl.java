/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.integration.epcommerce.impl;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;


import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionEntity;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.itemdefinitions.integration.ItemDefinitionLookupStrategy;
import com.elasticpath.rest.resource.itemdefinitions.integration.epcommerce.core.domain.wrapper.ProductSkuWithConfiguration;
import com.elasticpath.rest.resource.itemdefinitions.integration.epcommerce.transform.ProductSkuWithConfigurationTransformer;

/**
 * Lookup strategy form item definition.
 */
@Singleton
@Named("itemDefinitionLookupStrategy")
public class ItemDefinitionLookupStrategyImpl implements ItemDefinitionLookupStrategy {

	private final ResourceOperationContext resourceOperationContext;
	private final ItemRepository itemRepository;
	private final ProductSkuWithConfigurationTransformer productSkuWithConfigurationTransformer;

	/**
	 * Default constructor.
	 *
	 * @param resourceOperationContext the resource operation context
	 * @param itemRepository the item repository
	 * @param productSkuWithConfigurationTransformer the product sku with configuration transformer
	 */
	@Inject
	public ItemDefinitionLookupStrategyImpl(
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext,
			@Named("itemRepository")
			final ItemRepository itemRepository,
			@Named("productSkuWithConfigurationTransformer")
			final ProductSkuWithConfigurationTransformer productSkuWithConfigurationTransformer) {

		this.resourceOperationContext = resourceOperationContext;
		this.itemRepository = itemRepository;
		this.productSkuWithConfigurationTransformer = productSkuWithConfigurationTransformer;
	}


	@Override
	public ExecutionResult<ItemDefinitionEntity> find(final String storeCode, final String itemId) {
		ProductSku productSku = Assign.ifSuccessful(itemRepository.getSkuForItemId(itemId));
		ProductSkuWithConfiguration productSkuWrapper = new ProductSkuWithConfiguration(productSku, itemId);
		ItemDefinitionEntity itemDefinitionEntity =
				productSkuWithConfigurationTransformer.transformToEntity(productSkuWrapper, getLocale());
		return ExecutionResultFactory.createReadOK(itemDefinitionEntity);
	}


	private Locale getLocale() {
		return SubjectUtil.getLocale(resourceOperationContext.getSubject());
	}
}
