/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.changeset;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.core.AbstractImageRegistry;

/**
 * Provides an image registry for the com.elasticpath.cmclient.changeset plugin.
 * Caches the images so that they don't have to be loaded more than once.
 */
public final class ChangeSetImageRegistry extends AbstractImageRegistry {

	private static final String PLUGIN_ID = ChangeSetPlugin.PLUGIN_ID;

	/**
	 * Change Set image.
	 */
	public static final ImageDescriptor CHANGESET = getImageDescriptor(PLUGIN_ID, "changeset_default_22.png"); //$NON-NLS-1$

	/**
	 * Add Change Set image.
	 */
	public static final ImageDescriptor CHANGESET_ADD = getImageDescriptor(PLUGIN_ID, "changeset-create_default_22.png"); //$NON-NLS-1$

	/**
	 * Delete Change Set image.
	 */
	public static final ImageDescriptor CHANGESET_DELETE = getImageDescriptor(PLUGIN_ID, "changeset-delete_default_22.png"); //$NON-NLS-1$

	/**
	 * Change set lock for overlay.
	 */
	public static final ImageDescriptor CHANGESET_LOCK_DECORATOR =
			getImageDescriptor(PLUGIN_ID, "state-locked-decorator_active_22.png"); //$NON-NLS-1$

	/**
	 * Change set add object.
	 * */
	public static final ImageDescriptor CHANGESET_ADD_OBJECT_LARGE =
			getImageDescriptor(PLUGIN_ID, "changeset-add-object_default_34.png"); //$NON-NLS-1$

	/**
	 * Change set object added.
	 * */
	public static final ImageDescriptor CHANGESET_OBJECT_ADDED_LARGE =
			getImageDescriptor(PLUGIN_ID, "changeset-object-added_selected_34.png"); //$NON-NLS-1$

	/**
	 * Change set object added disabled.
	 * */
	public static final ImageDescriptor CHANGESET_OBJECT_ADDED_DISABLED_LARGE =
			getImageDescriptor(PLUGIN_ID, "changeset-object-added_selected_disabled_34.png"); //$NON-NLS-1$

	/**
	 * Right arrow image.
	 */
	public static final ImageDescriptor CHANGESET_MOVE_OBJECT = getImageDescriptor(PLUGIN_ID, "move-object_default_22.png"); //$NON-NLS-1$

	private ChangeSetImageRegistry() {
		// utility class
	}

	/**
	 * Decorate an image by adding an overlay image.
	 *
	 * @param baseImage    is the base image
	 * @param overlayImage is the image to overlay
	 * @return the composite image
	 */
	public static Image decorateImage(final Image baseImage, final ImageDescriptor overlayImage) {
		return new DecorationOverlayIcon(baseImage, overlayImage, IDecoration.BOTTOM_LEFT).createImage();
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
