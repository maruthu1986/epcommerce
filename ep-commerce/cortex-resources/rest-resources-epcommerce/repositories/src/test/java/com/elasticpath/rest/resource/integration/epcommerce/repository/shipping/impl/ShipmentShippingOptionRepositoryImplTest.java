/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipping.impl;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.service.shipping.ShippingOptionService;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * Tests for {@link ShipmentShippingOptionRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentShippingOptionRepositoryImplTest {

	private static final String STORE_CODE = "testStoreCode";
	private static final Locale LOCALE = Locale.CANADA;
	private static final String SHIPPING_OPTION_CODE = "testShippingOptionCode";
	private static final String NON_EXIST_SHIPPING_OPTION_CODE = "nonExistShippingOptionCode";
	private static final String USER_ID = "testUserId";

	@Mock
	private ResourceOperationContext resourceOperationContext;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private ShippingOptionService shippingOptionService;
	@Mock private ShippingOption shippingOption;

	@InjectMocks
	private ShipmentShippingOptionRepositoryImpl repositoryImpl;

	@Before
	public void setUp() {
		Subject subject = TestSubjectFactory.createWithScopeAndUserIdAndLocale(STORE_CODE, USER_ID, LOCALE);
		when(resourceOperationContext.getSubject()).thenReturn(subject);

		when(shippingOptionService.getAllShippingOptions(STORE_CODE, LOCALE).getAvailableShippingOptions()).thenReturn(singletonList(shippingOption));
		when(shippingOption.getCode()).thenReturn(SHIPPING_OPTION_CODE);

	}

	@Test
	public void testFindByGuidSuccess() {

		ExecutionResult<ShippingOption> shippingOptionResult = repositoryImpl.findByCode(SHIPPING_OPTION_CODE);

		assertThat(shippingOptionResult.isSuccessful())
			.as("ShippingOption lookup should be successful.")
			.isTrue();
		assertThat(shippingOptionResult.getData())
			.as("Result data should be output of service.")
			.isEqualTo(shippingOption);
	}

	@Test
	public void testFindByGuidFailure() {
		ExecutionResult<ShippingOption> shippingOptionResult = repositoryImpl.findByCode(NON_EXIST_SHIPPING_OPTION_CODE);

		assertThat(shippingOptionResult.isFailure())
			.as("ShippingOption lookup should be a failure.")
			.isTrue();
	}

}
