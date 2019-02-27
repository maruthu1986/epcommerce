/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.targetedselling.dynamiccontent.model.impl;

import com.elasticpath.cmclient.store.targetedselling.dynamiccontent.model.DynamicContentSearchTabModel;
/**
 * View model, represent search criteria for dynamic content list. 
 */
public class DynamicContentSearchTabModelImpl implements DynamicContentSearchTabModel {
	
	private String name = ""; //$NON-NLS-1$
	
	private AssignedStatus assignedStatus = AssignedStatus.ALL;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public void setAssignedStatus(final AssignedStatus assignedStatus) {
		this.assignedStatus = assignedStatus;
	}

	@Override
	public AssignedStatus getAssignedStatus() {
		return assignedStatus;
	}

}
