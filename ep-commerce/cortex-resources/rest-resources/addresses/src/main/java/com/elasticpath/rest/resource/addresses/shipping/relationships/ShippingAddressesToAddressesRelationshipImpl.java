/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.shipping.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.addresses.AddressesIdentifier;
import com.elasticpath.rest.definition.addresses.ShippingAddressesFromAddressesRelationship;
import com.elasticpath.rest.definition.addresses.ShippingAddressesIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Shipping addresses to addresses link.
 */
public class ShippingAddressesToAddressesRelationshipImpl implements ShippingAddressesFromAddressesRelationship.LinkFrom {

	private final ShippingAddressesIdentifier shippingAddressesIdentifier;

	/**
	 * Constructor.
	 *
	 * @param shippingAddressesIdentifier shippingAddressesIdentifier
	 */
	@Inject
	public ShippingAddressesToAddressesRelationshipImpl(@RequestIdentifier final ShippingAddressesIdentifier shippingAddressesIdentifier) {
		this.shippingAddressesIdentifier = shippingAddressesIdentifier;
	}

	@Override
	public Observable<AddressesIdentifier> onLinkFrom() {
		return Observable.just(shippingAddressesIdentifier.getAddresses());
	}
}
