/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemselections.integration;

import java.util.Collection;

import com.elasticpath.rest.schema.ResourceEntity;

/**
 * Adaptable DTO for item selectable option values.
 */
public interface ItemSelectionOptionValuesDto extends ResourceEntity {

	/**
	 * Gets the selectable option value correlation ids.
	 *
	 * @return the selectable option value correlation ids
	 */
	Collection<String> getSelectableOptionValueCorrelationIds();

	/**
	 * Sets the selectable option value correlation ids.
	 *
	 * @param selectableOptionValueCorrelationIds the selectable option value correlation ids
	 * @return the item selection option values dto
	 */
	ItemSelectionOptionValuesDto setSelectableOptionValueCorrelationIds(Collection<String> selectableOptionValueCorrelationIds);

	/**
	 * Gets the chosen option value correlation id.
	 *
	 * @return the chosen option value correlation id
	 */
	String getChosenOptionValueCorrelationId();

	/**
	 * Sets the chosen option value correlation id.
	 *
	 * @param chosenOptionValueCorrelationId the chosen option value correlation id
	 * @return the item selection option values dto
	 */
	ItemSelectionOptionValuesDto setChosenOptionValueCorrelationId(String chosenOptionValueCorrelationId);
}
