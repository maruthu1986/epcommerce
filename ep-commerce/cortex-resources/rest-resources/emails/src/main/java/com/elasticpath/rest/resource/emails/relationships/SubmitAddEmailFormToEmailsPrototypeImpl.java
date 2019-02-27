/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.relationships;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.emails.AddEmailFormResource;
import com.elasticpath.rest.definition.emails.EmailEntity;
import com.elasticpath.rest.definition.emails.EmailIdentifier;
import com.elasticpath.rest.definition.emails.EmailsIdentifier;
import com.elasticpath.rest.form.SubmitResult;
import com.elasticpath.rest.helix.data.annotation.RequestForm;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;
import com.elasticpath.rest.helix.data.annotation.UriPart;
import com.elasticpath.rest.id.IdentifierPart;

/**
 * Add email form to emails link.
 */
public class SubmitAddEmailFormToEmailsPrototypeImpl implements AddEmailFormResource.SubmitWithResult {

	private final EmailEntity emailEntity;

	private final Repository<EmailEntity, EmailIdentifier> repository;

	private final IdentifierPart<String> scope;

	/**
	 * Constructor.
	 *
	 * @param emailEntity   emailEntity
	 * @param scope         scope
	 * @param repository    repository
	 */
	@Inject
	public SubmitAddEmailFormToEmailsPrototypeImpl(@RequestForm final EmailEntity emailEntity,
												   @UriPart(EmailsIdentifier.SCOPE) final IdentifierPart<String> scope,
												   @ResourceRepository final Repository<EmailEntity, EmailIdentifier> repository) {
		this.emailEntity = emailEntity;
		this.repository = repository;
		this.scope = scope;
	}

	@Override
	public Single<SubmitResult<EmailIdentifier>> onSubmitWithResult() {
		return repository.submit(emailEntity, scope);
	}
}
