/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.messaging;

import java.util.Map;

/**
 * <p>
 * Represents an application event message.
 * </p>
 * <p>
 * This is an implementation of a pull based Event Message, as described in Enterprise Integration Patterns. A pull based message does not include
 * details of the event, but provides enough context for downstream listeners to retrieve those details, hence why the only details are the object
 * GUID.
 * </p>
 */
public interface EventMessage {

	/**
	 * The type of event that this message communicates.
	 * 
	 * @return the type of event
	 */
	EventType getEventType();

	/**
	 * The guid of the object to which this event applies.
	 * 
	 * @return the object's guid
	 */
	String getGuid();

	/**
	 * Gets the data associated with the underlying object.
	 * 
	 * @return data map, never {@code null}.
	 */
	Map<String, Object> getData();

}
