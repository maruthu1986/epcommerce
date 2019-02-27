/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.service.datapolicy.readers;

import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.service.datapolicy.DataPointLocationEnum;

@SuppressWarnings({"PMD.TestClassWithoutTestCases", "PMD.AvoidUsingHardCodedIP"})
@RunWith(MockitoJUnitRunner.class)
public class OrderIPAddressReaderTest extends AbstractDataPointValueReaderTest {

	private static final String LOCATION = DataPointLocationEnum.ORDER_IP_ADDRESS.getName();

	private static final String DATAPOINT_1_KEY = "ORDER_IP_ADDRESS";
	private static final String DATAPOINT_1_DB_FIELD_NAME = "ipAddress";
	private static final String DATAPOINT_1_DB_VALUE = "127.0.0.1";

	private static final String DATAPOINT_2_KEY = "ORDER_IP_ADDRESS";

	@InjectMocks
	private OrderIPAddressReader reader;

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
	protected String getDataPoint1DbFieldName() {
		return DATAPOINT_1_DB_FIELD_NAME;
	}

	@Override
	protected String getExpectedReadQuery(final String... dataPointKeys) {

		return "SELECT o.uidPk, o.createdDate, o.lastModifiedDate"
			.concat(",'")
			.concat(DATAPOINT_1_KEY)
			.concat("',o.")
			.concat(DATAPOINT_1_DB_FIELD_NAME)
			.concat(" FROM OrderImpl o")
			.concat(" WHERE o.customer.guid = ?1");
	}
}
