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
import com.elasticpath.rest.definition.orders.BillingaddressInfoIdentifier;
import com.elasticpath.rest.id.ResourceIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.service.shoppingcart.validation.impl.BillingAddressShoppingCartValidatorImpl;

@RunWith(MockitoJUnitRunner.class)
public class BillingAddressResolutionStrategyTest {
	private static final String SHOPPING_CART_GUID = "SHOPPING_CART_GUID";
	private static final String CART_ORDER_GUID = "CARD_ORDER_GUID";
	private static final String STORE_CODE = "STORE_CODE";

	@InjectMocks
	private BillingAddressResolutionStrategy billingAddressResolutionStrategy;


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
		final StructuredErrorMessage structuredErrorMessage = new StructuredErrorMessage(BillingAddressShoppingCartValidatorImpl.MESSAGE_ID, "",
				new HashMap<>());
		Maybe<ResourceIdentifier> resourceIdentifierMaybe
				= billingAddressResolutionStrategy.getResourceIdentifier(structuredErrorMessage, SHOPPING_CART_GUID);

		// Then
		ResourceIdentifier resourceIdentifier = resourceIdentifierMaybe.blockingGet();
		assertTrue(resourceIdentifier instanceof BillingaddressInfoIdentifier);
		BillingaddressInfoIdentifier billingaddressInfoIdentifier = (BillingaddressInfoIdentifier) resourceIdentifier;
		assertEquals(STORE_CODE, billingaddressInfoIdentifier.getOrder().getScope().getValue());
		assertEquals(CART_ORDER_GUID, billingaddressInfoIdentifier.getOrder().getOrderId().getValue());
	}

	@Test
	public void testGetResourceIdentifierWithInvalidMessageId() {
		// When
		final StructuredErrorMessage structuredErrorMessage = new StructuredErrorMessage("INVALID", "", new HashMap<>());
		Maybe<ResourceIdentifier> resourceIdentifierMaybe =
				billingAddressResolutionStrategy.getResourceIdentifier(structuredErrorMessage, SHOPPING_CART_GUID);

		// Then
		assertTrue(resourceIdentifierMaybe.isEmpty().blockingGet());
	}
}