/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.datapolicies.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.datapolicies.DataPoliciesIdentifier;
import com.elasticpath.rest.definition.datapolicies.DataPolicyIdentifier;
import com.elasticpath.rest.definition.datapolicies.DataPolicyToDataPoliciesRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Add data policy to data policies link.
 */
public class DataPolicyToDataPoliciesRelationshipImpl implements DataPolicyToDataPoliciesRelationship.LinkTo {

	private final DataPolicyIdentifier dataPolicyIdentifier;

	/**
	 * Constructor.
	 *
	 * @param dataPolicyIdentifier dataPolicyIdentifier
	 */
	@Inject
	public DataPolicyToDataPoliciesRelationshipImpl(@RequestIdentifier final DataPolicyIdentifier dataPolicyIdentifier) {
		this.dataPolicyIdentifier = dataPolicyIdentifier;
	}

	@Override
	public Observable<DataPoliciesIdentifier> onLinkTo() {
		return Observable.just(dataPolicyIdentifier.getDataPolicies());
	}
}
