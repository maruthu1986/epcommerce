/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.messaging.camel.itest;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
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
 * Functional test class that verifies that routes can be configured to support ActiveMQ Virtual Topics.
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
public class ActiveMqVirtualTopicConsumptionITest {

	private static final long SECONDS_TO_WAIT_FOR_MESSAGE_CONSUMPTION = 5;

	@Autowired
	@Qualifier("ep-messaging-camel-itest")
	private CamelContext context;

	@Autowired
	private Endpoint publishingEndpoint;

	@Autowired
	private Endpoint consumerEndpointOne;

	@Autowired
	private Endpoint consumerEndpointTwo;

	// Used to validate that each consumer actually consumed a message.
	@EndpointInject(uri="mock:monitor", context = "ep-messaging-camel-itest")
	private MockEndpoint monitor;

	/**
	 * <p>Verifies that the Settings Framework values, in conjunction with the SettingValueBackedCamelEndpointFactoryBean,
	 * can be used to configure different endpoint URIs for messaging publishing and consumption.  This is required when using Virtual Topics,
	 * a feature supported by many JMS brokers whereby messages sent to a JMS Topic are forwarded to one or many JMS queues.  This allows both a
	 * pub/sub pattern alongside competing consumers for HA and throughput optimisation.</p>
	 * <p/>
	 * <p>The test steps are as follows:</p>
	 * <ol>
	 * <li>Send a message to VirtualTopic.Foo</li>
	 * <li>Show that the message can be consumed on a Consumer.A.VirtualTopic.Foo queue</li>
	 * <li>Show that the message can also be consumed on a Consumer.B.VirtualTopic.Foo queue</li>
	 * </ol>
	 * <p>The objective is to be able to enable this functionality by introducing settings context values,
	 * rather than modifying code or Spring configuration.</p>
	 *
	 * @throws Exception
	 */
	@Test
	public void verifySettingFrameworkEndpointUrisSupportAsymmetricalPublishAndSubscribeEndpointUris() throws Exception {
		// one per consumer
		final int expectedNumberOfReceivedMessages = 2;

		monitor.expectedMessageCount(expectedNumberOfReceivedMessages);

		context.addRoutes(new MessageConsumer(consumerEndpointOne));
		context.addRoutes(new MessageConsumer(consumerEndpointTwo));

		Thread.sleep(SECONDS_TO_WAIT_FOR_MESSAGE_CONSUMPTION);

		context.createProducerTemplate().sendBody(publishingEndpoint, "Message");

		Thread.sleep(SECONDS_TO_WAIT_FOR_MESSAGE_CONSUMPTION);

		monitor.assertIsSatisfied();
	}

	/**
	 * Consumes messages from a dedicated queue, forwarded by the Virtual Topic.
	 */
	class MessageConsumer extends RouteBuilder {

		private final Endpoint incomingEndpoint;

		MessageConsumer(final Endpoint incomingEndpoint) {
			this.incomingEndpoint = incomingEndpoint;
		}

		@Override
		public void configure() {
			from(incomingEndpoint)
					.to(monitor);
		}

	}

}