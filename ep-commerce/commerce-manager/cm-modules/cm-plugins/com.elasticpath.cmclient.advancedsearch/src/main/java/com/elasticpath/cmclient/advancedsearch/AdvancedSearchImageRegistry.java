/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.advancedsearch;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.core.AbstractImageRegistry;

/**
 * Provides an image registry for any plugins that require the CMClientCore. Caches the images so that they don't have to be loaded more than once.
 */
public final class AdvancedSearchImageRegistry extends AbstractImageRegistry {

	// HashMap for storing the images
	private static final String PLUGIN_ID = AdvancedSearchPlugin.PLUGIN_ID;

	/**
	 * Search Image.
	 */
	public static final ImageDescriptor SEARCH = getImageDescriptor(PLUGIN_ID, "advanced-search_default_22.png"); //$NON-NLS-1$

	/**
	 * Query Image.
	 */
	public static final ImageDescriptor QUERY = getImageDescriptor(PLUGIN_ID, "query_default_22.png"); //$NON-NLS-1$

	/**
	 * Query Builder Image.
	 */
	public static final ImageDescriptor QUERY_BUILDER = getImageDescriptor(PLUGIN_ID, "query-builder_default_22.png"); //$NON-NLS-1$

	/**
	 * Query Open Image.
	 */
	public static final ImageDescriptor QUERY_OPEN = getImageDescriptor(PLUGIN_ID, "query_default_22.png"); //$NON-NLS-1$

	/**
	 * Query Add Image.
	 */
	public static final ImageDescriptor QUERY_ADD = getImageDescriptor(PLUGIN_ID, "query-add_default_22.png"); //$NON-NLS-1$

	/**
	 * Query Delete Image.
	 */
	public static final ImageDescriptor QUERY_DELETE = getImageDescriptor(PLUGIN_ID, "query-delete_default_22.png"); //$NON-NLS-1$

	/**
	 * Query Edit Image.
	 */
	public static final ImageDescriptor QUERY_EDIT = getImageDescriptor(PLUGIN_ID, "query-edit_default_22.png"); //$NON-NLS-1$

	/**
	 * Query error icon.
	 */
	public static final ImageDescriptor IMAGE_QUERY_ERROR_SMALL = getImageDescriptor(PLUGIN_ID, "error_active_16.png"); //$NON-NLS-1$

	/**
	 * Query refresh icon.
	 */
	public static final ImageDescriptor IMAGE_QUERY_VALIDATE = getImageDescriptor(PLUGIN_ID, "tick_default_22.png"); //$NON-NLS-1$

	/**
	 * Checkmark icon.
	 */
	public static final ImageDescriptor IMAGE_CHECKMARK = getImageDescriptor(PLUGIN_ID, "tick_default_22.png"); //$NON-NLS-1$

	/**
	 * Run query icon.
	 */
	public static final ImageDescriptor IMAGE_QUERY_RUN = getImageDescriptor(PLUGIN_ID, "query-run_default_22.png"); //$NON-NLS-1$

	private AdvancedSearchImageRegistry() {
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
