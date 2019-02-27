/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.messaging.camel.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import com.elasticpath.messaging.EventType;
import com.elasticpath.messaging.spi.EventTypeLookup;

/**
 * Handles deserialization of {@link com.elasticpath.messaging.EventType} instances.
 * 
 * @param <T> the Java type of the {@link EventType} subclass to be deserialized
 */
class EventTypeDeserializer<T extends EventType> extends JsonDeserializer<T> {

	private static final String NAME_FIELD = "name";

	private final EventTypeLookup<T> eventTypeLookup;

	/**
	 * Constructor.
	 * 
	 * @param eventTypeLookup the {@link EventTypeLookup} used to retrieve {@link EventType} instances during deserialization
	 */
	EventTypeDeserializer(final EventTypeLookup<T> eventTypeLookup) {
		this.eventTypeLookup = eventTypeLookup;
	}

	@Override
	public T deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
		final ObjectCodec objectCodec = jsonParser.getCodec();
		final JsonNode node = objectCodec.readTree(jsonParser);

		final String eventTypeName = node.get(NAME_FIELD).asText();

		final T eventType = getEventTypeLookup().lookup(eventTypeName);

		if (eventType == null) {
			throw new JsonParseException("Unable to find EventType with name [" + eventTypeName + "]", jsonParser.getCurrentLocation());
		}

		return eventType;
	}

	protected EventTypeLookup<T> getEventTypeLookup() {
		return eventTypeLookup;
	}

}
