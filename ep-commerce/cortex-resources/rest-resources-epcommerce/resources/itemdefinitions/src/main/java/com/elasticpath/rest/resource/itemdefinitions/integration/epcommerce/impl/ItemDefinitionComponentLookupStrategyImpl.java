/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.integration.epcommerce.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionComponentEntity;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.itemdefinitions.components.integration.ItemDefinitionComponentLookupStrategy;
import com.elasticpath.rest.resource.itemdefinitions.integration.epcommerce.core.domain.wrapper.BundleConstituentWithAttributesWrapper;
import com.elasticpath.rest.resource.itemdefinitions.integration.epcommerce.transform.BundleConstituentWithAttributesTransformer;

/**
 * Lookup strategy for item definition components.
 */
@Singleton
@Named("itemDefinitionComponentLookupStrategy")
public class ItemDefinitionComponentLookupStrategyImpl implements ItemDefinitionComponentLookupStrategy {

	private final ResourceOperationContext resourceOperationContext;
	private final ItemRepository itemRepository;
	private final BundleConstituentWithAttributesTransformer bundleConstituentWithAttributesTransformer;


	/**
	 * Default constructor.
	 *
	 * @param resourceOperationContext the resource operation context
	 * @param itemRepository the item repository
	 * @param bundleConstituentWithAttributesTransformer the bundle constituent with attributes transformer
	 */
	@Inject
	ItemDefinitionComponentLookupStrategyImpl(
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext,
			@Named("itemRepository")
			final ItemRepository itemRepository,
			@Named("bundleConstituentWithAttributesTransformer")
			final BundleConstituentWithAttributesTransformer bundleConstituentWithAttributesTransformer) {

		this.resourceOperationContext = resourceOperationContext;
		this.itemRepository = itemRepository;
		this.bundleConstituentWithAttributesTransformer = bundleConstituentWithAttributesTransformer;
	}


	@Override
	public ExecutionResult<Boolean> hasComponents(final String storeCode, final String itemId) {
		return itemRepository.isItemBundle(itemId);
	}

	@Override
	public ExecutionResult<Collection<String>> findComponentIds(final String storeCode, final String itemId) {

		ProductSku productSku = Assign.ifSuccessful(itemRepository.getSkuForItemId(itemId));

		Product product = productSku.getProduct();
		ProductBundle productBundle = itemRepository.asProductBundle(product);

		Collection<String> componentCodes = new ArrayList<>(productBundle.getConstituents().size());

		for (BundleConstituent constituent : productBundle.getConstituents()) {
			componentCodes.add(constituent.getGuid());
		}

		return ExecutionResultFactory.createReadOK(componentCodes);
	}

	@Override
	public ExecutionResult<ItemDefinitionComponentEntity> findComponentById(final String storeCode, final String itemId,
			final String bundleConstituentGuid) {

		ProductSku productSku = Assign.ifSuccessful(itemRepository.getSkuForItemId(itemId));
		ProductBundle productBundle = itemRepository.asProductBundle(productSku.getProduct());
		BundleConstituent bundleConstituent = Assign.ifSuccessful(getNestedBundleConstituent(productBundle, bundleConstituentGuid));
		Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());
		String constituentStandaloneItemId =
				Assign.ifSuccessful(getConstituentItemId(bundleConstituent));

		BundleConstituentWithAttributesWrapper wrapper = new BundleConstituentWithAttributesWrapper();
		wrapper.setStandaloneItemId(constituentStandaloneItemId);
		wrapper.setBundleConstituent(bundleConstituent);
		wrapper.setAttributes(getAttributesForConstituent(bundleConstituent.getConstituent(), locale));

		ItemDefinitionComponentEntity entity = bundleConstituentWithAttributesTransformer.transformToEntity(wrapper, locale);
		return ExecutionResultFactory.createReadOK(entity);
	}

	private ExecutionResult<String> getConstituentItemId(final BundleConstituent bundleConstituent) {

		ProductSku constituentSku = bundleConstituent.getConstituent().getProductSku();
		return itemRepository.getItemIdForSku(constituentSku);
	}

	private ExecutionResult<BundleConstituent> getNestedBundleConstituent(final ProductBundle bundle, final String bundleConstituentGuid) {

		//cannot be null
		ExecutionResult<BundleConstituent> result = ExecutionResultFactory.createNotFound("no component found");

		for (BundleConstituent bundleConstituent : bundle.getConstituents()) {
			if (bundleConstituentGuid.equals(bundleConstituent.getGuid())) {
				result = ExecutionResultFactory.createReadOK(bundleConstituent);
				break;
			} else {
				if (bundleConstituent.getConstituent().isBundle()) {
					Product product = bundleConstituent.getConstituent().getProduct();
					ExecutionResult<BundleConstituent> nestedBundleConstituent =
							getNestedBundleConstituent(itemRepository.asProductBundle(product), bundleConstituentGuid);
					if (nestedBundleConstituent.isSuccessful()) {
						result = nestedBundleConstituent;
						break;
					}
				}
			}
		}
		return result;
	}

	private Collection<AttributeValue> getAttributesForConstituent(final ConstituentItem constituentItem, final Locale locale) {

		Product product = constituentItem.getProduct();
		List<AttributeValue> attributesForProduct = product.getFullAttributeValues(locale);

		if (constituentItem.isProductSku()) {
			attributesForProduct.addAll(constituentItem.getProductSku().getFullAttributeValues(locale));
		}
		return attributesForProduct;
	}
}
