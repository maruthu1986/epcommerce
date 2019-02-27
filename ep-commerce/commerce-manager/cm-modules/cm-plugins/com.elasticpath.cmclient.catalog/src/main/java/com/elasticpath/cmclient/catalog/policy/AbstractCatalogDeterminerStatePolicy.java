/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.catalog.policy;

import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.policy.common.AbstractDeterminerStatePolicyImpl;

/**
 * Abstract Catalog Determiner State Policy class.
 */
public abstract class AbstractCatalogDeterminerStatePolicy extends AbstractDeterminerStatePolicyImpl {

	@Override
	protected String getPluginId() {
		return CatalogPlugin.PLUGIN_ID;
	}
}
