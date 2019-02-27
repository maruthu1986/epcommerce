/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.misc;

import java.util.Map;
import java.util.Properties;

import com.elasticpath.persistence.PropertiesDao;

/**
 * This service provides access to domain objects containing
 * property (file) information, such as country information.
 */
public interface PropertyService {

	/**
	 * Returns a properties map where the keys are properties file names
	 * and the values are Properties objects.
	 * @return the Map of properties file names to Properties objects
	 */
	Map<String, Properties> getPropertiesMap();

	/**
	 * Set the DAO used to load properties.
	 * @param propertiesDao the properties DAO used to retrieve country information.
	 */
	void setPropertiesDao(PropertiesDao propertiesDao);

}
