/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.rate.impl;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

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
import com.elasticpath.plugin.tax.rate.dto.MutableTaxRateDescriptorResult;

/**
 * Test cases for tax inclusive calculation.
 */
@RunWith(MockitoJUnitRunner.class)
public class TaxInclusiveRateApplierTest {

	private static final BigDecimal SIXTEEN_SIXTY_SEVEN = new BigDecimal("16.67");
	private static final BigDecimal SIXTEEN_SIXTY_SIX = new BigDecimal("16.66");
	private static final BigDecimal TAX_TWENTY_PERCENT = new BigDecimal("0.20");
	private static final BigDecimal NINETY_NINE_NINETY_NINE = new BigDecimal("99.99");
	private static final BigDecimal NINETY_NINE_NINETY_SIX = new BigDecimal("99.96");
	private static final BigDecimal NINETY_NINE_NINETY_EIGHT = new BigDecimal("99.98");

	private final MutableTaxedItem taxedItem = new MutableTaxedItem();

	@Mock
	private MutableTaxRateDescriptorResult taxRateDescriptorResult;
	@Mock
	private MutableTaxRateDescriptor taxRateDescriptor;
	@Mock
	private TaxableItemImpl taxableItem;

	@InjectMocks
	private TaxInclusiveRateApplier applier;

	@Before
	public void setup() {
		when(taxRateDescriptorResult.getSumOfTaxRates()).thenReturn(TAX_TWENTY_PERCENT);
		when(taxRateDescriptor.getTaxRateValue()).thenReturn(TAX_TWENTY_PERCENT);
		when(taxRateDescriptor.getTaxRateDescriptorResult()).thenReturn(taxRateDescriptorResult);

		taxedItem.setTaxableItem(taxableItem);
	}

	/**
	 * Test case for tax inclusive calculation.
	 */
	@Test
	public void testInclusiveTax() {
		assertThat(applier.getTaxIncludedInPrice(TAX_TWENTY_PERCENT, TAX_TWENTY_PERCENT, NINETY_NINE_NINETY_NINE)).isEqualTo(SIXTEEN_SIXTY_SEVEN);
		assertThat(applier.getTaxIncludedInPrice(TAX_TWENTY_PERCENT, TAX_TWENTY_PERCENT, NINETY_NINE_NINETY_SIX)).isEqualTo(SIXTEEN_SIXTY_SIX);
		assertThat(applier.getTaxIncludedInPrice(TAX_TWENTY_PERCENT, TAX_TWENTY_PERCENT, NINETY_NINE_NINETY_EIGHT)).isEqualTo(SIXTEEN_SIXTY_SIX);
	}

	@Test
	public void verifyCorrectTaxAndTotalPriceWhenRoundingTaxUp() {
		//  Total price:	99.99
		//  Taxes:			16.67		-->		0.2 * 99.98 / 1.2 = 16.6666
		//  Total w/o tax:	83.32		-->		99.99 - 16.67 = 83.32

		when(taxableItem.getTaxablePrice()).thenReturn(NINETY_NINE_NINETY_NINE);
		when(taxableItem.getPrice()).thenReturn(NINETY_NINE_NINETY_NINE);

		AppliedTaxValue appliedTaxValue = applier.applyTaxRate(taxRateDescriptor, taxedItem);

		assertThat(appliedTaxValue.getBeforeTaxAmount()).isEqualTo(NINETY_NINE_NINETY_NINE.subtract(SIXTEEN_SIXTY_SEVEN));
		assertThat(appliedTaxValue.getTaxAmount()).isEqualTo(SIXTEEN_SIXTY_SEVEN);
		assertThat(appliedTaxValue.getIncludedTaxAmount()).isEqualTo(SIXTEEN_SIXTY_SEVEN);
		assertThat(appliedTaxValue.getBeforeTaxAmount().add(appliedTaxValue.getTaxAmount())).isEqualTo(NINETY_NINE_NINETY_NINE);
	}

	@Test
	public void verifyCorrectTaxAndTotalPriceWhenNoTaxRounding() {
		//  Total price:	99.96
		//  Taxes:			16.66		-->		0.2 * 99.96 / 1.2 = 16.66
		//  Total w/o tax:	83.30		-->		99.96 - 16.67 = 83.30

		when(taxableItem.getTaxablePrice()).thenReturn(NINETY_NINE_NINETY_SIX);
		when(taxableItem.getPrice()).thenReturn(NINETY_NINE_NINETY_SIX);

		AppliedTaxValue appliedTaxValue = applier.applyTaxRate(taxRateDescriptor, taxedItem);

		assertThat(appliedTaxValue.getBeforeTaxAmount()).isEqualTo(NINETY_NINE_NINETY_SIX.subtract(SIXTEEN_SIXTY_SIX));
		assertThat(appliedTaxValue.getTaxAmount()).isEqualTo(SIXTEEN_SIXTY_SIX);
		assertThat(appliedTaxValue.getIncludedTaxAmount()).isEqualTo(SIXTEEN_SIXTY_SIX);
		assertThat(appliedTaxValue.getBeforeTaxAmount().add(appliedTaxValue.getTaxAmount())).isEqualTo(NINETY_NINE_NINETY_SIX);
	}

	@Test
	public void verifyCorrectTaxAndTotalPriceWhenRoundingTaxDown() {
		//  Total price:	99.98
		//  Taxes:			16.66		-->		0.2 * 99.98 / 1.2 = 16.6633
		//  Total w/o tax:	83.32		-->		99.99 - 16.66 = 83.32

		when(taxableItem.getTaxablePrice()).thenReturn(NINETY_NINE_NINETY_EIGHT);
		when(taxableItem.getPrice()).thenReturn(NINETY_NINE_NINETY_EIGHT);

		AppliedTaxValue appliedTaxValue = applier.applyTaxRate(taxRateDescriptor, taxedItem);

		assertThat(appliedTaxValue.getBeforeTaxAmount()).isEqualTo(NINETY_NINE_NINETY_EIGHT.subtract(SIXTEEN_SIXTY_SIX));
		assertThat(appliedTaxValue.getTaxAmount()).isEqualTo(SIXTEEN_SIXTY_SIX);
		assertThat(appliedTaxValue.getIncludedTaxAmount()).isEqualTo(SIXTEEN_SIXTY_SIX);
		assertThat(appliedTaxValue.getBeforeTaxAmount().add(appliedTaxValue.getTaxAmount())).isEqualTo(NINETY_NINE_NINETY_EIGHT);
	}

}
