/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.emails.EmailsIdentifier;
import com.elasticpath.rest.definition.emails.ProfileToEmailsRelationship;
import com.elasticpath.rest.definition.profiles.ProfileIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.UserId;
import com.elasticpath.rest.id.type.StringIdentifier;

/**
 * Emails to Profile link.
 */
public class EmailsToProfileRelationshipImpl implements ProfileToEmailsRelationship.LinkFrom {

	private final EmailsIdentifier emailsIdentifier;

	private final String userId;

	/**
	 * Constructor.
	 *
	 * @param emailsIdentifier emailsIdentifier
	 * @param userId userId
	 */
	@Inject
	public EmailsToProfileRelationshipImpl(@UserId final String userId,
										   @RequestIdentifier final EmailsIdentifier emailsIdentifier) {
		this.emailsIdentifier = emailsIdentifier;
		this.userId = userId;
	}

	@Override
	public Observable<ProfileIdentifier> onLinkFrom() {
		return Observable.just(ProfileIdentifier.builder()
				.withScope(emailsIdentifier.getScope())
				.withProfileId(StringIdentifier.of(userId))
				.build());
	}
}
