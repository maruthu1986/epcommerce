/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.datapolicy.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.datapolicy.DataPoint;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.persistence.api.PersistenceEngine;

@RunWith(MockitoJUnitRunner.class)
public class DataPointServiceImplTest {

	private static final long DATAPOINT_UIDPK = 1L;
	private static final String DATAPOINT_GUID1 = "GUID1";
	private static final String DATAPOINT_NAME = "NAME";
	private static final String DATAPOINT_KEY = "KEY";
	private static final String DATAPOINT_LOCATION = "LOCATION";
	private static final String DATA_POLICY_GUID = "POLICY_GUID";
	private static final String CUSTOMER_GUID = "CUSTOMER_GUID";

	private static final String DATAPOINTS_FIND_WITH_REVOKED_CONSENTS_LATEST = "DATAPOINTS_FIND_WITH_REVOKED_CONSENTS_LATEST";

	@Mock
	private PersistenceEngine persistenceEngine;

	@Mock
	private DataPoint dataPoint1;

	@Mock
	private DataPoint dataPoint2;

	@Mock
	private DataPolicy dataPolicy1;

	@Mock
	private DataPolicy dataPolicy2;

	@Mock
	private ElasticPath elasticPath;

	@InjectMocks
	private DataPointServiceImpl dataPointServiceImpl;

	@Before
	public void setUp() {
		Mockito.<Class<DataPoint>>when(elasticPath.getBeanImplClass(ContextIdNames.DATA_POINT)).thenReturn(DataPoint.class);
		when(elasticPath.getBean(ContextIdNames.DATA_POINT)).thenReturn(dataPoint1);
	}

	@Test
	public void verifyAdd() {
		assertThat(dataPointServiceImpl.save(dataPoint1)).isEqualTo(dataPoint1);

		verify(persistenceEngine).save(dataPoint1);
	}

	@Test
	public void loadWithUidPkOfZeroReturnsNewDataPointFromBean() {
		assertThat(dataPointServiceImpl.load(0)).isEqualTo(dataPoint1);

		verify(elasticPath).getBean(ContextIdNames.DATA_POINT);
	}

	@Test
	public void loadWithUidPkGreaterThanZeroReturnsTheCorrespondingDataPointFromThePersistenceEngine() {
		when(persistenceEngine.load(DataPoint.class, DATAPOINT_UIDPK)).thenReturn(dataPoint1);
		MockDataPointServiceImpl dataPointService = new MockDataPointServiceImpl();

		dataPointService.setPersistenceEngine(persistenceEngine);
		dataPointService.setElasticPath(elasticPath);

		assertThat(dataPointService.load(DATAPOINT_UIDPK))
				.isEqualTo(dataPoint1);
	}

	@Test
	public void listDelegatesQueryAndReturnsListOfResultingDataPoints() {
		when(persistenceEngine.retrieveByNamedQuery("DATAPOINT_SELECT_ALL")).thenReturn(Collections.singletonList(dataPoint1));

		assertThat(dataPointServiceImpl.list())
				.isEqualTo(Collections.singletonList(dataPoint1));
	}

	@Test
	public void listRetriveByGuidsDelegatesQueryAndReturnsListOfResultingDataPoints() {
		List<String> guidList = Collections.singletonList(DATAPOINT_GUID1);

		when(persistenceEngine.retrieveByNamedQueryWithList("DATAPOINT_FIND_BY_GUIDS",
				"list", guidList)).thenReturn(Collections.singletonList(dataPoint1));

		assertThat(dataPointServiceImpl.findByGuids(guidList))
				.isEqualTo(Collections.singletonList(dataPoint1));
	}

	@Test
	public void listRetriveByIncorrectGuidsDelegatesQueryAndReturnsNull() {
		List<String> guidList = Collections.singletonList(DATAPOINT_GUID1);

		when(persistenceEngine.retrieveByNamedQueryWithList("DATAPOINT_FIND_BY_GUIDS",
				"list", guidList)).thenReturn(Collections.emptyList());

		assertThat(dataPointServiceImpl.findByGuids(guidList))
				.isNull();
	}

	@Test
	public void nullReturnedWhenDataPointDoesNotExistForGivenUidPk() {
		MockDataPointServiceImpl dataPointService = new MockDataPointServiceImpl();

		dataPointService.setPersistenceEngine(persistenceEngine);
		dataPointService.setElasticPath(elasticPath);

		assertThat(dataPointService.get(DATAPOINT_UIDPK))
				.isNull();
	}

	@Test
	public void getWithUidPkOfZeroReturnsNewDataPointFromBean() {
		assertThat(dataPointServiceImpl.get(0)).isEqualTo(dataPoint1);
		verify(elasticPath).getBean(ContextIdNames.DATA_POINT);
	}

	@Test
	public void gettingWithFetchGroupLoadTunerDelegatesToConfigureFetchPlanHelper() {
		MockDataPointServiceImpl dataPointService = new MockDataPointServiceImpl();

		when(persistenceEngine.get(DataPoint.class, DATAPOINT_UIDPK)).thenReturn(dataPoint1);

		dataPointService.setPersistenceEngine(persistenceEngine);
		dataPointService.setElasticPath(elasticPath);

		assertThat(dataPointService.get(DATAPOINT_UIDPK))
				.isEqualTo(dataPoint1);
	}

	@Test
	public void verifyFindByNameReturnsDataPointWhenExists() {
		when(persistenceEngine.retrieveByNamedQuery("DATAPOINT_FIND_BY_NAME", DATAPOINT_NAME))
				.thenReturn(Collections.singletonList(dataPoint1));

		assertThat(dataPointServiceImpl.findByName(DATAPOINT_NAME))
				.isNotNull();
	}

	@Test
	public void verifyFindByNameReturnsNullWhenDoesntExist() {
		when(persistenceEngine.retrieveByNamedQuery("DATAPOINT_FIND_BY_NAME", DATAPOINT_NAME))
				.thenReturn(Collections.emptyList());

		assertThat(dataPointServiceImpl.findByName(DATAPOINT_NAME))
				.isNull();
	}

	@Test
	public void verifyFindByKeyAndLocationReturnsDataPointWhenExists() {
		when(persistenceEngine.retrieveByNamedQuery("DATAPOINT_FIND_BY_LOCATION_AND_KEY", DATAPOINT_LOCATION, DATAPOINT_KEY))
				.thenReturn(Collections.singletonList(dataPoint1));

		assertThat(dataPointServiceImpl.findByDataLocationAndDataKey(DATAPOINT_LOCATION, DATAPOINT_KEY))
				.isNotNull();
	}

	@Test
	public void verifyFindByKeyAndLocationReturnsNullWhenDoesntExist() {
		when(persistenceEngine.retrieveByNamedQuery("DATAPOINT_FIND_BY_LOCATION_AND_KEY", DATAPOINT_LOCATION, DATAPOINT_KEY))
				.thenReturn(Collections.emptyList());

		assertThat(dataPointServiceImpl.findByDataLocationAndDataKey(DATAPOINT_LOCATION, DATAPOINT_KEY))
				.isNull();
	}

	@Test
	public void verifyFindUniqueForDataPolicyReturnsDataPointWhenFound() {
		when(persistenceEngine
			.retrieveByNamedQuery("FIND_REMOVABLE_UNIQUE_POLICY_DATA_POINTS_FOR_CUSTOMER", DATA_POLICY_GUID, CUSTOMER_GUID))
				.thenReturn(Collections.singletonList(dataPoint1));

		List<DataPoint> dataPoints = dataPointServiceImpl.findUniqueRemovableForDataPolicyAndCustomer(DATA_POLICY_GUID, CUSTOMER_GUID);

		assertThat(dataPoints)
				.containsExactly(dataPoint1);
	}

	@Test
	public void verifyFindUniqueForDataPolicyReturnsEmptyListWhenNotFound() {
		when(persistenceEngine
			.retrieveByNamedQuery("FIND_REMOVABLE_UNIQUE_POLICY_DATA_POINTS_FOR_CUSTOMER", DATA_POLICY_GUID, CUSTOMER_GUID))
				.thenReturn(Collections.emptyList());

		List<DataPoint> dataPoints = dataPointServiceImpl.findUniqueRemovableForDataPolicyAndCustomer(DATA_POLICY_GUID, CUSTOMER_GUID);

		assertThat(dataPoints)
				.isEmpty();
	}

	@Test
	public void verifyFindByConsentActionReturnsEmptyMapWhenNoDataPointFound() {
		Map<String, Map<DataPoint, Set<DataPolicy>>> customerDataPoints = dataPointServiceImpl.findWithRevokedConsentsLatest();

		assertThat(customerDataPoints)
				.isEmpty();
	}

	@Test
	public void verifyFindByConsentActionReturnsOneKeyOneDataPointOneDataPolicyMapWhenOnlyOneDataPointFound() {
		String customerGuid = UUID.randomUUID().toString();

		Object[] resultSet = new Object[]{customerGuid, dataPoint1, dataPolicy1};

		when(persistenceEngine.retrieveByNamedQuery(DATAPOINTS_FIND_WITH_REVOKED_CONSENTS_LATEST)).thenReturn(Collections.singletonList(resultSet));

		Map<String, Map<DataPoint, Set<DataPolicy>>> customerDataPoints = dataPointServiceImpl.findWithRevokedConsentsLatest();

		assertThat(customerDataPoints)
				.containsOnlyKeys(customerGuid);

		assertThat(customerDataPoints.get(customerGuid))
				.containsOnlyKeys(dataPoint1);

		assertThat(customerDataPoints.get(customerGuid).get(dataPoint1))
				.containsOnly(dataPolicy1);
	}

	@Test
	public void verifyFindByConsentActionReturnsOneKeyOneDataPointTwoDataPolicyMap() {
		String customerGuid = UUID.randomUUID().toString();

		Object[] resultSet1 = new Object[]{customerGuid, dataPoint1, dataPolicy1};
		Object[] resultSet2 = new Object[]{customerGuid, dataPoint1, dataPolicy2};

		when(persistenceEngine.retrieveByNamedQuery(DATAPOINTS_FIND_WITH_REVOKED_CONSENTS_LATEST)).thenReturn(Arrays.asList(resultSet1, resultSet2));

		Map<String, Map<DataPoint, Set<DataPolicy>>> customerDataPoints = dataPointServiceImpl.findWithRevokedConsentsLatest();

		assertThat(customerDataPoints)
				.containsOnlyKeys(customerGuid);

		assertThat(customerDataPoints.get(customerGuid))
				.containsOnlyKeys(dataPoint1);

		assertThat(customerDataPoints.get(customerGuid).get(dataPoint1))
				.containsOnly(dataPolicy1, dataPolicy2);
	}

	@Test
	public void verifyFindByConsentActionReturnsOneKeyTwoDataPointOneDataPolicyMap() {
		String customerGuid = UUID.randomUUID().toString();

		Object[] resultSet1 = new Object[]{customerGuid, dataPoint1, dataPolicy1};
		Object[] resultSet2 = new Object[]{customerGuid, dataPoint2, dataPolicy2};

		when(persistenceEngine.retrieveByNamedQuery(DATAPOINTS_FIND_WITH_REVOKED_CONSENTS_LATEST)).thenReturn(Arrays.asList(resultSet1, resultSet2));

		Map<String, Map<DataPoint, Set<DataPolicy>>> customerDataPoints = dataPointServiceImpl.findWithRevokedConsentsLatest();

		assertThat(customerDataPoints)
				.containsOnlyKeys(customerGuid);

		assertThat(customerDataPoints.get(customerGuid))
				.containsOnlyKeys(dataPoint1, dataPoint2);

		assertThat(customerDataPoints.get(customerGuid).get(dataPoint1))
				.containsOnly(dataPolicy1);

		assertThat(customerDataPoints.get(customerGuid).get(dataPoint2))
				.containsOnly(dataPolicy2);
	}

	private class MockDataPointServiceImpl extends DataPointServiceImpl {
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
