/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.domain.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.plugin.tax.domain.DiscountableTaxItem;
import com.elasticpath.plugin.tax.domain.TaxRecord;
import com.elasticpath.plugin.tax.domain.TaxableItem;
import com.elasticpath.plugin.tax.domain.TaxedItem;

/**
 * Mutable implementation of {@link com.elasticpath.plugin.tax.domain.TaxedItem}.
 */
public class MutableTaxedItem implements TaxedItem, Serializable {

	private static final long serialVersionUID = 50000000001L;

	private final List<TaxRecord> taxRecords = new ArrayList<>();

	private BigDecimal totalTax = BigDecimal.ZERO;
	private BigDecimal priceBeforeTax = BigDecimal.ZERO;
	private BigDecimal taxInPrice = BigDecimal.ZERO;

	private DiscountableTaxItem taxableItem;

	@Override
	public BigDecimal getTotalTax() {
		return totalTax;
	}

	@Override
	public List<TaxRecord> getTaxRecords() {
		return taxRecords;
	}

	@Override
	public BigDecimal getPriceBeforeTax() {
		return priceBeforeTax;
	}

	/**
	 * Adds a new tax record.
	 *
	 * @param taxRecord the tax record
	 */
	public void addTaxRecord(final TaxRecord taxRecord) {
		this.taxRecords.add(taxRecord);
		this.totalTax = totalTax.add(taxRecord.getTaxValue());
	}

	/**
	 * Adds an amount to the total before tax price.
	 *
	 * @param amount the amount to add
	 */
	public void setPriceBeforeTax(final BigDecimal amount) {
		this.priceBeforeTax = amount;
	}

	@Override
	public Currency getCurrency() {
		return getTaxableItem().getCurrency();
	}

	@Override
	public BigDecimal getPrice() {
		return getTaxableItem().getPrice();
	}

	@Override
	public BigDecimal getTaxablePrice() {
		return getTaxableItem().getTaxablePrice();
	}

	@Override
	public String getTaxCode() {
		return getTaxableItem().getTaxCode();
	}

	@Override
	public boolean isTaxCodeActive() {
		return getTaxableItem().isTaxCodeActive();
	}

	@Override
	public String getItemGuid() {
		return getTaxableItem().getItemGuid();
	}

	@Override
	public void setItemGuid(final String itemId) {
		getTaxableItem().setItemGuid(itemId);
	}

	@Override
	public BigDecimal getDiscount() {
		return getTaxableItem().getDiscount();
	}

	@Override
	public void applyDiscount(final BigDecimal discount) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getItemCode() {
		return getTaxableItem().getItemCode();
	}

	@Override
	public DiscountableTaxItem getTaxableItem() {
		return taxableItem;
	}

	public void setTaxableItem(final TaxableItem taxableItem) {
		this.taxableItem = (DiscountableTaxItem) taxableItem;
	}

	/**
	 * Add the tax amount included in the price.
	 *
	 * @param amount the tax amount included in the price
	 */
	public void addTaxInPrice(final BigDecimal amount) {
		this.taxInPrice = this.taxInPrice.add(amount);
	}

	@Override
	public BigDecimal getTaxInPrice() {
		return taxInPrice;
	}

	@Override
	public String getFieldValue(final String name) {
		return getTaxableItem().getFieldValue(name);
	}

	@Override
	public Map<String, String> getFields() {
		return getTaxableItem().getFields();
	}

	@Override
	public void setFieldValue(final String name, final String value) {
		getTaxableItem().setFieldValue(name, value);
	}

	@Override
	public int getQuantity() {
		return getTaxableItem().getQuantity();
	}

	@Override
	public void setQuantity(final int quantity) {
		getTaxableItem().setQuantity(quantity);
	}

	@Override
	public String getItemDescription() {
		return getTaxableItem().getItemDescription();
	}

	@Override
	public void setItemDescription(final String itemDescription) {
		getTaxableItem().setItemDescription(itemDescription);
	}

	@Override
	public void setFields(final Map<String, String> fieldValues) {
		getTaxableItem().setFields(fieldValues);
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
