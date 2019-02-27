/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.service.datapolicy.readers;

public abstract class AbstractAddressFieldReaderTest extends AbstractDataPointValueReaderTest {

	protected static final String DATAPOINT_1_KEY = "PHONE_NUMBER";
	protected static final String DATAPOINT_1_DB_FIELD_NAME = "phoneNumber";
	protected static final String DATAPOINT_1_DB_VALUE = "123456789";

	protected static final String DATAPOINT_2_KEY = "FIRST_NAME";
	protected static final String DATAPOINT_2_DB_FIELD_NAME = "firstName";
	protected static final String DATAPOINT_2_DB_VALUE = "Harry";

	protected abstract String getJPQLFrom();

	@Override
	protected String getDataPoint1Key() {
		return DATAPOINT_1_KEY;
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
	protected String getDataPoint2Key() {
		return DATAPOINT_2_KEY;
	}

	@Override
	protected String getExpectedReadQuery(final String... dataPointKeys) {

		String query =  "SELECT address.uidPk, address.creationDate, address.lastModifiedDate"
			.concat(",'")
			.concat(dataPointKeys[0])
			.concat("',address.")
			.concat(DATAPOINT_1_DB_FIELD_NAME);

			if (dataPointKeys.length == 2) {
				query = query.concat(",'")
					.concat(dataPointKeys[1])
					.concat("',address.")
					.concat(DATAPOINT_2_DB_FIELD_NAME);
			}

		return query.concat(getJPQLFrom());
	}
}
