package com.elasticpath.cortex.dce.addresses

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.getClient

class AddressConstants {

	public static final DEFAULT_COUNTRY_NAME = "CA"

	public static final DEFAULT_LOCALITY = "Vancouver"

	public static final DEFAULT_POSTAL_CODE = "V7V7V7"

	public static final DEFAULT_POSTAL_REGION = "BC"

	public static final DEFAULT_STREE_ADDRESS_SNIPPET = "random street"

	public static final DEFAULT_FAMILY_NAME = "itest"

	public static final DEFAULT_GIVEN_NAME = "generated"

	public static final ADDRESS_REL = "addresses"

	public static final ADDRESS_LINK_TYPE = "elasticpath.addresses.address"

	public static void goToAddressForm() {
		client.GET("/")
				.defaultprofile()
				.addresses()
				.addressform()
				.stopIfFailure()
	}

	public static void goToAddress() {
		client.GET("/")
				.defaultprofile()
				.addresses()
				.stopIfFailure()
	}
}
