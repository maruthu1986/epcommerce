/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails;

import java.util.Map;

import io.reactivex.Observable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.CompositeIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;

/**
 * Service that returns shipment detail ids accessible by a user.
 */
@Component
public class ShipmentDetailsIdParameterServiceImpl implements ShipmentDetailsIdParameterService {

	private CartOrderRepository cartOrderRepository;
	private ShipmentDetailsService shipmentDetailsService;

	@Override
	public Observable<IdentifierPart<Map<String, String>>> findShipmentDetailsIds(final String scope, final String userId) {
		return cartOrderRepository.findCartOrderGuidsByCustomerAsObservable(scope, userId)
				.flatMapSingle(orderId -> shipmentDetailsService.getShipmentDetailsIdForOrder(scope, orderId))
				.map(fieldValueMap -> (IdentifierPart<Map<String, String>>) CompositeIdentifier.of(fieldValueMap))
				.onErrorResumeNext(Observable.empty());
	}

	@Reference
	public void setCartOrderRepository(final CartOrderRepository cartOrderRepository) {
		this.cartOrderRepository = cartOrderRepository;
	}

	@Reference
	public void setShipmentDetailsService(final ShipmentDetailsService shipmentDetailsService) {
		this.shipmentDetailsService = shipmentDetailsService;
	}
}