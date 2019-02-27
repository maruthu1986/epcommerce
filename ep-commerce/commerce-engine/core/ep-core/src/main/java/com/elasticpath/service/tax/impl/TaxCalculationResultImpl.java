/*
 * Copyright (c) Elastic Path Software Inc., 2006-2014
 */
package com.elasticpath.service.tax.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.money.Money;
import com.elasticpath.plugin.tax.domain.TaxDocument;
import com.elasticpath.service.tax.TaxCalculationResult;

/**
 * Represents the results of a tax calculation.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.GodClass" })
public class TaxCalculationResultImpl extends AbstractEpDomainImpl
implements TaxCalculationResult {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	private final Map<TaxCategory, Money> taxValues = new HashMap<>();
	private final Map<String, Money> taxAmountPerLineItem = new HashMap<>();
	private boolean taxInclusive;
	private Money taxInItemPrice;
	private Money beforeTaxShippingCost;
	private Money shippingTaxes;
	private Money beforeTaxSubTotal;
	private Currency defaultCurrency;
	private Money totalItemTax;

	private Money beforeTaxSubTotalWithoutDiscount;
	private TaxDocument taxDocument;

	private static final Logger LOG = Logger.getLogger(
			TaxCalculationResultImpl.class);

	/**
	 * Adds the given amount of tax to the specified <code>TaxCategory</code>.
	 * @param taxCategory the <code>TaxCategory</code> to add taxes to
	 * @param amount the amount of tax to add
	 */
	@Override
	public void addTaxValue(final TaxCategory taxCategory, final Money amount) {
		Money original = taxValues.get(taxCategory);
		if (null == original) {
			taxValues.put(taxCategory, amount);
		} else {
			taxValues.put(taxCategory, original.add(amount));
		}
	}
	
	/**
	 * Adds the given amount of tax to the ShippingTax.
	 *
	 * @param shippingTax the shipping tax
	 */
	@Override
	public void addShippingTax(final Money shippingTax) {
		shippingTaxes = getShippingTax().add(shippingTax);
	}

	/**
	 * Retrieves the shipping tax.
	 *
	 * @return the shipping tax
	 */
	@Override
	public Money getShippingTax() {
		if (shippingTaxes == null) {
			shippingTaxes = getMoneyZero();
		}
		return shippingTaxes;
	}

	/**
	 * Retrieves the amount of tax that will be charged for the specified
	 * <code>TaxCategory</code>.
     *
	 * @param taxCategory the <code>TaxCategory</code> to retrieve taxes for
	 * @return the amount of taxes for that category
	 */
	@Override
	public Money getTaxValue(final TaxCategory taxCategory) {
		Money taxValue = taxValues.get(taxCategory);
		if (null == taxValue) {
			taxValue = getMoneyZero();
		}
		return taxValue;
	}

	/**
	 * Retrieves the mapping of TaxCategories to values.
     *
	 * @return the map of TaxCategories to values.
	 */
	@Override
	public Map<TaxCategory, Money> getTaxMap() {
		Map<TaxCategory, Money> taxMap = new HashMap<>();
		for (TaxCategory taxCategory : getTaxCategoriesSet()) {
			taxMap.put(taxCategory, getTaxValue(taxCategory));
		}
		return taxMap;
	}

	/**
	 * Retrieves an iterator of the <code>TaxCategory</code>s in this
	 * <code>TaxCalculationResult</code>.
     *
	 * @return an iterator of the <code>TaxCategory</code>s in this
	 * <code>TaxCalculationResult</code>
	 */
	@Override
	public Iterator<TaxCategory> getTaxCategoriesIterator() {
		return getTaxCategoriesSet().iterator();
	}

	/**
	 * Retrives a <code>Set</code> of the <code>TaxCategory</code>s in this
	 * <code>TaxCalculationResult</code>.
     *
	 * @return a <code>Set</code> of the <code>TaxCategory</code>s in this
	 * <code>TaxCalculationResult</code>.
	 */
	@Override
	public Set<TaxCategory> getTaxCategoriesSet() {
		return taxValues.keySet();
	}

	/**
	 * Retrieves an iterator of the <code>Money</code> values referenced by
	 * <code>TaxCategory</code>s in this <code>TaxCalculationResult</code>.
     *
	 * @return the <code>Money</code>s
	 */
	public Iterator<Money> getTaxValueIterator() {
		return taxValues.values().iterator();
	}

	/**
	 * Sets whether or not the taxes were calculated for a price-inclusive
	 * tax jurisdiction.
     *
	 * @param taxInclusive whether or not the tax jurisdiction is price
	 * inclusive
	 */
	@Override
	public void setTaxInclusive(final boolean taxInclusive) {
		this.taxInclusive = taxInclusive;
	}

	/**
	 * Retrieves whether or not the taxes were calculated for a price-inclusive
	 * tax jurisdiction.
     *
	 * @return whether or not the tax jurisdiction is price
	 * inclusive
	 *
	 */
	@Override
	public boolean isTaxInclusive() {
		return taxInclusive;
	}

	/**
	 * Retrieves the total amount of tax included in the item prices.
     *
	 * @return the amount of tax in the item price
	 */
	@Override
	public Money getTaxInItemPrice() {
		if (taxInItemPrice == null) {
			taxInItemPrice = getMoneyZero();
		}
		return taxInItemPrice;
	}

	/**
	 * Adds to the total amount of tax included in the item prices.
     *
	 * @param taxInItemPrice the amount of tax to add
	 */
	@Override
	public void addToTaxInItemPrice(final Money taxInItemPrice) {
		if (this.taxInItemPrice == null) {
			this.taxInItemPrice = taxInItemPrice;
			return;
		}
		this.taxInItemPrice = this.taxInItemPrice.add(taxInItemPrice);
	}

	/**
	 * Retrieves the cost of shipping before tax.
     *
	 * @return the cost of shipping before tax
	 */
	@Override
	public Money getBeforeTaxShippingCost() {
		if (beforeTaxShippingCost == null) {
			setBeforeTaxShippingCost(getMoneyZero());
		}

		return beforeTaxShippingCost;
	}

	/**
	 * Sets the cost of shipping before tax.
     *
	 * @param beforeTaxShippingCost the cost of shipping before tax
	 */
	@Override
	public void setBeforeTaxShippingCost(final Money beforeTaxShippingCost) {
		this.beforeTaxShippingCost = beforeTaxShippingCost;
	}

	/**
	 * Returns the value of the shopping cart before tax.
     *
	 * @return the value of the shopping cart before tax
	 */
	@Override
	public Money getBeforeTaxSubTotal() {
		if (beforeTaxSubTotal == null) {
			setBeforeTaxSubTotal(getMoneyZero());
		}

		return beforeTaxSubTotal;
	}

	/**
	 * Returns the value of the shopping cart before tax without the discount applied.
	 *
	 * @return the value of the shopping cart before tax without the discount applied
	 */
	@Override
	public Money getBeforeTaxSubTotalWithoutDiscount() {
		if (beforeTaxSubTotalWithoutDiscount == null) {
			setBeforeTaxSubTotalWithoutDiscount(getMoneyZero());
		}

		return beforeTaxSubTotalWithoutDiscount;
	}

	/**
	 * Sets the before tax subtotal without the discount applied.
	 *
	 * @param amount the amount to be set
	 */
	@Override
	public void setBeforeTaxSubTotalWithoutDiscount(final Money amount) {
		beforeTaxSubTotalWithoutDiscount = amount;
	}

	/**
	 * Sets the value of the shopping cart before tax.
     *
	 * @param beforeTaxSubTotal the value of the shopping cart before tax
	 */
	@Override
	public void setBeforeTaxSubTotal(final Money beforeTaxSubTotal) {
		this.beforeTaxSubTotal = beforeTaxSubTotal;
	}

	/**
	 * Retrieves the total amount of taxes.
     *
	 * @return the total amount of taxes
	 */
	@Override
	public Money getTotalTaxes() {
		LOG.debug("Entered getTotalTaxes()");
		Money total = getMoneyZero();
		for (Iterator<Money> taxValueIterator = getTaxValueIterator();
				taxValueIterator.hasNext();) {
			Money currentMoney = taxValueIterator.next();
			if (null != currentMoney) {
				total = total.add(currentMoney);
			}
		}
		return total;
	}

	/**
	 * Sets the default currency for this <code>TaxCalculationResult</code>.
	 * This value will be used when there is not enough information to
	 * determine the currency.
     *
	 * @param defaultCurrency the default currency to use
	 */
	@Override
	public void setDefaultCurrency(final Currency defaultCurrency) {
		this.defaultCurrency = defaultCurrency;
	}

	/**
	 * Creates a Money object with zero amount and default currency.
	 *
	 * @return a zero Money instance
	 */
	Money getMoneyZero() {
		LOG.debug("Entered getMoneyZero()");
		if (null == defaultCurrency) {
			throw new EpServiceException("Default currency for the tax result is not set");
		}

		return Money.valueOf(BigDecimal.ZERO.setScale(defaultCurrency.getDefaultFractionDigits()), defaultCurrency);
	}

	@Override
	public int hashCode() {
		return Objects.hash(beforeTaxShippingCost, beforeTaxSubTotal, defaultCurrency, shippingTaxes, taxInItemPrice,
			taxInclusive, taxValues);
	}

	/**
	 * Implements equals semantics.<br>
	 * This class more than likely would be extended to add functionality that would not effect the equals method in comparisons, and as such would
	 * act as an entity type. In this case, content is not crucial in the equals comparison. Using instanceof within the equals method enables
	 * comparison in the extended classes where the equals method can be reused without violating symmetry conditions. If getClass() was used in the
	 * comparison this could potentially cause equality failure when we do not expect it. If when extending additional fields are included in the
	 * equals method, then the equals needs to be overridden to maintain symmetry.
	 *
	 * @param obj the other object to compare
	 * @return true if equal
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof TaxCalculationResultImpl)) {
			return false;
		}

		final TaxCalculationResultImpl other = (TaxCalculationResultImpl) obj;
		return Objects.equals(getBeforeTaxShippingCost(), other.getBeforeTaxShippingCost())
			&& Objects.equals(getBeforeTaxSubTotal(), other.getBeforeTaxSubTotal())
			&& Objects.equals(defaultCurrency, other.defaultCurrency)
			&& Objects.equals(shippingTaxes, other.shippingTaxes)
			&& Objects.equals(getTaxInItemPrice(), other.getTaxInItemPrice())
			&& Objects.equals(isTaxInclusive(), other.isTaxInclusive())
			&& Objects.equals(taxValues, other.taxValues);
	}

	/**
	 * Gets the total item tax collected.
	 *
	 * @return amount of taxes
	 */
	@Override
	public Money getTotalItemTax() {
		//all taxes are scaled to 2 and rounded half up when added to the result.
		totalItemTax = getMoneyZero();
		for (Money itemTaxAmount : taxAmountPerLineItem.values()) {
			totalItemTax = totalItemTax.add(itemTaxAmount);
		}
		return totalItemTax;
	}


	/**
	 * Sets a tax amount for the line item identified by itemId
	 *
	 * <p>Note: {@link #addTaxValue(TaxCategory, com.elasticpath.money.Money)} must be called
	 * as well. Otherwise {@link #getTotalTaxes()} will not return correct amount of taxes.
	 *
	 * @param amount the amount to be added
	 * @param itemId the id of the associated line item
	 */
	@Override
	public void addItemTax(final String itemId, final Money amount) {
		taxAmountPerLineItem.put(itemId, amount);
	}

	@Override
	public Money getLineItemTax(final String itemId) {
		return taxAmountPerLineItem.get(itemId);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
			.append("taxInclusive", isTaxInclusive())
			.append("defaultCurrency", defaultCurrency)
			.append("beforeTaxShippingCost", getBeforeTaxShippingCost())
			.append("shippingTax", getShippingTax())
			.append("totalItemTax", getTotalItemTax())
			.append("taxInItemPrice", getTaxInItemPrice())
			.append("beforeTaxSubTotal", getBeforeTaxSubTotal())
			.toString();
	}

	@Override
	public void addBeforeTaxItemPrice(final Money value) {
		setBeforeTaxSubTotal(getBeforeTaxSubTotal().add(value));
	}

	@Override
	public void addBeforeTaxWithoutDiscount(final Money value) {
		setBeforeTaxSubTotalWithoutDiscount(getBeforeTaxSubTotalWithoutDiscount().add(value));
	}

	@Override
	public void addBeforeTaxShippingCost(final Money value) {
		setBeforeTaxShippingCost(getBeforeTaxShippingCost().add(value));
	}

	/**
	 * Calculates the item subtotal.
	 * <p>
	 * For tax inclusive this is the before tax subtotal without discount plus the item taxes.
	 * <p>
	 * For tax exclusive this is the before tax subtotal without discount only.
	 *
	 * @return the money of the items subtotal
	 */
	@Override
	public Money getSubtotal() {
		Money beforeTaxSubTotal = getBeforeTaxSubTotal();
		Money result = beforeTaxSubTotal;
		if (isTaxInclusive() && getTotalItemTax() != null) {
			result = beforeTaxSubTotal.add(getTotalItemTax());
		}
		return result;
	}

	@Override
	public void applyTaxes(final Collection<? extends ShoppingItem> lineItems) {
		if (lineItems == null) {
			return;
		}
		for (ShoppingItem lineItem : lineItems) {
			// On Order exchange wizard tax calculation may be invoked
			// on item without unit price
			if (!lineItem.hasPrice()) {
				continue;
			}
			Money taxAmount = getLineItemTax(lineItem.getGuid());
			if (taxAmount == null) {
				throw new IllegalArgumentException("LineItem tax not present in TaxCalculationResult for LineItem with guid " + lineItem.getGuid());
			}
			lineItem.setTaxAmount(taxAmount.getAmount());
		}
	}
	
	@Override
	public TaxDocument getTaxDocument() {
		return taxDocument;
	}
	
	@Override
	public void setTaxDocument(final TaxDocument document) {
		this.taxDocument = document;
	}

	@Override
	public void initialize() {
	
		if (defaultCurrency == null) {
			return;
		}
		
		this.beforeTaxShippingCost = getMoneyZero();
		this.beforeTaxSubTotal = getMoneyZero();
		this.shippingTaxes = getMoneyZero();
		this.taxInclusive = false;
		this.taxInItemPrice = getMoneyZero();
		this.totalItemTax = getMoneyZero();
	}
	
	@Override
	public void initialize(final Currency currency) {
		this.defaultCurrency = currency;
		initialize();
	}

}
