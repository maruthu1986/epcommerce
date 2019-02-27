/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.security.Activity;
import com.elasticpath.cmclient.core.security.Permission;
import com.elasticpath.cmclient.core.security.PermissionsExtensionPoint;

/**
 * <p>Each plugin has the option of extending the CmClient Core authorizations extension,
 * to provide a list of permissions (in the form of Strings) upon which the plugin will filter
 * the UI.</p>
 * <p>This class populates and keeps track of the defined Permissions from all
 * currently-installed plugins.</p>
 */
public final class PermissionsProvider {

	/**
	 * Set of permissions known to all the currently-installed plugins, 
	 * keyed on the pluginId of the contributing plugin.
	 */
	private Map<Activity, Collection<Permission>> knownPermissions;
	
	private PermissionsProvider() {
		//
	}
	
	/**
	 * Singleton's instance getter.
	 *
	 * @return instance of PermissionsProvider
	 */
	public static PermissionsProvider getInstance() {
		return  CmSingletonUtil.getSessionInstance(PermissionsProvider.class);
	}
	
	/**
	 * Gets all the currently-known permissions.
	 *
	 * @return a set of all known permissions
	 */
	public Map<Activity, Collection<Permission>> getKnownPermissions() {
		if (knownPermissions == null) {
			populateKnownPermissions();
		}
		return knownPermissions;
	}
	
	/**
	 * Loops through all the currently-installed extensions looking for any 
	 * extensions to the "authorizations" extension point. Once found, it
	 * adds the defined permissions to the global {@link Permission}.
	 */
	private void populateKnownPermissions() {
		knownPermissions = new PermissionsExtensionPoint().populateKnownPermissions();
	}	

	/**
	 * Retrieves all permissions associated with current perspective.
	 * 
	 * @param perspectiveId perspective id.
	 * @return collection of all permissions associated with perspective.
	 */
	public Collection<Permission> retrievePermissions(final String perspectiveId) {
		Collection<Permission> permissions = new HashSet<Permission>();
		for (Activity activity : getKnownPermissions().keySet()) {
			if (activity.getPerspectiveId().equals(perspectiveId)) {
				permissions.addAll(getKnownPermissions().get(activity));
			}
		}
		return permissions;
	}
}
