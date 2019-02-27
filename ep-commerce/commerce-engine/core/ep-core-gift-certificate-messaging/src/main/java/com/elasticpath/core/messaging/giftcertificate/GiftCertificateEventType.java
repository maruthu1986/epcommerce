/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.core.messaging.giftcertificate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;
import com.elasticpath.messaging.EventType;
import com.elasticpath.messaging.spi.EventTypeLookup;

/**
 * An enum representing gift-certificate-based {@link EventType}s that are available in the platform.
 */
public class GiftCertificateEventType extends AbstractExtensibleEnum<GiftCertificateEventType> implements EventType {

	private static final long serialVersionUID = 4190688281614615470L;

	/** Ordinal constant for GIFT_CERTIFICATE_CREATED. */
	public static final int GIFT_CERTIFICATE_CREATED_ORDINAL = 0;

	/**
	 * Signals that a gift certificate has been created.
	 */
	public static final GiftCertificateEventType GIFT_CERTIFICATE_CREATED = new GiftCertificateEventType(
			GIFT_CERTIFICATE_CREATED_ORDINAL, "GIFT_CERTIFICATE_CREATED");

	/** Ordinal constant for RESEND_GIFT_CERTIFICATE. */
	public static final int RESEND_GIFT_CERTIFICATE_ORDINAL = 1;

	/**
	 * Signals a request for the gift certificate to be resent.
	 */
	public static final GiftCertificateEventType RESEND_GIFT_CERTIFICATE = new GiftCertificateEventType(RESEND_GIFT_CERTIFICATE_ORDINAL,
			"RESEND_GIFT_CERTIFICATE");

	/**
	 * Create an enum value for a enum type. Name is case-insensitive and will be stored in upper-case.
	 * 
	 * @param ordinal the ordinal value
	 * @param name the name value (this will be converted to upper-case).
	 */
	protected GiftCertificateEventType(final int ordinal, final String name) {
		super(ordinal, name, GiftCertificateEventType.class);
	}

	@Override
	protected Class<GiftCertificateEventType> getEnumType() {
		return GiftCertificateEventType.class;
	}

	@JsonIgnore
	@Override
	public int getOrdinal() {
		return super.getOrdinal();
	}

	/**
	 * Find the enum value with the specified name.
	 * 
	 * @param name the name
	 * @return the enum value
	 */
	public static GiftCertificateEventType valueOf(final String name) {
		return valueOf(name, GiftCertificateEventType.class);
	}

	/**
	 * GiftCertificateEventType implementation of lookup interface.
	 */
	public static class GiftCertificateEventTypeLookup implements EventTypeLookup<GiftCertificateEventType> {

		@Override
		public GiftCertificateEventType lookup(final String name) {
			try {
				return GiftCertificateEventType.valueOf(name);
			} catch (IllegalArgumentException e) {
				return null;
			}
		}

	}

}
