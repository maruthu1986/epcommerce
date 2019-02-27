/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.transform;

import java.util.Date;
import java.util.Locale;

import com.elasticpath.rest.definition.base.DateEntity;

/**
 * Transforms {@link Date} to {@link DateEntity} and vice versa.
 */
public interface DateTransformer {

	/**
	 * Transform to domain.
	 *
	 * @param dateEntity the date entity
	 * @return the date
	 */
	Date transformToDomain(DateEntity dateEntity);

	/**
	 * Transform to domain.
	 *
	 * @param dateEntity the date entity
	 * @param locale the locale
	 * @return the date
	 */
	Date transformToDomain(DateEntity dateEntity, Locale locale);

	/**
	 * Transform to entity.
	 *
	 * @param date the date
	 * @return the date entity
	 */
	DateEntity transformToEntity(Date date);

	/**
	 * Transform to entity.
	 *
	 * @param date the date
	 * @param locale the locale
	 * @return the date entity
	 */
	DateEntity transformToEntity(Date date, Locale locale);
}
