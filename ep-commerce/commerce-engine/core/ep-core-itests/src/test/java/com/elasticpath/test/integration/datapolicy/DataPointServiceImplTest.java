/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.test.integration.datapolicy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.datapolicy.ConsentAction;
import com.elasticpath.domain.datapolicy.CustomerConsent;
import com.elasticpath.domain.datapolicy.DataPoint;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;

public class DataPointServiceImplTest extends AbstractDataPolicyTest {

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
	public void verifySavesNewDataPointWhenUsingSaveMethod() {
		DataPoint dataPoint = createAndSaveDataPoint(DATA_POINT_NAME, DATA_POINT_KEY_1, DATA_POINT_LOCATION);

		assertThat(dataPoint)
				.isNotNull();
		assertThat(dataPoint.isPersisted())
				.isTrue();
	}

	@Test
	@DirtiesDatabase
	public void verifyLoadsDataPointWhenUsingGetMethodAndDataPointExists() {
		DataPoint dataPoint = createAndSaveDataPoint(DATA_POINT_NAME, DATA_POINT_KEY_1, DATA_POINT_LOCATION);
		long uidPk = dataPoint.getUidPk();

		dataPoint = dataPointService.get(uidPk);

		assertThat(dataPoint)
				.isNotNull();
	}

	@Test
	@DirtiesDatabase
	public void verifyLoadsDataPointWhenUsingLoadMethodAndDataPointExists() {
		DataPoint dataPoint = createAndSaveDataPoint(DATA_POINT_NAME, DATA_POINT_KEY_1, DATA_POINT_LOCATION);
		long uidPk = dataPoint.getUidPk();

		dataPoint = dataPointService.load(uidPk);

		assertThat(dataPoint)
				.isNotNull();
	}

	@Test
	@DirtiesDatabase
	public void verifyLoadsDataPointWhenUsingFindByGuidAndDataPointExists() {
		DataPoint dataPoint = createAndSaveDataPoint(DATA_POINT_NAME, DATA_POINT_KEY_1, DATA_POINT_LOCATION);

		dataPoint = dataPointService.findByGuid(dataPoint.getGuid());

		assertThat(dataPoint)
				.isNotNull();
	}

	@Test
	@DirtiesDatabase
	public void verifyLoadsDataPointWhenUsingFindByGuidListAndDataPointsExists() {
		DataPoint dataPoint = createAndSaveDataPoint(DATA_POINT_NAME, DATA_POINT_KEY_1, DATA_POINT_LOCATION);
		DataPoint dataPoint2 = createAndSaveDataPoint(DATA_POINT_NAME_2, DATA_POINT_KEY_2, DATA_POINT_LOCATION);

		List<DataPoint> dataPoints = dataPointService.findByGuids(Arrays.asList(dataPoint.getGuid(), dataPoint2.getGuid()));

		assertThat(dataPoints)
				.isNotNull()
				.hasSize(2);
	}

	@Test
	@DirtiesDatabase
	public void verifyReturnsNullWhenUsingFindByGuidListAndDataPointsDoNotExists() {
		List<DataPoint> dataPoints = dataPointService.findByGuids(Collections.singletonList(DATA_POINT_UNIQUE_CODE));

		assertThat(dataPoints)
				.isNull();
	}

	@Test
	@DirtiesDatabase
	public void verifyReturnsDataPointBeanWhenUsingLoadMethodPassedInUidPKIsLessThanZero() {
		DataPoint dataPoint = dataPointService.load(-1);

		assertThat(dataPoint)
				.isNotNull();
		assertThat(dataPoint.isPersisted())
				.isFalse();
	}

	@Test
	@DirtiesDatabase
	public void verifyReturnsDataPointBeanWhenUsingGetMethodPassedInUidPKIsLessThanZero() {
		DataPoint dataPoint = dataPointService.get(-1);

		assertThat(dataPoint)
				.isNotNull();
		assertThat(dataPoint.isPersisted())
				.isFalse();
	}

	@Test
	@DirtiesDatabase
	public void verifyReturnsListOfAllDataPoliciesWhenUsingListMethod() {
		createAndSaveDataPoint(DATA_POINT_NAME, DATA_POINT_KEY_1, DATA_POINT_LOCATION);

		List<DataPoint> all = dataPointService.list();

		assertThat(all)
				.isNotEmpty();
	}


	@Test
	@DirtiesDatabase
	public void verifyDbExceptionIsThrownWhenTryingToSaveDataPointWithExistingKeyLocationCombination() {
		createAndSaveDataPoint(DATA_POINT_NAME, DATA_POINT_KEY_1, DATA_POINT_LOCATION);
		assertThatThrownBy(() -> createAndSaveDataPoint(DATA_POINT_NAME, DATA_POINT_KEY_1, DATA_POINT_LOCATION))
				.isInstanceOf(DataIntegrityViolationException.class);
	}

	@Test
	@DirtiesDatabase
	public void verifyFindByKeyAndLocationReturnsDataPointWhenExists() {
		createAndSaveDataPoint(DATA_POINT_NAME, DATA_POINT_KEY_1, DATA_POINT_LOCATION);

		DataPoint dataPoint = dataPointService.findByDataLocationAndDataKey(DATA_POINT_LOCATION, DATA_POINT_KEY_1);
		assertThat(dataPoint)
				.isNotNull();
	}

	@Test
	@DirtiesDatabase
	public void verifyFindByKeyAndLocationReturnsNullWhenDoesntExist() {
		DataPoint dataPoint = dataPointService.findByDataLocationAndDataKey(DATA_POINT_LOCATION, DATA_POINT_KEY_1);
		assertThat(dataPoint)
				.isNull();
	}

	@Test
	@DirtiesDatabase
	public void verifyFindUniqueForDataPolicyReturnsDataPointWhenFound() {
		Customer customer = createPersistedCustomer(scenario.getStore().getCode(), TEST_EMAIL, true);
		Date customerConsentDate = new Date();

		DataPoint dataPoint1 = createAndSaveDataPoint(DATA_POINT_NAME, DATA_POINT_KEY_1, DATA_POINT_LOCATION, true);
		DataPoint dataPoint2 = createAndSaveDataPoint(DATA_POINT_NAME_2, DATA_POINT_KEY_2, DATA_POINT_LOCATION, true);

		DataPolicy dataPolicy1 = createAndSaveDataPolicy(DATA_POLICY_UNIQUE_CODE);
		dataPolicy1.setDataPoints(Arrays.asList(dataPoint1, dataPoint2));
		dataPolicy1 = dataPolicyService.update(dataPolicy1);

		DataPolicy dataPolicy2 = createAndSaveDataPolicy(DATA_POLICY_UNIQUE_CODE2);
		dataPolicy2.setDataPoints(Collections.singletonList(dataPoint1));
		dataPolicy2 = dataPolicyService.update(dataPolicy2);

		createAndSaveCustomerConsent(CUSTOMER_CONSENT_UNIQUE_CODE, customer, dataPolicy1, customerConsentDate);
		createAndSaveCustomerConsent(CUSTOMER_CONSENT_UNIQUE_CODE2, customer, dataPolicy2, customerConsentDate);

		List<DataPoint> dataPoints = dataPointService.findUniqueRemovableForDataPolicyAndCustomer(dataPolicy1.getGuid(), customer.getGuid());

		assertThat(dataPoints)
				.containsExactly(dataPoint2);

	}

	@Test
	@DirtiesDatabase
	public void verifyDbExceptionIsThrownWhenTryingToSaveDataPointWithExistingName() {
		createAndSaveDataPoint(DATA_POINT_NAME);
		assertThatThrownBy(() -> createAndSaveDataPoint(DATA_POINT_NAME))
				.isInstanceOf(DataIntegrityViolationException.class);
	}

	@Test
	@DirtiesDatabase
	public void verifyFindByNameReturnsDataPointWhenExists() {
		createAndSaveDataPoint(DATA_POINT_NAME);

		DataPoint dataPoint = dataPointService.findByName(DATA_POINT_NAME);
		assertThat(dataPoint)
				.isNotNull();
	}

	@Test
	@DirtiesDatabase
	public void verifyFindByNameReturnsDataPointWhenExistsAndDoesntMatchCase() {
		createAndSaveDataPoint(DATA_POINT_NAME.toUpperCase(Locale.US));

		DataPoint dataPoint = dataPointService.findByName(DATA_POINT_NAME.toLowerCase(Locale.US));
		assertThat(dataPoint)
				.isNotNull();
	}

	@Test
	@DirtiesDatabase
	public void verifyFindByNameReturnsNullWhenDoesntExist() {
		DataPoint dataPoint = dataPointService.findByName(DATA_POINT_NAME);
		assertThat(dataPoint)
				.isNull();
	}

	@Test
	@DirtiesDatabase
	public void verifyFindWithRevokedConsentsLatestReturnsEmptyMapWhenNoDataPointFound() {
		Map<String, Map<DataPoint, Set<DataPolicy>>> customerDataPoints = dataPointService.findWithRevokedConsentsLatest();

		assertThat(customerDataPoints)
				.isEmpty();
	}

	@Test
	@DirtiesDatabase
	public void verifyFindWithRevokedConsentsLatestReturnsEmptyMapWhenNoRemovableDataPointFound() {
		DataPoint dataPoint = createDataPoint(DATA_POINT_NAME, DATA_POINT_KEY_1, DATA_POINT_LOCATION);
		dataPoint.setRemovable(false);
		dataPointService.save(dataPoint);

		Map<String, Map<DataPoint, Set<DataPolicy>>> customerDataPoints = dataPointService.findWithRevokedConsentsLatest();

		assertThat(customerDataPoints)
				.isEmpty();
	}

	@Test
	@DirtiesDatabase
	public void verifyFindWithRevokedConsentsLatestReturnsOneKeyOneDataPointOneDataPolicyMapWhenOnlyOneDataPointFound() {
		DataPolicy dataPolicy = createAndSaveDataPolicy(DATA_POLICY_UNIQUE_CODE);
		DataPoint dataPoint = createAndSaveDataPoint(DATA_POINT_NAME);
		dataPolicy.getDataPoints().add(dataPoint);

		dataPolicyService.update(dataPolicy);

		Customer registeredCustomer = createPersistedCustomer(scenario.getStore().getCode(), TEST_EMAIL, false);
		CustomerConsent customerConsent = createCustomerConsent(UUID.randomUUID().toString(), dataPolicy, ConsentAction.REVOKED);
		customerConsent.setCustomerGuid(registeredCustomer.getGuid());
		customerConsentService.save(customerConsent);

		Map<String, Map<DataPoint, Set<DataPolicy>>> customerDataPoints = dataPointService.findWithRevokedConsentsLatest();

		assertThat(customerDataPoints)
				.containsOnlyKeys(registeredCustomer.getGuid());

		assertThat(customerDataPoints.get(registeredCustomer.getGuid()))
				.containsOnlyKeys(dataPoint);

		assertThat(customerDataPoints.get(registeredCustomer.getGuid()).get(dataPoint))
				.containsOnly(dataPolicy);
	}

	@Test
	@DirtiesDatabase
	public void verifyFindWithGrantedConsentsLatestReturnsOneKeyOneDataPointOneDataPolicyMapWhenOnlyOneDataPointFound() {
		DataPolicy dataPolicy = createAndSaveDataPolicy(DATA_POLICY_UNIQUE_CODE);
		DataPoint dataPoint = createAndSaveDataPoint(DATA_POINT_NAME);
		dataPolicy.getDataPoints().add(dataPoint);

		dataPolicyService.update(dataPolicy);

		Customer registeredCustomer = createPersistedCustomer(scenario.getStore().getCode(), TEST_EMAIL, false);
		CustomerConsent customerConsent = createCustomerConsent(UUID.randomUUID().toString(), dataPolicy, ConsentAction.GRANTED);
		customerConsent.setCustomerGuid(registeredCustomer.getGuid());
		customerConsentService.save(customerConsent);

		Map<String, Map<DataPoint, Set<DataPolicy>>> customerDataPoints = dataPointService.findWithGrantedConsentsLatest();

		assertThat(customerDataPoints)
				.containsOnlyKeys(registeredCustomer.getGuid());

		assertThat(customerDataPoints.get(registeredCustomer.getGuid()))
				.containsOnlyKeys(dataPoint);

		assertThat(customerDataPoints.get(registeredCustomer.getGuid()).get(dataPoint))
				.containsOnly(dataPolicy);
	}

	@Test
	@DirtiesDatabase
	public void verifyFindWithRevokedConsentsLatestReturnsOneKeyOneDataPointTwoDataPolicyMap() {
		DataPolicy dataPolicy1 = createAndSaveDataPolicy(DATA_POLICY_UNIQUE_CODE);
		DataPolicy dataPolicy2 = createAndSaveDataPolicy(DATA_POLICY_UNIQUE_CODE2);
		DataPoint dataPoint = createAndSaveDataPoint(DATA_POINT_NAME);
		dataPolicy1.getDataPoints().add(dataPoint);
		dataPolicy2.getDataPoints().add(dataPoint);

		dataPolicyService.update(dataPolicy1);
		dataPolicyService.update(dataPolicy2);

		Customer registeredCustomer = createPersistedCustomer(scenario.getStore().getCode(), TEST_EMAIL, false);
		CustomerConsent customerConsent1 = createCustomerConsent(UUID.randomUUID().toString(), dataPolicy1, ConsentAction.REVOKED);
		customerConsent1.setCustomerGuid(registeredCustomer.getGuid());
		CustomerConsent customerConsent2 = createCustomerConsent(UUID.randomUUID().toString(), dataPolicy2, ConsentAction.REVOKED);
		customerConsent2.setCustomerGuid(registeredCustomer.getGuid());
		customerConsentService.save(customerConsent1);
		customerConsentService.save(customerConsent2);

		Map<String, Map<DataPoint, Set<DataPolicy>>> customerDataPoints = dataPointService.findWithRevokedConsentsLatest();

		assertThat(customerDataPoints)
				.containsOnlyKeys(registeredCustomer.getGuid());

		assertThat(customerDataPoints.get(registeredCustomer.getGuid()))
				.containsOnlyKeys(dataPoint);

		assertThat(customerDataPoints.get(registeredCustomer.getGuid()).get(dataPoint))
				.containsOnly(dataPolicy1, dataPolicy2);
	}

	@Test
	@DirtiesDatabase
	public void verifyFindWithGrantedConsentsLatestReturnsOneKeyOneDataPointTwoDataPolicyMap() {
		DataPolicy dataPolicy1 = createAndSaveDataPolicy(DATA_POLICY_UNIQUE_CODE);
		DataPolicy dataPolicy2 = createAndSaveDataPolicy(DATA_POLICY_UNIQUE_CODE2);
		DataPoint dataPoint = createAndSaveDataPoint(DATA_POINT_NAME);
		dataPolicy1.getDataPoints().add(dataPoint);
		dataPolicy2.getDataPoints().add(dataPoint);

		dataPolicyService.update(dataPolicy1);
		dataPolicyService.update(dataPolicy2);

		Customer registeredCustomer = createPersistedCustomer(scenario.getStore().getCode(), TEST_EMAIL, false);
		CustomerConsent customerConsent1 = createCustomerConsent(UUID.randomUUID().toString(), dataPolicy1, ConsentAction.GRANTED);
		customerConsent1.setCustomerGuid(registeredCustomer.getGuid());
		CustomerConsent customerConsent2 = createCustomerConsent(UUID.randomUUID().toString(), dataPolicy2, ConsentAction.GRANTED);
		customerConsent2.setCustomerGuid(registeredCustomer.getGuid());
		customerConsentService.save(customerConsent1);
		customerConsentService.save(customerConsent2);

		Map<String, Map<DataPoint, Set<DataPolicy>>> customerDataPoints = dataPointService.findWithGrantedConsentsLatest();

		assertThat(customerDataPoints)
				.containsOnlyKeys(registeredCustomer.getGuid());

		assertThat(customerDataPoints.get(registeredCustomer.getGuid()))
				.containsOnlyKeys(dataPoint);

		assertThat(customerDataPoints.get(registeredCustomer.getGuid()).get(dataPoint))
				.containsOnly(dataPolicy1, dataPolicy2);
	}

	@Test
	@DirtiesDatabase
	public void verifyFindWithRevokedConsentsLatestReturnsOneKeyTwoDataPointOneDataPolicyMap() {
		DataPolicy dataPolicy1 = createAndSaveDataPolicy(DATA_POLICY_UNIQUE_CODE);
		DataPolicy dataPolicy2 = createAndSaveDataPolicy(DATA_POLICY_UNIQUE_CODE2);
		DataPoint dataPoint1 = createAndSaveDataPoint(DATA_POINT_NAME, DATA_POINT_KEY_1, DATA_POINT_LOCATION);
		DataPoint dataPoint2 = createAndSaveDataPoint(DATA_POINT_NAME_2, DATA_POINT_KEY_2, DATA_POINT_LOCATION);
		dataPolicy1.getDataPoints().add(dataPoint1);
		dataPolicy2.getDataPoints().add(dataPoint2);

		dataPolicyService.update(dataPolicy1);
		dataPolicyService.update(dataPolicy2);

		Customer registeredCustomer = createPersistedCustomer(scenario.getStore().getCode(), TEST_EMAIL, false);

		CustomerConsent customerConsent1 = createCustomerConsent(UUID.randomUUID().toString(), dataPolicy1, ConsentAction.REVOKED);
		customerConsent1.setCustomerGuid(registeredCustomer.getGuid());
		CustomerConsent customerConsent2 = createCustomerConsent(UUID.randomUUID().toString(), dataPolicy2, ConsentAction.REVOKED);
		customerConsent2.setCustomerGuid(registeredCustomer.getGuid());

		customerConsentService.save(customerConsent1);
		customerConsentService.save(customerConsent2);

		Map<String, Map<DataPoint, Set<DataPolicy>>> customerDataPoints = dataPointService.findWithRevokedConsentsLatest();

		assertThat(customerDataPoints)
				.containsOnlyKeys(registeredCustomer.getGuid());

		assertThat(customerDataPoints.get(registeredCustomer.getGuid()))
				.containsOnlyKeys(dataPoint1, dataPoint2);

		assertThat(customerDataPoints.get(registeredCustomer.getGuid()).get(dataPoint1))
				.containsOnly(dataPolicy1);

		assertThat(customerDataPoints.get(registeredCustomer.getGuid()).get(dataPoint2))
				.containsOnly(dataPolicy2);
	}

	@Test
	@DirtiesDatabase
	public void verifyFindWithGrantedConsentsLatestReturnsOneKeyTwoDataPointOneDataPolicyMap() {
		DataPolicy dataPolicy1 = createAndSaveDataPolicy(DATA_POLICY_UNIQUE_CODE);
		DataPolicy dataPolicy2 = createAndSaveDataPolicy(DATA_POLICY_UNIQUE_CODE2);
		DataPoint dataPoint1 = createAndSaveDataPoint(DATA_POINT_NAME, DATA_POINT_KEY_1, DATA_POINT_LOCATION);
		DataPoint dataPoint2 = createAndSaveDataPoint(DATA_POINT_NAME_2, DATA_POINT_KEY_2, DATA_POINT_LOCATION);
		dataPolicy1.getDataPoints().add(dataPoint1);
		dataPolicy2.getDataPoints().add(dataPoint2);

		dataPolicyService.update(dataPolicy1);
		dataPolicyService.update(dataPolicy2);

		Customer registeredCustomer = createPersistedCustomer(scenario.getStore().getCode(), TEST_EMAIL, false);

		CustomerConsent customerConsent1 = createCustomerConsent(UUID.randomUUID().toString(), dataPolicy1, ConsentAction.GRANTED);
		customerConsent1.setCustomerGuid(registeredCustomer.getGuid());
		CustomerConsent customerConsent2 = createCustomerConsent(UUID.randomUUID().toString(), dataPolicy2, ConsentAction.GRANTED);
		customerConsent2.setCustomerGuid(registeredCustomer.getGuid());

		customerConsentService.save(customerConsent1);
		customerConsentService.save(customerConsent2);

		Map<String, Map<DataPoint, Set<DataPolicy>>> customerDataPoints = dataPointService.findWithGrantedConsentsLatest();

		assertThat(customerDataPoints)
				.containsOnlyKeys(registeredCustomer.getGuid());

		assertThat(customerDataPoints.get(registeredCustomer.getGuid()))
				.containsOnlyKeys(dataPoint1, dataPoint2);

		assertThat(customerDataPoints.get(registeredCustomer.getGuid()).get(dataPoint1))
				.containsOnly(dataPolicy1);

		assertThat(customerDataPoints.get(registeredCustomer.getGuid()).get(dataPoint2))
				.containsOnly(dataPolicy2);
	}

	@Test
	@DirtiesDatabase
	public void verifyFindWithRevokedConsentsLatestReturnsTwoKeysTwoDataPointOneDataPolicyMap() {
		DataPolicy dataPolicy1 = createAndSaveDataPolicy(DATA_POLICY_UNIQUE_CODE);
		DataPolicy dataPolicy2 = createAndSaveDataPolicy(DATA_POLICY_UNIQUE_CODE2);
		DataPoint dataPoint1 = createAndSaveDataPoint(DATA_POINT_NAME, DATA_POINT_KEY_1, DATA_POINT_LOCATION);
		DataPoint dataPoint2 = createAndSaveDataPoint(DATA_POINT_NAME_2, DATA_POINT_KEY_2, DATA_POINT_LOCATION);
		dataPolicy1.getDataPoints().add(dataPoint1);
		dataPolicy2.getDataPoints().add(dataPoint2);

		dataPolicyService.update(dataPolicy1);
		dataPolicyService.update(dataPolicy2);

		Customer registeredCustomer = createPersistedCustomer(scenario.getStore().getCode(), TEST_EMAIL, false);
		Customer registeredCustomer2 = createPersistedCustomer(scenario.getStore().getCode(), TEST_EMAIL2, false);

		CustomerConsent customerConsent1 = createCustomerConsent(UUID.randomUUID().toString(), dataPolicy1, ConsentAction.REVOKED);
		customerConsent1.setCustomerGuid(registeredCustomer.getGuid());
		CustomerConsent customerConsent2 = createCustomerConsent(UUID.randomUUID().toString(), dataPolicy2, ConsentAction.REVOKED);
		customerConsent2.setCustomerGuid(registeredCustomer.getGuid());

		CustomerConsent customerConsent3 = createCustomerConsent(UUID.randomUUID().toString(), dataPolicy1, ConsentAction.REVOKED);
		customerConsent3.setCustomerGuid(registeredCustomer2.getGuid());
		CustomerConsent customerConsent4 = createCustomerConsent(UUID.randomUUID().toString(), dataPolicy2, ConsentAction.REVOKED);
		customerConsent4.setCustomerGuid(registeredCustomer2.getGuid());

		customerConsentService.save(customerConsent1);
		customerConsentService.save(customerConsent2);
		customerConsentService.save(customerConsent3);
		customerConsentService.save(customerConsent4);

		Map<String, Map<DataPoint, Set<DataPolicy>>> customerDataPoints = dataPointService.findWithRevokedConsentsLatest();

		assertThat(customerDataPoints)
				.containsOnlyKeys(registeredCustomer.getGuid(), registeredCustomer2.getGuid());

		assertThat(customerDataPoints.get(registeredCustomer.getGuid()))
				.containsOnlyKeys(dataPoint1, dataPoint2);

		assertThat(customerDataPoints.get(registeredCustomer2.getGuid()))
				.containsOnlyKeys(dataPoint1, dataPoint2);

		assertThat(customerDataPoints.get(registeredCustomer.getGuid()).get(dataPoint1))
				.containsOnly(dataPolicy1);

		assertThat(customerDataPoints.get(registeredCustomer.getGuid()).get(dataPoint2))
				.containsOnly(dataPolicy2);

		assertThat(customerDataPoints.get(registeredCustomer2.getGuid()).get(dataPoint1))
				.containsOnly(dataPolicy1);

		assertThat(customerDataPoints.get(registeredCustomer2.getGuid()).get(dataPoint2))
				.containsOnly(dataPolicy2);
	}

	@Test
	@DirtiesDatabase
	public void verifyFindWithGrantedConsentsLatestReturnsTwoKeysTwoDataPointOneDataPolicyMap() {
		DataPolicy dataPolicy1 = createAndSaveDataPolicy(DATA_POLICY_UNIQUE_CODE);
		DataPolicy dataPolicy2 = createAndSaveDataPolicy(DATA_POLICY_UNIQUE_CODE2);
		DataPoint dataPoint1 = createAndSaveDataPoint(DATA_POINT_NAME, DATA_POINT_KEY_1, DATA_POINT_LOCATION);
		DataPoint dataPoint2 = createAndSaveDataPoint(DATA_POINT_NAME_2, DATA_POINT_KEY_2, DATA_POINT_LOCATION);
		dataPolicy1.getDataPoints().add(dataPoint1);
		dataPolicy2.getDataPoints().add(dataPoint2);

		dataPolicyService.update(dataPolicy1);
		dataPolicyService.update(dataPolicy2);

		Customer registeredCustomer = createPersistedCustomer(scenario.getStore().getCode(), TEST_EMAIL, false);
		Customer registeredCustomer2 = createPersistedCustomer(scenario.getStore().getCode(), TEST_EMAIL2, false);

		CustomerConsent customerConsent1 = createCustomerConsent(UUID.randomUUID().toString(), dataPolicy1, ConsentAction.GRANTED);
		customerConsent1.setCustomerGuid(registeredCustomer.getGuid());
		CustomerConsent customerConsent2 = createCustomerConsent(UUID.randomUUID().toString(), dataPolicy2, ConsentAction.GRANTED);
		customerConsent2.setCustomerGuid(registeredCustomer.getGuid());

		CustomerConsent customerConsent3 = createCustomerConsent(UUID.randomUUID().toString(), dataPolicy1, ConsentAction.GRANTED);
		customerConsent3.setCustomerGuid(registeredCustomer2.getGuid());
		CustomerConsent customerConsent4 = createCustomerConsent(UUID.randomUUID().toString(), dataPolicy2, ConsentAction.GRANTED);
		customerConsent4.setCustomerGuid(registeredCustomer2.getGuid());

		customerConsentService.save(customerConsent1);
		customerConsentService.save(customerConsent2);
		customerConsentService.save(customerConsent3);
		customerConsentService.save(customerConsent4);

		Map<String, Map<DataPoint, Set<DataPolicy>>> customerDataPoints = dataPointService.findWithGrantedConsentsLatest();

		assertThat(customerDataPoints)
				.containsOnlyKeys(registeredCustomer.getGuid(), registeredCustomer2.getGuid());

		assertThat(customerDataPoints.get(registeredCustomer.getGuid()))
				.containsOnlyKeys(dataPoint1, dataPoint2);

		assertThat(customerDataPoints.get(registeredCustomer2.getGuid()))
				.containsOnlyKeys(dataPoint1, dataPoint2);

		assertThat(customerDataPoints.get(registeredCustomer.getGuid()).get(dataPoint1))
				.containsOnly(dataPolicy1);

		assertThat(customerDataPoints.get(registeredCustomer.getGuid()).get(dataPoint2))
				.containsOnly(dataPolicy2);

		assertThat(customerDataPoints.get(registeredCustomer2.getGuid()).get(dataPoint1))
				.containsOnly(dataPolicy1);

		assertThat(customerDataPoints.get(registeredCustomer2.getGuid()).get(dataPoint2))
				.containsOnly(dataPolicy2);
	}

	@Test
	@DirtiesDatabase
	public void verifyFindWithGrantedConsentsLatestReturnsOneKeysTwoDataPointOneDataPolicyMapWhenOnePolicyRevokedForACustomer() {
		DataPolicy dataPolicy1 = createAndSaveDataPolicy(DATA_POLICY_UNIQUE_CODE);
		DataPolicy dataPolicy2 = createAndSaveDataPolicy(DATA_POLICY_UNIQUE_CODE2);
		DataPoint dataPoint1 = createAndSaveDataPoint(DATA_POINT_NAME, DATA_POINT_KEY_1, DATA_POINT_LOCATION);
		DataPoint dataPoint2 = createAndSaveDataPoint(DATA_POINT_NAME_2, DATA_POINT_KEY_2, DATA_POINT_LOCATION);
		dataPolicy1.getDataPoints().add(dataPoint1);
		dataPolicy2.getDataPoints().add(dataPoint2);

		dataPolicyService.update(dataPolicy1);
		dataPolicyService.update(dataPolicy2);

		Customer registeredCustomer = createPersistedCustomer(scenario.getStore().getCode(), TEST_EMAIL, false);
		Customer registeredCustomer2 = createPersistedCustomer(scenario.getStore().getCode(), TEST_EMAIL2, false);

		CustomerConsent customerConsent1 = createCustomerConsent(UUID.randomUUID().toString(), dataPolicy1, ConsentAction.GRANTED);
		customerConsent1.setCustomerGuid(registeredCustomer.getGuid());
		CustomerConsent customerConsent2 = createCustomerConsent(UUID.randomUUID().toString(), dataPolicy2, ConsentAction.REVOKED);
		customerConsent2.setCustomerGuid(registeredCustomer.getGuid());

		CustomerConsent customerConsent3 = createCustomerConsent(UUID.randomUUID().toString(), dataPolicy1, ConsentAction.GRANTED);
		customerConsent3.setCustomerGuid(registeredCustomer2.getGuid());
		CustomerConsent customerConsent4 = createCustomerConsent(UUID.randomUUID().toString(), dataPolicy2, ConsentAction.GRANTED);
		customerConsent4.setCustomerGuid(registeredCustomer2.getGuid());

		customerConsentService.save(customerConsent1);
		customerConsentService.save(customerConsent2);
		customerConsentService.save(customerConsent3);
		customerConsentService.save(customerConsent4);

		Map<String, Map<DataPoint, Set<DataPolicy>>> customerDataPoints = dataPointService.findWithGrantedConsentsLatest();

		assertThat(customerDataPoints)
				.containsOnlyKeys(registeredCustomer.getGuid(), registeredCustomer2.getGuid());

		assertThat(customerDataPoints.get(registeredCustomer.getGuid()))
				.containsOnlyKeys(dataPoint1);

		assertThat(customerDataPoints.get(registeredCustomer2.getGuid()))
				.containsOnlyKeys(dataPoint1, dataPoint2);

		assertThat(customerDataPoints.get(registeredCustomer.getGuid()).get(dataPoint1))
				.containsOnly(dataPolicy1);

		assertThat(customerDataPoints.get(registeredCustomer2.getGuid()).get(dataPoint1))
				.containsOnly(dataPolicy1);

		assertThat(customerDataPoints.get(registeredCustomer2.getGuid()).get(dataPoint2))
				.containsOnly(dataPolicy2);
	}

	@Test
	@DirtiesDatabase
	public void verifyFindWithRevokedConsentsLatestReturnsEmptyMapWhenThereIsGrantedConsentForTheClashingDataPolicy() {
		DataPolicy dataPolicy1 = createAndSaveDataPolicy(DATA_POLICY_UNIQUE_CODE);
		DataPolicy dataPolicy2 = createAndSaveDataPolicy(DATA_POLICY_UNIQUE_CODE2);
		DataPoint dataPoint1 = createAndSaveDataPoint(DATA_POINT_NAME, DATA_POINT_KEY_1, DATA_POINT_LOCATION);
		dataPolicy1.getDataPoints().add(dataPoint1);
		dataPolicy2.getDataPoints().add(dataPoint1);

		dataPolicyService.update(dataPolicy1);
		dataPolicyService.update(dataPolicy2);

		Customer registeredCustomer = createPersistedCustomer(scenario.getStore().getCode(), TEST_EMAIL, false);

		CustomerConsent customerConsent1 = createCustomerConsent(UUID.randomUUID().toString(), dataPolicy1, ConsentAction.GRANTED);
		customerConsent1.setCustomerGuid(registeredCustomer.getGuid());
		CustomerConsent customerConsent2 = createCustomerConsent(UUID.randomUUID().toString(), dataPolicy2, ConsentAction.REVOKED);
		customerConsent2.setCustomerGuid(registeredCustomer.getGuid());

		customerConsentService.save(customerConsent1);
		customerConsentService.save(customerConsent2);

		Map<String, Map<DataPoint, Set<DataPolicy>>> customerDataPoints = dataPointService.findWithRevokedConsentsLatest();

		assertThat(customerDataPoints)
				.isEmpty();
	}

	@Test
	@DirtiesDatabase
	public void verifyFindWithGrantedConsentsLatestReturnsAllAvailableConsentsMapWhenThereIsGrantedConsentForTheClashingDataPolicy() {
		DataPolicy dataPolicy1 = createAndSaveDataPolicy(DATA_POLICY_UNIQUE_CODE);
		DataPolicy dataPolicy2 = createAndSaveDataPolicy(DATA_POLICY_UNIQUE_CODE2);
		DataPoint dataPoint1 = createAndSaveDataPoint(DATA_POINT_NAME, DATA_POINT_KEY_1, DATA_POINT_LOCATION);
		dataPolicy1.getDataPoints().add(dataPoint1);
		dataPolicy2.getDataPoints().add(dataPoint1);

		dataPolicyService.update(dataPolicy1);
		dataPolicyService.update(dataPolicy2);

		Customer registeredCustomer = createPersistedCustomer(scenario.getStore().getCode(), TEST_EMAIL, false);

		CustomerConsent customerConsent1 = createCustomerConsent(UUID.randomUUID().toString(), dataPolicy1, ConsentAction.GRANTED);
		customerConsent1.setCustomerGuid(registeredCustomer.getGuid());
		CustomerConsent customerConsent2 = createCustomerConsent(UUID.randomUUID().toString(), dataPolicy2, ConsentAction.REVOKED);
		customerConsent2.setCustomerGuid(registeredCustomer.getGuid());

		customerConsentService.save(customerConsent1);
		customerConsentService.save(customerConsent2);

		Map<String, Map<DataPoint, Set<DataPolicy>>> customerDataPoints = dataPointService.findWithGrantedConsentsLatest();

		assertThat(customerDataPoints)
				.containsOnlyKeys(registeredCustomer.getGuid());

		assertThat(customerDataPoints.get(registeredCustomer.getGuid()))
				.containsOnlyKeys(dataPoint1);

		assertThat(customerDataPoints.get(registeredCustomer.getGuid()).get(dataPoint1))
				.containsOnly(dataPolicy1);
	}
}
