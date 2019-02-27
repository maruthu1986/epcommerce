/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.service.selector.impl;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.shipping.connectivity.spi.ShippingCalculationPlugin;

@RunWith(MockitoJUnitRunner.class)
public class GenericShippingCalculationPluginSelectorImplTest {

	private static final String PLUGIN_NAME = "testPluginName";
	@Mock
	private ShippingCalculationPlugin mockShippingCalculationPlugin;

	@InjectMocks
	private GenericStaticShippingCalculationPluginSelectorImpl target;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void setUp() {
		target.setShippingCalculationPluginList(singletonList(mockShippingCalculationPlugin));
		when(mockShippingCalculationPlugin.getName()).thenReturn(PLUGIN_NAME);
	}

	@Test
	public void testGetShippingCalculationPlugin() {

		// when
		ShippingCalculationPlugin plugin = target.getShippingCalculationPlugin();

		// verify
		assertThat(plugin).isNotNull();
	}

	@Test
	public void testGetShippingCalculationPluginWithNullPluginList() {

		// given
		target.setShippingCalculationPluginList(null);
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("ShippingCalculationPlugin list cannot be empty or null.");

		// when
		ShippingCalculationPlugin plugin = target.getShippingCalculationPlugin();
		assertThat(plugin).isNotNull();
	}

	@Test
	public void testGetShippingCalculationPluginWithEmptyPluginList() {

		// given
		target.setShippingCalculationPluginList(emptyList());
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("ShippingCalculationPlugin list cannot be empty or null.");

		// when
		target.getShippingCalculationPlugin();
	}

	@Test
	public void testGetShippingCalculationPluginWithMultiplePlugins() {

		// given
		target.setShippingCalculationPluginList(Arrays.asList(mockShippingCalculationPlugin, mockShippingCalculationPlugin));

		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("Expects only one ShippingCalculationPlugin. Found multiple ShippingCalculationPlugin"
				+ ".[testPluginName,testPluginName]");

		// when
		target.getShippingCalculationPlugin();

	}

	@Test
	public void testGetShippingCalculationPluginWithNullPlugin() {

		// given
		target.setShippingCalculationPluginList(singletonList(null));

		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("ShippingCalculationPlugin cannot be null.");

		// when
		target.getShippingCalculationPlugin();

	}

}
