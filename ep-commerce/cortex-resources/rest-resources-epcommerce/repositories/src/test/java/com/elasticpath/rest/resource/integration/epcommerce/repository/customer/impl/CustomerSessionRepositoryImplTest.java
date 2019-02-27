/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.customer.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Currency;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.store.StoreRepository;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.customer.CustomerSessionService;
import com.elasticpath.service.pricing.SessionPriceListLifecycle;
import com.elasticpath.service.shopper.ShopperService;
import com.elasticpath.tags.Tag;
import com.elasticpath.tags.TagSet;

/**
 * Tests the {@link CustomerSessionRepositoryImpl} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomerSessionRepositoryImplTest {
	private static final String RESULT_SHOULD_BE_A_FAILURE = "Result should be a failure";
	private static final String USER_GUID = "userGuid";
	private static final String STORE_CODE = "StoreCode";
	private static final String TAG_KEY = "tag";
	private static final Locale LOCALE = Locale.CANADA;
	private static final Currency CURRENCY = Currency.getInstance(LOCALE);

	private TagSet sessionTagSet;

	@Mock
	private Store mockStore;
	@Mock
	private Shopper mockShopper;
	@Mock
	private CustomerSession mockCustomerSession;
	@Mock
	private Customer mockCustomer;
	@Mock
	private ShopperService shopperService;
	@Mock
	private CustomerService customerService;
	@Mock
	private CustomerSessionService customerSessionService;
	@Mock
	private SessionPriceListLifecycle sessionPriceListLifecycle;
	@Mock
	private StoreRepository storeRepository;
	@Mock
	private ResourceOperationContext resourceOperationContext;
	@Mock
	private CustomerSessionTagSetFactory tagSetFactory;

	@InjectMocks
	private CustomerSessionRepositoryImpl customerSessionRepository;

	/**
	 * Setting up tests.
	 */
	@Before
	public void setUp() {
		mockCreateCustomerSessionWithShopper();
	}

	@Test
	public void testFindOrCreateCustomerSessionExpectedResultNotNullAndSubjectNotNull() {
		setupSubject();

		Tag tag = new Tag();
		TagSet tagSet = new TagSet();
		tagSet.addTag(TAG_KEY, tag);

		when(resourceOperationContext.getUserIdentifier()).thenReturn(USER_GUID);
		when(shopperService.findByCustomerGuidAndStoreCode(USER_GUID, STORE_CODE)).thenReturn(mockShopper);
		when(storeRepository.findStore(STORE_CODE)).thenReturn(ExecutionResultFactory.createReadOK(mockStore));
		when(mockShopper.getCustomer()).thenReturn(mockCustomer);
		when(mockStore.getCode()).thenReturn(STORE_CODE);
		when(mockStore.getDefaultLocale()).thenReturn(LOCALE);
		when(mockStore.getDefaultCurrency()).thenReturn(CURRENCY);
		when(customerSessionService.initializeCustomerSessionForPricing(mockCustomerSession, STORE_CODE, CURRENCY))
				.thenReturn(mockCustomerSession);
		when(tagSetFactory.createTagSet(mockCustomer)).thenReturn(tagSet);

		ExecutionResult<CustomerSession> result = customerSessionRepository.findOrCreateCustomerSession();
		assertEquals("The customer session returned should be same as expected", mockCustomerSession, result.getData());
		assertEquals("Expected tag not found in customer session tag set.", tag, sessionTagSet.getTagValue(TAG_KEY));
		verify(sessionPriceListLifecycle).refreshPriceListStack(mockCustomerSession, mockStore);
	}

	@Test
	public void testFindOrCreateCustomerSessionExpectedResultNotNullAndSubjectIsNull() {

		when(resourceOperationContext.getUserIdentifier()).thenReturn(USER_GUID);

		Tag tag = new Tag();
		TagSet tagSet = new TagSet();
		tagSet.addTag(TAG_KEY, tag);

		when(shopperService.findByCustomerGuid(USER_GUID)).thenReturn(mockShopper);
		when(storeRepository.findStore(STORE_CODE)).thenReturn(ExecutionResultFactory.createReadOK(mockStore));
		when(mockShopper.getStoreCode()).thenReturn(STORE_CODE);
		when(mockShopper.getCustomer()).thenReturn(mockCustomer);
		when(mockStore.getCode()).thenReturn(STORE_CODE);
		when(mockStore.getDefaultLocale()).thenReturn(LOCALE);
		when(mockStore.getDefaultCurrency()).thenReturn(CURRENCY);
		when(customerSessionService.initializeCustomerSessionForPricing(mockCustomerSession, STORE_CODE, CURRENCY))
				.thenReturn(mockCustomerSession);
		when(tagSetFactory.createTagSet(mockCustomer)).thenReturn(tagSet);

		ExecutionResult<CustomerSession> result = customerSessionRepository.findOrCreateCustomerSession();
		assertEquals("The customer session returned should be same as expected", mockCustomerSession, result.getData());
		assertEquals("Expected tag not found in customer session tag set.", tag, sessionTagSet.getTagValue(TAG_KEY));
		verify(sessionPriceListLifecycle).refreshPriceListStack(mockCustomerSession, mockStore);
	}

	@Test
	public void testFindOrCreateCustomerSessionWhenCustomerNotFound() {
		setupSubject();
		when(resourceOperationContext.getUserIdentifier()).thenReturn(USER_GUID);

		when(customerService.findByGuid(USER_GUID)).thenReturn(mockCustomer);

		ExecutionResult<CustomerSession> result = customerSessionRepository.findOrCreateCustomerSession();

		assertTrue(RESULT_SHOULD_BE_A_FAILURE, result.isFailure());
		assertEquals("Result should be not found message", ResourceStatus.NOT_FOUND, result.getResourceStatus());
	}

	@Test
	public void testFindOrCreateCustomerSessionWhenExceptionIsThrown() {
		setupSubject();
		when(resourceOperationContext.getUserIdentifier()).thenReturn(USER_GUID);

		when(customerService.findByGuid(USER_GUID)).thenThrow(new EpServiceException("Error in customer service"));

		ExecutionResult<CustomerSession> result = customerSessionRepository.findOrCreateCustomerSession();

		assertTrue(RESULT_SHOULD_BE_A_FAILURE, result.isFailure());
		assertEquals("Result should be not found message", ResourceStatus.SERVER_ERROR, result.getResourceStatus());
	}

	@Test
	public void testCreateCustomerSessionWhenStoreLookupNotFoundExpectedResultIsFailure() {
		setupSubject();

		when(mockCustomer.getGuid()).thenReturn(USER_GUID);

		ExecutionResult<CustomerSession> result = customerSessionRepository.findCustomerSessionByGuid(mockCustomer.getGuid());

		assertTrue(RESULT_SHOULD_BE_A_FAILURE, result.isFailure());
		assertEquals("Result should be not found message", ResourceStatus.NOT_FOUND, result.getResourceStatus());
	}

	@Test
	public void testCreateCustomerSessionWhenExceptionIsThrownExpectedResultIsFailure() {
		setupSubject();
		when(shopperService.findByCustomerGuidAndStoreCode(USER_GUID, STORE_CODE)).thenReturn(mockShopper);
		when(mockShopper.getCustomer()).thenReturn(mockCustomer);
		when(mockCustomer.getGuid()).thenReturn(USER_GUID);
		when(storeRepository.findStore(STORE_CODE)).thenReturn(ExecutionResultFactory.createReadOK(mockStore));
		when(mockStore.getCode()).thenReturn(STORE_CODE);
		when(mockStore.getDefaultLocale()).thenReturn(LOCALE);

		ExecutionResult<CustomerSession> result = customerSessionRepository.findCustomerSessionByGuid(mockCustomer.getGuid());

		assertTrue(RESULT_SHOULD_BE_A_FAILURE, result.isFailure());
		assertEquals("Result should be a server Error", ResourceStatus.SERVER_ERROR, result.getResourceStatus());
	}

	@Test
	public void testFindLocaleWhenSubjectHasNoLocale() {
		Subject subject = TestSubjectFactory.createWithScopeAndUserId(STORE_CODE, USER_GUID);
		when(resourceOperationContext.getSubject()).thenReturn(subject);
		when(mockStore.getDefaultLocale()).thenReturn(LOCALE);

		Locale actual = customerSessionRepository.findLocaleForCurrentOperation(mockStore);

		assertEquals(LOCALE, actual);
	}

	@Test
	public void testFindLocaleWhenSubjectHasLocale() {
		setupSubject();
		when(mockStore.getDefaultLocale()).thenReturn(LOCALE);

		Locale actual = customerSessionRepository.findLocaleForCurrentOperation(mockStore);

		assertEquals(LOCALE, actual);
	}

	@Test
	public void testFindCurrencyWhenSubjectHasNoCurrency() {
		Subject subject = TestSubjectFactory.createWithScopeAndUserId(STORE_CODE, USER_GUID);
		when(resourceOperationContext.getSubject()).thenReturn(subject);
		when(mockStore.getDefaultCurrency()).thenReturn(CURRENCY);

		Currency actual = customerSessionRepository.findCurrencyForCurrentOperation(mockStore);

		assertEquals(CURRENCY, actual);
	}

	@Test
	public void testFindCurrencyWhenSubjectHasCurrency() {
		setupSubject();
		when(mockStore.getDefaultCurrency()).thenReturn(CURRENCY);

		Currency actual = customerSessionRepository.findCurrencyForCurrentOperation(mockStore);

		assertEquals(CURRENCY, actual);
	}

	private void mockCreateCustomerSessionWithShopper() {
		sessionTagSet = new TagSet();

		when(customerService.findByGuid(USER_GUID)).thenReturn(mockCustomer);
		when(customerSessionService.createWithShopper(mockShopper)).thenReturn(mockCustomerSession);
		when(mockCustomerSession.getCustomerTagSet()).thenReturn(sessionTagSet);
	}

	private void setupSubject() {
		Subject subject = TestSubjectFactory.createWithScopeAndUserIdAndLocaleAndCurrency(
				STORE_CODE, USER_GUID, LOCALE, CURRENCY);

		when(resourceOperationContext.getSubject()).thenReturn(subject);
	}
}