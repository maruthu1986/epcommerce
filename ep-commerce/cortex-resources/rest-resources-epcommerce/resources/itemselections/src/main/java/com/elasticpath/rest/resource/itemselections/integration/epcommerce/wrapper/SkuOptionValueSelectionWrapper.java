/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemselections.integration.epcommerce.wrapper;

import java.util.Collection;

import com.elasticpath.domain.skuconfiguration.SkuOptionValue;

/**
 * Wraps collections of selectable sku option values, and the selected sku option value of an item.
 */
public class SkuOptionValueSelectionWrapper {

	/**
	 * The selectable sku option values.
	 */
	private final Collection<SkuOptionValue> selectableSkuOptionValues;

	/**
	 * The selected sku option value.
	 */
	private final SkuOptionValue selectedSkuOptionValue;

	/**
	 * Instantiates a new sku option value selection wrapper.
	 *
	 * @param selectableSkuOptionValues the selectable sku option values
	 * @param selectedSkuOptionValue the selected sku option value
	 */
	public SkuOptionValueSelectionWrapper(final Collection<SkuOptionValue> selectableSkuOptionValues,
			final SkuOptionValue selectedSkuOptionValue) {
		this.selectableSkuOptionValues = selectableSkuOptionValues;
		this.selectedSkuOptionValue = selectedSkuOptionValue;
	}

	/**
	 * Gets the selectable sku option values.
	 *
	 * @return the selectable sku option values
	 */
	public Collection<SkuOptionValue> getSelectableSkuOptionValues() {
		return selectableSkuOptionValues;
	}

	/**
	 * Gets the selected sku option value.
	 *
	 * @return the selected sku option value
	 */
	public SkuOptionValue getSelectedSkuOptionValue() {
		return selectedSkuOptionValue;
	}

}
