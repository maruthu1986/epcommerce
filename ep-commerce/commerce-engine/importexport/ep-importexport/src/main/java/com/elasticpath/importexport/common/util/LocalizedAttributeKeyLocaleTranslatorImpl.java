/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.importexport.common.util;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of {@link LocalizedAttributeKeyLocaleTranslator} that uses a regex to identify locale parts.
 */
public class LocalizedAttributeKeyLocaleTranslatorImpl implements LocalizedAttributeKeyLocaleTranslator {

	private static final String LANGUAGE_TAG_MATCH_GROUP_NAME = "languagetag";

	/**
	 * Finds the language tag portion of a localised attribute key.  For example, will match "en-CA" in the localised attribute key
	 * "my_attribute_name_en-CA".
	 * <p>
	 * The match can be found in the 'languagetag' named group.
	 */
	private static final String LANGUAGE_TAG_MATCHING_REGEX = new StringBuilder()
		// leading hyphen
		.append("(?:-)")
		// big outer match group (contains the language tag string result)
		.append("(?<" + LANGUAGE_TAG_MATCH_GROUP_NAME + ">")
		// Support for explicitly-listed legacy language tags.
		.append("(?<grandfathered>(en-GB-oed|i-ami|i-bnn|i-default|i-enochian|i-hak|")
		.append("i-klingon|i-lux|i-mingo|i-navajo|i-pwn|i-tao|i-tay|i-tsu|sgn-BE-FR")
		.append("|sgn-BE-NL|sgn-CH-DE)|(art-lojban|cel-gaulish|no-bok|no-nyn|zh-guoyu|zh-hakka|zh-min|zh-min-nan|zh-xiang)\\b)")
		// language e.g. the "en" part of "en-CA" (includes extlang)
		.append("|((?<language>([a-z]{2,3}\\b")
		// extlang is actually not used by Java.  However, it will successfully parse and consume language tags including extlang parts, and
		// strip them out when converting to a Locale instance.
		.append("(-(?<extlang>[A-Za-z]{3}(-[A-Za-z]{3}){0,2}))?))")
		// script e.g. the "Latn" part of "sr-Latn-BA"
		.append("(-(?<script>[A-Za-z]{4})\\b)?")
		// region/country e.g. the "CA" part of "en-CA"
		.append("(-(?<region>[A-Za-z]{2}|[0-9]{3})\\b)?")
		// variant e.g. the "POSIX" part of "en-US-POSIX"
		.append("(-(?<variant>[A-Za-z0-9]{5,8}|[0-9][A-Za-z0-9]{3})\\b)*")
		// extensions e.g. the complicated parts of "th-TH-u-ca-buddhist-nu-thai"
		.append("(-(?<extension>[0-9A-WY-Za-wy-z](-[A-Za-z0-9]{2,8})+)\\b)*")
		.append("(-(?<privateUse>x(-[A-Za-z0-9]{1,8})+)\\b)?)|(?<privateUse2>x(-[A-Za-z0-9]{1,8}\\b)+)")
		// end of outer match group
		.append(")$")
		.toString();

	private final Pattern languageTagMatchingPattern = Pattern.compile(LANGUAGE_TAG_MATCHING_REGEX);

	private static final String LOCALE_STRING_MATCHING_REGEX = "([^_]+)_([^_]+)_#([^-]{2,})";

	private final Pattern localeStringMatchingPattern = Pattern.compile(LOCALE_STRING_MATCHING_REGEX);

	@Override
	public Locale getLocaleFromLocalizedKeyName(final String attributeKeyName) {
		final String languageTag = getLanguageTagFromLocalizedKeyName(attributeKeyName);

		if (languageTag == null) {
			return null;
		}

		return Locale.forLanguageTag(languageTag);
	}

	@Override
	public String getLanguageTagFromLocalizedKeyName(final String attributeKeyName) {
		final Matcher matcher = getLanguageTagMatchingPattern().matcher(convertLocaleStringToLanguageTag(attributeKeyName));

		if (matcher.find()) {
			return matcher.group(LANGUAGE_TAG_MATCH_GROUP_NAME);
		}

		return null;
	}

	@Override
	public Locale convertLocaleStringToLocale(final String language) {
		if (language == null) {
			return null;
		}

		return Locale.forLanguageTag(convertLocaleStringToLanguageTag(language));
	}

	/**
	 * <p>Returns a language tag based on the input language string.</p>
	 * <p>Previous versions of Elastic Path Commerce Engine exported this attribute as a Locale string, e.g. "en_CA".  This attribute is now
	 * encoded as a Language Tag e.g. "en-CA", as per best practices following first-party support in Java versions 1.7 and above.</p>
	 * <p>In order to maintain backwards compatibility, we continue to support unmarshalling Locale strings.  This method will convert from the
	 * former format to the current if necessary.</p>
	 *
	 * @param language the language string to convert
	 * @return a string representing a valid locale language tag
	 */
	protected String convertLocaleStringToLanguageTag(final String language) {
		// Locale.toString example: sr_BA_#Latn
		// Locale.toLanguageTag example: sr-Latn-BA
		// but
		// Locale.toString example: jp_JP_#u-some-extension and th_TH_TH_#u-some-extension
		// Locale.toLanguageTag example: jp-JP-u-some-extension and th-TH-TH-u-some-extension
		return getLocaleStringMatchingPattern().matcher(language).replaceAll("$1-$3-$2")
			.replaceAll("_", "-")
			.replaceAll("#", "");
	}

	/**
	 * Returns a {@link Pattern} that will locate the language tag portion of a localised attribute key.  For example, will match "en-CA" in the
	 * localised attribute key
	 * "my_attribute_name_en-CA".
	 * <p>
	 * The match can be found in the 'languagetag' named group.
	 *
	 * @return a Pattern that locates language tags in localised attribute keys
	 */
	protected Pattern getLanguageTagMatchingPattern() {
		return languageTagMatchingPattern;
	}

	/**
	 * Returns a {@link Pattern} that will match a {@link Locale#toString locale string} and group by its constituent elements.
	 *
	 * @return a Pattern that matches locale strings
	 */
	protected Pattern getLocaleStringMatchingPattern() {
		return localeStringMatchingPattern;
	}

}