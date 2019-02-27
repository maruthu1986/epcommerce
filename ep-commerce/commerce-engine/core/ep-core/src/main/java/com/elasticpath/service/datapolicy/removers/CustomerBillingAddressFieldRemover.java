/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.service.datapolicy.removers;

import com.elasticpath.commons.util.Pair;
import com.elasticpath.service.datapolicy.DataPointLocationEnum;
import com.elasticpath.service.search.IndexType;

/**
 * The customer billing address field value remover.
 */
public class CustomerBillingAddressFieldRemover extends AbstractDataPointValueRemover {

	@Override
	public String getSupportedLocation() {
		return DataPointLocationEnum.CUSTOMER_BILLING_ADDRESS.getName();
	}

	@Override
	public Pair<String, String> getJPQLUpdate() {
		return new Pair<>("CustomerAddressImpl", "address");
	}

	@Override
	protected IndexType getIndexType() {
		return IndexType.CUSTOMER;
	}
}
