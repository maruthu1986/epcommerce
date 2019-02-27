/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.changeset.policy;

import java.util.HashSet;
import java.util.Set;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.domain.dataimport.ImportJob;

/**
 * 
 * The policy for change set selection for import jobs.
 *
 */
public class ChangeSetSelectedForImportJobStatePolicy extends ChangeSetSelectedStatePolicy {
	
	@Override
	public EpState determineContainerState(final PolicyActionContainer targetContainer) {
		
		ImportJob selectedImportJob = (ImportJob) targetContainer.getPolicyDependent();
		
		if (selectedImportJob != null && getEditableImportDataTypeNames().contains(selectedImportJob.getImportDataTypeName())) { 
			return EpState.EDITABLE;
		}
		
		return super.determineContainerState(targetContainer);		
	}
	
	/**
	 * Template method which controls the import jobs which do not require change set permission. 
	 * @return The set of importDataTypeNames which are editable.
	 */
	protected Set<String> getEditableImportDataTypeNames() {
		Set<String> returnSet = new HashSet<>();
		returnSet.add("Customer"); //$NON-NLS-1$
		returnSet.add("Product Association"); //$NON-NLS-1$
		returnSet.add("Inventory"); //$NON-NLS-1$
		return returnSet;
	}
}
