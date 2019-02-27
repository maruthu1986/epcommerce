/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.service.tax.resolver.impl;

import java.math.BigDecimal;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.domain.tax.TaxJurisdiction;
import com.elasticpath.domain.tax.TaxRegion;
import com.elasticpath.plugin.tax.common.TaxCalculationConstants;
import com.elasticpath.plugin.tax.common.TaxContextIdNames;
import com.elasticpath.plugin.tax.domain.TaxAddress;
import com.elasticpath.plugin.tax.domain.TaxableItem;
import com.elasticpath.plugin.tax.domain.TaxableItemContainer;
import com.elasticpath.plugin.tax.rate.TaxRateDescriptor;
import com.elasticpath.plugin.tax.rate.TaxRateDescriptorResult;
import com.elasticpath.plugin.tax.rate.dto.MutableTaxRateDescriptor;
import com.elasticpath.plugin.tax.rate.dto.MutableTaxRateDescriptorResult;
import com.elasticpath.plugin.tax.rate.dto.NoTaxRateDescriptor;
import com.elasticpath.plugin.tax.rate.dto.NoTaxRateDescriptorResult;
import com.elasticpath.plugin.tax.rate.impl.TaxRateApplier;
import com.elasticpath.plugin.tax.resolver.TaxRateDescriptorResolver;
import com.elasticpath.service.tax.TaxJurisdictionService;

/**
 * Default implementation of a {@link TaxRateDescriptorResolver} to retrieve tax rates and tax jurisdiction from EP persistence. 
 */
public class TaxRateDescriptorResolverImpl implements TaxRateDescriptorResolver {
	private static final Logger LOG = Logger.getLogger(TaxRateDescriptorResolverImpl.class);

	private static final BigDecimal PERCENT_CONVERT = new BigDecimal("100");

	private BeanFactory beanFactory;

	private TaxJurisdictionService taxJurisdictionService;

	@Override
	public TaxRateDescriptorResult findTaxRateDescriptors(final TaxableItem taxableItem, final TaxableItemContainer container) {

		final TaxJurisdiction taxJurisdiction = findTaxJurisdictionByStoreAndAddress(container.getStoreCode(),
				container.getDestinationAddress());
		if (taxJurisdiction == null) {
			LOG.debug("Could not find a tax jurisdiction for the given container: " + container);
			return new NoTaxRateDescriptorResult();
		}

		MutableTaxRateDescriptorResult result = getBeanFactory().getBean(TaxContextIdNames.MUTABLE_TAX_RATE_DESCRIPTOR_RESULT);
		
		for (TaxCategory taxCategory : taxJurisdiction.getTaxCategorySet()) {
			for (TaxRegion taxRegion : taxCategory.getTaxRegionSet()) {
				
				if (taxRegion.getRegionName() == null) {
					LOG.debug("Tax region has not been found for the given container: " + container);
					return new NoTaxRateDescriptorResult(isInclusiveTaxCalculationInUse(taxJurisdiction));
				}
				
				final BigDecimal decimalTaxRate = getDecimalTaxRate(taxRegion, taxableItem.getTaxCode());

				if (decimalTaxRate != null) {
					result.addTaxRateDescriptor(createNewTaxRateDescriptor(taxJurisdiction, taxRegion, taxCategory, decimalTaxRate));
				}
			}
		}
		if (CollectionUtils.isEmpty(result.getTaxRateDescriptors())) {
			LOG.debug("No a tax value for the given container: " + container);
			
			return new NoTaxRateDescriptorResult(isInclusiveTaxCalculationInUse(taxJurisdiction));
		}

		result.setTaxInclusive(isInclusiveTaxCalculationInUse(taxJurisdiction));
		
		return result;
	}

	/**
	 * Creates a new tax rate.
	 * 
	 * @param taxJurisdiction the tax jurisdiction
	 * @param taxRegion taxRegion
	 * @param taxCategory the tax category
	 * @param value the value of the tax rate
	 * @return a {@link TaxRateDescriptor} instance
	 */
	protected TaxRateDescriptor createNewTaxRateDescriptor(final TaxJurisdiction taxJurisdiction, 
															final TaxRegion taxRegion,
															final TaxCategory taxCategory,
															final BigDecimal value) {
		MutableTaxRateDescriptor taxRateDescriptor = getBeanFactory().getBean(TaxContextIdNames.MUTABLE_TAX_RATE_DESCRIPTOR);
		taxRateDescriptor.setId(taxCategory.getName());

		if (value != null) {
			taxRateDescriptor.setValue(value);
		}
		
		boolean inclusiveTaxCalculationInUse = isInclusiveTaxCalculationInUse(taxJurisdiction);
		setTaxRateApplier(taxRateDescriptor, inclusiveTaxCalculationInUse);
		
		taxRateDescriptor.setTaxJurisdiction(taxJurisdiction.getRegionCode());
		taxRateDescriptor.setTaxRegion(taxRegion.getRegionName());
		
		return taxRateDescriptor;
	}

	private void setTaxRateApplier(final MutableTaxRateDescriptor taxRateDescriptor, final boolean inclusiveTaxCalculationInUse) {
		TaxRateApplier taxRateApplier = getBeanFactory().getBean(TaxContextIdNames.TAX_EXCLUSIVE_RATE_APPLIER);
		if (inclusiveTaxCalculationInUse) {
			taxRateApplier = getBeanFactory().getBean(TaxContextIdNames.TAX_INCLUSIVE_RATE_APPLIER);
		}
		taxRateDescriptor.setTaxRateApplier(taxRateApplier);
	}

	/**
	 * Sums all taxes applicable to the given tax code in the given tax jurisdiction.
	 * 
	 * @param itemTaxCode the tax code to find the rate for
	 * @param taxJurisdiction the jurisdiction to get the taxes from
	 * @return sum of all tax rates which apply to itemTaxCode in taxJurisdiction
	 */
	protected BigDecimal getTotalDecimalTaxRate(final String itemTaxCode, final TaxJurisdiction taxJurisdiction) {
		BigDecimal totalTaxRate = BigDecimal.ZERO;
		for (TaxCategory taxCategory : taxJurisdiction.getTaxCategorySet()) {
			for (TaxRegion taxRegion : taxCategory.getTaxRegionSet()) {
				totalTaxRate = totalTaxRate.add(getDecimalTaxRate(taxRegion, itemTaxCode));
			}
		}
		return totalTaxRate;
	}

	/**
	 * Returns true if the "inclusive" tax calculation method is in use; otherwise false. This is based on the specified
	 * <code>TaxJurisdiction</code>, which is based on the shipping address. If the taxJurisdiction is null, this method returns
	 * false by default.
	 * 
	 * @param taxJurisdiction the <code>TaxJurisdiction</code>
	 * @return true if the "inclusive" tax calculation method is in use; otherwise false.
	 */
	protected boolean isInclusiveTaxCalculationInUse(final TaxJurisdiction taxJurisdiction) {
		if (taxJurisdiction == null) {
			return false;
		}
		return taxJurisdiction.getPriceCalculationMethod().equals(TaxJurisdiction.PRICE_CALCULATION_INCLUSIVE);
	}

	/**
	 * Retrieves a tax jurisdiction for the specified address and store.
	 * 
	 * @param storeCode the store to use
	 * @param address the address to use
	 * @return an instance of a {@link TaxJurisdiction} or null if none found
	 */
	protected TaxJurisdiction findTaxJurisdictionByStoreAndAddress(final String storeCode, final TaxAddress address) {
		if (storeCode != null && address != null) {
			return this.getTaxJurisdictionService().retrieveEnabledInStoreTaxJurisdiction(storeCode, address);
		}
		return null;
	}

	/**
	 * Gets the tax rate for a given tax code in the given tax region, expressed as a decimal (e.g. a 7.5% tax represented as
	 * 0.0750).
	 * 
	 * @param taxRegion the region in which the tax applies
	 * @param taxCode the code representing the tax category being applied
	 * @return the tax rate expressed as a decimal (e.g. 0.075 for a 7.5% tax), or zero if one does not exist for the given
	 *         inputs.
	 */
	protected BigDecimal getDecimalTaxRate(final TaxRegion taxRegion, final String taxCode) {
		BigDecimal taxRatePercentage = taxRegion.getTaxRate(taxCode);
		if (taxRatePercentage == null) {
			return null;
		}
		return taxRatePercentage.divide(PERCENT_CONVERT, 
										TaxCalculationConstants.DEFAULT_DIVIDE_SCALE, 
										TaxCalculationConstants.DEFAULT_ROUNDING_MODE);
	}

	@Override
	public TaxRateDescriptorResult findTaxJurisdiction(final TaxableItemContainer container) {

		final TaxJurisdiction taxJurisdiction = findTaxJurisdictionByStoreAndAddress(container.getStoreCode(),
				container.getDestinationAddress());
		if (taxJurisdiction == null) {
			LOG.debug("Could not find a tax jurisdiction for the given container: " + container);
			return new NoTaxRateDescriptorResult();
		}

		MutableTaxRateDescriptorResult result = getBeanFactory().getBean(TaxContextIdNames.MUTABLE_TAX_RATE_DESCRIPTOR_RESULT);
		
		result.setTaxInclusive(isInclusiveTaxCalculationInUse(taxJurisdiction));
		result.addTaxRateDescriptor(new NoTaxRateDescriptor(taxJurisdiction.getRegionCode(), null));
		
		return result;
	}
	
	public TaxJurisdictionService getTaxJurisdictionService() {
		return taxJurisdictionService;
	}

	public void setTaxJurisdictionService(final TaxJurisdictionService taxJurisdictionService) {
		this.taxJurisdictionService = taxJurisdictionService;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

}
