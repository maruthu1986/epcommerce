/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.ql.parser.valueresolver.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.elasticpath.ql.parser.EpQLFieldType;
import com.elasticpath.ql.parser.EpQLTerm;
import com.elasticpath.ql.parser.EpQuery;
import com.elasticpath.ql.parser.gen.ParseException;
import com.elasticpath.ql.parser.valueresolver.EpQLValueResolver;

/**
 * Checks a value to be in a set of allowed enum values.
 */
public class EnumValueResolver implements EpQLValueResolver {

	private Set<String> enumValues;

	private String allowedValues;

	/**
	 * Checks that epQLTerm contains configured allowed enum value.
	 * 
	 * @param epQLTerm EPQL term
	 * @param fieldType type of field value passed in EPQL query
	 * @param epQuery the epQuery
	 * @return list of values corresponding to given value of EpQL field
	 * @throws ParseException if value is not allowed enum value or value is quoted or the field type is not enum
	 */
	@Override
	public List<String> resolve(final EpQLTerm epQLTerm, final EpQLFieldType fieldType, final EpQuery epQuery) throws ParseException {
		verifyFieldTypeIsEnum(epQLTerm, fieldType);
		verifyQuoteAbsenceValue(epQLTerm);
		verifyEnumValue(epQLTerm);

		final List<String> resolvedValues = new ArrayList<>();
		resolvedValues.add(epQLTerm.getQueryText());
		return resolvedValues;
	}

	private void verifyFieldTypeIsEnum(final EpQLTerm epQLTerm, final EpQLFieldType fieldType) throws ParseException {
		if (fieldType != EpQLFieldType.ENUM) {
			throw new ParseException("The field <" + epQLTerm.getEpQLField().getFieldName() + "> should be of type enum");
		}
	}

	private void verifyEnumValue(final EpQLTerm epQLTerm) throws ParseException {
		if (!enumValues.contains(epQLTerm.getQueryText())) {
			throw new ParseException("Unexpected enum value. Allowed values are [" + allowedValues + "]");
		}
	}

	private void verifyQuoteAbsenceValue(final EpQLTerm epQLTerm) throws ParseException {
		if (epQLTerm.getQueryText().indexOf('\'') > -1) {
			throw new ParseException("Enum can not contain quoted value for: " + epQLTerm.getEpQLField().getFieldName());
		}
	}

	/**
	 * Sets allowed values for the configured enum.
	 *
	 * @param enumValues a set of allowed enum values
	 */
	public void setEnumValues(final Set<String> enumValues) {
		this.enumValues = enumValues;
		this.allowedValues = getAllowedValues();
	}

	/**
	 * Cashes enum values as String just to print error message decently.
	 *
	 * @return
	 */
	private String getAllowedValues() {
		StringBuilder builder = new StringBuilder();
		for (String value : enumValues) {
			builder.append(value).append(',');
		}

		//remove trailing comma
		int bufferLength = builder.length();
		if (bufferLength > 0) {
			builder.delete(bufferLength - 1, bufferLength);
		}

		return builder.toString();
	}

}
