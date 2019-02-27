/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.datapolicies.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.datapolicies.DataPoliciesIdentifier;
import com.elasticpath.rest.definition.datapolicies.ProfileToDataPoliciesRelationship;
import com.elasticpath.rest.definition.profiles.ProfileIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Profile to data policies link.
 */
public class ProfileToDataPoliciesRelationshipImpl implements ProfileToDataPoliciesRelationship.LinkTo {

	private final ProfileIdentifier profileIdentifier;

	/**
	 * Constructor.
	 *
	 * @param profileIdentifier profileIdentifier
	 */
	@Inject
	public ProfileToDataPoliciesRelationshipImpl(@RequestIdentifier final ProfileIdentifier profileIdentifier) {
		this.profileIdentifier = profileIdentifier;
	}

	@Override
	public Observable<DataPoliciesIdentifier> onLinkTo() {
		return Observable.just(DataPoliciesIdentifier.builder()
				.withScope(profileIdentifier.getScope())
				.build());
	}
}
