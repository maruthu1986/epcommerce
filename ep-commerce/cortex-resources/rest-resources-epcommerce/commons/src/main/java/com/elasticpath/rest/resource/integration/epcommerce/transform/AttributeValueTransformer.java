/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.transform;

import java.util.Locale;

import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.rest.definition.base.DetailsEntity;

/**
 * Transforms a {@link AttributeValue} into a {@link DetailsEntity}, and vice-versa.
 */
public interface AttributeValueTransformer {

	/**
	 * Transform to domain.
	 *
	 * @param detailsEntity the details entity
	 * @return the attribute value
	 */
	AttributeValue transformToDomain(DetailsEntity detailsEntity);

	/**
	 * Transform to domain.
	 *
	 * @param detailsEntity the details entity
	 * @param locale the locale
	 * @return the attribute value
	 */
	AttributeValue transformToDomain(DetailsEntity detailsEntity, Locale locale);

	/**
	 * Transform to entity.
	 *
	 * @param attributeValue the attribute value
	 * @return the details entity
	 */
	DetailsEntity transformToEntity(AttributeValue attributeValue);

	/**
	 * Transform to entity.
	 *
	 * @param attributeValue the attribute value
	 * @param locale the locale
	 * @return the details entity
	 */
	DetailsEntity transformToEntity(AttributeValue attributeValue, Locale locale);
}