/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.service.filtering.helper;

import java.util.Properties;

import org.springframework.util.StringValueResolver;

/**
 * An interface which extends the {@link StringValueResolver} interface to allow {@link Properties} objects to be added for use in resolving
 * String values.
 */
public interface PropertiesStringValueResolver extends StringValueResolver {
	/**
	 * Adds a {@link Properties} to resolve String values against. The order that {@link Properties} objects are added is normally significant if
	 * the same property key is defined in multiply added {@link Properties} objects. The first {@link Properties} object added should normally
	 * resolve the String, unless the implementing class has a compelling reason to not adhere to this behaviour.
	 *
	 * @param overridingProperties the {@link Properties} object to add.
	 */
	void addProperties(Properties overridingProperties);
}
