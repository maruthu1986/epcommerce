/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.service.remote.impl;

import java.util.Map;
import java.util.Properties;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.persistence.PropertiesDao;
import com.elasticpath.service.remote.ResourceRetrievalService;

/**
 * Provides the contents of various configuration files as Strings.
 */
public class ResourceRetrievalServiceImpl implements ResourceRetrievalService {

	private PropertiesDao propertiesDao;


	@Override
	public Map<String, Properties> getProperties() throws EpServiceException {
		return getPropertiesDao().loadProperties();
	}

	public void setPropertiesDao(final PropertiesDao propertiesDao) {
		this.propertiesDao = propertiesDao;
	}

	protected PropertiesDao getPropertiesDao() {
		return propertiesDao;
	}
}
