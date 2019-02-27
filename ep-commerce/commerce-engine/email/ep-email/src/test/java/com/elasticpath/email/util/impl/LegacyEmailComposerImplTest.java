/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.email.util.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.mail.EmailException;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.util.StoreMessageSource;
import com.elasticpath.commons.util.VelocityEngineInstanceFactory;
import com.elasticpath.domain.store.Store;
import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.domain.impl.EmailPropertiesImpl;
import com.elasticpath.email.producer.spi.composer.util.EmailContextFactory;
import com.elasticpath.service.catalogview.impl.ThreadLocalStorageImpl;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.settings.provider.SettingValueProvider;

/** Test cases for <code>EmailServiceImpl</code>. */
public class LegacyEmailComposerImplTest {

	private static final String UNEXPECTED_EXCEPTION = "unexpected exception";

	private LegacyEmailComposerImpl emailComposer;

	private EmailProperties emailProperties;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private StoreService mockStoreService;
	private StoreMessageSource mockStoreMessageSource;
	private EmailContextFactory emailContextFactory;

	@Mock
	private SettingValueProvider<Boolean> emailTextTemplateEnabled;

	@Mock
	private SettingValueProvider<String> emailGlobalSenderAddress;

	@Mock
	private SettingValueProvider<String> emailGlobalSenderName;

	/**
	 * Prepare for the tests.
	 *
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		emailContextFactory = context.mock(EmailContextFactory.class);
		mockStoreService = context.mock(StoreService.class);
		mockStoreMessageSource = context.mock(StoreMessageSource.class);

		// Create an email service that is stubbed for testing
		emailComposer = new LegacyEmailComposerImpl() {
			@Override
			protected String mergeTemplateIntoString(final String txtTemplate, final Map<String, Object> velocityTemplateResources,
					final String storeCode) throws VelocityException {
				return "Processed Template";
			}
		};
		emailComposer.setEmailContextFactory(emailContextFactory);
		emailComposer.setStoreConfig(new ThreadLocalStorageImpl());
		emailComposer.setStoreService(mockStoreService);
		emailComposer.setStoreMessageSource(mockStoreMessageSource);
		emailComposer.setVelocityEngineFactory(new VelocityEngineInstanceFactory() {
			/** stubbed **/
			@Override
			public VelocityEngine getVelocityEngine(final String storeCode) {
				return null;
			}
		});

		emailProperties  = createAndInitializeEmailProperties();

		assertNotNull(emailProperties.getStoreCode()); // can't be null for tests
	}

	private EmailProperties createAndInitializeEmailProperties() {
		EmailProperties emailProperties = new EmailPropertiesImpl();
		emailProperties.setTextTemplate("text template");
		emailProperties.setHtmlTemplate("html template");
		emailProperties.setDefaultSubject("defaultSubject");
		emailProperties.setStoreCode("test code");
		emailProperties.setTextOnly(true);
		emailProperties.setLocaleDependentSubjectKey(null);
		emailProperties.setEmailLocale(null);

		return emailProperties;
	}

	/**
	 * Test that if the given StoreCode does not map to a Store then an EpServiceException will be thrown.
	 */
	@Test
	public void testSendEmailListStoreSpecificBadStoreCode() {
		emailProperties.setStoreCode("some store");
		// expectations
		context.checking(new Expectations() {
			{
				oneOf(mockStoreService).findStoreWithCode(emailProperties.getStoreCode());
				will(returnValue(null));
			}
		});

		try {
			emailComposer.composeMessage(emailProperties);
			fail("emailComposer should throw exception for non-existing store code");
		} catch (EpServiceException e) { // NOPMD -- nothing to assert, empty block OK
			// pass
		} catch (EmailException e) {
			fail(UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * Test method for 'com.elasticpath.service.misc.impl.emailComposerImpl.sendMail(EmailProperties)'.
	 */
	@Test
	public void testComposeTextEmail() {
		// lets change this to make sure we get the right results
		emailProperties.setHtmlTemplate(null);
		emailProperties.setRecipientAddress("whatever@asif.com");

		final Store mockStore = context.mock(Store.class);
		context.checking(new Expectations() {
			{
				oneOf(mockStore).getEmailSenderAddress();
				will(returnValue("test@elasticpath.com"));

				oneOf(mockStore).getEmailSenderName();
				will(returnValue("test"));

				oneOf(mockStore).getContentEncoding();
				will(returnValue("UTF-8"));

				oneOf(mockStoreService).findStoreWithCode(emailProperties.getStoreCode());
				will(returnValue(mockStore));

				allowing(emailContextFactory).createVelocityContext(mockStore, emailProperties); will(returnValue(Collections.emptyMap()));
			}
		});
		try {
			emailComposer.composeMessage(emailProperties);
		} catch (EmailException e) {
			fail(UNEXPECTED_EXCEPTION);
		}

		// test non-existing store
		emailProperties.setStoreCode("some store");
		// expectations
		context.checking(new Expectations() {
			{
				oneOf(mockStoreService).findStoreWithCode(emailProperties.getStoreCode());
				will(returnValue(null));
			}
		});

		try {
			emailComposer.composeMessage(emailProperties);
			fail("emailComposer should throw exception for non-persisted store code");
		} catch (EpServiceException e) { // NOPMD -- nothing to assert, empty block OK
			// pass
		} catch (EmailException e) {
			fail(UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * Test method for 'com.elasticpath.service.misc.impl.emailComposerImpl.sendMail(EmailProperties)'.
	 */
	@Test
	public void testComposeHtmlEmail() {
		emailProperties.setRecipientAddress("whatever@asif.com");
		emailProperties.setTextOnly(false);

		context.checking(new Expectations() {
			{
				oneOf(emailTextTemplateEnabled).get();
				will(returnValue(false));
			}
		});

		emailComposer.setEmailTextTemplateEnabledProvider(emailTextTemplateEnabled);

		final Store mockStore = context.mock(Store.class);
		context.checking(new Expectations() {
			{
				oneOf(mockStore).getEmailSenderAddress();
				will(returnValue("test@elasticpath.com"));

				oneOf(mockStore).getEmailSenderName();
				will(returnValue("test"));

				oneOf(mockStoreService).findStoreWithCode(emailProperties.getStoreCode());
				will(returnValue(mockStore));

				allowing(emailContextFactory).createVelocityContext(mockStore, emailProperties); will(returnValue(Collections.emptyMap()));
			}
		});
		try {
			emailComposer.composeMessage(emailProperties);
		} catch (EmailException e) {
			fail(UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * Test method for 'com.elasticpath.service.misc.impl.emailComposerImpl.composeMessage(EmailProperties)'.
	 */
	@Test
	public void testUseGlobalTemplates() {
		emailProperties.setRecipientAddress("whatever@asif.com");
		emailProperties.setStoreCode(null);

		context.checking(new Expectations() {
			{
				oneOf(emailGlobalSenderAddress).get();
				will(returnValue("customerService@SomeDomain.com"));
			}
		});
		emailComposer.setEmailGlobalSenderAddressProvider(emailGlobalSenderAddress);

		context.checking(new Expectations() {
			{
				oneOf(emailGlobalSenderName).get();
				will(returnValue("Customer Service"));
			}
		});
		emailComposer.setEmailGlobalSenderNameProvider(emailGlobalSenderName);

		context.checking(new Expectations() { {
			allowing(emailContextFactory).createVelocityContext(null, emailProperties); will(returnValue(Collections.emptyMap()));
		} });

		try {
			emailComposer.composeMessage(emailProperties);
		} catch (EmailException e) {
			fail(UNEXPECTED_EXCEPTION);
		}
	}
	/**
	 * Test method for 'com.elasticpath.service.misc.impl.emailComposerImpl.getEmailSubject(EmailProperties)'.
	 */
	@Test
	public void testGetEmailSubject() {
		final String defaultSubject = "my default subject";
		final EmailProperties emailProp = createAndInitializeEmailProperties();

		//only default message
		emailProp.setDefaultSubject(defaultSubject);
		assertEquals("No message key, will get the default message", defaultSubject, emailComposer.getEmailSubject(emailProp));

		//property key exists, but message not found
		emailProp.setLocaleDependentSubjectKey("my.english.key");
		emailProp.setEmailLocale(Locale.CANADA);
		context.checking(new Expectations() {
			{
				oneOf(mockStoreMessageSource).getMessage(
						emailProp.getStoreCode(), emailProp.getLocaleDependentSubjectKey(), emailProp.getEmailLocale());
				will(returnValue(null));
			}
		});

		assertEquals("Message key exists, but no message found in properties files", defaultSubject, emailComposer.getEmailSubject(emailProp));

		//property key exists, and message found
		final String englishTranslationMessage = "my meesage for en locale";
		context.checking(new Expectations() {
			{

				oneOf(mockStoreMessageSource).getMessage(
						emailProp.getStoreCode(), emailProp.getLocaleDependentSubjectKey(), emailProp.getEmailLocale());
				will(returnValue(englishTranslationMessage));
			}
		});

		assertEquals("Message key exists, and message found in properties files",
				englishTranslationMessage, emailComposer.getEmailSubject(emailProp));

	}

}
