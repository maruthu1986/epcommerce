/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.taxes;


import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.core.AbstractImageRegistry;

/**
 * Provides an image registry for the Taxes plug-in. Caches the images so that they don't have to be loaded more than once.
 */
public final class TaxesImageRegistry extends AbstractImageRegistry {

	private static final String PLUGIN_ID = TaxesPlugin.PLUGIN_ID;

	/**
	 * Filter icon.
	 */
	public static final ImageDescriptor IMAGE_FILTER = getImageDescriptor(PLUGIN_ID, "filter_default_22.png"); //$NON-NLS-1$

	/**
	 * Tax code image.
	 */
	public static final ImageDescriptor IMAGE_TAX_CODE = getImageDescriptor(PLUGIN_ID, "tax-code_default_22.png"); //$NON-NLS-1$

	/**
	 * Tax code create image.
	 */
	public static final ImageDescriptor IMAGE_TAX_CODE_CREATE = getImageDescriptor(PLUGIN_ID, "add_default_22.png"); //$NON-NLS-1$

	/**
	 * Tax code edit image.
	 */
	public static final ImageDescriptor IMAGE_TAX_CODE_EDIT = getImageDescriptor(PLUGIN_ID, "edit_default_22.png"); //$NON-NLS-1$

	/**
	 * Tax code delete image.
	 */
	public static final ImageDescriptor IMAGE_TAX_CODE_DELETE = getImageDescriptor(PLUGIN_ID, "delete_default_22.png"); //$NON-NLS-1$

	/**
	 * Section item images image.
	 */
	public static final ImageDescriptor IMAGE_TAX_JURISDICTION_ADMIN_SECTION_ITEM =
		getImageDescriptor(PLUGIN_ID, "tax-jurisdiction_default_22.png"); //$NON-NLS-1$

	/**
	 * TaxJurisdiction Create Image.
	 */
	public static final ImageDescriptor IMAGE_TAX_JURISDICTION_CREATE = getImageDescriptor(PLUGIN_ID, "add_default_22.png"); //$NON-NLS-1$

	/**
	 * TaxJurisdiction Edit Image.
	 */
	public static final ImageDescriptor IMAGE_TAX_JURISDICTION_EDIT = getImageDescriptor(PLUGIN_ID, "edit_default_22.png"); //$NON-NLS-1$

	/**
	 * TaxJurisdiction Delete Image.
	 */
	public static final ImageDescriptor IMAGE_TAX_JURISDICTION_DELETE = getImageDescriptor(PLUGIN_ID, "delete_default_22.png"); //$NON-NLS-1$

	/**
	 * StoreListView column Image.
	 */
	public static final ImageDescriptor IMAGE_TAX_JURISDICTION_SMALL = getImageDescriptor(PLUGIN_ID, "tax-jurisdiction_default_22.png"); //$NON-NLS-1$

	/**
	 * Tax value Image.
	 */
	public static final ImageDescriptor IMAGE_TAX_VALUE = getImageDescriptor(PLUGIN_ID, "tax-value_default_22.png"); //$NON-NLS-1$

	/**
	 * Tax value Create Image.
	 */
	public static final ImageDescriptor IMAGE_TAX_VALUE_CREATE = getImageDescriptor(PLUGIN_ID, "add_default_22.png"); //$NON-NLS-1$

	/**
	 * Tax value Edit Image.
	 */
	public static final ImageDescriptor IMAGE_TAX_VALUE_EDIT = getImageDescriptor(PLUGIN_ID, "edit_default_22.png"); //$NON-NLS-1$

	/**
	 * Tax value Delete Image.
	 */
	public static final ImageDescriptor IMAGE_TAX_VALUE_DELETE = getImageDescriptor(PLUGIN_ID, "delete_default_22.png"); //$NON-NLS-1$

	private TaxesImageRegistry() {
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
