/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.domain.attribute.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.elasticpath.domain.attribute.AttributeMultiValueType;

/**
 * Test the parsing of multi-value attributes of <code>AbstractAttributeValueImpl</code>.
 */
public class AbstractAttributeValueImplCommaDelimitingTest {
	
	/**
	 * Test the cases where the input is empty.
	 */
	@Test
	public void testParseEmptyString() {
		String multiValueString = "";
		String[] expectedArray = {};
		
		decodingCompare(multiValueString, expectedArray, AttributeMultiValueType.LEGACY);
	}
	
	/**
	 * Test the cases where the input is null.
	 */
	@Test
	public void testParseNullString() {
		String multiValueString = null;
		String[] expectedArray = {};
		
		decodingCompare(multiValueString, expectedArray, AttributeMultiValueType.LEGACY);
	}

	/**
	 * Tests <code>buildShortTextMultiValues</code> with alphanumeric characters only.
	 */
	@Test
	public void testBuildShortTextMultiValueNormalCase() {
		List<String> inputList = new ArrayList<>();
		inputList.add("Test1");
		inputList.add("Test3");
		inputList.add("no comma s");
		
		String expectedString = "Test1,Test3,no comma s";
		
		encodingCompare(inputList, expectedString, AttributeMultiValueType.LEGACY);
	}
	
	/**
	 * Tests the decoding where elements have commas.
	 * Legacy decoding should ignore escape-characters and still delimit on any commas found.
	 */
	@Test
	public void testLegacyEncodingWithCommas() {
		String multiValueString = "test value,\"test, value2\",ttt";
		String[] expectedArray = { "test value", "\"test", "value2\"", "ttt" };
		
		decodingCompare(multiValueString, expectedArray, AttributeMultiValueType.LEGACY);
	}
	
	/**
	 * Tests the decoding where elements have commas.
	 * Legacy decoding should ignore escape-characters and still delimit on any commas found.
	 */
	@Test
	public void testRfcCompliantEncodingWithCommas() {
		String multiValueString = "test value,\"test, value2\",ttt";
		String[] expectedArray = { "test value", "test, value2", "ttt" };
		
		decodingCompare(multiValueString, expectedArray, AttributeMultiValueType.RFC_4180);
	}
		
	private void encodingCompare(final List<String> inputList, final String expectedString,
			final AttributeMultiValueType multiValueType) {
		String actualString = AbstractAttributeValueImpl.
						buildShortTextMultiValues(inputList, multiValueType);
		assertEquals(expectedString, actualString);
	}
	
	private void decodingCompare(final String multiValueString, final String[] expectedArray,
			final AttributeMultiValueType multiValueType) {
		List<String> actualArray = AbstractAttributeValueImpl.
						parseShortTextMultiValues(multiValueString, multiValueType);
		
		assertEquals(expectedArray.length, actualArray.size());
		
		for (int index = 0; index < expectedArray.length; index++) {
			assertEquals(expectedArray[index], actualArray.get(index));
		}
	}
}
