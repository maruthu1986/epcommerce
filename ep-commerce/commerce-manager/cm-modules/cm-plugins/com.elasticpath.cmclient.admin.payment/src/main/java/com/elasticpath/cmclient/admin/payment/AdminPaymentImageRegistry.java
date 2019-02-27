/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.payment;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.core.AbstractImageRegistry;

/**
 * Provides an image registry for the AdminPayment plugin. Caches the images so that they don't have to be loaded more than once.
 */
public final class AdminPaymentImageRegistry extends AbstractImageRegistry {

	private static final String PLUGIN_ID = AdminPaymentPlugin.PLUGIN_ID;

	/**
	 * Payment Gateways image.
	 */
	public static final ImageDescriptor IMAGE_PAYMENT_GATEWAY = getImageDescriptor(PLUGIN_ID, "payment-gateway_default_22.png"); //$NON-NLS-1$

	/**
	 * Payment Gateway create image.
	 */
	public static final ImageDescriptor IMAGE_PAYMENT_GATEWAY_CREATE = getImageDescriptor(PLUGIN_ID, "add_default_22.png"); //$NON-NLS-1$

	/**
	 * Payment Gateway edit image.
	 */
	public static final ImageDescriptor IMAGE_PAYMENT_GATEWAY_EDIT = getImageDescriptor(PLUGIN_ID, "edit_default_22.png"); //$NON-NLS-1$

	/**
	 * Payment Gateway delete image.
	 */
	public static final ImageDescriptor IMAGE_PAYMENT_GATEWAY_DELETE = getImageDescriptor(PLUGIN_ID, "delete_default_22.png"); //$NON-NLS-1$

	private AdminPaymentImageRegistry() {
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
