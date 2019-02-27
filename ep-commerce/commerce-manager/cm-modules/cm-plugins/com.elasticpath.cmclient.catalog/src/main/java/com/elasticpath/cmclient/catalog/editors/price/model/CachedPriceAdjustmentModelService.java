/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.editors.price.model;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.common.pricing.service.PriceListHelperService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.SelectionRule;
import com.elasticpath.domain.pricing.PriceAdjustment;

/**
 * A price adjustment service that constructs price adjustment model.
 */
public class CachedPriceAdjustmentModelService {
	private final PriceListHelperService priceListHelperService = ServiceLocator.getService(ContextIdNames.PRICE_LIST_HELPER_SERVICE);

	private final Map<PriceListDescriptorDTO, PriceAdjustmentModelRoot> cachedModels =
			new HashMap<>();

	private final ProductBundle bundle;

	/**
	 * Constructs price adjustment models for a bundle based on different price list descriptor.
	 * 
	 * @param bundle {@link ProductBundle}.
	 */
	public CachedPriceAdjustmentModelService(final ProductBundle bundle) {
		this.bundle = bundle;
	}

	/**
	 * Gets the price adjustment model based on the the price list descriptor. This method is thread-safe.
	 * 
	 * @param priceList {@link PriceListDescriptorDTO}.
	 * @return {@link PriceAdjustmentModelRoot}.
	 */
	public PriceAdjustmentModelRoot getPriceAdjustmentModel(final PriceListDescriptorDTO priceList) {
		synchronized (cachedModels) {
			PriceAdjustmentModelRoot root = cachedModels.get(priceList);
			if (root != null) {
				return root;
			}

			int selectionParameter = getSelectionRuleParameter(bundle.getSelectionRule());
			BigDecimal price = getItemPrice(priceListHelperService.getPriceListMap(bundle, Arrays.asList(priceList)), null);

			root = new PriceAdjustmentModelRoot(bundle, selectionParameter, price, priceList);
			for (BundleConstituent bundleConstituent : bundle.getConstituents()) {
				addConstituentModels(priceList, root, bundleConstituent);
			}

			cachedModels.put(priceList, root);

			return root;
		}
	}

	/** 
	 * Saves the cached price adjustment models.
	 */
	public void saveCachedPriceAdjustmentModels() {
		synchronized (cachedModels) {
			for (PriceAdjustmentModelRoot cachedModel : cachedModels.values()) {
				String plGuid = cachedModel.getPriceListDescriptorDto().getGuid();
				for (PriceAdjustmentModel model : cachedModel.getChildren()) {
					BigDecimal adjustmentAmount = model.getPriceAdjustment();
					BundleConstituent bundleConstituent = model.getBundleConstituent();
					boolean found = false;
					// update or remove
					for (PriceAdjustment adj : bundleConstituent.getPriceAdjustments()) {
						if (plGuid.equals(adj.getPriceListGuid())) {
							if (adjustmentAmount == null) {
								bundleConstituent.getPriceAdjustments().remove(adj);
							} else {
								adj.setAdjustmentAmount(adjustmentAmount);
							}
							found = true;
							break;
						}
					}
					// add if not found
					if (!found && adjustmentAmount != null) {
						PriceAdjustment priceAdjustment = ServiceLocator.getService(ContextIdNames.PRICE_ADJUSTMENT);
						priceAdjustment.setPriceListGuid(plGuid);
						priceAdjustment.setAdjustmentAmount(adjustmentAmount);
						bundleConstituent.addPriceAdjustment(priceAdjustment);
					}
				}
			}
		}
	}

	private int getSelectionRuleParameter(final SelectionRule selectionRule) {
		int selectionParameter = 0;
		if (selectionRule != null) {
			selectionParameter = selectionRule.getParameter();
		}
		return selectionParameter;
	}

	private BigDecimal getItemPrice(final Map<PriceListDescriptorDTO, List<BaseAmountDTO>> priceListMap, final BundleConstituent bundleConstituent) {
		BaseAmountDTO itemPrice;

		//Check if constituent item is a sku, if so, use sku price, not product price.
		if (bundleConstituent != null && bundleConstituent.getConstituent() != null && bundleConstituent.getConstituent().isProductSku()) {
			itemPrice = getSkuPrice(priceListMap, bundleConstituent);
		} else { 
			itemPrice = getProductPrice(priceListMap, bundleConstituent);
		}

		if (itemPrice == null) {
			return null;
		}

		BigDecimal itemPriceBigDecimal = itemPrice.getListValue();
		if (itemPrice.getSaleValue() != null) {
			itemPriceBigDecimal = itemPrice.getSaleValue();
		}

		return itemPriceBigDecimal;
	}

	/**
	 * Gets the sku price for the constituent item from the priceList map based on a quantity of 1. If no sku level price found
	 * will return the product level price.
	 * @param priceListMap The map of PriceLists to Base Amounts
	 * @param bundleConstituent the bundle constituent whose price is to be returned.
	 * @return the sku level BaseAmountDTO or the Product level BaseAmountDTO
	 */
	private BaseAmountDTO getSkuPrice(
			final Map<PriceListDescriptorDTO, List<BaseAmountDTO>> priceListMap,
			final BundleConstituent bundleConstituent) {
		
		BaseAmountDTO itemPrice = null;
		ConstituentItem constituentItem = bundleConstituent.getConstituent();
		int constituentItemQty = 1; 
		String skuCode = constituentItem.getProductSku().getSkuCode();
		List<BaseAmountDTO> baseAmounts = priceListMap.values().iterator().next();
		constituentItemQty = getBaseAmountQtyClosestTo(baseAmounts, bundleConstituent.getQuantity());
		for (BaseAmountDTO baseAmount : baseAmounts) {
			
			// need to find the largest base amount qty that is <= constituent qty
			if (skuCode.equals(baseAmount.getSkuCode()) && (baseAmount.getQuantity().intValue() == constituentItemQty)) {
				itemPrice = baseAmount;
				break;
			}
		}
		// check if we need to get the product price
		if (itemPrice == null) {
			itemPrice = getProductPrice(priceListMap, bundleConstituent);
		}

		return itemPrice;
	}

	/**
	 * Gets the largest base amount quantity which is smaller than or equal to quantity.
	 * 
	 * @param baseAmounts the list of base amounts to check
	 * @param quantity the maximum value of the base amounts
	 * @return the largest integer value contained in the base amount list which is smaller than quantity, or 1 if none found
	 */
	private int getBaseAmountQtyClosestTo(final List<BaseAmountDTO> baseAmounts,
										  final Integer quantity) {
		int result = 1;
		for (BaseAmountDTO baseAmount : baseAmounts) {
			int baseAmountQty = baseAmount.getQuantity().intValue();
			if (baseAmountQty <= quantity && baseAmountQty > result) {
				//set result to larger baseAmount qty
				result = baseAmountQty;
			}
		}
		return result;
	}

	/**
	 * Gets the products BaseAmountDTO based on a quantity of 1.
	 * @param priceListMap The map of PriceLists to Base Amounts
	 * @param bundleConstituent the constituent item used to get the quantity.
	 * @return the products BaseAmountDTO or null if not found.
	 */
	private BaseAmountDTO getProductPrice(
			final Map<PriceListDescriptorDTO, List<BaseAmountDTO>> priceListMap,
			final BundleConstituent bundleConstituent) {
		BaseAmountDTO itemPrice = null;
		List<BaseAmountDTO> baseAmounts = priceListMap.values().iterator().next();

		int constituentItemQty = 1;
		if (bundleConstituent != null) {
			constituentItemQty = getBaseAmountQtyClosestTo(baseAmounts, bundleConstituent.getQuantity());
		}
		
		for (BaseAmountDTO baseAmount : baseAmounts) {
			if (baseAmount.getQuantity().intValue() == constituentItemQty) {
				itemPrice = baseAmount;
				break;
			}
		}
		return itemPrice;
	}

	private void addConstituentModels(final PriceListDescriptorDTO priceListDescriptor, final PriceAdjustmentModel parent,
			final BundleConstituent bundleConstituent) {
		ConstituentItem constituentItem = bundleConstituent.getConstituent();
		BigDecimal itemPrice = getItemPrice(priceListHelperService.getPriceListMap(constituentItem,
				Arrays.asList(priceListDescriptor)), bundleConstituent);
		BigDecimal priceAdj = getPriceAdjustment(bundleConstituent, priceListDescriptor.getGuid());

		PriceAdjustmentModel child;
		if (constituentItem.isBundle()) {
			ProductBundle bundle = (ProductBundle) constituentItem.getProduct();
			int selectionRuleParameter = getSelectionRuleParameter(bundle.getSelectionRule());

			child = new PriceAdjustmentModel(bundleConstituent, itemPrice, priceAdj, selectionRuleParameter);
			for (BundleConstituent childBc : bundle.getConstituents()) {
				addConstituentModels(priceListDescriptor, child, childBc);
			}
		} else {
			child = new PriceAdjustmentModel(bundleConstituent, itemPrice, priceAdj, 0);
		}

		parent.addChild(child);
	}

	private BigDecimal getPriceAdjustment(final BundleConstituent bundleConstituent, final String plGuid) {
		for (PriceAdjustment adj : bundleConstituent.getPriceAdjustments()) {
			if (plGuid.equals(adj.getPriceListGuid())) {
				return adj.getAdjustmentAmount();
			}
		}
		return null;
	}
}
