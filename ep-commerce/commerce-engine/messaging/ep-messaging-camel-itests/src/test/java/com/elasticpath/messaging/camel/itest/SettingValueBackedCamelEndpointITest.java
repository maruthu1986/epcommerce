/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.messaging.camel.itest;

import static org.junit.Assert.assertEquals;

import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.elasticpath.test.integration.junit.DatabaseHandlingTestExecutionListener;
import com.elasticpath.test.support.junit.JmsRegistrationTestExecutionListener;

/**
 * Functional test class for {@link com.elasticpath.messaging.camel.SettingValueBackedCamelEndpointFactoryBean}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/integration-context.xml")
@TestExecutionListeners({
		JmsRegistrationTestExecutionListener.class,
		DatabaseHandlingTestExecutionListener.class,
		DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class
})
public class SettingValueBackedCamelEndpointITest {

	// All expected values defined in insert-setting-definitions-for-itests.xml Liquibase change set
	private static final String EXPECTED_DEFAULT_VALUE = "direct:ep.messaging.itest";
	private static final String EXPECTED_CONTEXT1_VALUE = "direct:ep.messaging.itest.context1";
	private static final String EXPECTED_CONTEXT2_VALUE = "direct:ep.messaging.itest.context2";

	private static final String UNEXPECTED_ENDPOINT_ASSERT_MESSAGE = "Unexpected Endpoint";

	@EndpointInject(uri = EXPECTED_DEFAULT_VALUE)
	private Endpoint expectedNoContextMessageEndpoint;

	@EndpointInject(uri = EXPECTED_CONTEXT1_VALUE)
	private Endpoint expectedContext1MessageEndpoint;

	@EndpointInject(uri = EXPECTED_CONTEXT2_VALUE)
	private Endpoint expectedContext2MessageEndpoint;

	@Autowired
	@Qualifier("noContextMessageEndpoint")
	private Endpoint actualNoContextMessageEndpoint;

	@Autowired
	@Qualifier("noMatchingContextMessageEndpoint")
	private Endpoint noMatchingContextMessageEndpoint;

	@Autowired
	@Qualifier("context1MessageEndpoint")
	private Endpoint actualContext1MessageEndpoint;

	@Autowired
	@Qualifier("context2MessageEndpoint")
	private Endpoint actualContext2MessageEndpoint;

	@Test
	public void verifyEndpointIsConstructedWithoutSettingsContextValue() throws Exception {
		assertEquals(UNEXPECTED_ENDPOINT_ASSERT_MESSAGE, expectedNoContextMessageEndpoint, actualNoContextMessageEndpoint);
	}

	@Test
	public void verifyEndpointIsConstructedWhenUsingNoSuchSettingsContextValue() throws Exception {
		assertEquals(UNEXPECTED_ENDPOINT_ASSERT_MESSAGE, expectedNoContextMessageEndpoint, noMatchingContextMessageEndpoint);
	}

	@Test
	public void verifyEndpointIsConstructedWithASettingsContextValue() throws Exception {
		assertEquals(UNEXPECTED_ENDPOINT_ASSERT_MESSAGE, expectedContext1MessageEndpoint, actualContext1MessageEndpoint);
	}

	@Test
	public void verifyEndpointIsConstructedWithADifferentSettingsContextValue() throws Exception {
		assertEquals(UNEXPECTED_ENDPOINT_ASSERT_MESSAGE, expectedContext2MessageEndpoint, actualContext2MessageEndpoint);
	}

}