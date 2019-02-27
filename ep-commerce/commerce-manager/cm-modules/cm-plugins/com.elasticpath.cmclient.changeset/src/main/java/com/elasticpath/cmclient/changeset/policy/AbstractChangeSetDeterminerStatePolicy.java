/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.changeset.policy;


import com.elasticpath.cmclient.changeset.ChangeSetPlugin;
import com.elasticpath.cmclient.policy.common.AbstractDeterminerStatePolicyImpl;

/**
 * Abstract ChangeSet determiner class.
 */
public abstract class AbstractChangeSetDeterminerStatePolicy extends AbstractDeterminerStatePolicyImpl {

	@Override
	protected String getPluginId() {
		return ChangeSetPlugin.PLUGIN_ID;
	}
}


