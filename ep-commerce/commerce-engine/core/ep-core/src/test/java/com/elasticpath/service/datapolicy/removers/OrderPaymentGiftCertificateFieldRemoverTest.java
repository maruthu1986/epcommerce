/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.service.datapolicy.removers;

import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.service.datapolicy.DataPointLocationEnum;

@SuppressWarnings("PMD.TestClassWithoutTestCases")
@RunWith(MockitoJUnitRunner.class)
public class OrderPaymentGiftCertificateFieldRemoverTest extends AbstractDataPointValueRemoverTest {

	private static final String LOCATION = DataPointLocationEnum.ORDER_PAYMENT_GIFT_CERTIFICATE.getName();

	@InjectMocks
	private OrderPaymentGiftCertificateFieldRemover remover;

	@Override
	protected String getLocation() {
		return LOCATION;
	}

	@Override
	protected AbstractDataPointValueRemover getRemover() {
		return remover;
	}

	@Override
	protected String getDPV1Field() {
		return "recipientName";
	}

	@Override
	protected String getDPV2Field() {
		return "recipientEmail";
	}

	@Override
	protected String getExpectedRemoveQuery(final boolean updateMoreFields) {
		String query = "UPDATE GiftCertificateImpl gc SET gc.lastModifiedDate=CURRENT_TIMESTAMP, gc.recipientName='‐'";
		if (updateMoreFields) {
			query = query.concat(",gc.recipientEmail='‐'");
		}

		query = query.concat(" WHERE gc.uidPk IN (:uidpks)");

		return query;
	}
}
