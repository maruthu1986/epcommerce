/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistmanager;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.core.AbstractImageRegistry;

/**
 * Provides an image registry for the com.elasticpath.cmclient.pricelistmanager plugin.
 * Caches the images so that they don't have to be loaded more than once.
 */
public final class PriceListManagerImageRegistry extends AbstractImageRegistry {

	private static final String PLUGIN_ID = PriceListManagerPlugin.PLUGIN_ID;

	/**
	 * Select PL image.
	 */
	public static final ImageDescriptor IMAGE_PRICE_LIST = getImageDescriptor(PLUGIN_ID, "price-list_default_22.png"); //$NON-NLS-1$
	/**
	 * Select PL add image.
	 */
	public static final ImageDescriptor IMAGE_PRICE_LIST_ADD = getImageDescriptor(PLUGIN_ID, "price-list-add_default_22.png"); //$NON-NLS-1$
	/**
	 * Select PL edit image.
	 */
	public static final ImageDescriptor IMAGE_PRICE_LIST_OPEN = getImageDescriptor(PLUGIN_ID, "price-list_default_22.png"); //$NON-NLS-1$
	/**
	 * Select PL delete image.
	 */
	public static final ImageDescriptor IMAGE_PRICE_LIST_DELETE = getImageDescriptor(PLUGIN_ID, "price-list-delete_default_22.png"); //$NON-NLS-1$

	/**
	 * Select PLA image.
	 */
	public static final ImageDescriptor IMAGE_PRICE_LIST_ASSIGN =
		getImageDescriptor(PLUGIN_ID, "price-list-assignment_default_22.png"); //$NON-NLS-1$
	/**
	 * Select PLA add image.
	 */
	public static final ImageDescriptor IMAGE_PRICE_LIST_ASSIGN_ADD =
		getImageDescriptor(PLUGIN_ID, "price-list-assignment-add_default_22.png"); //$NON-NLS-1$
	/**
	 * Select PLA edit image.
	 */
	public static final ImageDescriptor IMAGE_PRICE_LIST_ASSIGN_OPEN =
		getImageDescriptor(PLUGIN_ID, "price-list-assignment_default_22.png"); //$NON-NLS-1$
	/**
	 * Select PLA delete image.
	 */
	public static final ImageDescriptor IMAGE_PRICE_LIST_ASSIGN_DELETE =
		getImageDescriptor(PLUGIN_ID, "price-list-assignment-delete_default_22.png"); //$NON-NLS-1$

	/**
	 * CSV export image.
	 */
	public static final ImageDescriptor IMAGE_CSV_EXPORT = getImageDescriptor(PLUGIN_ID, "csv-export_default_22.png"); //$NON-NLS-1$

	/**
	 * Search image.
	 */
	public static final ImageDescriptor IMAGE_SEARCH = getImageDescriptor(PLUGIN_ID, "search_default_22.png"); //$NON-NLS-1$

	/**
	 * Select SKU image (Disabled).
	 */
	public static final ImageDescriptor IMAGE_SEARCH_DISABLED = getImageDescriptor(PLUGIN_ID, "search_default_22.png"); //$NON-NLS-1$

	/**
	 * Row selected image.
	 */
	public static final ImageDescriptor IMAGE_ROW_SELECTED_SMALL = getImageDescriptor(PLUGIN_ID, "state-ticked_default_16.png"); //$NON-NLS-1$
	
	/**
	 * Empty image.
	 */
	public static final ImageDescriptor IMAGE_EMPTY_ICON_SMALL = getImageDescriptor(PLUGIN_ID, "empty_default_16.png"); //$NON-NLS-1$


	private PriceListManagerImageRegistry() {
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
