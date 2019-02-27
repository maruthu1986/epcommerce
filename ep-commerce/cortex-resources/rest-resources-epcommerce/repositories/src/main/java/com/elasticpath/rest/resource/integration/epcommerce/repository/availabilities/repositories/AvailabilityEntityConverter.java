/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.availabilities.repositories;

import java.util.Locale;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;

import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.rest.definition.availabilities.AvailabilityEntity;
import com.elasticpath.rest.definition.base.DateEntity;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.transform.DateTransformer;

/**
 * Converter for AvailabilityEntity.
 */
@Singleton
@Named
public class AvailabilityEntityConverter implements Converter<Pair<StoreProduct, ProductSku>, AvailabilityEntity> {

	private final DateTransformer dateTransformer;

	private final ResourceOperationContext resourceOperationContext;

	/**
	 * Constructor.
	 *
	 * @param dateTransformer          dateTransformer
	 * @param resourceOperationContext resourceOperationContext
	 */
	@Inject
	public AvailabilityEntityConverter(
			@Named("dateTransformer")
			final DateTransformer dateTransformer,
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext) {
		this.dateTransformer = dateTransformer;
		this.resourceOperationContext = resourceOperationContext;
	}

	@Override
	public AvailabilityEntity convert(final Pair<StoreProduct, ProductSku> storeProductSku) {
		Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());
		StoreProduct storeProduct = storeProductSku.getFirst();
		String skuCode = storeProductSku.getSecond().getSkuCode();

		AvailabilityEntity.Builder builder = AvailabilityEntity.builder()
				.withState(Objects.toString(storeProduct.getSkuAvailability(skuCode), StringUtils.EMPTY));
		if (storeProduct.getExpectedReleaseDate() != null) {
			DateEntity releaseDate = dateTransformer.transformToEntity(storeProduct.getExpectedReleaseDate(), locale);
			builder.withReleaseDate(releaseDate);
		}

		return builder.build();
	}
}
