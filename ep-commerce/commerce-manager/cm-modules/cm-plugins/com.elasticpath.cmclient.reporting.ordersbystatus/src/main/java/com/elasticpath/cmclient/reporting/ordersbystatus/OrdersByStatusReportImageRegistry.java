/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.ordersbystatus;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

/**
 * Provides an image registry for order by status report plugin. 
 * Caches the images so that they don't have to be loaded more than once.
 */
public final class OrdersByStatusReportImageRegistry {

	// HashMap for storing the images
	private static final Map<ImageDescriptor, Image> IMAGES_MAP = new HashMap<ImageDescriptor, Image>();

	/** exclamation image representing an error. */
	public static final ImageDescriptor IMAGE_ERROR = ImageDescriptor.createFromURL(getImageUrl("exclamation.png")); //$NON-NLS-1$
	
	private OrdersByStatusReportImageRegistry() {
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
		IMAGES_MAP.forEach((imageDescriptor, image) -> {
			if (!image.isDisposed()) {
				image.dispose();
			}
		});
	}

	/**
	 * Gets the <code>URL</code> of an image by its name.
	 * 
	 * @param imageName
	 * @return
	 */
	private static URL getImageUrl(final String imageName) {
		return ReportingOrdersByStatusPlugin.getDefault().getBundle().getEntry("/icons/" + imageName); //$NON-NLS-1$
	}
}
