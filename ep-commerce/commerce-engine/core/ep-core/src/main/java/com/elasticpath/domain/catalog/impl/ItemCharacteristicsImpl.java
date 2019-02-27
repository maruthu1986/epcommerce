/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.domain.catalog.impl;

import com.elasticpath.domain.catalog.ItemCharacteristics;

/**
 * Methods to show characteristics of an item.
 */
public class ItemCharacteristicsImpl implements ItemCharacteristics {

	private Boolean shippable;
	private Boolean configurable;
	
	@Override
	public boolean isShippable() {
		if (shippable == null) {
			throw new IllegalStateException("Item Characteristics Query did not request shippable");
		}
		return shippable;
	}
	@Override
	public boolean isConfigurable() {
		if (configurable == null) {
			throw new IllegalStateException("Configurable does not exist");
		}
		return configurable;
	}
	
	public void setShippable(final Boolean shippable) {
		this.shippable = shippable;
	}
	
	public void setConfigurable(final Boolean configurable) {
		this.configurable = configurable;
	}
}
