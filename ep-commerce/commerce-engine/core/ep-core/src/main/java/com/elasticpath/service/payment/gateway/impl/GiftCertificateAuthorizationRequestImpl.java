/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.service.payment.gateway.impl;

import java.util.Objects;

import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.plugin.payment.transaction.impl.AuthorizationTransactionRequestImpl;
import com.elasticpath.service.payment.gateway.GiftCertificateAuthorizationRequest;

/**
 * Implementation of an authorization request for a gift certificate.
 */
public class GiftCertificateAuthorizationRequestImpl extends AuthorizationTransactionRequestImpl implements GiftCertificateAuthorizationRequest {
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
		if (!(other instanceof GiftCertificateAuthorizationRequestImpl)) {
			return false;
		}
		if (!super.equals(other)) {
			return false;
		}
		GiftCertificateAuthorizationRequestImpl that = (GiftCertificateAuthorizationRequestImpl) other;
		return Objects.equals(giftCertificate, that.giftCertificate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), giftCertificate);
	}
}
