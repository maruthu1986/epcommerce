/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.transform;

import java.util.Locale;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionValueEntity;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Transform between a {@link SkuOptionValue} and {@link PurchaseLineItemOptionValueEntity}, and vice versa.
 */
@Singleton
@Named("skuOptionValueTransformer")
public class SkuOptionValueTransformer extends AbstractDomainTransformer<SkuOptionValue, PurchaseLineItemOptionValueEntity> {

	@Override
	public SkuOptionValue transformToDomain(
			final PurchaseLineItemOptionValueEntity purchaseLineItemOptionValueEntity, final Locale locale) {
		throw new UnsupportedOperationException("This operation is not implemented.");
	}

	@Override
	public PurchaseLineItemOptionValueEntity transformToEntity(final SkuOptionValue skuOptionValue, final Locale locale) {

		return PurchaseLineItemOptionValueEntity.builder()
				.withName(skuOptionValue.getOptionValueKey())
				.withDisplayName(skuOptionValue.getDisplayName(locale, true))
				.build();
	}

}
