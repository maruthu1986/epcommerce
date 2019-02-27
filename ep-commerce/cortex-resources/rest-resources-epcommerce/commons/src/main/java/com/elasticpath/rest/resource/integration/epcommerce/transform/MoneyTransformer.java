/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.transform;

import java.util.Locale;

import com.elasticpath.money.Money;
import com.elasticpath.rest.definition.base.CostEntity;

/**
 * Transforms a {@link Money} to a {@link CostEntity} and vice versa.
 */
public interface MoneyTransformer {

	/**
	 * Transform to domain.
	 *
	 * @param costEntity the cost entity
	 * @return the money
	 */
	Money transformToDomain(CostEntity costEntity);

	/**
	 * Transform to domain.
	 *
	 * @param costEntity the cost entity
	 * @param locale the locale
	 * @return the money
	 */
	Money transformToDomain(CostEntity costEntity, Locale locale);

	/**
	 * Transform to entity.
	 *
	 * @param money the money
	 * @return the cost entity
	 */
	CostEntity transformToEntity(Money money);

	/**
	 * Transform to entity.
	 *
	 * @param money the money
	 * @param locale the locale
	 * @return the cost entity
	 */
	CostEntity transformToEntity(Money money, Locale locale);
}
