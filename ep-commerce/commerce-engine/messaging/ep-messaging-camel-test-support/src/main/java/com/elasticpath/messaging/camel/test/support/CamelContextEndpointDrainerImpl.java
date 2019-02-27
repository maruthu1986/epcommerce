/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.messaging.camel.test.support;

import java.util.concurrent.RejectedExecutionException;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spi.BrowsableEndpoint;
import org.apache.log4j.Logger;
import org.junit.Assert;

/**
 * Implementation of <code>CamelContextMessagePurger</code> that purges the context by draining all BrowsableEndpoints.
 */
public class CamelContextEndpointDrainerImpl implements CamelContextMessagePurger {
	private static final Logger LOG = Logger.getLogger(CamelContextEndpointDrainerImpl.class);

	/**
	 * Purges all messages on the given endpoint.
	 * 
	 * @param endpoint the endpoint
	 * @throws Exception if the endpoint cannot be consumed from
	 */
	protected void purgeMessages(final BrowsableEndpoint endpoint) throws Exception {
		final int messageCount = endpoint.getExchanges().size();

		if (messageCount > 0) {
			LOG.debug("Purging " + messageCount + " messages from " + endpoint.getEndpointUri());

			int deleted = 0;
			while (!endpoint.getExchanges().isEmpty()) {
				if (endpoint.createPollingConsumer().receiveNoWait() != null) {
					deleted++;
				}
			}
			LOG.debug(deleted + " messages purged from "
					+ endpoint.getEndpointUri()
					+ " (current number of messages: " + endpoint.getExchanges().size() + ")");
			Assert.assertEquals("Could not purge all messages. Clean up the queue("
					+ endpoint.getEndpointUri()
					+ ") manually before proceeding", 0, endpoint.getExchanges().size());
		} else {
			LOG.debug("No messages to purge in " + endpoint);
		}
	}

	@Override
	public void purgeMessages(final CamelContext context) throws Exception {
		LOG.info("Purging ALL queues");
		for (Endpoint endpoint : context.getEndpoints()) {
			if (endpoint instanceof BrowsableEndpoint && !(endpoint instanceof MockEndpoint)) {
				try {
					purgeMessages((BrowsableEndpoint) endpoint);
				} catch (RejectedExecutionException exc) {
					LOG.warn("Could not purge an endpoint", exc);
				}
			}
		}
		LOG.info("Purging ALL queues - DONE");
	}
}