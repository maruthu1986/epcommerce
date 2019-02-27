/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */

package com.elasticpath.domain.shipping.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import static com.elasticpath.commons.constants.EpShippingContextIdNames.SHIPPING_COST_CALCULATION_PARAMETER;
import static com.elasticpath.domain.shipping.impl.ShippingAsserts.assertShippingCost;
import static com.elasticpath.domain.shipping.impl.ShippingCostTestDataFactory.aCostCalculationParam;
import static com.elasticpath.domain.shipping.impl.ShippingCostTestDataFactory.someCalculationParams;
import static com.elasticpath.test.factory.ShippableItemContainerStubBuilder.aContainer;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import com.elasticpath.domain.shipping.ShippingCostCalculationParametersEnum;
import com.elasticpath.money.Money;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.ShippableItemContainer;
import com.elasticpath.test.factory.ShippableItemContainerStubBuilder;

/**
 * Test cases for <code>FixedBaseAndOrderTotalPercentageMethodImpl</code>.
 */
@SuppressWarnings({"PMD.TooManyStaticImports", "PMD.DontUseElasticPathImplGetInstance"})
@RunWith(MockitoJUnitRunner.class)
public class FixedBaseAndOrderTotalPercentageMethodImplTest {

	private static final Currency CURRENCY_CAD = Currency.getInstance("CAD");
	private static final Currency CURRENCY_USD = Currency.getInstance("USD");

	private static final BigDecimal FIXED_BASE = new BigDecimal("10.0");

	private static final BigDecimal PERCENTAGE = new BigDecimal("10.0");

	private static final Money ZERO_DOLLARS_CAD = Money.valueOf(BigDecimal.ZERO, CURRENCY_CAD);

	private ShippingCostCalculationMethod shippingCostCalculationMethod;

	@Mock
	private BeanFactory beanFactory;
	@Mock
	private ProductSkuLookup productSkuLookup;

	/**
	 * Prepare for each test.
	 */
	@Before
	public void setUp() {
		this.shippingCostCalculationMethod = new FixedBaseAndOrderTotalPercentageMethodImpl();

		shippingCostCalculationMethod.setParameters(
				someCalculationParams(
						aCostCalculationParam(ShippingCostCalculationParametersEnum.FIXED_BASE, FIXED_BASE, CURRENCY_CAD),
						aCostCalculationParam(ShippingCostCalculationParametersEnum.PERCENTAGE_OF_ORDER_TOTOAL, PERCENTAGE)));
		((ElasticPathImpl) ElasticPathImpl.getInstance()).setBeanFactory(beanFactory);
		when(beanFactory.getBean(SHIPPING_COST_CALCULATION_PARAMETER))
				.thenAnswer(invocationOnMock -> new ShippingCostCalculationParameterImpl());
	}

	@After
	public void tearDown() {
		((ElasticPathImpl) ElasticPathImpl.getInstance()).setBeanFactory(beanFactory);
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.FixedBaseAndOrderTotalPercentageMethodImpl.getDisplayText()'.
	 */
	@Test
	public void testGetDisplayText() {
		assertThat(this.shippingCostCalculationMethod.getDisplayText()).isNotNull();
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.FixedBaseAndOrderTotalPercentageMethodImpl.getDisplayText()'.
	 */
	@Test
	public void testGetType() {
		assertThat(this.shippingCostCalculationMethod.getType())
				.isEqualTo(((FixedBaseAndOrderTotalPercentageMethodImpl) this.shippingCostCalculationMethod)
						.getMethodType());
	}

	/**
	 * Test that no items incurs no shipping cost.
	 */
	@Test
	public void testCalculateShippingCostNoItems() {
		ShippableItemContainer<ShippableItem> shippableItemContainer = aContainer().build();
		assertShippingCost("0.00", shippingCostCalculationMethod, shippableItemContainer, ZERO_DOLLARS_CAD, CURRENCY_CAD, productSkuLookup);
	}

	/**
	 * Test that two shippable items have the right price applied once.
	 */
	@Test
	public void testCalculateShippingCostTwoShippableItems() {
		ShippableItemContainer<ShippableItem> shippableItemContainer = aContainer()
				.with(ShippableItemContainerStubBuilder.aShippableItem())
				.with(ShippableItemContainerStubBuilder.aShippableItem()).build();

		// Cost: $10 + 10% * $65 = $16.5
		assertShippingCost("16.50", shippingCostCalculationMethod, shippableItemContainer, toCadMoney(new BigDecimal("65.00")),
				CURRENCY_CAD, productSkuLookup);
	}

	/**
	 * Test that with one shippable and one non-shippable items in the list
	 * the total cost is correct.
	 */
	@Test
	public void testCalculateShippingCostOneNonShippable() {
		ShippableItemContainer<ShippableItem> shippableItemContainer = aContainer()
				.with(ShippableItemContainerStubBuilder.aShippableItem())
				.with(ShippableItemContainerStubBuilder.aShippableItem()).build();

		// Cost: $10 + 10% * $10 = $11
		assertShippingCost("11.00", shippingCostCalculationMethod, shippableItemContainer, toCadMoney(new BigDecimal("10.00")),
				CURRENCY_CAD, productSkuLookup);
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
			.containsExactlyInAnyOrder(CURRENCY_USD, CURRENCY_CAD, null);

	}

	private Money toCadMoney(final BigDecimal itemCost) {
		return Money.valueOf(itemCost, CURRENCY_CAD);
	}

}
