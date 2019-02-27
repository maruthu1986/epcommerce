/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.environment.impl;

import java.io.File;
import javax.servlet.ServletContext;

import org.springframework.web.context.ServletContextAware;

import com.elasticpath.service.environment.EnvironmentInfoService;

/**
 * Service for determining information about current running environment paths.
 */
public class WebAppEnvironmentInfoService implements ServletContextAware, EnvironmentInfoService {

	private String applicationPath;

	@Override
	public String getApplicationRootPath() {
		return applicationPath;
	}

	@Override
	public String getConfigurationRootPath() {
		return getApplicationRootPath() + File.separator + "WEB-INF";
	}

	@Override
	public void setServletContext(final ServletContext servletContext) {
		applicationPath = servletContext.getRealPath("");
	}
}