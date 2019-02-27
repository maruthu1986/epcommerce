/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.jms.test.support.activemq;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.elasticpath.jms.test.support.JmsChannelType;
import com.elasticpath.jms.test.support.JmsTestConsumer;
import com.elasticpath.jms.test.support.JmsTestFactory;
import com.elasticpath.jms.test.support.JmsTestProducer;

/**
 * Factory to create ActiveMQ consumers and producers.
 */
public class ActiveMqTestFactory implements JmsTestFactory {

	private static final String BROKER_URL_PROPERTY = "ep.jms.url";

	private static final Map<String, JmsTestFactory> FACTORY_MAP = new HashMap<>();
	private static final Map<String, ActiveMqTestConsumer> CONSUMER_MAP = new HashMap<>();
	private static final Map<String, ActiveMqTestProducer> PRODUCER_MAP = new HashMap<>();

	private final Properties settings = new Properties();
	private final ConnectionFactory factory;
	private final String configFilename;

	/**
	 * Gets an instance of <class>ActiveMqTestFactory</class>.
	 *
	 * @param configFilename the configuration filename.
	 * @return instance
	 */
	public static JmsTestFactory getInstance(final String configFilename) {
		JmsTestFactory instance = FACTORY_MAP.get(configFilename);
		if (instance == null) {
			instance = new ActiveMqTestFactory(configFilename);
			FACTORY_MAP.put(configFilename, instance);
		}
		return instance;
	}

	/**
	 * Constructor. Reads configuration property file and creates an <class>ActiveMQConnectionFactory</class>.
	 *
	 * @param configFilename the configuration filename.
	 */
	public ActiveMqTestFactory(final String configFilename) {
		try {
			settings.load(getClass().getClassLoader().getResourceAsStream(configFilename));
		} catch (Exception ex) {
			throw new IllegalArgumentException(ex);
		}
		this.configFilename = configFilename;
		factory = new ActiveMQConnectionFactory(settings.getProperty(BROKER_URL_PROPERTY));
	}

	@Override
	public JmsTestConsumer getConsumer(final String channelName, final JmsChannelType channelType) {
		final String key = channelName + channelType.name();
		ActiveMqTestConsumer consumer = CONSUMER_MAP.get(key);
		if (consumer == null) {
			consumer = new ActiveMqTestConsumer(factory, channelName, channelType, settings);
			CONSUMER_MAP.put(key, consumer);
		}
		return consumer;
	}

	@Override
	public JmsTestProducer getProducer(final String channelName, final JmsChannelType channelType) {
		final String key = channelName + channelType.name();
		ActiveMqTestProducer producer = PRODUCER_MAP.get(key);
		if (producer == null) {
			producer = new ActiveMqTestProducer(factory, channelName, channelType);
			PRODUCER_MAP.put(key, producer);
		}
		return producer;
	}

	@Override
	public void close() {
		FACTORY_MAP.remove(configFilename);
		CONSUMER_MAP.forEach((key, value) -> value.close());
		CONSUMER_MAP.clear();
		PRODUCER_MAP.forEach((key, value) -> value.close());
		PRODUCER_MAP.clear();
	}

}