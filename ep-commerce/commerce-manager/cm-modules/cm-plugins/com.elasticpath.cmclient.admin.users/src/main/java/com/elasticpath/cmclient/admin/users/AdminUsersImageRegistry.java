/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.users;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.core.AbstractImageRegistry;

/**
 * Provides an image registry for the AdminUsers plugin.
 * Caches the images so that they don't have to be loaded more than once.
 */
public final class AdminUsersImageRegistry extends AbstractImageRegistry {

	private static final String PLUGIN_ID = AdminUsersPlugin.PLUGIN_ID;

	/**
	 * User image.
	 */
	public static final ImageDescriptor IMAGE_USER = getImageDescriptor(PLUGIN_ID, "user_default_22.png"); //$NON-NLS-1$
	/**
	 * User create image.
	 */
	public static final ImageDescriptor IMAGE_USER_CREATE = getImageDescriptor(PLUGIN_ID, "add_default_22.png"); //$NON-NLS-1$
	/**
	 * User edit image.
	 */
	public static final ImageDescriptor IMAGE_USER_EDIT = getImageDescriptor(PLUGIN_ID, "edit_default_22.png"); //$NON-NLS-1$
	/**
	 * User delete image.
	 */
	public static final ImageDescriptor IMAGE_USER_DELETE = getImageDescriptor(PLUGIN_ID, "delete_default_22.png"); //$NON-NLS-1$
	/**
	 * Role image.
	 */
	public static final ImageDescriptor IMAGE_ROLE = getImageDescriptor(PLUGIN_ID, "user-role_default_22.png"); //$NON-NLS-1$
	/**
	 * Role create image.
	 */
	public static final ImageDescriptor IMAGE_ROLE_CREATE = getImageDescriptor(PLUGIN_ID, "add_default_22.png"); //$NON-NLS-1$
	/**
	 * Role edit image.
	 */
	public static final ImageDescriptor IMAGE_ROLE_EDIT = getImageDescriptor(PLUGIN_ID, "edit_default_22.png"); //$NON-NLS-1$
	/**
	 * Role delete image.
	 */
	public static final ImageDescriptor IMAGE_ROLE_DELETE = getImageDescriptor(PLUGIN_ID, "delete_default_22.png"); //$NON-NLS-1$
	/**
	 * Change password icon.
	 */
	public static final ImageDescriptor IMAGE_CHANGE_USER_PASSWORD = getImageDescriptor(PLUGIN_ID, "password-change_default_22.png"); //$NON-NLS-1$

	private AdminUsersImageRegistry() {
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
