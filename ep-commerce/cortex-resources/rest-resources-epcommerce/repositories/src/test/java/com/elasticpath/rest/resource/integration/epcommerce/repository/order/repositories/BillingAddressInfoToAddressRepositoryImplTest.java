/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.order.repositories;

import static org.mockito.Mockito.when;

import io.reactivex.Maybe;
import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.rest.definition.addresses.AddressIdentifier;
import com.elasticpath.rest.definition.orders.BillingaddressInfoIdentifier;
import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;

/**
 * Test for {@link BillingAddressInfoToAddressRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class BillingAddressInfoToAddressRepositoryImplTest {

	private static final String ADDRESS_GUID = "addressId";
	private static final String ORDER_ID = "orderId";
	public static final String SCOPE = "scope";

	@Mock
	private CartOrder cartOrder;

	@Mock
	private Address address;

	@Mock
	private CartOrderRepository cartOrderRepository;

	@InjectMocks
	private BillingAddressInfoToAddressRepositoryImpl<BillingaddressInfoIdentifier, AddressIdentifier> repository;

	@Test
	public void shouldReturnNoValuesIfCartBillingAddressDoesNotExistTest() {
		when(cartOrderRepository.findByGuidAsSingle(SCOPE, ORDER_ID)).thenReturn(Single.just(cartOrder));
		when(cartOrderRepository.getBillingAddress(cartOrder)).thenReturn(Maybe.empty());
		repository.getElements(getBillingAddressInfoIdentifier())
				.test()
				.assertNoErrors()
				.assertNoValues();
	}

	@Test
	public void shouldReturnGuidIfCartBillingAddressExistTest() {
		when(cartOrderRepository.findByGuidAsSingle(SCOPE, ORDER_ID)).thenReturn(Single.just(cartOrder));
		when(cartOrderRepository.getBillingAddress(cartOrder)).thenReturn(Maybe.just(address));
		when(address.getGuid()).thenReturn(ADDRESS_GUID);
		repository.getElements(getBillingAddressInfoIdentifier())
				.test()
				.assertNoErrors()
				.assertValue(addressIdentifier -> addressIdentifier.getAddressId().getValue().equals(ADDRESS_GUID));
	}

	private BillingaddressInfoIdentifier getBillingAddressInfoIdentifier() {
		return BillingaddressInfoIdentifier.builder()
				.withOrder(OrderIdentifier.builder()
						.withOrderId(StringIdentifier.of(ORDER_ID))
						.withScope(StringIdentifier.of(SCOPE))
						.build())
				.build();
	}
}