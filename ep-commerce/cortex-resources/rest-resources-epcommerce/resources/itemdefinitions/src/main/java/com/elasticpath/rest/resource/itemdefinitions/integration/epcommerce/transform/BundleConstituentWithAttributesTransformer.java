/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.integration.epcommerce.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.rest.definition.base.DetailsEntity;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionComponentEntity;
import com.elasticpath.rest.resource.integration.epcommerce.transform.AttributeValueTransformer;
import com.elasticpath.rest.resource.itemdefinitions.integration.epcommerce.core.domain.wrapper.BundleConstituentWithAttributesWrapper;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;
import com.elasticpath.rest.util.collection.CollectionUtil;

/**
 * Transforms a {@link BundleConstituentWithAttributesWrapper} into a {@link ItemDefinitionComponentEntity}, and vice versa.
 */
@Singleton
@Named("bundleConstituentWithAttributesTransformer")
public class BundleConstituentWithAttributesTransformer
		extends AbstractDomainTransformer<BundleConstituentWithAttributesWrapper, ItemDefinitionComponentEntity> {

	private final AttributeValueTransformer attributeValueTransformer;

	/**
	 * Instantiates a new item definition component domain transformer.
	 *
	 * @param attributeValueTransformer the attribute value transformer
	 */
	@Inject
	BundleConstituentWithAttributesTransformer(
			@Named("attributeValueTransformer")
			final AttributeValueTransformer attributeValueTransformer) {

		this.attributeValueTransformer = attributeValueTransformer;
	}

	@Override
	public BundleConstituentWithAttributesWrapper transformToDomain(
			final ItemDefinitionComponentEntity resourceEntity, final Locale locale) {
		throw new UnsupportedOperationException("This operation is not implemented.");
	}

	@Override
	public ItemDefinitionComponentEntity transformToEntity(final BundleConstituentWithAttributesWrapper bundleConstituentWithAttributesWrapper,
															final Locale locale) {


		BundleConstituent bundleConstituent = bundleConstituentWithAttributesWrapper.getBundleConstituent();
		String displayName = bundleConstituent.getConstituent().getProduct().getDisplayName(locale);

		ItemDefinitionComponentEntity entity = ItemDefinitionComponentEntity.builder()
				.withDetails(getDetails(bundleConstituentWithAttributesWrapper.getAttributes(), locale))
				.withDisplayName(displayName)
				.withQuantity(bundleConstituent.getQuantity())
				.withStandaloneItemId(bundleConstituentWithAttributesWrapper.getStandaloneItemId()).build();

		return entity;
	}

	private Collection<DetailsEntity> getDetails(final Collection<AttributeValue> attributeValues, final Locale locale) {
		final Collection<DetailsEntity> result;

		if (CollectionUtil.isEmpty(attributeValues)) {
			result = Collections.emptySet();
		} else {
			result = createDetailsEntities(attributeValues, locale);
		}

		return result;
	}

	private Collection<DetailsEntity> createDetailsEntities(final Collection<AttributeValue> attributeValues, final Locale locale) {
		Collection<DetailsEntity> attributes = new ArrayList<>(attributeValues.size());

		for (AttributeValue attributeValue : attributeValues) {
			DetailsEntity detailsEntity = attributeValueTransformer.transformToEntity(attributeValue, locale);
			if (detailsEntity != null) {
				attributes.add(detailsEntity);
			}
		}

		return attributes;
	}
}
