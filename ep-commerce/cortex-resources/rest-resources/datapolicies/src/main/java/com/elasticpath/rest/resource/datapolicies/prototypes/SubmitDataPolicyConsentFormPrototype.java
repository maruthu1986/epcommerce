/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.datapolicies.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.datapolicies.DataPolicyConsentFormIdentifier;
import com.elasticpath.rest.definition.datapolicies.DataPolicyConsentFormResource;
import com.elasticpath.rest.definition.datapolicies.DataPolicyEntity;
import com.elasticpath.rest.definition.datapolicies.DataPolicyIdentifier;
import com.elasticpath.rest.form.SubmitResult;
import com.elasticpath.rest.helix.data.annotation.RequestForm;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Submit prototype for data policy consent form.
 */
public class SubmitDataPolicyConsentFormPrototype implements DataPolicyConsentFormResource.SubmitWithResult {

	private final DataPolicyEntity dataPolicyEntity;

	private final Repository<DataPolicyEntity, DataPolicyIdentifier> repository;

	private final DataPolicyConsentFormIdentifier consentFormIdentifier;

	/**
	 * Constructor.
	 *
	 * @param dataPolicyEntity      dataPolicyEntity
	 * @param repository            Repository
	 * @param consentFormIdentifier consentFormIdentifier
	 */
	@Inject
	public SubmitDataPolicyConsentFormPrototype(@RequestForm final DataPolicyEntity dataPolicyEntity,
												@ResourceRepository final Repository<DataPolicyEntity, DataPolicyIdentifier> repository,
												@RequestIdentifier final DataPolicyConsentFormIdentifier consentFormIdentifier) {
		this.dataPolicyEntity = dataPolicyEntity;
		this.repository = repository;
		this.consentFormIdentifier = consentFormIdentifier;
	}

	@Override
	public Single<SubmitResult<DataPolicyIdentifier>> onSubmitWithResult() {
		return repository.submit(dataPolicyEntity, consentFormIdentifier.getDataPolicy().getDataPolicies().getScope());
	}
}
