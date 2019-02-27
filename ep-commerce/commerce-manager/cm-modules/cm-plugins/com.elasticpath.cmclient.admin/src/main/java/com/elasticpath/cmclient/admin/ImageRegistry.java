/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

/**
 * Contains an image registry for the plugin, and disposes of all loaded images
 * when the plugin is unloaded.
 */
public final class ImageRegistry {
	
	private ImageRegistry() {
		//util class
	}
	
	// HashMap for storing the images
	private static final Map<ImageDescriptor, Image> IMAGE_MAP = new HashMap<>();
	
	/**
	 * Returns and instance of <code>Image</code> of an <code>ImageDescriptor</code>.
	 * 
	 * @param imageDescriptor the image descriptor
	 * @return instance of an <code>Image</code>
	 */
	public static Image getImage(final ImageDescriptor imageDescriptor) {
		if (ImageRegistry.IMAGE_MAP.containsKey(imageDescriptor)) {
			return ImageRegistry.IMAGE_MAP.get(imageDescriptor);
		}
		final Image image = imageDescriptor.createImage();
		ImageRegistry.IMAGE_MAP.put(imageDescriptor, image);
		return image;
	}
	
	/**
	 * Disposes all the images in the <code>HashMap</code>.
	 * 
	 */
	static void disposeAllImages() {
		for (final ImageDescriptor desc : ImageRegistry.IMAGE_MAP.keySet()) {
			final Image image = ImageRegistry.IMAGE_MAP.get(desc);
			if (!image.isDisposed()) {
				image.dispose();
			}
		}
	}
}
