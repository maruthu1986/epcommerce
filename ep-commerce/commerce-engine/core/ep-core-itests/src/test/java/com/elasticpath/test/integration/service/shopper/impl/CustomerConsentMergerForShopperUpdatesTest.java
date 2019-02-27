/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.test.integration.service.shopper.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.service.datapolicy.CustomerConsentService;
import com.elasticpath.service.datapolicy.DataPolicyService;
import com.elasticpath.service.shopper.impl.CustomerConsentMergerForShopperUpdates;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.integration.datapolicy.AbstractDataPolicyTest;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;

/**
 * Test functionality regarding {@link CustomerConsentMergerForShopperUpdates}.
 */
public class CustomerConsentMergerForShopperUpdatesTest extends AbstractDataPolicyTest {

	private CustomerConsentMergerForShopperUpdates customerConsentMergerForShopperUpdates;

	private SimpleStoreScenario scenario;

	@Autowired
	private CustomerConsentService customerConsentService;

	@Autowired
	private DataPolicyService dataPolicyService;

	/**
	 * Setup the tests.
	 */
	@Before
	public void setUp() {
		scenario = getTac().useScenario(SimpleStoreScenario.class);
		customerConsentMergerForShopperUpdates = getBeanFactory().getBean("customerConsentMergerForShopperUpdates");
		createAndSaveDataPolicy(DATA_POLICY_UNIQUE_CODE);
		createAndSaveDataPolicy(DATA_POLICY_UNIQUE_CODE2);
		createAndSaveDataPolicy(DATA_POLICY_UNIQUE_CODE3);

		assertThat(dataPolicyService.list()).hasSize(3);
	}

	/**
	 * Test that the correct customer consents are attached to the registered account after logging in.<br>
	 * In other words, test when an anonymous shopper logs in to a registered account, that the customer consents acknowledged, will,
	 * going forward are now associated with the registered account and not the one created for the anonymous shopper.<br>
	 * Also tests that the anonymous customer consents are no longer available in this case.
	 */
	@DirtiesDatabase
	@Test
	public void testMergingCustomerConsentsFromAnonymouslyAcceptedConsentsToCustomerAccount() {
		Customer registeredCustomer = createPersistedCustomer(scenario.getStore().getCode(), TEST_EMAIL, false);
		CustomerSession registeredCustomerSession = createCustomerSession(registeredCustomer, scenario.getCatalog());
		Shopper registeredShopper = registeredCustomerSession.getShopper();
		createAndSaveCustomerConsent(DATA_POLICY_UNIQUE_CODE, registeredCustomer);

		assertThat(customerConsentService.findByCustomerGuid(registeredCustomer.getGuid()))
				.hasSize(1);

		Customer anonymousCustomer = createPersistedCustomer(scenario.getStore().getCode(), TEST_EMAIL2, true);
		CustomerSession anonymousCustomerSession = createCustomerSession(anonymousCustomer, scenario.getCatalog());
		Shopper anonymousShopper = anonymousCustomerSession.getShopper();
		createAndSaveCustomerConsent(DATA_POLICY_UNIQUE_CODE2, anonymousCustomer);

		assertThat(customerConsentService.findByCustomerGuid(anonymousCustomer.getGuid()))
				.hasSize(1);

		customerConsentMergerForShopperUpdates.invalidateShopper(registeredCustomerSession, anonymousShopper);

		assertThat(customerConsentService.findByCustomerGuid(registeredCustomer.getGuid()))
				.hasSize(2);

		assertThat(customerConsentService.findByCustomerGuid(anonymousCustomer.getGuid()))
				.isNull();
	}


	/**
	 * Test that customer consents if the originating account is already registered.
	 * Also tests that the registered customer consents are available in this case.
	 */
	@DirtiesDatabase
	@Test
	public void testNotMergingCustomerConsentsFromRegisteredAccountAcceptedConsentsToAnotherCustomerAccount() {
		Customer secondRegisteredCustomer = createPersistedCustomer(scenario.getStore().getCode(), TEST_EMAIL, false);
		CustomerSession secondRegisteredCustomerSession = createCustomerSession(secondRegisteredCustomer, scenario.getCatalog());
		Shopper secondRegisteredShopper = secondRegisteredCustomerSession.getShopper();
		createAndSaveCustomerConsent(DATA_POLICY_UNIQUE_CODE, secondRegisteredCustomer);

		assertThat(customerConsentService.findByCustomerGuid(secondRegisteredCustomer.getGuid()))
				.hasSize(1);

		Customer originalRegisteredCustomer = createPersistedCustomer(scenario.getStore().getCode(), TEST_EMAIL2, false);
		CustomerSession originalRegisteredCustomerSession = createCustomerSession(originalRegisteredCustomer, scenario.getCatalog());
		Shopper originalRegisteredShopper = originalRegisteredCustomerSession.getShopper();
		createAndSaveCustomerConsent(DATA_POLICY_UNIQUE_CODE2, originalRegisteredCustomer);

		assertThat(customerConsentService.findByCustomerGuid(originalRegisteredCustomer.getGuid()))
				.hasSize(1);

		customerConsentMergerForShopperUpdates.invalidateShopper(secondRegisteredCustomerSession, originalRegisteredShopper);

		assertThat(customerConsentService.findByCustomerGuid(secondRegisteredCustomer.getGuid()))
				.hasSize(1);

		assertThat(customerConsentService.findByCustomerGuid(originalRegisteredCustomer.getGuid()))
				.hasSize(1);

	}

}
