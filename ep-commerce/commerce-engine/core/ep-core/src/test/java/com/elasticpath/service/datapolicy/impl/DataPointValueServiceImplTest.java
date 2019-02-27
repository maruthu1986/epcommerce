/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.service.datapolicy.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.datapolicy.DataPoint;
import com.elasticpath.domain.datapolicy.impl.DataPointImpl;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.datapolicy.DataPointLocationEnum;
import com.elasticpath.service.datapolicy.DataPointValueReader;
import com.elasticpath.service.datapolicy.DataPointValueRemover;
import com.elasticpath.service.datapolicy.readers.OrderIPAddressReader;
import com.elasticpath.service.search.IndexNotificationService;
import com.elasticpath.service.search.IndexType;

@RunWith(MockitoJUnitRunner.class)
public class DataPointValueServiceImplTest {

	private static final String CUSTOMER_GUID = "guid";
	private static final String STORE_CODE = "store";
	private static final String POLICY_GUID = "policy_guid";

	@Mock
	private PersistenceEngine persistenceEngine;
	@Mock
	private IndexNotificationService indexNotificationService;
	@Mock
	private DataPointValueReader reader;
	@Mock
	private DataPointValueRemover remover;

	@InjectMocks
	private DataPointValueServiceImpl service;

	@Before
	public void init() {
		service.setDataPointValueReaders(Collections.singletonList(reader));
		service.setDataPointValueRemovers(Collections.singletonList(remover));
	}

	@Test
	public void shouldRemoveDataPointValueWhenRemoverIsFound() {

		DataPointValue dataPointValue = new DataPointValue();
		dataPointValue.setLocation(DataPointLocationEnum.ORDER_IP_ADDRESS.getName());
		dataPointValue.setKey("ORDER_IP_ADDRESS");

		Collection<DataPointValue> dataPointValues = Collections.singletonList(dataPointValue);

		when(remover.isApplicableTo(dataPointValue.getLocation())).thenReturn(true);
		when(remover.removeValues(dataPointValues)).thenReturn(1);

		int numOfRemovedValues = service.removeValues(dataPointValues);

		assertThat(numOfRemovedValues)
			.as("One data point value must be removed")
			.isEqualTo(1);

		verify(remover).removeValues(dataPointValues);
		verify(remover).isApplicableTo(dataPointValue.getLocation());
	}

	@Test
	public void shouldRemoveCustomerProfileFirstNameAndTriggerIndexing() {
		String inList = "list";
		String updateQuery = "update CustomerProfileValueImpl prof SET profile.shortTextValue=null WHERE profile.uidPk IN (:" + inList + ")";
		Long uidPk = 1L;
		Collection<Long> entityUidPKs = Collections.singletonList(uidPk);

		when(persistenceEngine.executeQueryWithList(updateQuery, inList, entityUidPKs)).thenReturn(1);

		int numOfRemovedValues = service.removeValuesByQuery(updateQuery, inList, entityUidPKs, IndexType.CUSTOMER);

		assertThat(numOfRemovedValues)
			.as("One record must be updated")
			.isEqualTo(1);

		verify(indexNotificationService).addNotificationForEntityIndexUpdate(IndexType.CUSTOMER, uidPk);
		verify(persistenceEngine).executeQueryWithList(updateQuery, inList, entityUidPKs);
	}

	@Test
	public void shouldRemoveOrderGiftCertificateFirstNameWithoutIndexing() {
		String inList = "list";
		String updateQuery = "update GiftCertificateImpl gc SET gc.firstName=null WHERE gc.uidPk IN (:" + inList + ")";
		Long uidPk = 1L;
		Collection<Long> entityUidPKs = Collections.singletonList(uidPk);

		when(persistenceEngine.executeQueryWithList(updateQuery, inList, entityUidPKs)).thenReturn(1);

		int numOfRemovedValues = service.removeValuesByQuery(updateQuery, inList, entityUidPKs, null);

		assertThat(numOfRemovedValues)
			.as("One record must be updated")
			.isEqualTo(1);

		verify(indexNotificationService, never()).addNotificationForEntityIndexUpdate(any(IndexType.class), eq(uidPk));
		verify(persistenceEngine).executeQueryWithList(updateQuery, inList, entityUidPKs);
	}

	@Test
	public void shouldThrowExceptionWhileRemovingDataPointValueAndRemoverIsNotFound() {

		DataPointValue dataPointValue = new DataPointValue();
		dataPointValue.setLocation("NOT_SUPPURTED_LOCATION");

		Collection<DataPointValue> dataPointValues = Collections.singletonList(dataPointValue);

		when(remover.isApplicableTo(dataPointValue.getLocation())).thenReturn(false);

		assertThatThrownBy(() -> service.removeValues(dataPointValues))
			.isInstanceOf(EpSystemException.class);

		verify(remover).isApplicableTo(dataPointValue.getLocation());
		verifyNoMoreInteractions(remover);
	}

	@Test
	public void shouldReadDataPointValueWhenReaderIsFound() {

		DataPoint dataPoint = new DataPointImpl();
		dataPoint.setDataKey("ORDER_IP_ADDRESS");
		dataPoint.setDataLocation(DataPointLocationEnum.ORDER_IP_ADDRESS.getName());

		Map<String, Collection<DataPoint>> customerGuidDataPoints = new HashMap<>(1);
		customerGuidDataPoints.put(CUSTOMER_GUID, Collections.singletonList(dataPoint));

		Collection<DataPoint> dataPoints = Collections.singleton(dataPoint);

		when(reader.isApplicableTo(dataPoint.getDataLocation())).thenReturn(true);
		when(reader.readValues(CUSTOMER_GUID, dataPoints))
			.thenReturn(Collections.singletonList(new DataPointValue()));

		Collection<DataPointValue> actualDataPointValues =  service.getValues(customerGuidDataPoints);

		assertThat(actualDataPointValues)
			.as("One data point value must be returned")
			.hasSize(1);

		verify(reader).readValues(CUSTOMER_GUID, dataPoints);
		verify(reader).isApplicableTo(dataPoint.getDataLocation());
	}

	@Test
	public void shouldThrowExceptionWhileReadingDataPointAndReaderIsNotFound() {

		DataPoint dataPoint = new DataPointImpl();
		dataPoint.setDataLocation("Non supported location");

		Map<String, Collection<DataPoint>> customerGuidDataPoints = new HashMap<>(1);
		customerGuidDataPoints.put(CUSTOMER_GUID, Collections.singletonList(dataPoint));

		when(reader.isApplicableTo(dataPoint.getDataLocation())).thenReturn(false);

		assertThatThrownBy(() -> service.getValues(customerGuidDataPoints))
			.isInstanceOf(EpSystemException.class);

		verify(reader).isApplicableTo(dataPoint.getDataLocation());
		verifyNoMoreInteractions(reader);
	}

	@Test
	public void shouldGetLocationWithSupportedFields() {
		when(reader.getSupportedLocation()).thenReturn(DataPointLocationEnum.ORDER_IP_ADDRESS.getName());
		when(reader.getSupportedFields()).thenReturn(new OrderIPAddressReader().getSupportedFields());

		Map<String, Set<String>> dataLocationWithSupportedFields = service.getLocationAndSupportedFields();

		assertThat(dataLocationWithSupportedFields)
			.as("The map can't be empty")
			.hasSize(1);

		verify(reader).getSupportedLocation();
		verify(reader).getSupportedFields();
	}

	@Test
	public void shouldReadDataPointValueByQueryWhenListParametersIsNotEmpty() {
		String listParamName = "listParamName";
		String query = "query with list param :" + listParamName;
		Collection<String> dpKeys = Collections.singletonList("dpKey");
		Object[] params = new Object[]{"guid"};

		Long expectedLong = 1L;
		String expectedString = "val1";

		when(persistenceEngine.retrieveWithList(query, listParamName, dpKeys, params, 0, dpKeys.size()))
			.thenReturn(Collections.singletonList(new Object[]{expectedLong, expectedString}));

		Collection<Object[]> dbValues =  service.readValuesByQuery(query, listParamName, dpKeys, params);

		assertThat(dbValues)
			.as("Must have 1 row")
			.hasSize(1);

		Object[] row = dbValues.iterator().next();

		assertThat(row[0])
			.isEqualTo(expectedLong);
		assertThat(row[1])
			.isEqualTo(expectedString);

		verify(persistenceEngine).retrieveWithList(query, listParamName, dpKeys, params, 0, dpKeys.size());
	}

	@Test
	public void shouldReadDataPointValueByQueryWhenListParametersIsEmpty() {
		String query = "query";
		String listParamName = "listParamName";
		Collection<String> dpKeys = Collections.emptyList();
		Object[] params = new Object[]{"guid"};

		Long expectedLong = 1L;
		String expectedString = "val1";

		when(persistenceEngine.retrieve(query, params))
			.thenReturn(Collections.singletonList(new Object[]{expectedLong, expectedString}));

		Collection<Object[]> dbValues =  service.readValuesByQuery(query, listParamName, dpKeys, params);

		assertThat(dbValues)
			.as("Must have 1 row")
			.hasSize(1);

		Object[] row = dbValues.iterator().next();

		assertThat(row[0])
			.isEqualTo(expectedLong);
		assertThat(row[1])
			.isEqualTo(expectedString);

		verify(persistenceEngine).retrieve(query, params);
	}

	@Test
	public void shouldReturnTrueWhenCustomerBillingAddressFieldExists() {
		String dataPointLocation  = DataPointLocationEnum.CUSTOMER_BILLING_ADDRESS.getName();
		String keyToValidate = "STREET_1";

		when(reader.isApplicableTo(dataPointLocation)).thenReturn(true);
		when(reader.validateKey(keyToValidate)).thenReturn(true);

		boolean isValid = service.validateKeyForLocation(dataPointLocation, keyToValidate);

		assertThat(isValid)
			.as("The data point key must be valid")
			.isTrue();
	}

	@Test
	public void shouldReturnFalseWhenCustomerBillingAddressFieldDoesNotExist() {
		String dataPointLocation  = DataPointLocationEnum.CUSTOMER_BILLING_ADDRESS.getName();
		String keyToValidate = "NON-EXISTING-FIELD";

		when(reader.isApplicableTo(dataPointLocation)).thenReturn(true);
		when(reader.validateKey(keyToValidate)).thenReturn(false);

		boolean isValid = service.validateKeyForLocation(dataPointLocation, keyToValidate);

		assertThat(isValid)
			.as("The data point key must be invalid")
			.isFalse();
	}

	@Test
	public void shouldNotGetCustomerDataPointsForStoreByPolicyGuidWhenCustomerDoesNotExist() {
		String query = "FIND_DATA_POINTS_FOR_STORE_BY_CUSTOMER_AND_DATA_POLICY_GUIDS";

		when(persistenceEngine.retrieveByNamedQuery(query,
			CUSTOMER_GUID, STORE_CODE, POLICY_GUID)).thenReturn(Collections.emptyList());

		Collection<DataPointValue> actualResult = service.getCustomerDataPointValuesForStoreByPolicyGuid(CUSTOMER_GUID, STORE_CODE, POLICY_GUID);

		assertThat(actualResult)
			.isEmpty();

		verify(persistenceEngine).retrieveByNamedQuery(query, CUSTOMER_GUID, STORE_CODE, POLICY_GUID);
		verifyZeroInteractions(reader);
	}

	@Test
	public void shouldGetCustomerDataPointsForStoreByPolicyGuid() {
		String query = "FIND_DATA_POINTS_FOR_STORE_BY_CUSTOMER_AND_DATA_POLICY_GUIDS";
		String dataPointLocation = "LOCATION";
		DataPointValue mockExpectedDataPointValue = Mockito.mock(DataPointValue.class);
		DataPoint mockDataPoint = Mockito.mock(DataPoint.class);

		List<DataPoint> dataPoints = Collections.singletonList(mockDataPoint);
		Collection<DataPointValue> dataPointValues = Collections.singletonList(mockExpectedDataPointValue);

		when(persistenceEngine.<DataPoint>retrieveByNamedQuery(query, CUSTOMER_GUID, STORE_CODE, POLICY_GUID)).thenReturn(dataPoints);
		when(mockDataPoint.getDataLocation()).thenReturn(dataPointLocation);

		when(reader.isApplicableTo(dataPointLocation)).thenReturn(true);
		when(reader.readValues(eq(CUSTOMER_GUID), anyCollection())).thenReturn(dataPointValues);

		Collection<DataPointValue> actualResult = service.getCustomerDataPointValuesForStoreByPolicyGuid(CUSTOMER_GUID, STORE_CODE, POLICY_GUID);

		assertThat(actualResult)
			.hasSize(1);

		verify(persistenceEngine).retrieveByNamedQuery(query, CUSTOMER_GUID, STORE_CODE, POLICY_GUID);
		verify(reader).isApplicableTo(dataPointLocation);
		verify(reader).readValues(eq(CUSTOMER_GUID), anyCollection());
	}

}
