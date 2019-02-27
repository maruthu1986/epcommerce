/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.jms.test.support.activemq;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.elasticpath.jms.test.support.JmsChannelType;
import com.elasticpath.jms.test.support.JmsTestConsumer;

/**
 * An ActiveMQ consumer.
 */
public class ActiveMqTestConsumer implements JmsTestConsumer {

	private static final Logger LOGGER = Logger.getLogger(ActiveMqTestConsumer.class);

	private static final String READ_TIMEOUT_PROPERTY = "jms.read.timeout";
	private static final long LOOP_TIMEOUT = 100;
	private final long readTimeout;

	private Connection connection;
	private Session session;
	private Destination destination;
	private MessageConsumer messageConsumer;

	/**
	 * Constructor.
	 *
	 * @param factory     An ActiveMQ connection factory.
	 * @param channelName The channel name.
	 * @param channelType The channel type.
	 * @param settings    The settings properties.
	 */
	public ActiveMqTestConsumer(final ConnectionFactory factory, final String channelName, final JmsChannelType channelType,
								final Properties settings) {
		this.readTimeout = Long.parseLong(settings.getProperty(READ_TIMEOUT_PROPERTY));
		try {
			initializeConnection(factory, channelType, channelName);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			closeConnection();
		}
	}

	@Override
	public void drain() {

		List<Message> messages = getMessages(LOOP_TIMEOUT);
		for (Message message : messages) {
			LOGGER.debug("deleting message: " + message);
		}
		LOGGER.debug("done - deleting messages");
	}

	@Override
	public List<JSONObject> read() {
		return read(readTimeout);
	}

	@Override
	public List<JSONObject> read(final long readTimeout) {

		List<JSONObject> jsonObjectList = new ArrayList<>();

		try {
			List<Message> messages = getMessages(readTimeout);
			for (Message message : messages) {
				JSONParser parser = new JSONParser();
				TextMessage txtMessage = (TextMessage) message;
				Object obj = parser.parse(txtMessage.getText());

				JSONObject jsonObject = (JSONObject) obj;
				jsonObjectList.add(jsonObject);
				LOGGER.debug("jsonObject: " + jsonObject);
			}

		} catch (Exception e) {
			LOGGER.debug(e.getMessage());
		}
		return jsonObjectList;
	}

	@Override
	public List<TextMessage> readText() {
		return readText(readTimeout);
	}

	@Override
	public List<TextMessage> readText(final long readTimeout) {

		List<TextMessage> txtMessageList = new ArrayList<>();

		try {
			List<Message> messages = getMessages(readTimeout);
			for (Message message : messages) {
				TextMessage txtMessage = (TextMessage) message;
				txtMessageList.add(txtMessage);
				LOGGER.debug("txtMessage: " + txtMessage);
			}

		} catch (Exception e) {
			LOGGER.debug(e.getMessage());
		}
		return txtMessageList;
	}

	private List<Message> getMessages(final long readTimeout) {
		List<Message> messages = new ArrayList<>();
		try {
			Message message = messageConsumer.receive(readTimeout);
			while (message != null) {
				messages.add(message);
				message = messageConsumer.receive(LOOP_TIMEOUT);
			}

		} catch (JMSException e) {
			LOGGER.debug(e.getMessage());
		}
		return messages;
	}

	/**
	 * Closes the consumer.
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
		messageConsumer = session.createConsumer(destination);
		connection.start();
	}

	private void closeConnection() {
		LOGGER.debug("Closing ActiveMQ consumer");
		try {
			if (messageConsumer != null) {
				messageConsumer.close();
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
