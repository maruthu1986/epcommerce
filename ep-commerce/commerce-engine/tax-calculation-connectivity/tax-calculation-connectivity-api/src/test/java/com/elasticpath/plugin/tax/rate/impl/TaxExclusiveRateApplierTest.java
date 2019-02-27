/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.rate.impl;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.plugin.tax.domain.impl.MutableTaxedItem;
import com.elasticpath.plugin.tax.domain.impl.TaxableItemImpl;
import com.elasticpath.plugin.tax.rate.dto.AppliedTaxValue;
import com.elasticpath.plugin.tax.rate.dto.MutableTaxRateDescriptor;

/**
 * Test cases for {@link com.elasticpath.plugin.tax.rate.impl.TaxExclusiveRateApplier}.
 */
@RunWith(MockitoJUnitRunner.class)
public class TaxExclusiveRateApplierTest {

	private static final BigDecimal TWO_TWENTY = new BigDecimal("2.20");
	private static final BigDecimal TWO_NINETEEN = new BigDecimal("2.19");
	private static final BigDecimal TWENTY_PERCENT_TAX = new BigDecimal("0.20");
	private static final BigDecimal TEN_NINETY_NINE = new BigDecimal("10.99");
	private static final BigDecimal TEN_NINETY_SEVEN = new BigDecimal("10.97");
	private static final BigDecimal TEN_NINETY_FIVE = new BigDecimal("10.95");

	private final MutableTaxedItem taxedItem = new MutableTaxedItem();

	@Mock
	private MutableTaxRateDescriptor taxRateDescriptor;
	@Mock
	private TaxableItemImpl taxableItem;

	@InjectMocks
	private TaxExclusiveRateApplier applier;

	@Before
	public void setup() {
		when(taxRateDescriptor.getTaxRateValue()).thenReturn(TWENTY_PERCENT_TAX);

		taxedItem.setTaxableItem(taxableItem);
	}
	@Test
	public void testCalculateExclusiveTax() {
		assertThat(applier.calculateTax(TEN_NINETY_NINE, TWENTY_PERCENT_TAX)).isEqualTo(TWO_TWENTY);
		assertThat(applier.calculateTax(TEN_NINETY_SEVEN, TWENTY_PERCENT_TAX)).isEqualTo(TWO_NINETEEN);
		assertThat(applier.calculateTax(TEN_NINETY_FIVE, TWENTY_PERCENT_TAX)).isEqualTo(TWO_NINETEEN);
		assertThat(applier.calculateTax(BigDecimal.ZERO, TWENTY_PERCENT_TAX)).isEqualTo(BigDecimal.valueOf(0, 2));
		assertThat(applier.calculateTax(TEN_NINETY_NINE, BigDecimal.ZERO)).isEqualTo(BigDecimal.valueOf(0, 2));
	}

	@Test
	public void testExclusiveTaxRateAppliedCorrectly() {
		when(taxableItem.getTaxablePrice()).thenReturn(TEN_NINETY_NINE);
		when(taxableItem.getPrice()).thenReturn(TEN_NINETY_NINE);

		AppliedTaxValue appliedTaxValue = applier.applyTaxRate(taxRateDescriptor, taxedItem);

		assertThat(appliedTaxValue.getBeforeTaxAmount()).isEqualTo(TEN_NINETY_NINE);
		assertThat(appliedTaxValue.getTaxAmount()).isEqualTo(TWO_TWENTY);
		assertThat(appliedTaxValue.getIncludedTaxAmount()).isEqualTo(BigDecimal.ZERO);
	}


}
