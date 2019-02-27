/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.commons.util.csv.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

import com.elasticpath.commons.util.csv.CsvStringEncoder;

/**
 * The implementation for the encoder and decoder of CSV strings for EP's legacy system.
 * 
 * This encoder parses by splitting CSV data on every comma. Thus, it does not allow
 * embedded commas in the data. See <code>RfcCompliantCsvEncoder</code> for a RFC-compliant
 * encoder.
 **/
public class LegacyCsvStringEncoderImpl implements CsvStringEncoder {

	@Override
	public List<String> decodeStringToList(final String csvLine, final char delimiter) {
		if (Strings.isNullOrEmpty(csvLine)) {
			return Collections.emptyList();
		}
		List<String> multiValueList = new ArrayList<>();
		multiValueList.addAll(Splitter.on(delimiter).trimResults().splitToList(csvLine));
		return multiValueList;
	}

	@Override
	public String[] decodeStringToArray(final String csvLine, final char delimiter) {
		return Iterables.toArray(decodeStringToList(csvLine, delimiter), String.class);
	}

	@Override
	public String encodeString(final String[] csvElements, final char delimiter) {
		if (csvElements == null) {
			return "";
		}
		return Joiner.on(delimiter).useForNull("").join(csvElements);
	}

	@Override
	public String encodeString(final List<String> csvElements, final char delimiter) {
		return Joiner.on(delimiter).useForNull("").join(csvElements);
	}

}
