/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.emails.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.emails.EmailIdentifier;
import com.elasticpath.rest.definition.emails.EmailToEmailsRelationship;
import com.elasticpath.rest.definition.emails.EmailsIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Add email form to emails link.
 */
public class EmailToEmailsRelationshipImpl implements EmailToEmailsRelationship.LinkTo {

	private final EmailIdentifier emailIdentifier;

	/**
	 * Constructor.
	 *
	 * @param emailIdentifier emailIdentifier
	 */
	@Inject
	public EmailToEmailsRelationshipImpl(@RequestIdentifier final EmailIdentifier emailIdentifier) {
		this.emailIdentifier = emailIdentifier;
	}

	@Override
	public Observable<EmailsIdentifier> onLinkTo() {
		return Observable.just(emailIdentifier.getEmails());
	}
}
