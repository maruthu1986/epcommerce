/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.messaging.camel;

import org.apache.camel.spring.CamelEndpointFactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;

/**
 * A {@link org.springframework.beans.factory.FactoryBean} which instantiates {@link org.apache.camel.Endpoint} objects based on values stored in
 * the Commerce Engine Settings Framework.
 */
public class SettingValueBackedCamelEndpointFactoryBean extends CamelEndpointFactoryBean implements InitializingBean {

	private String path;
	private String context;
	private SettingsReader settingsReader;

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();

		if (path == null) {
			throw new IllegalStateException("path property must be set.");
		}

		if (getSettingsReader() == null) {
			throw new IllegalStateException("settingsReader property must be set.");
		}

		final SettingValue settingValue = getSettingsReader().getSettingValue(getPath(), getContext());
		setUri(settingValue.getValue());
	}

	public void setPath(final String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setContext(final String context) {
		this.context = context;
	}

	public String getContext() {
		return context;
	}

	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}

	public SettingsReader getSettingsReader() {
		return settingsReader;
	}
}
