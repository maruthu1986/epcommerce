/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.messaging.camel;

import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.apache.log4j.Logger;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.messaging.EventMessage;
import com.elasticpath.messaging.EventType;
import com.elasticpath.messaging.camel.jackson.EventMessageObjectMapper;
import com.elasticpath.messaging.camel.test.support.AbstractCamelRouteBuilderTest;
import com.elasticpath.messaging.camel.test.support.TransactionPolicyRegistryManager;
import com.elasticpath.messaging.factory.impl.EventMessageFactoryImpl;

/**
 * Test for {@link EventRouteBuilder}.
 */
public class EventRouteBuilderTest extends AbstractCamelRouteBuilderTest<EventMessage, EventMessageObjectMapper> {

	private static final Logger LOG = Logger.getLogger(EventRouteBuilderTest.class);

	private static final String INCOMING_ENDPOINT_URI = "direct:orderEvents";

	private static final String JMS_ENDPOINT_URI = "jms:orderEvents";
	private static final String GUID = "GUID";
	private static final long TIMEOUT_SECONDS = 5L;

	private MockEndpoint mockOutgoingEndpoint;

	private final EventRouteBuilder eventRouteBuilder = new EventRouteBuilder();

	private EventType eventType;

	@Override
	protected JndiRegistry createRegistry() throws Exception {
		final JndiRegistry registry = super.createRegistry();
		new TransactionPolicyRegistryManager(registry).registerDefaultTransactionPolicy();
		return registry;
	}

	/**
	 * Sets up test case.
	 * 
	 * @throws Exception on error
	 */
	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		setSourceEndpoint(getMandatoryEndpoint(INCOMING_ENDPOINT_URI));
		mockOutgoingEndpoint = getMockEndpoint("mock:" + JMS_ENDPOINT_URI);

		eventRouteBuilder.setIncomingEndpoint(getSourceEndpoint());
		eventRouteBuilder.setOutgoingEndpoint(mockOutgoingEndpoint);
		eventRouteBuilder.setEventMessageDataFormat(givenJacksonDataFormat(EventMessage.class, EventMessageObjectMapper.class));

		eventType = (EventType) () -> "eventType";

		context.addRoutes(eventRouteBuilder);
	}

	/**
	 * Ensure order event enqueues a JSON representation of an EventMessage.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void testDefaultOrderEventEnqueue() throws Exception {
		final EventMessage eventMessage = createTestOrderEventMessage();

		sendMessage(eventMessage, EventMessageObjectMapper.class);

		mockOutgoingEndpoint.expectedMessageCount(1);
		mockOutgoingEndpoint.expectedMessagesMatches(exchange -> {
			try { // Horrible try/catch because of checked exception.
				String expectedJson = getJson(eventMessage, EventMessageObjectMapper.class);
				Assertions.assertThat(exchange.getIn().getBody(String.class)).isEqualTo(expectedJson);
				return true;
			} catch (JsonProcessingException | IllegalAccessException | InstantiationException e) {
				LOG.trace("Error asserting expected message.", e);
				throw new IllegalArgumentException(e);
			}
		});

		mockOutgoingEndpoint.assertIsSatisfied();
	}

	private EventMessage createTestOrderEventMessage() {
		return new EventMessageFactoryImpl().createEventMessage(eventType, GUID);
	}

	@Override
	protected EventMessageObjectMapper configureObjectMapper(final EventMessageObjectMapper objectMapper) {
		objectMapper.init();
		return super.configureObjectMapper(objectMapper);
	}

	@Override
	protected void sendMessage(final EventMessage message, final Class<EventMessageObjectMapper> objectMapperClass) throws Exception {
		final NotifyBuilder notifyBuilder = new NotifyBuilder(context())
				.from(getSourceEndpoint().getEndpointUri())
				.whenDone(1)
				.create();

		template().sendBody(getSourceEndpoint(), message);

		Assertions.assertThat(notifyBuilder.matches(TIMEOUT_SECONDS, TimeUnit.SECONDS))
				.isTrue()
				.withFailMessage("Did not receive message(s) on endpoint within " + TIMEOUT_SECONDS + " seconds");
	}

}
