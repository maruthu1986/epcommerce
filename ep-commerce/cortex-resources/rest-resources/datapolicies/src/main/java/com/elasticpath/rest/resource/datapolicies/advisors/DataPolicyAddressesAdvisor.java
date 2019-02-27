/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.datapolicies.advisors;

import java.util.Arrays;
import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.advise.LinkedMessage;
import com.elasticpath.rest.definition.addresses.AddressFormIdentifier;
import com.elasticpath.rest.definition.datapolicies.DataPolicyConsentFormIdentifier;
import com.elasticpath.rest.definition.datapolicies.DataPolicyForAddressesAdvisor;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceService;
import com.elasticpath.rest.resource.integration.epcommerce.repository.datapolicies.DataPolicyValidationService;

/**
 * Data policy advisor on addresses resource.
 */
public final class DataPolicyAddressesAdvisor implements DataPolicyForAddressesAdvisor.LinkedFormAdvisor {

	private static final String CUSTOMER_BILLING_ADDRESS = "CUSTOMER_BILLING_ADDRESS";
	private static final String CUSTOMER_SHIPPING_ADDRESS = "CUSTOMER_SHIPPING_ADDRESS";

	@Inject
	@RequestIdentifier
	private AddressFormIdentifier addressFormIdentifier;

	@Inject
	@ResourceService
	private DataPolicyValidationService validationService;

	@Override
	public Observable<LinkedMessage<DataPolicyConsentFormIdentifier>> onLinkedAdvise() {
		return validationService.validate(addressFormIdentifier.getAddresses().getScope(), Arrays.asList(CUSTOMER_BILLING_ADDRESS,
				CUSTOMER_SHIPPING_ADDRESS));
	}
}
