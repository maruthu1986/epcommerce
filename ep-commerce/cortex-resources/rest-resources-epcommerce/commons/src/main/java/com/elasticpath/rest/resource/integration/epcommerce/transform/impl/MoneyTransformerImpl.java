/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.transform.impl;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.money.Money;
import com.elasticpath.money.MoneyFormatter;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Transforms a {@link Money} to a {@link CostEntity} and vice versa.
 */
@Component(property = AbstractDomainTransformer.DS_SERVICE_RANKING)
public class MoneyTransformerImpl extends AbstractDomainTransformer<Money, CostEntity> implements MoneyTransformer {

	@Reference
	private MoneyFormatter moneyFormatter;

	@Reference
	private ResourceOperationContext context;


	@Override
	public Money transformToDomain(final CostEntity costEntity, final Locale locale) {
		throw new UnsupportedOperationException("This operation is not implemented.");
	}

	@Override
	public CostEntity transformToEntity(final Money money, final Locale originalLocale) {
		Locale locale = originalLocale;
		if (locale == null) {
			locale = SubjectUtil.getLocale(context.getSubject());
		}

		return CostEntity.builder()
				.withAmount(money.getAmount())
				.withCurrency(money.getCurrency().getCurrencyCode())
				.withDisplay(moneyFormatter.formatCurrency(money, locale))
				.build();
	}
}
