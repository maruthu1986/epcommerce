/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.common.pricing.service.impl;

import static java.util.Arrays.asList;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.common.pricing.service.BundleShoppingItemPriceBuilder;
import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceSchedule;
import com.elasticpath.domain.catalog.PriceTier;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.pricing.PriceAdjustment;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCartMessageIds;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.money.Money;
import com.elasticpath.sellingchannel.ProductUnavailableException;
import com.elasticpath.service.catalog.BundleIdentifier;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.pricing.PriceProvider;

/**
 * Implementation of BundlePriceCalculator strategy.
 *
 */
public class CalculatedBundleShoppingItemPriceBuilder implements
		BundleShoppingItemPriceBuilder {

	private final PriceLookupFacade priceLookupFacade;
	private final BeanFactory beanFactory;
	private final ProductSkuLookup productSkuLookup;
	private BundleIdentifier bundleIdentifier;
	
	/**
	 * A local cache of prices.
	 */
	private final Map<String, Price> priceMap = new HashMap<>();
	private Shopper shopper;
	private Store store;

	/**
	 * Constructor.
	 * @param priceLookupFacade price lookup facade.
	 * @param productSkuLookup a product sku lookup
	 * @param beanFactory bean factory.
	 */
	public CalculatedBundleShoppingItemPriceBuilder(
			final PriceLookupFacade priceLookupFacade, final ProductSkuLookup productSkuLookup,
			final BeanFactory beanFactory) {
		
		this.priceLookupFacade = priceLookupFacade;
		this.productSkuLookup = productSkuLookup;
		this.beanFactory = beanFactory;
	}

	/**
	 * Builds the <code>Price</code> object for the calculated bundle shopping item.
	 * <p/>
	 * The <code>Price</code> object will contain :
	 * 1. the list price as the sum of the constituent list prices
	 * 2. the sale price as the sum of the constituent sale prices
	 * 3. the computed price as the sum of the lowest prices plus their constituent adjustments [negative or positive],
	 * if applicable for the selected price list for that price tier
	 * <p/>
	 * <p/>
	 * The algorithm will :
	 * 1. iterate through the bundle items on the shoppingItem, calling this method recursively
	 * 2. if item is not a calculated bundle, get the promoted price for sku from the <code>PriceLookupFacade</code>
	 * 3. use this price to contribute to the calculated bundle price as per the price structure above
	 * The price adjustment at the item level is obtained from the <code>BundleConstituent</code> reference,
	 * which contains the map of PriceAdjustments
	 * <p/>
	 * Finally, one PriceTier is constructed using the results of the algorithm, and then set into a new Price object and returned.
	 *
	 * @param bundleShoppingItem ShoppingItem.
	 * @param shopper            CustomerSession.
	 * @param store              the store
	 * @return Price.
	 */
	@Override
	public Price build(final ShoppingItem bundleShoppingItem, final Shopper shopper, final Store store) {

		final ProductSku bundleSku = getProductSkuLookup().findByGuid(bundleShoppingItem.getSkuGuid());
		if (!getBundleIdentifier().isCalculatedBundle(bundleSku.getProduct())) {
			throw new EpSystemException("Only calculated bundles are supported by this method.");
		}

		this.shopper = shopper;
		this.store = store;

		ProductBundle bundle = (ProductBundle) bundleSku.getProduct();

		List<ShoppingItem> bundleItems = bundleShoppingItem.getBundleItems(getProductSkuLookup());

		//shoppingItemQuantity is the calculated bundle quantity (ex, 3 Sony Camera bundles with 2 Batteries equals 6 batteries in total.)
		int shoppingItemQuantity = bundleShoppingItem.getQuantity();

		for (ShoppingItem bundleItem : bundleItems) {
			Price itemPrice = null;
			final ProductSku bundleItemSku = getProductSkuLookup().findByGuid(bundleItem.getSkuGuid());
			if (getBundleIdentifier().isCalculatedBundle(bundleItemSku.getProduct())) {
				itemPrice = build(bundleItem, shopper, store);
				updateBundlePrice(bundleItem, itemPrice);
			} else {
				itemPrice = priceLookupFacade.getPromotedPriceForSku(bundleItemSku, store,
						shopper);

				if (itemPrice == null) {
					final ProductSku productSku = getProductSkuLookup().findByGuid(bundleItem.getSkuGuid());
					final String errorMessage = "Bundle constituent item has no price for bundle item " + productSku.getSkuCode();
					throw new ProductUnavailableException(
							errorMessage,
							asList(
									new StructuredErrorMessage(
											ShoppingCartMessageIds.ITEM_NOT_AVAILABLE,
											errorMessage,
											ImmutableMap.of("item-code", productSku.getSkuCode())
									)
							)
					);
				}

				updateItemPrice(bundle, bundleItem, shoppingItemQuantity, itemPrice);
			}

		}

		PricedCalculatedBundleForShoppingItem priced = new PricedCalculatedBundleForShoppingItem(
				bundleShoppingItem, bundle, beanFactory, getPriceProvider(), getProductSkuLookup());
		return priced.getPrice();
	}
	
	private void updateBundlePrice(final ShoppingItem bundleItem, final Price itemPrice) {
		bundleItem.setPrice(bundleItem.getQuantity(), itemPrice);
		priceMap.put(bundleItem.getSkuGuid(), itemPrice);
	}

	private PriceProvider getPriceProvider() {		
		return new PriceProvider() {

			@Override
			public Currency getCurrency() {
				return shopper.getCurrency();
			}

			@Override
			public Price getProductPrice(final Product product) {
				return null;
			}

			@Override
			public Price getProductSkuPrice(final ProductSku productSku) {
				return getSkuPrice(productSku);
			}
			
		};
	}

	/**
	 * 
	 * @param productSku ProductSku
	 * @return promoted price of the sku. 
	 */
	protected Price getSkuPrice(final ProductSku productSku) {
		Price price = priceMap.get(productSku.getGuid());
		if (price == null) {
			price = priceLookupFacade.getPromotedPriceForSku(productSku, store, shopper);
			priceMap.put(productSku.getGuid(), price);
		}
		return price;
	}

	/**
	 * This method will retrieve the lowest price for each item, multiplied by constituent quantity, added by constituent price adjustment.<br>
	 * 
	 * It is not recommended to extend and modify the behaviour of this method, independently of the calculate method within this class.
	 * 
	 * @param bundle the parent bundle.
	 * @param bundleItem the current shopping item.
	 * @param bundleQuantity the bundle quantity
	 * @param itemPrice the item price
	 */
	protected void updateItemPrice(final ProductBundle bundle, final ShoppingItem bundleItem, final int bundleQuantity, 
			final Price itemPrice) {
		
		Collection<PriceSchedule> schedules = itemPrice.getPricingScheme().getSchedules();
		if (schedules.size() != 1) {
			throw new EpSystemException("Bundle constituent item " + bundleItem.getGuid()
					+ " should have only one price schedule, but it does have " + schedules + " schedules!");
		}
		
		//bundleItemQuantity is the line item quantity already multiplied by the shoppingItemQuantity
		//ex. 3 bundles of 2 batteries = 6!  This was calculated elsewhere and passed in...
		int bundleItemQuantity = bundleItem.getQuantity();

		PriceSchedule schedule = schedules.iterator().next();
		Price effectivePrice = (Price) itemPrice.getPricingScheme().getSimplePriceForSchedule(schedule);
		PriceTier priceTier = effectivePrice.getPriceTierByQty(bundleItemQuantity);
		
		// This item is not available for this quantity!
		if (priceTier == null) {
			final ProductSku productSku = getProductSkuLookup().findByGuid(bundleItem.getSkuGuid());
			final String errorMessage = "Bundle constituent item has no price for quantity " + bundleItemQuantity;
			throw new ProductUnavailableException(
					errorMessage,
					asList(
							new StructuredErrorMessage(
									ShoppingCartMessageIds.ITEM_NOT_AVAILABLE,
									errorMessage,
									ImmutableMap.of("item-code", productSku.getSkuCode())
							)
					)
			);
		}
		
		BundleConstituent bundleConstituent = findBundleConstituentForShoppingItem(bundle, bundleItem);
		PriceAdjustment priceAdjustment = bundleConstituent.getPriceAdjustmentForPriceList(priceTier.getPriceListGuid());
		
		Money itemComputedPrice = effectivePrice.getLowestPrice(bundleItemQuantity);
		if (priceAdjustment != null) {
			// Use the minimum of either 0 or the adjustment because positive price adjustments are not 
			// allowed in calculated bundles.
			// Apply price adjustment to unit price.
			BigDecimal adjustedAmount = itemComputedPrice.getAmount().add(priceAdjustment.getAdjustmentAmount().min(BigDecimal.ZERO));

			if (adjustedAmount.compareTo(BigDecimal.ZERO) < 0) {
				adjustedAmount = BigDecimal.ZERO;
			}
			
			itemComputedPrice = Money.valueOf(adjustedAmount, itemComputedPrice.getCurrency());
		}
		
		// NOT forget to update the item price object, because the itemComputedPrice is transient and new object.		
		priceTier.setComputedPriceIfLower(itemComputedPrice.getAmount());
		
		// NOT forget to update the shopping item.		
		bundleItem.setPrice(bundleItemQuantity, itemPrice);
		
	}

	private BundleConstituent findBundleConstituentForShoppingItem(final ProductBundle bundle,
			final ShoppingItem shoppingItem) {
		
		for (BundleConstituent bundleConstituent : bundle.getConstituents()) {
			if (bundleConstituent.getOrdering() == shoppingItem.getOrdering()) {
				return bundleConstituent;
			}
		}
		
		throw new EpSystemException("Can't find the matching bundle constituent: " + shoppingItem.getSkuGuid());
	}

	/**
	 * @return the BundleIdentifier instance
	 */
	protected BundleIdentifier getBundleIdentifier() {
		return bundleIdentifier;
	}
	
	/**
	 * Set the {@link BundleIdentifier} instance.
	 * @param bundleIdentifier the bundleIdentifier instance to set
	 */
	public void setBundleIdentifier(final BundleIdentifier bundleIdentifier) {
		this.bundleIdentifier = bundleIdentifier;
	}

	protected ProductSkuLookup getProductSkuLookup() {
		return productSkuLookup;
	}
}
