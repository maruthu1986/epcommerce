/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.messaging.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;

import com.elasticpath.settings.SettingsReader;

/**
 * Implementation of {@link Predicate} backed by a configured EP Setting.
 */
public class SettingValueBackedPredicate implements Predicate {

	private String path;
	private String context;
	private SettingsReader settingsReader;

	@Override
	public boolean matches(final Exchange exchange) {
		return getSettingsReader().getSettingValue(path, context).getBooleanValue();
	}

	protected String getPath() {
		return path;
	}

	public void setPath(final String path) {
		this.path = path;
	}

	protected String getContext() {
		return context;
	}

	public void setContext(final String context) {
		this.context = context;
	}

	protected SettingsReader getSettingsReader() {
		return settingsReader;
	}

	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}

}
