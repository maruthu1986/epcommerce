/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.navigations.relationship;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.navigations.NavigationIdentifier;
import com.elasticpath.rest.definition.navigations.ParentNodeToChildRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Relationship from the navigation to the child.
 */
public class ParentNodeToChildRelationshipImpl implements ParentNodeToChildRelationship.LinkTo {

	private final NavigationIdentifier parentIdentifier;
	private final LinksRepository<NavigationIdentifier, NavigationIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param parentIdentifier parent navigation identifier
	 * @param repository nav to nav repository to search children navigations
	 */
	@Inject
	public ParentNodeToChildRelationshipImpl(
			@RequestIdentifier final NavigationIdentifier parentIdentifier,
			@ResourceRepository final LinksRepository<NavigationIdentifier, NavigationIdentifier> repository) {

		this.parentIdentifier = parentIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<NavigationIdentifier> onLinkTo() {
		return repository.getElements(parentIdentifier);
	}
}
