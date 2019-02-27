/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.navigations.impl;

import io.reactivex.Observable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.navigations.NavigationIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.category.CategoryRepository;

/**
 * This repository keeps track of the parent child relationship for the Navigation.
 * @param <IP> Parent identifier
 * @param <IC> Child identifier
 */
@Component
public class NavigationToChildNavigationRepositoryImpl<IP extends NavigationIdentifier, IC extends NavigationIdentifier>
		implements LinksRepository<NavigationIdentifier, NavigationIdentifier> {

	private CategoryRepository categoryRepository;

	@Override
	public Observable<NavigationIdentifier> getElements(final NavigationIdentifier identifier) {
		String storeCode = identifier.getNavigations().getScope().getValue();
		String nodeId = identifier.getNodeId().getValue();

		return categoryRepository.findChildren(storeCode, nodeId)
				.map(category -> buildNavigationIdentifier(identifier, category.getCode()));
	}

	/**
	 * Method that build navigation identifier using node id and navigations identifier.
	 *
	 * @param identifier navigations identifier
	 * @param nodeId id of the navigation, code category
	 * @return navigation identifier
	 */
	protected NavigationIdentifier buildNavigationIdentifier(final NavigationIdentifier identifier, final String nodeId) {

		return NavigationIdentifier.builder()
				.withNavigations(identifier.getNavigations())
				.withNodeId(StringIdentifier.of(nodeId))
				.build();
	}

	@Reference
	public void setCategoryRepository(final CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}
}
