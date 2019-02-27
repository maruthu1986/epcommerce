/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.definition.base.common;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.definition.base.CostEntity;

/**
 * Tests for {@link AssertCostEntity}.
 */
@RunWith(MockitoJUnitRunner.class)
public class AssertCostEntityTest {

	private static final BigDecimal AMOUNT = BigDecimal.valueOf(10.10);
	private static final BigDecimal WRONG_AMOUNT = BigDecimal.ZERO;
	private static final String CURRENCY_CODE = "CAD";
	private static final String WRONG_CURRENCY_CODE = "USD";
	private static final String DISPLAY = "$10.10";
	private static final String WRONG_DISPLAY = "$0.00";

	private CostEntity costEntity;

	@Before
	public void setUp() {
		costEntity = CostEntity.builder()
				.withAmount(AMOUNT)
				.withCurrency(CURRENCY_CODE)
				.withDisplay(DISPLAY)
				.build();
	}

	@Test
	public void testAssertSuccesses() {
		AssertCostEntity.assertCostEntity(costEntity)
				.amount(AMOUNT)
				.currency(CURRENCY_CODE)
				.display(DISPLAY);
	}

	@Test(expected = AssertionError.class)
	public void testAssertAmountFailure() {
		AssertCostEntity.assertCostEntity(costEntity)
				.amount(WRONG_AMOUNT);
	}

	@Test(expected = AssertionError.class)
	public void testAssertCurrencyFailure() {
		AssertCostEntity.assertCostEntity(costEntity)
				.currency(WRONG_CURRENCY_CODE);
	}

	@Test(expected = AssertionError.class)
	public void testAssertDisplayFailure() {
		AssertCostEntity.assertCostEntity(costEntity)
				.display(WRONG_DISPLAY);
	}

}
