/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */

package com.elasticpath.domain.shipping.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import static com.elasticpath.commons.constants.EpShippingContextIdNames.SHIPPING_COST_CALCULATION_PARAMETER;
import static com.elasticpath.domain.shipping.ShippingCostCalculationParametersEnum.COST_PER_UNIT_WEIGHT;
import static com.elasticpath.domain.shipping.ShippingCostCalculationParametersEnum.FIXED_BASE;
import static com.elasticpath.domain.shipping.impl.ShippingCostTestDataFactory.aCostCalculationParam;
import static com.elasticpath.domain.shipping.impl.ShippingCostTestDataFactory.someCalculationParams;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.impl.ElasticPathImpl;
import com.elasticpath.domain.shipping.ShippingCostCalculationMethod;
import com.elasticpath.domain.shipping.ShippingCostCalculationParameter;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.impl.ShippableItemImpl;

/**
 * Test cases for <code>FixedBaseAndCostPerUnitWeightMethodImpl</code>.
 */
@SuppressWarnings({"PMD.TooManyStaticImports", "PMD.DontUseElasticPathImplGetInstance"})
@RunWith(MockitoJUnitRunner.class)
public class FixedBaseAndCostPerUnitWeightMethodImplTest {

	private static final Currency CURRENCY_CAD = Currency.getInstance("CAD");
	private static final Currency CURRENCY_USD = Currency.getInstance("USD");

	private static final BigDecimal FIXED_BASE_VAL = new BigDecimal("5.0");

	private static final BigDecimal COST_PER_UNIT_WEIGHT_VAL = new BigDecimal("5.0");

	private static final int QTY_1 = 1;

	private FixedBaseAndCostPerUnitWeightMethodImpl shippingCostCalculationMethod;

	@Mock
	private BeanFactory beanFactory;
	@Mock
	private ProductSkuLookup productSkuLookup;

	/**
	 * Prepare for each test.
	 */
	@Before
	public void setUp() {
		((ElasticPathImpl) ElasticPathImpl.getInstance()).setBeanFactory(beanFactory);
		when(beanFactory.getBean(SHIPPING_COST_CALCULATION_PARAMETER))
				.thenAnswer(invocation -> new ShippingCostCalculationParameterImpl());
		shippingCostCalculationMethod = new FixedBaseAndCostPerUnitWeightMethodImpl();
		this.setDefaultParameterSet(this.shippingCostCalculationMethod);
	}

	@After
	public void tearDown() {
		((ElasticPathImpl) ElasticPathImpl.getInstance()).setBeanFactory(null);
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.FixedBaseAndCostPerUnitWeightMethodImpl.getDisplayText()'.
	 */
	@Test
	public void testGetDisplayText() {
		assertThat(this.shippingCostCalculationMethod.getDisplayText()).isNotNull();
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.FixedBaseAndCostPerUnitWeightMethodImpl.getMethodType()'.
	 */
	@Test
	public void testGetType() {
		assertThat(this.shippingCostCalculationMethod.getType()).isEqualTo(this.shippingCostCalculationMethod.getMethodType());
	}

	/**
	 * Test that two shippable items have the right price applied once.
	 */
	@Test
	public void testCalculateShippingCostThreeShippableItems() {

		List<ShippableItem> items = items(
				shippableItem(QTY_1),
				shippableItem(QTY_1),
				shippableItem(QTY_1));

		// Cost: $5.00 + ($5.00 * 3) = $20.00
		assertCost("20.00", items, CURRENCY_CAD);
	}

	/**
	 * Test that when calculating the shipping cost, the fixed base and the cost per unit weight parameter
	 * values are retrieved for the given currency.
	 */
	@Test
	public void testParameterRetrievedForGivenCurrency() {
		BigDecimal fixedBaseUsd = BigDecimal.ONE;
		BigDecimal costPerUnitWeightUsd = BigDecimal.ONE;

		BigDecimal fixedBaseCad = BigDecimal.TEN;
		BigDecimal costPerUnitWeightCad = BigDecimal.TEN;

		this.shippingCostCalculationMethod.setParameters(
				someCalculationParams(
						aCostCalculationParam(FIXED_BASE, fixedBaseUsd, CURRENCY_USD),
						aCostCalculationParam(COST_PER_UNIT_WEIGHT, costPerUnitWeightUsd, CURRENCY_USD),

						aCostCalculationParam(FIXED_BASE, fixedBaseCad, CURRENCY_CAD),
						aCostCalculationParam(COST_PER_UNIT_WEIGHT, costPerUnitWeightCad, CURRENCY_CAD)));

		List<ShippableItem> items = items(
				shippableItem(QTY_1),
				shippableItem(QTY_1),
				shippableItem(QTY_1));

		assertCost("40.00", items, CURRENCY_CAD);
		assertCost("4.00", items, CURRENCY_USD);
	}

	private void assertCost(final String expectedCost, final List<ShippableItem> items, final Currency currency) {
		assertThat(shippingCostCalculationMethod.calculateShippingCost(items, null, currency, productSkuLookup).getAmount())
				.isEqualTo(new BigDecimal(expectedCost));
	}

	/**
	 * Test that no items incurs no shipping cost.
	 */
	@Test
	public void testCalculateShippingCostNoItems() {
		assertCost("0.00", items(), CURRENCY_CAD);
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.FixedBaseAndCostPerUnitWeightMethodImpl.getDefaultParameters(currencyList)'.
	 */
	@Test
	public void testGetDefaultParametersSet() {
		List<Currency> currencyList = new ArrayList<>();
		currencyList.add(CURRENCY_USD);
		currencyList.add(CURRENCY_CAD);
		List<ShippingCostCalculationParameter> calcParams = shippingCostCalculationMethod.getDefaultParameters(currencyList);

		assertThat(calcParams).extracting(ShippingCostCalculationParameter::getCurrency)
			.containsExactlyInAnyOrder(CURRENCY_USD, CURRENCY_CAD, CURRENCY_USD, CURRENCY_CAD);

	}

	private void setDefaultParameterSet(final ShippingCostCalculationMethod shippingCostCalculationMethod) {
		shippingCostCalculationMethod.setParameters(
				someCalculationParams(
						aCostCalculationParam(FIXED_BASE, FIXED_BASE_VAL, CURRENCY_CAD),
						aCostCalculationParam(COST_PER_UNIT_WEIGHT, COST_PER_UNIT_WEIGHT_VAL, CURRENCY_CAD)));
	}

	private List<ShippableItem> items(final ShippableItem... items) {
		return Arrays.asList(items);
	}

	private ShippableItem shippableItem(final int quantity) {

		ShippableItemImpl item = new ShippableItemImpl();
		item.setQuantity(quantity);
		item.setWeight(BigDecimal.ONE);

		return item;
	}

}
