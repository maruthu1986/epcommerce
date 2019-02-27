/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.epcommerce.events;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.relos.rs.events.RoleTransitionEvent;
import com.elasticpath.rest.relos.rs.events.ScopedEventEntityHandler;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerSessionRepository;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.resource.integration.epcommerce.common.authentication.AuthenticationConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerRepository;

/**
 * Merges the customer session on transition from public to registered role.
 */
@Component(property = {
		Constants.SERVICE_RANKING + ":Integer=100",
		"eventType=" + RoleTransitionEvent.EVENT_TYPE })
public class AnonymousToRegisteredTransitionEventHandler implements ScopedEventEntityHandler<RoleTransitionEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(AnonymousToRegisteredTransitionEventHandler.class);


	@Reference
	private CustomerRepository customerRepository;
	@Reference
	private CustomerSessionRepository customerSessionRepository;


	@Override
	public void handleEvent(final String scope, final RoleTransitionEvent event) {
		if (isPublicToRegisteredTransition(event)) {
			String publicUserGuid = event.getOldUserGuid();
			String registeredUserGuid = event.getNewUserGuid();
			ExecutionResult<Void> mergeResult = mergeCustomerSession(scope, publicUserGuid, registeredUserGuid);

			if (mergeResult.isFailure()) {
				LOG.error("Error merging cart: {}", mergeResult.getErrorMessage());
			}
		}
	}

	private boolean isPublicToRegisteredTransition(final RoleTransitionEvent event) {
		return AuthenticationConstants.PUBLIC_ROLENAME.equals(event.getOldRole())
				&& AuthenticationConstants.REGISTERED_ROLE.equals(event.getNewRole());
	}

	private ExecutionResult<Void> mergeCustomerSession(final String storeCode, final String donorCustomerGuid,
														final String recipientCustomerGuid) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				Customer recipientCustomer = Assign.ifSuccessful(customerRepository.findCustomerByGuid(recipientCustomerGuid));
				CustomerSession customerSession =
						Assign.ifSuccessful(customerSessionRepository.findCustomerSessionByGuid(donorCustomerGuid));

				return customerRepository.mergeCustomer(customerSession, recipientCustomer, storeCode);
			}
		}.execute();
	}
}
