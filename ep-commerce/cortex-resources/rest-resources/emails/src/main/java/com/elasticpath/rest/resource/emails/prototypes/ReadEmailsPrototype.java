/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.prototypes;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.emails.EmailEntity;
import com.elasticpath.rest.definition.emails.EmailIdentifier;
import com.elasticpath.rest.definition.emails.EmailsIdentifier;
import com.elasticpath.rest.definition.emails.EmailsResource;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;
import com.elasticpath.rest.helix.data.annotation.UriPart;
import com.elasticpath.rest.id.IdentifierPart;

/**
 * Emails prototype for Read operation.
 */
public class ReadEmailsPrototype implements EmailsResource.Read {

	private final IdentifierPart<String> scope;

	private final Repository<EmailEntity, EmailIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param scope 	 scope
	 * @param repository repository
	 */
	@Inject
	public ReadEmailsPrototype(@UriPart(EmailsIdentifier.SCOPE) final IdentifierPart<String> scope,
							   @ResourceRepository final Repository<EmailEntity, EmailIdentifier> repository) {
		this.scope = scope;
		this.repository = repository;
	}

	@Override
	public Observable<EmailIdentifier> onRead() {
		return repository.findAll(scope);
	}

}