/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.changeset.helpers;

/**
 * A helper for determing change set permissions.
 */
public interface ChangeSetPermissionsHelper {
	/**
	 * Returns true if the change set feature is enabled.
	 * Package-default access for testing.
	 * @return The result
	 */
	boolean isChangeSetFeatureEnabled();
	
	/**
	 * Default access for testing.
	 * @return True if the user has one of the change set permissions.
	 */
	boolean userHasChangeSetPermission();
}
