/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.datapolicies.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.datapolicies.DataPoliciesIdentifier;
import com.elasticpath.rest.definition.datapolicies.RootToDataPoliciesRelationship;
import com.elasticpath.rest.helix.data.annotation.UserScopes;
import com.elasticpath.rest.id.type.StringIdentifier;

/**
 * Root to data policies link.
 */
public class RootToDataPoliciesRelationshipImpl implements RootToDataPoliciesRelationship.LinkTo {

	private final Iterable<String> scopes;

	/**
	 * Constructor.
	 *
	 * @param scopes scopes
	 */
	@Inject
	public RootToDataPoliciesRelationshipImpl(@UserScopes final Iterable<String> scopes) {
		this.scopes = scopes;
	}

	@Override
	public Observable<DataPoliciesIdentifier> onLinkTo() {
		return Observable.fromIterable(scopes)
				.map(StringIdentifier::of)
				.map(scopeId -> DataPoliciesIdentifier.builder().withScope(scopeId).build())
				.firstElement().toObservable();
	}

}
