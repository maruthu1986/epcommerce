/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.targetedselling;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.core.AbstractImageRegistry;
import com.elasticpath.cmclient.store.StorePlugin;

/**
 * Declares keys that can be used to retrieve images. Frequently used images
 * should be registered in the Activator class (<code>PromotionsPlugin</code>).
 */
public final class TargetedSellingImageRegistry extends AbstractImageRegistry {

	private static final String CONDITION_PNG = "condition.png"; //$NON-NLS-1$

	private static final String DYNAMIC_CONTENT_PNG = "dynamic_content.png"; //$NON-NLS-1$

	private static final String PLUGIN_ID = StorePlugin.PLUGIN_ID;

	// Tabs images. --------------------------------------

	/**
	 * Search tab icon.
	 */
	public static final ImageDescriptor IMAGE_DYNAMIC_CONTENT_TAB = getImageDescriptor(PLUGIN_ID, DYNAMIC_CONTENT_PNG);

	/**
	 * Dynamic content assignment icon.
	 */
	public static final ImageDescriptor IMAGE_DYNAMIC_CONTENT_DELIVERY_TAB =
		getImageDescriptor(PLUGIN_ID, "dynamic_content_delivery.png"); //$NON-NLS-1$

	/**
	 * Dynamic content assignment icon for search result list.
	 */
	public static final ImageDescriptor IMAGE_DYNAMIC_CONTENT_DELIVERY_LIST =
		getImageDescriptor(PLUGIN_ID, "dynamic_content_delivery.png"); //$NON-NLS-1$

	/**
	 * Dynamic content assignment icon for search result list.
	 */
	public static final ImageDescriptor IMAGE_DYNAMIC_CONTENT_LIST = getImageDescriptor(PLUGIN_ID, DYNAMIC_CONTENT_PNG);


	// Action images. --------------------------------------
	/**
	 * Dynamic content create action.
	 */
	public static final ImageDescriptor IMAGE_DYNAMIC_CONTENT_CREATE_ACTION =
		getImageDescriptor(PLUGIN_ID, "dynamic_content_add.png"); //$NON-NLS-1$

	/**
	 * Dynamic content delete action.
	 */
	public static final ImageDescriptor IMAGE_DYNAMIC_CONTENT_DELETE_ACTION =
		getImageDescriptor(PLUGIN_ID, "dynamic_content_delete.png"); //$NON-NLS-1$

	/**
	 * Dynamic content edit action.
	 */
	public static final ImageDescriptor IMAGE_DYNAMIC_CONTENT_EDIT_ACTION =
		getImageDescriptor(PLUGIN_ID, "dynamic_content_edit.png"); //$NON-NLS-1$
	/** Action images. */

	/**
	 * Dynamic content assignment create action.
	 */
	public static final ImageDescriptor IMAGE_DYNAMIC_CONTENT_DELIVERY_CREATE_ACTION =
		getImageDescriptor(PLUGIN_ID, "dynamic_content_delivery_add.png"); //$NON-NLS-1$
	/**
	 * Dynamic content assignment delete action.
	 */
	public static final ImageDescriptor IMAGE_DYNAMIC_CONTENT_DELIVERY_DELETE_ACTION =
		getImageDescriptor(PLUGIN_ID, "dynamic_content_delivery_delete.png"); //$NON-NLS-1$
	/**
	 * Dynamic content assignment edit action.
	 */
	public static final ImageDescriptor IMAGE_DYNAMIC_CONTENT_DELIVERY_EDIT_ACTION =
		getImageDescriptor(PLUGIN_ID, "dynamic_content_delivery_edit.png"); //$NON-NLS-1$

	/**
	 * Row selected image.
	 */
	public static final ImageDescriptor IMAGE_ROW_SELECTED = getImageDescriptor(PLUGIN_ID, "tick.png"); //$NON-NLS-1$

	/**
	 * Empty image.
	 */
	public static final ImageDescriptor IMAGE_EMPTY_ICON = getImageDescriptor(PLUGIN_ID, "empty.gif"); //$NON-NLS-1$

	// Tags expression images
	/**
	 * Search tab icon.
	 */
	public static final ImageDescriptor IMAGE_CONDITIONAL_EXPRESSION_TAB = getImageDescriptor(PLUGIN_ID, CONDITION_PNG);

	/**
	 * Search result tab icon.
	 */
	public static final ImageDescriptor IMAGE_CONDITIONAL_EXPRESSION_LIST = getImageDescriptor(PLUGIN_ID, CONDITION_PNG);

	// Action images. --------------------------------------
	/**
	 * Conditional expression create action.
	 */
	public static final ImageDescriptor IMAGE_CONDITIONAL_EXPRESSION_CREATE_ACTION =
		getImageDescriptor(PLUGIN_ID, "condition_add.png"); //$NON-NLS-1$

	/**
	 * Conditional expression delete action.
	 */
	public static final ImageDescriptor IMAGE_CONDITIONAL_EXPRESSION_DELETE_ACTION =
		getImageDescriptor(PLUGIN_ID, "condition_delete.png"); //$NON-NLS-1$

	/**
	 * Conditional expression edit action.
	 */
	public static final ImageDescriptor IMAGE_CONDITIONAL_EXPRESSION_EDIT_ACTION =
		getImageDescriptor(PLUGIN_ID, "condition_edit.png"); //$NON-NLS-1$

	private TargetedSellingImageRegistry() {
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
