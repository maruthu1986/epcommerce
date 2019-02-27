/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.integration.epcommerce.repository.taxes.converters;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.Locale;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.springframework.core.convert.converter.Converter;

import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderTaxValue;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.money.Money;
import com.elasticpath.money.MoneyFormatter;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.base.NamedCostEntity;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;

/**
 * Shipment to Tax Entity Converter.
 */
@Singleton
@Named("shipmentToTaxesEntityConverter")
public class ShipmentToTaxEntityConverter implements Converter<PhysicalOrderShipment, TaxesEntity> {

	private static final BigDecimal DEFAULT_TAX = BigDecimal.ZERO.setScale(2);
	private final ResourceOperationContext resourceOperationContext;
	private final MoneyTransformer moneyTransformer;
	private final MoneyFormatter moneyFormatter;

	/**
	 * Constructor.
	 *
	 * @param moneyTransformer the money transformer
	 * @param moneyFormatter   the money formatter
	 * @param resourceOperationContext context for resource operation
	 */
	@Inject
	public ShipmentToTaxEntityConverter(
			@Named("resourceOperationContext") final ResourceOperationContext resourceOperationContext,
			@Named("moneyTransformer") final MoneyTransformer moneyTransformer,
			@Named("moneyFormatter") final MoneyFormatter moneyFormatter) {

		this.resourceOperationContext = resourceOperationContext;
		this.moneyTransformer = moneyTransformer;
		this.moneyFormatter = moneyFormatter;
	}

	@Override
	public TaxesEntity convert(final PhysicalOrderShipment physicalOrderShipment) {

		Money taxMoney = physicalOrderShipment.getTotalTaxMoney();

		if (taxMoney == null) {
			taxMoney = Money.valueOf(DEFAULT_TAX, physicalOrderShipment.getOrder().getCurrency());
		}

		Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());

		CostEntity total = moneyTransformer.transformToEntity(taxMoney, locale);
		Collection<NamedCostEntity> taxes = buildCost(physicalOrderShipment, locale);

		return TaxesEntity.builder()
				.withTotal(total)
				.withCost(taxes)
				.build();

	}

	private Collection<NamedCostEntity> buildCost(final OrderShipment orderShipment, final Locale locale) {
		Set<OrderTaxValue> shipmentTaxes = orderShipment.getShipmentTaxes();
		Collection<NamedCostEntity> taxEntities = new ArrayList<>(shipmentTaxes.size());
		for (OrderTaxValue taxValue : shipmentTaxes) {
			Currency currency = orderShipment.getOrder().getCurrency();
			BigDecimal amount = taxValue.getTaxValue();
			NamedCostEntity taxEntity = NamedCostEntity.builder()
					.withAmount(amount)
					.withCurrency(currency.getCurrencyCode())
					.withDisplay(moneyFormatter.formatCurrency(currency, amount, locale))
					.withTitle(taxValue.getTaxCategoryDisplayName())
					.build();
			taxEntities.add(taxEntity);
		}
		return taxEntities;
	}
}
