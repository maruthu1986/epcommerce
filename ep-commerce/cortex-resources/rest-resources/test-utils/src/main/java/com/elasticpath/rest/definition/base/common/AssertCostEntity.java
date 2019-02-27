/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.definition.base.common;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import com.elasticpath.rest.definition.base.CostEntity;

/**
 * The Class AssertCostEntity.
 */
public final class AssertCostEntity {
	private final CostEntity costEntity;

	/**
	 * Instantiates a new assert cost entity.
	 *
	 * @param costEntity the cost entity
	 */
	public AssertCostEntity(final CostEntity costEntity) {
		this.costEntity = costEntity;
	}

	/**
	 * Creates a new {@link AssertCostEntity} object.
	 *
	 * @param costEntity the cost entity
	 * @return the new {@link AssertCostEntity} object
	 */
	public static AssertCostEntity assertCostEntity(final CostEntity costEntity) {
		return new AssertCostEntity(costEntity);
	}

	/**
	 * Assert the amount.
	 *
	 * @param amount the amount
	 * @return the assert cost entity
	 */
	public AssertCostEntity amount(final BigDecimal amount) {
		assertEquals(amount, costEntity.getAmount());
		return this;
	}

	/**
	 * Assert the display amount.
	 *
	 * @param displayAmount the display amount
	 * @return the assert cost entity
	 */
	public AssertCostEntity display(final String displayAmount) {
		assertEquals(displayAmount, costEntity.getDisplay());
		return this;
	}

	/**
	 * Assert the currency.
	 *
	 * @param currency the currency
	 * @return the assert cost entity
	 */
	public AssertCostEntity currency(final String currency) {
		assertEquals(currency, costEntity.getCurrency());
		return this;
	}
}
