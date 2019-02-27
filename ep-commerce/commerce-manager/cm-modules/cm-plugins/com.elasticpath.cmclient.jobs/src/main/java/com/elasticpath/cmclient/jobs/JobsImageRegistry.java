/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.jobs;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.core.AbstractImageRegistry;

/**
 * Provides an image registry for any plugins that require the CMClientCore. Caches the images so that they don't have to be loaded more than once.
 */
public final class JobsImageRegistry extends AbstractImageRegistry {

	private static final String PLUGIN_ID = JobsPlugin.PLUGIN_ID;

	/** Job Icon. * */
	public static final ImageDescriptor JOB = getImageDescriptor(PLUGIN_ID, "csv-import_default_22.png"); //$NON-NLS-1$

	/** Job create Icon. * */
	public static final ImageDescriptor JOB_CREATE = getImageDescriptor(PLUGIN_ID, "csv-import-create_default_22.png"); //$NON-NLS-1$
	
	/** Job search .csv file Icon. * */
	public static final ImageDescriptor JOB_SEARCH_FILE = getImageDescriptor(PLUGIN_ID, "search_default_22.png"); //$NON-NLS-1$

	/** Job delete Icon. * */
	public static final ImageDescriptor JOB_DELETE = getImageDescriptor(PLUGIN_ID, "csv-import-delete_default_22.png"); //$NON-NLS-1$

	/** Job edit Icon. * */
	public static final ImageDescriptor JOB_EDIT = getImageDescriptor(PLUGIN_ID, "csv-import-edit_default_22.png"); //$NON-NLS-1$

	/** Job run Icon. * */
	public static final ImageDescriptor JOB_RUN = getImageDescriptor(PLUGIN_ID, "csv-import-run_default_22.png"); //$NON-NLS-1$

	/** Job run Icon. * */
	public static final ImageDescriptor JOB_IMPORT_ERROR_SMALL = getImageDescriptor(PLUGIN_ID, "error_active_16.png"); //$NON-NLS-1$

	/** Job run Icon. * */
	public static final ImageDescriptor JOB_IMPORT_DONE = getImageDescriptor(PLUGIN_ID, "tick_default_22.png"); //$NON-NLS-1$

	private JobsImageRegistry() {
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
