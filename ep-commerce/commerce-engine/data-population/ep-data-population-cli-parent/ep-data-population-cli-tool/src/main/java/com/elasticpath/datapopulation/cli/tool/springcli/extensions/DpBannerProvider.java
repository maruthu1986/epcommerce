/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.cli.tool.springcli.extensions;

import static org.apache.commons.io.IOUtils.LINE_SEPARATOR;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.BannerProvider;
import org.springframework.stereotype.Component;

import com.elasticpath.datapopulation.cli.tool.DataPopulationCliApplication;

/**
 * Standard Data Population CLI {@link BannerProvider} implementation.
 */
@Component
@Order(DpBannerProvider.STANDARD_DATA_POPULATION_PROVIDER_PRECEDENCE)
public class DpBannerProvider implements BannerProvider {
	/**
	 * Offset from {@link Ordered#LOWEST_PRECEDENCE} to use by {@link #STANDARD_DATA_POPULATION_PROVIDER_PRECEDENCE}.
	 */
	protected static final int STANDARD_DATA_POPULATION_PROVIDER_OFFSET = 100;

	/**
	 * The standard ordering value to use by the standard Data Population providers. Spring Shell sets their ordering value as
	 * {@link Ordered#LOWEST_PRECEDENCE}. This value uses that minus {@link #STANDARD_DATA_POPULATION_PROVIDER_OFFSET} so that the Data Population
	 * providers take precedence but leaves room for them to be overridden if needed.
	 */
	public static final int STANDARD_DATA_POPULATION_PROVIDER_PRECEDENCE = Ordered.LOWEST_PRECEDENCE - STANDARD_DATA_POPULATION_PROVIDER_OFFSET;


	/**
	 * The value to be returned by {@link #getWelcomeMessage()}.
	 */
	protected static final String WELCOME_MESSAGE = "Please enter your command; type 'help' for available options.";

	/**
	 * The value returned by {@link #getVersion()} if the version of this application cannot be determined.
	 */
	protected static final String UNKNOWN_VERSION = "Unknown Version";

	/**
	 * The value to be returned by {@link #name()}.
	 */
	private static final String BANNER_NAME = "Elastic Path Data Population Tool";
	private String version;

	@Override
	public String name() {
		return BANNER_NAME;
	}

	/**
	 * Returns the name and version of this application.
	 *
	 * @return the name and version of this application.
	 */
	@Override
	public String getBanner() {
		final StringBuilder banner = new StringBuilder();

		banner.append(BANNER_NAME);
		banner.append(LINE_SEPARATOR);
		banner.append("Version: ");
		banner.append(getVersion());
		banner.append(LINE_SEPARATOR);

		return banner.toString();
	}

	@Override
	public String getWelcomeMessage() {
		return WELCOME_MESSAGE;
	}

	/**
	 * Calls {@link #calculateVersion()} to determine the version of this application.
	 *
	 * @return the version of this application as reported by {@link #calculateVersion()}.
	 */
	@Override
	public String getVersion() {
		if (this.version == null) {
			this.version = calculateVersion();
		}
		return this.version;
	}

	/**
	 * Sets the verson of this application if a particular version String should explicitly be used.
	 *
	 * @param version the explicit version of that this class should return.
	 */
	public void setVersion(final String version) {
		this.version = version;
	}

	/**
	 * Returns the version of this application if it can be determined, or {@link #UNKNOWN_VERSION} if it is unknown.
	 *
	 * @return the version of this application, or {@link #UNKNOWN_VERSION} if it is unknown.
	 */
	protected String calculateVersion() {
		String result = UNKNOWN_VERSION;

		final Package pkg = DataPopulationCliApplication.class.getPackage();
		if (pkg != null) {
			final String implementationVersion = pkg.getImplementationVersion();
			if (StringUtils.isNotBlank(implementationVersion)) {
				result = implementationVersion;
			}
		}

		return result;
	}
}
