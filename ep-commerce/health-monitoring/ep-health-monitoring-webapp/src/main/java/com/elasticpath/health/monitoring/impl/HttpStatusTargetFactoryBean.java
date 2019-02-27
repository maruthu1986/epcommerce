/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.health.monitoring.impl;

import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.split;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.FactoryBean;

import com.elasticpath.health.monitoring.StatusCheckerTarget;

/**
 * Factory bean used to instantiate a list of HttpStatusTargetImpl objects
 * based on a comma-separated list of endpoint URLs.
 */
public class HttpStatusTargetFactoryBean implements FactoryBean<List<StatusCheckerTarget>> {

	private static final char COMMA_DELIMITER = ',';
	private static final char NAME_SEPARATOR = '|';

	private final List<StatusCheckerTarget> httpStatusTargetList = new ArrayList<>();

	private String endpointUrls;

	@Override
	public List<StatusCheckerTarget> getObject() {
		if (StringUtils.isEmpty(endpointUrls)) {
			return Collections.singletonList(new InvalidEndpointStatusTarget());
		}

		for (String endpoint : split(endpointUrls, COMMA_DELIMITER)) {
			HttpStatusTargetImpl httpStatusTarget = new HttpStatusTargetImpl();

			String name = endpoint;
			String url = endpoint;
			if (contains(endpoint, NAME_SEPARATOR)) {
				String[] endpointParts = split(endpoint, NAME_SEPARATOR);
				name = endpointParts[0];
				url = endpointParts[1];
			}

			httpStatusTarget.setName(name.trim());
			httpStatusTarget.setUrl(url.trim());

			httpStatusTargetList.add(httpStatusTarget);
		}

		return httpStatusTargetList;
	}

	@Override
	public Class<?> getObjectType() {
		return List.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	/**
	 * Sets the comma separated list of endpoints urls.
	 *
	 * @param endpointUrls the comma separated list of endpoint urls
	 */
	public void setEndpointUrls(final String endpointUrls) {
		this.endpointUrls = endpointUrls;
	}

	/**
	 * Gets the comma separated list of endpoint urls.
	 *
	 * @return the comma separated list of endpoint urls
	 */
	protected String getEndpointsUrls() {
		return endpointUrls;
	}
}
