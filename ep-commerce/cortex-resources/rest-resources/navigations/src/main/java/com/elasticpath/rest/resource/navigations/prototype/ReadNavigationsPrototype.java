/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.navigations.prototype;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.navigations.NavigationIdentifier;
import com.elasticpath.rest.definition.navigations.NavigationsIdentifier;
import com.elasticpath.rest.definition.navigations.NavigationsResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Read operation on the navigations.
 */
public class ReadNavigationsPrototype implements NavigationsResource.Read {

	private final NavigationsIdentifier navigationsIdentifier;
	private final LinksRepository<NavigationsIdentifier, NavigationIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param navigationsIdentifier navigations identifier
	 * @param repository navigations to navigation repository
	 */
	@Inject
	public ReadNavigationsPrototype(
			@RequestIdentifier final NavigationsIdentifier navigationsIdentifier,
			@ResourceRepository final LinksRepository<NavigationsIdentifier, NavigationIdentifier> repository) {

		this.navigationsIdentifier = navigationsIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<NavigationIdentifier> onRead() {
		return repository.getElements(navigationsIdentifier);
	}
}
