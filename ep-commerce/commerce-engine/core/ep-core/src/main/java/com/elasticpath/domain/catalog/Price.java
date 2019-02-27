/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog;

import java.util.Collection;
import java.util.Currency;
import java.util.Map;
import java.util.SortedMap;

import com.elasticpath.domain.EpDomain;
import com.elasticpath.domain.shoppingcart.DiscountRecord;
import com.elasticpath.money.Money;

/**
 * Interface implemented by classes representing product or service prices.
 */
public interface Price extends EpDomain, SimplePrice {

	/**
	 * Get the product's list price.
	 *
	 * @return the list price as a <code>Money</code>
	 */
	Money getListPrice();

	/**
	 * Set the product's list price.
	 *
	 * @param listPrice the product's list price as a <code>Money</code>
	 */
	void setListPrice(Money listPrice);

	/**
	 * Get the product's sale price.
	 *
	 * @return the sale price as a <code>Money</code> Returns null if there is no sale price specified
	 */
	Money getSalePrice();

	/**
	 * Set the product's sale price.
	 *
	 * @param salePrice the product's sale price as a <code>Money</code>
	 */
	void setSalePrice(Money salePrice);

	/**
	 * Get the product's computed price (e.g. the result of executing a rule).
	 *
	 * @return the sale price as a <code>Money</code> Returns null if no computed price has been set
	 */
	Money getComputedPrice();

	/**
	 * Get the {@link DiscountRecord}s representing the promotions that were valid for this price.
	 *
	 * @return the corresponding {@link DiscountRecord}s, or an empty collection if none present; never {@code null}
	 */
	Collection<DiscountRecord> getDiscountRecords();

	/**
	 * Removes the computed price, along with the discount record, if present.
	 */
	void clearComputedPrice();

	/**
	 * Set the product's computed price (e.g. the result of executing a rule).
	 *
	 * @param computedPrice the product's computed price as a <code>Money</code>
	 */
	void setComputedPriceIfLower(Money computedPrice);

	/**
	 * Adds a {@link DiscountRecord} representing a promotion that was valid for this price tier.
	 *
	 * @param discountRecord a discount record
	 */
	void addDiscountRecord(DiscountRecord discountRecord);

	/**
	 * Clears any {@link DiscountRecord} applied to this price via {@link #addDiscountRecord(DiscountRecord)}.
	 */
	void clearDiscountRecords();

	/**
	 * Set the currency for the price.
	 *
	 * @param currency of the price as a <code>Currency</code>
	 */
	void setCurrency(Currency currency);

	/**
	 * Set the product's list price.
	 *
	 * @param listPrice the product's list price as a <code>Money</code>
	 * @param minQty the minimum quantity of the <code>PriceTier</code>
	 */
	void setListPrice(Money listPrice, int minQty);

	/**
	 * Set the product's sale price.
	 *
	 * @param salePrice the product's sale price as a <code>Money</code>
	 * @param minQty the minimum quantity of the <code>PriceTier</code>
	 */
	void setSalePrice(Money salePrice, int minQty);

	/**
	 * Set the product's computed price (e.g. the result of executing a rule).
	 * A computed price cannot be less than zero. If a negative price is specified,
	 * the computed price will be set to zero.
	 *
	 * @param computedPrice the product's computed price as a <code>Money</code>
	 * @param minQty the minimum quantity of the <code>PriceTier</code>
	 */
	void setComputedPriceIfLower(Money computedPrice, int minQty);

	/**
	 * Adds a {@link DiscountRecord} representing a promotion that was valid for this price tier, for the given quantity.
	 *
	 * @param discountRecord a discount record
	 * @param minQty the minimum quantity of the <code>PriceTier</code>
	 */
	void addDiscountRecord(DiscountRecord discountRecord, int minQty);

	/**
	 * Get the sorted price Tiers.
	 *
	 * @return the map of price tiers for this product price as a <code>PriceTierImpl</code>
	 */
	SortedMap<Integer, PriceTier> getPriceTiers();


	/**
	 * This method will get persistent price tiers. It is mainly called by JPA.
	 * @return the persisted map of price tiers for this product price as a <code>PriceTierImpl</code>
	 */
	Map<Integer, PriceTier> getPersistentPriceTiers();


	/**
	 * Set the price Tiers to member variable which is mainly call by JPA.
	 *
	 * @param priceTiers the price tiers as a map of <code>PriceTier</code>
	 */
	void setPersistentPriceTiers(Map<Integer, PriceTier> priceTiers);


	/**
	 * add the price Tiers.
	 *
	 * @param priceTier the price tier to be added
	 */
	void addOrUpdatePriceTier(PriceTier priceTier);

	/**
	 * Check if the product has price tiers.
	 *
	 * @return true if the product has more than one price tier, or no quantity one price tier
	 */
	boolean hasPriceTiers();

	/**
	 * Check if the lowest price is less than the list price, i.e. the price has a discount.
	 *
	 * @return true if the price has a lower price than the list price.
	 */
	boolean isLowestLessThanList();

	/**
	 * Calculates the <code>Money</code> savings if the price has a discount.
	 *
	 * @return the price savings as a <code>Money</code>
	 */
	Money getDollarSavings();

	/**
	 * Get the Price tier with the same minimum quantity with the provided min Qty.
	 *
	 * @param minQty the minimum quantity to be matched
	 * @return the matched <code>PriceTier</code>
	 */
	PriceTier getPriceTierByExactMinQty(int minQty);

	/**
	 * Get the pre-promotion price of the first price tier to which promotions are to be applied.
	 * This is currently the lower of the sale price and the list price.
	 *
	 * @return a <code>Money</code> object representing the pre-promotion price
	 */
	Money getPrePromotionPrice();

	/**
	 * Update the sorted price tiers to synchronize with the persistent price tiers.
	 */
	void updateSortedPriceTiers();

	/**
	 * Get the pricing scheme associated with this price object.
	 *
	 * @return the {@link PricingScheme}
	 */
	PricingScheme getPricingScheme();

	/**
	 * Set the pricing scheme associated with this price object.
	 *
	 * @param pricingScheme the {@link PricingScheme} to set
	 */
	void setPricingScheme(PricingScheme pricingScheme);

	/**
	 *  Gets the PriceTier based on a quantity.
	 * @param qty the price tier minimum quantity
	 * @return The price tier which has a minimum quantity of <code>qty</code>
	 */
	PriceTier getPriceTierByQty(int qty);


	/**
	 * Get the lowest of the price values specified in this <code>Price</code>.
	 *
	 * @param qty the quantity of the product
	 * @return the lowest price as a <code>Money</code>
	 */
	@Override
	Money getLowestPrice(int qty);

}
