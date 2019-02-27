/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.conditionbuilder.plugin;

import com.elasticpath.cmclient.core.ui.AbstractEpUIPlugin;

/**
 * The activator class for this plugin.
 */
public class ConditionBuilderPlugin extends AbstractEpUIPlugin {
	/**
	 * Condition Builder plugin ID.
	 */
	public static final String PLUGIN_ID = "com.elasticpath.cmclient.conditionbuilder"; //$NON-NLS-1$


	@Override
	protected void loadLocalizedMessages() {
		ConditionBuilderMessages.get();
	}
}
