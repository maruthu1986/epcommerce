/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.importexport.common.util;

import java.util.Locale;

import org.apache.commons.lang.LocaleUtils;

/**
 * Utilities for I/E.
 */
public class ImportExportUtil {

	private static ImportExportUtil instance = new ImportExportUtil();

	/**
	 * Get the singleton instance.
	 *
	 * @return the instance
	 */
	public static ImportExportUtil getInstance() {
		return instance;
	}

	/**
	 * Validates a locale.
	 *
	 * @param localeString the locale to validate.
	 * @return the locale
	 */
	public Locale validateLocale(final String localeString) {
		Locale locale = LocaleUtils.toLocale(localeString);
		if (!LocaleUtils.isAvailableLocale(locale)) {
			throw new IllegalArgumentException("Could not find locale with code " + locale + ". Locale is not supported.");
		}
		return locale;
	}
}
