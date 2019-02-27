/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.integration.epcommerce.repository.navigations.impl;

import io.reactivex.Observable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.catalog.Category;
import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.navigations.NavigationIdentifier;
import com.elasticpath.rest.definition.navigations.NavigationsIdentifier;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.category.CategoryRepository;

/**
 * Repository that retrieves navigations from the root.
 * @param <N> Navigations identifier
 * @param <I> Navigation identifier
 */
@Component
public class RootNavigationsRepositoryImpl<N extends NavigationsIdentifier, I extends NavigationIdentifier>
		implements LinksRepository<NavigationsIdentifier, NavigationIdentifier> {

	private CategoryRepository categoryRepository;

	@Override
	public Observable<NavigationIdentifier> getElements(final NavigationsIdentifier identifier) {
		String storeCode = identifier.getScope().getValue();

		return categoryRepository.findRootCategories(storeCode)
				.map(Category::getCode)
				.map(categoryCode -> buildNavigationIdentifier(identifier, categoryCode));
	}

	/**
	 * Method that build navigation identifier using node id and navigations identifier.
	 *
	 * @param identifier navigations identifier
	 * @param nodeId id of the navigation, code category
	 * @return navigation identifier
	 */
	protected NavigationIdentifier buildNavigationIdentifier(final NavigationsIdentifier identifier, final String nodeId) {
		IdentifierPart<String> rootId = StringIdentifier.of(nodeId);

		return NavigationIdentifier.builder()
				.withNavigations(identifier)
				.withNodeId(rootId)
				.build();
	}

	@Reference
	public void setCategoryRepository(final CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}
}
