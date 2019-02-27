/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.service.datapolicy.readers;

import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.service.datapolicy.DataPointLocationEnum;

@SuppressWarnings("PMD.TestClassWithoutTestCases")
@RunWith(MockitoJUnitRunner.class)
public class OrderPaymentGiftCertificateFieldReaderTest extends AbstractDataPointValueReaderTest {

	private static final String LOCATION = DataPointLocationEnum.ORDER_PAYMENT_GIFT_CERTIFICATE.getName();

	private static final String DATAPOINT_1_KEY = "RECIPIENT_NAME";
	private static final String DATAPOINT_1_DB_FIELD_NAME = "recipientName";
	private static final String DATAPOINT_1_DB_VALUE = "Recipient";

	private static final String DATAPOINT_2_KEY = "RECIPIENT_EMAIL";
	private static final String DATAPOINT_2_DB_FIELD_NAME = "recipientEmail";

	@InjectMocks
	private OrderPaymentGiftCertificateFieldReader reader;

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

		String query =  "SELECT gc.uidPk, gc.creationDate, gc.lastModifiedDate"
			.concat(",'")
			.concat(dataPointKeys[0])
			.concat("',gc.")
			.concat(DATAPOINT_1_DB_FIELD_NAME);

			if (dataPointKeys.length == 2) {
				query = query.concat(",'")
					.concat(dataPointKeys[1])
					.concat("',gc.")
					.concat(DATAPOINT_2_DB_FIELD_NAME);
			}

		return query.concat(" FROM GiftCertificateImpl gc")
					.concat(" WHERE gc.purchaser.guid = ?1");
	}
}
