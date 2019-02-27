/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.common.pricing.service.impl;

import static java.util.Arrays.asList;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.common.pricing.service.BundleShoppingItemPriceBuilder;
import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.commons.exception.InvalidProductStructureException;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceTier;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.pricing.PriceAdjustment;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCartMessageIds;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.sellingchannel.ProductUnavailableException;
import com.elasticpath.service.catalog.ProductSkuLookup;

/**
 * Implementation of BundlePriceCalculator strategy.
 *
 */
public class AssignedBundleShoppingItemPriceBuilder implements BundleShoppingItemPriceBuilder {

	/**
	 * 
	 */
	private final PriceLookupFacade priceLookupFacade;
	private final ProductSkuLookup productSkuLookup;

	/**
	 * @param priceLookupFacade PriceLookupFacade.
	 * @param productSkuLookup a product sku lookup
	 */
	AssignedBundleShoppingItemPriceBuilder(final PriceLookupFacade priceLookupFacade, final ProductSkuLookup productSkuLookup) {
		this.priceLookupFacade = priceLookupFacade;
		this.productSkuLookup = productSkuLookup;
	}

	@Override
	public Price build(final ShoppingItem bundleShoppingItem, final Shopper shopper, final Store store) {
		
		// Traverse through shoppingItem tree to find all bundle constituent guids corresponding to our shopping items.
		List<ShoppingItem> shoppingItems = bundleShoppingItem.getChildren();

		final ProductSku bundleSku = getProductSkuLookup().findByGuid(bundleShoppingItem.getSkuGuid());
		ProductBundle bundle = (ProductBundle) bundleSku.getProduct();
		
		List<BundleConstituent> constituents = bundle.getConstituents();

		Map<String, String> bundleConstituentGuids = new HashMap<>();
		populateMatchingBundleConstituentGuids(shoppingItems, constituents, bundleConstituentGuids);

		Map<String, PriceAdjustment> priceAdjustmentsForBundle = priceLookupFacade.getPriceAdjustmentsForBundle(bundle,
				store.getCatalog().getCode(), shopper);		
		
		BigDecimal sumOfAdjustments = sumAdjustments(priceAdjustmentsForBundle, bundleConstituentGuids.values());
		
		// With the adjustments collected, apply them to the root shoppingItem (bundle)
		Price promotedItemPrice = priceLookupFacade.getPromotedPriceForSku(bundleSku, store, shopper);
		
		adjustPrice(promotedItemPrice, sumOfAdjustments);
		
		return promotedItemPrice;
	}

	private BigDecimal sumAdjustments(final Map<String, PriceAdjustment> adjustments, final Collection<String> constituentsGuids) {
		BigDecimal sum = BigDecimal.ZERO;

		for (String guid : constituentsGuids) {
			PriceAdjustment priceAdjustment = adjustments.get(guid);
			if (priceAdjustment != null) {
				int relative = BigDecimal.ZERO.compareTo(priceAdjustment.getAdjustmentAmount());
				//SUBS-39 else preserve original logic				
				if (relative <= 0) { //only care about positive price adjustments on an assigned bundles
					sum = sum.add(priceAdjustment.getAdjustmentAmount());
				}
			}
		}
		
		return sum;
	}
	
	/**
	 * @param price to adjust
	 * @param adjustment the amount to adjust.
	 */
	public void adjustPrice(final Price price, final BigDecimal adjustment) {
		//SUBS-39 else preserve original logic
		
		if (BigDecimal.ZERO.compareTo(adjustment) >= 0) { //ignore negative price adjustments on an assigned bundles
			return;
		}
		
		for (Map.Entry<Integer, PriceTier> tierEntry : price.getPriceTiers().entrySet()) {
			//Add the adjustment to every price tier.
			PriceTier priceTier = tierEntry.getValue();
			priceTier.setListPrice(adjustment.add(priceTier.getListPrice()));
			BigDecimal salePrice = priceTier.getSalePrice();
			if (salePrice != null) {
				priceTier.setSalePrice(adjustment.add(salePrice));
			}
			
			// add adjustment to promoted price
			BigDecimal computedPrice = priceTier.getComputedPrice();
			if (computedPrice != null) {
				// reset computed price
				priceTier.clearComputedPrice();
				// sum promoted and adjustment
				priceTier.setComputedPriceIfLower(adjustment.add(computedPrice));
			}
		}
	}
	
	/**
	 * Gather a list of all BundleConstituentGuids on our shopping item tree. If bundle definition changes and differs from our shopping item
	 * structure, we throw an exception.
	 * 
	 * @param shoppingItemList shoppingItem
	 * @param constituents list of BundleConstituents at the same level.
	 * @param bundleConstituentGuids map of shopping item guid to bundle constituent guids
	 */
	protected void populateMatchingBundleConstituentGuids(final List<ShoppingItem> shoppingItemList, final List<BundleConstituent> constituents,
			final Map<String, String> bundleConstituentGuids) {
		for (ShoppingItem shoppingItem : shoppingItemList) {
			int ordering = shoppingItem.getOrdering();
			boolean match = false;
			for (BundleConstituent constituent : constituents) {
				if (constituent.getOrdering() == ordering) {
					match = true;
					// Add guid when matched
					bundleConstituentGuids.put(shoppingItem.getGuid(), constituent.getGuid());
					List<ShoppingItem> children = shoppingItem.getChildren();
					iterateChildren(bundleConstituentGuids, constituent, children);
				}
			}
			if (!match) {
				final ProductSku productSku = getProductSkuLookup().findByGuid(shoppingItem.getSkuGuid());
				final String errorMessage = "Cart item has no matching bundle constituent";
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
		}
	}

	private void iterateChildren(final Map<String, String> bundleConstituentGuids, 
									final BundleConstituent constituent,
									final List<ShoppingItem> children) {
		if (children != null && !children.isEmpty()) {
			ConstituentItem constituentItem = constituent.getConstituent();
			if (constituentItem.isBundle()) {
				populateMatchingBundleConstituentGuids(children,
						((ProductBundle) constituentItem.getProduct()).getConstituents(),
						bundleConstituentGuids);
			} else {
				throw new InvalidProductStructureException("ShoppingItem structure invalid");
			}
		}
	}

	protected ProductSkuLookup getProductSkuLookup() {
		return productSkuLookup;
	}
}
