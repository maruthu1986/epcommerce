/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.configuration;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.core.AbstractImageRegistry;

/**
 * Provides an image registry for the AdminUsers plugin.
 * Caches the images so that they don't have to be loaded more than once.
 */
public final class AdminConfigurationImageRegistry extends AbstractImageRegistry {

	// HashMap for storing the images
	private static final String PLUGIN_ID = AdminConfigurationPlugin.PLUGIN_ID;

	/**
	 * Attribute image.
	 */
	public static final ImageDescriptor IMAGE_SYSTEM_PROPERTIES = getImageDescriptor(PLUGIN_ID, "system-configuration_default_22.png"); //$NON-NLS-1$
	/**
	 * Search Indexes image.
	 */
	public static final ImageDescriptor IMAGE_SEARCH_INDEXES = getImageDescriptor(PLUGIN_ID, "search-indexes_default_22.png"); //$NON-NLS-1$
	/**
	 * Search Index rebuild image.
	 */
	public static final ImageDescriptor IMAGE_SEARCH_INDEX_REBUILD =
			getImageDescriptor(PLUGIN_ID, "search-index-rebuild_default_22.png"); //$NON-NLS-1$
	/**
	 * Attribute image.
	 */
	public static final ImageDescriptor CONFIGURATION_VALUE_ADD = getImageDescriptor(PLUGIN_ID, "add_default_22.png");  //$NON-NLS-1$
	/**
	 * Attribute image.
	 */
	public static final ImageDescriptor CONFIGURATION_VALUE_DELETE = getImageDescriptor(PLUGIN_ID, "delete_default_22.png"); //$NON-NLS-1$
	/**
	 * Attribute image.
	 */
	public static final ImageDescriptor CONFIGURATION_VALUE_EDIT = getImageDescriptor(PLUGIN_ID, "edit_default_22.png"); //$NON-NLS-1$

	private AdminConfigurationImageRegistry() {
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
