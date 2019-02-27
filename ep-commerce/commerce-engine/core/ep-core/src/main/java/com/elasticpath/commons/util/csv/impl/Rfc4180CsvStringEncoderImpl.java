/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.commons.util.csv.impl;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.util.csv.CsvStringEncoder;

/**
 * The implementation for the encoder and decoder of CSV strings under RFC-4180 compliancy
 * using the SuperCsv 2.1 library.
 * 
 * See {@link http://tools.ietf.org/html/rfc4180} for definition of CSV encoding format.
 */
public class Rfc4180CsvStringEncoderImpl implements CsvStringEncoder {
	
	@Override
	public List<String> decodeStringToList(final String shortTextValue, final char delimiter) {
		if (StringUtils.isEmpty(shortTextValue)) {
			return new ArrayList<>();
		}
		List<String> csvData;
		
		CsvPreference.Builder builder = new CsvPreference.Builder('\"', delimiter, "\n");
		
		try {
			csvData = new CsvListReader(new StringReader(shortTextValue), builder.build()).read();
		} catch (IOException exception) {
			throw new EpServiceException("CSV Reader error.", exception);
		} catch	(SuperCsvException exception) {
			throw new EpServiceException("The encoded string is not properly encoded in CSV: " + shortTextValue, exception);
		}
		return csvData;
	}
	
	@Override
	public String[] decodeStringToArray(final String shortTextValue, final char delimiter) {
		return decodeStringToList(shortTextValue, delimiter).toArray(new String[0]);
	}
	
	@Override
	public String encodeString(final String[] csvElements, final char delimiter) {
		if (ArrayUtils.isEmpty(csvElements)) {
			return StringUtils.EMPTY;
		}
		
		StringWriter encodedStringWriter = new StringWriter();
		CsvPreference.Builder builder = new CsvPreference.Builder('\"', delimiter, "\n");
		CsvListWriter csvWriter = new CsvListWriter(encodedStringWriter, builder.build());
		
		try {
			csvWriter.write(csvElements);
			csvWriter.flush();
			csvWriter.close();
		} catch (IOException exception) {
			throw new EpServiceException("CSV Writer error.", exception);
		} 
		
		String encodedStr = StringUtils.chomp(encodedStringWriter.toString());
		
		if (encodedStr.length() == 0) {
			return StringUtils.EMPTY;
		}
		
		return encodedStr;
	}

	@Override
	public String encodeString(final List<String> list, final char delimiter) {
		return encodeString(list.toArray(new String[list.size()]), delimiter);
	}
}
