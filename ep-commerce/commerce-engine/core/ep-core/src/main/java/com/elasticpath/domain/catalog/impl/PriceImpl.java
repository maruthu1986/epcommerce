/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceTier;
import com.elasticpath.domain.catalog.PricingScheme;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.domain.shoppingcart.DiscountRecord;
import com.elasticpath.money.Money;

/**
 * The implementation of <code>Price</code>.
 */
@SuppressWarnings("PMD.GodClass")
public class PriceImpl extends AbstractEpDomainImpl implements Price {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000002L;

	private Currency currency;

	private Map<Integer, PriceTier> priceTiers;

	private SortedMap<Integer, PriceTier> sortedPriceTiers;

	private PricingScheme pricingScheme;

	@Override
	public Money getListPrice(final int qty) {
		Money money = null;
		BigDecimal listPrice = null;

		PriceTier priceTier = getPriceTierByQty(qty);
		if (priceTier != null) {
			listPrice = priceTier.getListPrice();
			if (listPrice != null) {
				money = Money.valueOf(listPrice, getCurrency());
			}
		}

		return money;
	}

	@Override
	public Money getListPrice() {
		return getListPrice(getFirstPriceTierMinQty());
	}

	@Override
	public void setListPrice(final Money listPrice, final int minQty) {
		if (listPrice == null) {
			throw new EpDomainException("Invalid list price");
		}
		checkCurrencyMatch(listPrice.getCurrency());
		setCurrency(listPrice.getCurrency());

		PriceTier priceTier = getPriceTierByExactMinQty(minQty);

		if (priceTier == null) {
			priceTier = initializePriceTier();
			priceTier.setListPrice(listPrice.getAmount());
			priceTier.setMinQty(minQty);
			addOrUpdatePriceTier(priceTier);
		} else {
			priceTier.setListPrice(listPrice.getAmount());
		}
	}

	/**
	 * Initialize the price tier.
	 *
	 * @return the initialized price tier object
	 */
	protected PriceTier initializePriceTier() {
		PriceTier priceTier = new PriceTierImpl();
		priceTier.initialize();
		return priceTier;
	}

	@Override
	public PriceTier getPriceTierByExactMinQty(final int minQty) {
		if (this.getPersistentPriceTiers() != null) {
			return this.getPersistentPriceTiers().get(Integer.valueOf(minQty));
		}
		return null;
	}

	@Override
	public PriceTier getPriceTierByQty(final int qty) {
		PriceTier nearestTier = null;
		if (this.getPriceTiers() != null) {
			for (PriceTier priceTier : this.getPriceTiers().values()) { // the priceTiers set should be sorted by ascending qty
				if (priceTier.getMinQty() <= qty) {
					nearestTier = priceTier;
				}
			}
		}
		return nearestTier;
	}

	@Override
	public int getFirstPriceTierMinQty() {
		int firstMinQty = 1;
		SortedMap<Integer, PriceTier> priceTiers =  this.getPriceTiers();
		if (priceTiers != null && !priceTiers.isEmpty()) {
			firstMinQty = priceTiers.values().iterator().next().getMinQty();
		}
		return firstMinQty;
	}

	@Override
	public void setListPrice(final Money listPrice) {
		int firstTierMinQty = getFirstPriceTierMinQty();
		setListPrice(listPrice, firstTierMinQty);
	}

	@Override
	public Money getSalePrice(final int qty) {

		Money money = null;
		BigDecimal salePrice = null;

		PriceTier priceTier = getPriceTierByQty(qty);

		if (priceTier != null) {
			salePrice = priceTier.getSalePrice();
			if (salePrice != null) {
				money = Money.valueOf(salePrice, getCurrency());
			}
		}

		return money;
	}

	@Override
	public Money getSalePrice() {
		return getSalePrice(getFirstPriceTierMinQty());
	}

	@Override
	public void setSalePrice(final Money salePrice, final int minQty) {
		if (salePrice == null) {
			throw new EpDomainException("Invalid sale price");
		}
		checkCurrencyMatch(salePrice.getCurrency());
		setCurrency(salePrice.getCurrency());

		PriceTier priceTier = getPriceTierByExactMinQty(minQty);
		// if (priceTier == null) {
		// throw new EpDomainException("No price tier found for quantity : " + minQty);
		// }
		if (priceTier == null) {
			priceTier = initializePriceTier();
			priceTier.setSalePrice(salePrice.getAmount());
			priceTier.setMinQty(minQty);
			addOrUpdatePriceTier(priceTier);
		} else {
			priceTier.setSalePrice(salePrice.getAmount());
		}
	}

	@Override
	public void setSalePrice(final Money salePrice) {
		int firstTierMinQty = getFirstPriceTierMinQty();
		setSalePrice(salePrice, firstTierMinQty);
	}

	@Override
	public Money getComputedPrice(final int qty) {
		Money money = null;
		BigDecimal computedPrice = null;

		PriceTier priceTier = getPriceTierByQty(qty);

		if (priceTier != null) {
			computedPrice = priceTier.getComputedPrice();
			if (computedPrice != null) {
				money = Money.valueOf(computedPrice, getCurrency());
			}
		}

		return money;
	}

	@Override
	public Money getComputedPrice() {
		return getComputedPrice(getFirstPriceTierMinQty());
	}

	@Override
	public void setComputedPriceIfLower(final Money computedPrice) {
		int firstTierMinQty = getFirstPriceTierMinQty();
		setComputedPriceIfLower(computedPrice, firstTierMinQty);
	}

	@Override
	public void setComputedPriceIfLower(final Money computedPrice, final int minQty) {
		if (computedPrice == null) {
			throw new EpDomainException("Invalid computed price");
		}
		checkCurrencyMatch(computedPrice.getCurrency());

		setCurrency(computedPrice.getCurrency());
		PriceTier priceTier = getPriceTierByExactMinQty(minQty);
		priceTier.setComputedPriceIfLower(computedPrice.getAmount());
	}

	@Override
	public void clearComputedPrice() {
		for (PriceTier priceTier : priceTiers.values()) {
			priceTier.clearComputedPrice();
		}
	}

	@Override
	public Collection<DiscountRecord> getDiscountRecords(final int qty) {
		final PriceTier priceTier = getPriceTierByQty(qty);

		if (priceTier != null) {
			return priceTier.getDiscountRecords();
		}

		return Collections.emptySet();
	}

	@Override
	public Collection<DiscountRecord> getDiscountRecords() {
		return getDiscountRecords(getFirstPriceTierMinQty());
	}

	@Override
	public void addDiscountRecord(final DiscountRecord discountRecord) {
		addDiscountRecord(discountRecord, getFirstPriceTierMinQty());
	}

	@Override
	public void addDiscountRecord(final DiscountRecord discountRecord, final int minQty) {
		final PriceTier priceTier = getPriceTierByQty(minQty);

		if (priceTier != null) {
			priceTier.addDiscountRecord(discountRecord);
		}
	}

	@Override
	public void clearDiscountRecords() {
		for (final PriceTier priceTier : priceTiers.values()) {
			priceTier.clearDiscountRecords();
		}
	}

	@Override
	public Money getLowestPrice(final int qty) {
		Money lowestPrice = this.getListPrice(qty);

		final Money salePrice = this.getSalePrice(qty);
		if (salePrice != null && salePrice.lessThan(lowestPrice)) {
			lowestPrice = salePrice;
		}

		final Money computedPrice = this.getComputedPrice(qty);
		if (computedPrice != null && computedPrice.lessThan(lowestPrice)) {
			lowestPrice = computedPrice;
		}

		return lowestPrice;
	}

	@Override
	public Money getLowestPrice() {
		return getLowestPrice(getFirstPriceTierMinQty());
	}

	@Override
	public Currency getCurrency() {
		if (this.currency == null && pricingScheme != null) {
			return pricingScheme.getCurrency();
		}
		return this.currency;
	}

	@Override
	public void setCurrency(final Currency currency) {
		this.currency = currency;
	}

	/**
	 * Checks that a new price matches any previously set currency. If it does not match, an exception is thrown because this price could be
	 * incorrectly mapped by the previous currency.
	 *
	 * @param newCurrency the new currency to check
	 */
	private void checkCurrencyMatch(final Currency newCurrency) {
		if (getCurrency() != null && !newCurrency.equals(getCurrency())) {
			throw new EpDomainException("New price does not match previously set currency");
		}
	}

	@Override
	public SortedMap<Integer, PriceTier> getPriceTiers() {
		if (sortedPriceTiers == null) {
			updateSortedPriceTiers();
		}
		return sortedPriceTiers;
	}


	@Override
	public void updateSortedPriceTiers() {
		if (getPersistentPriceTiers() == null) {
			this.sortedPriceTiers = null;
		} else {
			this.sortedPriceTiers = new TreeMap<>(getPersistentPriceTiers());
		}
	}


	/**
	 * {@inheritDoc}
	 *
	 * This method should be implemented in subclass, as the member variable is defined in this class,
	 * so we have not make this method abstract.
	 *
	 * @return a map of price tiers for this product price as a <code>PriceTierImpl</code>
	 */
	@Override
	public Map<Integer, PriceTier> getPersistentPriceTiers() {
		return priceTiers;
	}

	@Override
	public void setPersistentPriceTiers(final Map<Integer, PriceTier> priceTiers) {
		this.priceTiers = priceTiers;

		//Should access the variable directly rather than use get/set method. This is to avoid cyclic JPA calls.
		//this.updateSortedPriceTiers();
		if (priceTiers == null) {
			this.sortedPriceTiers = null;
		} else {
			this.sortedPriceTiers = new TreeMap<>(priceTiers);
		}
	}

	@Override
	public boolean hasPriceTiers() {
		// more than one price tier
		if (priceTiers != null && priceTiers.size() > 1) {
			return true;
		}

		// non-quantity-one price tier
		return priceTiers != null && !priceTiers.isEmpty() && Collections.min(priceTiers.keySet()) > 1;

	}

	@Override
	public void initialize() {
		if (this.priceTiers == null) {
			this.priceTiers = new HashMap<>();
		}
	}

	@Override
	public boolean isLowestLessThanList() {
		final Money lowestPrice = getLowestPrice();
		if (lowestPrice == null) {
			return false;
		}
		return lowestPrice.lessThan(getListPrice());
	}

	@Override
	public boolean isLowestLessThanList(final int qty) {
		final Money lowestPrice = getLowestPrice(qty);
		if (lowestPrice == null) {
			return false;
		}
		return lowestPrice.lessThan(getListPrice(qty));
	}

	@Override
	public Money getDollarSavings() {
		final Money listPrice = getListPrice();
		if (listPrice == null) {
			return null;
		}
		return listPrice.subtract(getLowestPrice());
	}

	@Override
	public Money getDollarSavings(final int qty) {
		if (qty >= getFirstPriceTierMinQty()) {
			return getListPrice(qty).subtract(getLowestPrice(qty));
		}

		return null;
	}

	@Override
	public Money getPrePromotionPrice() {
		Money prePromotionPrice = getSalePrice();
		if (prePromotionPrice == null) {
			prePromotionPrice = getListPrice();
		}
		return prePromotionPrice;
	}

	@Override
	public Money getPrePromotionPrice(final int qty) {
		Money prePromotionPrice = getSalePrice(qty);
		if (prePromotionPrice == null) {
			prePromotionPrice = getListPrice(qty);
		}
		return prePromotionPrice;
	}

	@Override
	public void addOrUpdatePriceTier(final PriceTier priceTier) {
		if (this.getPersistentPriceTiers() == null) {
			this.setPersistentPriceTiers(new HashMap<>());
		}
		this.getPersistentPriceTiers().put(Integer.valueOf(priceTier.getMinQty()), priceTier);
		this.updateSortedPriceTiers();
	}

	@Override
	public String toString() {
		ToStringBuilder priceBuilder = new ToStringBuilder(this, ToStringStyle.NO_FIELD_NAMES_STYLE);

		if (priceTiers != null) {
			for (final Map.Entry<Integer, PriceTier> priceTierEntry : priceTiers.entrySet()) {
				PriceTier tier = priceTierEntry.getValue();

				if (tier != null) {
					ToStringBuilder tierBuilder = new ToStringBuilder(tier, ToStringStyle.MULTI_LINE_STYLE);
					tierBuilder.append("minQty", tier.getMinQty());
					tierBuilder.append("listPrice", tier.getListPrice());
					tierBuilder.append("salePrice", tier.getSalePrice());
					tierBuilder.append("prePromotionPrice", tier.getPrePromotionPrice());
					tierBuilder.append("computedPrice", tier.getComputedPrice());

					priceBuilder.append(String.valueOf(priceTierEntry.getKey()), tierBuilder.toString());
				}
			}
		}

		return priceBuilder.toString();
	}

	@Override
	public PricingScheme getPricingScheme() {
		return this.pricingScheme;
	}

	@Override
	public void setPricingScheme(final PricingScheme pricingScheme) {
		this.pricingScheme = pricingScheme;
	}


	@Override
	public Set<Integer> getPriceTiersMinQuantities() {
		SortedMap<Integer, PriceTier> priceTiers = getPriceTiers();
		if (priceTiers == null) {
			return Collections.emptySet();
		}

		return priceTiers.keySet();
	}

}

