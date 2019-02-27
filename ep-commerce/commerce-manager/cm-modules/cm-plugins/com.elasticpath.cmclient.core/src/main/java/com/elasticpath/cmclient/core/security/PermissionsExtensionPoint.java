/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;

import com.elasticpath.cmclient.core.CorePlugin;

/**
 * Permissions extension point constructor.
 */
public class PermissionsExtensionPoint {

	/**
	 * Permissions extension id.
	 */
	private static final String AUTHORIZATIONS_EXT_ID = "authorizations"; //$NON-NLS-1$

	private static final Logger LOG = Logger.getLogger(Permission.class);

	/** The extension's permissions element. */
	public static final String TAG_PERMISSIONS = "permissions"; //$NON-NLS-1$

	/** The extension's permission element. */
	public static final String TAG_PERMISSION = "permission"; //$NON-NLS-1$

	/** The extension's permission element. */
	public static final String TAG_ACTIVITY = "activity"; //$NON-NLS-1$

	/** The extension's name element. */
	public static final String ATT_NAME = "name"; //$NON-NLS-1$

	/** The extension's activityId element. */
	public static final String ATT_ACTIVITY_ID = "activityId"; //$NON-NLS-1$

	/** The extension's key element. */
	public static final String ATT_KEY = "key"; //$NON-NLS-1$

	/** The extension's id element. */
	private static final String ATT_ID = "id"; //$NON-NLS-1$
	
	/** The extension's perspective id element. */
	private static final String ATT_PERSPECTIVE_ID = "perspectiveId"; //$NON-NLS-1$
	
	/** The extension's description element. */
	private static final String ATT_DESCRIPTION = "description"; //$NON-NLS-1$

	/**
	 * Parses the Authorizations extension's xml elements and returns a collection of permissions that are defined in the extension.
	 * 
	 * @param permissionsElement The permissionsElement contained within the extension definition
	 * @return a Set of permission SecureId strings that are defined within the extension
	 */
	private void parsePermissionConfigElement(final IConfigurationElement permissionsElement, final Collection<Permission> permissions) {
		try {
			for (IConfigurationElement permissionElement : permissionsElement.getChildren(TAG_PERMISSION)) {
				String key = permissionElement.getAttribute(ATT_KEY);
				String name = permissionElement.getAttribute(ATT_NAME);
				String description = permissionElement.getAttribute(ATT_DESCRIPTION);
				String activityId = permissionElement.getAttribute(ATT_ACTIVITY_ID);
				Permission permission = new Permission(name, description, key, activityId);
				permissions.add(permission);
			}
		} catch (Exception e) {
			LOG.error("Failed to load a permission from " + permissionsElement.getDeclaringExtension().getNamespaceIdentifier()); //$NON-NLS-1$
		}
	}

	/**
	 *
	 * @return the permissions map
	 */
	public Map<Activity, Collection<Permission>> populateKnownPermissions() {
		LOG.debug("Populating known permissions from all plugins"); //$NON-NLS-1$
		Map<Activity, Collection<Permission>> knownPermissions = new HashMap<Activity, Collection<Permission>>();

		List<Activity> activities = new ArrayList<Activity>();
		List<Permission> permissions = new ArrayList<Permission>();
		final IExtension[] extensions = Platform.getExtensionRegistry().
			getExtensionPoint(CorePlugin.PLUGIN_ID, AUTHORIZATIONS_EXT_ID).getExtensions();

		parseExtensions(activities, permissions, extensions);
		
		for (Activity activity : activities) {
			knownPermissions.put(activity, new ArrayList<Permission>());
		}
		for (Permission permission : permissions) {
			boolean permissionHasParent = false;
			for (Activity activity : knownPermissions.keySet()) {
				if (permission.getActivityId() == null) {
					LOG.error("Permission: " + permission.getKey() + " does not have activity ID defined");  //$NON-NLS-1$//$NON-NLS-2$
				} else if (permission.getActivityId().equals(activity.getActivityId())) {
					knownPermissions.get(activity).add(permission);
					permissionHasParent = true;
				}
			}
			if (!permissionHasParent) {
				LOG.error("Permission: " + permission.getKey() + " does not have parent: " + permission.getActivityId());  //$NON-NLS-1$//$NON-NLS-2$
			}
		}
		
		return knownPermissions;
	}

	/**
	 *
	 * @param activities
	 * @param permissions
	 * @param extensions
	 */
	private void parseExtensions(final List<Activity> activities, final List<Permission> permissions, final IExtension[] extensions) {
		for (final IExtension extension : extensions) {
			for (final IConfigurationElement configElement : extension.getConfigurationElements()) {
				if (TAG_PERMISSIONS.equals(configElement.getName())) {
					parsePermissionConfigElement(configElement, permissions);
				} else if (TAG_ACTIVITY.equals(configElement.getName())) {
					parseActivityConfigElement(configElement, activities);
				}
			}
		}
	}

	/**
	 *
	 * @param configElement
	 * @return
	 */
	private void parseActivityConfigElement(final IConfigurationElement activityElement, final Collection<Activity> activities) {
		try {
			String name = activityElement.getAttribute(ATT_NAME);
			String activityId = activityElement.getAttribute(ATT_ID);
			String perspectiveId = activityElement.getAttribute(ATT_PERSPECTIVE_ID);
			Activity activity = new Activity(activityId, name, perspectiveId);
			activities.add(activity);
		} catch (Exception e) {
			LOG.error("Failed to load an activity from " + activityElement.getDeclaringExtension().getNamespaceIdentifier()); //$NON-NLS-1$
		}
	}

}
