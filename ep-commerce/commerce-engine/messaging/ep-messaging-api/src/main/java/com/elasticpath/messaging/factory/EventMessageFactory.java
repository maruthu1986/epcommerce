/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.messaging.factory;

import java.io.Serializable;
import java.util.Map;

import com.elasticpath.messaging.EventMessage;
import com.elasticpath.messaging.EventType;

/**
 * Factory for creating {@link EventMessage}s.
 */
public interface EventMessageFactory extends Serializable {

	/**
	 * Creates an event message that is produced/consumed by camel components.
	 * 
	 * @param eventType the event type.
	 * @param objectGuid the object guid.
	 * @return a newly created {@link EventMessage}
	 */
	EventMessage createEventMessage(EventType eventType, String objectGuid);

	/**
	 * Create a new event message with data.
	 * 
	 * @param eventType the event type
	 * @param objectGuid the object GUID
	 * @param data the data as a map
	 * @return a new event message instance
	 */
	EventMessage createEventMessage(EventType eventType, String objectGuid, Map<String, Object> data);

}
