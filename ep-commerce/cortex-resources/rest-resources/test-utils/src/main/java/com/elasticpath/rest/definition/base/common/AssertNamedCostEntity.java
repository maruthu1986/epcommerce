/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.definition.base.common;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import com.elasticpath.rest.definition.base.NamedCostEntity;

/**
 * The Class AssertNamedCostEntity.
 */
public final class AssertNamedCostEntity {
	private final NamedCostEntity namedCostEntity;

	/**
	 * Instantiates a new assert tax entity.
	 *
	 * @param namedCostEntity the tax entity
	 */
	public AssertNamedCostEntity(final NamedCostEntity namedCostEntity) {
		this.namedCostEntity = namedCostEntity;
	}

	/**
	 * Creates a new {@link AssertNamedCostEntity} object.
	 *
	 * @param namedCostEntity the tax entity
	 * @return the new {@link AssertNamedCostEntity} object
	 */
	public static AssertNamedCostEntity assertNamedCostEntity(final NamedCostEntity namedCostEntity) {
		return new AssertNamedCostEntity(namedCostEntity);
	}

	/**
	 * Assert the title.
	 *
	 * @param title the title
	 * @return the assert tax entity
	 */
	public AssertNamedCostEntity title(final String title) {
		assertEquals(title, namedCostEntity.getTitle());
		return this;
	}

	/**
	 * Assert the amount.
	 *
	 * @param amount the amount
	 * @return the assert tax entity
	 */
	public AssertNamedCostEntity amount(final BigDecimal amount) {
		assertEquals(amount, namedCostEntity.getAmount());
		return this;
	}

	/**
	 * Assert the display amount.
	 *
	 * @param displayAmount the display amount
	 * @return the assert tax entity
	 */
	public AssertNamedCostEntity display(final String displayAmount) {
		assertEquals(displayAmount, namedCostEntity.getDisplay());
		return this;
	}

	/**
	 * Assert the currency.
	 *
	 * @param currency the currency
	 * @return the assert tax entity
	 */
	public AssertNamedCostEntity currency(final String currency) {
		assertEquals(currency, namedCostEntity.getCurrency());
		return this;
	}
}
