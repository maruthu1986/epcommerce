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

/**
 * Profile to email memberships link.
 */
public class ProfileToEmailsRelationshipImpl implements ProfileToEmailsRelationship.LinkTo {

	private final ProfileIdentifier profileIdentifier;

	/**
	 * Constructor.
	 *
	 * @param profileIdentifier profileIdentifier
	 */
	@Inject
	public ProfileToEmailsRelationshipImpl(@RequestIdentifier final ProfileIdentifier profileIdentifier) {
		this.profileIdentifier = profileIdentifier;
	}

	@Override
	public Observable<EmailsIdentifier> onLinkTo() {
		return Observable.just(EmailsIdentifier.builder()
				.withScope(profileIdentifier.getScope())
				.build());
	}
}
