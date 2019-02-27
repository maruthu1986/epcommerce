/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.domain.misc;

import java.util.Locale;

import com.elasticpath.persistence.api.Persistable;

/**
 * The interface for supported locale.
 */
public interface SupportedLocale extends Persistable {

	/**
	 * Get the locale.
	 * @return the locale
	 */
	Locale getLocale();

	/**
	 * Set the locale.
	 * @param locale the locale to set
	 */
	void setLocale(Locale locale);

}