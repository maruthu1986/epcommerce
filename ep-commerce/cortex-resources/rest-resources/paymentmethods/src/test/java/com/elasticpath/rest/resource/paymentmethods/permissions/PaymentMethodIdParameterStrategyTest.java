/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.permissions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.subject.PrincipalCollection;

import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.paymentmethods.PaymentMethodLookup;
import com.elasticpath.rest.id.util.Base32Util;

/**
 * Test class for {@link PaymentMethodIdParameterStrategy}.
 */
public final class PaymentMethodIdParameterStrategyTest {
	private static final String USER_ID = "BB1E992F-DA7E-E648-BABB-DF1D5B23968F";
	private static final String SCOPE = "scope";
	private static final String DECODED_PAYMENT_METHOD_ID = "7D1E992F-DA7E-E648-BA11-DF1D5B23968F";
	private static final String PAYMENT_METHOD_ID = Base32Util.encode(DECODED_PAYMENT_METHOD_ID);
	private static final PrincipalCollection PRINCIPALS = TestSubjectFactory.createCollectionWithScopeAndUserId(SCOPE, USER_ID);

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	@Mock
	private PaymentMethodLookup mockPaymentMethodLookup;


	/**
	 * Test get payment method ID parameter.
	 */
	@Test
	public void testPaymentMethodIdsFound() {
		final Collection<String> paymentMethodIds = Collections.singleton(PAYMENT_METHOD_ID);
		PaymentMethodIdParameterStrategy permissionGenerator = new PaymentMethodIdParameterStrategy(mockPaymentMethodLookup);

		context.checking(new Expectations() {
			{
				oneOf(mockPaymentMethodLookup).getPaymentMethodIds(SCOPE, USER_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(paymentMethodIds)));
			}
		});

		String paymentMethodIdString = permissionGenerator.getParameterValue(PRINCIPALS);

		assertEquals(PAYMENT_METHOD_ID, paymentMethodIdString);
	}

	/**
	 * Test no scope found.
	 */
	@Test
	public void testNoPaymentMethodIdsFound() {
		final Collection<String> paymentMethodIds = Collections.emptyList();

		context.checking(new Expectations() {
			{
				oneOf(mockPaymentMethodLookup).getPaymentMethodIds(SCOPE, USER_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(paymentMethodIds)));
			}
		});

		PaymentMethodIdParameterStrategy permissionGenerator = new PaymentMethodIdParameterStrategy(mockPaymentMethodLookup);
		String paymentMethodIdString = permissionGenerator.getParameterValue(PRINCIPALS);

		assertTrue(StringUtils.isEmpty(paymentMethodIdString));
	}
}
