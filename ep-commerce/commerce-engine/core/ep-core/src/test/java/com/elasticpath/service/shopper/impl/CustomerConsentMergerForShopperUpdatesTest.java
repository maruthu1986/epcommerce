/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shopper.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.datapolicy.ConsentAction;
import com.elasticpath.domain.datapolicy.CustomerConsent;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.domain.datapolicy.impl.CustomerConsentImpl;
import com.elasticpath.domain.datapolicy.impl.DataPolicyImpl;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.service.datapolicy.CustomerConsentService;

/**
 * Tests {@link CustomerConsentMergerForShopperUpdates}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomerConsentMergerForShopperUpdatesTest {

	private CustomerConsentMergerForShopperUpdates customerConsentMergerForShopperUpdates;

	@Mock
	private CustomerConsentService customerConsentService;

	@Mock
	private CustomerSession customerSession;

	private static final String MYSTERY_MAN = "MysteryMan";
	private static final String REGULAR_JOE = "RegularJoe";
	private static final String REGULAR_JANE = "RegularJane";

	private static final String GUID = "guid";
	private static final String GUID1 = "guid1";
	private static final String GUID2 = "guid2";
	private static final String DATA_POLICY_GUID = "dataPolicyGuid";
	private static final String CUSTOMER = "Customer";

	@Before
	public void setUp() {
		customerConsentMergerForShopperUpdates = new CustomerConsentMergerForShopperUpdates(customerConsentService);
	}

	@Test
	public void testCustomerConsentMergeWhenTransitioningFromAnonymousToRegistered() {
		final String anonymousCustomerGuid = MYSTERY_MAN + CUSTOMER;
		final CustomerConsent customerConsent = createCustomerConsent(GUID, anonymousCustomerGuid);
		final CustomerConsent customerConsent1 = createCustomerConsent(GUID2, anonymousCustomerGuid);
		Shopper anonymousShopper = createShopper(anonymousCustomerGuid, Arrays.asList(customerConsent, customerConsent1), true);

		Shopper registeredShopper = createRegisteredCustomerWithConsents();

		when(customerSession.getShopper()).thenReturn(registeredShopper);

		customerConsentMergerForShopperUpdates.invalidateShopper(customerSession, anonymousShopper);

		verify(customerConsentService).updateCustomerGuids(
				Arrays.asList(customerConsent.getUidPk(), customerConsent1.getUidPk()), registeredShopper.getCustomer().getGuid());
	}

	@Test
	public void testCustomerConsentMergeWhenTransitioningFromAnonymousToRegisteredWithoutExistingConsents() {
		final String anonymousCustomerGuid = MYSTERY_MAN + CUSTOMER;
		final CustomerConsent customerConsent = createCustomerConsent(GUID, anonymousCustomerGuid);
		final CustomerConsent customerConsent1 = createCustomerConsent(GUID2, anonymousCustomerGuid);
		Shopper anonymousShopper = createShopper(anonymousCustomerGuid, Arrays.asList(customerConsent, customerConsent1), true);

		Shopper registeredShopper = createRegisteredCustomerWithoutConsents();

		when(customerSession.getShopper()).thenReturn(registeredShopper);

		customerConsentMergerForShopperUpdates.invalidateShopper(customerSession, anonymousShopper);

		verify(customerConsentService).updateCustomerGuids(
				Arrays.asList(customerConsent.getUidPk(), customerConsent1.getUidPk()), registeredShopper.getCustomer().getGuid());
	}

	@Test
	public void testNoCustomerConsentMergeWhenInvalidShopperHasNoCustomerConsents() {
		final String anonymousCustomerGuid = MYSTERY_MAN + CUSTOMER;
		Shopper anonymousShopper = createShopper(anonymousCustomerGuid, null, true);

		Shopper registeredShopper = createRegisteredCustomerWithConsents();

		customerConsentMergerForShopperUpdates.invalidateShopper(customerSession, anonymousShopper);

		verify(customerConsentService, never()).updateCustomerGuids(
				Collections.emptyList(), registeredShopper.getCustomer().getGuid());
	}

	@Test
	public void testNoCustomerConsentMergeWhenSwithingFromRegisteredToRegisteredCustomer() {
		final String originalRegisteredCustomerGuid = REGULAR_JANE + CUSTOMER;
		final CustomerConsent customerConsent = createCustomerConsent(GUID, originalRegisteredCustomerGuid);
		final CustomerConsent customerConsent1 = createCustomerConsent(GUID2, originalRegisteredCustomerGuid);
		Shopper anonymousShopper = createShopper(originalRegisteredCustomerGuid, Arrays.asList(customerConsent, customerConsent1), false);

		Shopper registeredShopper = createRegisteredCustomerWithConsents();

		customerConsentMergerForShopperUpdates.invalidateShopper(customerSession, anonymousShopper);

		verify(customerConsentService, never()).updateCustomerGuids(
				Arrays.asList(customerConsent.getUidPk(), customerConsent1.getUidPk()), registeredShopper.getCustomer().getGuid());
	}


	private Shopper createShopper(final String customerGuid, final List<CustomerConsent> customerConsent, final boolean anonymous) {
		final Shopper shopper = mock(Shopper.class, customerGuid + "Shopper");
		final Customer customer = mock(Customer.class, customerGuid + CUSTOMER);

		when(shopper.getCustomer()).thenReturn(customer);
		when(customer.isAnonymous()).thenReturn(anonymous);
		when(customer.getGuid()).thenReturn(customerGuid);

		when(customerConsentService.findByCustomerGuid(customerGuid)).thenReturn(customerConsent);
		return shopper;
	}

	private CustomerConsent createCustomerConsent(final String guid, final String customerGuid) {
		DataPolicy dataPolicy = new DataPolicyImpl();
		dataPolicy.setGuid(DATA_POLICY_GUID);

		CustomerConsent customerConsent = new CustomerConsentImpl();
		customerConsent.setGuid(guid);
		customerConsent.setCustomerGuid(customerGuid);
		customerConsent.setDataPolicy(dataPolicy);
		customerConsent.setConsentDate(new Date());
		customerConsent.setAction(ConsentAction.GRANTED);
		return customerConsent;
	}

	private Shopper createRegisteredCustomerWithConsents() {
		final String registeredCustomerGuid = REGULAR_JOE + CUSTOMER;
		final CustomerConsent customerConsent1 = createCustomerConsent(GUID1, registeredCustomerGuid);
		return createShopper(registeredCustomerGuid, Collections.singletonList(customerConsent1), false);
	}

	private Shopper createRegisteredCustomerWithoutConsents() {
		final String registeredCustomerGuid = REGULAR_JOE + CUSTOMER;
		return createShopper(registeredCustomerGuid, null, false);
	}

}
