/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.changeset.policy;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;

/**
 * A version of the standard ChangeSet member state policy which will use a given
 * category's catalog as the dependent object rather than the category itself.
 */
public class ChangeSetMemberCategoryCatalogStatePolicyImpl extends ChangesetMemberStatePolicyImpl {

	private Catalog catalog;
	
	@Override
	public EpState determineContainerState(final PolicyActionContainer targetContainer) {
		if (catalog != null && catalog.isMaster()) {
			return EpState.EDITABLE;
		}
		return super.determineContainerState(targetContainer);
	}

	@Override
	public void init(final Object dependentObject) {
		if (dependentObject instanceof Category) {
			catalog = ((Category) dependentObject).getCatalog();
			super.init(catalog);
		} else {
			if (dependentObject instanceof Catalog) {
				catalog = (Catalog) dependentObject;
			}
			super.init(dependentObject);
		}
	}

}
