/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.domain.shopper;

import com.elasticpath.domain.customer.CustomerSession;

/**
 * An interface to update any of the transient data on {@link CustomerSession}. 
 */
public interface UpdateShopperTransientData {

	/**
	 * Updates transient data on {@link CustomerSession} that comes from {@link CustomerSession}.
	 *
	 * @param customerSession {@link CustomerSession} which contains the transient data that {@link CustomerSession} requires.
	 */
	void updateTransientDataWith(CustomerSession customerSession);
	
}
