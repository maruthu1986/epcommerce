/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.email.producer.spi.composer.util.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.store.Store;
import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.domain.impl.EmailPropertiesImpl;
import com.elasticpath.email.producer.spi.composer.util.EmailCompositionConfiguration;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Test class for {@link EmailCompositionConfigurationFactoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class EmailCompositionConfigurationFactoryImplTest {

	@Mock
	private StoreService storeService;

	@Mock
	private SettingsReader settingsReader;

	@Mock
	private Store store;

	@InjectMocks
	private EmailCompositionConfigurationFactoryImpl factory;

	@Before
	public void setUp() {
		when(settingsReader.getSettingValue(any(String.class)))
				.thenReturn(mock(SettingValue.class));
	}

	@Test
	public void verifyFactoryCreatesNewInstanceWithStore() {
		final String storeCode = "MYSTORE";

		when(storeService.findStoreWithCode(storeCode)).thenReturn(store);

		final EmailProperties emailProperties = new EmailPropertiesImpl();
		emailProperties.setStoreCode(storeCode);

		final EmailCompositionConfiguration emailCompositionConfiguration = factory.create(emailProperties);

		assertThat(emailCompositionConfiguration).isNotNull();
	}

	@Test
	public void verifyFactoryCreatesNewInstanceWithStoreNotFound() {
		final String storeCode = "MYSTORE";

		// We have a store code, but no such store exists
		when(storeService.findStoreWithCode(storeCode)).thenReturn(null);

		final EmailProperties emailProperties = new EmailPropertiesImpl();
		emailProperties.setStoreCode(storeCode);

		final EmailCompositionConfiguration emailCompositionConfiguration = factory.create(emailProperties);

		assertThat(emailCompositionConfiguration).isNotNull();
	}

	@Test
	public void verifyFactoryCreatesNewInstanceWithoutStore() {
		final EmailProperties emailProperties = new EmailPropertiesImpl();

		// No store code set
		emailProperties.setStoreCode(null);

		final EmailCompositionConfiguration emailCompositionConfiguration = factory.create(emailProperties);

		assertThat(emailCompositionConfiguration).isNotNull();
	}

}