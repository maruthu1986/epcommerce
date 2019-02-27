/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.csv;

import java.io.StringWriter;

import org.apache.commons.lang.StringUtils;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * DelimitedLineAggregator isn't quite good enough since it doesn't:
 * escape delimiter or quote values, nor
 * quote column values.
 * @param <T> The type of object being aggregated.
 */
public class CsvLineAggregator<T> extends DelimitedLineAggregator<T> {

	// Note: This field is shadowed as the silly library didn't allow access to it.
	private String delimiter = ","; //$NON-NLS-1$

	/**
	 * Public setter for the delimiter.
	 * @param delimiter the delimiter to set
	 */
	public void setDelimiter(final String delimiter) {
		this.delimiter = delimiter;
	}

	@Override
	public String doAggregate(final Object[] fields) {
		StringWriter stringWriter = new StringWriter();
		CSVWriter writer = new CSVWriter(stringWriter, delimiter.charAt(0));

		// TODO: Harden.
		String[] strings = new String[fields.length];
		for (int i = 0; i < fields.length; i++) {
			strings[i] = fields[i].toString();
		}

		writer.writeNext(strings);
		return StringUtils.trim(stringWriter.toString());
	}
}
