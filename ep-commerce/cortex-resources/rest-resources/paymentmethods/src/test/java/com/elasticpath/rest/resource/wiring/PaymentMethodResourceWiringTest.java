/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.wiring;

import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.springframework.test.context.ContextConfiguration;

import com.elasticpath.rest.resource.paymentmethods.integration.PaymentMethodLookupStrategy;
import com.elasticpath.rest.resource.paymentmethods.integration.PaymentMethodWriterStrategy;
import com.elasticpath.rest.resource.paymentmethods.integration.alias.DefaultPaymentMethodLookupStrategy;
import com.elasticpath.rest.schema.uri.ProfilesUriBuilderFactory;

/**
 * Tests payment methdos resource wiring.
 */
@ContextConfiguration
@SuppressWarnings({"PMD.UnusedPrivateField", "PMD.TestClassWithoutTestCases"})
public class PaymentMethodResourceWiringTest extends AbstractResourceWiringTest {

	@ReplaceWithMock(beanName = "paymentMethodLookupStrategy")
	private PaymentMethodLookupStrategy paymentMethodLookupStrategy;

	@ReplaceWithMock(beanName = "defaultPaymentMethodLookupStrategy")
	private DefaultPaymentMethodLookupStrategy defaultPaymentMethodLookupStrategy;

	@ReplaceWithMock(beanName = "paymentMethodWriterStrategy")
	private PaymentMethodWriterStrategy paymentMethodWriterStrategy;

	@ReplaceWithMock(beanName = "profilesUriBuilderFactory")
	private ProfilesUriBuilderFactory profilesUriBuilderFactory;
}
