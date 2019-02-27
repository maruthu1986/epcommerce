/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.customer.payment.token.impl;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.domain.customer.impl.CustomerImpl;
import com.elasticpath.domain.customer.impl.PaymentTokenImpl;
import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.payment.token.PaymentTokenRepository;

/**
 * Unit test for {@link PaymentTokenRepository}.
 */
public class PaymentTokenRepositoryImplTest {
	private static final String CUSTOMER_GUID = "customer-guid";

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();
	private final CustomerRepository mockCustomerRepository = context.mock(CustomerRepository.class);

	private final PaymentTokenRepository repository = new PaymentTokenRepositoryImpl(mockCustomerRepository);

	/**
	 * Test successfully adding token to customer.
	 */
	@Test
	public void testSuccessfullyAddingTokenToCustomer() {
		PaymentToken token = new PaymentTokenImpl.TokenBuilder().build();
		final Customer customer = new CustomerImpl();
		context.checking(new Expectations() {
			{
				oneOf(mockCustomerRepository).findCustomerByGuid(CUSTOMER_GUID);
				will(returnValue(ExecutionResultFactory.createReadOK(customer)));

				oneOf(mockCustomerRepository).updateCustomer(customer);
				will(returnValue(ExecutionResultFactory.createReadOK(ExecutionResultFactory.createUpdateOK())));

			}
		});

		repository.setDefaultPaymentToken(CUSTOMER_GUID, token);


	}

}
