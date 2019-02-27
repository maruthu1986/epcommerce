/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.warehouse;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.core.AbstractImageRegistry;

/**
 * Provides an image registry for the com.elasticpath.cmclient.warehouse plugin. 
 * Caches the images so that they don't have to be loaded more than once.
 */
public final class WarehouseImageRegistry extends AbstractImageRegistry {

	// HashMap for storing the images
	private static final String PLUGIN_ID = WarehousePlugin.PLUGIN_ID;

	/** Return image. */
	public static final ImageDescriptor IMAGE_RETURN = getImageDescriptor(PLUGIN_ID, "return_default_22.png"); //$NON-NLS-1$

	/** Return Edit image. */
	public static final ImageDescriptor IMAGE_RETURN_SMALL = getImageDescriptor(PLUGIN_ID, "return_default_16.png"); //$NON-NLS-1$

	/** Return Remove image. */
	public static final ImageDescriptor IMAGE_RETURN_DELETE = getImageDescriptor(PLUGIN_ID, "delete_default_22.png"); //$NON-NLS-1$

	/** Exchange image. */
	public static final ImageDescriptor IMAGE_EXCHANGE = getImageDescriptor(PLUGIN_ID, "exchange_default_22.png"); //$NON-NLS-1$

	/** Exchange Edit image. */
	public static final ImageDescriptor IMAGE_EXCHANGE_SMALL = getImageDescriptor(PLUGIN_ID, "exchange_default_16.png"); //$NON-NLS-1$
	
	/** SKU table item icon. */
	public static final ImageDescriptor ICON_SKUTABLE_ITEM = getImageDescriptor(PLUGIN_ID, "sku_default_16.png"); //$NON-NLS-1$
	
	/** SKU table edit item icon. */
	public static final ImageDescriptor ICON_SKUTABLE_EDIT_CELL = getImageDescriptor(PLUGIN_ID, "edit-cell_default_16.png"); //$NON-NLS-1$

	/** Warehouse. */
	public static final ImageDescriptor ICON_WAREHOUSE = getImageDescriptor(PLUGIN_ID, "warehouse_default_22.png"); //$NON-NLS-1$
	
	/** Refresh arrow. */
	public static final ImageDescriptor REFRESH = getImageDescriptor(PLUGIN_ID, "arrow_refresh.png"); //$NON-NLS-1$

	/** Open original button. */
	public static final ImageDescriptor IMAGE_OPEN_ORIGINAL_ORDER_BUTTON = getImageDescriptor(PLUGIN_ID, "order_default_22.png"); //$NON-NLS-1$

	/** Complete Shipment dialog image.*/
	public static final ImageDescriptor IMAGE_COMPLETE_SHIPMENT = getImageDescriptor(PLUGIN_ID, "shipping-complete_default_22.png"); //$NON-NLS-1$
	
	/** Complete Shipment dialog title image.*/
	public static final ImageDescriptor IMAGE_VALIDATE_BUTTON = getImageDescriptor(PLUGIN_ID, "accept_default_22.png"); //$NON-NLS-1$
	
	/** Select SKU image. */
	public static final ImageDescriptor IMAGE_SELECT_SKU = getImageDescriptor(PLUGIN_ID, "search_default_22.png"); //$NON-NLS-1$

	/** Inventory image. */
	public static final ImageDescriptor IMAGE_INVENTORY = getImageDescriptor(PLUGIN_ID, "inventory_default_22.png"); //$NON-NLS-1$

	/** Retrieve inventory image. */
	public static final ImageDescriptor IMAGE_RETRIEVE_INVENTORY = getImageDescriptor(PLUGIN_ID, "search_default_22.png"); //$NON-NLS-1$

	private WarehouseImageRegistry() {
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
