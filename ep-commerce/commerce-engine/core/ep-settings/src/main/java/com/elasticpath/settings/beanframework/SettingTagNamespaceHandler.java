/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.settings.beanframework;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Namespace Handler to parse the {@code <setting path="foo" context="bar" />} tag.
 */
public class SettingTagNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("setting", new SettingBeanDefinitionParser());
	}

}
