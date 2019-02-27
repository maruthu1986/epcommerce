/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.destinationinfo.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.addresses.AddressFormIdentifier;
import com.elasticpath.rest.definition.addresses.AddressesIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.ShippingAddressFormForOrderRelationship;
import com.elasticpath.rest.helix.data.annotation.UriPart;
import com.elasticpath.rest.id.IdentifierPart;

/**
 * Adds an address form link in destination info.
 */
public class DestinationInfoToAddressFormRelationshipImpl implements ShippingAddressFormForOrderRelationship.LinkTo {

	private final IdentifierPart<String> scope;

	/**
	 * Constructor.
	 *
	 * @param scope	scope
	 */
	@Inject
	public DestinationInfoToAddressFormRelationshipImpl(@UriPart(AddressesIdentifier.SCOPE) final IdentifierPart<String> scope) {
		this.scope = scope;
	}

	@Override
	public Observable<AddressFormIdentifier> onLinkTo() {
		AddressesIdentifier addressesIdentifier = AddressesIdentifier.builder()
				.withScope(scope)
				.build();
		return Observable.just(AddressFormIdentifier.builder()
				.withAddresses(addressesIdentifier)
				.build());
	}
}
