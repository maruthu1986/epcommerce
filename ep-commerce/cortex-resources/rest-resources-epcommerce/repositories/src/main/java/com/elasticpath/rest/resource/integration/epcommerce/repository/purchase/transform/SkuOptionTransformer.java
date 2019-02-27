/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.transform;

import java.util.Locale;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionEntity;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Transforms a {@link SkuOptionValue}, and its {@link SkuOption}, into {@link PurchaseLineItemOptionEntity}, and vice versa.
 */
@Singleton
@Named("skuOptionTransformer")
public class SkuOptionTransformer extends AbstractDomainTransformer<SkuOptionValue, PurchaseLineItemOptionEntity> {

	@Override
	public SkuOptionValue transformToDomain(final PurchaseLineItemOptionEntity purchaseLineItemOptionEntity, final Locale locale) {
		throw new UnsupportedOperationException("This operation is not implemented.");
	}

	@Override
	public PurchaseLineItemOptionEntity transformToEntity(final SkuOptionValue skuOptionValue, final Locale locale) {
		SkuOption skuOption = skuOptionValue.getSkuOption();

		return PurchaseLineItemOptionEntity.builder()
				.withName(skuOption.getOptionKey())
				.withDisplayName(skuOption.getDisplayName(locale, true))
				.withSelectedValueId(skuOptionValue.getOptionValueKey())
				.withOptionId(skuOption.getGuid())
				.build();
	}
}
