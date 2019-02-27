/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.commons.util;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.elasticpath.base.exception.EpSystemException;

/**
 * The class {@link org.springframework.cache.ehcache.EhCacheManagerFactoryBean} doesn't check whether given resource,
 * provided via "configLocation" property, exists or not and throws NullPointerException in case when doesn't and stream is requested.
 * <p>
 * EhcacheConfigurationLoader class is a workaround for this issue, using {@link #getResource} method
 * that throws EpSystemException when resource doesn't exist or returns null when resource path name is empty or equals null.
 */
public class EhcacheConfigurationLoader {

	private String pathname;

	/**
	 * Returns {@link org.springframework.core.io.Resource}, if exists.
	 *
	 * @return file system resource or null if pathname is empty or null.
	 * @throws EpSystemException when resource with given pathname doesn't exist.
	 */
	public Resource getResource() {

		if (StringUtils.isEmpty(pathname) || "null".equals(pathname)) {
			return null;
		}

		String path = pathname
				.replace("file://", "")
				.replace("${user.home}", System.getProperty("user.home"));

		File file = new File(path);
		if (!file.exists()) {
			throw new EpSystemException("EhCache configuration file not found: " + pathname);
		}
		return new FileSystemResource(file);
	}

	public void setPathname(final String pathname) {
		this.pathname = pathname;
	}
}
