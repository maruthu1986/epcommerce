/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.shipping;

import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.BundleContext;

import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.AbstractEpUIPlugin;

/**
 * The activator class controls the plug-in life cycle.
 */
public class AdminShippingPlugin extends AbstractEpUIPlugin {

	/**
	 * The plug-in ID.
	 */
	public static final String PLUGIN_ID = "com.elasticpath.cmclient.admin.shipping"; //$NON-NLS-1$

	@Override
	public void stop(final BundleContext context) throws Exception {
		try {
			AdminShippingImageRegistry.disposeAllImages();
		} catch (ExceptionInInitializerError | IllegalStateException e) {
			// Do nothing.
		}
		super.stop(context);
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path.
	 * 
	 * @param path relative to the plug-in root path to the image.
	 * @return the image descriptor.
	 */
	public static ImageDescriptor getImageDescriptor(final String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	/**
	 * Determines whether the current user is authorized to use this plugin.
	 * 
	 * @return true if yes, else false.
	 */
	public static boolean isAuthorized() {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(AdminShippingPermissions.ADMIN_SHIPPING_MANAGE);
	}


	@Override
	protected void loadLocalizedMessages() {
		AdminShippingMessages.get();
	}
}
