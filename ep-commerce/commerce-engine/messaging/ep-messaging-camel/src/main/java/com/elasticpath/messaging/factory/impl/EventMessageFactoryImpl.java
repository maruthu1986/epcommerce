/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.messaging.factory.impl;

import java.util.Map;

import com.elasticpath.messaging.EventMessage;
import com.elasticpath.messaging.EventType;
import com.elasticpath.messaging.factory.EventMessageFactory;
import com.elasticpath.messaging.impl.EventMessageImpl;

/**
 * An implementation of EventMessageFactory that returns EventMessageImpl instances.
 */
public class EventMessageFactoryImpl implements EventMessageFactory {

	private static final long serialVersionUID = -4193881035585969495L;

	@Override
	public EventMessage createEventMessage(final EventType eventType, final String guid) {
		return createEventMessage(eventType, guid, null);
	}

	@Override
	public EventMessage createEventMessage(final EventType eventType, final String guid, final Map<String, Object> data) {
		return new EventMessageImpl(eventType, guid, data);
	}

}
