/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.messaging.camel.jackson;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Jackson mixin that adds serialization properties to the {@link com.elasticpath.messaging.EventType} interface.
 * @see com.elasticpath.messaging.camel.jackson.EventMessageObjectMapper#addMixInAnnotations(Class, Class)
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class EventTypeMixin {
}
