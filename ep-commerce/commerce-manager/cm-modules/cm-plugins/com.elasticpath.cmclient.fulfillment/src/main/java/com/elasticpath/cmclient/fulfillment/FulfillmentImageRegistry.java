/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.fulfillment;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.core.AbstractImageRegistry;

/**
 * Provides an image registry for any plugins that require the CMClientCore. Caches the images so that they don't have to be
 * loaded more than once.
 */
public final class FulfillmentImageRegistry extends AbstractImageRegistry {

	private static final String PLUGIN_ID = FulfillmentPlugin.PLUGIN_ID;

	/** Return table item image. */
	public static final ImageDescriptor ICON_ORDERTABLE_ITEM = getImageDescriptor(PLUGIN_ID, "product_default_16.png"); //$NON-NLS-1$
	
	/** Return table return qty column edit icon. */
	public static final ImageDescriptor ICON_ORDERTABLE_RETURN_QTY = getImageDescriptor(PLUGIN_ID, "edit-cell_default_16.png"); //$NON-NLS-1$
	
	/** Return table reason column edit icon. */
	public static final ImageDescriptor ICON_ORDERTABLE_REASON = getImageDescriptor(PLUGIN_ID, "edit-cell_default_16.png"); //$NON-NLS-1$

	/** Create Refund Image. */
	public static final ImageDescriptor IMAGE_REFUND_CREATE = getImageDescriptor(PLUGIN_ID, "refund-create_default_22.png"); //$NON-NLS-1$

	/** Return image. */
	public static final ImageDescriptor IMAGE_RETURN = getImageDescriptor(PLUGIN_ID, "return_default_22.png"); //$NON-NLS-1$

	/** Return Create image. */
	public static final ImageDescriptor IMAGE_RETURN_CREATE = getImageDescriptor(PLUGIN_ID, "return-create_default_22.png"); //$NON-NLS-1$

	/** Return Edit image. */
	public static final ImageDescriptor IMAGE_RETURN_EDIT = getImageDescriptor(PLUGIN_ID, "edit_default_22.png"); //$NON-NLS-1$

	/** Return Delete image. */
	public static final ImageDescriptor IMAGE_RETURN_DELETE = getImageDescriptor(PLUGIN_ID, "delete_default_22.png"); //$NON-NLS-1$
	
	/** Return Delete image. */
	public static final ImageDescriptor IMAGE_RETURN_COMPLETE = getImageDescriptor(PLUGIN_ID, "accept_default_22.png"); //$NON-NLS-1$

	/** Create Exchange image. */
	public static final ImageDescriptor IMAGE_EXCHANGE_CREATE = getImageDescriptor(PLUGIN_ID, "exchange-create_default_22.png"); //$NON-NLS-1$

	/** Return Edit image. */
	public static final ImageDescriptor IMAGE_EXCHANGE_CANCEL = getImageDescriptor(PLUGIN_ID, "delete_default_22.png"); //$NON-NLS-1$

	/** Return Delete image. */
	public static final ImageDescriptor IMAGE_EXCHANGE_COMPLETE = getImageDescriptor(PLUGIN_ID, "accept_default_22.png"); //$NON-NLS-1$

	/** View Order Image. */
	public static final ImageDescriptor IMAGE_VIEW_ORDER = getImageDescriptor(PLUGIN_ID, "order_default_22.png"); //$NON-NLS-1$

	/** Create Order Image. */
	public static final ImageDescriptor IMAGE_CREATE_ORDER = getImageDescriptor(PLUGIN_ID, "order-create_default_22.png"); //$NON-NLS-1$

	/** Return Open image. */
	public static final ImageDescriptor IMAGE_EXCHANGE_OPEN_ORDER = getImageDescriptor(PLUGIN_ID, "order_default_22.png"); //$NON-NLS-1$

	/** Customer password change icon. */
	public static final ImageDescriptor ICON_CUSTOMER_EDIT_PASSWORD = getImageDescriptor(PLUGIN_ID, "password-change_default_22.png"); //$NON-NLS-1$

	/** Order Cancel image. */
	public static final ImageDescriptor IMAGE_CANCEL_ORDER = getImageDescriptor(PLUGIN_ID, "delete_default_22.png"); //$NON-NLS-1$

	/** Order Hold image. */
	public static final ImageDescriptor IMAGE_HOLD_ORDER = getImageDescriptor(PLUGIN_ID, "hold_default_22.png"); //$NON-NLS-1$

	/** Order Release image. */
	public static final ImageDescriptor IMAGE_RELEASE_ORDER = getImageDescriptor(PLUGIN_ID, "accept_default_22.png"); //$NON-NLS-1$

	/** Add new credit card icon. */
	public static final ImageDescriptor IMAGE_REAUTH_ADD = getImageDescriptor(PLUGIN_ID, "add_default_22.png"); //$NON-NLS-1$

	/** Gift Certificate icon. */
	public static final ImageDescriptor ICON_GIFT_CERTIFICATE = getImageDescriptor(PLUGIN_ID, "gift-certificate_default_22.png"); //$NON-NLS-1$

	/** Gift Certificate Resend icon. */
	public static final ImageDescriptor ICON_GIFT_CERTIFICATE_RESEND = getImageDescriptor(PLUGIN_ID, "email-send_default_22.png"); //$NON-NLS-1$
	
	/** Lock locked Icon. * */
	public static final ImageDescriptor LOCK_LOCKED = getImageDescriptor(PLUGIN_ID, "lock-locked_default_22.png"); //$NON-NLS-1$

	/** Lock unlocked Icon. * */
	public static final ImageDescriptor LOCK_UNLOCKED = getImageDescriptor(PLUGIN_ID, "lock-unlocked_default_22.png"); //$NON-NLS-1$
	
	/** Email confirmation resend Icon. * */
	public static final ImageDescriptor EMAIL_CONFIRMATION_RESEND = getImageDescriptor(PLUGIN_ID, "email-send_default_22.png"); //$NON-NLS-1$

	 /** Resend RMA Email icon. */
	 public static final ImageDescriptor IMAGE_RESEND_RMA_EMAIL = getImageDescriptor(PLUGIN_ID, "email-send_default_22.png"); //$NON-NLS-1$

	/** Customer segment icon. */
	public static final ImageDescriptor CUSTOMER_SEGMENT_ICON = getImageDescriptor(PLUGIN_ID, "customer-segment_default_22.png"); //$NON-NLS-1$

	private FulfillmentImageRegistry() {
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
