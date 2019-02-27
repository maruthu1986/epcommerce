/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.messaging.camel.jackson;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.elasticpath.messaging.spi.EventTypeLookup;

/**
 * Test class for {@link EventTypeDeserializer}.
 */
public class EventTypeDeserializerTest {

	private EventTypeDeserializer<SampleEventType> deserializer;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@SuppressWarnings("unchecked")
	private final EventTypeLookup<SampleEventType> eventTypeLookup = context.mock(EventTypeLookup.class);

	@Before
	public void setUp() {
		deserializer = new EventTypeDeserializer<>(eventTypeLookup);
	}

	@Test
	public void verifyDeserializeUsesLookupToInstantiate() throws Exception {
		final SampleEventType expectedEventType = SampleEventType.SAMPLE;

		final JsonParser jsonParser = context.mock(JsonParser.class);

		givenJsonParserReturnsEventName(jsonParser, expectedEventType.getName());

		context.checking(new Expectations() {
			{
				oneOf(eventTypeLookup).lookup(expectedEventType.getName());
				will(returnValue(expectedEventType));
			}
		});

		final SampleEventType actualEventType = deserializer.deserialize(jsonParser, context.mock(DeserializationContext.class));
		assertEquals("Unexpected event type produced by deserializer", expectedEventType, actualEventType);
	}

	@Test
	public void verifyJsonParseExceptionThrownWhenNoMatchingEventTypeFound() throws Exception {
		final JsonParser jsonParser = context.mock(JsonParser.class);
		final DeserializationContext deserializationContext = context.mock(DeserializationContext.class);

		final String noSuchEventType = "noSuchEventType";
		givenJsonParserReturnsEventName(jsonParser, noSuchEventType);

		context.checking(new Expectations() {
			{
				oneOf(eventTypeLookup).lookup(noSuchEventType);
				will(returnValue(null));

				allowing(jsonParser).getCurrentLocation(); // for exception message
			}
		});

		thrown.expect(JsonParseException.class);
		thrown.expectMessage("Unable to find EventType with name [" + noSuchEventType + "]");

		deserializer.deserialize(jsonParser, deserializationContext);
	}

	private void givenJsonParserReturnsEventName(final JsonParser jsonParser, final String name) throws IOException {
		context.checking(new Expectations() {
			{
				final ObjectCodec objectCodec = context.mock(ObjectCodec.class);

				allowing(jsonParser).getCodec();
				will(returnValue(objectCodec));

				final JsonNode jsonNode = context.mock(JsonNode.class);

				allowing(objectCodec).readTree(jsonParser);
				will(returnValue(jsonNode));

				allowing(jsonNode).get("name");
				will(returnValue(jsonNode)); // for convenience. Actually a different instance, but we don't really care.

				allowing(jsonNode).asText();
				will(returnValue(name));
			}
		});
	}

}