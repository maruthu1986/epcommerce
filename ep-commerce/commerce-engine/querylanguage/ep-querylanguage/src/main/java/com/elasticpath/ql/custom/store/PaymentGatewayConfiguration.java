/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.ql.custom.store;

import com.elasticpath.domain.payment.impl.PaymentGatewayImpl;
import com.elasticpath.ql.parser.AbstractEpQLCustomConfiguration;
import com.elasticpath.ql.parser.EpQLSortOrder;

/**
 * Holds mapping between EPQL fields and Store domain object fields.
 */
public class PaymentGatewayConfiguration extends AbstractEpQLCustomConfiguration {

	@Override
	public void initialize() {
		setQueryPrefix(String.format("SELECT pg.name FROM %s pg", PaymentGatewayImpl.class.getName()));
		addSortField("pg.name", EpQLSortOrder.ASC);
	}
}
