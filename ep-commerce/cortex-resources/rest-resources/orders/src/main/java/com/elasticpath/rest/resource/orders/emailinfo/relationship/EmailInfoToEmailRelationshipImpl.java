/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.emailinfo.relationship;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.emails.EmailIdentifier;
import com.elasticpath.rest.definition.orders.EmailForEmailinfoRelationship;
import com.elasticpath.rest.definition.orders.EmailInfoIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Email info to email link.
 */
public class EmailInfoToEmailRelationshipImpl implements EmailForEmailinfoRelationship.LinkTo {

	private final EmailInfoIdentifier emailInfoIdentifier;

	private final LinksRepository<EmailInfoIdentifier, EmailIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param emailInfoIdentifier emailInfoIdentifier
	 * @param repository          repository
	 */
	@Inject
	public EmailInfoToEmailRelationshipImpl(@RequestIdentifier final EmailInfoIdentifier emailInfoIdentifier,
											@ResourceRepository final LinksRepository<EmailInfoIdentifier, EmailIdentifier> repository) {
		this.emailInfoIdentifier = emailInfoIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<EmailIdentifier> onLinkTo() {
		return repository.getElements(emailInfoIdentifier);
	}
}
