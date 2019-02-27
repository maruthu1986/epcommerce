/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.messaging.camel.jackson;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.messaging.EventMessage;
import com.elasticpath.messaging.EventType;
import com.elasticpath.messaging.factory.impl.EventMessageFactoryImpl;
import com.elasticpath.messaging.impl.EventMessageImpl;
import com.elasticpath.messaging.spi.impl.EventTypeProviderImpl;

/**
 * Verifies that {@link EventMessage} instances can be unmarshalled from JSON strings.
 */
public class EventMessageUnmarshallingTest extends CamelTestSupport {

	private static final int SECONDS_TO_WAIT = 2;

	private static final String EVENT_MESSAGE_GUID = "EVENT_MESSAGE_GUID-123";
	private static final EventType EVENT_TYPE = SampleEventType.SAMPLE;

	private Endpoint incomingEndpoint;
	private MockEndpoint outgoingEndpoint;

	private Map<String, Object> data;
	private EventMessageObjectMapper objectMapper;
	private JacksonDataFormat jacksonDataFormat;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();

		data = new HashMap<>(2);
		data.put("key1", "val1");
		data.put("key2", "val2");

		incomingEndpoint = getMandatoryEndpoint("direct:incoming");
		outgoingEndpoint = getMockEndpoint("mock:outgoing");

		objectMapper = new EventMessageObjectMapper();
		objectMapper.setEventMessageFactory(new EventMessageFactoryImpl());
		objectMapper.init();

		objectMapper.registerEventType(new EventTypeProviderImpl<>(
			SampleEventType.class,
			new SampleEventType.SampleEventTypeLookup()));

		jacksonDataFormat = new JacksonDataFormat(objectMapper, EventMessage.class);
	}

	@Test
	public void testEventMessageCanBeUnmarshalledFromJson() throws Exception {
		context().addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from(incomingEndpoint)
						.log(LoggingLevel.INFO, EventMessageUnmarshallingTest.class.getName(), "Received message '${body}'")
						.unmarshal(jacksonDataFormat)
						.log(LoggingLevel.INFO, EventMessageUnmarshallingTest.class.getName(), "Unmarshalled '${body}'")
						.to(outgoingEndpoint);
			}
		});

		final EventMessage eventMessage = new EventMessageImpl(EVENT_TYPE, EVENT_MESSAGE_GUID, data);

		// the payload
		final String eventMessageAsJson = marshalToJson(eventMessage);

		final NotifyBuilder notifyBuilder = new NotifyBuilder(context)
				.wereSentTo(outgoingEndpoint.getEndpointUri())
				.whenDone(1)
				.create();

		template().sendBody(incomingEndpoint, eventMessageAsJson);

		assertTrue("Did not receive message on outgoing endpoint within " + SECONDS_TO_WAIT + " seconds",
					notifyBuilder.matches(SECONDS_TO_WAIT, TimeUnit.SECONDS));

		outgoingEndpoint.expectedMessageCount(1);
		outgoingEndpoint.assertIsSatisfied();

		final Exchange exchange = outgoingEndpoint.getExchanges().get(0);
		assertEquals("Unmarshalled EventType differs from original, input instance",
					eventMessage, exchange.getIn().getBody(EventMessage.class));
	}

	private String marshalToJson(final EventMessage eventMessage) throws JsonProcessingException {
		return objectMapper.writeValueAsString(eventMessage);
	}

}