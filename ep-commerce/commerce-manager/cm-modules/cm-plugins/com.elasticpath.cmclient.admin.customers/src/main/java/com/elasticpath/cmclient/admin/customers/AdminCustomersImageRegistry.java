/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.customers;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.core.AbstractImageRegistry;

/**
 * Provides an image registry for the AdminUsers plugin.
 * Caches the images so that they don't have to be loaded more than once.
 */
public final class AdminCustomersImageRegistry extends AbstractImageRegistry {

	// HashMap for storing the images
	private static final String PLUGIN_ID = AdminCustomersPlugin.PLUGIN_ID;

	/**
	 * Attribute image.
	 */
	public static final ImageDescriptor IMAGE_ATTRIBUTE = getImageDescriptor(PLUGIN_ID, "attribute_default_22.png"); //$NON-NLS-1$
	/**
	 * Attribute create image.
	 */
	public static final ImageDescriptor IMAGE_ATTRIBUTE_CREATE = getImageDescriptor(PLUGIN_ID, "add_default_22.png"); //$NON-NLS-1$
	/**
	 * Attribute edit image.
	 */
	public static final ImageDescriptor IMAGE_ATTRIBUTE_EDIT = getImageDescriptor(PLUGIN_ID, "edit_default_22.png"); //$NON-NLS-1$
	/**
	 * Attribute delete image.
	 */
	public static final ImageDescriptor IMAGE_ATTRIBUTE_DELETE = getImageDescriptor(PLUGIN_ID, "delete_default_22.png"); //$NON-NLS-1$

	/**
	 * Customer segment image.
	 */
	public static final ImageDescriptor IMAGE_CUSTOMER_SEGMENT = getImageDescriptor(PLUGIN_ID, "customer-segment_default_22.png"); //$NON-NLS-1$
	/**
	 * Customer segment create image.
	 */
	public static final ImageDescriptor IMAGE_CUSTOMER_SEGMENT_CREATE = getImageDescriptor(PLUGIN_ID, "add_default_22.png"); //$NON-NLS-1$
	/**
	 * Customer segment edit image.
	 */
	public static final ImageDescriptor IMAGE_CUSTOMER_SEGMENT_EDIT = getImageDescriptor(PLUGIN_ID, "edit_default_22.png"); //$NON-NLS-1$
	/**
	 * Customer segment delete image.
	 */
	public static final ImageDescriptor IMAGE_CUSTOMER_SEGMENT_DELETE = getImageDescriptor(PLUGIN_ID, "delete_default_22.png"); //$NON-NLS-1$

	private AdminCustomersImageRegistry() {
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
