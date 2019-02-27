/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.stores;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.core.AbstractImageRegistry;

/**
 * Provides an image registry for the Admin Stores plugin.
 * Caches the images so that they don't have to be loaded more than once.
 */
public final class AdminStoresImageRegistry extends AbstractImageRegistry {

	private static final String PLUGIN_ID = AdminStoresPlugin.PLUGIN_ID;

	/**
	 * Store Create Action Image.
	 */
	public static final ImageDescriptor IMAGE_STORE_CREATE_ACTION = getImageDescriptor(PLUGIN_ID, "add_default_22.png"); //$NON-NLS-1$

	/**
	 * Store Edit Action Image.
	 */
	public static final ImageDescriptor IMAGE_STORE_EDIT_ACTION = getImageDescriptor(PLUGIN_ID, "edit_default_22.png"); //$NON-NLS-1$

	/**
	 * Store Delete Action Image.
	 */
	public static final ImageDescriptor IMAGE_STORE_DELETE_ACTION = getImageDescriptor(PLUGIN_ID, "delete_default_22.png"); //$NON-NLS-1$

	/**
	 * Section item images image.
	 */
	public static final ImageDescriptor IMAGE_STORE_ADMIN_SECTION_ITEM = getImageDescriptor(PLUGIN_ID, "store_default_22.png"); //$NON-NLS-1$

	/**
	 * Catalog Image.
	 */
	public static final ImageDescriptor IMAGE_CATALOG = getImageDescriptor(PLUGIN_ID, "catalog_default_22.png"); //$NON-NLS-1$

	/**
	 * Virtual Catalog Image.
	 */
	public static final ImageDescriptor IMAGE_VIRTUAL_CATALOG = getImageDescriptor(PLUGIN_ID, "catalog-virtual_default_22.png"); //$NON-NLS-1$

	/**
	 * Warehouse Image.
	 */
	public static final ImageDescriptor IMAGE_WAREHOUSE = getImageDescriptor(PLUGIN_ID, "warehouse_default_22.png"); //$NON-NLS-1$

	/**
	 * Change Store State Action Image.
	 */
	public static final ImageDescriptor IMAGE_STORE_PROMOTE_ACTION = getImageDescriptor(PLUGIN_ID, "store-promote_default_22.png"); //$NON-NLS-1$

	private AdminStoresImageRegistry() {
		//utility class
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
