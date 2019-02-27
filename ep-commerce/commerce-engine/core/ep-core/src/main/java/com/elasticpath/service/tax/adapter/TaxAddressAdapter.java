/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.tax.adapter;

import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.store.WarehouseAddress;
import com.elasticpath.plugin.tax.builder.TaxAddressBuilder;
import com.elasticpath.plugin.tax.domain.TaxAddress;

/**
 * Builder for {@link com.elasticpath.plugin.tax.domain.impl.MutableTaxAddress} for a warehouse address.
 */
public class TaxAddressAdapter {

	/**
	 * Converts a warehouse address to a type of {@link com.elasticpath.plugin.tax.domain.TaxAddress}.
	 *
	 * @param warehouseAddress the warehouse address
	 * @return address the warehouse address as an {@link com.elasticpath.domain.customer.Address}
	 */
	public TaxAddress toTaxAddress(final WarehouseAddress warehouseAddress) {
		if (warehouseAddress == null) {
			return null;
		} else {
			return TaxAddressBuilder.newBuilder()
					.withStreet1(warehouseAddress.getStreet1())
					.withStreet2(warehouseAddress.getStreet2())
					.withCity(warehouseAddress.getCity())
					.withSubCountry(warehouseAddress.getSubCountry())
					.withCountry(warehouseAddress.getCountry())
					.withZipOrPostalCode(warehouseAddress.getZipOrPostalCode())
					.build();
		}
	}

	/**
	 * Converts an address to a type of {@link com.elasticpath.plugin.tax.domain.TaxAddress}.
	 *
	 * This should return a null if the address is null because during the preliminary stages of checkout, shipping/billing address
	 * is unknown but tax is still calculated on.
	 *
	 * @param address the address
	 * @return address as a type of TaxAddress
	 */
	public TaxAddress toTaxAddress(final Address address) {
		if (address == null) {
			return null;
		}
		return TaxAddressBuilder.newBuilder()
				.withStreet1(address.getStreet1())
				.withStreet2(address.getStreet2())
				.withCity(address.getCity())
				.withSubCountry(address.getSubCountry())
				.withCountry(address.getCountry())
				.withZipOrPostalCode(address.getZipOrPostalCode())
				.build();
	}
}
