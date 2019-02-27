/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.messaging.predicate.impl;

import java.util.Arrays;
import java.util.Collection;

import com.elasticpath.messaging.EventMessage;
import com.elasticpath.messaging.EventMessagePredicate;
import com.elasticpath.messaging.EventType;

/**
 * This predicate inspects the event message's {@link EventType} and determines whether or not it exists within the {@code compatibleEventTypes}
 * collection.
 */
public class CompatibleEventTypePredicate implements EventMessagePredicate {

	private Collection<EventType> compatibleEventTypes;

	/**
	 * Determines whether or not the event message's {@link EventType} exists within the {@code compatibleEventTypes} collection.
	 * 
	 * @param eventMessage the event message
	 * @return {@code true} if the event type exists within the {@code compatibleEventTypes} collection; {@code false} otherwise
	 */
	@Override
	public boolean apply(final EventMessage eventMessage) {
		return getCompatibleEventTypes().contains(eventMessage.getEventType());
	}

	public void setCompatibleEventTypes(final EventType... compatibleEventTypes) {
		this.compatibleEventTypes = Arrays.asList(compatibleEventTypes);
	}

	public void setCompatibleEventTypes(final Collection<EventType> compatibleEventTypes) {
		this.compatibleEventTypes = compatibleEventTypes;
	}

	protected Collection<EventType> getCompatibleEventTypes() {
		return compatibleEventTypes;
	}

}
