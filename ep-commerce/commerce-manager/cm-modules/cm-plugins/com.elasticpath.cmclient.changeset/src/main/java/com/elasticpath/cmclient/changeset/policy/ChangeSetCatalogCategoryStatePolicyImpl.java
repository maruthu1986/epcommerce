/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.changeset.policy;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.domain.catalog.Catalog;

/**
 * Change set policy for catalog and category.
 * Just skip policy checks for master catalogs. 
 * Virtual catalogs and categories processed in common way.
 */
public class ChangeSetCatalogCategoryStatePolicyImpl extends ChangesetMemberStatePolicyImpl {

	/* (non-Javadoc)
	 * @see com.elasticpath.cmclient.changeset.policy.ChangesetMemberStatePolicyImpl#
	 *  determineState(com.elasticpath.cmclient.policy.common.PolicyActionContainer)
	 */
	@Override
	public EpState determineContainerState(final PolicyActionContainer targetContainer) {
		if (this.getDependentObject() != null 
				&& this.getDependentObject() instanceof Catalog
				&& ((Catalog) this.getDependentObject()).isMaster()) {
			return EpState.EDITABLE;
		}
		return super.determineContainerState(targetContainer);
	}

}
