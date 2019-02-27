/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.email.handler.dataimport.producer.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static com.elasticpath.email.EmailDto.builder;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.dataimport.ImportJobStatus;
import com.elasticpath.email.EmailDto;
import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.handler.dataimport.helper.ImportEmailPropertyHelper;
import com.elasticpath.email.producer.spi.composer.EmailComposer;
import com.elasticpath.service.cmuser.CmUserService;
import com.elasticpath.service.dataimport.ImportJobStatusHandler;

/**
 * Test class for {@link ImportJobCompletedEmailProducer}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ImportJobCompletedEmailProducerTest {

	private static final String LOCALE_KEY = "locale";

	private static final String CMUSER_GUID_KEY = "cmUserGuid";

	private static final String PROCESS_ID = "processId1";

	private static final String CMUSER_GUID = "cmUserGuid1";

	private static final Locale LOCALE = Locale.CANADA;

	@Mock
	private EmailComposer emailComposer;

	@Mock
	private CmUserService cmUserService;

	@Mock
	private ImportJobStatusHandler importJobStatusHandler;

	@Mock
	private ImportEmailPropertyHelper importEmailPropertyHelper;

	@InjectMocks
	private ImportJobCompletedEmailProducer emailProducer;

	@Test
	public void verifyImportJobCompletedEmailIsCreated() throws Exception {
		final EmailDto expectedEmail = builder().build();

		final ImportJobStatus importJobStatus = givenImportJobStatusHandlerReturnsImportJobStatus(PROCESS_ID);
		final CmUser cmUser = givenCmUserServiceFindsCmUser(CMUSER_GUID);
		final EmailProperties emailProperties = givenImportEmailPropertyHelperBuildsEmailProperties(importJobStatus, cmUser, LOCALE);

		when(emailComposer.composeMessage(emailProperties))
				.thenReturn(expectedEmail);

		final EmailDto actualEmail = emailProducer.createEmail(PROCESS_ID, buildValidAdditionalData(CMUSER_GUID, LOCALE));

		assertThat(actualEmail)
				.as("Unexpected email created by producer")
				.isSameAs(expectedEmail);
	}

	@Test
	public void verifyExceptionThrownWhenNullProcessId() throws Exception {
		givenCmUserServiceFindsCmUser(CMUSER_GUID);

		assertThatThrownBy(() -> emailProducer.createEmail(null, buildValidAdditionalData(CMUSER_GUID, LOCALE)))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void verifyNoEmailCreatedWhenNullCmUser() throws Exception {
		final Map<String, Object> additionalData = buildValidAdditionalData(CMUSER_GUID, LOCALE);
		additionalData.remove(CMUSER_GUID_KEY);

		final EmailDto actualEmail = emailProducer.createEmail(PROCESS_ID, additionalData);

		assertThat(actualEmail)
				.as("No email should have been created when no CM User Guid was given")
				.isNull();
	}

	@Test
	public void verifyExceptionThrownWhenNullLocale() throws Exception {
		givenCmUserServiceFindsCmUser(CMUSER_GUID);

		final Map<String, Object> additionalData = buildValidAdditionalData(CMUSER_GUID, LOCALE);
		additionalData.remove(LOCALE_KEY);

		assertThatThrownBy(() -> emailProducer.createEmail(PROCESS_ID, additionalData))
				.isInstanceOf(IllegalArgumentException.class);
	}

	private ImportJobStatus givenImportJobStatusHandlerReturnsImportJobStatus(final String processId) {
		final ImportJobStatus importJobStatus = mock(ImportJobStatus.class);

		when(importJobStatusHandler.getImportJobStatus(processId)).thenReturn(importJobStatus);

		return importJobStatus;
	}

	private CmUser givenCmUserServiceFindsCmUser(final String cmUserGuid) {
		final CmUser cmUser = mock(CmUser.class);

		when(cmUserService.findByGuid(cmUserGuid)).thenReturn(cmUser);

		return cmUser;
	}

	private EmailProperties givenImportEmailPropertyHelperBuildsEmailProperties(final ImportJobStatus importJobStatus, final CmUser cmUser,
																				final Locale locale) {
		final EmailProperties emailProperties = mock(EmailProperties.class);

		when(importEmailPropertyHelper.getEmailProperties(importJobStatus, cmUser, locale)).thenReturn(emailProperties);

		return emailProperties;
	}

	private Map<String, Object> buildValidAdditionalData(final String cmUserGuid, final Locale locale) {
		final Map<String, Object> additionalData = new HashMap<>();
		additionalData.put(CMUSER_GUID_KEY, cmUserGuid);
		additionalData.put(LOCALE_KEY, locale.toString());
		return additionalData;
	}

}
