/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.service.payment.gateway.impl;

import java.util.Objects;

import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.plugin.payment.dto.impl.OrderPaymentDtoImpl;
import com.elasticpath.service.payment.gateway.GiftCertificateOrderPaymentDto;

/**
 * Extension of OrderPaymentDto to support the Gift Certificate payment gateway plugin.
 */
public class GiftCertificateOrderPaymentDtoImpl extends OrderPaymentDtoImpl implements GiftCertificateOrderPaymentDto {
	private GiftCertificate giftCertificate;

	@Override
	public GiftCertificate getGiftCertificate() {
		return giftCertificate;
	}

	@Override
	public void setGiftCertificate(final GiftCertificate giftCertificate) {
		this.giftCertificate = giftCertificate;
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof GiftCertificateOrderPaymentDtoImpl)) {
			return false;
		}
		if (!super.equals(other)) {
			return false;
		}
		GiftCertificateOrderPaymentDtoImpl that = (GiftCertificateOrderPaymentDtoImpl) other;
		return Objects.equals(giftCertificate, that.giftCertificate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), giftCertificate);
	}
}
