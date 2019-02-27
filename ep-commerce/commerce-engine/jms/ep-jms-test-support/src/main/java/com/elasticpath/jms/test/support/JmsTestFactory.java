/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.jms.test.support;

import com.elasticpath.jms.test.support.activemq.ActiveMqTestFactory;

/**
 * Factory interface to create JMS consumers and producers.
 */
public interface JmsTestFactory {

	/**
	 * Gets an instance of <class>JmsTestFactory</class>.
	 *
	 * @param configFilename the configuration filename.
	 * @return instance
	 */
	static JmsTestFactory getInstance(String configFilename) {
		return ActiveMqTestFactory.getInstance(configFilename);
	}

	/**
	 * Gets a JMS consumer for a channel.
	 *
	 * @param channelName The channel name
	 * @param channelType The type of channel
	 * @return A JMS consumer
	 */
	JmsTestConsumer getConsumer(String channelName, JmsChannelType channelType);

	/**
	 * Gets a JMS producer for a channel.
	 *
	 * @param channelName The channel name
	 * @param channelType The type of channel
	 * @return A JMS producer
	 */
	JmsTestProducer getProducer(String channelName, JmsChannelType channelType);

	/**
	 * Closes the factory and all connections.
	 */
	void close();
}
