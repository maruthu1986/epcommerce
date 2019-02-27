/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.common.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.google.common.testing.EqualsTester;

import com.elasticpath.base.common.dto.StructuredErrorMessage;

/**
 * Tests for {@link StructuredErrorMessage}.
 */
public class StructuredErrorMessageTest {

	private static final String TYPE = "error";
	private static final String DEBUG_MESSAGE = "debugMessage";
	private static final String MESSAGE_ID = "messageId";

	@Test
	public void testObjects() {
		new EqualsTester()
				.addEqualityGroup(createCommerceMessageObject(MESSAGE_ID, DEBUG_MESSAGE), createCommerceMessageObject(MESSAGE_ID, DEBUG_MESSAGE))
				.addEqualityGroup(createCommerceMessageObject(MESSAGE_ID, "debugMessage1"))
				.addEqualityGroup(createCommerceMessageObject("messageId1", DEBUG_MESSAGE))
				.testEquals();
	}

	@Test
	public void testObjectDataIneEquality() {
		Map<String, String> data = new HashMap<>();
		data.put("key4", "value4");
		data.put("key5", "value5");
		data.put("key6", "value6");
		data.put("key7", "value7");

		StructuredErrorMessage structuredErrorMessage1 = new StructuredErrorMessage("messageId4", "debugMessage4", data);
		StructuredErrorMessage structuredErrorMessage2 = createCommerceMessageObject("messageId4", "debugMessage4");
		assertFalse(structuredErrorMessage1.equals(structuredErrorMessage2));
	}

	@Test
	public void testToString() {
		StructuredErrorMessage commerceMessageObject = createCommerceMessageObject(MESSAGE_ID, DEBUG_MESSAGE);
		assertEquals(TYPE + "," + MESSAGE_ID + "," + DEBUG_MESSAGE + ",{key1=value1, key2=value2, key3=value3}", commerceMessageObject.toString());
	}

	private StructuredErrorMessage createCommerceMessageObject(final String messageId, final String debugMessage) {
		Map<String, String> data = new HashMap<>();
		data.put("key1", "value1");
		data.put("key2", "value2");
		data.put("key3", "value3");

		return new StructuredErrorMessage(messageId, debugMessage, data);
	}
}
