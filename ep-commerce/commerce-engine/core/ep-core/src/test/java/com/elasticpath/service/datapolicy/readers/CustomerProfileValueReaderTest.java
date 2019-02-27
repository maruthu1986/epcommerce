/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.service.datapolicy.readers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.datapolicy.DataPoint;
import com.elasticpath.service.datapolicy.DataPointLocationEnum;
import com.elasticpath.service.datapolicy.impl.DataPointValue;

@SuppressWarnings("PMD.TestClassWithoutTestCases")
@RunWith(MockitoJUnitRunner.class)
public class CustomerProfileValueReaderTest extends AbstractDataPointValueReaderTest {

	private static final String LOCATION = DataPointLocationEnum.CUSTOMER_PROFILE.getName();

	private static final String DATAPOINT_1_KEY = "CP_EMAIL";
	private static final String DATAPOINT_1_DB_VALUE = "email@company.com";

	private static final String DATAPOINT_2_KEY = "CP_FIRST_NAME";
	private static final String DATAPOINT_2_DB_VALUE = "Harry";

	@InjectMocks
	private CustomerProfileValueReader reader;

	@Override
	protected String getLocation() {
		return LOCATION;
	}

	@Override
	protected AbstractDataPointValueReader getReader() {
		return reader;
	}

	@Override
	protected String getDataPoint1Key() {
		return DATAPOINT_1_KEY;
	}

	@Override
	protected String getDataPoint2Key() {
		return DATAPOINT_2_KEY;
	}

	@Override
	protected String getDataPoint1Value() {
		return DATAPOINT_1_DB_VALUE;
	}

	@Override
	protected void mockDataServiceIfRequired(final String dataPointKey, final boolean returnValue) {
		when(dataPointValueService.validateKey(dataPointKey, reader.getNamedValidationQuery()))
		.thenReturn(returnValue);
	}

	@Override
	protected String getExpectedReadQuery(final String... dataPointKeys) {


		return "SELECT cpv.uidPk,cpv.creationDate,cpv.lastModifiedDate,"
			.concat(" cpv.localizedAttributeKey,cpv.shortTextValue,cpv.integerValue,cpv.longTextValue,")
			.concat(" cpv.decimalValue,cpv.booleanValue,cpv.dateValue")
			.concat(" FROM CustomerImpl cust")
			.concat(" INNER JOIN cust.profileValueMap cpv")
			.concat(" WHERE cust.guid = ?1")
			.concat(" AND cpv.localizedAttributeKey IN (:dataPointKeys)");
	}

	@Test
	public void shouldConvertRawData() {
		long uidPk1 = 1L;
		long uidPk2 = 2L;

		Date createdDate = new Date();
		Date lastModifiedDate = new Date();

		String dataPoint1Name = "Customer Profile Email";
		String dataPoint2Name = "Customer Profile First Name";

		String dataPoint1Description = "Description 1";
		String dataPoint2Description = "Description 2";

		Object[] row1 = new Object[]{uidPk1, createdDate, lastModifiedDate, DATAPOINT_1_KEY, DATAPOINT_1_DB_VALUE, null, null, null, null, null};
		Object[] row2 = new Object[]{uidPk2, createdDate, lastModifiedDate, DATAPOINT_2_KEY, DATAPOINT_2_DB_VALUE, null, null, null, null, null};

		List<Object[]> rawData = Arrays.asList(row1, row2);

		DataPoint cartGCDRecipientNameDataPoint = createDataPoint(dataPoint1Name, DATAPOINT_1_KEY, dataPoint1Description);
		DataPoint cartGCDRecipientEmailDataPoint = createDataPoint(dataPoint2Name, DATAPOINT_2_KEY, dataPoint2Description);


		DataPointValue expectedDataPointValue1 =  createDataPointValue(uidPk1, createdDate, lastModifiedDate, dataPoint1Name, DATAPOINT_1_KEY,
			DATAPOINT_1_DB_VALUE);

		DataPointValue expectedDataPointValue2 =  createDataPointValue(uidPk2, createdDate, lastModifiedDate, dataPoint2Name, DATAPOINT_2_KEY,
			DATAPOINT_2_DB_VALUE);

		List<DataPointValue> dataPointValues = reader.convertRawData(rawData, CUSTOMER_GUID, Arrays.asList(cartGCDRecipientNameDataPoint,
			cartGCDRecipientEmailDataPoint));

		assertThat(dataPointValues)
			.containsExactly(expectedDataPointValue1, expectedDataPointValue2);
	}
}
