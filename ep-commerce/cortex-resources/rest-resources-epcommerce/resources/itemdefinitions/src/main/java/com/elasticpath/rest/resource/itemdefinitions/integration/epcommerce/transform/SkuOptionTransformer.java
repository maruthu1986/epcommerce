/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.integration.epcommerce.transform;

import java.util.Locale;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionOptionEntity;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Transforms a {@link SkuOptionValue} and its {@link SkuOption} to {@link ItemDefinitionOptionEntity} and vice versa.
 */
@Singleton
@Named("skuOptionTransformer")
public class SkuOptionTransformer extends AbstractDomainTransformer<SkuOptionValue, ItemDefinitionOptionEntity> {

	@Override
	public SkuOptionValue transformToDomain(final ItemDefinitionOptionEntity itemDefinitionOptionEntity, final Locale locale) {
		throw new UnsupportedOperationException("This operation is not implemented.");
	}

	/**
	 * SkuOptionValue holds both the SkuOption information, as well as the selected SkuOptionValue key.
	 * In this case, {@link ItemDefinitionOptionEntity} requires both information.
	 * So we pass in a SkuOptionValue object, rather than SkuOption.
	 *
	 * @param skuOptionValue the sku option value with sku option and value key info
	 * @param locale the locale
	 * @return the item definition option entity
	 */
	@Override
	public ItemDefinitionOptionEntity transformToEntity(final SkuOptionValue skuOptionValue, final Locale locale) {
		SkuOption skuOption = skuOptionValue.getSkuOption();

		return ItemDefinitionOptionEntity.builder()
				.withName(skuOption.getOptionKey())
				.withDisplayName(skuOption.getDisplayName(locale, true))
				.withOptionValueId(skuOptionValue.getOptionValueKey())
				.withOptionId(skuOption.getGuid()).build();
	}
}
