/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.targetedselling.delivery.model.impl;

import com.elasticpath.cmclient.store.targetedselling.delivery.model.DynamicContentDeliverySearchTabModel;
/**
 * View model, represent search criteria for {@link com.elasticpath.domain.targetedselling.DynamicContentDelivery} list.
 */
public class DynamicContentDeliverySearchTabModelImpl implements DynamicContentDeliverySearchTabModel {
	
	private String name = ""; //$NON-NLS-1$
	
	private String dynamicContentName = ""; //$NON-NLS-1$
	
	private String contentspaceId = ""; //$NON-NLS-1$

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public String getDynamicContentName() {
		return dynamicContentName;
	}

	@Override
	public void setDynamicContentName(final String dynamicContentName) {
		this.dynamicContentName = dynamicContentName;
	}

	@Override
	public String getContentspaceId() {
		return contentspaceId;
	}

	@Override
	public void setContentspaceId(final String assignmentTargetId) {
		this.contentspaceId = assignmentTargetId;
	}	

}
