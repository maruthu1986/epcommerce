/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.order.repositories;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.rest.definition.orders.DeliveriesIdentifier;
import com.elasticpath.rest.definition.orders.DeliveryIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;

@RunWith(MockitoJUnitRunner.class)
public class DeliveryListRepositoryImplTest {

	private static final String SHIPMENT = "SHIPMENT";
	private static final String CART_ID = "CART";

	@Mock
	private ShoppingCartRepository shoppingCartRepository;

	@InjectMocks
	private DeliveryListRepositoryImpl<DeliveriesIdentifier, DeliveryIdentifier> repository;

	@Test
	public void testShoppingCartShipmentTypes() {
		ShoppingCart shoppingCart = mock(ShoppingCart.class);

		Set<ShipmentType> types = new HashSet<>();
		types.add(ShipmentType.ELECTRONIC);
		types.add(ShipmentType.PHYSICAL);
		types.add(ShipmentType.SERVICE);

		when(shoppingCart.getShipmentTypes()).thenReturn(types);
		when(shoppingCartRepository.getShoppingCart(CART_ID)).thenReturn(Single.just(shoppingCart));

		repository.getShoppingCartDeliveryTypes(CART_ID)
				.test()
				.assertValueCount(1)
				.assertValues(SHIPMENT);
	}
}
