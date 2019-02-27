/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.integration.epcommerce.transform;

import java.util.Locale;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionOptionValueEntity;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Transforms a {@link SkuOptionValue} into a {@link ItemDefinitionOptionValueEntity}, and vice versa.
 */
@Singleton
@Named("skuOptionValueTransformer")
public class SkuOptionValueTransformer extends AbstractDomainTransformer<SkuOptionValue, ItemDefinitionOptionValueEntity> {

	@Override
	public SkuOptionValue transformToDomain(final ItemDefinitionOptionValueEntity itemDefinitionOptionValueEntity, final Locale locale) {
		throw new UnsupportedOperationException("This operation is not implemented.");
	}

	@Override
	public ItemDefinitionOptionValueEntity transformToEntity(final SkuOptionValue skuOptionValue, final Locale locale) {
		return ItemDefinitionOptionValueEntity.builder()
				.withName(skuOptionValue.getOptionValueKey())
				.withDisplayName(skuOptionValue.getDisplayName(locale, true))
				.build();
	}
}
