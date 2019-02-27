/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.lookups.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.lookups.CodeEntity;
import com.elasticpath.rest.definition.lookups.LookupItemFormIdentifier;
import com.elasticpath.rest.definition.lookups.LookupItemFormResource;
import com.elasticpath.rest.form.SubmitResult;
import com.elasticpath.rest.helix.data.annotation.RequestForm;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;
import com.elasticpath.rest.id.type.StringIdentifier;

/**
 * Submit prototype for item lookup form resource.
 */
public class SubmitItemLookupFormPrototype implements LookupItemFormResource.SubmitWithResult {

	private final CodeEntity codeEntity;

	private final Repository<CodeEntity, ItemIdentifier> repository;

	private final LookupItemFormIdentifier lookupItemFormIdentifier;

	/**
	 * Constructor.
	 *
	 * @param codeEntity        		codeEntity
	 * @param repository                Repository
	 * @param lookupItemFormIdentifier	LookupItemFormIdentifier
	 */
	@Inject
	public SubmitItemLookupFormPrototype(@RequestForm final CodeEntity codeEntity,
										 @ResourceRepository final Repository<CodeEntity, ItemIdentifier> repository,
										 @RequestIdentifier final LookupItemFormIdentifier lookupItemFormIdentifier) {
		this.codeEntity = codeEntity;
		this.repository = repository;
		this.lookupItemFormIdentifier = lookupItemFormIdentifier;
	}

	@Override
	public Single<SubmitResult<ItemIdentifier>> onSubmitWithResult() {
		return repository.submit(codeEntity, StringIdentifier.of(lookupItemFormIdentifier.getLookups().getScope().getValue()));
	}
}
