/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment;

import com.elasticpath.cmclient.core.ui.AbstractEpUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 */
public class FulfillmentPlugin extends AbstractEpUIPlugin {

	/**
	 * The plug-in ID.
	 */
	public static final String PLUGIN_ID = "com.elasticpath.cmclient.fulfillment"; //$NON-NLS-1$
	
	
	@Override
	public void stop(final BundleContext context) throws Exception {
		try {
			FulfillmentImageRegistry.disposeAllImages();
		} catch (ExceptionInInitializerError | IllegalStateException e) {
			// Do nothing.
		}
		super.stop(context);
	}

	@Override
	protected void loadLocalizedMessages() {
		FulfillmentMessages.get();
	}
}
