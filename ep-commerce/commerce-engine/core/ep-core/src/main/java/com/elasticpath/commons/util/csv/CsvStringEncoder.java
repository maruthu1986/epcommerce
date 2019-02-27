/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.commons.util.csv;

import java.util.List;

/**   
 *  Utility wrapper for Encoding (Array to single string) and 
 *  Decoding (String to a list of strings) in CSV format. 
 */
public interface CsvStringEncoder {
	
	/**
	 * Decode a delimited string to a List of Strings.
	 * @param csvLine a delimited String
	 * @param delimiter the delimiting character
	 * @return a List of Strings
	 */
	List<String> decodeStringToList(String csvLine, char delimiter);
	
	/**
	 * Decode a delimited string to an array of Strings.
	 * @param csvLine a delimited String
	 * @param delimiter the delimiting character
	 * @return an array of strings
	 */
	String[] decodeStringToArray(String csvLine, char delimiter);
	
	/**
	 * Encode an array of Strings to a delimited String.
	 * @param array an array of Strings
	 * @param delimiter the delimiting character
	 * @return a delimited String
	 */
	String encodeString(String[] array, char delimiter);
	
	/**
	 * Encode a list of Strings to a delimited String.
	 *
	 * @param list a list of strings
	 * @param delimiter the delimiting character
	 * @return a delimited String
	 */
	String encodeString(List<String> list, char delimiter);
}
