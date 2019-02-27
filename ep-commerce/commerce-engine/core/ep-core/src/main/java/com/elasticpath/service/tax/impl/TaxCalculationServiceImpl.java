/*
 * Copyright (c) Elastic Path Software Inc., 2006-2014
 */
package com.elasticpath.service.tax.impl;

import static com.elasticpath.plugin.tax.domain.TaxRecord.NO_TAX_RATE_TAX_NAME;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.domain.tax.TaxJurisdiction;
import com.elasticpath.money.Money;
import com.elasticpath.plugin.tax.domain.TaxAddress;
import com.elasticpath.plugin.tax.domain.TaxDocument;
import com.elasticpath.plugin.tax.domain.TaxOperationContext;
import com.elasticpath.plugin.tax.domain.TaxRecord;
import com.elasticpath.plugin.tax.domain.TaxableItemContainer;
import com.elasticpath.plugin.tax.domain.TaxedItem;
import com.elasticpath.plugin.tax.domain.TaxedItemContainer;
import com.elasticpath.plugin.tax.manager.TaxManager;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.service.tax.DiscountApportioningCalculator;
import com.elasticpath.service.tax.TaxCalculationResult;
import com.elasticpath.service.tax.TaxCalculationService;
import com.elasticpath.service.tax.TaxJurisdictionService;

/**
 * Implementation of {@link TaxCalculationService} </code> that uses EP tax tables to calculate taxes.
 * All calculations are performed and returned on BigDecimals to a scale of 4 to prevent rounding issues.
 */
public class TaxCalculationServiceImpl implements TaxCalculationService {

	private static final Logger LOG = Logger.getLogger(TaxCalculationServiceImpl.class);

	private TaxManager taxManager;
	
	private TaxableItemContainerAdapter taxableItemContainerAdapter;
	
	private TaxJurisdictionService taxJurisdictionService;
	
	private StoreService storeService;
	private DiscountApportioningCalculator discountCalculator;

	private BeanFactory beanFactory;

	@Override
	public TaxCalculationResult calculateTaxes(final String storeCode, 
												final TaxAddress destinationAddress,
												final TaxAddress originAddress,
												final Money shippingCost,
												final Map<? extends ShoppingItem, ShoppingItemPricingSnapshot> shoppingItemPricingSnapshotMap,
												final Money preTaxDiscount,
												final TaxOperationContext taxOperationContext) {
		
		return calculateTaxesAndAddToResult(getNewTaxCalculationResult(taxOperationContext.getCurrency()), 
											storeCode, 
											destinationAddress,
											originAddress,
											shippingCost,
											shoppingItemPricingSnapshotMap,
											preTaxDiscount,
											taxOperationContext);
	}

	@Override
	@SuppressWarnings("checkstyle:parameternumber")
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
		
		if (currency == null || shippingCost == null || shoppingItemPricingSnapshotMap == null || preTaxDiscount == null) {
			throw new EpServiceException("Required parameter is null");
		}
		
		final Store store = getStoreService().findStoreWithCode(storeCode);
	
		Set<String> activeTaxCodes = getActiveTaxCodeNames(store);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Active tax codes: " + activeTaxCodes);
		}
		
		Map<String, BigDecimal> discounts = calculateApportionedAmounts(preTaxDiscount.getAmount(), 
																		currency,
																		shoppingItemPricingSnapshotMap);
		TaxableItemContainer container = getTaxableItemContainerAdapter().adapt(
				shoppingItemPricingSnapshotMap,
				shippingCost.getAmount(),
				discounts,
				activeTaxCodes, 
				storeCode, 
				destinationAddress,
				originAddress,
				taxOperationContext);
				
		if (LOG.isDebugEnabled()) {
			LOG.debug("Taxable item container with apportioned discounts: " + container);
		}

		TaxDocument taxDocument = getTaxManager().calculate(container);
		
		TaxedItemContainer resultContainer = taxDocument.getTaxedItemContainer();
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Result taxed item container: " + resultContainer);
		}
		populateTaxCalculationResult(taxCalculationResult, resultContainer, currency);
		
		taxCalculationResult.setTaxDocument(taxDocument);
		
		return taxCalculationResult;
	}

	/**
	 * Calculates the apportioned amounts out of the given discount.
	 *
	 * @param discount      the discount value
	 * @param currency      the currency
	 * @param shoppingItemPricingSnapshotMap the map of shopping items to their corresponding pricing snapshots
	 * @return a map of shopping item ID to
	 */
	protected Map<String, BigDecimal> calculateApportionedAmounts(
			final BigDecimal discount,
			final Currency currency,
			final Map<? extends ShoppingItem, ShoppingItemPricingSnapshot> shoppingItemPricingSnapshotMap) {
		return getDiscountCalculator().apportionDiscountToShoppingItems(Money.valueOf(discount, currency), shoppingItemPricingSnapshotMap);
	}
	

	/**
	 * Populates the tax calculation result.
	 * 
	 * @param taxCalculationResult the tax calculation result to populate
	 * @param resultContainer the container
	 * @param currency the currency
	 */
	protected void populateTaxCalculationResult(final TaxCalculationResult taxCalculationResult,
			final TaxedItemContainer resultContainer, final Currency currency) {
		taxCalculationResult.setTaxInclusive(resultContainer.isTaxInclusive());

		TaxJurisdiction taxJurisdiction = getTaxJurisdiction(resultContainer.getStoreCode(), resultContainer.getDestinationAddress());

		for (TaxedItem item : resultContainer.getItems()) {
			if (TaxCode.TAX_CODE_SHIPPING.equals(item.getTaxCode())) {
				taxCalculationResult.addBeforeTaxShippingCost(toMoney(item.getPriceBeforeTax(), currency));
				taxCalculationResult.addShippingTax(toMoney(item.getTotalTax(), currency));
				for (TaxRecord taxRecord : item.getTaxRecords()) {
					if (isTaxRecordIncludedInTaxCalculationResult(taxRecord)) {
						taxCalculationResult.addTaxValue(
								getTaxCategory(
										taxRecord.getTaxName(),
										taxJurisdiction),
								toMoney(taxRecord.getTaxValue(), currency));
					}
				}
			} else {
				taxCalculationResult.addBeforeTaxItemPrice(toMoney(item.getPriceBeforeTax(), currency));
				taxCalculationResult.addToTaxInItemPrice(toMoney(item.getTaxInPrice(), currency));
				taxCalculationResult.addItemTax(item.getItemGuid(), toMoney(item.getTotalTax(), currency));
				for (TaxRecord taxRecord : item.getTaxRecords()) {
					if (isTaxRecordIncludedInTaxCalculationResult(taxRecord)) {
						taxCalculationResult.addTaxValue(getTaxCategory(
								taxRecord.getTaxName(),
								taxJurisdiction),
						toMoney(taxRecord.getTaxValue(), currency));
					}
				}
			}
		}
	}

	private TaxJurisdiction getTaxJurisdiction(final String storeCode, final TaxAddress address) {
		if (StringUtils.isBlank(storeCode) || address == null) {
			return null;
		}
		return getTaxJurisdictionService().retrieveEnabledInStoreTaxJurisdiction(storeCode, address);
	}

	/**
	 * Returns whether a given {@link TaxRecord} should be included in the {@link TaxCalculationResult} returned.
	 * This currently accepts all records other than null ones and ones that return true from
	 * {@link #isTaxRecordIndicatingNoTaxMatched(com.elasticpath.plugin.tax.domain.TaxRecord)}.
	 *
	 * @param taxRecord the {@link TaxRecord} to check, may be null.
	 * @return true if taxRecord is not null and {@link #isTaxRecordIndicatingNoTaxMatched(com.elasticpath.plugin.tax.domain.TaxRecord)} returns
	 * false; false otherwise.
	 */
	protected boolean isTaxRecordIncludedInTaxCalculationResult(final TaxRecord taxRecord) {
		return taxRecord != null && !isTaxRecordIndicatingNoTaxMatched(taxRecord);
	}

	/**
	 * Returns whether the given {@link TaxRecord} indicates that no tax rate has been matched.
	 * Checks if {@link com.elasticpath.plugin.tax.domain.TaxRecord#getTaxName()} matches {@link TaxRecord#NO_TAX_RATE_TAX_NAME} and returns true
	 * if so.
	 *
	 * @param taxRecord the {@link TaxRecord} to check, must not be null.
	 * @return true if {@link com.elasticpath.plugin.tax.domain.TaxRecord#getTaxName()} matches {@link TaxRecord#NO_TAX_RATE_TAX_NAME}; false
	 * otherwise.
	 */
	protected boolean isTaxRecordIndicatingNoTaxMatched(final TaxRecord taxRecord) {
		return StringUtils.equals(taxRecord.getTaxName(), NO_TAX_RATE_TAX_NAME);
	}
	
	/**
	 * Gets tax category for a given tax name for a store and a address, if not found returns a new tax category with the given name.
	 * 
	 * @param taxName the tax category name to look pu
	 * @param taxJurisdiction the tax Jurisdiction
	 *
	 * @return a tax category found by the name and tax jurisdiction, or a new tax category with the given name
	 */
	protected TaxCategory getTaxCategory(final String taxName, final TaxJurisdiction taxJurisdiction) {
		
		if (taxJurisdiction != null) {
			for (TaxCategory taxCategory : taxJurisdiction.getTaxCategorySet()) {
				if (taxCategory.getName().equals(taxName)) {
					return taxCategory;
				}
			}
		}

		/** if storeCode is blank, or address is null, or there is no tax jurisdiction for the store and the address, 
		 * or there is no tax category with the given tax name, then returns a new instance {@link TaxCategory} with the given tax name.
		 */
		TaxCategory taxCategory = beanFactory.getBean(ContextIdNames.TAX_CATEGORY);
		taxCategory.setName(taxName);
		return taxCategory;
	}
	
	/**
	 * Get a set of the names of the <code>TaxCode</code>s which are active in the given <code>Store</code>.
	 */
	private Set<String> getActiveTaxCodeNames(final Store store) {
		Set<String> activeTaxCodes = new HashSet<>();
		if (store.getTaxCodes() != null) {
			for (TaxCode taxCode : store.getTaxCodes()) {
				activeTaxCodes.add(taxCode.getCode());
			}
		}
		return activeTaxCodes;
	}

	/**
	 * Creates a {@link Money} instance with the supplied value and currency.
	 * 
	 * @param value the amount
	 * @param currency the currency
	 * @return {@link Money} instance
	 */
	protected Money toMoney(final BigDecimal value, final Currency currency) {
		return Money.valueOf(value, currency);
	}

	public TaxableItemContainerAdapter getTaxableItemContainerAdapter() {
		return taxableItemContainerAdapter;
	}

	public void setTaxableItemContainerAdapter(final TaxableItemContainerAdapter taxableItemContainerAdapter) {
		this.taxableItemContainerAdapter = taxableItemContainerAdapter;
	}

	public TaxManager getTaxManager() {
		return taxManager;
	}

	public void setTaxManager(final TaxManager taxManager) {
		this.taxManager = taxManager;
	}
	
	private TaxCalculationResult getNewTaxCalculationResult(final Currency currency) {
		TaxCalculationResult taxCalculationResult = beanFactory.getBean(ContextIdNames.TAX_CALCULATION_RESULT);
		taxCalculationResult.initialize(currency);
		
		return taxCalculationResult;
	}

	public TaxJurisdictionService getTaxJurisdictionService() {
		return taxJurisdictionService;
	}

	public void setTaxJurisdictionService(final TaxJurisdictionService taxJurisdictionService) {
		this.taxJurisdictionService = taxJurisdictionService;
	}
	
	protected StoreService getStoreService() {
		return storeService;
	}

	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	protected DiscountApportioningCalculator getDiscountCalculator() {
		return discountCalculator;
	}

	public void setDiscountCalculator(final DiscountApportioningCalculator discountCalculator) {
		this.discountCalculator = discountCalculator;
	}
}
