/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.messaging.camel.jackson;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.elasticpath.messaging.EventMessage;
import com.elasticpath.messaging.EventType;
import com.elasticpath.messaging.factory.EventMessageFactory;

/**
 * Handles deserialization of {@link EventMessage} instances.
 */
class EventMessageDeserializer extends JsonDeserializer<EventMessage> {

	private final ObjectMapper objectMapper;

	private final EventMessageFactory eventMessageFactory;

	/**
	 * Constructor.
	 * 
	 * @param objectMapper the Jackson object mapper used in deserialization
	 * @param eventMessageFactory the {@link EventMessageFactory} used to create new {@link EventMessage} instances
	 */
	EventMessageDeserializer(final ObjectMapper objectMapper, final EventMessageFactory eventMessageFactory) {
		this.objectMapper = objectMapper;
		this.eventMessageFactory = eventMessageFactory;
	}

	@Override
	public EventMessage deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
		final ObjectCodec objectCodec = jsonParser.getCodec();
		final JsonNode node = objectCodec.readTree(jsonParser);

		final EventType eventType = getObjectMapper().treeToValue(node.get("eventType"), EventType.class);
		final String guid = node.get("guid").asText();
		final JsonNode dataNode = node.get("data");

		if (dataNode == null) {
			return getEventMessageFactory().createEventMessage(eventType, guid);
		} else {
			@SuppressWarnings("unchecked")
			final Map<String, Object> data = getObjectMapper().treeToValue(dataNode, Map.class);

			return getEventMessageFactory().createEventMessage(eventType, guid, data);
		}
	}

	protected ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	protected EventMessageFactory getEventMessageFactory() {
		return eventMessageFactory;
	}

}
