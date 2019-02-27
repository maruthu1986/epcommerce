/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.subject.attribute.epcommerce;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.net.HttpHeaders;

import org.hamcrest.Matchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.identity.attribute.LocaleSubjectAttribute;
import com.elasticpath.rest.identity.attribute.SubjectAttribute;
import com.elasticpath.rest.relos.rs.subject.SubjectHeaderConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.store.StoreRepository;

/**
 * Test class for {@link LocaleSubjectAttributeLookupStrategy}.
 */
@RunWith(MockitoJUnitRunner.class)
public class LocaleSubjectAttributeLookupStrategyTest {

	private static final String SCOPE = "storecode";
	private static final Map<String, String> EMPTY_HEADERS = ImmutableMap.of();

	@Mock
	private StoreRepository storeRepository;
	@Mock
	private Store store;
	@Mock
	private HttpServletRequest mockRequest;

	@InjectMocks
	private LocaleSubjectAttributeLookupStrategy classUnderTest;


	@Test
	public void testMissingStore() {
		Iterable<SubjectAttribute> actual = classUnderTest.from(mockRequest, EMPTY_HEADERS);

		assertThat(actual, Matchers.emptyIterable());
	}

	@Test
	public void testGetLocaleFromStore() {
		Locale expectedLocale = Locale.ENGLISH;
		setupStore(expectedLocale);

		Iterable<SubjectAttribute> actualAttributes = classUnderTest.from(mockRequest, EMPTY_HEADERS);

		assertThat(actualAttributes, Matchers.iterableWithSize(1));
		LocaleSubjectAttribute subjectAttribute = (LocaleSubjectAttribute) Iterables.getFirst(actualAttributes, null);
		assertEquals("Expected store locale.", subjectAttribute.getLocale(), expectedLocale);
	}

	@Test
	public void testBadLocaleStringInHeader() {
		Locale expectedLocale = Locale.ENGLISH;
		setupStore(expectedLocale);
		Map<String, String> existing = ImmutableMap.of(LocaleSubjectAttribute.TYPE, "not a locale");

		Iterable<SubjectAttribute> actualAttributes = classUnderTest.from(mockRequest, existing);

		assertThat(actualAttributes, Matchers.iterableWithSize(1));
		LocaleSubjectAttribute subjectAttribute = (LocaleSubjectAttribute) Iterables.getFirst(actualAttributes, null);
		assertEquals("Expected store locale.", subjectAttribute.getLocale(), expectedLocale);
	}

	@Test
	public void testGetLocaleFromAttributeHeader() {
		Locale expectedLocale = Locale.ENGLISH;
		setupStore(expectedLocale);
		Map<String, String> existing = ImmutableMap.of(LocaleSubjectAttribute.TYPE, expectedLocale.toLanguageTag());

		Iterable<SubjectAttribute> actualAttributes = classUnderTest.from(mockRequest, existing);

		assertThat(actualAttributes, Matchers.iterableWithSize(1));
		LocaleSubjectAttribute subjectAttribute = (LocaleSubjectAttribute) Iterables.getFirst(actualAttributes, null);
		assertEquals("Expected attribute header locale.", subjectAttribute.getLocale(), expectedLocale);
	}

	@Test
	public void testGetLocaleFromAcceptLanguageHeader() {
		Locale expectedLocale = Locale.ENGLISH;
		setupStore(expectedLocale);
		setupAcceptLanguageHeader(expectedLocale);

		Iterable<SubjectAttribute> actualAttributes = classUnderTest.from(mockRequest, EMPTY_HEADERS);

		assertThat(actualAttributes, Matchers.iterableWithSize(1));
		LocaleSubjectAttribute subjectAttribute = (LocaleSubjectAttribute) Iterables.getFirst(actualAttributes, null);
		assertEquals("Expected accept-language  locale.", subjectAttribute.getLocale(), expectedLocale);
	}

	@Test
	public void testPreferLocaleFromAttributeHeader() {
		setupStore(Locale.FRENCH, Locale.CANADA_FRENCH, Locale.GERMAN);
		Locale expectedLocale = Locale.CANADA_FRENCH;
		setupAcceptLanguageHeader(Locale.GERMAN);
		Map<String, String> existing = ImmutableMap.of(LocaleSubjectAttribute.TYPE, expectedLocale.toLanguageTag());

		Iterable<SubjectAttribute> actualAttributes = classUnderTest.from(mockRequest, existing);

		assertThat(actualAttributes, Matchers.iterableWithSize(1));
		LocaleSubjectAttribute subjectAttribute = (LocaleSubjectAttribute) Iterables.getFirst(actualAttributes, null);
		assertEquals("Expected attribute header locale over store or accept-language locale.",
			expectedLocale, subjectAttribute.getLocale());
	}

	@Test
	public void testPreferLocaleFromAcceptLanguageWhenNoAttributeHeader() {
		setupStore(Locale.FRENCH, Locale.CANADA_FRENCH);
		Locale expectedLocale = Locale.CANADA_FRENCH;
		setupAcceptLanguageHeader(expectedLocale);

		Iterable<SubjectAttribute> actualAttributes = classUnderTest.from(mockRequest, EMPTY_HEADERS);

		assertThat(actualAttributes, Matchers.iterableWithSize(1));
		LocaleSubjectAttribute subjectAttribute = (LocaleSubjectAttribute) Iterables.getFirst(actualAttributes, null);
		assertEquals("Expected accept-language locale.", expectedLocale, subjectAttribute.getLocale());
	}


	@Test
	public void testFindBestSupportedLocaleWhenRequestHasNoPreference() {
		Locale expected = Locale.CHINA;
		when(store.getDefaultLocale()).thenReturn(expected);

		Locale actual = classUnderTest.findBestSupportedLocale(Stream.empty(), store);

		assertEquals(expected, actual);
	}

	@Test
	public void testFindBestSupportedLocaleWhenRequestHasSamePreferenceAsStore() {
		Locale expected = Locale.CHINA;
		when(store.getSupportedLocales()).thenReturn(Collections.singletonList(expected));

		Locale actual = classUnderTest.findBestSupportedLocale(Stream.of(expected), store);

		assertEquals(expected, actual);
	}

	@Test
	public void testFindBestSupportedLocaleWhenRequestHasDifferentPreferenceAsStore() {
		Locale expected = Locale.CHINA;
		when(store.getSupportedLocales()).thenReturn(Collections.singletonList(expected));
		when(store.getDefaultLocale()).thenReturn(expected);

		Locale actual = classUnderTest.findBestSupportedLocale(Stream.of(Locale.CANADA_FRENCH), store);

		assertEquals(expected, actual);
	}

	@Test
	public void testFindBestLocaleWhenSubjectHasRegionalPreferenceAndStoreHasLanguage() {
		Locale expected = Locale.ENGLISH;
		when(store.getSupportedLocales()).thenReturn(Collections.singletonList(expected));

		Locale actual = classUnderTest.findBestSupportedLocale(Stream.of(Locale.CANADA), store);

		assertEquals(expected, actual);
	}


	private void setupStore(final Locale... locales) {
		when(mockRequest.getHeaders(SubjectHeaderConstants.USER_SCOPES))
			.thenReturn(Iterators.asEnumeration(Iterators.forArray(SCOPE)));
		when(storeRepository.findStore(anyString()))
			.thenReturn(ExecutionResultFactory.createReadOK(store));
		when(store.getSupportedLocales()).thenReturn(Arrays.asList(locales));
		when(store.getDefaultLocale()).thenReturn(locales[0]);
	}

	private void setupAcceptLanguageHeader(final Locale locale) {
		when(mockRequest.getHeader(HttpHeaders.ACCEPT_LANGUAGE))
			.thenReturn(locale.toLanguageTag());
		when(mockRequest.getLocales())
			.thenReturn(Iterators.asEnumeration(Iterators.forArray(locale)));
	}
}
