/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.dataimport.impl;

import java.util.Locale;
import java.util.Map;

import com.google.common.collect.Maps;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.core.messaging.dataimport.DataImportEventType;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.cmuser.impl.CmUserImpl;
import com.elasticpath.domain.dataimport.ImportJobStatus;
import com.elasticpath.domain.dataimport.impl.ImportJobStatusImpl;
import com.elasticpath.messaging.EventMessage;
import com.elasticpath.messaging.EventMessagePublisher;
import com.elasticpath.messaging.EventType;
import com.elasticpath.messaging.factory.EventMessageFactory;

/**
 * Test class for {@link com.elasticpath.service.dataimport.impl.ImportJobProcessorImpl}.
 */
public class ImportJobProcessorImplTest {

	private ImportJobProcessorImpl processor;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	@Mock
	private EventMessageFactory eventMessageFactory;

	@Mock
	private EventMessagePublisher eventMessagePublisher;

	@Before
	public void setUp() {
		processor = new ImportJobProcessorImpl();
		processor.setEventMessageFactory(eventMessageFactory);
		processor.setEventMessagePublisher(eventMessagePublisher);
	}

	@Test
	public void verifySendEventPublishesEventMessage() throws Exception {
		final Locale locale = Locale.CANADA;
		final String cmUserGuid = "cmUser-123";
		final String importProcessId = "import-process-1";

		final CmUser cmUser = new CmUserImpl();
		cmUser.setGuid(cmUserGuid);

		final ImportJobStatus importJobStatus = new ImportJobStatusImpl();
		importJobStatus.setProcessId(importProcessId);

		final EventType eventType = DataImportEventType.IMPORT_JOB_COMPLETED;

		final Map<String, Object> data = Maps.newHashMapWithExpectedSize(2);
		data.put("cmUserGuid", cmUserGuid);
		data.put("locale", locale.toString());

		context.checking(new Expectations() {
			{
				final EventMessage eventMessage = context.mock(EventMessage.class);

				oneOf(eventMessageFactory).createEventMessage(eventType, importProcessId, data);
				will(returnValue(eventMessage));

				oneOf(eventMessagePublisher).publish(eventMessage);
			}
		});

		processor.sendEvent(importJobStatus, cmUser, locale);
	}

}