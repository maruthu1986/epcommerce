/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.emailinfo.relationship;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.emails.AddEmailFormIdentifier;
import com.elasticpath.rest.definition.emails.EmailsIdentifier;
import com.elasticpath.rest.definition.orders.EmailFormForEmailinfoRelationship;
import com.elasticpath.rest.definition.orders.EmailInfoIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Email info to email form link.
 */
public class EmailInfoToFormRelationshipImpl implements EmailFormForEmailinfoRelationship.LinkTo {

	private final EmailInfoIdentifier emailInfoIdentifier;

	/**
	 * Constructor.
	 *
	 * @param emailInfoIdentifier emailInfoIdentifier
	 */
	@Inject
	public EmailInfoToFormRelationshipImpl(@RequestIdentifier final EmailInfoIdentifier emailInfoIdentifier) {
		this.emailInfoIdentifier = emailInfoIdentifier;
	}

	@Override
	public Observable<AddEmailFormIdentifier> onLinkTo() {
		return Observable.just(AddEmailFormIdentifier.builder()
				.withEmails(EmailsIdentifier.builder()
						.withScope(emailInfoIdentifier.getOrder().getScope())
						.build())
				.build());
	}
}
