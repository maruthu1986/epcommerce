/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.sellingchannel.director.impl;

import java.io.Serializable;
import java.util.Comparator;

import com.google.common.collect.Ordering;

import com.elasticpath.inventory.InventoryDto;

/**
 * Gets the worse case of the inventory.
 */
public class InventoryWorseCaseComparator implements Comparator<InventoryDto>, Serializable {

	private static final long serialVersionUID = -4969847994628054074L;

	@Override
	public int compare(final InventoryDto inventory1, final InventoryDto inventory2) {
		return Ordering.natural().nullsFirst()
			.onResultOf(InventoryDto::getRestockDate).nullsFirst()
			.compare(inventory1, inventory2);
	}
}
