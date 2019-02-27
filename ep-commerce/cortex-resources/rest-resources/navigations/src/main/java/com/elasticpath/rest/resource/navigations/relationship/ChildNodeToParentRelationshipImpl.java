/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.navigations.relationship;

import javax.inject.Inject;

import io.reactivex.Observable;
import org.apache.commons.lang.StringUtils;

import com.elasticpath.domain.catalog.Category;
import com.elasticpath.rest.definition.navigations.NavigationIdentifier;
import com.elasticpath.rest.definition.navigations.ParentNodeToChildRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.category.CategoryRepository;

/**
 * Reverse link from the child to the parent navigation.
 */
public class ChildNodeToParentRelationshipImpl implements ParentNodeToChildRelationship.LinkFrom {

	private final NavigationIdentifier childIdentifier;

	private final CategoryRepository categoryRepository;

	/**
	 * Constructor.
	 *
	 * @param childIdentifier    childIdentifier
	 * @param categoryRepository categoryRepository
	 */
	@Inject
	public ChildNodeToParentRelationshipImpl(@RequestIdentifier final NavigationIdentifier childIdentifier,
			@ResourceRepository final CategoryRepository categoryRepository) {
		this.childIdentifier = childIdentifier;
		this.categoryRepository = categoryRepository;
	}

	@Override
	public Observable<NavigationIdentifier> onLinkFrom() {

		 return categoryRepository.findByStoreAndCategoryCode(childIdentifier.getNavigations().getScope().getValue(), childIdentifier.getNodeId()
				.getValue())
				.flatMapObservable(category -> StringUtils.isNotEmpty(category.getParentGuid())
						? Observable.just(category) : Observable.empty())
				.flatMap(category -> categoryRepository.findByGuid(category.getParentGuid()).toObservable())
		        .flatMap(this::buildNavigationIdentifier);
	}

	private Observable<NavigationIdentifier> buildNavigationIdentifier(final Category parentCategory) {

		return  Observable.just(NavigationIdentifier.builder()
				.withNavigations(childIdentifier.getNavigations())
				.withNodeId(StringIdentifier.of(parentCategory.getCode()))
				.build());
	}
}
