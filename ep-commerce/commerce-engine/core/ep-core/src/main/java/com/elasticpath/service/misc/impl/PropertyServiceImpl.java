/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.misc.impl;

import java.util.Map;
import java.util.Properties;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.persistence.PropertiesDao;
import com.elasticpath.service.misc.PropertyService;

/**
 * This service provides domain objects relating to system properties.
 */
public class PropertyServiceImpl implements PropertyService {

	private PropertiesDao propertiesDao;
	private Map<String, Properties> propertiesMap;

	/**
	 * Returns a properties map where the keys are properties file names
	 * and the values are Properties objects.
	 * @return the Map of properties file names to Properties objects
	 */
	@Override
	public Map<String, Properties> getPropertiesMap() {
		if (propertiesMap == null) {
			sanityCheck();
			propertiesMap = propertiesDao.loadProperties();
		}
		return propertiesMap;
	}

	/**
	 * Set the DAO used to load properties.
	 * @param propertiesDao the DAO used to load properties.
	 */
	@Override
	public void setPropertiesDao(final PropertiesDao propertiesDao) {
		this.propertiesDao = propertiesDao;
	}

	/** Checks that the required objects have been set. */
	protected void sanityCheck() {
		if (propertiesDao == null) {
			throw new EpServiceException("The properties Dao has not been set.");
		}
	}
}
