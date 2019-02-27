/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.shipping.connectivity.dto.ShippingCalculationResult;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * Unit test for {@link ShippingOptionResultImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShippingOptionResultImplTest {

	private ShippingOptionResultImpl target;

	@Mock
	private List<ShippingOption> shippingOptionList;

	@Mock
	private ShippingCalculationResult.ErrorInformation errorInformation;

	@Before
	public void setUp() {
		target = new ShippingOptionResultImpl();
	}


	@Test
	public void testSetAvailableShippingOptions() {
		target.setAvailableShippingOptions(shippingOptionList);

		assertThat(target.getAvailableShippingOptions()).isSameAs(shippingOptionList);
	}

	@Test
	public void testSetErrorInformation() {
		target.setErrorInformation(errorInformation);

		assertThat(target.getErrorInformation()).isPresent().contains(errorInformation);
	}

}
