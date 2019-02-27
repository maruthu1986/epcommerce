/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.datapolicies.advisors;

import java.util.Arrays;
import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.advise.LinkedMessage;
import com.elasticpath.rest.definition.datapolicies.DataPolicyConsentFormIdentifier;
import com.elasticpath.rest.definition.datapolicies.DataPolicyForRegistrationsAdvisor;
import com.elasticpath.rest.definition.registrations.NewAccountRegistrationFormIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceService;
import com.elasticpath.rest.resource.integration.epcommerce.repository.datapolicies.DataPolicyValidationService;

/**
 * Data policy advisor on registrations resource.
 */
public final class DataPolicyRegistrationsAdvisor implements DataPolicyForRegistrationsAdvisor.LinkedFormAdvisor {

	private static final String CUSTOMER_PROFILE = "CUSTOMER_PROFILE";

	@Inject
	@RequestIdentifier
	private NewAccountRegistrationFormIdentifier registrationFormIdentifier;

	@Inject
	@ResourceService
	private DataPolicyValidationService validationService;

	@Override
	public Observable<LinkedMessage<DataPolicyConsentFormIdentifier>> onLinkedAdvise() {
		return validationService.validate(registrationFormIdentifier.getScope(), Arrays.asList(CUSTOMER_PROFILE));
	}
}
