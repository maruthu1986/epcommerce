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
public class OrderBillingAddressFieldRemoverTest extends AbstractDataPointValueRemoverTest {

	private static final String LOCATION = DataPointLocationEnum.ORDER_BILLING_ADDRESS.getName();

	@InjectMocks
	private OrderBillingAddressFieldRemover remover;

	@Override
	protected String getLocation() {
		return LOCATION;
	}

	@Override
	protected String getDPV1Field() {
		return "lastName";
	}

	@Override
	protected String getDPV2Field() {
		return "street2";
	}

	@Override
	protected AbstractDataPointValueRemover getRemover() {
		return remover;
	}

	@Override
	protected String getExpectedRemoveQuery(final boolean updateMoreFields) {
		String query = "UPDATE OrderAddressImpl address SET address.lastModifiedDate=CURRENT_TIMESTAMP, address.lastName='‐'";
		if (updateMoreFields) {
			query = query.concat(",address.street2='‐'");
		}

		query = query.concat(" WHERE address.uidPk IN (:uidpks)");

		return query;
	}
}
