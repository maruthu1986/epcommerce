/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.service.datapolicy.readers;

import static org.mockito.Mockito.when;

import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.service.datapolicy.DataPointLocationEnum;

@SuppressWarnings("PMD.TestClassWithoutTestCases")
@RunWith(MockitoJUnitRunner.class)
public class OrderGiftCertificateFieldReaderTest extends AbstractDataPointValueReaderTest {

	private static final String LOCATION = DataPointLocationEnum.ORDER_GIFT_CERTIFICATE.getName();

	private static final String DATAPOINT_1_KEY = "giftCertificate.recipientName";
	private static final String DATAPOINT_1_DB_VALUE = "Recipient";

	private static final String DATAPOINT_2_KEY = "giftCertificate.recipientEmail";

	@InjectMocks
	private OrderGiftCertificateFieldReader reader;

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

		return "SELECT oItemData.uidPk, oItemData.creationDate, oItemData.lastModifiedDate, oItemData.key, oItemData.value"
			.concat(" FROM OrderImpl o")
			.concat(" JOIN o.shipments shipments")
			.concat(" JOIN shipments.shipmentOrderSkusInternal skus")
			.concat(" JOIN skus.itemData oItemData")
			.concat(" WHERE o.customer.guid = ?1")
			.concat(" AND oItemData.key IN (:dataPointKeys)");
	}
}
