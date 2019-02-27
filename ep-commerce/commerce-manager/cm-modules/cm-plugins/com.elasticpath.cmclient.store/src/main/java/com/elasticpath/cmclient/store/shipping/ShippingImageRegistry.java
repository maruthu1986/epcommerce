/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.shipping;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.core.AbstractImageRegistry;
import com.elasticpath.cmclient.store.StorePlugin;

/**
 * Provides an image registry for the promotions plugin. Caches the images so that they don't have to be loaded more than once.
 */
public final class ShippingImageRegistry extends AbstractImageRegistry {
	/**
	 * HashMap for storing the images.
	 */
	private static final String PLUGIN_ID = StorePlugin.PLUGIN_ID;

	/**
	 * Search tab icon.
	 */
	public static final ImageDescriptor IMAGE_SHIPPING_LEVEL = getImageDescriptor(PLUGIN_ID, "shipping_default_22.png"); //$NON-NLS-1$

	/**
	 * Create Shipping Level Dialog icon.
	 */
	public static final ImageDescriptor IMAGE_SHIPPING_LEVEL_CREATE = getImageDescriptor(PLUGIN_ID, "shipping-create_default_22.png"); //$NON-NLS-1$

	/**
	 * Shipping levels delete action.
	 */
	public static final ImageDescriptor IMAGE_SHIPPING_LEVEL_DELETE = getImageDescriptor(PLUGIN_ID, "shipping-delete_default_22.png"); //$NON-NLS-1$
	
	private ShippingImageRegistry() {
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
