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
public class OrderIPAddressRemoverTest extends AbstractDataPointValueRemoverTest {

	private static final String LOCATION = DataPointLocationEnum.ORDER_IP_ADDRESS.getName();

	@InjectMocks
	private OrderIPAddressRemover remover;

	@Override
	protected String getLocation() {
		return LOCATION;
	}

	@Override
	protected AbstractDataPointValueRemover getRemover() {
		return remover;
	}

	@Override
	protected String getExpectedRemoveQuery(final boolean updateMoreFields) {
		return "UPDATE OrderImpl o SET o.lastModifiedDate=CURRENT_TIMESTAMP, o.ipAddress='‐' WHERE o.uidPk IN (:uidpks)";
	}
}
