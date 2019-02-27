/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.profiles.ProfileIdentifier;
import com.elasticpath.rest.definition.purchases.PurchasesForProfileRelationship;
import com.elasticpath.rest.definition.purchases.PurchasesIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Profile to purchases link.
 */
public class ProfileToPurchasesRelationshipImpl implements PurchasesForProfileRelationship.LinkTo {

	private final ProfileIdentifier profileIdentifier;

	/**
	 * Constructor.
	 *
	 * @param profileIdentifier profileIdentifier
	 */
	@Inject
	public ProfileToPurchasesRelationshipImpl(@RequestIdentifier final ProfileIdentifier profileIdentifier) {
		this.profileIdentifier = profileIdentifier;
	}

	@Override
	public Observable<PurchasesIdentifier> onLinkTo() {
		return Observable.just(PurchasesIdentifier.builder()
				.withScope(profileIdentifier.getScope())
				.build());
	}
}
