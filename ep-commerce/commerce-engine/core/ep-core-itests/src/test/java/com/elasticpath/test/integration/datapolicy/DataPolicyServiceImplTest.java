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

import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;

import com.elasticpath.domain.datapolicy.DataPoint;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.domain.datapolicy.DataPolicyState;
import com.elasticpath.test.integration.DirtiesDatabase;

public class DataPolicyServiceImplTest extends AbstractDataPolicyTest {

	private static final String UPDATED_DATA_POLICY_NAME = "UPDATED_DATA_POLICY_NAME";
	private static final String FALSE_SEGMENT = "FALSE_SEGMENT";
	private static final String STORE_CODE = "STORE_CODE";
	private static final long DAY_IN_MILLISEC = 1000 * 60 * 60 * 24;
	private static final Date TOMORROW_DATE = new Date(System.currentTimeMillis() + DAY_IN_MILLISEC);
	private static final Date YESTERDAY_DATE = new Date(System.currentTimeMillis() - DAY_IN_MILLISEC);
	private static final Date TODAY_DATE = new Date();

	@Test
	@DirtiesDatabase
	public void verifySavesNewDataPolicyWhenUsingSaveMethod() {
		DataPolicy dataPolicy = createAndSaveDataPolicy(DATA_POLICY_UNIQUE_CODE);

		assertThat(dataPolicy)
				.isNotNull();
		assertThat(dataPolicy.isPersisted())
				.isTrue();
	}

	@Test
	@DirtiesDatabase
	public void verifyUpdatesExistingDataPolicyWhenUsingUpdateMethod() {
		DataPolicy dataPolicy = createAndSaveDataPolicy(DATA_POLICY_UNIQUE_CODE);
		dataPolicy.setPolicyName(UPDATED_DATA_POLICY_NAME);

		DataPolicy updateDataPolicy = dataPolicyService.update(dataPolicy);
		assertThat(updateDataPolicy.getPolicyName())
				.isEqualTo(UPDATED_DATA_POLICY_NAME);
	}

	@Test
	@DirtiesDatabase
	public void verifyRemovesDataPolicyWhenUsingRemoveMethodWithPreviouslySavedDataPolicy() {
		DataPolicy dataPolicy = createAndSaveDataPolicy(DATA_POLICY_UNIQUE_CODE);
		long uidPk = dataPolicy.getUidPk();

		dataPolicyService.remove(dataPolicy);
		dataPolicy = dataPolicyService.get(uidPk);

		assertThat(dataPolicy)
				.isNull();
	}

	@Test
	@DirtiesDatabase
	public void verifyLoadsDataPolicyWhenUsingGetMethodAndDataPolicyExists() {
		DataPolicy dataPolicy = createAndSaveDataPolicy(DATA_POLICY_UNIQUE_CODE);
		long uidPk = dataPolicy.getUidPk();

		dataPolicy = dataPolicyService.get(uidPk);

		assertThat(dataPolicy)
				.isNotNull();
	}

	@Test
	@DirtiesDatabase
	public void verifyLoadsDataPolicyWhenUsingLoadMethodAndDataPolicyExists() {
		DataPolicy dataPolicy = createAndSaveDataPolicy(DATA_POLICY_UNIQUE_CODE);
		long uidPk = dataPolicy.getUidPk();

		dataPolicy = dataPolicyService.load(uidPk);

		assertThat(dataPolicy)
				.isNotNull();
	}

	@Test
	@DirtiesDatabase
	public void verifyReturnsDataPolicyBeanWhenUsingLoadMethodAndPassedInUidPKIsNegative() {
		DataPolicy dataPolicy = dataPolicyService.load(-1);

		assertThat(dataPolicy)
				.isNotNull();
		assertThat(dataPolicy.isPersisted())
				.isFalse();
	}

	@Test
	@DirtiesDatabase
	public void verifyReturnsDataPolicyBeanWhenUsingGetMethodAndPassedInUidPKIsNegative() {
		DataPolicy dataPolicy = dataPolicyService.get(-1);

		assertThat(dataPolicy)
				.isNotNull();
		assertThat(dataPolicy.isPersisted())
				.isFalse();
	}

	@Test
	@DirtiesDatabase
	public void verifyReturnsListOfAllDataPoliciesWhenUsingListMethod() {
		createAndSaveDataPolicy(DATA_POLICY_UNIQUE_CODE);

		List<DataPolicy> all = dataPolicyService.list();

		assertThat(all)
				.isNotNull()
				.isNotEmpty();
	}

	@Test
	@DirtiesDatabase
	public void verifyLoadsDataPolicyWhenUsingFindByGuidAndDataPoliciesExists() {
		DataPolicy dataPolicy = createAndSaveDataPolicy(DATA_POLICY_UNIQUE_CODE);

		dataPolicy = dataPolicyService.findByGuid(dataPolicy.getGuid());

		assertThat(dataPolicy)
				.isNotNull();
	}

	@Test
	@DirtiesDatabase
	public void findActiveByGuidReturnsActiveDataPolicyWhenOneExists() {
		DataPolicy dataPolicy = createAndSaveDataPolicyWithStateAndStartDateAndEndDate(DATA_POLICY_UNIQUE_CODE, DataPolicyState.ACTIVE,
				TODAY_DATE, TOMORROW_DATE);

		DataPolicy expectedDataPolicy = dataPolicyService.findActiveByGuid(dataPolicy.getGuid());

		assertThat(expectedDataPolicy)
				.isEqualTo(dataPolicy);
	}

	@Test
	@DirtiesDatabase
	public void findActiveByGuidReturnsNullWhenDataPolicyForGuidIsInactive() {
		DataPolicy dataPolicy = createAndSaveDataPolicyWithState(DATA_POLICY_UNIQUE_CODE, DataPolicyState.DISABLED);

		DataPolicy expectedDataPolicy = dataPolicyService.findActiveByGuid(dataPolicy.getGuid());

		assertThat(expectedDataPolicy)
				.isNull();
	}

	@Test
	@DirtiesDatabase
	public void verifyLoadsDataPolicyWhenUsingFindByGuidListAndDataPolicyExists() {
		DataPolicy dataPolicy = createAndSaveDataPolicy(DATA_POLICY_UNIQUE_CODE);
		DataPolicy dataPolicy2 = createAndSaveDataPolicy(DATA_POLICY_UNIQUE_CODE2);

		List<DataPolicy> dataPolices = dataPolicyService.findByGuids(Arrays.asList(dataPolicy.getGuid(), dataPolicy2.getGuid()));

		assertThat(dataPolices)
				.isNotNull()
				.hasSize(2);
	}

	@Test
	@DirtiesDatabase
	public void verifyReturnsNullWhenUsingFindByGuidListAndDataPoliciesDoNotExists() {
		List<DataPolicy> dataPolices = dataPolicyService.findByGuids(Arrays.asList(DATA_POLICY_UNIQUE_CODE3));

		assertThat(dataPolices)
				.isNull();
	}

	@Test
	@DirtiesDatabase
	public void verifyDataPolicyContainsDataPointsWhenTheyExist() {
		DataPolicy dataPolicy = createAndSaveDataPolicy(DATA_POLICY_UNIQUE_CODE);

		DataPoint dataPoint = createDataPoint(DATA_POINT_NAME, DATA_POINT_KEY_1, DATA_POINT_LOCATION);

		dataPolicy.getDataPoints().add(dataPoint);

		dataPolicyService.update(dataPolicy);

		dataPolicy = dataPolicyService.load(dataPolicy.getUidPk());

		assertThat(dataPolicy)
				.isNotNull();
		assertThat(dataPolicy.getDataPoints())
				.hasSize(1);
	}

	@Test
	@DirtiesDatabase
	public void findActivePoliciesForSegmentsAndStoreReturnsNoDataPoliciesWhenStoreIsDisabled() {
		createAndSaveDataPolicyWithStateAndStartDateAndEndDate(DATA_POLICY_UNIQUE_CODE, DataPolicyState.ACTIVE, TODAY_DATE, TOMORROW_DATE);
		setupEnableDataPoliciesSettingValue(STORE_CODE, false);

		List<DataPolicy> foundDataPolicies =
				dataPolicyService.findActiveDataPoliciesForSegmentsAndStore(Arrays.asList(EU, CA, FALSE_SEGMENT), STORE_CODE);

		assertThat(foundDataPolicies)
				.isEmpty();
	}

	@Test
	@DirtiesDatabase
	public void findActivePoliciesForSegmentsAndStoreReturnsActiveDataPoliciesForTheGivenSegments() {
		DataPolicy dataPolicy = createAndSaveDataPolicyWithStateAndStartDateAndEndDate(DATA_POLICY_UNIQUE_CODE, DataPolicyState.ACTIVE,
				TODAY_DATE, TOMORROW_DATE);
		setupEnableDataPoliciesSettingValue(STORE_CODE, true);

		List<DataPolicy> foundDataPolicies =
				dataPolicyService.findActiveDataPoliciesForSegmentsAndStore(Arrays.asList(EU, CA, FALSE_SEGMENT), STORE_CODE);

		assertThat(foundDataPolicies)
				.containsExactly(dataPolicy);
	}

	@Test
	@DirtiesDatabase
	public void findActivePoliciesForSegmentsAndStoreIsCaseInsensitive() {
		DataPolicy dataPolicy = createAndSaveDataPolicyWithStateAndStartDateAndEndDate(DATA_POLICY_UNIQUE_CODE, DataPolicyState.ACTIVE,
				TODAY_DATE, TOMORROW_DATE);
		setupEnableDataPoliciesSettingValue(STORE_CODE, true);

		List<DataPolicy> foundDataPolicies =
				dataPolicyService.findActiveDataPoliciesForSegmentsAndStore(Arrays.asList(EU.toLowerCase(Locale.ENGLISH),
						CA.toLowerCase(Locale.ENGLISH)), STORE_CODE);

		assertThat(foundDataPolicies)
				.containsExactly(dataPolicy);
	}

	@Test
	@DirtiesDatabase
	public void findActivePoliciesForSegmentsAndStoreReturnsActiveDataPoliciesForTheGivenSegmentsWhenEndDateNull() {
		DataPolicy dataPolicy = createAndSaveDataPolicyWithStateAndStartDateAndEndDate(DATA_POLICY_UNIQUE_CODE, DataPolicyState.ACTIVE,
				TODAY_DATE, null);
		setupEnableDataPoliciesSettingValue(STORE_CODE, true);

		List<DataPolicy> foundDataPolicies =
				dataPolicyService.findActiveDataPoliciesForSegmentsAndStore(Arrays.asList(EU, CA, FALSE_SEGMENT), STORE_CODE);

		assertThat(foundDataPolicies)
				.containsExactly(dataPolicy);
	}

	@Test
	@DirtiesDatabase
	public void findActivePoliciesForSegmentsAndStoreReturnsActiveDataPoliciesForDifferentSegments() {
		DataPolicy dataPolicy1 = createAndSaveDataPolicyWithStateAndSegments(DATA_POLICY_UNIQUE_CODE, DataPolicyState.ACTIVE, EU);
		DataPolicy dataPolicy2 = createAndSaveDataPolicyWithStateAndSegments(DATA_POLICY_UNIQUE_CODE2, DataPolicyState.ACTIVE, CA);
		DataPolicy dataPolicy3 = createAndSaveDataPolicyWithStateAndSegments(DATA_POLICY_UNIQUE_CODE3, DataPolicyState.ACTIVE, EU, CA);
		setupEnableDataPoliciesSettingValue(STORE_CODE, true);

		List<DataPolicy> foundDataPolicies =
				dataPolicyService.findActiveDataPoliciesForSegmentsAndStore(Arrays.asList(EU, CA, FALSE_SEGMENT), STORE_CODE);

		assertThat(foundDataPolicies)
				.containsExactly(dataPolicy1, dataPolicy2, dataPolicy3);
	}

	@Test
	@DirtiesDatabase
	public void findActivePoliciesForSegmentsAndStoreReturnsEmptyListWhenNoDataPoliciesContainTheGivenSegments() {
		createAndSaveDataPolicyWithStateAndStartDateAndEndDate(DATA_POLICY_UNIQUE_CODE, DataPolicyState.ACTIVE,
				TODAY_DATE, TOMORROW_DATE);
		setupEnableDataPoliciesSettingValue(STORE_CODE, true);

		List<DataPolicy> foundDataPolicies =
				dataPolicyService.findActiveDataPoliciesForSegmentsAndStore(Arrays.asList(FALSE_SEGMENT), STORE_CODE);

		assertThat(foundDataPolicies).isEmpty();
	}

	@Test
	@DirtiesDatabase
	public void findActivePoliciesForSegmentsAndStoreReturnsEmptyListWhenNoDataPoliciesWithActiveStartDate() {
		createAndSaveDataPolicyWithStateAndStartDateAndEndDate(DATA_POLICY_UNIQUE_CODE, DataPolicyState.ACTIVE,
				TOMORROW_DATE, TOMORROW_DATE);
		setupEnableDataPoliciesSettingValue(STORE_CODE, true);

		List<DataPolicy> foundDataPolicies =
				dataPolicyService.findActiveDataPoliciesForSegmentsAndStore(Collections.singletonList(EU), STORE_CODE);

		assertThat(foundDataPolicies).isEmpty();
	}

	@Test
	@DirtiesDatabase
	public void findActivePoliciesForSegmentsAndStoreReturnsEmptyListWhenNoDataPoliciesAreActive() {
		createAndSaveDataPolicyWithStateAndStartDateAndEndDate(DATA_POLICY_UNIQUE_CODE, DataPolicyState.DISABLED,
				TODAY_DATE, TOMORROW_DATE);
		setupEnableDataPoliciesSettingValue(STORE_CODE, true);

		List<DataPolicy> foundDataPolicies = dataPolicyService.findActiveDataPoliciesForSegmentsAndStore(Collections.singletonList(EU), STORE_CODE);
		assertThat(foundDataPolicies).isEmpty();
	}

	@Test
	@DirtiesDatabase
	public void findActivePoliciesForSegmentsAndStoreReturnsEmptyListWhenEndDateHasPassed() {
		createAndSaveDataPolicyWithStateAndStartDateAndEndDate(DATA_POLICY_UNIQUE_CODE, DataPolicyState.ACTIVE,
				YESTERDAY_DATE, YESTERDAY_DATE);
		setupEnableDataPoliciesSettingValue(STORE_CODE, true);

		List<DataPolicy> foundDataPolicies = dataPolicyService.findActiveDataPoliciesForSegmentsAndStore(Collections.singletonList(EU), STORE_CODE);
		assertThat(foundDataPolicies).isEmpty();
	}

	@Test
	@DirtiesDatabase
	public void verifyLoadsAllDataPolicyByProvidedStates() {
		createAndSaveDataPolicyWithState(DATA_POLICY_UNIQUE_CODE, DataPolicyState.ACTIVE);
		createAndSaveDataPolicyWithState(DATA_POLICY_UNIQUE_CODE, DataPolicyState.DISABLED);

		List<DataPolicy> dataPolicies = dataPolicyService.findByStates(DataPolicyState.ACTIVE, DataPolicyState.DISABLED);

		int expectedSize = 2;

		assertThat(dataPolicies)
				.hasSize(expectedSize);
	}

	@Test
	@DirtiesDatabase
	public void verifyLoadsAllDataPolicyOnlyByProvidedStates() {
		createAndSaveDataPolicyWithState(DATA_POLICY_UNIQUE_CODE, DataPolicyState.DRAFT);
		createAndSaveDataPolicyWithState(DATA_POLICY_UNIQUE_CODE, DataPolicyState.ACTIVE);

		List<DataPolicy> dataPolicies = dataPolicyService.findByStates(DataPolicyState.ACTIVE, DataPolicyState.DISABLED);

		assertThat(dataPolicies)
				.hasSize(1);
	}

	@Test
	@DirtiesDatabase
	public void verifyDbExceptionIsThrownWhenTryingToSaveDataPolicyWithDuplicatedDataPoint() {
		DataPolicy dataPolicy = createAndSaveDataPolicy(STORE_CODE);
		DataPoint dataPoint = createAndSaveDataPoint(DATA_POINT_NAME, DATA_POINT_KEY_1, DATA_POINT_LOCATION);

		dataPolicy.getDataPoints().add(dataPoint);
		dataPolicyService.update(dataPolicy);

		dataPolicy.getDataPoints().add(dataPoint);
		assertThatThrownBy(() -> dataPolicyService.update(dataPolicy))
				.isInstanceOf(DataIntegrityViolationException.class);
	}
}
