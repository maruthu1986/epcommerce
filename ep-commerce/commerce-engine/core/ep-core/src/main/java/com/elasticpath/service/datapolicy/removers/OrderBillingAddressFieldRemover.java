/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.service.datapolicy.removers;

import com.elasticpath.commons.util.Pair;
import com.elasticpath.service.datapolicy.DataPointLocationEnum;

/**
 * The order shipping address field value remover.
 */
public class OrderBillingAddressFieldRemover extends AbstractDataPointValueRemover {

	@Override
	public String getSupportedLocation() {
		return DataPointLocationEnum.ORDER_BILLING_ADDRESS.getName();
	}

	@Override
	public Pair<String, String> getJPQLUpdate() {
		return new Pair<>("OrderAddressImpl", "address");
	}
}