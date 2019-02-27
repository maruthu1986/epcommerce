/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.importexport.common.util;

import java.util.Locale;

/**
 * Extracts a {@link Locale} instance from a localized attribute key name.
 */
public interface LocalizedAttributeKeyLocaleTranslator {

	/**
	 * Locates the locale part of the given Localized Attribute Key Name, and returns a matching {@link Locale} instance.
	 *
	 * @param attributeKeyName the localized attribute key name
	 * @return the corresponding {@link Locale} if one can be found; otherwise {@code null}
	 */
	Locale getLocaleFromLocalizedKeyName(String attributeKeyName);

	/**
	 * Locates the locale part of the given Localized Attribute Key Name, and returns a matching language tag.
	 *
	 * @param attributeKeyName the localized attribute key name
	 * @return the corresponding language tag if one can be found; otherwise {@code null}
	 */
	String getLanguageTagFromLocalizedKeyName(String attributeKeyName);

	/**
	 * <p>Returns a Locale based on the input language string.</p>
	 * <p>Previous versions of Elastic Path Commerce Engine exported this attribute as a Locale string, e.g. "en_CA".  This attribute is now
	 * encoded as a Language Tag e.g. "en-CA", as per best practices following first-party support in Java versions 1.7 and above.</p>
	 * <p>In order to maintain backwards compatibility, we continue to support unmarshalling Locale strings.  This method will convert from the
	 * former format to the current if necessary.</p>
	 *
	 * @param language the language string to convert
	 * @return a Locale
	 */
	Locale convertLocaleStringToLocale(String language);

}