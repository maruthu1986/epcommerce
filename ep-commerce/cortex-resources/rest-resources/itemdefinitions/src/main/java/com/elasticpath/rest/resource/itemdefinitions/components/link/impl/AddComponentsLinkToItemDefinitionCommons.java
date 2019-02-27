/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.components.link.impl;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Components;
import com.elasticpath.rest.resource.itemdefinitions.components.ItemDefinitionComponentLookup;
import com.elasticpath.rest.resource.itemdefinitions.rel.ItemDefinitionResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.schema.util.ResourceStateUtil;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Common code for adding a components link to an item definition or item definition component.
 */
@Singleton
@Named("addComponentsLinkToItemDefinitionCommons")
public final class AddComponentsLinkToItemDefinitionCommons {

	private final ItemDefinitionComponentLookup componentsLookup;

	/**
	 * Constructor.
	 *
	 * @param componentsLookup components lookup
	 */
	@Inject
	AddComponentsLinkToItemDefinitionCommons(
			@Named("itemDefinitionComponentLookup")
			final ItemDefinitionComponentLookup componentsLookup) {
		this.componentsLookup = componentsLookup;
	}

	/**
	 * Get links for a {@link ResourceState} of {@link com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionEntity}
	 * or {@link com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionComponentEntity}.
	 * @param representation the resource state
	 * @param itemId the item id
	 * @return the collection of links
	 */
	public Collection<ResourceLink> getLinks(final ResourceState<?> representation, final String itemId) {

		final Collection<ResourceLink> result;

		String scope = representation.getScope();
		ExecutionResult<Boolean> hasComponentsResult = componentsLookup.hasComponents(scope, itemId);

		if (hasComponentsResult.isSuccessful() && hasComponentsResult.getData()) {
			String itemDefinitionUri = ResourceStateUtil.getSelfUri(representation);
			ResourceLink componentsLink = ResourceLinkFactory.create(
					URIUtil.format(itemDefinitionUri, Components.URI_PART),
					CollectionsMediaTypes.LINKS.id(),
					ItemDefinitionResourceRels.COMPONENTS_REL,
					ItemDefinitionResourceRels.DEFINITION_REV);
			result = Collections.singleton(componentsLink);
		} else {
			result = Collections.emptyList();
		}

		return result;
	}
}
