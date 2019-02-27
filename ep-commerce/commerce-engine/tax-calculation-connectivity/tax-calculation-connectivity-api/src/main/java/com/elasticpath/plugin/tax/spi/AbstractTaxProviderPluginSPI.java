/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.spi;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.plugin.tax.TaxProviderPluginInvoker;
import com.elasticpath.plugin.tax.capability.TaxCalculationCapability;
import com.elasticpath.plugin.tax.capability.TaxExemptionCapability;
import com.elasticpath.plugin.tax.capability.TaxProviderCapability;
import com.elasticpath.plugin.tax.domain.TaxableItemContainer;
import com.elasticpath.plugin.tax.domain.TaxedItemContainer;
import com.elasticpath.plugin.tax.exception.TaxCalculationServiceException;

/**
 * The SPI abstract class for tax provider plugins.
 */
public abstract class AbstractTaxProviderPluginSPI implements TaxProviderPluginInvoker {

	private static final Logger LOG = Logger.getLogger(AbstractTaxProviderPluginSPI.class);

	@Override
	public <T extends TaxProviderCapability> T getCapability(final Class<T> capability) {
		if (capability.isAssignableFrom(this.getClass())) {
			return capability.cast(this);
		}
		return null;
	}

	/**
	 * This should determine whether the plugin has tax exemption capability.
	 * If not, and a tax exemption code is provided, an error is thrown.
	 *
	 * @param container the taxable item container
	 * @return null
	 */
	@Override
	public final TaxedItemContainer calculateTaxes(final TaxableItemContainer container) {
		if (getCapability(TaxExemptionCapability.class) == null && container.getTaxOperationContext().getTaxExemption() != null
				&& StringUtils.isNotEmpty(container.getTaxOperationContext().getTaxExemption().getExemptionId())) {
			LOG.warn("The tax provider does not provide tax exemption services. Class: " + this.getClass());
		}

		TaxCalculationCapability taxCalculationCapability = this.getCapability(TaxCalculationCapability.class);
		if (taxCalculationCapability == null) {
			throw new TaxCalculationServiceException("Critical error: The tax provider cannot calculate taxes. Class: " + this.getClass());
		} else {
			return taxCalculationCapability.calculate(container);
		}
	}
}
