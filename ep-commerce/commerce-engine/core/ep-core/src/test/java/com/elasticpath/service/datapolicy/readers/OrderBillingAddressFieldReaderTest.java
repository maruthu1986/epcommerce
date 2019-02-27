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
public class OrderBillingAddressFieldReaderTest extends AbstractAddressFieldReaderTest {

	private static final String LOCATION = DataPointLocationEnum.ORDER_BILLING_ADDRESS.getName();

	@InjectMocks
	private OrderBillingAddressFieldReader reader;

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
		return " FROM OrderImpl o"
			.concat(" INNER JOIN o.billingAddress address")
			.concat(" WHERE o.customer.guid = ?1");
	}
}
