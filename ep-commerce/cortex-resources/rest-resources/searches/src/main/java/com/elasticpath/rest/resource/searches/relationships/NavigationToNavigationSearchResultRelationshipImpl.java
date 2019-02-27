/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.searches.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.navigations.NavigationIdentifier;
import com.elasticpath.rest.definition.searches.NavigationSearchResultIdentifier;
import com.elasticpath.rest.definition.searches.NavigationToNavigationSearchResultRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.id.type.IntegerIdentifier;

/**
 * Navigation resource to NavigationSearch resource relationship implementation.
 */
public class NavigationToNavigationSearchResultRelationshipImpl implements NavigationToNavigationSearchResultRelationship.LinkTo {

	private static final int FIRST_PAGE = 1;
	private final NavigationIdentifier navigationIdentifier;

	/**
	 * Consturctor.
	 *
	 * @param navigationIdentifier NavigationIdentifier
	 */
	@Inject
	public NavigationToNavigationSearchResultRelationshipImpl(@RequestIdentifier final NavigationIdentifier navigationIdentifier) {
		this.navigationIdentifier = navigationIdentifier;
	}

	@Override
	public Observable<NavigationSearchResultIdentifier> onLinkTo() {
		return Observable.just(
				NavigationSearchResultIdentifier.builder()
						.withNavigation(navigationIdentifier)
						.withPageId(IntegerIdentifier.of(FIRST_PAGE))
						.build()
		);
	}
}
