/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.email.producer.spi.composer.util.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.domain.impl.EmailPropertiesImpl;

/**
 * Test class for {@link EmailCanIncludeHtmlContentsPredicateImpl}.
 */
public class EmailCanIncludeHtmlContentsPredicateImplTest {

	private EmailProperties emailProperties;

	@Before
	public void setUp() {
		emailProperties = new EmailPropertiesImpl();
	}

	@Test
	public void verifyNoMatchWhenEmailPropertiesSpecifiesTextOnly() throws Exception {
		emailProperties.setTextOnly(true);

		assertThat(new EmailCanIncludeHtmlContentsPredicateImpl(false).test(emailProperties))
				.isFalse();
	}

	@Test
	public void verifyNoMatchWhenSettingSpecifiesTextOnly() throws Exception {
		emailProperties.setTextOnly(false);

		assertThat(new EmailCanIncludeHtmlContentsPredicateImpl(true).test(emailProperties))
				.isFalse();
	}

	@Test
	public void verifyMatchWhenEmailPropertiesAndSettingsDoNotSpecifyTextOnly() throws Exception {
		emailProperties.setTextOnly(false);

		assertThat(new EmailCanIncludeHtmlContentsPredicateImpl(false).test(emailProperties))
				.isTrue();
	}

	@Test
	public void verifyMatchWhenEmailPropertiesHasNullTextOnlyFieldAndSettingsDoNotSpecifyTextOnly() throws Exception {
		emailProperties.setTextOnly(null);

		assertThat(new EmailCanIncludeHtmlContentsPredicateImpl(false).test(emailProperties))
				.isTrue();

	}

}