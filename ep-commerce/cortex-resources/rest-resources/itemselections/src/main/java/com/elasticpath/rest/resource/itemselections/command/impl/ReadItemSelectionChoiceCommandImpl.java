/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemselections.command.impl;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.controls.ControlsMediaTypes;
import com.elasticpath.rest.definition.itemdefinitions.ItemdefinitionsMediaTypes;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Options;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Values;
import com.elasticpath.rest.resource.itemselections.ItemSelectionLookup;
import com.elasticpath.rest.resource.itemselections.command.ReadItemSelectionChoiceCommand;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.common.selector.SelectorRepresentationRels;
import com.elasticpath.rest.schema.uri.ItemDefinitionsOptionValueUriBuilderFactory;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Implements {@link ReadItemSelectionChoiceCommand}.
 */
@Named
public final class ReadItemSelectionChoiceCommandImpl implements ReadItemSelectionChoiceCommand {

	private final String resourceServerName;
	private final ItemDefinitionsOptionValueUriBuilderFactory itemDefinitionsOptionValueUriBuilderFactory;
	private final ItemSelectionLookup itemSelectionLookup;

	private String scope;
	private String itemId;
	private String optionId;
	private String valueId;


	/**
	 * Read item definition command.
	 *
	 * @param resourceServerName the resource server name
	 * @param itemDefinitionsOptionValueUriBuilderFactory the item definitions URI Builder Factory
	 * @param itemSelectionLookup the item definition lookup
	 */
	@Inject
	public ReadItemSelectionChoiceCommandImpl(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("itemDefinitionsOptionValueUriBuilderFactory")
			final ItemDefinitionsOptionValueUriBuilderFactory itemDefinitionsOptionValueUriBuilderFactory,
			@Named("itemSelectionLookup")
			final ItemSelectionLookup itemSelectionLookup) {

		this.resourceServerName = resourceServerName;
		this.itemDefinitionsOptionValueUriBuilderFactory = itemDefinitionsOptionValueUriBuilderFactory;
		this.itemSelectionLookup = itemSelectionLookup;
	}


	@Override
	public ExecutionResult<ResourceState<LinksEntity>> execute() {
		Collection<ResourceLink> links = new ArrayList<>();

		String descriptionUri = itemDefinitionsOptionValueUriBuilderFactory.get()
			.setScope(scope)
			.setItemId(itemId)
			.setOptionId(optionId)
			.setValueId(valueId)
			.build();

		ResourceLink descriptionLink = ResourceLinkFactory.createNoRev(descriptionUri, ItemdefinitionsMediaTypes.ITEM_DEFINITION_OPTION_VALUE.id(),
			SelectorRepresentationRels.DESCRIPTION);
		links.add(descriptionLink);

		String optionUri = URIUtil.format(resourceServerName, scope, itemId, Options.URI_PART, optionId);
		String optionSelectorUri = URIUtil.format(optionUri, Selector.URI_PART);
		ResourceLink selectorLink = ResourceLinkFactory.createNoRev(optionSelectorUri, ControlsMediaTypes.SELECTOR.id(),
			SelectorRepresentationRels.SELECTOR);
		links.add(selectorLink);

		String selfUri = URIUtil.format(optionUri, Values.URI_PART, valueId, Selector.URI_PART);
		Self self = SelfFactory.createSelf(selfUri);

		ExecutionResult<String> selectedOptionChoiceResult = itemSelectionLookup.getSelectedOptionChoiceForItemId(scope, itemId, optionId);
		if (selectedOptionChoiceResult.isFailure() || !StringUtils.equals(selectedOptionChoiceResult.getData(), valueId)) {
			ResourceLink selectActionLink = ResourceLinkFactory.createUriRel(selfUri, SelectorRepresentationRels.SELECT_ACTION);
			links.add(selectActionLink);
		}

		return ExecutionResultFactory.createReadOK(ResourceState.Builder
				.create(LinksEntity.builder().build())
				.withSelf(self)
				.addingLinks(links)
				.build());
	}

	/**
	 * Constructs a {@link ReadItemSelectionChoiceCommand}.
	 */
	@Named("readItemSelectionChoiceCommandBuilder")
	static class BuilderImpl implements Builder {

		private final ReadItemSelectionChoiceCommandImpl command;

		/**
		 * Default constructor.
		 *
		 * @param command the command
		 */
		@Inject
		BuilderImpl(final ReadItemSelectionChoiceCommandImpl command) {
			this.command = command;
		}

		@Override
		public Builder setItemId(final String itemId) {
			command.itemId = itemId;
			return this;
		}

		@Override
		public Builder setOptionId(final String optionId) {
			command.optionId = optionId;
			return this;
		}

		@Override
		public Builder setValueId(final String valueId) {
			command.valueId = valueId;
			return this;
		}

		@Override
		public Builder setScope(final String scope) {
			command.scope = scope;
			return this;
		}

		@Override
		public ReadItemSelectionChoiceCommand build() {
			assert command.scope != null : "Scope must be set.";
			assert command.itemId != null : "Item id must be set.";
			assert command.optionId != null : "Option id must be set.";
			assert command.valueId != null : "Value id must be set.";
			return command;
		}
	}
}
