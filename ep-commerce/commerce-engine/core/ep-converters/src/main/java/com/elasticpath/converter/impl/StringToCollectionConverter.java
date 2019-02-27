/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.converter.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.converter.StringToTypeConverter;

/**
 * Converts a String to a {@link Collection} with parameterised type {@code String}.
 */
public class StringToCollectionConverter implements StringToTypeConverter<Collection<String>> {

	private static final String SEPARATOR = ",";

	@Override
	public Collection<String> convert(final String input) {
		if (StringUtils.isEmpty(input)) {
			return Collections.emptyList();
		}

		return Arrays.asList(input.split(SEPARATOR));
	}

}