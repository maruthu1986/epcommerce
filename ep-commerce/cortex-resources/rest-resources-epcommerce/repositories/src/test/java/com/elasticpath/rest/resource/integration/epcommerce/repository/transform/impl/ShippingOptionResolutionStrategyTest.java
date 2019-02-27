package com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.Maybe;
import io.reactivex.Single;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionInfoIdentifier;
import com.elasticpath.rest.id.ResourceIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.ShipmentDetailsConstants;
import com.elasticpath.service.shoppingcart.validation.impl.ShippingOptionShoppingCartValidatorImpl;

@RunWith(MockitoJUnitRunner.class)
public class ShippingOptionResolutionStrategyTest {
	private static final String SHOPPING_CART_GUID = "SHOPPING_CART_GUID";
	private static final String CART_ORDER_GUID = "CARD_ORDER_GUID";
	private static final String STORE_CODE = "STORE_CODE";

	@InjectMocks
	private ShippingOptionResolutionStrategy shippingOptionResolutionStrategy;

	@Mock
	private ShoppingCartRepository shoppingCartRepository;

	@Mock
	private CartOrder cartOrder;

	@Mock
	private CartOrderRepository cartOrderRepository;

	@Test
	public void testGetResourceIdentifier() {
		// Given

		when(shoppingCartRepository.findStoreForCartGuid(SHOPPING_CART_GUID)).thenReturn(Single.just(STORE_CODE));
		when(cartOrderRepository.findByCartGuidSingle(SHOPPING_CART_GUID)).thenReturn(Single.just(cartOrder));
		when(cartOrder.getGuid()).thenReturn(CART_ORDER_GUID);

		// When
		final StructuredErrorMessage structuredErrorMessage = new StructuredErrorMessage(
				ShippingOptionShoppingCartValidatorImpl.MESSAGE_IDS.iterator().next(), "", new HashMap<>());
		Maybe<ResourceIdentifier> resourceIdentifierMaybe =
				shippingOptionResolutionStrategy.getResourceIdentifier(structuredErrorMessage, SHOPPING_CART_GUID);

		// Then
		ResourceIdentifier resourceIdentifier = resourceIdentifierMaybe.blockingGet();
		assertTrue(resourceIdentifier instanceof ShippingOptionInfoIdentifier);
		ShippingOptionInfoIdentifier shippingOptionInfoIdentifier = (ShippingOptionInfoIdentifier) resourceIdentifier;
		assertEquals(STORE_CODE, shippingOptionInfoIdentifier.getScope().getValue());
		assertEquals(CART_ORDER_GUID, shippingOptionInfoIdentifier.getShipmentDetailsId().getValue().get(ShipmentDetailsConstants.ORDER_ID));
		assertEquals(ShipmentDetailsConstants.SHIPMENT_TYPE,
				shippingOptionInfoIdentifier.getShipmentDetailsId().getValue().get(ShipmentDetailsConstants.DELIVERY_ID));
	}

	@Test
	public void testGetResourceIdentifierWithInvalidMessageId() {
		// When
		final StructuredErrorMessage structuredErrorMessage = new StructuredErrorMessage("INVALID", "", new HashMap<>());
		Maybe<ResourceIdentifier> resourceIdentifierMaybe =
				shippingOptionResolutionStrategy.getResourceIdentifier(structuredErrorMessage, SHOPPING_CART_GUID);

		// Then
		assertTrue(resourceIdentifierMaybe.isEmpty().blockingGet());
	}

}