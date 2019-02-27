/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */

package com.elasticpath.cmclient.reporting.ordersummary;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Provides an image registry for the AdminUsers plugin. 
 * Caches the images so that they don't have to be loaded more than once.
 */
public final class OrderSummaryReportImageRegistry {

	// HashMap for storing the images
	private static final Map<ImageDescriptor, Image> IMAGES_MAP = new HashMap<ImageDescriptor, Image>();

	/** exclamation image representing an error. */
	public static final ImageDescriptor IMAGE_ERROR = getImageDescriptor("exclamation.png"); //$NON-NLS-1$
	
	private OrderSummaryReportImageRegistry() {
		// utility class
	}

	/**
	 * Returns and instance of <code>Image</code> of an <code>ImageDescriptor</code>.
	 * 
	 * @param imageDescriptor the image descriptor
	 * @return instance of an <code>Image</code>
	 */
	public static Image getImage(final ImageDescriptor imageDescriptor) {
		if (imageDescriptor == null) {
			return null;
		}
		if (IMAGES_MAP.containsKey(imageDescriptor)) {
			return IMAGES_MAP.get(imageDescriptor);
		}
		final Image image = imageDescriptor.createImage();
		IMAGES_MAP.put(imageDescriptor, image);
		return image;
	}

	/**
	 * Disposes all the images in the <code>HashMap</code>. Should be called by the Plugin's stop method.
	 */
	static void disposeAllImages() {
		for (final ImageDescriptor desc : IMAGES_MAP.keySet()) {
			final Image image = IMAGES_MAP.get(desc);
			if (!image.isDisposed()) {
				image.dispose();
			}
		}
	}

	/**
	 * Gets the <code>ImageDescriptor</code> of an image by its name.
	 *
	 * @param imageName
	 * @return
	 */
	private static ImageDescriptor getImageDescriptor(final String imageName) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(OrderSummaryReportPlugin.PLUGIN_ID, "/icons/" + imageName); //$NON-NLS-1$
	}
}
