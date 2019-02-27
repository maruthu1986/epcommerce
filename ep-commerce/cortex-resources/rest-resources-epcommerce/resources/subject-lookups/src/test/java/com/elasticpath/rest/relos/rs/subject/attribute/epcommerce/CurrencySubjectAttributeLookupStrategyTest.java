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
import java.util.Currency;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

import org.hamcrest.Matchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.identity.attribute.CurrencySubjectAttribute;
import com.elasticpath.rest.identity.attribute.SubjectAttribute;
import com.elasticpath.rest.relos.rs.subject.SubjectHeaderConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.store.StoreRepository;

/**
 * Test class for {@link CurrencySubjectAttributeLookupStrategy}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CurrencySubjectAttributeLookupStrategyTest {

	static final String SCOPE = "storecode";
	static final Currency CAD_CURRENCY = Currency.getInstance("CAD");
	static final Currency USD_CURRENCY = Currency.getInstance("USD");
	static final Map<String, String> EMPTY_HEADERS = ImmutableMap.of();
	public static final String EXPECTED_CURRENCY_DOES_NOT_MATCH_RESULT = "Expected currency does not match result.";

	@Mock
	StoreRepository storeRepository;
	@Mock
	Store store;
	@Mock
	HttpServletRequest mockRequest;

	@InjectMocks
	CurrencySubjectAttributeLookupStrategy classUnderTest;

	@Test
	public void testMissingStore() {
		Iterable<SubjectAttribute> actual = classUnderTest.from(mockRequest, EMPTY_HEADERS);

		assertThat(actual, Matchers.emptyIterable());
	}

	@Test
	public void testGetCurrencyFromStore() {
		setupStore(CAD_CURRENCY);

		Iterable<SubjectAttribute> actualAttributes = classUnderTest.from(mockRequest, EMPTY_HEADERS);

		assertThat(actualAttributes, Matchers.iterableWithSize(1));
		CurrencySubjectAttribute subjectAttribute = (CurrencySubjectAttribute) Iterables.getFirst(actualAttributes, null);
		assertEquals(EXPECTED_CURRENCY_DOES_NOT_MATCH_RESULT, subjectAttribute.getCurrency(), CAD_CURRENCY);
	}

	@Test
	public void testGetCurrencyFromHeader() {
		setupStore(CAD_CURRENCY);
		Map<String, String> existing = ImmutableMap.of(CurrencySubjectAttribute.TYPE, CAD_CURRENCY.getCurrencyCode());

		Iterable<SubjectAttribute> actualAttributes = classUnderTest.from(mockRequest, existing);

		assertThat(actualAttributes, Matchers.iterableWithSize(1));
		CurrencySubjectAttribute subjectAttribute = (CurrencySubjectAttribute) Iterables.getFirst(actualAttributes, null);
		assertEquals(EXPECTED_CURRENCY_DOES_NOT_MATCH_RESULT, CAD_CURRENCY, subjectAttribute.getCurrency());
	}

	@Test
	public void testBadCurrencyFromHeader() {
		setupStore(CAD_CURRENCY);
		Map<String, String> existing = ImmutableMap.of(CurrencySubjectAttribute.TYPE, "not a currency code");

		Iterable<SubjectAttribute> actualAttributes = classUnderTest.from(mockRequest, existing);

		assertThat(actualAttributes, Matchers.iterableWithSize(1));
		CurrencySubjectAttribute subjectAttribute = (CurrencySubjectAttribute) Iterables.getFirst(actualAttributes, null);
		assertEquals(EXPECTED_CURRENCY_DOES_NOT_MATCH_RESULT, CAD_CURRENCY, subjectAttribute.getCurrency());
	}

	@Test
	public void testPreferCurrencyFromHeader() {
		setupStore(USD_CURRENCY, CAD_CURRENCY);
		Map<String, String> existing = ImmutableMap.of(CurrencySubjectAttribute.TYPE, CAD_CURRENCY.getCurrencyCode());

		Iterable<SubjectAttribute> actualAttributes = classUnderTest.from(mockRequest, existing);

		assertThat(actualAttributes, Matchers.iterableWithSize(1));
		CurrencySubjectAttribute subjectAttribute = (CurrencySubjectAttribute) Iterables.getFirst(actualAttributes, null);
		assertEquals(EXPECTED_CURRENCY_DOES_NOT_MATCH_RESULT, CAD_CURRENCY, subjectAttribute.getCurrency());
	}

	@Test
	public void testFindBestSupportedCurrencyWhenRequestHasNoPreference() {
		when(store.getDefaultCurrency()).thenReturn(USD_CURRENCY);

		Currency actual = classUnderTest.findBestSupportedCurrency(null, store);

		assertEquals(USD_CURRENCY, actual);
	}

	@Test
	public void testFindBestSupportedCurrencyWhenRequestHasSamePreferenceAsStore() {
		when(store.getSupportedCurrencies()).thenReturn(Collections.singletonList(USD_CURRENCY));

		Currency actual = classUnderTest.findBestSupportedCurrency(USD_CURRENCY, store);

		assertEquals(USD_CURRENCY, actual);
	}

	@Test
	public void testFindBestCurrencyWhenRequestHasDifferentPreferenceAsStore() {
		when(store.getSupportedCurrencies()).thenReturn(Arrays.asList(CAD_CURRENCY, USD_CURRENCY));

		Currency actual = classUnderTest.findBestSupportedCurrency(USD_CURRENCY, store);

		assertEquals(USD_CURRENCY, actual);
	}


	private void setupStore(final Currency... currencies) {
		when(mockRequest.getHeaders(SubjectHeaderConstants.USER_SCOPES))
			.thenReturn(Iterators.asEnumeration(Iterators.forArray(SCOPE)));
		when(storeRepository.findStore(anyString()))
			.thenReturn(ExecutionResultFactory.createReadOK(store));
		when(store.getSupportedCurrencies()).thenReturn(Arrays.asList(currencies));
		when(store.getDefaultCurrency()).thenReturn(currencies[0]);
	}
}
