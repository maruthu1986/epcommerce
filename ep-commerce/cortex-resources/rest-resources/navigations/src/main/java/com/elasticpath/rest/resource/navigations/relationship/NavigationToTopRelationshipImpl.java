/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.navigations.relationship;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.navigations.NavigationIdentifier;
import com.elasticpath.rest.definition.navigations.NavigationToTopRelationship;
import com.elasticpath.rest.definition.navigations.NavigationsIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Link to the very top of navigations from a navigation.
 */
public class NavigationToTopRelationshipImpl implements NavigationToTopRelationship.LinkTo {

	private final NavigationIdentifier navigationIdentifier;

	/**
	 * Constructor.
	 *
	 * @param navigationIdentifier navigation identifier
	 */
	@Inject
	public NavigationToTopRelationshipImpl(@RequestIdentifier final NavigationIdentifier navigationIdentifier) {
		this.navigationIdentifier = navigationIdentifier;
	}

	@Override
	public Observable<NavigationsIdentifier> onLinkTo() {
		return Observable.just(navigationIdentifier.getNavigations());
	}
}
