/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.order.repositories;

import io.reactivex.Observable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.addresses.AddressIdentifier;
import com.elasticpath.rest.definition.addresses.AddressesIdentifier;
import com.elasticpath.rest.definition.orders.BillingaddressInfoIdentifier;
import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;

/**
 * Repository for selected address for billing address info.
 *
 * @param <I>  the identifier type
 * @param <LI> the linked identifier type
 */
@Component
public class BillingAddressInfoToAddressRepositoryImpl<I extends BillingaddressInfoIdentifier, LI extends AddressIdentifier>
		implements LinksRepository<BillingaddressInfoIdentifier, AddressIdentifier> {

	private CartOrderRepository cartOrderRepository;

	@Override
	public Observable<AddressIdentifier> getElements(final BillingaddressInfoIdentifier billingaddressInfoIdentifier) {
		OrderIdentifier orderIdentifier = billingaddressInfoIdentifier.getOrder();
		IdentifierPart<String> scope = orderIdentifier.getScope();
		String orderId = orderIdentifier.getOrderId().getValue();

		return cartOrderRepository.findByGuidAsSingle(scope.getValue(), orderId)
				.flatMapMaybe(cartOrder -> cartOrderRepository.getBillingAddress(cartOrder))
				.map(address -> buildAddressIdentifier(scope, address.getGuid()))
				.toObservable();
	}

	/**
	 * Build the address identifier.
	 *
	 * @param scope     scope
	 * @param addressId addressId
	 * @return the address identifier
	 */
	protected AddressIdentifier buildAddressIdentifier(final IdentifierPart<String> scope, final String addressId) {
		return AddressIdentifier.builder()
				.withAddresses(AddressesIdentifier.builder()
						.withScope(scope)
						.build())
				.withAddressId(StringIdentifier.of(addressId))
				.build();
	}

	@Reference
	public void setCartOrderRepository(final CartOrderRepository cartOrderRepository) {
		this.cartOrderRepository = cartOrderRepository;
	}
}
