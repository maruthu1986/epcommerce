/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemselections.integration.epcommerce.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.product.StoreProductRepository;
import com.elasticpath.rest.resource.itemselections.integration.ItemSelectionLookupStrategy;
import com.elasticpath.rest.resource.itemselections.integration.ItemSelectionOptionValuesDto;
import com.elasticpath.rest.resource.itemselections.integration.epcommerce.transform.SkuOptionValueSelectionTransformer;
import com.elasticpath.rest.resource.itemselections.integration.epcommerce.wrapper.SkuOptionValueSelectionWrapper;
import com.elasticpath.service.catalog.MultiSkuProductConfigurationService;

/**
 * Lookup strategy for item selections.
 */
@Singleton
@Named("itemSelectionLookupStrategy")
public class ItemSelectionLookupStrategyImpl implements ItemSelectionLookupStrategy {

	private final MultiSkuProductConfigurationService multiSkuProductConfigurationService;
	private final StoreProductRepository storeProductRepository;
	private final ItemRepository itemRepository;
	private final SkuOptionValueSelectionTransformer skuOptionValueSelectionTransformer;


	/**
	 * Default constructor.
	 *
	 * @param multiSkuProductConfigurationService the multi sku product configuration service
	 * @param storeProductRepository the store product repository
	 * @param itemRepository the item repository
	 * @param skuOptionValueSelectionTransformer the sku option value selection transformer
	 */
	@Inject
	public ItemSelectionLookupStrategyImpl(
			@Named("multiSkuProductConfigurationService")
			final MultiSkuProductConfigurationService multiSkuProductConfigurationService,
			@Named("storeProductRepository")
			final StoreProductRepository storeProductRepository,
			@Named("itemRepository")
			final ItemRepository itemRepository,
			@Named("skuOptionValueSelectionTransformer")
			final SkuOptionValueSelectionTransformer skuOptionValueSelectionTransformer) {

		this.multiSkuProductConfigurationService = multiSkuProductConfigurationService;
		this.storeProductRepository = storeProductRepository;
		this.itemRepository = itemRepository;
		this.skuOptionValueSelectionTransformer = skuOptionValueSelectionTransformer;
	}


	@Override
	public ExecutionResult<String> findSelectedOptionValueForOption(final String scope, final String itemId, final String optionKey) {

		ProductSku productSku = Assign.ifSuccessful(itemRepository.getSkuForItemId(itemId));

		SkuOptionValue selectedOptionValue = getSelectedOptionValueForOption(productSku, optionKey);
		String optionValueKey = selectedOptionValue.getOptionValueKey();

		return ExecutionResultFactory.createReadOK(optionValueKey);
	}

	@Override
	public ExecutionResult<ItemSelectionOptionValuesDto> findOptionValueSelections(final String storeCode,
			final String itemId, final String optionKey) {

		ProductSku productSku = Assign.ifSuccessful(itemRepository.getSkuForItemId(itemId));
		Product product = productSku.getProduct();
		StoreProduct storeProduct = Assign.ifSuccessful(storeProductRepository
						.findDisplayableStoreProductWithAttributesByProductGuid(storeCode, product.getGuid()));

		Collection<SkuOptionValue> selectedOptionValues = getSelectedOptionValues(productSku, optionKey);
		Collection<SkuOptionValue> availableOptionValuesForOption =
				multiSkuProductConfigurationService.getAvailableOptionValuesForOption(storeProduct, optionKey, selectedOptionValues);
		SkuOptionValue selectedOptionValueForOption = getSelectedOptionValueForOption(productSku, optionKey);

		// PB-1829 It is possible the currently selected option is not available, so add it to the list
		// to avoid a 500 error.
		if (selectedOptionValueForOption != null && !availableOptionValuesForOption.contains(selectedOptionValueForOption)) {
			availableOptionValuesForOption.add(selectedOptionValueForOption);
		}

		SkuOptionValueSelectionWrapper selectionWrapper =
				new SkuOptionValueSelectionWrapper(availableOptionValuesForOption, selectedOptionValueForOption);

		ItemSelectionOptionValuesDto itemSelectableOptionValuesDto =
				skuOptionValueSelectionTransformer.transformToEntity(selectionWrapper);

		return ExecutionResultFactory.createReadOK(itemSelectableOptionValuesDto);
	}

	private Collection<SkuOptionValue> getSelectedOptionValues(final ProductSku productSku, final String skuOptionKey) {
		Map<String, SkuOptionValue> selectableOptionsValueMap = productSku.getOptionValueMap();
		Collection<SkuOptionValue> selectedSkuOptionValues = new ArrayList<>(selectableOptionsValueMap.size());

		for (SkuOptionValue skuOptionValue : selectableOptionsValueMap.values()) {
			if (!skuOptionValue.getSkuOption().getOptionKey().equals(skuOptionKey)) {
				selectedSkuOptionValues.add(skuOptionValue);
			}
		}

		return selectedSkuOptionValues;
	}

	private SkuOptionValue getSelectedOptionValueForOption(final ProductSku productSku, final String optionKey) {
		return productSku.getOptionValueMap().get(optionKey);
	}
}
