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
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Data policy consent form prototype for read operations.
 */
public class ReadDataPolicyConsentFormPrototype implements DataPolicyConsentFormResource.Read {

	private final DataPolicyConsentFormIdentifier consentFormIdentifier;

	private final Repository<DataPolicyEntity, DataPolicyIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param consentFormIdentifier data policy consent form identifier
	 * @param repository            repository
	 */
	@Inject
	public ReadDataPolicyConsentFormPrototype(@RequestIdentifier final DataPolicyConsentFormIdentifier consentFormIdentifier,
											  @ResourceRepository final Repository<DataPolicyEntity, DataPolicyIdentifier> repository) {
		this.consentFormIdentifier = consentFormIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<DataPolicyEntity> onRead() {
		return repository.findOne(consentFormIdentifier.getDataPolicy());
	}
}
