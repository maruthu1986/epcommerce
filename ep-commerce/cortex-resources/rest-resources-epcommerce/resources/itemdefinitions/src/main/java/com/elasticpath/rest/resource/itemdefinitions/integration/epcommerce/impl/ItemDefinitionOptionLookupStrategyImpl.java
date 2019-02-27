/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.integration.epcommerce.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionOptionEntity;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionOptionValueEntity;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.itemdefinitions.integration.epcommerce.transform.SkuOptionTransformer;
import com.elasticpath.rest.resource.itemdefinitions.integration.epcommerce.transform.SkuOptionValueTransformer;
import com.elasticpath.rest.resource.itemdefinitions.options.integration.ItemDefinitionOptionLookupStrategy;

/**
 * Implements {@link ItemDefinitionOptionLookupStrategy}.
 */
@Singleton
@Named("itemDefinitionOptionLookupStrategy")
public class ItemDefinitionOptionLookupStrategyImpl implements ItemDefinitionOptionLookupStrategy {

	private final ResourceOperationContext resourceOperationContext;
	private final ItemRepository itemRepository;
	private final SkuOptionTransformer skuOptionTransformer;
	private final SkuOptionValueTransformer skuOptionValueTransformer;

	/**
	 * Default constructor.
	 *
	 * @param resourceOperationContext the resource operation context
	 * @param itemRepository the item repository
	 * @param skuOptionTransformer the sku option transformer
	 * @param skuOptionValueTransformer the sku option value transformer
	 */
	@Inject
	public ItemDefinitionOptionLookupStrategyImpl(
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext,
			@Named("itemRepository")
			final ItemRepository itemRepository,
			@Named("skuOptionTransformer")
			final SkuOptionTransformer skuOptionTransformer,
			@Named("skuOptionValueTransformer")
			final SkuOptionValueTransformer skuOptionValueTransformer) {

		this.resourceOperationContext = resourceOperationContext;
		this.itemRepository = itemRepository;
		this.skuOptionTransformer = skuOptionTransformer;
		this.skuOptionValueTransformer = skuOptionValueTransformer;
	}

	@Override
	public ExecutionResult<Collection<String>> findOptionIds(final String scope, final String itemId) {
		Set<SkuOption> skuOptions = Assign.ifSuccessful(itemRepository.getSkuOptionsForItemId(itemId));
		Collection<String> optionGuids = new ArrayList<>(skuOptions.size());

		for (SkuOption skuOption : skuOptions) {
			optionGuids.add(skuOption.getGuid());
		}
		return ExecutionResultFactory.createReadOK(optionGuids);
	}

	@Override
	public ExecutionResult<ItemDefinitionOptionEntity> findOption(final String scope, final String itemId, final String optionKey) {

		ProductSku productSku = Assign.ifSuccessful(itemRepository.getSkuForItemId(itemId));
		SkuOptionValue skuOptionValue = Assign.ifNotNull(productSku.getOptionValueMap().get(optionKey),
				OnFailure.returnNotFound("option not found for item"));

		ItemDefinitionOptionEntity dto = skuOptionTransformer.transformToEntity(skuOptionValue, getLocale());
		return ExecutionResultFactory.createReadOK(dto);
	}

	@Override
	public ExecutionResult<ItemDefinitionOptionValueEntity> findOptionValue(
			final String scope, final String itemId, final String optionKey, final String optionValue) {

		ProductSku productSku = Assign.ifSuccessful(itemRepository.getSkuForItemId(itemId));
		ProductType productType = productSku.getProduct().getProductType();
		Collection<SkuOption> skuOptions = productType.getSkuOptions();
		ItemDefinitionOptionValueEntity dto = null;

		for (SkuOption option : skuOptions) {
			if (option.getOptionKey().equals(optionKey)) {
				SkuOptionValue skuOptionValue = option.getOptionValue(optionValue);
				if (skuOptionValue != null) {
					dto = skuOptionValueTransformer.transformToEntity(skuOptionValue, getLocale());
				}
				break;
			}
		}
		Ensure.notNull(dto,
				OnFailure.returnNotFound("Option value not found."));
		return ExecutionResultFactory.createReadOK(dto);
	}

	private Locale getLocale() {
		return SubjectUtil.getLocale(resourceOperationContext.getSubject());
	}
}
