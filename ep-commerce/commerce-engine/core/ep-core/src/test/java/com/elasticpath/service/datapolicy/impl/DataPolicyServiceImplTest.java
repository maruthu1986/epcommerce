/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.datapolicy.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.domain.datapolicy.DataPolicyState;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.datapolicy.DataPolicyService;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;

@RunWith(MockitoJUnitRunner.class)
public class DataPolicyServiceImplTest {

	private static final long DATAPOLICY_UIDPK = 1L;
	private static final String DATAPOLICY_GUID1 = "GUID1";
	public static final String STORE_CODE = "STORE_CODE";
	public static final String SEGMENT_CODE = "SEGMENT_CODE";

	@Mock
	private PersistenceEngine persistenceEngine;

	@Mock
	private ElasticPath elasticPath;

	@Mock
	private DataPolicy dataPolicy;

	@Mock
	private SettingsReader settingsReader;

	@InjectMocks
	private DataPolicyServiceImpl dataPolicyServiceImpl;

	@Before
	public void setUp() {
		Mockito.<Class<DataPolicy>>when(elasticPath.getBeanImplClass(ContextIdNames.DATA_POLICY)).thenReturn(DataPolicy.class);
		when(elasticPath.getBean(ContextIdNames.DATA_POLICY)).thenReturn(dataPolicy);
	}

	@Test
	public void verifyAdd() {
		DataPolicy savedDataPolicy = dataPolicyServiceImpl.save(dataPolicy);

		assertThat(savedDataPolicy)
				.isEqualTo(dataPolicy);
		verify(persistenceEngine).save(dataPolicy);
	}

	@Test
	public void verifyUpdate() {
		when(persistenceEngine.update(dataPolicy)).thenReturn(dataPolicy);

		assertThat(dataPolicyServiceImpl.update(dataPolicy))
				.isEqualTo(dataPolicy);
		verify(persistenceEngine).update(dataPolicy);
	}

	@Test
	public void loadWithUidPkOfZeroReturnsNewDataPolicyFromBean() {
		assertThat(dataPolicyServiceImpl.load(0))
				.isEqualTo(dataPolicy);
		verify(elasticPath).getBean(ContextIdNames.DATA_POLICY);
	}

	@Test
	public void loadWithUidPkGreaterThanZeroReturnsTheCorrespondingDataPolicyFromThePersistenceEngine() {
		when(persistenceEngine.load(DataPolicy.class, DATAPOLICY_UIDPK)).thenReturn(dataPolicy);
		MockDataPolicyServiceImpl dataPointService = new MockDataPolicyServiceImpl();

		dataPointService.setPersistenceEngine(persistenceEngine);
		dataPointService.setElasticPath(elasticPath);

		assertThat(dataPointService.load(DATAPOLICY_UIDPK))
				.isEqualTo(dataPolicy);
	}

	@Test
	public void verifyRemove() {
		dataPolicyServiceImpl.remove(dataPolicy);
		verify(persistenceEngine).delete(dataPolicy);
	}

	@Test
	public void listDelegatesQueryAndReturnsListOfResultingDataPolicies() {
		when(persistenceEngine.retrieveByNamedQuery("DATAPOLICY_SELECT_ALL")).thenReturn(Collections.singletonList(dataPolicy));

		assertThat(dataPolicyServiceImpl.list())
				.containsExactly(dataPolicy);
	}

	@Test
	public void listRetrieveByGuidsDelegatesQueryAndReturnsListOfResultingDataPoints() {
		List<String> guidList = Collections.singletonList(DATAPOLICY_GUID1);
		when(persistenceEngine.retrieveByNamedQueryWithList("DATAPOLICY_FIND_BY_GUIDS",
				"list", guidList)).thenReturn(Collections.singletonList(dataPolicy));

		assertThat(dataPolicyServiceImpl.findByGuids(guidList))
				.containsExactly(dataPolicy);
	}

	@Test
	public void listRetrieveByIncorrectGuidsDelegatesQueryAndReturnsNull() {
		List<String> guidList = Collections.singletonList(DATAPOLICY_GUID1);

		when(persistenceEngine.retrieveByNamedQueryWithList("DATAPOLICY_FIND_BY_GUIDS",
				"list", guidList)).thenReturn(Collections.emptyList());

		assertThat(dataPolicyServiceImpl.findByGuids(guidList))
				.isNull();
	}

	@Test
	public void nullReturnedWhenDataPolicyDoesNotExistForGivenUidPk() {
		MockDataPolicyServiceImpl dataPolicyService = new MockDataPolicyServiceImpl();

		dataPolicyService.setPersistenceEngine(persistenceEngine);
		dataPolicyService.setElasticPath(elasticPath);

		assertThat(dataPolicyService.get(DATAPOLICY_UIDPK))
				.isNull();
	}

	@Test
	public void getWithUidPkOfZeroReturnsNewDataPolicyFromBean() {
		assertThat(dataPolicyServiceImpl.get(0))
				.isEqualTo(dataPolicy);

		verify(elasticPath).getBean(ContextIdNames.DATA_POLICY);
	}

	@Test
	public void gettingWithFetchGroupLoadTunerDelegatesToConfigureFetchPlanHelper() {
		MockDataPolicyServiceImpl dataPolicyService = new MockDataPolicyServiceImpl();

		when(persistenceEngine.get(DataPolicy.class, DATAPOLICY_UIDPK)).thenReturn(dataPolicy);

		dataPolicyService.setPersistenceEngine(persistenceEngine);
		dataPolicyService.setElasticPath(elasticPath);

		assertThat(dataPolicyService.get(DATAPOLICY_UIDPK))
				.isEqualTo(dataPolicy);
	}

	@Test
	public void verifyFindActiveDataPoliciesForSegmentsAndStoreWhenEnableDataPoliciesDisabled() {
		setupEnableDataPolicySetting(false);

		assertThat(dataPolicyServiceImpl.findActiveDataPoliciesForSegmentsAndStore(Collections.singletonList(SEGMENT_CODE), STORE_CODE))
				.isEmpty();

		verify(settingsReader).getSettingValue(DataPolicyService.COMMERCE_STORE_ENABLE_DATA_POLICIES, STORE_CODE);
	}

	@Test
	public void verifyFindActiveDataPoliciesForSegmentsAndStoreWhenEnableDataPoliciesEnabledAndNoDataPoliciesFound() {
		setupEnableDataPolicySetting(true);
		when(persistenceEngine.retrieveByNamedQuery("DATAPOLICY_FIND_ACTIVE", DataPolicyState.ACTIVE))
				.thenReturn(Collections.emptyList());

		assertThat(dataPolicyServiceImpl.findActiveDataPoliciesForSegmentsAndStore(Collections.singletonList(SEGMENT_CODE), STORE_CODE))
				.isEmpty();

		verify(settingsReader).getSettingValue(DataPolicyService.COMMERCE_STORE_ENABLE_DATA_POLICIES, STORE_CODE);
	}

	@Test
	public void verifyFindActiveDataPoliciesForSegmentsAndStoreWhenEnableDataPoliciesEnabledAndDataPoliciesFound() {
		setupEnableDataPolicySetting(true);

		Set<String> headers = new HashSet<>();
		headers.add(SEGMENT_CODE);

		when(persistenceEngine.retrieveByNamedQuery("DATAPOLICY_FIND_ACTIVE", DataPolicyState.ACTIVE))
				.thenReturn(Collections.singletonList(dataPolicy));
		when(dataPolicy.getSegments()).thenReturn(headers);

		assertThat(dataPolicyServiceImpl.findActiveDataPoliciesForSegmentsAndStore(Arrays.asList(SEGMENT_CODE), STORE_CODE))
				.containsExactly(dataPolicy);

		verify(settingsReader).getSettingValue(DataPolicyService.COMMERCE_STORE_ENABLE_DATA_POLICIES, STORE_CODE);
	}

	@Test
	public void verifyAreDataPoliciesEnabledForTheStoreWhenDisabled() {
		setupEnableDataPolicySetting(false);

		assertThat(dataPolicyServiceImpl.areDataPoliciesEnabledForTheStore(STORE_CODE))
				.isEqualTo(false);

		verify(settingsReader).getSettingValue(DataPolicyService.COMMERCE_STORE_ENABLE_DATA_POLICIES, STORE_CODE);
	}

	@Test
	public void verifyAreDataPoliciesEnabledForTheStoreWhenEnabled() {
		setupEnableDataPolicySetting(true);

		assertThat(dataPolicyServiceImpl.areDataPoliciesEnabledForTheStore(STORE_CODE))
				.isEqualTo(true);

		verify(settingsReader).getSettingValue(DataPolicyService.COMMERCE_STORE_ENABLE_DATA_POLICIES, STORE_CODE);
	}

	@Test
	public void verifyAreDataPoliciesEnabledForTheStoreWhenNull() {
		when(settingsReader.getSettingValue(DataPolicyService.COMMERCE_STORE_ENABLE_DATA_POLICIES, STORE_CODE)).thenReturn(null);

		assertThat(dataPolicyServiceImpl.areDataPoliciesEnabledForTheStore(STORE_CODE))
				.isEqualTo(false);

		verify(settingsReader).getSettingValue(DataPolicyService.COMMERCE_STORE_ENABLE_DATA_POLICIES, STORE_CODE);
	}

	private void setupEnableDataPolicySetting(final boolean enabled) {
		SettingValue mockedSettingValue = mock(SettingValue.class);

		when(settingsReader.getSettingValue(DataPolicyService.COMMERCE_STORE_ENABLE_DATA_POLICIES, STORE_CODE)).thenReturn(mockedSettingValue);
		when(mockedSettingValue.getBooleanValue()).thenReturn(enabled);
	}

	private class MockDataPolicyServiceImpl extends DataPolicyServiceImpl {
		@Override
		public PersistentBeanFinder getPersistentBeanFinder() {
			return super.getPersistentBeanFinder();
		}

		@Override
		public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
			super.setPersistenceEngine(persistenceEngine);
		}
	}
}
