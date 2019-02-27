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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.ShippableItemContainer;
import com.elasticpath.test.factory.ShippableItemContainerStubBuilder;

/**
 * Test cases for <code>FixedPriceMethodImpl</code>.
 */
@SuppressWarnings({"PMD.TooManyStaticImports", "PMD.DontUseElasticPathImplGetInstance"})
@RunWith(MockitoJUnitRunner.class)
public class FixedPriceMethodImplTest {

	private static final BigDecimal FIXED_PRICE = new BigDecimal("10.00");
	private static final Currency CAD = Currency.getInstance("CAD");

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
		this.shippingCostCalculationMethod = new FixedPriceMethodImpl();
		shippingCostCalculationMethod.setParameters(
				someCalculationParams(
						aCostCalculationParam(ShippingCostCalculationParametersEnum.FIXED_PRICE,
								FIXED_PRICE, Currency.getInstance("CAD"))));

		((ElasticPathImpl) ElasticPathImpl.getInstance()).setBeanFactory(beanFactory);
	}

	@After
	public void tearDown() {
		((ElasticPathImpl) ElasticPathImpl.getInstance()).setBeanFactory(null);
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.FixedPriceMethodImpl.getDisplayText()'.
	 */
	@Test
	public void testGetDisplayText() {
		assertThat(this.shippingCostCalculationMethod.getDisplayText()).isNotNull();
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.FixedPriceMethodImpl.getDisplayText()'.
	 */
	@Test
	public void testGetType() {
		assertThat(this.shippingCostCalculationMethod.getType())
				.isEqualTo(((FixedPriceMethodImpl) this.shippingCostCalculationMethod).getMethodType());
	}

	/**
	 * Test that no items incurs no shipping cost.
	 */
	@Test
	public void testCalculateShippingCostNoItems() {
		ShippableItemContainer<ShippableItem> shippableItemContainer = aContainer().build();
		assertShippingCost("0.00", shippingCostCalculationMethod, shippableItemContainer, null, CAD, productSkuLookup);
	}

	/**
	 * Test that two shippable items only have the fixed price applied once.
	 */
	@Test
	public void testCalculateShippingCostTwoShippable() {
		ShippableItemContainer<ShippableItem> shippableItemContainer = aContainer()
				.with(ShippableItemContainerStubBuilder.aShippableItem())
				.with(ShippableItemContainerStubBuilder.aShippableItem()).build();

		assertShippingCost(FIXED_PRICE.toString(), shippingCostCalculationMethod, shippableItemContainer, null, CAD, productSkuLookup);
	}

	/**
	 * Test that with mixed (shippable and non-shippable) items the
	 * fixed price is only applied once.
	 */
	@Test
	public void testCalculateShippingCostOneNonShippableOneShippable() {
		ShippableItemContainer<ShippableItem> shippableItemContainer = aContainer()
				.with(ShippableItemContainerStubBuilder.aShippableItem())
				.with(ShippableItemContainerStubBuilder.aShippableItem()).build();

		assertShippingCost(FIXED_PRICE.toString(), shippingCostCalculationMethod, shippableItemContainer, null, CAD, productSkuLookup);
	}

	/**
	 * Test method for
	 * 'com.elasticpath.domain.shipping.impl.FixedBaseAndCostPerUnitWeightMethodImpl.getDefaultParameters(currencyList)'.
	 */
	@Test
	public void testGetDefaultParametersSet() {
		when(beanFactory.getBean(SHIPPING_COST_CALCULATION_PARAMETER))
				.thenAnswer(invocation -> new ShippingCostCalculationParameterImpl());

		List<Currency> currencyList = new ArrayList<>();
		Currency usd = Currency.getInstance("USD");
		Currency cad = Currency.getInstance("CAD");
		currencyList.add(usd);
		currencyList.add(cad);
		List<ShippingCostCalculationParameter> calcParams = shippingCostCalculationMethod.getDefaultParameters(currencyList);

		assertThat(calcParams).extracting(ShippingCostCalculationParameter::getCurrency)
			.containsExactlyInAnyOrder(usd, cad);

	}

	/**
	 * Test that shipping can be calculated by currency, depending on shopping cart currency, and
	 * shipping level parameters set.
	 */
	@Test
	public void testCalculateShippingCostByCurrency() {
		when(beanFactory.getBean(SHIPPING_COST_CALCULATION_PARAMETER))
				.thenAnswer(invocation -> new ShippingCostCalculationParameterImpl());

		List<Currency> currencyList = new ArrayList<>();
		final Currency currencyUS = Currency.getInstance("USD");
		final Currency currencyGermany = Currency.getInstance(Locale.GERMANY);
		currencyList.add(currencyUS);
		currencyList.add(currencyGermany);

		List<ShippingCostCalculationParameter> params = shippingCostCalculationMethod.getDefaultParameters(currencyList);
		Set<ShippingCostCalculationParameter> paramSet = new HashSet<>();
		for (ShippingCostCalculationParameter param : params) {
			param.setKey(ShippingCostCalculationParametersEnum.FIXED_PRICE.getKey());
			if (param.getCurrency().equals(Currency.getInstance(Locale.US))) {
				param.setValue("5.5");
			}
			if (param.getCurrency().equals(Currency.getInstance(Locale.GERMANY))) {
				param.setValue("9.5");
			}
			paramSet.add(param);
		}
		this.shippingCostCalculationMethod.setParameters(paramSet);

		ShippableItemContainer<ShippableItem> cartGermany = aContainer()
				.with(ShippableItemContainerStubBuilder.aShippableItem()).build();
		assertShippingCost("9.50", shippingCostCalculationMethod, cartGermany, null, currencyGermany, productSkuLookup);

		ShippableItemContainer<ShippableItem> cartUS = aContainer()
				.with(ShippableItemContainerStubBuilder.aShippableItem()).build();
		assertShippingCost("5.50", shippingCostCalculationMethod, cartUS, null, currencyUS, productSkuLookup);

	}

}
