/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.service.datapolicy.removers;

import com.elasticpath.commons.util.Pair;
import com.elasticpath.service.datapolicy.DataPointLocationEnum;

/**
 * The order payment GC value remover.
 */
public class OrderPaymentGiftCertificateFieldRemover extends AbstractDataPointValueRemover {

	@Override
	public String getSupportedLocation() {
		return DataPointLocationEnum.ORDER_PAYMENT_GIFT_CERTIFICATE.getName();
	}

	@Override
	public Pair<String, String> getJPQLUpdate() {
		return new Pair<>("GiftCertificateImpl", "gc");
	}
}
