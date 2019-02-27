/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.integration.epcommerce.repository.taxes.transform.impl;

import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.Locale;

import com.google.common.collect.ImmutableList;
import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.TaxJournalRecord;
import com.elasticpath.domain.order.impl.OrderSkuImpl;
import com.elasticpath.domain.order.impl.TaxJournalRecordImpl;
import com.elasticpath.domain.shoppingcart.ShoppingItemTaxSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.money.MoneyFormatter;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.base.NamedCostEntity;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.attribute.LocaleSubjectAttribute;
import com.elasticpath.rest.identity.attribute.SubjectAttribute;
import com.elasticpath.rest.identity.type.ImmutableSubject;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.integration.epcommerce.transform.impl.MoneyTransformerImpl;
import com.elasticpath.rest.resource.shiro.impl.ShiroResourceOperationContext;

@RunWith(MockitoJUnitRunner.class)
public class ShipmentLineItemTaxesTransformerTest {

	private static final Locale LOCALE = Locale.CANADA;
	private static final Currency CURRENCY = Currency.getInstance(LOCALE);
	private static final String CURRENCY_CODE = CURRENCY.getCurrencyCode();
	private static final BigDecimal AMOUNT = ShipmentLineItemTaxesEntityTransformerImpl.DEFAULT_TAX;
	private static final String TAX_NAME = "tax name";
	private static final int QUANTITY = 10;

	@Mock
	private ShiroResourceOperationContext resourceOperationContext;

	@Mock
	private PricingSnapshotRepository pricingSnapshotRepository;

	@SuppressWarnings({"PMD.UnusedPrivateField"})
	@Mock
	private MoneyFormatter formatter;

	@InjectMocks
	private MoneyTransformerImpl moneyTransformer;

	@InjectMocks
	private ShipmentLineItemTaxesEntityTransformerImpl transformer;

	@Before
	public void setUp() {
		transformer.setMoneyTransformer(moneyTransformer);
	}

	@Test
	public void transformWithSuccess() {

		setUpLocale();

		Collection<TaxJournalRecord> taxJournalRecords = new ArrayList<TaxJournalRecord>() {
			{
				TaxJournalRecord record = new TaxJournalRecordImpl();
				record.setCurrency(CURRENCY_CODE);
				record.setTaxAmount(AMOUNT);
				record.setTaxName(TAX_NAME);
				add(record);
			}
		};

		OrderSku orderSku = createOrderSku();
		ShoppingItemTaxSnapshot taxSnapshot = mock(ShoppingItemTaxSnapshot.class);
		when(pricingSnapshotRepository.getTaxSnapshotForOrderSku(orderSku)).thenReturn(Single.just(taxSnapshot));

		transformer.transform(orderSku, taxJournalRecords)
				.test()
				.assertValue(createExpectedEntity());
	}

	private TaxesEntity createExpectedEntity() {
		CostEntity total = moneyTransformer.transformToEntity(Money.valueOf(AMOUNT, CURRENCY), LOCALE);

		Iterable<NamedCostEntity> taxes = ImmutableList.of(NamedCostEntity.builder()
				.withAmount(AMOUNT)
				.withCurrency(CURRENCY_CODE)
				.withDisplay(total.getDisplay())
				.withTitle(TAX_NAME)
				.build());
		return TaxesEntity.builder()
				.withTotal(total)
				.withCost(taxes)
				.build();
	}

	private void setUpLocale() {
		SubjectAttribute attribute = new LocaleSubjectAttribute(LOCALE.toLanguageTag(), LOCALE);
		Subject subject = new ImmutableSubject(emptyList(), singleton(attribute));
		when(resourceOperationContext.getSubject()).thenReturn(subject);
	}

	private OrderSku createOrderSku() {
		OrderSku orderSku = new OrderSkuImpl();
		PriceImpl price = new PriceImpl();
		price.setCurrency(CURRENCY);
		orderSku.setPrice(QUANTITY, price);
		return orderSku;
	}
}
