/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.email.producer.spi.composer.util.impl;

import java.util.Optional;

import com.elasticpath.commons.util.StoreMessageSource;
import com.elasticpath.domain.store.Store;
import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.producer.spi.composer.util.EmailCanIncludeHtmlContentsPredicate;
import com.elasticpath.email.producer.spi.composer.util.EmailCompositionConfiguration;
import com.elasticpath.email.producer.spi.composer.util.EmailCompositionConfigurationFactory;
import com.elasticpath.email.producer.spi.composer.util.EmailSentFromAddressSupplier;
import com.elasticpath.email.producer.spi.composer.util.EmailSubjectResolver;
import com.elasticpath.email.producer.spi.composer.util.TemplatePathResolver;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.settings.SettingsReader;

/**
 * Default implementation of {@link EmailCompositionConfigurationFactory}.
 */
public class EmailCompositionConfigurationFactoryImpl implements EmailCompositionConfigurationFactory {

	/**
	 * The path of the Setting that determines whether or not HTML emails are disabled.
	 */
	private static final String EMAIL_TEXT_TEMPLATE_ENABLED_SETTING_PATH = "COMMERCE/SYSTEM/EMAIL/emailTextTemplateEnabled";

	/**
	 * The path of the Setting that specifies the name of the global, default email sender.
	 */
	private static final String SETTING_GLOBAL_SENDER_NAME = "COMMERCE/SYSTEM/EMAIL/emailGlobalSenderName";

	/**
	 * The path of the Setting that specifies the email address of the global, default email sender.
	 */
	private static final String SETTING_GLOBAL_SENDER_ADDRESS = "COMMERCE/SYSTEM/EMAIL/emailGlobalSenderAddress";

	private SettingsReader settingsReader;
	private StoreMessageSource storeMessageSource;
	private StoreService storeService;

	@Override
	public EmailCompositionConfiguration create(final EmailProperties emailProperties) {
		final Optional<Store> storeOptional = Optional.ofNullable(emailProperties.getStoreCode())
				.map(storeService::findStoreWithCode);

		final EmailSentFromAddressSupplier emailSentFromAddressSupplier =
				new EmailSentFromAddressSupplierImpl(storeOptional.orElse(null),
						getEmailGlobalSenderName(),
						getEmailGlobalSenderAddress());

		final EmailSubjectResolver emailSubjectResolver =
				new EmailSubjectResolverImpl((storeCode, messageCode, locale) ->
						Optional.ofNullable(storeMessageSource.getMessage(storeCode, messageCode, locale)));

		final TemplatePathResolver templatePathResolver = new TemplatePathResolverImpl();

		final EmailCanIncludeHtmlContentsPredicate emailCanIncludeHtmlContentsPredicate =
				new EmailCanIncludeHtmlContentsPredicateImpl(isEmailTextTemplateEnabled());

		return storeOptional.map(store ->
				new EmailCompositionConfigurationImpl(
						emailProperties,
						emailSentFromAddressSupplier,
						emailSubjectResolver,
						templatePathResolver,
						emailCanIncludeHtmlContentsPredicate,
						store))
				.orElseGet(() ->
						new EmailCompositionConfigurationImpl(
								emailProperties,
								emailSentFromAddressSupplier,
								emailSubjectResolver,
								templatePathResolver,
								emailCanIncludeHtmlContentsPredicate));

	}

	/**
	 * Returns the name of the global, default email sender.
	 *
	 * @return the name of the global, default email sender
	 */
	protected String getEmailGlobalSenderName() {
		return getSystemSettingValue(SETTING_GLOBAL_SENDER_NAME);
	}

	/**
	 * Return the email address of the global, default email sender.
	 *
	 * @return the email address of the global, default email sender
	 */
	protected String getEmailGlobalSenderAddress() {
		return getSystemSettingValue(SETTING_GLOBAL_SENDER_ADDRESS);
	}

	/**
	 * Indicates whether or not the Email Text Template Enabled setting has been enabled.
	 *
	 * @return true if Email Text Template Enabled mode has been enabled
	 */
	protected boolean isEmailTextTemplateEnabled() {
		return getSettingsReader().getSettingValue(EMAIL_TEXT_TEMPLATE_ENABLED_SETTING_PATH).getBooleanValue();
	}

	/**
	 * Retrieves a setting value string with global context from the settings service.
	 *
	 * @param key the key to the setting value
	 * @return the setting value string
	 */
	private String getSystemSettingValue(final String key) {
		return settingsReader.getSettingValue(key).getValue();
	}

	protected SettingsReader getSettingsReader() {
		return settingsReader;
	}

	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}

	protected StoreMessageSource getStoreMessageSource() {
		return storeMessageSource;
	}

	public void setStoreMessageSource(final StoreMessageSource storeMessageSource) {
		this.storeMessageSource = storeMessageSource;
	}

	protected StoreService getStoreService() {
		return storeService;
	}

	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}

}
