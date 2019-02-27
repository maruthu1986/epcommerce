/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.service.payment.gateway.impl;

import java.util.Objects;

import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.plugin.payment.transaction.impl.CaptureTransactionRequestImpl;
import com.elasticpath.service.payment.gateway.GiftCertificateCaptureRequest;

/**
 * Gift certificate implementation for a capture request.
 */
public class GiftCertificateCaptureRequestImpl extends CaptureTransactionRequestImpl implements GiftCertificateCaptureRequest {
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
		if (!(other instanceof GiftCertificateCaptureRequestImpl)) {
			return false;
		}
		if (!super.equals(other)) {
			return false;
		}
		GiftCertificateCaptureRequestImpl that = (GiftCertificateCaptureRequestImpl) other;
		return Objects.equals(giftCertificate, that.giftCertificate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), giftCertificate);
	}
}
