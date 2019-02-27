/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.tags.service.impl;

import java.util.Map;

import org.apache.log4j.Logger;

import com.elasticpath.converter.ConversionMalformedValueException;
import com.elasticpath.converter.StringToTypeConverter;
import com.elasticpath.converter.impl.StringToStringConverter;
import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.domain.TagValueType;
import com.elasticpath.tags.service.TagTypeValueConverter;

/**
 * Converts tag value string to a value object.
 */
public class TagTypeValueConverterImpl implements TagTypeValueConverter {

	private Map<String, StringToTypeConverter<?>> typeConverterMap;

	private static final Logger LOG = Logger.getLogger(TagTypeValueConverterImpl.class);

	@Override
	public Object convertValueTypeToTagJavaType(final TagDefinition tagDefinition, final String tagValue) {
		Object convertedValue;
		String javaType = getJavaType(tagDefinition);
		StringToTypeConverter<?> typeConverter = getTypeConverter(javaType);
		try {
			convertedValue = typeConverter.convert(tagValue);
		} catch (ConversionMalformedValueException e) {
			LOG.debug("Error when converting tag value [" + tagValue
					+ "] to java type [" + javaType + "] defaulting to string", e);
			// a known problem but at the moment no better solution but simply
			// to return the value as string, since we need the error message
			// to return back from the validation engine and we need at least some
			// sort of value for it to fail.
			convertedValue = tagValue;
		}

		return convertedValue;
	}

	private String getJavaType(final TagDefinition tagDefinition) {
		String javaType;
		if (tagDefinition == null) {
			javaType = "UNKNOWN";
		} else {
			TagValueType valueType = tagDefinition.getValueType();
			javaType = valueType.getJavaType();
		}
		return javaType;
	}

	private StringToTypeConverter<?> getTypeConverter(final String javaType) {
		StringToTypeConverter<?> typeConverter = typeConverterMap.get(javaType);
		if (typeConverter == null) {
			LOG.debug("Cannot find the type converter for java type [" + javaType + "] defaulting to String");
			typeConverter = new StringToStringConverter();
		}

		return typeConverter;
	}

	public void setTypeConverterMap(final Map<String, StringToTypeConverter<?>> typeConverterMap) {
		this.typeConverterMap = typeConverterMap;
	}
}
