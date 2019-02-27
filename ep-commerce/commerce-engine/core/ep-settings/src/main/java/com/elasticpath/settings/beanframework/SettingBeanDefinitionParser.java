/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.settings.beanframework;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import com.elasticpath.settings.provider.impl.SettingValueProviderImpl;

/**
 * <p>
 * Parses an XML block representing a {@code <setting />} tag definition.
 * </p>
 * <p>
 * The parser will convert an XML block such as the following:
 * </p>
 * <pre>
 * {@code
 * 		<ep-settings:setting id="beanId" path="foo/bar" context="baz"/>
 * }
 * </pre>
 * <p>
 * to a bean definition equivalent to the following:
 * </p>
 * <pre>
 * {@code
 * 		<bean id="beanId" class="com.elasticpath.settings.provider.impl.SettingValueProviderImpl">
 * 			<property name="path" value="foo/bar"/>
 * 			<property name="context" value="baz"/>
 * 			<property name="settingsReader" ref="settingsReader"/>
 * 			<property name="settingValueTypeConverter" ref="settingValueTypeConverter">
 * 		</bean>
 * }
 * </pre>
 */
public class SettingBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

	@Override
	protected Class<?> getBeanClass(final Element element) {
		return SettingValueProviderImpl.class;
	}

	@Override
	protected void doParse(final Element element, final BeanDefinitionBuilder beanDefinitionBuilder) {
		// path will never be null since the schema requires that a value be supplied
		final String path = element.getAttribute("path");
		beanDefinitionBuilder.addPropertyValue("path", path);

		final String context = element.getAttribute("context");
		if (StringUtils.hasText(context)) {
			beanDefinitionBuilder.addPropertyValue("context", context);
		}

		beanDefinitionBuilder.addPropertyReference("settingsReader", "settingsReader");
		beanDefinitionBuilder.addPropertyReference("settingValueTypeConverter", "settingValueTypeConverter");
	}

}