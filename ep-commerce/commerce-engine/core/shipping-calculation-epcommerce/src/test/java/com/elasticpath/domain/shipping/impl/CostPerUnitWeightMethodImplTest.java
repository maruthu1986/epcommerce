/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.shipping.impl;

import static org.assertj.core.api.Assertions.assertThat;

import static com.elasticpath.domain.shipping.impl.ShippingAsserts.assertShippingCost;
import static com.elasticpath.domain.shipping.impl.ShippingCostTestDataFactory.aCostCalculationParam;
import static com.elasticpath.domain.shipping.impl.ShippingCostTestDataFactory.someCalculationParams;
import static com.elasticpath.test.factory.ShippableItemContainerStubBuilder.aContainer;
import static com.elasticpath.test.factory.ShippableItemContainerStubBuilder.aShippableItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.shipping.ShippingCostCalculationParameter;
import com.elasticpath.domain.shipping.ShippingCostCalculationParametersEnum;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.ShippableItemContainer;

/**
 * Test cases for <code>CostPerUnitWeightMethodImpl</code>.
 */
@SuppressWarnings({"PMD.TooManyStaticImports"})
@RunWith(MockitoJUnitRunner.class)
public class CostPerUnitWeightMethodImplTest {

	@Mock
	private ProductSkuLookup productSkuLookup;
	private static final BigDecimal COST_PER_UNIT_WEIGHT = new BigDecimal("5.0");
	private static final Currency CAD = Currency.getInstance("CAD");

	private CostPerUnitWeightMethodImpl method;

	/**
	 * Set the test fixture up.
	 */
	@Before
	public void setUp() {
		method = new CostPerUnitWeightMethodImpl();
		method.setParameters(
				someCalculationParams(
						aCostCalculationParam(
								ShippingCostCalculationParametersEnum.COST_PER_UNIT_WEIGHT, COST_PER_UNIT_WEIGHT,
								Currency.getInstance("CAD"))));
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.CostPerUnitWeightMethodImpl.getDisplayText()'.
	 */
	@Test
	public void testGetDisplayText() {
		CostPerUnitWeightMethodImpl method = new CostPerUnitWeightMethodImpl();
		assertThat(method.getDisplayText()).isNotNull();
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.CostPerUnitWeightMethodImpl.getDisplayText()'.
	 */
	@Test
	public void testGetType() {
		assertThat(method.getType()).isEqualTo(method.getMethodType());
	}

	/**
	 * Test that no items incurs no shipping cost.
	 */
	@Test
	public void testCalculateShippingCostNoItems() {
		final ShippableItemContainer<ShippableItem> shippableItemContainer = aContainer().build();
		assertShippingCost("0.00", method, shippableItemContainer, null, CAD, productSkuLookup);
	}

	/**
	 * Test that the shipping cost is the cost per unit weight * the total weight of all the applicable line items, rounded to two decimals.
	 */
	@Test
	public void testCalculateShippingCostTwoShippable() {
		final ShippableItemContainer<ShippableItem> shippableItemContainer = aContainer()
				.with(aShippableItem()
						.withQuantity(1)
						.withWeight(BigDecimal.TEN))
				.build();

		assertShippingCost("50.00", method, shippableItemContainer, null, CAD, productSkuLookup);
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.FixedBaseAndCostPerUnitWeightMethodImpl.getDefaultParameters(currencyList)'.
	 */
	@Test
	public void testGetDefaultParametersSet() {
		CostPerUnitWeightMethodImpl method = new CostPerUnitWeightMethodImpl() {
			private static final long serialVersionUID = 6582808358854860854L;

			@Override
			ShippingCostCalculationParameter getShippingCostCalculationParameterBean() {
				return new ShippingCostCalculationParameterImpl();
			}
		};
		List<Currency> currencyList = new ArrayList<>();
		Currency usd = Currency.getInstance("USD");
		Currency cad = Currency.getInstance("CAD");
		currencyList.add(usd);
		currencyList.add(cad);
		List<ShippingCostCalculationParameter> calcParams = method.getDefaultParameters(currencyList);

		assertThat(calcParams).extracting(ShippingCostCalculationParameter::getCurrency)
			.containsExactlyInAnyOrder(usd, cad);

	}

}
