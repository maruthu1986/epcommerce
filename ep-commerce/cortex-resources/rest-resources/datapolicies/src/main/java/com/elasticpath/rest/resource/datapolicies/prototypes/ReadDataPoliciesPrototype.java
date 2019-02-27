/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.datapolicies.prototypes;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.datapolicies.DataPoliciesIdentifier;
import com.elasticpath.rest.definition.datapolicies.DataPoliciesResource;
import com.elasticpath.rest.definition.datapolicies.DataPolicyEntity;
import com.elasticpath.rest.definition.datapolicies.DataPolicyIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;
import com.elasticpath.rest.helix.data.annotation.UriPart;
import com.elasticpath.rest.id.IdentifierPart;

/**
 * Data policies prototype for read operations.
 */
public class ReadDataPoliciesPrototype implements DataPoliciesResource.Read {

	private final IdentifierPart<String> scope;

	private final Repository<DataPolicyEntity, DataPolicyIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param scope      scope
	 * @param repository repository
	 */
	@Inject
	public ReadDataPoliciesPrototype(@UriPart(DataPoliciesIdentifier.SCOPE) final IdentifierPart<String> scope,
									 @ResourceRepository final Repository<DataPolicyEntity, DataPolicyIdentifier> repository) {
		this.scope = scope;
		this.repository = repository;
	}

	@Override
	public Observable<DataPolicyIdentifier> onRead() {
		return repository.findAll(scope);
	}
}
