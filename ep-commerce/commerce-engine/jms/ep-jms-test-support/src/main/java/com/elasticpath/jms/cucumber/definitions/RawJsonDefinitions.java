/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.jms.cucumber.definitions;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.json.simple.JSONObject;

import com.elasticpath.jms.cucumber.asserts.EmailTestFacade;
import com.elasticpath.jms.cucumber.asserts.RawJsonTestFacade;
import com.elasticpath.jms.test.support.JmsChannelType;
import com.elasticpath.jms.test.support.JmsTestFactory;
import com.elasticpath.jms.utilities.KeyValue;

/**
 * Common Definitions for validating channel messages' content.
 */
@SuppressWarnings("checkstyle.*")
public class RawJsonDefinitions {

	private List<JSONObject> jsonObjectList;

	private final JmsTestFactory jmsTestFactory = JmsTestFactory.getInstance("ep-test-plugin.properties");

	/**
	 * Reads messages from topic channel.
	 *
	 * @param channelName name of the channel to read messages from
	 */
	@When("^I read (.+) message from channel$")
	public void getTopicMessage(final String channelName) {
		jsonObjectList = jmsTestFactory.getConsumer(channelName, JmsChannelType.TOPIC).read();

		assertThat(jsonObjectList)
				.as("Json Object is empty")
				.isNotEmpty();
	}

	/**
	 * Reads messages from topic channel.
	 *
	 * @param queueName name of the channel to read messages from
	 */
	@When("^I read (.+) message from queue$")
	public void getQueueMessage(final String queueName) {
		jsonObjectList = jmsTestFactory.getConsumer(queueName, JmsChannelType.QUEUE).read();

		assertThat(jsonObjectList)
				.as("Json Object is empty")
				.isNotEmpty();
	}

	/**
	 * Verifies email value.
	 *
	 * @param key   the key
	 * @param value the value
	 */
	@Then("^email value for (.+) should be (.+)$")
	public void verifyEmailValue(final String key, final String value) {
		EmailTestFacade.assertEmailValue(jsonObjectList, key, value);
	}

	/**
	 * Verifies multiple json values.
	 *
	 * @param parent       parent of the key, value pair
	 * @param keyValueList key value list
	 */
	@Then("^json (.+) should contain following values$")
	public void verifyJsonValues(final String parent, final List<KeyValue> keyValueList) {
		for (KeyValue keyValue : keyValueList) {
			RawJsonTestFacade.verifyJsonValues(jsonObjectList, parent, keyValue.getKey(), keyValue.getValue());
		}
	}

	/**
	 * Verifies single json value.
	 *
	 * @param key   the key
	 * @param value the value
	 */
	@Then("^json (.+) value should be (.+)$")
	public void verifyJsonValues(final String key, final String value) {
		RawJsonTestFacade.verifyJsonValues(jsonObjectList, "", key, value);
	}

	public List<JSONObject> getJsonObjectList() {
		return jsonObjectList;
	}
}
