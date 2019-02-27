/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;

import com.google.common.collect.ImmutableSet;
import org.apache.log4j.Logger;

import com.elasticpath.domain.catalog.PriceTier;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.domain.shoppingcart.DiscountRecord;

/**
 * <code>PriceTier</code> represents different price for different minimum quantity A <code>Price</code> should at least have a
 * <code>PriceTier</code> When shopping a product, if quantity is provided, <code>Price</code> should get the nearest <code>PriceTier </code>
 * with the minimum quantity equal or less than the given quantity. If no quantity is provided, the <code>Price</code> will retrieve the first
 * price tier, i.e. the one with smallest minimum quantity.
 */
public class PriceTierImpl extends AbstractEpDomainImpl implements PriceTier {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000002L;

	private static final Logger LOG = Logger.getLogger(PriceTierImpl.class);

	private int minQty;

	private BigDecimal listPrice;

	private BigDecimal salePrice;

	private BigDecimal computedPrice;
	
	private String priceListGuid;

	private final Collection<DiscountRecord> discountRecords = new HashSet<>();

	@Override
	public BigDecimal getComputedPrice() {
		return computedPrice;
	}

	@Override
	public void setComputedPriceIfLower(final BigDecimal computedPrice) {
		// Don't set the computed price unless it is lower than the current price
		if (this.computedPrice != null && computedPrice != null
				&& this.computedPrice.compareTo(computedPrice) < 0) {
			return;
		}

		// If the given price is less than zero, set the computed price to zero
		this.computedPrice = getNonNegativePrice(computedPrice);
	}

	private BigDecimal getNonNegativePrice(final BigDecimal adjustedPrice) {
		if (adjustedPrice != null && adjustedPrice.compareTo(BigDecimal.ZERO) < 0) {
			LOG.warn("Attempt to set a negative price. Price set to zero instead.");
			return BigDecimal.ZERO;
		}
		return adjustedPrice;
	}

	@Override
	public Collection<DiscountRecord> getDiscountRecords() {
		return ImmutableSet.copyOf(discountRecords);
	}

	@Override
	public void addDiscountRecord(final DiscountRecord discountRecord) {
		discountRecords.add(discountRecord);
	}

	@Override
	public void clearDiscountRecords() {
		discountRecords.clear();
	}

	@Override
	public BigDecimal getListPrice() {
		return listPrice;
	}

	@Override
	public void setListPrice(final BigDecimal listPrice) {
		// If the given price is less than zero, set the computed price to zero
		this.listPrice = getNonNegativePrice(listPrice);
	}

	@Override
	public int getMinQty() {
		return minQty;
	}

	@Override
	public void setMinQty(final int minQty) {
		this.minQty = minQty;
	}

	@Override
	public BigDecimal getSalePrice() {
		return salePrice;
	}

	@Override
	public void setSalePrice(final BigDecimal salePrice) {
		// If the given price is less than zero, set the computed price to zero
		this.salePrice = getNonNegativePrice(salePrice);
	}

	@Override
	public void clearComputedPrice() {
		this.computedPrice = null;
	}

	/**
	 * set default values for price tier. This method will be used by JUnit.
	 */
	@Override
	public void initialize() {
		setMinQty(1);
	}

	@Override
	public BigDecimal getPrePromotionPrice() {
		if (getSalePrice() == null) {
			return getListPrice();
		}
		return getSalePrice();
	}

	@Override
	public Integer getMinQtyAsInteger() {
		return Integer.valueOf(getMinQty());
	}

	@Override
	public void setMinQtyAsInteger(final Integer minQty) {
		if (minQty != null) {
			setMinQty(minQty.intValue());
		}
	}
	
	@Override
	public BigDecimal getLowestPrice() {
		BigDecimal lower = getLower(getSalePrice(), getListPrice());
		return getLower(lower, getComputedPrice());
	}

	private BigDecimal getLower(final BigDecimal price1, final BigDecimal price2) {
		if (price1 == null && price2 == null) {
			return null;
		} else if (price1 != null && price2 == null) {
			return price1;
		} else if (price1 == null) {
			return price2;
		} else if (price1.compareTo(price2) > 0) {
			return price2;
		} else {
			return price1;
		}		
	}

	/**
	 * Compare the lowestPrice against another PriceTier.
	 * @param other the PriceTier to compare against.
	 * @return -1, 0, or 1
	 */
	@Override
	public int compareTo(final PriceTier other) {
		final BigDecimal lowestPrice = getLowestPrice();
		final BigDecimal otherLowestPrice = other.getLowestPrice();

		if (lowestPrice == null) {
			if (otherLowestPrice == null) {
				return 0;
			}
			return -1;
		}
		if (otherLowestPrice == null) {
			return 1;
		}

		return lowestPrice.compareTo(otherLowestPrice);
	}

	@Override
	public String getPriceListGuid() {
		return priceListGuid;
	}

	@Override
	public void setPriceListGuid(final String priceListGuid) {
		this.priceListGuid = priceListGuid;
	}

}
