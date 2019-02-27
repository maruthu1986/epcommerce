/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.navigations.prototype;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.navigations.NavigationEntity;
import com.elasticpath.rest.definition.navigations.NavigationIdentifier;
import com.elasticpath.rest.definition.navigations.NavigationResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Read on the navigation resource.
 */
public class ReadNavigationPrototype implements NavigationResource.Read {

	private final NavigationIdentifier navigationIdentifier;
	private final Repository<NavigationEntity, NavigationIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param navigationIdentifier navigation identifier
	 * @param repository repository which handles children of the navigation
	 */
	@Inject
	public ReadNavigationPrototype(
			@RequestIdentifier final NavigationIdentifier navigationIdentifier,
			@ResourceRepository final Repository<NavigationEntity, NavigationIdentifier> repository) {

		this.navigationIdentifier = navigationIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<NavigationEntity> onRead() {
		return repository.findOne(navigationIdentifier);
	}
}
