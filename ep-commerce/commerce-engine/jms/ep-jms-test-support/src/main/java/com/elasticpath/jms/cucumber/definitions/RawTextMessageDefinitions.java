/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.jms.cucumber.definitions;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import javax.jms.TextMessage;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import com.elasticpath.jms.cucumber.asserts.RawTextMessageTestFacade;
import com.elasticpath.jms.test.support.JmsChannelType;
import com.elasticpath.jms.test.support.JmsTestFactory;

/**
 * Common Definitions for validating channel messages' content.
 */
@SuppressWarnings("checkstyle.*")
public class RawTextMessageDefinitions {

	private List<TextMessage> txtMessageList;

	private final JmsTestFactory jmsTestFactory = JmsTestFactory.getInstance("ep-test-plugin.properties");

	/**
	 * Reads text messages from topic channel.
	 *
	 * @param channelName name of the channel to read messages from
	 */
	@When("^I read text message from channel (.+)$")
	public void getTopicMessageAsText(final String channelName) {
		txtMessageList = jmsTestFactory.getConsumer(channelName, JmsChannelType.TOPIC).readText();

		assertThat(txtMessageList)
				.as("Text Message is empty")
				.isNotEmpty();
	}

	/**
	 * Reads text messages from topic channel.
	 *
	 * @param queueName name of the channel to read messages from
	 */
	@When("^I read text message from queue (.+)$")
	public void getQueueMessageAsText(final String queueName) {
		txtMessageList = jmsTestFactory.getConsumer(queueName, JmsChannelType.QUEUE).readText();

		assertThat(txtMessageList)
				.as("Text Message is empty")
				.isNotEmpty();
	}

	/**
	 * Verifies text message values.
	 *
	 * @param txtValueList key value list
	 */
	@Then("^text message should contain following values?$")
	public void verifyTextValues(final List<String> txtValueList) {
		for (String textMessage : txtValueList) {
			RawTextMessageTestFacade.verifyMessageText(txtMessageList, textMessage);
		}
	}

	public List<TextMessage> getTxtMessageList() {
		return txtMessageList;
	}
}
