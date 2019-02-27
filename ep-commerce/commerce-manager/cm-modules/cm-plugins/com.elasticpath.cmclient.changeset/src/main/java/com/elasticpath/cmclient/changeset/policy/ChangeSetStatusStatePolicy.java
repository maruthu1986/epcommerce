/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.changeset.policy;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.service.changeset.ChangeSetManagementService;
import com.elasticpath.cmclient.core.ServiceLocator;

/**
 * A state policy to determine the editable state depending on the 
 * change set state.
 */
public class ChangeSetStatusStatePolicy extends AbstractChangeSetEditorStatePolicy {

	private ChangeSetManagementService changesetManagementService;

	private ChangeSet changeSet;
	
	@Override
	public void init(final Object dependentObject) {
		super.init(dependentObject);
		this.changeSet = (ChangeSet) dependentObject;
	}
	@Override
	public EpState determineContainerState(final PolicyActionContainer targetContainer) {
		if (!getChangeSetManagementService().isChangeAllowed(getChangeSet().getGuid())) {
			return EpState.READ_ONLY;
		}
		
		return EpState.EDITABLE;
	}
	
	/**
	 * Get the change set management service.
	 * 
	 * @return the change set management service.
	 */
	protected ChangeSetManagementService getChangeSetManagementService() {
		if (changesetManagementService == null) {
			changesetManagementService = ServiceLocator.getService(ContextIdNames.CHANGESET_MANAGEMENT_SERVICE);
		}
		return changesetManagementService;
	}
	
	/**
	 *
	 * @return the changeSet the change set
	 */
	public ChangeSet getChangeSet() {
		return changeSet;
	}
}
