/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.repositories.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.misc.CheckoutResults;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.definition.purchases.PurchasesIdentifier;
import com.elasticpath.rest.form.SubmitResult;
import com.elasticpath.rest.form.SubmitStatus;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerSessionRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.repositories.PurchaseRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.service.impl.CartHasItemsServiceImpl;

/**
 * Test for the  {@link PurchaseEntityRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PurchaseEntityRepositoryImplTest {

	private static final IdentifierPart<String> SCOPE = StringIdentifier.of("Store");
	private static final IdentifierPart<String> PURCHASE_ID = StringIdentifier.of("1234");
	private static final String ORDER_ID = "Order id";

	@InjectMocks
	private PurchaseEntityRepositoryImpl<PurchaseEntity, PurchaseIdentifier> entityRepository;

	@Mock
	private PricingSnapshotRepository pricingSnapshotRepository;

	@Mock
	private CustomerSessionRepository sessionRepository;

	@Mock
	private PurchaseRepository purchaseRepository;

	@Mock
	private CartOrderRepository cartOrderRepository;

	@InjectMocks
	private CartHasItemsServiceImpl cartHasItemsService;

	@Before
	public void setUp() {
		entityRepository.setCartOrderRepository(cartOrderRepository);
		entityRepository.setCartHasItemsService(cartHasItemsService);
		entityRepository.setPricingSnapshotRepository(pricingSnapshotRepository);
	}

	@Test
	public void createPurchaseWithFailure1() {
		PurchaseEntity entity = performCreateOperationSetupWhere(true);

		entityRepository.submit(entity, SCOPE)
				.test()
				.assertError(ResourceOperationFailure.serverError(PurchaseEntityRepositoryImpl.NOT_PURCHASABLE));
	}

	@Test
	public void createPurchaseWithFailure2() {
		PurchaseEntity entity = performCreateOperationSetupWhere(true);

		entityRepository.submit(entity, SCOPE)
				.test()
				.assertError(ResourceOperationFailure.serverError(PurchaseEntityRepositoryImpl.NOT_PURCHASABLE));
	}

	@Test
	public void createPurchaseWithSuccess() {
		PurchaseEntity entity = performCreateOperationSetupWhere(false);

		PurchasesIdentifier purchasesIdentifier = PurchasesIdentifier.builder()
				.withScope(SCOPE)
				.build();
		PurchaseIdentifier purchaseIdentifier = PurchaseIdentifier.builder()
				.withPurchaseId(PURCHASE_ID)
				.withPurchases(purchasesIdentifier)
				.build();

		SubmitResult<PurchaseIdentifier> submitResult = SubmitResult.<PurchaseIdentifier>builder()
				.withIdentifier(purchaseIdentifier)
				.withStatus(SubmitStatus.CREATED)
				.build();

		entityRepository.submit(entity, SCOPE)
				.test()
				.assertValue(submitResult);
	}

	private PurchaseEntity performCreateOperationSetupWhere(final boolean shoppingCartIsEmpty) {
		CartOrder cartOrder = mock(CartOrder.class);
		ShoppingCartTaxSnapshot taxSnapshot = mock(ShoppingCartTaxSnapshot.class);
		CustomerSession customerSession = mock(CustomerSession.class);
		OrderPayment orderPayment = mock(OrderPayment.class);
		CheckoutResults checkoutResults = mock(CheckoutResults.class);
		ShoppingCart shoppingCart = createShoppingCart(shoppingCartIsEmpty);

		when(cartOrderRepository.findByGuidAsSingle(SCOPE.getValue(), ORDER_ID)).thenReturn(Single.just(cartOrder));

		when(purchaseRepository.createNewOrderPaymentEntity()).thenReturn(Single.just(orderPayment));

		when(cartOrderRepository.getEnrichedShoppingCartSingle(SCOPE.getValue(), cartOrder)).thenReturn(Single.just(shoppingCart));

		when(sessionRepository.findOrCreateCustomerSessionAsSingle()).thenReturn(Single.just(customerSession));

		when(pricingSnapshotRepository.getShoppingCartTaxSnapshot(shoppingCart)).thenReturn(Single.just(taxSnapshot));

		when(purchaseRepository.checkout(shoppingCart, taxSnapshot, customerSession, orderPayment)).thenReturn(Single.just(checkoutResults));

		Order order = mock(Order.class);
		when(checkoutResults.isOrderFailed()).thenReturn(false);
		when(checkoutResults.getOrder()).thenReturn((order));
		when(order.getGuid()).thenReturn(PURCHASE_ID.getValue());

		return PurchaseEntity.builder()
				.withOrderId(ORDER_ID)
				.build();
	}

	private ShoppingCart createShoppingCart(final boolean empty) {
		final ShoppingCart shoppingCart = mock(ShoppingCart.class);

		when(shoppingCart.isEmpty()).thenReturn(empty);

		return shoppingCart;
	}

}
