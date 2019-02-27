/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.email.producer.spi.composer.util.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;

/**
 * Test class for {@link EmailSentFromAddressSupplierImpl}.
 */
public class EmailSentFromAddressSupplierImplTest {

	private static final String FALLBACK_SENDER_NAME = "Test Testingman";
	private static final String FALLBACK_SENDER_ADDRESS = "test.person@elasticpath.com";

	@Test
	public void verifySentFromNameRetrievedFromStoreWhenStorePresent() throws Exception {
		final String storeSenderName = "Store Storeperson";
		final String storeSenderAddress = "storemanager@elasticpath.com";

		final Store store = new StoreImpl();
		store.setEmailSenderName(storeSenderName);
		store.setEmailSenderAddress(storeSenderAddress);

		final EmailSentFromAddressSupplierImpl emailAddressSupplier =
				new EmailSentFromAddressSupplierImpl(store, FALLBACK_SENDER_NAME, FALLBACK_SENDER_ADDRESS);

		assertThat(emailAddressSupplier.get())
				.isEqualTo(storeSenderName + " <" + storeSenderAddress + ">");
	}

	@Test
	public void verifyFallBackNameAndAddressUsedWhenStoreNotPresent() throws Exception {
		final EmailSentFromAddressSupplierImpl emailAddressSupplier =
				new EmailSentFromAddressSupplierImpl(null, FALLBACK_SENDER_NAME, FALLBACK_SENDER_ADDRESS);

		assertThat(emailAddressSupplier.get())
				.isEqualTo(FALLBACK_SENDER_NAME + " <" + FALLBACK_SENDER_ADDRESS + ">");
	}

}