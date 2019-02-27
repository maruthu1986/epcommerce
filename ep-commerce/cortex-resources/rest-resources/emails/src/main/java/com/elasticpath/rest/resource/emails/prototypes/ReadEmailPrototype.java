/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.emails.EmailEntity;
import com.elasticpath.rest.definition.emails.EmailIdentifier;
import com.elasticpath.rest.definition.emails.EmailResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Email prototype for Read operation.
 */
public class ReadEmailPrototype implements EmailResource.Read {

	private final EmailIdentifier emailIdentifier;

	private final Repository<EmailEntity, EmailIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param emailIdentifier email identifier
	 * @param repository      repository
	 */
	@Inject
	public ReadEmailPrototype(@RequestIdentifier final EmailIdentifier emailIdentifier,
							  @ResourceRepository final Repository<EmailEntity, EmailIdentifier> repository) {
		this.emailIdentifier = emailIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<EmailEntity> onRead() {
		return repository.findOne(emailIdentifier);
	}
}
