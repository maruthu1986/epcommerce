/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.emails.AddEmailFormIdentifier;
import com.elasticpath.rest.definition.emails.EmailsIdentifier;
import com.elasticpath.rest.definition.emails.EmailsToAddEmailFormRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Add email to emails form link.
 */
public class EmailsToAddEmailFormRelationshipImpl implements EmailsToAddEmailFormRelationship.LinkTo {

	private final EmailsIdentifier emailsIdentifier;

	/**
	 * Constructor.
	 *
	 * @param emailsIdentifier emailsIdentifier
	 */
	@Inject
	public EmailsToAddEmailFormRelationshipImpl(@RequestIdentifier final EmailsIdentifier emailsIdentifier) {
		this.emailsIdentifier = emailsIdentifier;
	}

	@Override
	public Observable<AddEmailFormIdentifier> onLinkTo() {
		return Observable.just(AddEmailFormIdentifier.builder()
				.withEmails(emailsIdentifier)
				.build());
	}
}
