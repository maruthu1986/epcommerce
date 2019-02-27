/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.common.selector;

import com.elasticpath.rest.definition.controls.SelectorEntity;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Builds Selector representations.
 */
public interface SelectorResourceStateBuilder {

	/**
	 * Add a choice to the Selector.
	 *
	 * @param choice The ResourceLink to the option
	 * @return This instance.
	 */
	SelectorResourceStateBuilder addChoice(ResourceLink choice);

	/**
	 * Add a link to the Selector.
	 *
	 * @param link The ResourceLink to add
	 * @return This instance.
	 */
	SelectorResourceStateBuilder addLink(ResourceLink link);

	/**
	 * Sets a particular choice to be selected. The ResourceLink needs to be added to the builder
	 * at some point.
	 *
	 * @param option The ResourceLink to be selected
	 * @return This instance.
	 */
	SelectorResourceStateBuilder setSelection(ResourceLink option);

	/**
	 * Set the self URI for the Selector representation.
	 *
	 * @param selfUri The self uri string.
	 * @return This instance
	 */
	SelectorResourceStateBuilder setSelfUri(String selfUri);

	/**
	 * Set the name of the selector.
	 *
	 * @param name the name of the selector.
	 * @return This instance.
	 */
	SelectorResourceStateBuilder setName(String name);

	/**
	 * Build the representation from the parameters provided to the builder.
	 *
	 * @return The Selector Representation.
	 */
	ResourceState<SelectorEntity> build();

	/**
	 * Sets selectorId.
	 *
	 * @param selectorId String
	 * @return builder
	 */
	SelectorResourceStateBuilder setSelectorId(String selectorId);

	/**
	 * Sets selectorType.
	 *
	 * @param selectorType Type
	 * @return builder
	 */
	SelectorResourceStateBuilder setSelectorType(Class<? extends ResourceEntity> selectorType);

	/**
	 * Sets scope.
	 *
	 * @param scope String
	 * @return builder
	 */
	SelectorResourceStateBuilder setScope(String scope);
}
