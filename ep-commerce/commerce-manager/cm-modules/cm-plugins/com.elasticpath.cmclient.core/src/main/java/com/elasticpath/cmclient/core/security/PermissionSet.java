/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.security;

import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;

/**
 * Defines a Set of Permissions, and knows how to read the Permissions information from
 * the authorization extension point.
 */
public class PermissionSet extends TreeSet<String> {
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(PermissionSet.class);
	
	/** The extension's permissions element. */
	public static final String TAG_PERMISSIONS = "permissions"; //$NON-NLS-1$
	/** The extension's permission element. */
	public static final String TAG_PERMISSION = "permission"; //$NON-NLS-1$
	/** The extension's key element. */
	public static final String ATT_KEY = "key"; //$NON-NLS-1$
	
	
	/**
	 * Parses the Authorizations extension's xml elements and returns a collection of permissions
	 * that are defined in the extension.
	 * @param permissionsElement The permissionsElement contained within the extension definition
	 * @return a Set of permission SecureId strings that are defined within the extension
	 */
	public PermissionSet parseConfigElement(final IConfigurationElement permissionsElement) {
		if (!permissionsElement.getName().equals(TAG_PERMISSIONS)) {
			return null;
		}
		try {
			for (IConfigurationElement permissionElement : permissionsElement.getChildren(TAG_PERMISSION)) {
				String permission = permissionElement.getAttribute(ATT_KEY);
				this.add(permission);
			}
		} catch (Exception e) {
			LOG.error("Failed to load a permission from " + permissionsElement.getDeclaringExtension().getNamespaceIdentifier()); //$NON-NLS-1$
		}
		return this;		
	}
}
