/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.navigations.prototype;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.navigations.CategoryCodeEntity;
import com.elasticpath.rest.definition.navigations.NavigationIdentifier;
import com.elasticpath.rest.definition.navigations.NavigationLookupFormIdentifier;
import com.elasticpath.rest.definition.navigations.NavigationLookupFormResource;
import com.elasticpath.rest.form.SubmitResult;
import com.elasticpath.rest.helix.data.annotation.RequestForm;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Submit prototype for navigation lookup form resource.
 */
public class SubmitNavigationLookupFormPrototype implements NavigationLookupFormResource.SubmitWithResult {
	private final CategoryCodeEntity categoryCodeEntity;

	private final Repository<CategoryCodeEntity, NavigationIdentifier> repository;

	private final NavigationLookupFormIdentifier navigationLookupFormIdentifier;

	/**
	 * Constructor.
	 *
	 * @param categoryCodeEntity             categoryCodeEntity
	 * @param repository                     repository
	 * @param navigationLookupFormIdentifier navigationLookupFormIdentifier
	 */
	@Inject
	public SubmitNavigationLookupFormPrototype(@RequestForm final CategoryCodeEntity categoryCodeEntity,
			@ResourceRepository final Repository<CategoryCodeEntity, NavigationIdentifier> repository,
			@RequestIdentifier final NavigationLookupFormIdentifier navigationLookupFormIdentifier) {
		this.categoryCodeEntity = categoryCodeEntity;
		this.repository = repository;
		this.navigationLookupFormIdentifier = navigationLookupFormIdentifier;
	}

	@Override
	public Single<SubmitResult<NavigationIdentifier>> onSubmitWithResult() {
		return repository.submit(categoryCodeEntity, navigationLookupFormIdentifier.getNavigations().getScope());
	}
}
