/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemselections.integration.epcommerce.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.resource.itemselections.integration.ItemSelectionOptionValuesDto;
import com.elasticpath.rest.resource.itemselections.integration.epcommerce.wrapper.SkuOptionValueSelectionWrapper;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Transforms a {@link SkuOptionValueSelectionWrapper} into a {@link ItemSelectionOptionValuesDto}, and vice versa.
 */
@Singleton
@Named("skuOptionValueSelectionTransformer")
public class SkuOptionValueSelectionTransformer
		extends AbstractDomainTransformer<SkuOptionValueSelectionWrapper, ItemSelectionOptionValuesDto> {

	@Override
	public SkuOptionValueSelectionWrapper transformToDomain(final ItemSelectionOptionValuesDto dto, final Locale locale) {
		throw new UnsupportedOperationException("This operation is not implemented.");
	}

	@Override
	public ItemSelectionOptionValuesDto transformToEntity(final SkuOptionValueSelectionWrapper skuOptionValueSelection,
															final Locale locale) {

		Collection<SkuOptionValue> selectableSkuOptionValues = skuOptionValueSelection.getSelectableSkuOptionValues();
		SkuOptionValue selectedSkuOptionValue = skuOptionValueSelection.getSelectedSkuOptionValue();
		Collection<String> selectableOptionValueCorrelationIds = new ArrayList<>(selectableSkuOptionValues.size());

		for (SkuOptionValue skuOptionValue : selectableSkuOptionValues) {
			selectableOptionValueCorrelationIds.add(skuOptionValue.getGuid());
		}

		ItemSelectionOptionValuesDto itemSelectableOptionValuesDto =
				ResourceTypeFactory.createResourceEntity(ItemSelectionOptionValuesDto.class);

		itemSelectableOptionValuesDto.setSelectableOptionValueCorrelationIds(selectableOptionValueCorrelationIds)
				.setChosenOptionValueCorrelationId(selectedSkuOptionValue.getGuid());

		return itemSelectableOptionValuesDto;
	}
}
