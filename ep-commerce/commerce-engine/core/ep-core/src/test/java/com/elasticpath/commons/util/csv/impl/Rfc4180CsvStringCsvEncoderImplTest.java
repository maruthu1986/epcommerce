/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.commons.util.csv.impl;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.util.csv.CsvStringEncoder;

/**
 * Test the decoding and encoding of <code>RfcCompliantCsvEncodeImpl</code>.
 */
public class Rfc4180CsvStringCsvEncoderImplTest {

	private CsvStringEncoder encoder;
	
	/**
	 * Sets up the test cases.
	 */
	@Before
	public void setUp() {
		encoder = new Rfc4180CsvStringEncoderImpl();
	}
	
	private static final String TEST = "test";

	/**
	 * Test the decoding of setting a multi-value short text field where an element has a comma inside it.
	 * The expected case is that the parser will retain the comma in the value and not treat the 
	 * comma as a delimiter.
	 */
	@Test
	public void testDecodeEmbeddedCommaValues() {
		String multiValueString = "test value,\"test, value2\",ttt";
		String[] expectedArray = { "test value", "test, value2", "ttt" };
		
		afterParsingComparison(multiValueString, expectedArray);
	}
	
	/**
	 * Test the decoding where the elements in the multi-value short text field only contains
	 * alphanumeric values.
	 */
	@Test
	public void testDecodeNormalCommaDelimiting() {
		String multiValueString = "test value,test,value2,ttt";
		String[] expectedArray = { "test value", TEST, "value2", "ttt" };
		
		afterParsingComparison(multiValueString, expectedArray);
	}
	
	/**
	 * Test the decoding where the elements contain excessive spaces after and before the delimiters.
	 */
	@Test
	public void testDecodeSpacingBetweenElements() {
		String multiValueString = "test value,   test   , value2,ttt ";
		String[] expectedArray = { "test value", "   test   ", " value2", "ttt " };
		
		afterParsingComparison(multiValueString, expectedArray);
	}
	
	/**
	 * Test the decoding where the delimiters should be escaped.
	 */
	@Test
	public void testDecodeEscapedElements() {
		String multiValueString = "\",,,,,\"";
		String[] expectedArray = { ",,,,," };
		
		afterParsingComparison(multiValueString, expectedArray);
	}
	
	/**
	 * Test the decoding where the elements are empty.
	 */
	@Test
	public void testDecodeEmptyElements() {
		String multiValueString = "test,,test,,test,";
		String[] expectedArray = { TEST, null, TEST, null, TEST, null };
		
		afterParsingComparison(multiValueString, expectedArray);
	}

	/**
	 * Tests the decoding where embedded quotes on multiple values.
	 */
	@Test
	public void testDecodeEmbeddedQuotes() {
		String multiValueString = "\"Fill-in mode\",\"Auto mode with red-eye reduction\","
			+ "\"Auto mode\",\"Flash OFF mode\",\"Red-eye reduction\"";
		String[] expectedArray = {  "Fill-in mode", 
									"Auto mode with red-eye reduction",
									"Auto mode",
									"Flash OFF mode",
									"Red-eye reduction" };
		
		afterParsingComparison(multiValueString, expectedArray);
	}
	
	/**
	 * Tests the decoding where embedded quotes are not surrounded by a enclosing quote
	 * on the element.
	 * 
	 * Because the format of the string is not in RFC-compliant format, an exception will be thrown.
	 */
	@Test(expected = EpServiceException.class)
	public void testDecodeNoEnclosingQuotes() {
		String multiValueString = "3\" DVD";
		String[] expectedArray = { }; //nothing expected
		
		afterParsingComparison(multiValueString, expectedArray);
	}
	
	/**
	 * Tests the decoding where elements had improper quotes used in the elements.
	 * SuperCsv will escape these quotes and return a false positive.
	 */
	@Test
	public void testDecodeQuotesSuccessCase() {
		String multiValueString = "3\" DVD, 3\" DVD-R";
		String[] expectedArray = { "3 DVD, 3 DVD-R" }; //
		
		afterParsingComparison(multiValueString, expectedArray);
	}
	
	/**
	 * Tests the decoding where embedded quotes are not properly encoded (ie. double quotes "").
	 */
	@Test(expected = EpServiceException.class)
	public void testDecodeImproperEmbeddedQuotes() {
		String multiValueString = "\"Te\"st\"";
		String[] expectedArray = { }; //nothing expected
		
		afterParsingComparison(multiValueString, expectedArray);
	}
	
	/**
	 * Test the decoding where the input is empty.
	 */
	@Test
	public void testDecodeEmptyString() {
		String multiValueString = "";
		String[] expectedArray = {};
		
		afterParsingComparison(multiValueString, expectedArray);
	}
	
	/**
	 * Test the decoding where the input is null.
	 */
	@Test
	public void testDecodeNullString() {
		String multiValueString = null;
		String[] expectedArray = {};
		
		afterParsingComparison(multiValueString, expectedArray);
	}
	
	/**
	 * Tests the encoding of string elements with # as the delimiter.
	 */
	@Test
	public void testEncodeNormalElements() {
		String[] multiValues = new String[] { "test", "one", "two" };
		String expectedString = "test#one#two";
		
		afterEncodingComparison(multiValues, expectedString, '#');
	}
	
	/**
	 * Tests the encoding of string elements with the delimiter and escape characters within them.
	 */
	@Test
	public void testEncodeDelimiterAndQuoteInElements() {
		String[] multiValues = new String[] { "te,st", "on,\"\"e", "tw\"o" };
		String expectedString = "\"te,st\",\"on,\"\"\"\"e\",\"tw\"\"o\"";
		
		afterEncodingComparison(multiValues, expectedString, ',');
	}
	
	/**
	 * Tests the encoding of null or empty elements. Empty elements are encoded as ""
	 * while null values are empty.
	 */
	@Test
	public void testEncodeNullElements() {
		String[] multiValues = new String[] { null, "", "test" };
		String expectedString = "##test";
		
		afterEncodingComparison(multiValues, expectedString, '#');
	}
	
	/**
	 * Tests the encoding of a blank string array.
	 */
	@Test
	public void testEncodeEmptyString() {
		String[] multiValues = new String[0];
		String expectedString = "";
		
		afterEncodingComparison(multiValues, expectedString, ',');
	}
	
	/**
	 * Tests the encoding of a blank string array.
	 */
	@Test
	public void testEncodeEmptyElement() {
		String[] multiValues = new String[] { "" };
		String expectedString = "";
		
		afterEncodingComparison(multiValues, expectedString, ',');
	}
	
	/**
	 * Tests the encoding of a null string array.
	 */
	@Test
	public void testEncodeNullString() {
		String[] multiValues = null;
		String expectedString = "";
		
		afterEncodingComparison(multiValues, expectedString, ',');
	}
	
	
	/**
	 * Tests the encoding of return characters in the elements.
	 */
	@Test
	public void testEncodeReturnCharsElements() {
		String[] multiValues = new String[] { "one\n", "\r\ntwo", "\r" };
		String expectedString = "\"one\n\"#\"\ntwo\"#\"\n\"";
		
		afterEncodingComparison(multiValues, expectedString, '#');
	}

	private void afterEncodingComparison(final String[] multiValues, final String expectedString,
				final char delimiter) {
		String actualString = encoder.encodeString(multiValues, delimiter);
		assertEquals(expectedString, actualString);

	}

	private void afterParsingComparison(final String multiValueString, final String[] expectedArray) {
		compareAsList(multiValueString, expectedArray);
		compareAsArray(multiValueString, expectedArray);
	}
	
	private void compareAsList(final String multiValueString, final String[] expectedArray) {
		List<String> actualArray = encoder.decodeStringToList(multiValueString, ',');
		
		assertEquals(expectedArray.length, actualArray.size());
		
		for (int index = 0; index < expectedArray.length; index++) {
			assertEquals(expectedArray[index], actualArray.get(index));
		}
	}
	
	private void compareAsArray(final String multiValueString, final String[] expectedArray) {
		String[] actualArray = encoder.decodeStringToArray(multiValueString, ',');
		
		assertEquals(expectedArray.length, actualArray.length);
		
		for (int index = 0; index < expectedArray.length; index++) {
			assertEquals(expectedArray[index], actualArray[index]);
		}
	}
}
