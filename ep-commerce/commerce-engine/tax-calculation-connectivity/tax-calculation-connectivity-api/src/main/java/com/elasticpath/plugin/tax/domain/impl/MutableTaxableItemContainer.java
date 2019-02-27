/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.domain.impl;

import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.plugin.tax.domain.TaxOperationContext;
import com.elasticpath.plugin.tax.domain.TaxableItem;
import com.elasticpath.plugin.tax.domain.TaxableItemContainer;

/**
 * Mutable implementation of {@link TaxableItemContainer}.
 */
public class MutableTaxableItemContainer extends AbstractTaxItemContainer implements TaxableItemContainer {

	private static final long serialVersionUID = 50000000001L;
	
	private List<TaxableItem> items;
	private TaxOperationContext taxOperationContext;
	
	
	@Override
	public List<TaxableItem> getItems() {
		return items;
	}
	
	public void setItems(final List<TaxableItem> items) {
		this.items = items;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public TaxOperationContext getTaxOperationContext() {
		return taxOperationContext;
	}

	public void setTaxOperationContext(final TaxOperationContext taxOperationContext) {
		this.taxOperationContext = taxOperationContext;
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
