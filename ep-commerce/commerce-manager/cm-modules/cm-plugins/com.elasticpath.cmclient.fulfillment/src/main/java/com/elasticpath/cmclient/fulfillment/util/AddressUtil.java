/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.fulfillment.util;

import com.elasticpath.domain.customer.Address;

/**
 * Utility class for handling addresses.
 */
public final class AddressUtil {
	private static final String COMMA = ","; //$NON-NLS-1$

	private static final String SPACE = " "; //$NON-NLS-1$

	/**
	 * Constructor.
	 */
	private AddressUtil() {
		super();
	}
	
	/**
	 * Formats an address in a single line.
	 * 
	 * @param address the address to format
	 * @param displayNames should the customer names be displayed
	 * @return a string representing the address
	 */
	public static String formatAddress(final Address address, final boolean displayNames) {
		final StringBuilder addressStr = new StringBuilder();
		if (displayNames) {
			addressStr.append(address.getFirstName() + SPACE + address.getLastName() + COMMA + SPACE);
		}

		addressStr.append(address.getStreet1() + COMMA + SPACE);
		if (address.getStreet2() != null && address.getStreet2().length() > 0) {
			addressStr.append(address.getStreet2() + COMMA + SPACE);
		}
		addressStr.append(address.getCity() + COMMA + SPACE);
		if (address.getSubCountry() != null && address.getSubCountry().length() > 0) {
			addressStr.append(address.getSubCountry() + COMMA + SPACE);
		}
		addressStr.append(address.getZipOrPostalCode() + COMMA + SPACE);
		addressStr.append(address.getCountry());

		return addressStr.toString();
	}

	/**
	 * Gets a formatted string of only the customer names in the address.
	 * 
	 * @param address the address
	 * @return first and last name saved in the address 
	 */
	public static String getFullCustomerName(final Address address) {
		return address.getFirstName() + SPACE + address.getLastName();
	}

}
