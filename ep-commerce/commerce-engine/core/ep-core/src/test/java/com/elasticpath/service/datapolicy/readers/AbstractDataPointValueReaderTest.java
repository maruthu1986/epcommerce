/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.service.datapolicy.readers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import org.mockito.Mock;

import com.elasticpath.domain.datapolicy.DataPoint;
import com.elasticpath.domain.datapolicy.impl.DataPointImpl;
import com.elasticpath.service.datapolicy.DataPointValueService;
import com.elasticpath.service.datapolicy.impl.DataPointValue;

/**
 * Common test class for all data point value readers.
 */
public abstract class AbstractDataPointValueReaderTest {
	private static final Long UIDPK = 1L;
	private static final Date CREATED_DATE = new Date();
	private static final Date LAST_MODIFIED_DATE = new Date();

	protected static final String CUSTOMER_GUID = "guid";

	@Mock
	protected DataPointValueService dataPointValueService;

	@Test
	public void shouldReturnDataPointValueWithAllFields() {
		boolean shouldIncludeFieldValue = true;

		DataPoint dataPoint1 = new DataPointImpl();
		dataPoint1.setDataKey(getDataPoint1Key());

		Collection<DataPoint> dataPoints = Collections.singletonList(dataPoint1);
		Collection<String> dataPointKeys = Collections.singletonList(dataPoint1.getDataKey());

		String expectedReadQuery = getReader().getReadQuery(dataPointKeys);

		when(dataPointValueService.readValuesByQuery(expectedReadQuery, AbstractDataPointValueReader.PARAMETER_LIST_NAME, dataPointKeys,
			new Object[]{CUSTOMER_GUID}))
			.thenReturn(getRawData(shouldIncludeFieldValue));

		Collection<DataPointValue> dataPointValues = getReader().readValues(CUSTOMER_GUID, dataPoints);

		assertThat(dataPointValues)
			.as("There must be one data point value")
			.hasSize(1);

		DataPointValue actualDataPointValue = dataPointValues.iterator().next();
		assertThat(actualDataPointValue)
			.as("Actual and expected data point values must be the sames")
			.isEqualToComparingFieldByField(getExpectedDataPointValue(shouldIncludeFieldValue));

		verify(dataPointValueService).readValuesByQuery(expectedReadQuery, AbstractDataPointValueReader.PARAMETER_LIST_NAME, dataPointKeys,
			new Object[]{CUSTOMER_GUID});

	}

	//in case that db value doesn't exist, CM UI and report should display the data point with empty fields (dates, value etc)
	@Test
	public void shouldReturnDataPointValueWithAllFieldsAndDefaultValuesWhenDbValueDoesNotExist() {
		List<Object[]> emptyList = new ArrayList<>(); //can't Collections.EMPTY_LIST because the list must be modifiable

		DataPoint dataPoint1 = new DataPointImpl();
		dataPoint1.setDataKey(getDataPoint1Key());

		Collection<DataPoint> dataPoints = Collections.singletonList(dataPoint1);
		Collection<String> dataPointKeys = Collections.singletonList(dataPoint1.getDataKey());

		String expectedReadQuery = getReader().getReadQuery(dataPointKeys);

		when(dataPointValueService.readValuesByQuery(expectedReadQuery, AbstractDataPointValueReader.PARAMETER_LIST_NAME, dataPointKeys,
			new Object[]{CUSTOMER_GUID}))
			.thenReturn(emptyList);

		Collection<DataPointValue> dataPointValues = getReader().readValues(CUSTOMER_GUID, dataPoints);

		assertThat(dataPointValues)
			.as("There must be one data point value")
			.hasSize(1);

		DataPointValue actualDataPointValue = dataPointValues.iterator().next();

		DataPointValue expectedDataPointValue = new DataPointValue();
		expectedDataPointValue.setUidPk(0L);
		expectedDataPointValue.setLocation(getLocation());
		expectedDataPointValue.setField(getDataPoint1DbFieldName());
		expectedDataPointValue.setRemovable(dataPoint1.isRemovable());
		expectedDataPointValue.setDataPointName(dataPoint1.getName());
		expectedDataPointValue.setKey(dataPoint1.getDataKey());
		expectedDataPointValue.setValue("");

		assertThat(actualDataPointValue)
			.as("Actual and expected data point values must be the sames")
			.isEqualToComparingFieldByField(expectedDataPointValue);

		verify(dataPointValueService).readValuesByQuery(expectedReadQuery, AbstractDataPointValueReader.PARAMETER_LIST_NAME, dataPointKeys,
			new Object[]{CUSTOMER_GUID});

	}

	@Test
	public void shouldBeApplicableForLocation() {
		assertThat(getReader().isApplicableTo(getLocation()))
			.as("Should be applicable for", getLocation())
			.isTrue();
	}

	@Test
	public void shouldBeTrueForValidKey() {
		String dataPoint1Key = getDataPoint1Key();
		mockDataServiceIfRequired(dataPoint1Key, true);

		assertThat(getReader().validateKey(dataPoint1Key))
			.as("The data point key must be valid")
			.isTrue();
	}

	@Test
	public void shouldBeFalseForInvalidKey() {
		String dataPoint1Key = "dummyKey";
		mockDataServiceIfRequired(dataPoint1Key, false);

		assertThat(getReader().validateKey(dataPoint1Key))
			.as("The data point key must be invalid")
			.isFalse();
	}

	@Test
	public void shouldGenerateValidJPQLReadQueryForOneDataPointKeyWithoutDbValue() {
		List<String> dataPointKeys = Collections.singletonList(getDataPoint1Key());

		assertThat(getReader().getReadQuery(dataPointKeys))
			.as("JPQL read query is invalid")
			.isEqualTo(getExpectedReadQuery(getDataPoint1Key()));
	}

	@Test
	public void shouldGenerateValidJPQLReadQueryForOneDataPointKeyWithAllFields() {
		List<String> dataPointKeys = Collections.singletonList(getDataPoint1Key());

		assertThat(getReader().getReadQuery(dataPointKeys))
			.as("JPQL read query is invalid")
			.isEqualTo(getExpectedReadQuery(getDataPoint1Key()));
	}

	@Test
	public void shouldGenerateValidJPQLReadQueryForTwoDataPointKeysWithAllFields() {
		List<String> dataPointKeys = Arrays.asList(getDataPoint1Key(), getDataPoint2Key());

		assertThat(getReader().getReadQuery(dataPointKeys))
			.as("JPQL read query is invalid")
			.isEqualTo(getExpectedReadQuery(getDataPoint1Key(), getDataPoint2Key()));
	}

	/**
	 * The data point value location.
	 * @return the location
	 */
	protected abstract String getLocation();

	/**
	 * The raw data.
	 * @param shouldIncludeFieldValue if true, field value will be included
	 * @return the raw data to be provided by readers.
	 */
	protected List<Object[]> getRawData(final boolean shouldIncludeFieldValue) {
		Object[] rawData;

		if (shouldIncludeFieldValue) {
			rawData = new Object[]{UIDPK, CREATED_DATE, LAST_MODIFIED_DATE, getDataPoint1Key(), getDataPoint1Value()};
		} else {
			rawData = new Object[]{UIDPK, CREATED_DATE, LAST_MODIFIED_DATE};
		}

		return Collections.singletonList(rawData);
	}

	/**
	 * The expected {@link DataPointValue}.
	 *
	 * @param shouldIncludeFieldValue if true, the actual db value will be set to {@link DataPointValue}
	 * @return the expected {@link DataPointValue}
	 */
	protected DataPointValue getExpectedDataPointValue(final boolean shouldIncludeFieldValue) {
		DataPointValue dataPointValue = new DataPointValue();
		dataPointValue.setUidPk(UIDPK);
		dataPointValue.setCreatedDate(CREATED_DATE);
		dataPointValue.setLastModifiedDate(LAST_MODIFIED_DATE);
		dataPointValue.setLocation(getLocation());
		dataPointValue.setField(getDataPoint1DbFieldName());
		dataPointValue.setKey(getDataPoint1Key());
		dataPointValue.setCustomerGuid(CUSTOMER_GUID);
		dataPointValue.setPopulated(true);
		if (shouldIncludeFieldValue) {
			dataPointValue.setValue(getDataPoint1Value());
		}

		return dataPointValue;
	}

	/**
	 * The implementation of {@link com.elasticpath.service.datapolicy.DataPointValueRemover}.
	 * @return the remover
	 */
	protected abstract AbstractDataPointValueReader getReader();

	/**
	 * Return the value of the first data point.
	 *
	 * @return the data point value.
	 */
	protected abstract String getDataPoint1Value();

	/**
	 * Return the key for the first  data point.
	 * @return the key.
	 */
	protected abstract String getDataPoint1Key();

	/**
	 * Return the key for the second data point.
	 * @return the key.
	 */
	protected abstract String getDataPoint2Key();

	/**
	 * Return the data point value's database field name.
	 *
	 * @return null, if not supported.
	 */
	protected String getDataPoint1DbFieldName() {
		return null;
	}

	/**
	 * Mock data service, if required, when validating data point key.
	 *
	 * @param dataPointKey the data point key.
	 * @param returnValue the value returned by the mock.
	 */
	protected void mockDataServiceIfRequired(final String dataPointKey, final boolean returnValue) {
		//not required for all readers
	}

	protected abstract String getExpectedReadQuery(String... dataPointKeys);

	protected DataPoint createDataPoint(final String dataPoint1Name, final String dataPointKey, final String dataPoint1Description) {

		DataPoint dataPoint = new DataPointImpl();

		dataPoint.setName(dataPoint1Name);
		dataPoint.setDataLocation(getLocation());
		dataPoint.setDataKey(dataPointKey);
		dataPoint.setDescriptionKey(dataPoint1Description);

		return dataPoint;
	}

	protected DataPointValue createDataPointValue(final Long uidPk, final Date createdDate, final Date lastModifiedDate,
		final String dataPoint1Name, final String dataPointKey, final String dataPoint1Value) {

		return createDataPointValue(uidPk, createdDate, lastModifiedDate, dataPoint1Name, dataPointKey, dataPoint1Value, null);

	}
	protected DataPointValue createDataPointValue(final Long uidPk, final Date createdDate, final Date lastModifiedDate,
		final String dataPoint1Name, final String dataPointKey, final String dataPoint1Value, final String fieldName) {

		DataPointValue dataPointValue = new DataPointValue();

		dataPointValue.setUidPk(uidPk);
		dataPointValue.setCreatedDate(createdDate);
		dataPointValue.setLastModifiedDate(lastModifiedDate);
		dataPointValue.setLocation(getLocation());
		dataPointValue.setDataPointName(dataPoint1Name);
		dataPointValue.setKey(dataPointKey);
		dataPointValue.setRemovable(false);
		dataPointValue.setValue(dataPoint1Value);
		dataPointValue.setField(fieldName);

		return dataPointValue;
	}
}
