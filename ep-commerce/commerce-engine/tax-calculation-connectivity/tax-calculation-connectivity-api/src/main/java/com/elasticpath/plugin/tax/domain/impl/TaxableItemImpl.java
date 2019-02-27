/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.domain.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.plugin.tax.domain.DiscountableTaxItem;
import com.elasticpath.plugin.tax.domain.TaxableItem;

/**
 * Mutable implementation of a {@link com.elasticpath.plugin.tax.domain.DiscountableTaxItem}.
 */
public class TaxableItemImpl implements DiscountableTaxItem, Serializable {

	private static final long serialVersionUID = 50000000001L;

	private BigDecimal discount = BigDecimal.ZERO;
	private BigDecimal price = BigDecimal.ZERO;
	private Currency currency;
	private String taxCode;
	private boolean taxCodeActive;
	private String itemDescription;
	private String itemGuid;
	private String itemCode;
	private Map<String, String> fieldValues = new HashMap<>();
	private int quantity;

	@Override
	public BigDecimal getDiscount() {
		return discount;
	}

	@Override
	public void applyDiscount(final BigDecimal discount) {
		if (discount != null) {
			this.discount = discount;
		}
	}

	@Override
	public Currency getCurrency() {
		return currency;
	}

	@Override
	public BigDecimal getPrice() {
		return price;
	}

	@Override
	public BigDecimal getTaxablePrice() {
		return price.subtract(discount);
	}

	@Override
	public String getTaxCode() {
		return taxCode;
	}

	@Override
	public boolean isTaxCodeActive() {
		return taxCodeActive;
	}

	@Override
	public String getItemGuid() {
		return itemGuid;
	}

	@Override
	public void setItemGuid(final String itemGuid) {
		this.itemGuid = itemGuid;
	}

	@Override
	public String getItemCode() {
		return itemCode;
	}

	public void setCurrency(final Currency currency) {
		this.currency = currency;
	}

	/**
	 * Sets the taxable item price amount.
	 *
	 * @param price the amount
	 */
	public void setPrice(final BigDecimal price) {
		if (price != null) {
			this.price = price;
		}
	}

	public void setTaxCode(final String taxCode) {
		this.taxCode = taxCode;
	}

	public void setTaxCodeActive(final boolean taxCodeActive) {
		this.taxCodeActive = taxCodeActive;
	}

	public void setItemCode(final String itemCode) {
		this.itemCode = itemCode;
	}

	@Override
	public void setFields(final Map<String, String> fieldValues) {
		this.fieldValues = fieldValues;
	}

	@Override
	public void setFieldValue(final String name, final String value) {
		fieldValues.put(name, value);
	}

	@Override
	public int getQuantity() {
		return quantity;
	}

	@Override
	public void setQuantity(final int quantity) {
		this.quantity = quantity;
	}

	@Override
	public String getItemDescription() {
		return itemDescription;
	}

	@Override
	public void setItemDescription(final String itemDescription) {
		this.itemDescription = itemDescription;
	}

	@Override
	public String getFieldValue(final String name) {
		return fieldValues.get(name);
	}

	@Override
	public Map<String, String> getFields() {
		Map<String, String> fields = new HashMap<>();
		for (final Map.Entry<String, String> fieldEntry : fieldValues.entrySet()) {
			fields.put(fieldEntry.getKey(), fieldEntry.getValue());
		}
		return Collections.unmodifiableMap(fields);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof TaxableItem)) {
			return false;
		}
		TaxableItemImpl taxableItem = (TaxableItemImpl) obj;
		return Objects.equals(getDiscount(), taxableItem.getDiscount())
			&& Objects.equals(getCurrency(), taxableItem.getCurrency())
			&& Objects.equals(getPrice(), taxableItem.getPrice())
			&& Objects.equals(getItemCode(), taxableItem.getItemCode())
			&& Objects.equals(getTaxCode(), taxableItem.getTaxCode())
			&& Objects.equals(isTaxCodeActive(), taxableItem.isTaxCodeActive())
			&& Objects.equals(getFields(), taxableItem.getFields());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getDiscount(), getCurrency(), getPrice(), getItemCode(), getTaxCode(), isTaxCodeActive(), getFields());
	}
}
