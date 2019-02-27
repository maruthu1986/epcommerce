/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistmanager;

import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareUIPlugin;

/**
 * The activator class controls the plug-in life cycle.
 */
public class PriceListManagerPlugin extends AbstractPolicyAwareUIPlugin {

	/**
	 * The plug-in ID.
	 */
	public static final String PLUGIN_ID = "com.elasticpath.cmclient.pricelistmanager"; //$NON-NLS-1$

	@Override
	protected String getPluginId() {
		return PLUGIN_ID;
	}

	@Override
	protected void loadLocalizedMessages() {
		PriceListManagerMessages.get();
	}
}
