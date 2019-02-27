/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.capability;

/**
 * Plugins providing tax exemption features must implement this capability.
 * Otherwise, tax calculation involving exemption will throw an exception.
 */
public interface TaxExemptionCapability extends TaxProviderCapability {

}
