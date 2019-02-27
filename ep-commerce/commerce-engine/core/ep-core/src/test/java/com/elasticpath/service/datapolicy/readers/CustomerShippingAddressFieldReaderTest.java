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
public class CustomerShippingAddressFieldReaderTest extends AbstractAddressFieldReaderTest {

	private static final String LOCATION = DataPointLocationEnum.CUSTOMER_SHIPPING_ADDRESS.getName();

	@InjectMocks
	private CustomerShippingAddressFieldReader reader;

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
			.concat(" INNER JOIN cust.preferredShippingAddressInternal address")
			.concat(" WHERE cust.guid = ?1");
	}
}
