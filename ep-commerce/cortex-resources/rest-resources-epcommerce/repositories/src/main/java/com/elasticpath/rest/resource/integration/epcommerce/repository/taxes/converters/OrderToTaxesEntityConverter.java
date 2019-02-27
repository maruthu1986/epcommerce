/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.integration.epcommerce.repository.taxes.converters;

import static com.elasticpath.rest.ResourceTypeFactory.adaptResourceEntity;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.springframework.core.convert.converter.Converter;

import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.money.Money;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.base.NamedCostEntity;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;
import com.elasticpath.service.tax.TaxCalculationResult;

/**
 * Order to Taxes Entity Converter.
 */
@Singleton
@Named("orderToTaxesEntityConverter")
public class OrderToTaxesEntityConverter implements Converter<TaxCalculationResult, TaxesEntity> {

	private final ResourceOperationContext resourceOperationContext;
	private final MoneyTransformer moneyTransformer;

	/**
	 * Constructor.
	 *
	 * @param resourceOperationContext context for resource operation
	 * @param moneyTransformer money transformer
	 */
	@Inject
	public OrderToTaxesEntityConverter(
			@Named("resourceOperationContext") final ResourceOperationContext resourceOperationContext,
			@Named("moneyTransformer") final MoneyTransformer moneyTransformer) {

		this.resourceOperationContext = resourceOperationContext;
		this.moneyTransformer = moneyTransformer;
	}

	@Override
	public TaxesEntity convert(final TaxCalculationResult calculationResult) {
		Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());

		return transformToEntity(calculationResult, locale);
	}

	/**
	 * Transforms calculation for the tax to the taxes entity.
	 *
	 * @param taxCalculationResult tax calculation
	 * @param locale locale
	 * @return taxes entity
	 */
	protected TaxesEntity transformToEntity(final TaxCalculationResult taxCalculationResult, final Locale locale) {
		CostEntity totalTax = moneyTransformer.transformToEntity(taxCalculationResult.getTotalTaxes(), locale);

		Collection<NamedCostEntity> taxEntities = taxCalculationResult.getTaxMap().entrySet().stream()
				.map(tax -> createTaxEntry(tax, locale))
				.collect(Collectors.toList());

		return TaxesEntity.builder()
				.withTotal(totalTax)
				.withCost(taxEntities)
				.build();
	}

	/**
	 * Creates tax entry.
	 *
	 * @param tax  map entry from tax category to money
	 * @param locale locale
	 * @return named cost entity
	 */
	protected NamedCostEntity createTaxEntry(final Map.Entry<TaxCategory, Money> tax, final Locale locale) {
		CostEntity costEntity = moneyTransformer.transformToEntity(tax.getValue(), locale);

		return NamedCostEntity.builderFrom(adaptResourceEntity(costEntity, NamedCostEntity.class))
				.withTitle(tax.getKey().getName())
				.build();
	}
}
