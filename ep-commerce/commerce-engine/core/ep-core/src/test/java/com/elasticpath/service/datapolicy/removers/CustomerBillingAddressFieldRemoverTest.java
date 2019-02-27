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
public class CustomerBillingAddressFieldRemoverTest extends AbstractDataPointValueRemoverTest {

	private static final String LOCATION = DataPointLocationEnum.CUSTOMER_BILLING_ADDRESS.getName();

	@InjectMocks
	private CustomerBillingAddressFieldRemover remover;

	@Override
	protected String getLocation() {
		return LOCATION;
	}

	@Override
	protected String getDPV1Field() {
		return "firstName";
	}

	@Override
	protected String getDPV2Field() {
		return "street1";
	}

	@Override
	protected AbstractDataPointValueRemover getRemover() {
		return remover;
	}

	@Override
	protected String getExpectedRemoveQuery(final boolean updateMoreFields) {
		String query = "UPDATE CustomerAddressImpl address "
			.concat("SET address.lastModifiedDate=CURRENT_TIMESTAMP, address.firstName='‐'");

		if (updateMoreFields) {
			query = query.concat(",address.street1='‐'");
		}

		query = query.concat(" WHERE address.uidPk IN (:uidpks)");

		return query;
	}
}
