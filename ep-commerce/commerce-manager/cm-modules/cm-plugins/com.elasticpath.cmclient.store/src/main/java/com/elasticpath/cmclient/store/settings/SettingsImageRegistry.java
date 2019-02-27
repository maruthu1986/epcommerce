/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.settings;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.core.AbstractImageRegistry;
import com.elasticpath.cmclient.store.StorePlugin;

/**
 * Provides an image registry for the settings.
 */
public final class SettingsImageRegistry extends AbstractImageRegistry {

	/**
	 * HashMap for storing the images.
	 */
	private static final String PLUGIN_ID = StorePlugin.PLUGIN_ID;

	/**
	 * Settings tab icon.
	 */
	public static final ImageDescriptor IMAGE_SETTING_TAB = getImageDescriptor(PLUGIN_ID, "system-configuration_default_22.png"); //$NON-NLS-1$

	/**
	 * Store image.
	 */
	public static final ImageDescriptor IMAGE_STORE = getImageDescriptor(PLUGIN_ID, "store_default_22.png"); //$NON-NLS-1$

	private SettingsImageRegistry() {
		// utility class
	}

	/**
	 * Returns and instance of <code>Image</code> of an <code>ImageDescriptor</code>.
	 *
	 * @param imageDescriptor the image descriptor
	 * @return instance of an <code>Image</code>
	 */
	public static Image getImage(final ImageDescriptor imageDescriptor) {
		return getImage(imageDescriptor, PLUGIN_ID);
	}

	/**
	 * Disposes all the images in the <code>HashMap</code>. Should be called by the Plugin's stop method.
	 */
	public static void disposeAllImages() {
		disposeAllImages(PLUGIN_ID);
	}
}
