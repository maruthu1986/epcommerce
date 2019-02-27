/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.definition.base.common;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.rest.definition.base.NamedCostEntity;

/**
 * Tests for {@link AssertNamedCostEntity}.
 */
public class AssertNamedCostEntityTest {

	private static final BigDecimal AMOUNT = BigDecimal.valueOf(10.10);
	private static final BigDecimal WRONG_AMOUNT = BigDecimal.ZERO;
	private static final String CURRENCY_CODE = "CAD";
	private static final String WRONG_CURRENCY_CODE = "USD";
	private static final String DISPLAY = "$10.10";
	private static final String WRONG_DISPLAY = "$0.00";
	private static final String TITLE = "GST";
	private static final String WRONG_TITLE = "PST";

	private NamedCostEntity namedCostEntity;

	@Before
	public void setUp() {
		namedCostEntity = NamedCostEntity.builder()
				.withAmount(AMOUNT)
				.withCurrency(CURRENCY_CODE)
				.withDisplay(DISPLAY)
				.withTitle(TITLE)
				.build();
	}

	@Test
	public void testAssertSuccesses() {
		AssertNamedCostEntity.assertNamedCostEntity(namedCostEntity)
				.amount(AMOUNT)
				.currency(CURRENCY_CODE)
				.display(DISPLAY)
				.title(TITLE);
	}

	@Test(expected = AssertionError.class)
	public void testAssertAmountFailure() {
		AssertNamedCostEntity.assertNamedCostEntity(namedCostEntity)
				.amount(WRONG_AMOUNT);
	}

	@Test(expected = AssertionError.class)
	public void testAssertCurrencyFailure() {
		AssertNamedCostEntity.assertNamedCostEntity(namedCostEntity)
				.currency(WRONG_CURRENCY_CODE);
	}

	@Test(expected = AssertionError.class)
	public void testAssertDisplayFailure() {
		AssertNamedCostEntity.assertNamedCostEntity(namedCostEntity)
				.display(WRONG_DISPLAY);
	}

	@Test(expected = AssertionError.class)
	public void testAssertTitleFailure() {
		AssertNamedCostEntity.assertNamedCostEntity(namedCostEntity)
				.title(WRONG_TITLE);
	}

}
