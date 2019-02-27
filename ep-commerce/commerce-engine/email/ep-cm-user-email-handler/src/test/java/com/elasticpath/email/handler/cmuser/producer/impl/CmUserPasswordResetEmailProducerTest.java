/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.email.handler.cmuser.producer.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.email.EmailDto;
import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.handler.cmuser.helper.CmUserEmailPropertyHelper;
import com.elasticpath.email.producer.spi.composer.EmailComposer;
import com.elasticpath.service.cmuser.CmUserService;

/**
 * Unit test for {@link CmUserPasswordResetEmailProducer}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CmUserPasswordResetEmailProducerTest {

	private static final String CMUSER_GUID = "cmUserGuid1";
	private static final String PASSWORD = "password1";
	private static final Locale LOCALE = Locale.CANADA;

	@Mock
	private CmUserEmailPropertyHelper cmUserEmailPropertyHelper;

	@Mock
	private EmailComposer emailComposer;

	@Mock
	private CmUserService cmUserService;

	@InjectMocks
	private CmUserPasswordResetEmailProducer emailProducer;

	@Test
	public void verifyPasswordReminderEmailCreatedFromCmUserGuid() throws Exception {
		final EmailDto expectedEmail = EmailDto.builder().build();

		final EmailProperties emailProperties = mock(EmailProperties.class);
		final CmUser cmUser = mock(CmUser.class);

		when(cmUserService.findByGuid(CMUSER_GUID))
				.thenReturn(cmUser);

		when(cmUserEmailPropertyHelper.getResetEmailProperties(cmUser, PASSWORD, LOCALE))
				.thenReturn(emailProperties);

		when(emailComposer.composeMessage(emailProperties))
				.thenReturn(expectedEmail);

		final EmailDto actualEmail = emailProducer.createEmail(CMUSER_GUID, buildValidAdditionalData(PASSWORD, LOCALE));

		assertThat(actualEmail)
				.as("Unexpected email created by producer")
				.isSameAs(expectedEmail);
	}

	@Test
	public void verifyExceptionThrownWhenNoCmUserIsFound() throws Exception {

		when(cmUserService.findByGuid(CMUSER_GUID)).thenReturn(null);

		assertThatThrownBy(() -> emailProducer.createEmail(CMUSER_GUID, buildValidAdditionalData(PASSWORD, LOCALE)))
				.isInstanceOf(IllegalArgumentException.class);
	}

	private Map<String, Object> buildValidAdditionalData(final String password, final Locale locale) {
		final Map<String, Object> additionalData = new HashMap<>();
		additionalData.put("password", password);
		additionalData.put("locale", locale.toLanguageTag());

		return additionalData;
	}

}
