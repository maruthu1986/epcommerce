/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.common.selector;

import static com.elasticpath.rest.schema.ResourceState.Builder.create;
import static com.elasticpath.rest.schema.SelfFactory.createSelf;
import static com.google.common.collect.Iterables.isEmpty;

import javax.inject.Named;

import com.elasticpath.rest.definition.controls.SelectorEntity;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Builds a single selection Selector. Only one selection can be selected.
 */
@Named("singleSelectorResourceStateBuilder")
public class SingleSelectorResourceStateBuilder implements SelectorResourceStateBuilder {

	private static final Integer SELECTION_RULE = 1;

	private final SingleSelectorRepresentationBuilderDelegate delegate = new SingleSelectorRepresentationBuilderDelegate();

	@Override
	public SelectorResourceStateBuilder addChoice(final ResourceLink choice) {
		delegate.addChoice(choice);
		return this;
	}

	@Override
	public SelectorResourceStateBuilder addLink(final ResourceLink link) {
		delegate.addLink(link);
		return this;
	}


	@Override
	public SelectorResourceStateBuilder setSelection(final ResourceLink choice) {
		delegate.setSelection(choice);
		return this;
	}

	@Override
	public SelectorResourceStateBuilder setSelfUri(final String selfUri) {
		delegate.setSelfUri(selfUri);
		return this;
	}

	@Override
	public SelectorResourceStateBuilder setName(final String name) {
		delegate.setName(name);
		return this;
	}

	@Override
	public ResourceState<SelectorEntity> build() {

		delegate.assertValidity();

		SelectorEntity selectorEntity = SelectorEntity.builder()
				.withName(delegate.getName())
				.withSelectionRule(SELECTION_RULE)
				.withSelectorId(delegate.getSelectorId())
				.build();
		ResourceState.Builder<SelectorEntity> resourceStateBuilder = create(selectorEntity)
				.withSelf(
						createSelf(delegate.getSelfUri())
				)
				.withScope(delegate.getScope());

		resourceStateBuilder.addingLinks(delegate.buildCompletedLinks());

		if (!isEmpty(delegate.getLinks())) {
			resourceStateBuilder.addingLinks(delegate.getLinks());
		}

		return resourceStateBuilder.build();
	}

	@Override
	public SelectorResourceStateBuilder setSelectorId(final String orderId) {

		delegate.setSelectorId(orderId);
		return this;
	}

	@Override
	public SelectorResourceStateBuilder setSelectorType(final Class<? extends ResourceEntity> selectorType) {

		delegate.setSelectorType(selectorType);
		return this;
	}

	@Override
	public SelectorResourceStateBuilder setScope(final String scope) {

		delegate.setScope(scope);
		return this;
	}
}
