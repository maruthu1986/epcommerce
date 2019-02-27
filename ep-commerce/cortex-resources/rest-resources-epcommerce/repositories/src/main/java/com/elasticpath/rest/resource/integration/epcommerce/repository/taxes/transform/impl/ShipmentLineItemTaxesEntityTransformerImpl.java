/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.taxes.transform.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.TaxJournalRecord;
import com.elasticpath.domain.shoppingcart.ShoppingItemTaxSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.money.MoneyFormatter;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.base.NamedCostEntity;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.taxes.transform.ShipmentLineItemTaxesEntityTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;

/**
 * Creates {@link TaxesEntity} for a single shipment line item.
 */
@Component
public class ShipmentLineItemTaxesEntityTransformerImpl implements ShipmentLineItemTaxesEntityTransformer {

	/**
	 * Default tax.
	 */
	static final BigDecimal DEFAULT_TAX = BigDecimal.ZERO.setScale(2);
	private ResourceOperationContext resourceOperationContext;
	private MoneyTransformer moneyTransformer;
	private MoneyFormatter moneyFormatter;
	private PricingSnapshotRepository pricingSnapshotRepository;

	/**
	 * Returns a new {@link TaxesEntity} based on the given {@link TaxJournalRecord}s.
	 *
	 * @param orderSku the line item's {@link OrderSku}
	 * @param taxJournalRecords a collection of {@link TaxJournalRecord}s detailing the per tax amount breakdown
	 * @return the {@link TaxesEntity}
	 */
	@Override
	public Single<TaxesEntity> transform(final OrderSku orderSku, final Collection<TaxJournalRecord> taxJournalRecords) {
		Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());

		return pricingSnapshotRepository.getTaxSnapshotForOrderSku(orderSku)
				.map(this::getBigDecimal)
				.map(amount -> Money.valueOf(amount, orderSku.getCurrency()))
				.map(taxMoney -> moneyTransformer.transformToEntity(taxMoney, locale))
				.map(total -> buildTaxesEntity(taxJournalRecords, total, locale));
	}

	/**
	 * Get tax amount.
	 *
	 * @param taxSnapshot tax snapshot
	 * @return tax amount
	 */
	protected BigDecimal getBigDecimal(final ShoppingItemTaxSnapshot taxSnapshot) {
		BigDecimal taxAmount = taxSnapshot.getTaxAmount();
		return taxAmount == null ? DEFAULT_TAX : taxAmount;
	}

	/**
	 * Builds taxes entity.
	 *
	 * @param taxJournalRecords journal records
	 * @param total total cost
	 * @param locale locale
	 * @return taxes entity
	 */
	protected TaxesEntity buildTaxesEntity(final Collection<TaxJournalRecord> taxJournalRecords, final CostEntity total, final Locale locale) {
		List<NamedCostEntity> taxes = buildNamedCostEntityCollection(taxJournalRecords, locale);

		return TaxesEntity.builder()
				.withTotal(total)
				.withCost(taxes)
				.build();
	}

	/**
	 * Get the cost entities.
	 *
	 * @param taxRecords records
	 * @param locale locale
	 * @return list of cost entities
	 */
	protected List<NamedCostEntity> buildNamedCostEntityCollection(final Collection<TaxJournalRecord> taxRecords, final Locale locale) {
		return taxRecords.stream().map(record -> createCostEntity(locale, record)).collect(Collectors.toList());
	}

	/**
	 * Builds Named Cost Entity.
	 * @param locale locale
	 * @param record tax record
	 * @return named cost entity
	 */
	protected NamedCostEntity createCostEntity(final Locale locale, final TaxJournalRecord record) {
		BigDecimal taxAmount = record.getTaxAmount();
		String currency = record.getCurrency();

		return NamedCostEntity.builder()
				.withAmount(taxAmount)
				.withCurrency(currency)
				.withDisplay(moneyFormatter.formatCurrency(Currency.getInstance(currency), taxAmount, locale))
				.withTitle(record.getTaxName())
				.build();
	}

	@Reference
	public void setResourceOperationContext(final ResourceOperationContext resourceOperationContext) {
		this.resourceOperationContext = resourceOperationContext;
	}

	@Reference
	public void setMoneyTransformer(final MoneyTransformer moneyTransformer) {
		this.moneyTransformer = moneyTransformer;
	}

	@Reference
	public void setMoneyFormatter(final MoneyFormatter moneyFormatter) {
		this.moneyFormatter = moneyFormatter;
	}

	@Reference
	public void setPricingSnapshotRepository(final PricingSnapshotRepository pricingSnapshotRepository) {
		this.pricingSnapshotRepository = pricingSnapshotRepository;
	}
}
