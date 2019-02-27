/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.messaging;

/**
 * A predicate that determines whether or not a provide {@link EventMessage} matches certain selection criteria.
 */
public interface EventMessagePredicate {

	/**
	 * Indicates whether an {@link EventMessage} matches the selection criteria.
	 * 
	 * @param eventMessage the event message to test
	 * @return {@code true} if the event message matches the selection criteria; {@code false} otherwise.
	 */
	boolean apply(EventMessage eventMessage);

}
