/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.inventory.impl;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.Map;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.inventory.strategy.InventoryStrategy;
import com.elasticpath.settings.provider.SettingValueProvider;
import com.elasticpath.settings.test.support.SimpleSettingValueProvider;

/**
 * Test class for {@link InventoryFacadeImpl}.
 */
public class InventoryFacadeImplTest {

	private static final String INVENTORY_STRATEGY_ID = "inventoryStrategyId";

	private final InventoryFacadeImpl facade = new InventoryFacadeImpl();

	private final SettingValueProvider<String> inventoryStrategyIdProvider = new SimpleSettingValueProvider<>(INVENTORY_STRATEGY_ID);

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	@Before
	public void setUp() {
		facade.setInventoryStrategyIdProvider(inventoryStrategyIdProvider);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInitThrowsIllArgExceptionWhenNoSuchInventoryStrategy() throws Exception {
		final Map<String, InventoryStrategy> strategies = Collections.singletonMap("nonMatchingId", context.mock(InventoryStrategy.class));
		facade.setStrategies(strategies);

		facade.init();
	}

	@Test
	public void verifyInitSelectsInventoryStrategy() {
		final InventoryStrategy inventoryStrategy = context.mock(InventoryStrategy.class);
		final Map<String, InventoryStrategy> strategies = Collections.singletonMap(INVENTORY_STRATEGY_ID, inventoryStrategy);
		facade.setStrategies(strategies);

		facade.init();

		assertEquals("Unexpected InventoryStrategy selected", inventoryStrategy, facade.getSelectedInventoryStrategy());
	}

}