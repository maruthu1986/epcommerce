/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.integration.epcommerce.alias.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.paymentmethods.PaymentMethodEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.paymentmethods.CustomerPaymentMethodsRepository;
import com.elasticpath.rest.resource.paymentmethods.integration.epcommerce.transform.PaymentMethodTransformer;

/**
 * Test class for {@link DefaultPaymentMethodLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultPaymentMethodLookupStrategyImplTest {
	private static final String CUST_GUID = "customerguid";
	private static final String STORE_CODE = "store code";
	private static final String TEST_CORRELATION_ID = "testCorrelationId";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private CustomerPaymentMethodsRepository mockCustomerPaymentMethodsRepository;
	@Mock
	private PaymentMethodTransformer paymentMethodTransformer;
	@InjectMocks
	private DefaultPaymentMethodLookupStrategyImpl defaultPaymentMethodLookupStrategy;

	private final PaymentMethod defaultPaymentMethod = new PaymentMethod() { };
	private final PaymentMethodEntity paymentMethodEntity = createPaymentMethod();

	@Test
	public void ensureDefaultPaymentMethodIdReturnedWhenFound() {
		shouldTransformToEntity(defaultPaymentMethod, paymentMethodEntity);
		shouldFindDefaultPaymentMethod(defaultPaymentMethod);

		ExecutionResult<String> result = defaultPaymentMethodLookupStrategy.getDefaultPaymentMethodId(STORE_CODE, CUST_GUID);
		assertExecutionResult(result)
				.isSuccessful()
				.data(TEST_CORRELATION_ID);
	}

	@Test
	public void ensureNotFoundReturnedIfDefaultPaymentMethodIdDoesNotExist() {
		shouldNotFindDefaultPaymentMethod();
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		defaultPaymentMethodLookupStrategy.getDefaultPaymentMethodId(STORE_CODE, CUST_GUID);
	}

	@Test
	public void ensureDefaultPaymentMethodReturnedWhenFound() {
		shouldTransformToEntity(defaultPaymentMethod, paymentMethodEntity);
		shouldFindDefaultPaymentMethod(defaultPaymentMethod);

		ExecutionResult<PaymentMethodEntity> result = defaultPaymentMethodLookupStrategy.getDefaultPaymentMethod(STORE_CODE, CUST_GUID);
		assertExecutionResult(result)
				.isSuccessful()
				.data(paymentMethodEntity);
	}

	@Test
	public void ensureNotFoundReturnedIfDefaultPaymentMethodDoesNotExist() {
		shouldNotFindDefaultPaymentMethod();
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		defaultPaymentMethodLookupStrategy.getDefaultPaymentMethod(STORE_CODE, CUST_GUID);

	}

	private void shouldFindDefaultPaymentMethod(final PaymentMethod defaultPaymentMethod) {
		when(mockCustomerPaymentMethodsRepository.findDefaultPaymentMethodByCustomerGuid(CUST_GUID))
				.thenReturn(ExecutionResultFactory.createReadOK(defaultPaymentMethod));
	}

	private void shouldTransformToEntity(final PaymentMethod defaultPaymentMethod, final PaymentMethodEntity paymentMethodEntity) {
		when(paymentMethodTransformer.transformToEntity(defaultPaymentMethod)).thenReturn(paymentMethodEntity);
	}

	private void shouldNotFindDefaultPaymentMethod() {
		when(mockCustomerPaymentMethodsRepository.findDefaultPaymentMethodByCustomerGuid(CUST_GUID))
				.thenReturn(ExecutionResultFactory.<PaymentMethod>createNotFound("Default not found"));
	}

	private PaymentMethodEntity createPaymentMethod() {
		return PaymentMethodEntity.builder()
				.withPaymentMethodId(TEST_CORRELATION_ID)
				.build();
	}
}
