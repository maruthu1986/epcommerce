/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.destinationinfo;

import java.util.Map;

import io.reactivex.Observable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.addresses.AddressIdentifier;
import com.elasticpath.rest.definition.addresses.AddressesIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.DestinationInfoIdentifier;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.ShipmentDetailsConstants;

/**
 * Repository for finding the address selected.
 *
 * @param <I>	extends DestinationInfoIdentifier
 * @param <LI>	extends AddressIdentifier
 */
@Component
public class AddressForDestinationInfoRepositoryImpl<I extends DestinationInfoIdentifier, LI extends AddressIdentifier>
		implements LinksRepository<DestinationInfoIdentifier, AddressIdentifier> {

	private DestinationInfoService destinationInfoService;

	@Override
	public Observable<AddressIdentifier> getElements(final DestinationInfoIdentifier identifier) {
		IdentifierPart<String> scopeId = identifier.getScope();
		Map<String, String> shipmentDetailsId = identifier.getShipmentDetailsId().getValue();
		String orderId = shipmentDetailsId.get(ShipmentDetailsConstants.ORDER_ID);
		return destinationInfoService.getSelectedAddressGuidIfShippable(scopeId.getValue(), orderId)
				.flatMapObservable(addressId -> Observable.just(buildAddressIdentifier(scopeId, addressId)))
				.onErrorResumeNext(Observable.empty());
	}

	private AddressIdentifier buildAddressIdentifier(final IdentifierPart<String> scope, final String addressId) {
		return AddressIdentifier.builder()
				.withAddressId(StringIdentifier.of(addressId))
				.withAddresses(AddressesIdentifier.builder().withScope(scope).build())
				.build();
	}

	@Reference
	public void setDestinationInfoService(final DestinationInfoService destinationInfoService) {
		this.destinationInfoService = destinationInfoService;
	}
}
