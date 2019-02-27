/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.emails.EmailIdentifier;
import com.elasticpath.rest.definition.emails.EmailToProfileRelationship;
import com.elasticpath.rest.definition.profiles.ProfileIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.UserId;
import com.elasticpath.rest.id.type.StringIdentifier;

/**
 * Email to Profile link.
 */
public class EmailToProfileRelationshipImpl implements EmailToProfileRelationship.LinkTo {

	private final EmailIdentifier emailIdentifier;

	private final String userId;

	/**
	 * Constructor.
	 *
	 * @param emailIdentifier emailIdentifier
	 * @param userId userId
	 */
	@Inject
	public EmailToProfileRelationshipImpl(@UserId final String userId,
										   @RequestIdentifier final EmailIdentifier emailIdentifier) {
		this.emailIdentifier = emailIdentifier;
		this.userId = userId;
	}

	@Override
	public Observable<ProfileIdentifier> onLinkTo() {
		return Observable.just(ProfileIdentifier.builder()
				.withScope(emailIdentifier.getEmails().getScope())
				.withProfileId(StringIdentifier.of(userId))
				.build());
	}
}
