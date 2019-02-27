/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.cortex.dce.advisor

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client
import static org.assertj.core.api.Assertions.assertThat

import cucumber.api.DataTable
import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

import com.elasticpath.CucumberDTO.AdvisorMessage

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)

Then(~'there (?:is an advisor message|are advisor messages) with the following fields:') { DataTable error ->
	def advisorMessageList = error.asList(AdvisorMessage)

	assertAdvisorMessageStatus(client.response.status)

	for (AdvisorMessage advisorMessage : advisorMessageList) {
		boolean messageExists = client.body.messages.findAll { message ->
			message.'id' == advisorMessage.getMessageId()
		}.any { message ->
			return verifyLinkedtoAndBlocksFields(advisorMessage, message) && verifyStructuredErrorMessageFields(advisorMessage, message)
		}

		assertThat(messageExists)
				.as("Unable to retrieve expected advisor - " + advisorMessage.getDebugMessage())
				.isTrue()
	}
}

Then(~'there (?:is not an advisor message with message id and debug message|are no advisor messages with message ids and debug messages)') {
	DataTable error ->
	def advisorMessageList = error.asList(AdvisorMessage)

	for (AdvisorMessage advisorMessage : advisorMessageList) {
		boolean messageExists = client.body.messages.findAll { message ->
			message.'id' == advisorMessage.getMessageId()
		}.any { message ->
			return message.'debug-message' == advisorMessage.getDebugMessage()
		}

		assertThat(messageExists)
				.as("Found unexpected advisor - " + advisorMessage.getDebugMessage())
				.isFalse()
	}
}

private static def assertAdvisorMessageStatus(int status) {
	assertThat(status == 200)
			.as("Resposne status message is not as expected.  Expecting 200 but was " + client.response.status)
			.isTrue()
}

private static boolean verifyStructuredErrorMessageFields(AdvisorMessage advisorMessage, Object message) {
	return (message.'debug-message' == advisorMessage.getDebugMessage() && message.'type' == advisorMessage.getMessageType()
			&& message.'id' == advisorMessage.getMessageId())
}

private static boolean verifyLinkedtoAndBlocksFields(AdvisorMessage advisorMessage, Object message) {
	if (advisorMessage.getLinkedTo()
			&& ((message.'linked-to'.'type' && message.'linked-to'.'type' != advisorMessage.getLinkedTo())
			|| ((message.'linked-to'.'name' && message.'linked-to'.'name' != advisorMessage.getLinkedTo())))) {
		return false
	}
	if (advisorMessage.getBlocks() && message.'blocks'.'rel' != advisorMessage.getBlocks()) {
		return false
	}
	return true
}