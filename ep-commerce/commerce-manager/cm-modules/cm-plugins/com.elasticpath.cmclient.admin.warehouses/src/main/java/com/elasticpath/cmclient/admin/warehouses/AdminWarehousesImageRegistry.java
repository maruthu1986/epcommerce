/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.warehouses;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.core.AbstractImageRegistry;

/**
 * The warehouses functionality's image registry.
 */
public final class AdminWarehousesImageRegistry extends AbstractImageRegistry {

	private static final String PLUGIN_ID = AdminWarehousesPlugin.PLUGIN_ID;

	/**
	 * Warehouse create image.
	 */
	public static final ImageDescriptor IMAGE_WAREHOUSE_CREATE = getImageDescriptor(PLUGIN_ID, "add_default_22.png"); //$NON-NLS-1$

	/**
	 * Warehouse edit image.
	 */
	public static final ImageDescriptor IMAGE_WAREHOUSE_EDIT = getImageDescriptor(PLUGIN_ID, "edit_default_22.png"); //$NON-NLS-1$

	/**
	 * Warehouse delete image.
	 */
	public static final ImageDescriptor IMAGE_WAREHOUSE_DELETE = getImageDescriptor(PLUGIN_ID, "delete_default_22.png"); //$NON-NLS-1$

	/**
	 * Warehouse image.
	 */
	public static final ImageDescriptor IMAGE_WAREHOUSE = getImageDescriptor(PLUGIN_ID, "warehouse_default_22.png"); //$NON-NLS-1$


	private AdminWarehousesImageRegistry() {
		//util class
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
