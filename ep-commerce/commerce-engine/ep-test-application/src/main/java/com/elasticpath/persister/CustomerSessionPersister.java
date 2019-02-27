/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.persister;

import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.service.customer.CustomerSessionService;

/**
 * Persists CustomerSessions.
 */
public class CustomerSessionPersister implements Persister<CustomerSession> {

	@Autowired
	private CustomerSessionService customerSessionService;

	@Override
	public void persist(final CustomerSession customerSession) {
		if (customerSession.getCustomerSessionMemento().isPersisted()) {
			customerSessionService.update(customerSession);
		} else {
			customerSessionService.add(customerSession);
		}
	}
}
