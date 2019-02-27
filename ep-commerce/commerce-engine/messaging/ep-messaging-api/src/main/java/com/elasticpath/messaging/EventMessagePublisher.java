/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.messaging;

/**
 * Service used to publish event messages intended for consumers within and outside the EP system.
 */
public interface EventMessagePublisher {

	/**
	 * Publish an event message.
	 * 
	 * @param eventMessage the message to publish
	 */
	void publish(EventMessage eventMessage);

}
