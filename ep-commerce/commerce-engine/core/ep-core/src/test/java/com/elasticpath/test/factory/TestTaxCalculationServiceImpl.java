/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.test.factory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Map;
import java.util.Set;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.domain.tax.TaxRegion;
import com.elasticpath.money.Money;
import com.elasticpath.plugin.tax.domain.TaxAddress;
import com.elasticpath.plugin.tax.domain.TaxOperationContext;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.tax.TaxCalculationResult;
import com.elasticpath.service.tax.TaxCodeRetriever;
import com.elasticpath.service.tax.impl.TaxCalculationServiceImpl;
import com.elasticpath.service.tax.impl.TaxCodeRetrieverImpl;

/**
 * 
 * An overriding implementation of <code>TaxCalculationImpl</code> for testing purpose.
 */
public class TestTaxCalculationServiceImpl extends TaxCalculationServiceImpl {
	
	private static final int CALCULATION_FINAL_SCALE = 2;

	private final TaxCodeRetriever taxCodeRetriever = new TaxCodeRetrieverImpl();

	private BeanFactory beanFactory;

	@Override
	// CHECKSTYLE:OFF
	public TaxCalculationResult calculateTaxesAndAddToResult(final TaxCalculationResult taxCalculationResult, 
															 final String storeCode, 
															 final TaxAddress destinationAddress,
															 final TaxAddress originAddress,
															 final Money shippingCost, 
															 final Map<? extends ShoppingItem, ShoppingItemPricingSnapshot>
																	 shoppingItemPricingSnapshotMap,
															 final Money preTaxDiscount, 
															 final TaxOperationContext taxOperationContext) {
		
		Currency currency = taxOperationContext.getCurrency();
		
		taxCalculationResult.setDefaultCurrency(currency);
		Set<TaxCategory> taxCategories = getTaxJurisdictionService().retrieveEnabledInStoreTaxJurisdiction(storeCode, destinationAddress)
				.getTaxCategorySet();

		for (final Map.Entry<? extends ShoppingItem, ShoppingItemPricingSnapshot> entry : shoppingItemPricingSnapshotMap.entrySet()) {
			final ShoppingItem shoppingItem = entry.getKey();
			final ShoppingItemPricingSnapshot itemPricingSnapshot = entry.getValue();

			ProductSku sku = getProductSkuLookup().findByGuid(shoppingItem.getSkuGuid());
			String taxCode = taxCodeRetriever.getEffectiveTaxCode(sku).getCode();
			taxCalculationResult.addBeforeTaxItemPrice(itemPricingSnapshot.getTotal());
			
			BigDecimal itemTaxTotal = BigDecimal.ZERO;
			
			for (TaxCategory taxCategory : taxCategories) {
				for (TaxRegion taxRegion : taxCategory.getTaxRegionSet()) {
					final BigDecimal taxRate = taxRegion.getTaxRate(taxCode).divide(new BigDecimal(100), 10, RoundingMode.HALF_UP);
					
					final Money curTax = Money.valueOf(
							itemPricingSnapshot.getTotal().getAmount().multiply(taxRate).setScale(CALCULATION_FINAL_SCALE,
																								   BigDecimal.ROUND_HALF_UP), currency);
					itemTaxTotal = itemTaxTotal.add(curTax.getAmount());
					taxCalculationResult.addTaxValue(taxCategory, curTax);
				}
			}
			taxCalculationResult.addItemTax(shoppingItem.getGuid(), Money.valueOf(itemTaxTotal, currency));
		}
		
		Money newShippingCost = shippingCost;
		if (newShippingCost.getAmount() == null) {
			newShippingCost = Money.valueOf(BigDecimal.ZERO.setScale(CALCULATION_FINAL_SCALE), currency);
		}
		
		BigDecimal shippingTaxTotal = taxCalculationResult.getShippingTax().getAmount();
		
		for (TaxCategory taxCategory : taxCategories) {
			for (TaxRegion taxRegion : taxCategory.getTaxRegionSet()) {
				final BigDecimal taxRate = taxRegion.getTaxRate(TaxCode.TAX_CODE_SHIPPING).divide(
						new BigDecimal(100), 10, RoundingMode.HALF_UP);
				
				final Money shippingTax = Money.valueOf(
						shippingCost.getAmount().multiply(taxRate).setScale(CALCULATION_FINAL_SCALE, BigDecimal.ROUND_HALF_UP),
						currency);
				
				taxCalculationResult.addShippingTax(shippingTax);
				taxCalculationResult.addTaxValue(taxCategory, shippingTax);
				shippingTaxTotal = shippingTaxTotal.add(shippingTax.getAmount());
			}
		}
		taxCalculationResult.addItemTax(TaxCode.TAX_CODE_SHIPPING, Money.valueOf(shippingTaxTotal, currency));
		taxCalculationResult.setBeforeTaxShippingCost(Money.valueOf(
				newShippingCost.getAmount().add(taxCalculationResult.getBeforeTaxShippingCost().getAmount()), currency));

		return taxCalculationResult;
	}
	// CHECKSTYLE:ON

	protected ProductSkuLookup getProductSkuLookup() {
		return beanFactory.getBean(ContextIdNames.PRODUCT_SKU_LOOKUP);
	}

	@Override
	public void setBeanFactory(final BeanFactory beanFactory) {
		super.setBeanFactory(beanFactory);
		this.beanFactory = beanFactory;
	}
}
