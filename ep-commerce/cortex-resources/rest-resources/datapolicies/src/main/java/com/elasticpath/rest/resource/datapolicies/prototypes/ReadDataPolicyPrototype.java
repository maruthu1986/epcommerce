/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.datapolicies.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.datapolicies.DataPolicyEntity;
import com.elasticpath.rest.definition.datapolicies.DataPolicyIdentifier;
import com.elasticpath.rest.definition.datapolicies.DataPolicyResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Data policy prototype for read operations.
 */
public class ReadDataPolicyPrototype implements DataPolicyResource.Read {

	private final DataPolicyIdentifier dataPolicyIdentifier;

	private final Repository<DataPolicyEntity, DataPolicyIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param dataPolicyIdentifier data policy identifier
	 * @param repository           repository
	 */
	@Inject
	public ReadDataPolicyPrototype(@RequestIdentifier final DataPolicyIdentifier dataPolicyIdentifier,
								   @ResourceRepository final Repository<DataPolicyEntity, DataPolicyIdentifier> repository) {
		this.dataPolicyIdentifier = dataPolicyIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<DataPolicyEntity> onRead() {
		return repository.findOne(dataPolicyIdentifier);
	}
}
