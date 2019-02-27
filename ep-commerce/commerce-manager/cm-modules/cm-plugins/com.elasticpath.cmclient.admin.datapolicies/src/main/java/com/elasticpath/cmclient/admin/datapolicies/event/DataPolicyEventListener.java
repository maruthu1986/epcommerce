/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.cmclient.admin.datapolicies.event;

import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.domain.datapolicy.DataPolicy;

/**
 * Event listener for changes to objects which can be in change set.
 */
public interface DataPolicyEventListener {

	/**
	 * Notifies for a changed data policy.
	 *
	 * @param event customer segment change event
	 */
	void dataPolicyChanged(ItemChangeEvent<DataPolicy> event);
}
