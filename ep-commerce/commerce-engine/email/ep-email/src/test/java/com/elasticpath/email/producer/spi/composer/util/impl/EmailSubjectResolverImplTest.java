/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.email.producer.spi.composer.util.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.domain.impl.EmailPropertiesImpl;

/**
 * Test class for {@link EmailSubjectResolverImpl}.
 */
public class EmailSubjectResolverImplTest {

	private EmailProperties emailProperties;

	@Before
	public void setUp() {
		emailProperties = new EmailPropertiesImpl();
	}

	@Test
	public void verifyThemedSubjectMessageIsUsedWhenPresent() throws Exception {
		final String storeCode = "MYSTORE";
		final String subjectMessageKey = "email.subject.localised";
		final String subjectMessage = "my email subject";
		final Locale locale = Locale.CANADA;

		final EmailSubjectResolverImpl emailSubjectResolver =
				new EmailSubjectResolverImpl((paramStoreCode, paramMessageCode, paramLocale) -> {
					assertThat(paramStoreCode).isEqualTo(storeCode);
					assertThat(paramMessageCode).isEqualTo(subjectMessageKey);
					assertThat(paramLocale).isEqualTo(locale);
					return Optional.of(subjectMessage);
				});

		emailProperties.setEmailLocale(locale);
		emailProperties.setStoreCode(storeCode);
		emailProperties.setLocaleDependentSubjectKey(subjectMessageKey);

		assertThat(emailSubjectResolver.apply(emailProperties))
				.isEqualTo(subjectMessage);
	}

	@Test
	public void verifyDefaultSubjectIsUsedAsFallbackWhenThemedSubjectNotPresent() throws Exception {
		final String subjectMessage = "my email subject";

		final EmailSubjectResolverImpl emailSubjectResolver =
				new EmailSubjectResolverImpl((storeCode, messageCode, locale) -> Optional.empty());

		emailProperties.setDefaultSubject(subjectMessage);

		assertThat(emailSubjectResolver.apply(emailProperties))
				.isEqualTo(subjectMessage);
	}

}