/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.datapolicies.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.datapolicies.DataPolicyConsentFormIdentifier;
import com.elasticpath.rest.definition.datapolicies.DataPolicyIdentifier;
import com.elasticpath.rest.definition.datapolicies.DataPolicyToDataPolicyConsentFormRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Add data policy to data policy consent form link.
 */
public class DataPolicyToDataPolicyConsentFormRelationshipImpl implements DataPolicyToDataPolicyConsentFormRelationship.LinkTo {

	private final DataPolicyIdentifier dataPolicyIdentifier;

	/**
	 * Constructor.
	 *
	 * @param dataPolicyIdentifier data policy identifier
	 */
	@Inject
	public DataPolicyToDataPolicyConsentFormRelationshipImpl(@RequestIdentifier final DataPolicyIdentifier dataPolicyIdentifier) {
		this.dataPolicyIdentifier = dataPolicyIdentifier;
	}

	@Override
	public Observable<DataPolicyConsentFormIdentifier> onLinkTo() {
		return Observable.just(DataPolicyConsentFormIdentifier.builder()
				.withDataPolicy(dataPolicyIdentifier)
				.build());
	}
}
