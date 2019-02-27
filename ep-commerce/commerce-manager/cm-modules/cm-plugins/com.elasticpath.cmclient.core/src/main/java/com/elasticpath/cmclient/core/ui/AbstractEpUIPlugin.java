/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.elasticpath.cmclient.core.CorePlugin;

/**
 * This plugin binds the localized messages for a plugin when that plugin starts.
 */
public abstract class AbstractEpUIPlugin extends AbstractUIPlugin {
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		CorePlugin.registerPreStartupCallback(() -> {
			loadLocalizedMessages();

		});
	}

	/**
	 * Loads the localized messages for this plugin.
	 */
	protected abstract void loadLocalizedMessages();
}
