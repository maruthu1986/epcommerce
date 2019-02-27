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
import com.elasticpath.rest.helix.data.annotation.UserId;
import com.elasticpath.rest.id.type.StringIdentifier;

/**
 * Data policies to profile link.
 */
public class DataPoliciesToProfileRelationshipImpl implements ProfileToDataPoliciesRelationship.LinkFrom {

	private final DataPoliciesIdentifier dataPoliciesIdentifier;

	private final String userId;

	/**
	 * Constructor.
	 *
	 * @param dataPoliciesIdentifier dataPoliciesIdentifier
	 * @param userId                 userId
	 */
	@Inject
	public DataPoliciesToProfileRelationshipImpl(@UserId final String userId,
												 @RequestIdentifier final DataPoliciesIdentifier dataPoliciesIdentifier) {
		this.dataPoliciesIdentifier = dataPoliciesIdentifier;
		this.userId = userId;
	}

	@Override
	public Observable<ProfileIdentifier> onLinkFrom() {
		return Observable.just(ProfileIdentifier.builder()
				.withProfileId(StringIdentifier.of(userId))
				.withScope(dataPoliciesIdentifier.getScope())
				.build());
	}
}