/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;

/**
 * Provides an image registry for any plugins that require the CMClientCore. Caches the images so that they don't have to be
 * loaded more than once.
 */
public final class CoreImageRegistry extends AbstractImageRegistry {

	private static final String PLUGIN_ID = CorePlugin.PLUGIN_ID;

	/** inline error image. */
	public static final ImageDescriptor IMAGE_ERROR_SMALL = getImageDescriptor(PLUGIN_ID, "error_active_16.png"); //$NON-NLS-1$

	/** inline warning image. */
	public static final ImageDescriptor IMAGE_WARNING_SMALL = getImageDescriptor(PLUGIN_ID, "warning_active_16.png"); //$NON-NLS-1$

	/** Search image. */
	public static final ImageDescriptor IMAGE_SEARCH = getImageDescriptor(PLUGIN_ID, "search_default_22.png"); //$NON-NLS-1$

	/** Search active image. */
	public static final ImageDescriptor IMAGE_SEARCH_ACTIVE = getImageDescriptor(PLUGIN_ID, "search_active_22.png"); //$NON-NLS-1$

	/** Filter image. */
	public static final ImageDescriptor IMAGE_FILTER = getImageDescriptor(PLUGIN_ID, "filter_default_22.png"); //$NON-NLS-1$

	/** resultset first image. */
	public static final ImageDescriptor IMAGE_RESULTSET_FIRST = getImageDescriptor(PLUGIN_ID, "resultset-first_default_22.png"); //$NON-NLS-1$

	/** resultset previous image. */
	public static final ImageDescriptor IMAGE_RESULTSET_PREVIOUS = getImageDescriptor(PLUGIN_ID, "resultset-previous_default_22.png"); //$NON-NLS-1$

	/** resultset next image. */
	public static final ImageDescriptor IMAGE_RESULTSET_NEXT = getImageDescriptor(PLUGIN_ID, "resultset-next_default_22.png"); //$NON-NLS-1$

	/** resultset last image. */
	public static final ImageDescriptor IMAGE_RESULTSET_LAST = getImageDescriptor(PLUGIN_ID, "resultset-last_default_22.png"); //$NON-NLS-1$

	/** Order image. */
	public static final ImageDescriptor IMAGE_ORDER = getImageDescriptor(PLUGIN_ID, "order_default_22.png"); //$NON-NLS-1$

	/** Product image. */
	public static final ImageDescriptor IMAGE_PRODUCT = getImageDescriptor(PLUGIN_ID, "product_default_22.png"); //$NON-NLS-1$

	/** Product SKU image. */
	public static final ImageDescriptor IMAGE_PRODUCT_SKU = getImageDescriptor(PLUGIN_ID, "sku_default_22.png"); //$NON-NLS-1$
	
	/** user image. */
	public static final ImageDescriptor IMAGE_USER = getImageDescriptor(PLUGIN_ID, "user_default_22.png"); //$NON-NLS-1$

	/** user table image. */
	public static final ImageDescriptor IMAGE_USER_SMALL = getImageDescriptor(PLUGIN_ID, "user_default_16.png"); //$NON-NLS-1$

	/** pending user table image. */
	public static final ImageDescriptor IMAGE_USER_PENDING_APPROVAL_SMALL =
			getImageDescriptor(PLUGIN_ID, "user-pending-approval_default_16.png"); //$NON-NLS-1$

	/** disabled user table  image. */
	public static final ImageDescriptor IMAGE_USER_DISABLED_SMALL = getImageDescriptor(PLUGIN_ID, "user-disabled_default_16.png"); //$NON-NLS-1$

	/** role image. */
	public static final ImageDescriptor IMAGE_ROLE = getImageDescriptor(PLUGIN_ID, "user-role_default_22.png"); //$NON-NLS-1$

	/** add image. */
	public static final ImageDescriptor IMAGE_ADD = getImageDescriptor(PLUGIN_ID, "add_default_22.png"); //$NON-NLS-1$

	/** remove image. */
	public static final ImageDescriptor IMAGE_REMOVE = getImageDescriptor(PLUGIN_ID, "delete_default_22.png"); //$NON-NLS-1$

	/** address image. */
	public static final ImageDescriptor IMAGE_ADDRESS = getImageDescriptor(PLUGIN_ID, "address_default_22.png"); //$NON-NLS-1$

	/** edit address image. */
	public static final ImageDescriptor IMAGE_ADDRESS_EDIT = getImageDescriptor(PLUGIN_ID, "edit_default_22.png"); //$NON-NLS-1$

	/** edit image. */
	public static final ImageDescriptor IMAGE_EDIT = getImageDescriptor(PLUGIN_ID, "edit_default_22.png"); //$NON-NLS-1$

	/** move image. */
	public static final ImageDescriptor IMAGE_MOVE = getImageDescriptor(PLUGIN_ID, "move-object_default_22.png"); //$NON-NLS-1$

	/** X image. */
	public static final ImageDescriptor IMAGE_X = getImageDescriptor(PLUGIN_ID, "clear_default_22.png"); //$NON-NLS-1$

	/** Checkmark image. */
	public static final ImageDescriptor IMAGE_TICK = getImageDescriptor(PLUGIN_ID, "tick_default_22.png"); //$NON-NLS-1$

	/** Date image. */
	public static final ImageDescriptor IMAGE_DATE_PICKER = getImageDescriptor(PLUGIN_ID, "date-picker_default_22.png"); //$NON-NLS-1$

	/** Save image. */
	public static final ImageDescriptor IMAGE_SAVE = getImageDescriptor(PLUGIN_ID, "save_default_22.png"); //$NON-NLS-1$

	/** Save image. */
	public static final ImageDescriptor IMAGE_SAVE_LARGE = getImageDescriptor(PLUGIN_ID, "save_default_34.png"); //$NON-NLS-1$

	/** Save image. */
	public static final ImageDescriptor IMAGE_SAVE_ACTIVE_LARGE = getImageDescriptor(PLUGIN_ID, "save_active_34.png"); //$NON-NLS-1$

	/** Save all image. */
	public static final ImageDescriptor IMAGE_SAVE_ALL = getImageDescriptor(PLUGIN_ID, "save-all_default_22.png"); //$NON-NLS-1$

	/** Save all image. */
	public static final ImageDescriptor IMAGE_SAVE_ALL_LARGE = getImageDescriptor(PLUGIN_ID, "save-all_default_34.png"); //$NON-NLS-1$

	/** Save all image. */
	public static final ImageDescriptor IMAGE_SAVE_ALL_ACTIVE_LARGE = getImageDescriptor(PLUGIN_ID, "save-all_active_34.png"); //$NON-NLS-1$

	/** Save as image. */
	public static final ImageDescriptor IMAGE_SAVE_AS = getImageDescriptor(PLUGIN_ID, "save-as_default_22.png"); //$NON-NLS-1$

	/** Inventory allocated image. */
	public static final ImageDescriptor IMAGE_SHIPMENT_RELEASE = getImageDescriptor(PLUGIN_ID, "accept_default_22.png"); //$NON-NLS-1$

	/** Inventory waiting image. */
	public static final ImageDescriptor IMAGE_SHIPMENT_CANCEL = getImageDescriptor(PLUGIN_ID, "delete_default_22.png"); //$NON-NLS-1$

	/** Up arrow image. */
	public static final ImageDescriptor IMAGE_UP_ARROW = getImageDescriptor(PLUGIN_ID, "move-up_default_22.png"); //$NON-NLS-1$

	/** Downward arrow image. */
	public static final ImageDescriptor IMAGE_DOWN_ARROW = getImageDescriptor(PLUGIN_ID, "move-down_default_22.png"); //$NON-NLS-1$

	/** Unlock order image. */
	public static final ImageDescriptor IMAGE_UNLOCK = getImageDescriptor(PLUGIN_ID, "lock-unlocked_default_22.png"); //$NON-NLS-1$

	/** Add note image. */
	public static final ImageDescriptor IMAGE_ADD_NOTE = getImageDescriptor(PLUGIN_ID, "note-add_default_22.png"); //$NON-NLS-1$

	/** Edit note image. */
	public static final ImageDescriptor IMAGE_EDIT_NOTE = getImageDescriptor(PLUGIN_ID, "note-edit_default_22.png"); //$NON-NLS-1$

	/** View all notes image. */
	public static final ImageDescriptor IMAGE_VIEW_ALL_NOTES = getImageDescriptor(PLUGIN_ID, "note-view-all_default_22.png"); //$NON-NLS-1$

	/** Send email image. */
	public static final ImageDescriptor IMAGE_EMAIL_SEND = getImageDescriptor(PLUGIN_ID, "email_default_22.png"); //$NON-NLS-1$

	/** Branding image for LoginDialog. **/
	public static final ImageDescriptor BRANDING_IMAGE = getImageDescriptor(PLUGIN_ID, "login_branding.png"); //$NON-NLS-1$
	
	/** View jobs icon. **/
	public static final ImageDescriptor IMAGE_VIEW_IMPORT_JOBS = getImageDescriptor(PLUGIN_ID, "csv-import_default_22.png"); //$NON-NLS-1$

	/** CSV import icon. **/
	public static final ImageDescriptor IMAGE_CSV_IMPORT = getImageDescriptor(PLUGIN_ID, "csv-import_default_22.png"); //$NON-NLS-1$

	/** CSV export icon. **/
	public static final ImageDescriptor IMAGE_CSV_EXPORT = getImageDescriptor(PLUGIN_ID, "csv-export_default_22.png"); //$NON-NLS-1$

	/** Refresh icon. **/
	public static final ImageDescriptor IMAGE_REFRESH = getImageDescriptor(PLUGIN_ID, "arrow-refresh_default_22.png"); //$NON-NLS-1$

	/** Refresh icon. **/
	public static final ImageDescriptor IMAGE_REFRESH_LARGE = getImageDescriptor(PLUGIN_ID, "arrow-refresh_default_34.png"); //$NON-NLS-1$

	/** Refresh icon. **/
	public static final ImageDescriptor IMAGE_REFRESH_ACTIVE_LARGE = getImageDescriptor(PLUGIN_ID, "arrow-refresh_active_34.png"); //$NON-NLS-1$

	/** Inline edit icon. */
	public static final ImageDescriptor IMAGE_EDIT_CELL_SMALL = getImageDescriptor(PLUGIN_ID, "edit-cell_default_16.png"); //$NON-NLS-1$

	/** Product sku Icon. * */
	public static final ImageDescriptor PRODUCT_SKU = getImageDescriptor(PLUGIN_ID, "sku_default_22.png"); //$NON-NLS-1$

	/** Inline product sku Icon. * */
	public static final ImageDescriptor PRODUCT_SKU_SMALL = getImageDescriptor(PLUGIN_ID, "sku_default_16.png"); //$NON-NLS-1$

	/** Product Icon. * */
	public static final ImageDescriptor PRODUCT = getImageDescriptor(PLUGIN_ID, "product_default_22.png"); //$NON-NLS-1$

	/** Product inline icon. * */
	public static final ImageDescriptor PRODUCT_SMALL = getImageDescriptor(PLUGIN_ID, "product_default_16.png"); //$NON-NLS-1$

	/** product bundle. */
	public static final ImageDescriptor PRODUCT_BUNDLE = getImageDescriptor(PLUGIN_ID, "bundle_default_22.png"); //$NON-NLS-1$

	/** Product bundle inline icon. */
	public static final ImageDescriptor PRODUCT_BUNDLE_SMALL = getImageDescriptor(PLUGIN_ID, "bundle_default_16.png"); //$NON-NLS-1$

	/** Change password icon. */
	public static final ImageDescriptor CHANGE_PASSWORD = getImageDescriptor(PLUGIN_ID, "password-change_default_22.png"); //$NON-NLS-1$
	
	/** Change pagination icon. */
	public static final ImageDescriptor CHANGE_PAGINATION = getImageDescriptor(PLUGIN_ID, "pagination-change_default_22.png"); //$NON-NLS-1$

	/** Open icon. */
	public static final ImageDescriptor IMAGE_OPEN = getImageDescriptor(PLUGIN_ID, "open_default_22.png"); //$NON-NLS-1$

	/** Help contents icon. */
	public static final ImageDescriptor IMAGE_HELP_CONTENTS = getImageDescriptor(PLUGIN_ID, "help_default_22.png"); //$NON-NLS-1$
	
	/** Price list image. */
	public static final ImageDescriptor IMAGE_PRICE_LIST = getImageDescriptor(PLUGIN_ID, "price-list_default_22.png"); //$NON-NLS-1$
	
	/** plus image. */
	public static final ImageDescriptor IMAGE_PLUS = getImageDescriptor(PLUGIN_ID, "add_default_22.png"); //$NON-NLS-1$

	/** State added inline image. */
	public static final ImageDescriptor IMAGE_STATE_ADDED_SMALL = getImageDescriptor(PLUGIN_ID, "state-added_default_16.png"); //$NON-NLS-1$

	/** State added inline image. */
	public static final ImageDescriptor IMAGE_STATE_CHANGED_SMALL = getImageDescriptor(PLUGIN_ID, "state-changed_default_16.png"); //$NON-NLS-1$

	/** State added inline image. */
	public static final ImageDescriptor IMAGE_STATE_DELETED_SMALL = getImageDescriptor(PLUGIN_ID, "state-deleted_default_16.png");
	//$NON-NLS-1$

	/** State added inline image. */
	public static final ImageDescriptor IMAGE_STATE_LOCKED_SMALL = getImageDescriptor(PLUGIN_ID, "state-locked_active_16.png"); //$NON-NLS-1$

	/** State ticked inline image. */
	public static final ImageDescriptor IMAGE_STATE_TICKED_SMALL = getImageDescriptor(PLUGIN_ID, "state-ticked_default_16.png"); //$NON-NLS-1$

	/**
	 * Toolbar Separator image.
	 */
	public static final ImageDescriptor TOOLBAR_SEPARATOR = getImageDescriptor(PLUGIN_ID, "toolbar-separator_default_34.png"); //$NON-NLS-1$

	/**
	 * Perspective selection image.
	 */
	public static final ImageDescriptor PERSPECTIVE_SELECTED = getImageDescriptor(PLUGIN_ID, "toolbar-overlay_selected_34.png"); //$NON-NLS-1$


	/**
	 * Gets the proper image for {@link Product}.
	 * 
	 * @param product {@link Product}. Can't be null.
	 * 
	 * @return {@link Image}.
	 */
	public static Image getImageForProduct(final Product product) {
		if (product instanceof ProductBundle) {
			return getImage(PRODUCT_BUNDLE);
		}
		return getImage(PRODUCT);
	}

	/**
	 * Gets the proper inline image for {@link Product}.
	 *
	 * @param product {@link Product}. Can't be null.
	 *
	 * @return {@link Image}.
	 */
	public static Image getSmallImageForProduct(final Product product) {
		if (product instanceof ProductBundle) {
			return getImage(PRODUCT_BUNDLE_SMALL);
		}
		return getImage(PRODUCT_SMALL);
	}

	private CoreImageRegistry() {
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

	/**
	 * Puts Perspective image behind the given image.
	 *
	 * @param image perspective image to be decorated
	 * @return image indicating selection
	 */
	public static Image createPerspectiveDecoratedImage(final Image image) {
		return new DecorationOverlayIcon(image, PERSPECTIVE_SELECTED, IDecoration.UNDERLAY).createImage();
	}
}
