/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.settings.beanframework;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.w3c.dom.Element;

import com.elasticpath.settings.provider.impl.SettingValueProviderImpl;

/**
 * Test class for {@link com.elasticpath.settings.beanframework.SettingBeanDefinitionParser}.
 */
@RunWith(MockitoJUnitRunner.class)
public class SettingBeanDefinitionParserTest {

	private static final String PATH = "COMMERCE/ITEST/SETTINGS/mockSetting";
	private static final String CONTEXT = "CONTEXT";
	public static final String PATH_ATTRIBUTE = "path";
	public static final String CONTEXT_ATTRIBUTE = "context";
	public static final String SETTINGS_READER = "settingsReader";
	private static final String SETTING_VALUE_TYPE_CONVERTER = "settingValueTypeConverter";

	private final SettingBeanDefinitionParser parser = new SettingBeanDefinitionParser();

	@Test
	public void verifyGetBeanClassReturnsProvider() {
		assertThat(parser.getBeanClass(null))
				.as("Expected the parser to create beans of type com.elasticpath.settings.provider.impl.SettingValueProviderImpl")
				.isEqualTo(SettingValueProviderImpl.class);
	}

	@Test
	public void verifyDoParseWillConstructABeanDefinitionWithPathOnly() {
		final Element element = mock(Element.class);
		final BeanDefinitionBuilder beanDefinitionBuilder = mock(BeanDefinitionBuilder.class);

		when(element.getAttribute(PATH_ATTRIBUTE)).thenReturn(PATH);
		when(element.getAttribute(CONTEXT_ATTRIBUTE)).thenReturn(null);

		parser.doParse(element, beanDefinitionBuilder);

		verify(beanDefinitionBuilder).addPropertyValue(PATH_ATTRIBUTE, PATH);
		verify(beanDefinitionBuilder).addPropertyReference(SETTINGS_READER, SETTINGS_READER);
		verify(beanDefinitionBuilder).addPropertyReference(SETTING_VALUE_TYPE_CONVERTER, SETTING_VALUE_TYPE_CONVERTER);
	}

	@Test
	public void verifyDoParseWillConstructABeanDefinitionWithPathAndContext() {
		final Element element = mock(Element.class);
		final BeanDefinitionBuilder beanDefinitionBuilder = mock(BeanDefinitionBuilder.class);

		when(element.getAttribute(PATH_ATTRIBUTE)).thenReturn(PATH);
		when(element.getAttribute(CONTEXT_ATTRIBUTE)).thenReturn(CONTEXT);

		parser.doParse(element, beanDefinitionBuilder);

		verify(beanDefinitionBuilder).addPropertyValue(PATH_ATTRIBUTE, PATH);
		verify(beanDefinitionBuilder).addPropertyValue(CONTEXT_ATTRIBUTE, CONTEXT);
		verify(beanDefinitionBuilder).addPropertyReference(SETTINGS_READER, SETTINGS_READER);
		verify(beanDefinitionBuilder).addPropertyReference(SETTING_VALUE_TYPE_CONVERTER, SETTING_VALUE_TYPE_CONVERTER);
	}

}