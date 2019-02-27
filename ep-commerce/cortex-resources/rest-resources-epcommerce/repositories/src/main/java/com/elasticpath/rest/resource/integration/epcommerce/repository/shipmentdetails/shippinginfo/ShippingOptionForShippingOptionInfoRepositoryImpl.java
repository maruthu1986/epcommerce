/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.shippinginfo;

import java.util.Map;

import io.reactivex.Observable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionInfoIdentifier;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipping.ShippingOptionRepository;

/**
 * Repository for retrieving shipping option for shipping option info.
 *
 * @param <I>	extends ShippingOptionInfoIdentifier
 * @param <LI>	extends ShippingOptionIdentifier
 */
@Component
public class ShippingOptionForShippingOptionInfoRepositoryImpl<I extends ShippingOptionInfoIdentifier, LI extends ShippingOptionIdentifier>
		implements LinksRepository<ShippingOptionInfoIdentifier, ShippingOptionIdentifier> {

	private ShippingOptionRepository shippingOptionRepository;

	@Override
	public Observable<ShippingOptionIdentifier> getElements(final ShippingOptionInfoIdentifier identifier) {
		IdentifierPart<Map<String, String>> shipmentDetailsId = identifier.getShipmentDetailsId();
		IdentifierPart<String> scope = identifier.getScope();
		return shippingOptionRepository.getSelectedShippingOptionCodeForShipmentDetails(scope.getValue(), shipmentDetailsId.getValue())
				.map(shippingOptionId -> ShippingOptionIdentifier.builder()
						.withShipmentDetailsId(shipmentDetailsId)
						.withScope(scope)
						.withShippingOptionId(StringIdentifier.of(shippingOptionId))
						.build())
				.toObservable();
	}

	@Reference
	public void setShippingOptionRepository(final ShippingOptionRepository shippingOptionRepository) {
		this.shippingOptionRepository = shippingOptionRepository;
	}
}
