/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.totals.impl;

import java.util.Collections;
import java.util.Locale;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.springframework.core.convert.converter.Converter;

import com.elasticpath.money.Money;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;

/**
 * Converter for TotalEntity.
 */
@Singleton
@Named
public class TotalEntityConverter implements Converter<Money, TotalEntity> {

	private final MoneyTransformer moneyTransformer;

	private final ResourceOperationContext resourceOperationContext;

	/**
	 * Default constructor.
	 *
	 * @param moneyTransformer         the money transformer
	 * @param resourceOperationContext used to get the locale
	 */
	@Inject
	public TotalEntityConverter(@Named("moneyTransformer") final MoneyTransformer moneyTransformer,
								@Named("resourceOperationContext") final ResourceOperationContext resourceOperationContext) {
		this.moneyTransformer = moneyTransformer;
		this.resourceOperationContext = resourceOperationContext;
	}

	@Override
	public TotalEntity convert(final Money money) {
		Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());
		CostEntity cost = moneyTransformer.transformToEntity(money, locale);

		return TotalEntity.builder()
				.withCost(Collections.singleton(cost))
				.build();
	}
}
