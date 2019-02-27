/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.messaging.camel.test.support;

import org.apache.camel.CamelContext;

/**
 * Implementations of the interface can drain all messages from a camel context.
 */
public interface CamelContextMessagePurger {
	/**
	 * Drains all messages from the given <code>CamelContext</code>.
	 * 
	 * @param context the context
	 * @throws Exception if an endpoint within the context cannot be consumed from
	 */
	void purgeMessages(CamelContext context) throws Exception;
}
