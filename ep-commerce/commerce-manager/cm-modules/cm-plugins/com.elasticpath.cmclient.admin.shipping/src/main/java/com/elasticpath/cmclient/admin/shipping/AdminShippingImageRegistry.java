/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.shipping;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.core.AbstractImageRegistry;

/**
 * Provides an image registry for the AdminShipping plug-in. Caches the images so that they don't have to be loaded more than once.
 */
public final class AdminShippingImageRegistry extends AbstractImageRegistry {

	private static final String PLUGIN_ID = AdminShippingPlugin.PLUGIN_ID;

	/**
	 * Shipping region image.
	 */
	public static final ImageDescriptor IMAGE_SHIPPING = getImageDescriptor(PLUGIN_ID, "shipping-region_default_22.png"); //$NON-NLS-1$

	// FIXME: edit icon is incorrect, need to replace. All references are OK.
	/**
	 * Edit shipping region image.
	 */
	public static final ImageDescriptor IMAGE_SHIPPING_EDIT = getImageDescriptor(PLUGIN_ID, "edit_default_22.png"); //$NON-NLS-1$

	/**
	 * Create shipping region image.
	 */
	public static final ImageDescriptor IMAGE_SHIPPING_CREATE = getImageDescriptor(PLUGIN_ID, "add_default_22.png"); //$NON-NLS-1$

	/**
	 * Delete shipping region image.
	 */
	public static final ImageDescriptor IMAGE_SHIPPING_DELETE = getImageDescriptor(PLUGIN_ID, "delete_default_22.png"); //$NON-NLS-1$

	private AdminShippingImageRegistry() {
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
