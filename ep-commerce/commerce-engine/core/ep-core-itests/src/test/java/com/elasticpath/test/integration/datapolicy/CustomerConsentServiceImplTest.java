/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.test.integration.datapolicy;

import static org.assertj.core.api.Assertions.assertThat;

import static com.elasticpath.service.search.solr.SolrIndexConstants.STORE_CODE;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.datapolicy.ConsentAction;
import com.elasticpath.domain.datapolicy.CustomerConsent;
import com.elasticpath.domain.datapolicy.DataPoint;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.domain.datapolicy.DataPolicyState;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;

public class CustomerConsentServiceImplTest extends AbstractDataPolicyTest {

	private SimpleStoreScenario scenario;

	/**
	 * Get a reference to TestApplicationContext for use within the test. Setup scenarios.
	 */
	@Before
	public void setUp() throws Exception {
		scenario = getTac().useScenario(SimpleStoreScenario.class);
	}

	@Test
	@DirtiesDatabase
	public void verifySavesNewCustomerConsentWhenUsingSaveMethod() {
		CustomerConsent customerConsent =
				createAndSaveCustomerConsent(scenario.getStore().getCode(), CUSTOMER_CONSENT_UNIQUE_CODE, TEST_EMAIL);

		assertThat(customerConsent)
				.isNotNull();
		assertThat(customerConsent.isPersisted())
				.isTrue();
	}

	@Test
	@DirtiesDatabase
	public void verifyUpdatesCustomerConsentWithNewCustomerGUIDWhenUsinguUpdateCustomerGuidsMethod() {
		CustomerConsent customerConsent =
				createAndSaveCustomerConsent(scenario.getStore().getCode(), CUSTOMER_CONSENT_UNIQUE_CODE, TEST_EMAIL);

		Customer newCustomer = createPersistedCustomer(scenario.getStore().getCode(), TEST_EMAIL2, true);

		customerConsentService.updateCustomerGuids(Collections.singletonList(customerConsent.getUidPk()), newCustomer.getGuid());

		CustomerConsent updateCustomerConsent = customerConsentService.get(customerConsent.getUidPk());

		assertThat(updateCustomerConsent)
				.isNotNull();
		assertThat(updateCustomerConsent.getCustomerGuid())
				.isEqualTo(newCustomer.getGuid());
	}

	@Test
	@DirtiesDatabase
	public void verifyLoadsCustomerConsentWhenUsingGetMethodAndCustomerConsentExists() {
		CustomerConsent customerConsent =
				createAndSaveCustomerConsent(scenario.getStore().getCode(), CUSTOMER_CONSENT_UNIQUE_CODE, TEST_EMAIL);
		long uidPk = customerConsent.getUidPk();

		customerConsent = customerConsentService.get(uidPk);

		assertThat(customerConsent)
				.isNotNull();
	}

	@Test
	@DirtiesDatabase
	public void verifyLoadsCustomerConsentWhenUsingLoadMethodAndCustomerConsentExists() {
		CustomerConsent customerConsent =
				createAndSaveCustomerConsent(scenario.getStore().getCode(), CUSTOMER_CONSENT_UNIQUE_CODE, TEST_EMAIL);
		long uidPk = customerConsent.getUidPk();

		customerConsent = customerConsentService.load(uidPk);

		assertThat(customerConsent)
				.isNotNull();
	}

	@Test
	@DirtiesDatabase
	public void verifyReturnsCustomerConsentBeanWhenUsingLoadMethodAndPassedInUidPKIsLessThanZero() {
		CustomerConsent customerConsent = customerConsentService.load(-1);

		assertThat(customerConsent)
				.isNotNull();
		assertThat(customerConsent.isPersisted())
				.isFalse();
	}

	@Test
	@DirtiesDatabase
	public void verifyReturnsCustomerConsentBeanWhenUsingGetMethodAndPassedInUidPKIsLessThanZero() {
		CustomerConsent customerConsent = customerConsentService.get(-1);

		assertThat(customerConsent)
				.isNotNull();
		assertThat(customerConsent.isPersisted())
				.isFalse();
	}

	@Test
	@DirtiesDatabase
	public void verifyReturnsListOfAllCustomerConsentsWhenUsingListMethod() {
		createAndSaveCustomerConsent(scenario.getStore().getCode(), CUSTOMER_CONSENT_UNIQUE_CODE, TEST_EMAIL);

		List<CustomerConsent> all = customerConsentService.list();

		assertThat(all)
				.isNotEmpty();
	}

	@Test
	@DirtiesDatabase
	public void verifyLoadsCustomerConsentWhenUsingFindByGuidAndCustomerConsentExists() {
		CustomerConsent customerConsent =
				createAndSaveCustomerConsent(scenario.getStore().getCode(), CUSTOMER_CONSENT_UNIQUE_CODE, TEST_EMAIL);

		customerConsent = customerConsentService.findByGuid(customerConsent.getGuid());

		assertThat(customerConsent)
				.isNotNull();
	}

	@Test
	@DirtiesDatabase
	public void verifyLoadsCustomerConsentWhenUsingFindByGuidListAndCustomerConsentExists() {
		CustomerConsent customerConsent =
				createAndSaveCustomerConsent(scenario.getStore().getCode(), CUSTOMER_CONSENT_UNIQUE_CODE, TEST_EMAIL);
		CustomerConsent customerConsent2 =
				createAndSaveCustomerConsent(scenario.getStore().getCode(), CUSTOMER_CONSENT_UNIQUE_CODE2, TEST_EMAIL2);

		List<CustomerConsent> customerConsents =
				customerConsentService.findByGuids(Arrays.asList(customerConsent.getGuid(), customerConsent2.getGuid()));

		assertThat(customerConsents)
				.isNotNull()
				.hasSize(2);
	}

	@Test
	@DirtiesDatabase
	public void verifyReturnsNullWhenUsingFindByGuidListAndCustomerConsentsDoNotExists() {
		List<CustomerConsent> customerConsents = customerConsentService.findByGuids(Arrays.asList(CUSTOMER_CONSENT_UNIQUE_CODE3));

		assertThat(customerConsents)
				.isNull();
	}

	@Test
	@DirtiesDatabase
	public void findByDataPolicyGuidForCustomerLatestReturnsCustomerConsentWhenConsentExistsForDataPolicy() {
		Customer customer = createPersistedCustomer(scenario.getStore().getCode(), TEST_EMAIL, true);
		DataPolicy dataPolicy = createAndSaveDataPolicy(DATA_POLICY_UNIQUE_CODE);
		CustomerConsent customerConsent = createAndSaveCustomerConsent(CUSTOMER_CONSENT_UNIQUE_CODE, customer, dataPolicy, new Date());

		CustomerConsent foundCustomerConsent = customerConsentService
				.findByDataPolicyGuidForCustomerLatest(dataPolicy.getGuid(), customer.getGuid());

		assertThat(foundCustomerConsent)
				.isEqualTo(customerConsent);
	}

	@Test
	@DirtiesDatabase
	public void verifyFindByDataPolicyGuidForCustomerLatestReturnsNullWhenNoConsentExists() {
		CustomerConsent foundCustomerConsent = customerConsentService.findByDataPolicyGuidForCustomerLatest(DATA_POLICY_UNIQUE_CODE,
				TEST_EMAIL);

		assertThat(foundCustomerConsent)
				.isNull();
	}

	@Test
	@DirtiesDatabase
	public void verifyFindWithActiveDataPoliciesByCustomerGuid() {
		Customer customer = createPersistedCustomer(scenario.getStore().getCode(), TEST_EMAIL, true);
		DataPolicy activeDataPolicy = createAndSaveDataPolicyWithState(DATA_POLICY_UNIQUE_CODE, DataPolicyState.ACTIVE);
		createAndSaveCustomerConsent(CUSTOMER_CONSENT_UNIQUE_CODE, customer, activeDataPolicy, new Date());

		List<CustomerConsent> customerConsents = customerConsentService.findWithActiveDataPoliciesByCustomerGuid(customer.getGuid(), false);

		assertThat(customerConsents)
				.hasSize(1);

		CustomerConsent consent = customerConsents.get(0);
		assertThat(consent.getDataPolicy().getState())
				.isEqualTo(DataPolicyState.ACTIVE);
	}

	@Test
	@DirtiesDatabase
	public void verifyFindWithActiveAndDisabledDataPoliciesByCustomerGuid() {
		Date customerConsentDate = new Date();
		Customer customer = createPersistedCustomer(scenario.getStore().getCode(), TEST_EMAIL, true);
		DataPolicy activeDataPolicy = createAndSaveDataPolicyWithState(DATA_POLICY_UNIQUE_CODE, DataPolicyState.ACTIVE);
		DataPolicy disabledDataPolicy = createAndSaveDataPolicyWithState(DATA_POLICY_UNIQUE_CODE2, DataPolicyState.DISABLED);

		createAndSaveCustomerConsent(CUSTOMER_CONSENT_UNIQUE_CODE, customer, activeDataPolicy, customerConsentDate);
		createAndSaveCustomerConsent(CUSTOMER_CONSENT_UNIQUE_CODE2, customer, disabledDataPolicy, customerConsentDate);

		List<CustomerConsent> customerConsents = customerConsentService.findWithActiveDataPoliciesByCustomerGuid(customer.getGuid(), true);

		assertThat(customerConsents)
				.hasSize(2);

		List<DataPolicyState> dataPolicyStates = customerConsents.stream()
				.map(consent -> consent.getDataPolicy().getState())
				.collect(Collectors.toList());

		assertThat(dataPolicyStates)
				.containsExactly(DataPolicyState.ACTIVE, DataPolicyState.DISABLED);
	}

	@Test
	@DirtiesDatabase
	public void verifyFindWithActiveAndDisabledDataPoliciesByCustomerGuidIgnorningOtherCustomersConsents() {
		DataPolicy activeDataPolicy = createAndSaveDataPolicyWithState(DATA_POLICY_UNIQUE_CODE, DataPolicyState.ACTIVE);
		DataPolicy disabledDataPolicy = createAndSaveDataPolicyWithState(DATA_POLICY_UNIQUE_CODE2, DataPolicyState.DISABLED);

		Date customerConsentDate = new Date();
		Customer customer = createPersistedCustomer(scenario.getStore().getCode(), TEST_EMAIL, true);
		createAndSaveCustomerConsent(CUSTOMER_CONSENT_UNIQUE_CODE, customer, activeDataPolicy, customerConsentDate);

		Customer customer2 = createPersistedCustomer(scenario.getStore().getCode(), TEST_EMAIL2, true);
		createAndSaveCustomerConsent(CUSTOMER_CONSENT_UNIQUE_CODE2, customer2, disabledDataPolicy, customerConsentDate);

		List<CustomerConsent> customerConsents = customerConsentService.findWithActiveDataPoliciesByCustomerGuid(customer.getGuid(), true);

		assertThat(customerConsents)
				.hasSize(1);

		CustomerConsent consent = customerConsents.get(0);
		assertThat(consent.getDataPolicy().getState())
				.isEqualTo(DataPolicyState.ACTIVE);
	}

	@Test
	@DirtiesDatabase
	public void findByDataPolicyGuidForCustomerLatestReturnsLatestCustomerConsentWhenMoreThanOneConsentExists() {
		Customer customer = createPersistedCustomer(scenario.getStore().getCode(), TEST_EMAIL, true);
		DataPolicy dataPolicy = createAndSaveDataPolicy(DATA_POLICY_UNIQUE_CODE);
		CustomerConsent customerConsent1 = createAndSaveCustomerConsent(CUSTOMER_CONSENT_UNIQUE_CODE, customer, dataPolicy, YESTERDAY_DATE);
		CustomerConsent customerConsent2 = createAndSaveCustomerConsent(CUSTOMER_CONSENT_UNIQUE_CODE, customer, dataPolicy, TODAY_DATE);

		CustomerConsent foundCustomerConsent = customerConsentService
				.findByDataPolicyGuidForCustomerLatest(dataPolicy.getGuid(), customer.getGuid());

		assertThat(foundCustomerConsent)
				.isEqualTo(customerConsent2)
				.isNotEqualTo(customerConsent1);
	}


	@Test
	@DirtiesDatabase
	public void customerHasGivenConsentForAtLeastOneDataPolicyReturnsTrueWhenAcceptedDataPolicyExists() {
		DataPolicy dataPolicy = createAndSaveDataPolicy(STORE_CODE);
		DataPoint dataPoint = createAndSaveDataPoint(DATA_POINT_NAME, DATA_POINT_KEY_1, DATA_POINT_LOCATION);
		dataPolicy.getDataPoints().add(dataPoint);
		dataPolicyService.update(dataPolicy);

		Customer customer = createPersistedCustomer(scenario.getStore().getCode(), TEST_EMAIL, true);
		createAndSaveCustomerConsent(CUSTOMER_CONSENT_UNIQUE_CODE, customer, dataPolicy, YESTERDAY_DATE);

		assertThat(customerConsentService.customerHasGivenConsentForAtLeastOneDataPolicy(customer.getGuid(), Sets.newHashSet(dataPolicy)))
				.isTrue();
	}

	@Test
	@DirtiesDatabase
	public void customerHasGivenConsentForAtLeastOneDataPolicyReturnsFalseWhenNoDataPoliciesAccepted() {
		DataPolicy dataPolicy = createAndSaveDataPolicy(STORE_CODE);
		DataPoint dataPoint = createAndSaveDataPoint(DATA_POINT_NAME, DATA_POINT_KEY_1, DATA_POINT_LOCATION);
		dataPolicy.getDataPoints().add(dataPoint);
		dataPolicyService.update(dataPolicy);

		Customer customer = createPersistedCustomer(scenario.getStore().getCode(), TEST_EMAIL, true);

		assertThat(customerConsentService.customerHasGivenConsentForAtLeastOneDataPolicy(customer.getGuid(), Sets.newHashSet(dataPolicy)))
				.isFalse();
	}

	@Test
	@DirtiesDatabase
	public void customerHasGivenConsentForAtLeastOneDataPolicyReturnsTrueWhenTheMostRecentDataPolicyIsAccepted() {
		Customer customer = createPersistedCustomer(scenario.getStore().getCode(), TEST_EMAIL, true);
		DataPolicy dataPolicy = createAndSaveDataPolicy(DATA_POLICY_UNIQUE_CODE);
		createAndSaveCustomerConsent(CUSTOMER_CONSENT_UNIQUE_CODE, customer, dataPolicy, ConsentAction.REVOKED, TODAY_DATE);
		createAndSaveCustomerConsent(CUSTOMER_CONSENT_UNIQUE_CODE, customer, dataPolicy, ConsentAction.GRANTED, TODAY_DATE);

		assertThat(customerConsentService.customerHasGivenConsentForAtLeastOneDataPolicy(customer.getGuid(), Sets.newHashSet(dataPolicy)))
				.isTrue();
	}
}
