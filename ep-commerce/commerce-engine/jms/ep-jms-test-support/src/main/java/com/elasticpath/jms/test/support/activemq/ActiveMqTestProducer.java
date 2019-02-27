/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.jms.test.support.activemq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;

import com.elasticpath.jms.test.support.JmsChannelType;
import com.elasticpath.jms.test.support.JmsTestProducer;

/**
 * An ActiveMQ consumer.
 */
public class ActiveMqTestProducer implements JmsTestProducer {

	private static final Logger LOGGER = Logger.getLogger(ActiveMqTestProducer.class);

	private Connection connection;
	private Session session;
	private Destination destination;
	private MessageProducer messageProducer;

	/**
	 * Constructor.
	 *
	 * @param factory     An ActiveMQ connection factory.
	 * @param channelName The channel name.
	 * @param channelType The channel type.
	 */
	public ActiveMqTestProducer(final ConnectionFactory factory, final String channelName, final JmsChannelType channelType) {
		try {
			initializeConnection(factory, channelType, channelName);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			closeConnection();
		}
	}

	@Override
	public void send(final String message) {
		try {
			TextMessage msg = session.createTextMessage();
			msg.setText(message);
			messageProducer.send(msg);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * Closes the producer.
	 */
	void close() {
		closeConnection();
	}

	private void initializeConnection(final ConnectionFactory factory, final JmsChannelType channelType, final String channelName)
			throws JMSException {
		connection = factory.createConnection();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		switch (channelType) {
			case QUEUE:
				destination = session.createQueue(channelName);
				break;
			case TOPIC:
				destination = session.createTopic(channelName);
				break;
			default:
		}
		messageProducer = session.createProducer(destination);
		connection.start();
	}

	private void closeConnection() {
		LOGGER.debug("Closing ActiveMQ producer");
		try {
			if (messageProducer != null) {
				messageProducer.close();
			}
			if (session != null) {
				session.close();
			}
			if (connection != null) {
				connection.close();
			}

		} catch (Exception e) {
			LOGGER.debug(e.getMessage());
		}
	}

}
