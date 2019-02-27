/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.domain.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.plugin.tax.domain.TaxableItem;
import com.elasticpath.plugin.tax.domain.TaxableItemContainer;
import com.elasticpath.plugin.tax.domain.TaxedItem;
import com.elasticpath.plugin.tax.domain.TaxedItemContainer;

/**
 * Mutable implementation of {@link TaxedItemContainer}.
 */
public class MutableTaxedItemContainer extends AbstractTaxItemContainer implements TaxedItemContainer {

	private static final long serialVersionUID = 5000000001L;
	
	private final List<TaxedItem> taxedItems = new ArrayList<>();
	
	/**
	 * Initialize {@code MutableTaxedItemContainer} with {@code TaxableItemContainer} values.
	 *
	 * @param taxableItemContainer the taxableItemContainer
	 */
	public void initialize(final TaxableItemContainer taxableItemContainer) {
		this.setOriginAddress(taxableItemContainer.getOriginAddress());
		this.setDestinationAddress(taxableItemContainer.getDestinationAddress());
		this.setStoreCode(taxableItemContainer.getStoreCode());
		this.setCurrency(taxableItemContainer.getCurrency());
	}
	
	@Override
	public List<? extends TaxedItem> getItems() {
		return Collections.unmodifiableList(taxedItems);
	}

	/**
	 * Adds a new taxed item.
	 *  
	 * @param item the item to add
	 */
	public void addTaxedItem(final TaxedItem item) {
		taxedItems.add(item);
	}
	
	/**
	 * Copies over the items from taxableContainer to taxedContainer when there are no tax calculation result.
	 *
	 * @param taxableItems a collection of taxable items
	 */
	public void copyTaxableItems(final List<? extends TaxableItem> taxableItems) {
		for (TaxableItem item : taxableItems) {
			MutableTaxedItem taxedItem = new MutableTaxedItem();
			// assuming the order of the items is the same (list) we look up a taxable item by it's id
			taxedItem.setTaxableItem(item);
			taxedItem.setPriceBeforeTax(taxedItem.getPrice());
			this.addTaxedItem(taxedItem);
		}
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}
	
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
	
}
