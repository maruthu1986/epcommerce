/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.jms.test.support;

/**
 * JMS producer test interface.
 */
public interface JmsTestProducer {

	/**
	 * Sends a message to the producers channel.
	 *
	 * @param message the message
	 */
	void send(String message);

}
