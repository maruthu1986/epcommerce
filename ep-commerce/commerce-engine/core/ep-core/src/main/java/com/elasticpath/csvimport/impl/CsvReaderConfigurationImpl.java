/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.csvimport.impl;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.csvimport.CsvReaderConfiguration;

/**
 * Represents the criteria that a CSV reader might need to properly
 * parse a CSV file.
 */
public class CsvReaderConfigurationImpl implements CsvReaderConfiguration {

	private char delimiter = ',';
	private char textQualifier = '"';
	private String encoding = "UTF-8";
	private Map<String, Integer> mapping = new HashMap<>();
		
	/**
	 * @return the delimiter character (defaults to comma)
	 */
	@Override
	public char getDelimiter() {
		return this.delimiter;
	}

	/**
	 * @return the text qualifier, used in cases where a delimiter character
	 * might appear inside a string that should be imported. Defaults
	 * to double quote.
	 */
	@Override
	public char getTextQualifier() {
		return this.textQualifier;
	}

	/**
	 * @param delimiter the CSV delimiter
	 */
	@Override
	public void setDelimiter(final char delimiter) {
		this.delimiter = delimiter;
	}

	/**
	 * @param qualifier the text qualifier
	 */
	@Override
	public void setTextQualifier(final char qualifier) {
		this.textQualifier = qualifier;
	}

	/**
	 * @return the CSV input stream's encoding (defaults to UTF-8)
	 */
	@Override
	public String getEncoding() {
		return this.encoding;
	}

	/**
	 * @param encoding the CSV input stream's encoding (defaults to UTF-8)
	 */
	@Override
	public void setEncoding(final String encoding) {
		this.encoding = encoding;
	}

	/**
	 * Gets the map of object field names to CSV column indexes.
	 * @return the map of field names to column indexes
	 */
	@Override
	public Map<String, Integer> getFieldColumnIndexMapping() {
		return this.mapping;
	}

	/**
	 * Set the map of object field names to CSV column indexes.
	 * @param mapping the field name to column index map
	 */
	@Override
	public void setFieldColumnIndexMapping(final Map<String, Integer> mapping) {
		this.mapping = mapping;
	}

}
