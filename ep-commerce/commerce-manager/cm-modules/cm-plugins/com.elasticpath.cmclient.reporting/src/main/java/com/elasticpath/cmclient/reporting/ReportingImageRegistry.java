/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting;

import com.elasticpath.cmclient.core.AbstractImageRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

/**
 * Contains an image registry for the plugin, and disposes of all loaded images
 * when the plugin is unloaded.
 */
public final class ReportingImageRegistry extends AbstractImageRegistry {

	private static final String PLUGIN_ID = ReportingPlugin.PLUGIN_ID;

	/** RunReport Icon. */
	public static final ImageDescriptor RUN_REPORT = getImageDescriptor(PLUGIN_ID, "run_default_22.png"); //$NON-NLS-1$

	private ReportingImageRegistry() {
		//util class
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
