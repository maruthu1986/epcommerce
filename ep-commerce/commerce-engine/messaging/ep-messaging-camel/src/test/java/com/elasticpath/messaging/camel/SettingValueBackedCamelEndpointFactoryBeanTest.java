/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.messaging.camel;

import static org.junit.Assert.assertEquals;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Test class for {@link SettingValueBackedCamelEndpointFactoryBean}.
 */
public class SettingValueBackedCamelEndpointFactoryBeanTest {

	private static final String PATH = "THE/SETTINGS/path";
	private static final String CONTEXT = "SETTINGCONTEXT";
	private static final String ENDPOINT_URI = "direct:foo";

	private SettingValueBackedCamelEndpointFactoryBean factoryBean;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	@Mock
	private SettingsReader settingsReader;

	@Mock
	private CamelContext camelContext;

	@Before
	public void setUp() {
		factoryBean = new SettingValueBackedCamelEndpointFactoryBean();
		factoryBean.setCamelContext(camelContext);
		factoryBean.setSettingsReader(settingsReader);
	}

	@Test
	public void verifyGetObjectReturnsEndpointForSettingWithNoContext() throws Exception {
		givenSettingValuePathAndContextReturnsEndpointUri(PATH, null, ENDPOINT_URI);
		givenCamelContextReturnsEndpointForUri(ENDPOINT_URI);

		factoryBean.setPath(PATH);
		factoryBean.afterPropertiesSet();

		final Endpoint endpoint = factoryBean.getObject();
		assertEquals("Unexpected Endpoint URI", ENDPOINT_URI, endpoint.getEndpointUri());
	}

	@Test
	public void verifyGetObjectReturnsEndpointForSettingWithContext() throws Exception {
		givenSettingValuePathAndContextReturnsEndpointUri(PATH, CONTEXT, ENDPOINT_URI);
		givenCamelContextReturnsEndpointForUri(ENDPOINT_URI);

		factoryBean.setPath(PATH);
		factoryBean.setContext(CONTEXT);
		factoryBean.afterPropertiesSet();

		final Endpoint endpoint = factoryBean.getObject();
		assertEquals("Unexpected Endpoint URI", ENDPOINT_URI, endpoint.getEndpointUri());
	}

	@Test(expected = IllegalArgumentException.class)
	public void verifyGetObjectPropagatesSettingsReaderIllegalArgumentExceptions() throws Exception {
		givenSettingsReaderThrowsExceptionForPathAndContext(PATH, CONTEXT, new IllegalArgumentException());

		factoryBean.setPath(PATH);
		factoryBean.setContext(CONTEXT);
		factoryBean.afterPropertiesSet();
	}

	@Test(expected = EpServiceException.class)
	public void verifyGetObjectPropagatesSettingsReaderEpServiceExceptions() throws Exception {
		givenSettingsReaderThrowsExceptionForPathAndContext(PATH, CONTEXT, new EpServiceException("Boom!"));

		factoryBean.setPath(PATH);
		factoryBean.setContext(CONTEXT);
		factoryBean.afterPropertiesSet();
	}

	@Test(expected = IllegalStateException.class)
	public void verifyExceptionThrownIfNoSettingsReaderSet() throws Exception {
		factoryBean = new SettingValueBackedCamelEndpointFactoryBean();
		factoryBean.setPath(PATH);
		factoryBean.afterPropertiesSet();
	}

	@Test(expected = IllegalStateException.class)
	public void verifyExceptionThrownIfNoPathSet() throws Exception {
		factoryBean.afterPropertiesSet();
	}

	private void givenSettingValuePathAndContextReturnsEndpointUri(final String settingPath, final String settingContext, final String endpointUri) {
		context.checking(new Expectations() {
			{
				final SettingValue settingValue = context.mock(SettingValue.class);

				if (settingContext == null) {
					allowing(settingsReader).getSettingValue(settingPath);
					will(returnValue(settingValue));
				}

				allowing(settingsReader).getSettingValue(settingPath, settingContext);
				will(returnValue(settingValue));

				oneOf(settingValue).getValue();
				will(returnValue(endpointUri));
			}
		});
	}

	private void givenCamelContextReturnsEndpointForUri(final String endpointUri) throws Exception {
		context.checking(new Expectations() {
			{
				final Endpoint endpoint = context.mock(Endpoint.class);
				allowing(camelContext).getEndpoint(endpointUri);
				will(returnValue(endpoint));

				allowing(camelContext).resolvePropertyPlaceholders(endpointUri);
				will(returnValue(endpointUri));

				allowing(endpoint).getEndpointUri();
				will(returnValue(endpointUri));
			}
		});
	}

	private void givenSettingsReaderThrowsExceptionForPathAndContext(final String settingPath, final String settingContext,
																	final Exception exception) {
		context.checking(new Expectations() {
			{
				oneOf(settingsReader).getSettingValue(settingPath, settingContext);
				will(throwException(exception));
			}
		});
	}

}