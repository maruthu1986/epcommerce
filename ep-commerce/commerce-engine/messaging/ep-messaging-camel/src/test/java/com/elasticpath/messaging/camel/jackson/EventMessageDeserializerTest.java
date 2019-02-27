/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.messaging.camel.jackson;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.elasticpath.messaging.EventMessage;
import com.elasticpath.messaging.EventType;
import com.elasticpath.messaging.factory.EventMessageFactory;
import com.elasticpath.messaging.impl.EventMessageImpl;

/**
 * Test class for {@link com.elasticpath.messaging.camel.jackson.EventMessageDeserializer}.
 */
public class EventMessageDeserializerTest {

	private EventMessageDeserializer deserializer;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private final ObjectMapper objectMapper = context.mock(ObjectMapper.class);

	private final EventMessageFactory eventMessageFactory = context.mock(EventMessageFactory.class);

	@Before
	public void setUp() {
		deserializer = new EventMessageDeserializer(objectMapper, eventMessageFactory);
	}

	@Test
	public void verifyDeserializeUsesFactoryToInstantiateWithOptionalData() throws Exception {
		final EventType eventType = SampleEventType.SAMPLE;
		final String guid = "GUID";
		final Map<String, Object> data = Collections.<String, Object> singletonMap("key1", "val1");
		final EventMessage expectedEventMessage = new EventMessageImpl(eventType, guid, data);

		final JsonParser jsonParser = context.mock(JsonParser.class);
		final JsonNode rootNode = context.mock(JsonNode.class, "rootNode");

		givenJsonParserReturnsRootNode(jsonParser, rootNode);
		givenRootNodeContainsGuid(rootNode, guid);
		givenRootNodeContainsEventType(rootNode, eventType);
		givenRootNodeContainsDataMap(rootNode, data);

		context.checking(new Expectations() {
			{
				oneOf(eventMessageFactory).createEventMessage(eventType, guid, data);
				will(returnValue(expectedEventMessage));
			}
		});

		final EventMessage actualEventMessage = deserializer.deserialize(jsonParser, context.mock(DeserializationContext.class));
		assertEquals("Unexpected event message produced by the deserializer", expectedEventMessage, actualEventMessage);
	}

	@Test
	public void verifyDeserializeUsesFactoryToInstantiateWithoutOptionalData() throws Exception {
		final EventType eventType = SampleEventType.SAMPLE;
		final String guid = "GUID";
		final EventMessage expectedEventMessage = new EventMessageImpl(eventType, guid);

		final JsonParser jsonParser = context.mock(JsonParser.class);
		final DeserializationContext deserializationContext = context.mock(DeserializationContext.class);

		final JsonNode rootNode = context.mock(JsonNode.class, "rootNode");

		givenJsonParserReturnsRootNode(jsonParser, rootNode);
		givenRootNodeContainsGuid(rootNode, guid);
		givenRootNodeContainsEventType(rootNode, eventType);
		givenRootNodeContainsDataMap(rootNode, null);

		context.checking(new Expectations() {
			{
				oneOf(eventMessageFactory).createEventMessage(eventType, guid);
				will(returnValue(expectedEventMessage));
			}
		});

		final EventMessage actualEventMessage = deserializer.deserialize(jsonParser, deserializationContext);
		assertEquals("Unexpected event message produced by the deserializer", expectedEventMessage, actualEventMessage);
	}

	private void givenJsonParserReturnsRootNode(final JsonParser jsonParser, final JsonNode rootNode) throws IOException {
		context.checking(new Expectations() {
			{
				final ObjectCodec objectCodec = context.mock(ObjectCodec.class);

				allowing(jsonParser).getCodec();
				will(returnValue(objectCodec));

				allowing(objectCodec).readTree(jsonParser);
				will(returnValue(rootNode));
			}
		});
	}

	private void givenRootNodeContainsGuid(final JsonNode rootNode, final String guid) {
		context.checking(new Expectations() {
			{
				final JsonNode guidNode = context.mock(JsonNode.class, "guidNode");

				allowing(rootNode).get("guid");
				will(returnValue(guidNode));

				allowing(guidNode).asText();
				will(returnValue(guid));
			}
		});
	}

	private void givenRootNodeContainsEventType(final JsonNode rootNode, final EventType eventType) throws JsonProcessingException {
		context.checking(new Expectations() {
			{
				final JsonNode eventTypeNode = context.mock(JsonNode.class, "eventTypeNode");

				allowing(rootNode).get("eventType");
				will(returnValue(eventTypeNode));

				allowing(objectMapper).treeToValue(eventTypeNode, EventType.class);
				will(returnValue(eventType));
			}
		});
	}

	private void givenRootNodeContainsDataMap(final JsonNode rootNode, final Map<String, Object> data) throws JsonProcessingException {
		context.checking(new Expectations() {
			{
				final JsonNode dataNode = context.mock(JsonNode.class, "dataMapNode");

				allowing(rootNode).get("data");
				if (data == null) {
					will(returnValue(null));
				} else {
					will(returnValue(dataNode));
				}

				allowing(objectMapper).treeToValue(dataNode, Map.class);
				will(returnValue(data));
			}
		});
	}

}