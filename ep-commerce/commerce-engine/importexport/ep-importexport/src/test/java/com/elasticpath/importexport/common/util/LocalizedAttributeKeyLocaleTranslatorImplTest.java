/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.importexport.common.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

/**
 * LocalizedAttributeKeyLocaleTranslatorImplTest.
 */
public class LocalizedAttributeKeyLocaleTranslatorImplTest {

	private LocalizedAttributeKeyLocaleTranslatorImpl translator;

	/**
	 * This is a sample set of locales supported by Java 7+ that covers the bases of language tag complexity.
	 */
	private static final String[] SAMPLE_SUPPORTED_LOCALE_LANGUAGE_TAGS = new String[]{
		// Easy examples
		"en",
		"fr",
		"es",
		"ar-AE",
		"be-BY",
		"ca-ES",
		"da-DK",
		"el-CY",
		"fi-FI",
		"ga-IE",
		"hi-IN",
		"it-IT",
		"ja-JP",
		"ko-KR",
		"lt-LT",
		"mk-MK",
		"nl-BE",
		"pl-PL",
		"ro-RO",
		"sk-SK",
		"th-TH",
		"uk-UA",
		"vi-VN",
		"zh-CN",

		// Cunning counterexamples
		"agq",
		"agq-CM",
		"ar-001",
		"en-029",
		"es-419",
		"mn-Mong-CN",
		"sr-Latn-BA",
		"sr-Latn-ME",
		"sr-Latn-RS",

		// Downright devilish
		"en-US-POSIX",
		"ja-JP-u-ca-japanese",
		"th-TH-u-ca-buddhist",
		"th-TH-u-ca-buddhist-nu-thai",
	};

	private static final String[] UNLOCALISED_ATTRIBUTE_KEYS = new String[]{
		"I'm not a locale",
		"Neither am I.",
		"foooo-baaar",
		"attribute-name",
		"attributename",
		"attributenameen",
		"attributenameen-CA",
		"attribute-en-CA-name"
	};

	@Before
	public void setUp() {
		translator = new LocalizedAttributeKeyLocaleTranslatorImpl();
	}

	@Test
	public void verifyLocalesLanguageTagsCanBeTranslated() throws Exception {
		final String attributeName = "attribute_name";

		// Converts language-tag strings (e.g. from Locale.toLanguageTag) to Java locale-string strings (e.g. from Locale.toString) and appends
		// them to the end of a sample attribute name.
		final Map<String, String> languageTagToAttributeNames = Arrays.stream(SAMPLE_SUPPORTED_LOCALE_LANGUAGE_TAGS)
			.map(Locale::forLanguageTag)
			.collect(
				Collectors.toMap(Locale::toLanguageTag, locale -> attributeName + "_" + locale));

		for (final Map.Entry<String, String> entry : languageTagToAttributeNames.entrySet()) {
			final String actualLanguageTag = translator.getLanguageTagFromLocalizedKeyName(entry.getValue());
			assertEquals("Unexpected language tag produced by translator", entry.getKey(), actualLanguageTag);
		}
	}

	@Test
	public void verifyLocalesCanBeTranslated() throws Exception {
		final String attributeName = "attribute_name";

		// Converts language-tag strings (e.g. from Locale.toLanguageTag) to Java locale-string strings (e.g. from Locale.toString) and appends
		// them to the end of a sample attribute name.
		final Map<Locale, String> languageTagToAttributeNames = Arrays.stream(SAMPLE_SUPPORTED_LOCALE_LANGUAGE_TAGS)
			.map(Locale::forLanguageTag)
			.collect(
				Collectors.toMap(locale -> locale, locale -> attributeName + "_" + locale));

		for (final Map.Entry<Locale, String> entry : languageTagToAttributeNames.entrySet()) {
			final Locale actualLocale = translator.getLocaleFromLocalizedKeyName(entry.getValue());
			assertEquals("Unexpected locale produced by translator", entry.getKey(), actualLocale);
		}
	}

	@Test
	public void verifyNullLocaleLanguageTagReturnedWhenNoLocalePartOfKey() throws Exception {
		for (final String unlocalisedAttributeKey : UNLOCALISED_ATTRIBUTE_KEYS) {
			final String languageTag = translator.getLanguageTagFromLocalizedKeyName(unlocalisedAttributeKey);
			assertNull("Expected no language tag to be returned when a key does not contain a locale value", languageTag);
		}
	}

	@Test
	public void verifyNullLocaleReturnedWhenNoLocalePartOfKey() throws Exception {
		for (final String unlocalisedAttributeKey : UNLOCALISED_ATTRIBUTE_KEYS) {
			final Locale locale = translator.getLocaleFromLocalizedKeyName(unlocalisedAttributeKey);
			assertNull("Expected no Locale to be returned when a key does not contain a locale value", locale);
		}
	}

}