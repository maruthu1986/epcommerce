/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.cmclient.admin.datapolicies;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.core.AbstractImageRegistry;

/**
 * The data policy functionality's image registry.
 */
public final class AdminDataPoliciesImageRegistry extends AbstractImageRegistry {

	private static final String PLUGIN_ID = AdminDataPoliciesPlugin.PLUGIN_ID;

	/**
	 * Data Policy image.
	 */
	public static final ImageDescriptor IMAGE_DATA_POLICIES = getImageDescriptor(PLUGIN_ID, "data-privacy_default_22.png"); //$NON-NLS-1$
	/**
	 * Data Policy create image.
	 */
	public static final ImageDescriptor IMAGE_DATA_POLICY_CREATE = getImageDescriptor(PLUGIN_ID, "add_default_22.png"); //$NON-NLS-1$
	/**
	 * Data Policy open image.
	 */
	public static final ImageDescriptor IMAGE_DATA_POLICY_OPEN = getImageDescriptor(PLUGIN_ID, "open_default_22.png"); //$NON-NLS-1$


	/**
	 * Data Policy edit image.
	 */
	public static final ImageDescriptor IMAGE_DATA_POLICY_EDIT = getImageDescriptor(PLUGIN_ID, "edit_default_22.png"); //$NON-NLS-1$
	/**
	 * Data Policy disable image.
	 */
	public static final ImageDescriptor IMAGE_DATA_POLICY_DISABLE = getImageDescriptor(PLUGIN_ID, "delete_default_22.png"); //$NON-NLS-1$

	private AdminDataPoliciesImageRegistry() {
		// utility class
	}

	/**
	 * Returns an instance of <code>Image</code> of an <code>ImageDescriptor</code>.
	 *
	 * @param imageDescriptor the image descriptor
	 * @return instance of an <code>Image</code>
	 */
	public static Image getImage(final ImageDescriptor imageDescriptor) {
		return getImage(imageDescriptor, PLUGIN_ID);
	}
}
