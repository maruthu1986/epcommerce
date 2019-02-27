/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.promotions;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.core.AbstractImageRegistry;
import com.elasticpath.cmclient.store.StorePlugin;

/**
 * Declares keys that can be used to retrieve images.
 * Frequently used images should be registered in the Activator class (<code>PromotionsPlugin</code>).
 */
public final class PromotionsImageRegistry extends AbstractImageRegistry {

	private static final String PLUGIN_ID = StorePlugin.PLUGIN_ID;

	/**
	 * Catalog Promotion image.
	 */
	public static final ImageDescriptor PROMOTION_CATALOG = getImageDescriptor(PLUGIN_ID, "promotion_default_22.png"); //$NON-NLS-1$

	/**
	 * Catalog Promotion image for tables.
	 */
	public static final ImageDescriptor PROMOTION_CATALOG_SMALL = getImageDescriptor(PLUGIN_ID, "promotion-catalog_default_16.png"); //$NON-NLS-1$

	/**
	 * Catalog Promotion image.
	 */
	public static final ImageDescriptor PROMOTION_CATALOG_CREATE =
			getImageDescriptor(PLUGIN_ID, "promotion-catalog-create_default_22.png"); //$NON-NLS-1$

	/**
	 * Shopping Promotion image.
	 */
	public static final ImageDescriptor PROMOTION_SHOPPING_CART = getImageDescriptor(PLUGIN_ID, "promotion-cart_default_22.png"); //$NON-NLS-1$

	/**
	 * Shopping Promotion image for tables.
	 */
	public static final ImageDescriptor PROMOTION_SHOPPING_CART_SMALL = getImageDescriptor(PLUGIN_ID, "promotion-cart_default_16.png"); //$NON-NLS-1$

	/**
	 * Shopping promotion create image.
	 */
	public static final ImageDescriptor PROMOTION_SHOPPING_CREATE =
			getImageDescriptor(PLUGIN_ID, "promotion-cart-create_default_22.png"); //$NON-NLS-1$

	/**
	 * Promotion image.
	 */
	public static final ImageDescriptor PROMOTION_EDIT = getImageDescriptor(PLUGIN_ID, "promotion_default_22.png"); //$NON-NLS-1$

	/**
	 * Promotion image.
	 */
	public static final ImageDescriptor PROMOTION_DELETE = getImageDescriptor(PLUGIN_ID, "promotion-delete_default_22.png"); //$NON-NLS-1$

	/**
	 * Promotion image.
	 */
	public static final ImageDescriptor COUPON_CODES_MANAGE = getImageDescriptor(PLUGIN_ID, "coupon-codes-manage_default_22.png"); //$NON-NLS-1$

	private PromotionsImageRegistry() {
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
