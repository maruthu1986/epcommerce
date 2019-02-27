/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.email.producer.spi.composer.util.impl;

import com.elasticpath.domain.store.Store;
import com.elasticpath.email.producer.spi.composer.util.EmailSentFromAddressSupplier;

/**
 * Default implementation of {@link EmailSentFromAddressSupplier}.
 */
public class EmailSentFromAddressSupplierImpl implements EmailSentFromAddressSupplier {

	private final Store store;
	private final String fallbackSenderName;
	private final String fallbackSenderEmailAddress;

	/**
	 * Constructor.
	 *
	 * @param store                      the store
	 * @param fallbackSenderName         the name of the global, default email sender
	 * @param fallbackSenderEmailAddress the email address of the global, default email sender
	 */
	public EmailSentFromAddressSupplierImpl(final Store store, final String fallbackSenderName, final String fallbackSenderEmailAddress) {
		this.store = store;
		this.fallbackSenderName = fallbackSenderName;
		this.fallbackSenderEmailAddress = fallbackSenderEmailAddress;
	}

	@Override
	public String get() {
		if (store == null) {
			return toEmailAddress(fallbackSenderName, fallbackSenderEmailAddress);
		} else {
			return toEmailAddress(store.getEmailSenderName(), store.getEmailSenderAddress());
		}
	}

	/**
	 * Builds an Email address in the form {@literal Name <address>}.
	 *
	 * @param emailSenderName    the email name
	 * @param emailSenderAddress the email address
	 * @return a String representing an email name and address
	 */
	private String toEmailAddress(final String emailSenderName, final String emailSenderAddress) {
		return emailSenderName + " <" + emailSenderAddress + ">";
	}

}
