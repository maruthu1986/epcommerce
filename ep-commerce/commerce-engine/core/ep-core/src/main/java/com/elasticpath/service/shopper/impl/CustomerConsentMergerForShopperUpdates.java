/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shopper.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.datapolicy.CustomerConsent;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.service.customer.CustomerSessionShopperUpdateHandler;
import com.elasticpath.service.datapolicy.CustomerConsentService;

/**
 * Handles what happens to the {@link CustomerConsent} when an anonymous client logs in.
 */
public final class CustomerConsentMergerForShopperUpdates implements CustomerSessionShopperUpdateHandler {

	private final CustomerConsentService customerConsentService;

	/**
	 * Alternate constructor.
	 *
	 * @param customerConsentService customer consent service
	 */
	public CustomerConsentMergerForShopperUpdates(final CustomerConsentService customerConsentService) {
		this.customerConsentService = customerConsentService;
	}

	/**
	 * Moves the Customer Consent from previous Customer to new Customer when shopper changes, if the transition is from anonymous to registered.
	 * <p>
	 * {@inheritDoc}
	 */
	@Override
	public void invalidateShopper(final CustomerSession customerSession, final Shopper invalidShopper) {
		if (!invalidShopper.getCustomer().isAnonymous()) {
			return;
		}

		final List<CustomerConsent> invalidatedCustomerConsents =
				Optional.ofNullable(customerConsentService.findByCustomerGuid(invalidShopper.getCustomer().getGuid()))
						.orElseGet(ArrayList::new);

		if (invalidatedCustomerConsents.isEmpty()) {
			return;
		}

		List<Long> customerConsentUids = invalidatedCustomerConsents.stream().map(CustomerConsent::getUidPk).collect(Collectors.toList());

		customerConsentService.updateCustomerGuids(customerConsentUids, customerSession.getShopper().getCustomer().getGuid());
	}
}
