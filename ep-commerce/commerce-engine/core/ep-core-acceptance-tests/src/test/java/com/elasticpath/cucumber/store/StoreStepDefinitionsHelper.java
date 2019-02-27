/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cucumber.store;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.cucumber.ScenarioContextValueHolder;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.domain.tax.TaxCategoryTypeEnum;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.domain.tax.TaxJurisdiction;
import com.elasticpath.domain.tax.TaxRegion;
import com.elasticpath.domain.tax.TaxValue;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.tax.TaxCategoryTypeConverter;
import com.elasticpath.tax.TaxJurisdictionFacade;
import com.elasticpath.test.persister.TaxTestPersister;
import com.elasticpath.test.persister.TestApplicationContext;
import com.elasticpath.test.persister.TestDataPersisterFactory;

/**
 * Help class for {@link StoreStepDefinitions}.
 */
public class StoreStepDefinitionsHelper {
	
	private static final String TAX_INCLUSIVE = "inclusive";

	@Inject
	@Named("storeHolder")
	private ScenarioContextValueHolder<Store> storeHolder;
	
	@Inject
	@Named("taxJurisdictionHolder")
	private ScenarioContextValueHolder<TaxJurisdiction> taxJurisdictionHolder;
	
	@Autowired
	private TestApplicationContext tac;
	
	@Autowired
	private StoreService storeService;
	
	/**
	 * Sets tax rates for testing tax rate overriding. 
	 *
	 * @param taxRateProperties  the tax rates data
	 * 
	 */
	public void setTaxRatesForStore(final List<Map<String, String>> taxRateProperties) {
		
		Store updatedStore = storeHolder.get();
		
		for (Map<String, String> properties : taxRateProperties) {
			
			updatedStore = setTaxRateForTaxRegion(updatedStore, 
									properties.get("taxName"),
									new BigDecimal(properties.get("taxRate")),
									properties.get("taxRegion"));
			
		}
		
		storeHolder.set(updatedStore);
	}


	/**
	 * Sets tax rates for testing tax rate overriding. 
	 *
	 * @param store the store
	 * @param taxName the tax name
	 * @param taxRate the tax Rate
	 * @param taxRegionName the tax region name
	 * 
	 * @return a store with the updated tax jurisdiction
	 */
	public Store setTaxRateForTaxRegion(final Store store, final String taxName, final BigDecimal taxRate, final String taxRegionName) {
		
		TaxTestPersister taxTestPersister = tac.getPersistersFactory().getTaxTestPersister();
		
		final TaxCode goods = taxTestPersister.getTaxCode(TaxTestPersister.TAX_CODE_GOODS);
		final TaxCode shipping = taxTestPersister.getTaxCode(TaxTestPersister.TAX_CODE_SHIPPING);
		
		Set<TaxJurisdiction> updatedTaxJurisdictions = new HashSet<>();
		
		for (TaxJurisdiction taxJurisdiction : store.getTaxJurisdictions()) {
			for (TaxCategory taxCategory : taxJurisdiction.getTaxCategorySet()) {
				if (taxCategory.getName().equals(taxName)) {
					Set<TaxRegion> taxRegions = taxCategory.getTaxRegionSet();
					for (TaxRegion taxRegion : taxRegions) {
						
						if (taxRegion.getRegionName().equals(taxRegionName)) {
						
							TaxValue goodsTaxValue = taxRegion.getTaxValuesMap().get(goods.getCode());
							goodsTaxValue.setTaxValue(taxRate);
							
							TaxValue shippingTaxValue = taxRegion.getTaxValuesMap().get(shipping.getCode());
							shippingTaxValue.setTaxValue(taxRate);
							
							taxRegion.addTaxValue(goodsTaxValue);
							taxRegion.addTaxValue(shippingTaxValue);
						}
					}
				}
			}
			
			updatedTaxJurisdictions.add(taxTestPersister.persistTaxJurisdiction(taxJurisdiction));
		}
		
		store.setTaxJurisdictions(updatedTaxJurisdictions);
		
		return storeService.saveOrUpdate(store);
	}

	/**
	 * Sets up a tax jurisdiction with specified tax calculation method for a store.
	 *
	 * @param taxCalculationMethod the flag indicating if tax inclusive
	 * @param taxJurisdiction the tax jurisdiction name
	 */
	public void setUpTaxJurisdiction(final String taxCalculationMethod, final String taxJurisdiction) {
		
		Store store = storeHolder.get();
		TestDataPersisterFactory persisterFactory = tac.getPersistersFactory();
		
		TaxJurisdiction jurisdiction = persisterFactory.getTaxTestPersister().getTaxJurisdiction(taxJurisdiction);		
		
		if (jurisdiction == null) {
			jurisdiction = persisterFactory.getTaxTestPersister().persistTaxJurisdiction(
					taxJurisdiction, 
					isTaxInclusive(taxCalculationMethod));	
		}
		
		Set<TaxJurisdiction> taxJurisdictionsSet = new HashSet<>();
		taxJurisdictionsSet.add(jurisdiction);

		store.setTaxJurisdictions(taxJurisdictionsSet);
		
		storeHolder.set(storeService.saveOrUpdate(store));
		taxJurisdictionHolder.set(jurisdiction);
	}

	private boolean isTaxInclusive(final String taxCalculationMethod) {
		
		if (taxCalculationMethod.equals(TAX_INCLUSIVE)) {
			return true;
		}
		
		return false;
	}

	/**
	 * Sets tax categories for testing tax rate overriding. 
	 *
	 * @param taxCategoryProperties  the tax category data
	 * 
	 */
	public void setTaxCategories(final List<Map<String, String>> taxCategoryProperties) {

		TaxTestPersister taxTestPersister = tac.getPersistersFactory().getTaxTestPersister();

		final TaxCode goods = taxTestPersister.getTaxCode(TaxTestPersister.TAX_CODE_GOODS);
		final TaxCode shipping = taxTestPersister.getTaxCode(TaxTestPersister.TAX_CODE_SHIPPING);
		
		Store updatedStore = storeHolder.get();
		Set<TaxJurisdiction> taxJurisdictionsSet = new HashSet<>();
		
		TaxJurisdiction taxJurisdiction = taxJurisdictionHolder.get();
		TaxJurisdictionFacade taxJurisdictionFacade = new TaxJurisdictionFacade(taxJurisdiction);

		for (Map<String, String> properties : taxCategoryProperties) {
			
			String taxName = properties.get("taxName");
			String taxRegion = properties.get("taxRegion");
			String taxRate = properties.get("taxRate");
			
			taxJurisdictionFacade.putTaxCategory(
					taxName, 
					getTaxCategoryType(taxRegion, taxJurisdiction.getRegionCode()));
					
			taxJurisdictionFacade = new TaxJurisdictionFacade(
					taxTestPersister.persistTaxJurisdiction(taxJurisdictionFacade.getTaxJurisdiction()));
			
			taxJurisdictionFacade.putTaxRegion(taxName, taxRegion);
			taxJurisdictionFacade.putTaxValue(taxName, taxRegion, goods, new BigDecimal(taxRate));
			taxJurisdictionFacade.putTaxValue(taxName, taxRegion, shipping, new BigDecimal(taxRate));
		}
		
		TaxJurisdiction updatedTaxJurisdiction = taxTestPersister.persistTaxJurisdiction(taxJurisdictionFacade.getTaxJurisdiction());
		taxJurisdictionsSet.add(updatedTaxJurisdiction); 
		
		
		updatedStore.setTaxJurisdictions(taxJurisdictionsSet);
		
		storeHolder.set(storeService.saveOrUpdate(updatedStore));
		taxJurisdictionHolder.set(updatedTaxJurisdiction);
	}


	private TaxCategoryTypeEnum getTaxCategoryType(final String taxRegion, final String jurisdictionRegionCode) {
		
		if (taxRegion.equals(jurisdictionRegionCode)) {
			return TaxCategoryTypeConverter.getInstance(TaxCategoryTypeConverter.COUNTRY);
		} 
		
		return TaxCategoryTypeConverter.getInstance(TaxCategoryTypeConverter.SUBCOUNTRY);
	}
}
