/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemselections.integration.epcommerce.impl;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;


import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.product.StoreProductRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.product.option.SkuOptionRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;
import com.elasticpath.rest.resource.itemselections.integration.ItemSelectionWriterStrategy;
import com.elasticpath.rest.util.collection.CollectionUtil;
import com.elasticpath.service.catalog.MultiSkuProductConfigurationService;

/**
 * Item Selection Writer Strategy.
 */
@Singleton
@Named("itemSelectionWriterStrategy")
public class ItemSelectionWriterStrategyImpl implements ItemSelectionWriterStrategy {

	private final ItemRepository itemRepository;
	private final MultiSkuProductConfigurationService multiSkuProductConfigurationService;
	private final ProductSkuRepository productSkuRepository;
	private final SkuOptionRepository skuOptionRepository;
	private final StoreProductRepository storeProductRepository;

	/**
	 * Default Constructor.
	 *
	 * @param itemRepository the item repository
	 * @param multiSkuProductConfigurationService the multi sku product configuration service
	 * @param productSkuRepository the product sku repository
	 * @param skuOptionRepository the sku option repository
	 * @param storeProductRepository the store product repository
	 */
	@Inject
	public ItemSelectionWriterStrategyImpl(
			@Named("itemRepository")
			final ItemRepository itemRepository,
			@Named("multiSkuProductConfigurationService")
			final MultiSkuProductConfigurationService multiSkuProductConfigurationService,
			@Named("productSkuRepository")
			final ProductSkuRepository productSkuRepository,
			@Named("skuOptionRepository")
			final SkuOptionRepository skuOptionRepository,
			@Named("storeProductRepository")
			final StoreProductRepository storeProductRepository) {

		this.itemRepository = itemRepository;
		this.multiSkuProductConfigurationService = multiSkuProductConfigurationService;
		this.productSkuRepository = productSkuRepository;
		this.skuOptionRepository = skuOptionRepository;
		this.storeProductRepository = storeProductRepository;
	}


	@Override
	public ExecutionResult<String> saveItemConfiguration(final String storeCode, final String itemId,
			final String optionKey, final String optionValueKey) {

		ProductSku sku = Assign.ifSuccessful(itemRepository.getSkuForItemId(itemId));
		StoreProduct storeProduct = Assign.ifSuccessful(storeProductRepository
						.findDisplayableStoreProductWithAttributesByProductGuid(storeCode, sku.getProduct().getGuid()));
		Collection<SkuOptionValue> newOptionValueSelection = Assign.ifSuccessful(
				filterNewSelectedOptionValues(optionKey, optionValueKey, sku));
		return getNewItemId(storeProduct, newOptionValueSelection);
	}

	private ExecutionResult<String> getNewItemId(final StoreProduct storeProduct,
			final Collection<SkuOptionValue> newOptionValueSelection) {

		Collection<String> matchingSelectionSkuGuids =
				multiSkuProductConfigurationService.findSkuGuidsMatchingSelectedOptions(storeProduct, newOptionValueSelection);
		Ensure.isTrue(matchingSelectionSkuGuids.size() == 1,
				OnFailure.returnNotFound("Cannot find matching selection."));
		String selectedSkuGuid = CollectionUtil.first(matchingSelectionSkuGuids);
		ProductSku newProductSku = Assign.ifSuccessful(productSkuRepository.getProductSkuWithAttributesByGuid(selectedSkuGuid));
		return itemRepository.getItemIdForSku(newProductSku);
	}

	private ExecutionResult<Collection<SkuOptionValue>> filterNewSelectedOptionValues(final String optionKey,
			final String optionValueKey, final ProductSku sku) {

		Collection<SkuOptionValue> newOptionValueSelection = new ArrayList<>();
		Collection<SkuOptionValue> currentSelectedOptionValues = sku.getOptionValues();
		for (SkuOptionValue selectedOptionValue : currentSelectedOptionValues) {
			if (isPartOfNewSelection(optionKey, selectedOptionValue)) {
				newOptionValueSelection.add(selectedOptionValue);
			}
		}
		SkuOptionValue skuOptionValue = Assign.ifSuccessful(skuOptionRepository.findSkuOptionValueByKey(optionKey, optionValueKey));
		newOptionValueSelection.add(skuOptionValue);
		return ExecutionResultFactory.createReadOK(newOptionValueSelection);
	}

	private boolean isPartOfNewSelection(final String optionKey, final SkuOptionValue selectedOptionValue) {
		return !selectedOptionValue.getSkuOption().getOptionKey().equals(optionKey);
	}
}
