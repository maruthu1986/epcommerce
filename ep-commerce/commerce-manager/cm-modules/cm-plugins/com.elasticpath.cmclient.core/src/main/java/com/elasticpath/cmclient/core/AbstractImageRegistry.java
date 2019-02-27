/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Base image registry for others, plugin specific image registries.
 * This class contains common implementation for ImageRegistry utility classes.
 * Caches the images so that they don't have to be loaded more than once.
 */
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
public abstract class AbstractImageRegistry {

	private static final Logger LOG = Logger.getLogger(AbstractImageRegistry.class);

	/**
	 * String:	pluginId.
	 * Map:		map containing images for specific plugin
	 */
	private static final Map<String, Map<ImageDescriptor, Image>> IMAGES_MAP = new HashMap<>();

	/**
	 * Returns and instance of <code>Image</code> of an <code>ImageDescriptor</code>.
	 *
	 * @param imageDescriptor the image descriptor
	 * @param pluginId plugin id
	 * @return instance of an <code>Image</code>
	 */
	protected static Image getImage(final ImageDescriptor imageDescriptor, final String pluginId) {
		Map<ImageDescriptor, Image> imagesMap = getMap(pluginId);

		return imagesMap.get(imageDescriptor);
	}

	/**
	 * Disposes all the images in the <code>HashMap</code>. Should be called by the Plugin's stop method.
	 * @param pluginId plugin id
	 */
	protected static void disposeAllImages(final String pluginId) {
		Map<ImageDescriptor, Image> imagesMap = getMap(pluginId);

		for (Map.Entry<ImageDescriptor, Image> entry : imagesMap.entrySet()) {
			final Image image = entry.getValue();
			image.dispose();
		}
		IMAGES_MAP.remove(pluginId);
	}

	/**
	 * Gets the <code>ImageDescriptor</code> of an image by its name.
	 *
	 * @param pluginId name of the plugin
	 * @param imageName name of the image
	 * @return image descriptor
	 */
	protected static ImageDescriptor getImageDescriptor(final String pluginId, final String imageName) {
		ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(pluginId, "/icons/" + imageName); //$NON-NLS-1$
		putImageInPluginMap(imageDescriptor, pluginId);

		if (imageDescriptor == null) {
			LOG.warn("Image not found: " + pluginId + " " + imageName); //$NON-NLS-1$
		}

		return imageDescriptor;
	}

	/**
	 * Recreates image in case that image's device (display) is disposed, which happens when session expires.
	 *
	 * @param imageDescriptor the image descriptor
	 * @param pluginId the plugin id
	 * @return a new image, created from its descriptor
	 */
	protected static Image reCreateImage(final ImageDescriptor imageDescriptor, final String pluginId) {
		Map<ImageDescriptor, Image> imagesMap = getMap(pluginId);

		imagesMap.remove(imageDescriptor);
		createImage(imagesMap, imageDescriptor);

		return imagesMap.get(imageDescriptor);
	}

	private static Map<ImageDescriptor, Image> getMap(final String pluginId) {
		IMAGES_MAP.putIfAbsent(pluginId, new HashMap<>());

		return IMAGES_MAP.get(pluginId);
	}

	/**
	 * Puts images in the map specific for the plugin at compilation time.
	 *
	 * @param imageDescriptor image descriptor
	 * @param pluginId        plugin id
	 */
	private static void putImageInPluginMap(final ImageDescriptor imageDescriptor, final String pluginId) {
		if (imageDescriptor == null) {
			return;
		}

		Map<ImageDescriptor, Image> imagesMap = getMap(pluginId);
		createImage(imagesMap, imageDescriptor);
	}

	private static void createImage(final Map<ImageDescriptor, Image> imagesMap, final ImageDescriptor imageDescriptor) {
		Image image = imageDescriptor.createImage();
		imagesMap.put(imageDescriptor, image);
	}

}
