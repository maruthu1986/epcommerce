/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemselections.link.impl;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.rest.definition.controls.ControlsMediaTypes;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionOptionEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Options;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.itemselections.rel.ItemSelectionsResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.common.selector.SelectorRepresentationRels;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Strategy that adds the selector link to item definition options.
 */
@Singleton
@Named("addSelectorLinkToItemDefinitionOptionStrategy")
public final class AddSelectorLinkToItemDefinitionOptionStrategy implements ResourceStateLinkHandler<ItemDefinitionOptionEntity> {

	private final String resourceServerName;


	/**
	 * Default constructor.
	 *
	 * @param resourceServerName the resource server name
	 */
	@Inject
	public AddSelectorLinkToItemDefinitionOptionStrategy(
			@Named("resourceServerName")
			final String resourceServerName) {

		this.resourceServerName = resourceServerName;
	}

	@Override
	public Collection<ResourceLink> getLinks(final ResourceState<ItemDefinitionOptionEntity> optionRepresentation) {

		final Collection<ResourceLink> links;

		ItemDefinitionOptionEntity optionEntity = optionRepresentation.getEntity();
		if (StringUtils.isEmpty(optionEntity.getComponentId())) {
			String scope = optionRepresentation.getScope();
			String itemId = optionEntity.getItemId();
			String optionId = optionEntity.getOptionId();
			String uri = URIUtil.format(resourceServerName, scope, itemId, Options.PATH_PART,
					optionId, Selector.URI_PART);

			ResourceLink link = ResourceLinkFactory.create(uri, ControlsMediaTypes.SELECTOR.id(), SelectorRepresentationRels.SELECTOR,
					ItemSelectionsResourceRels.OPTION_REV);
			links = Collections.singleton(link);
		} else {
			links = Collections.emptyList();
		}

		return links;
	}
}
