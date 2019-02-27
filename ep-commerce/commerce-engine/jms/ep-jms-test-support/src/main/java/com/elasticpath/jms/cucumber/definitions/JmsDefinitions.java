/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.jms.cucumber.definitions;

import java.util.List;

import cucumber.api.java.en.Given;

import com.elasticpath.jms.test.support.JmsChannelType;
import com.elasticpath.jms.test.support.JmsTestFactory;

/**
 * Common Jms Definitions for reading and writing messages from JMS channels.
 */
public class JmsDefinitions {

	private final JmsTestFactory jmsTestFactory = JmsTestFactory.getInstance("ep-test-plugin.properties");

	/**
	 * Starts listening to the queue channel with the given name.
	 *
	 * @param queueName queue channel name
	 */
	@Given("^I am listening to (.+) queue$")
	public void listenToQueue(final String queueName) {
		jmsTestFactory.getConsumer(queueName, JmsChannelType.QUEUE);
	}

	/**
	 * Starts listening to default channel with the given name.
	 *
	 * @param channelName channel name
	 */
	@Given("^I am listening to (.+) channel$")
	public void listenToChannel(final String channelName) {
		jmsTestFactory.getConsumer(channelName, JmsChannelType.TOPIC);
	}

	/**
	 * Deletes messages from queue channel with the given name.
	 *
	 * @param queueName queue channel name
	 */
	@Given("^I delete all messages from (.+) queue$")
	public void deleteAllMessagesFromQueue(final String queueName) {
		jmsTestFactory.getConsumer(queueName, JmsChannelType.QUEUE).drain();
	}

	/**
	 * Deletes messages from the channel with the given name.
	 *
	 * @param channelName channel name
	 */
	@Given("^I delete all messages from (.+) channel$")
	public void deleteAllMessagesFromChannel(final String channelName) {
		jmsTestFactory.getConsumer(channelName, JmsChannelType.TOPIC).drain();
	}

	/**
	 * Sends message to the queue channel with the given name.
	 *
	 * @param queueName         queue channel name
	 * @param messageStringList message to send
	 */
	@Given("^I send following message to (.+) queue$")
	public void sendMessageToQueue(final String queueName, final List<String> messageStringList) {
		jmsTestFactory.getProducer(queueName, JmsChannelType.QUEUE).send(messageStringList.get(0));
	}

	/**
	 * Sends message to channel with the given name.
	 *
	 * @param channelName       channel name
	 * @param messageStringList message to send.
	 */
	@Given("^I send following message to (.+) channel")
	public void sendMessageToChannel(final String channelName, final List<String> messageStringList) {
		jmsTestFactory.getProducer(channelName, JmsChannelType.TOPIC).send(messageStringList.get(0));
	}

}
