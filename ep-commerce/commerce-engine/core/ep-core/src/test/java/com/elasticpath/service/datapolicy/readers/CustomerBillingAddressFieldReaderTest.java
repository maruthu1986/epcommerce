/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.service.datapolicy.readers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
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
public class CustomerBillingAddressFieldReaderTest extends AbstractAddressFieldReaderTest {

	private static final String LOCATION = DataPointLocationEnum.CUSTOMER_BILLING_ADDRESS.getName();

	@InjectMocks
	private CustomerBillingAddressFieldReader reader;

	@Override
	protected String getLocation() {
		return LOCATION;
	}

	@Override
	protected AbstractDataPointValueReader getReader() {
		return reader;
	}

	@Override
	protected String getJPQLFrom() {
		return " FROM CustomerImpl cust"
			.concat(" INNER JOIN cust.preferredBillingAddressInternal address")
			.concat(" WHERE cust.guid = ?1");
	}

	@Test
	public void shouldConvertRawData() {
		long uidPk1 = 1L;

		Date createdDate = new Date();
		Date lastModifiedDate = new Date();

		String dataPoint1Name = "Customer Billing Address Phone";
		String dataPoint2Name = "Customer Billing Address First Name";

		String dataPoint1Description = "Description 1";
		String dataPoint2Description = "Description 2";

		Object[] row = new Object[]{uidPk1, createdDate, lastModifiedDate, DATAPOINT_1_KEY, DATAPOINT_1_DB_VALUE,
			DATAPOINT_2_KEY, DATAPOINT_2_DB_VALUE};

		DataPoint cartGCDRecipientNameDataPoint = createDataPoint(dataPoint1Name, DATAPOINT_1_KEY, dataPoint1Description);
		DataPoint cartGCDRecipientEmailDataPoint = createDataPoint(dataPoint2Name, DATAPOINT_2_KEY, dataPoint2Description);


		DataPointValue expectedDataPointValue1 =  createDataPointValue(uidPk1, createdDate, lastModifiedDate, dataPoint1Name, DATAPOINT_1_KEY,
			DATAPOINT_1_DB_VALUE, DATAPOINT_1_DB_FIELD_NAME);

		DataPointValue expectedDataPointValue2 =  createDataPointValue(uidPk1, createdDate, lastModifiedDate, dataPoint2Name, DATAPOINT_2_KEY,
			DATAPOINT_2_DB_VALUE, DATAPOINT_2_DB_FIELD_NAME);

		List<DataPointValue> dataPointValues = reader.convertRawData(Collections.singletonList(row), CUSTOMER_GUID,
				Arrays.asList(cartGCDRecipientNameDataPoint, cartGCDRecipientEmailDataPoint));

		assertThat(dataPointValues)
			.containsExactly(expectedDataPointValue1, expectedDataPointValue2);

	}
}
