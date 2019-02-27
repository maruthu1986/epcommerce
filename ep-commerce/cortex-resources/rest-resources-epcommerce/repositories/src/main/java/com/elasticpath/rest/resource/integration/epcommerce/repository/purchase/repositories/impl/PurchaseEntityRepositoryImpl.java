/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.repositories.impl;

import com.google.common.annotations.VisibleForTesting;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.springframework.core.convert.ConversionService;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.misc.CheckoutResults;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.repository.Repository;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.definition.purchases.PurchasesIdentifier;
import com.elasticpath.rest.form.SubmitResult;
import com.elasticpath.rest.form.SubmitStatus;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerSessionRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.repositories.PurchaseRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.service.CartHasItemsService;

/**
 * Purchases Entity Repository.
 *
 * @param <E> the entity type
 * @param <I> the identifier type
 */
@Component
public class PurchaseEntityRepositoryImpl<E extends PurchaseEntity, I extends PurchaseIdentifier>
		implements Repository<PurchaseEntity, PurchaseIdentifier> {

	/**
	 * Error message for non purchasable order.
	 */
	@VisibleForTesting
	static final String NOT_PURCHASABLE = "The products selected are not purchasable";

	private CartOrderRepository cartOrderRepository;

	private OrderRepository orderRepository;

	private ResourceOperationContext resourceOperationContext;

	private PurchaseRepository purchaseRepository;

	private PricingSnapshotRepository pricingSnapshotRepository;

	private CustomerSessionRepository customerSessionRepository;

	private ConversionService conversionService;

	private CartHasItemsService cartHasItemsService;

	@Override
	public Single<PurchaseEntity> findOne(final PurchaseIdentifier identifier) {
		String scope = identifier.getPurchases().getScope().getValue();
		String purchaseId = identifier.getPurchaseId().getValue();
		return orderRepository.findByGuidAsSingle(scope, purchaseId)
				.map(this::convertOrderToPurchaseEntity);
	}

	@Override
	public Observable<PurchaseIdentifier> findAll(final IdentifierPart<String> scope) {
		String userId = resourceOperationContext.getUserIdentifier();
		return orderRepository.findOrderIdsByCustomerGuid(scope.getValue(), userId)
				.map(purchaseId -> PurchaseIdentifier.builder()
						.withPurchases(PurchasesIdentifier.builder()
								.withScope(scope)
								.build())
						.withPurchaseId(StringIdentifier.of(purchaseId))
						.build());
	}

	@Override
	public Single<SubmitResult<PurchaseIdentifier>> submit(final PurchaseEntity purchaseEntity, final IdentifierPart<String> scope) {
		String orderId = purchaseEntity.getOrderId();
		String store = scope.getValue();


		return isOrderPurchasable(scope, orderId)
				.flatMap(purchasable -> {
					if (purchasable) {
						return createPurchaseIdentifier(orderId, store)
								.map(purchaseIdentifier -> SubmitResult.<PurchaseIdentifier>builder()
										.withIdentifier(purchaseIdentifier)
										.withStatus(SubmitStatus.CREATED)
										.build());
					}
					return Single.error(ResourceOperationFailure.serverError(NOT_PURCHASABLE));
				});
	}

	private Single<Boolean> isOrderPurchasable(final IdentifierPart<String> scope, final String orderId) {
		OrderIdentifier identifier = OrderIdentifier.builder()
				.withOrderId(StringIdentifier.of(orderId))
				.withScope(scope)
				.build();

		return cartHasItemsService.isCartEmpty(identifier).map(isCartEmpty -> !isCartEmpty);
	}

	/**
	 * Convert order to purchase entity.
	 *
	 * @param order order
	 * @return purchase entity
	 */
	protected PurchaseEntity convertOrderToPurchaseEntity(final Order order) {
		return conversionService.convert(order, PurchaseEntity.class);
	}

	/**
	 * Construct purchase identifier.
	 *
	 * @param orderId order id
	 * @param store   store
	 * @return purchase identifier
	 */
	protected Single<PurchaseIdentifier> createPurchaseIdentifier(final String orderId, final String store) {
		return cartOrderRepository.findByGuidAsSingle(store, orderId)
				.flatMap(cartOrder -> createOrderPaymentFromCartOrder(cartOrder)
						.flatMap(orderPayment -> getShoppingCart(StringIdentifier.of(store), cartOrder)
								.flatMap(shoppingCart -> pricingSnapshotRepository.getShoppingCartTaxSnapshot(shoppingCart)
										.flatMap(taxSnapshot -> customerSessionRepository.findOrCreateCustomerSessionAsSingle()
												.flatMap(customerSession -> checkout(shoppingCart, taxSnapshot, customerSession, orderPayment))))))
				.map(purchaseId -> buildPurchaseIdentifier(store, purchaseId));
	}

	/**
	 * Build the PurchaseIdentifier.
	 *
	 * @param scope      scope
	 * @param purchaseId purchaseId
	 * @return purchase identifier
	 */
	protected PurchaseIdentifier buildPurchaseIdentifier(final String scope, final String purchaseId) {
		return PurchaseIdentifier.builder()
				.withPurchases(PurchasesIdentifier.builder()
						.withScope(StringIdentifier.of(scope))
						.build())
				.withPurchaseId(StringIdentifier.of(purchaseId))
				.build();
	}

	/**
	 * Get the shopping cart.
	 *
	 * @param scope     scope
	 * @param cartOrder cartOrder
	 * @return the shopping cart
	 */
	protected Single<ShoppingCart> getShoppingCart(final IdentifierPart<String> scope, final CartOrder cartOrder) {
		return cartOrderRepository.getEnrichedShoppingCartSingle(scope.getValue(), cartOrder);
	}

	/**
	 * Create the order payment from cart order.
	 *
	 * @param cartOrder cartOrder
	 * @return the order payment
	 */
	protected Single<OrderPayment> createOrderPaymentFromCartOrder(final CartOrder cartOrder) {
		PaymentMethod paymentMethod = cartOrder.getPaymentMethod();
		if (paymentMethod == null) {
			return createEmptyPaymentTokenOrderPaymentForZeroTotalPurchase();
		} else {
			return purchaseRepository.getOrderPaymentFromPaymentMethod(paymentMethod);
		}
	}

	/**
	 * Create an empty paymentToken order payment.
	 *
	 * @return empty paymentToken order payment
	 */
	protected Single<OrderPayment> createEmptyPaymentTokenOrderPaymentForZeroTotalPurchase() {
		return purchaseRepository.createNewOrderPaymentEntity()
				.map(this::setOrderPaymentAsPaymentToken);
	}

	/**
	 * Set the Order payment method as a payment token.
	 *
	 * @param orderPayment orderPayment
	 * @return orderPayment
	 */
	protected OrderPayment setOrderPaymentAsPaymentToken(final OrderPayment orderPayment) {
		orderPayment.setPaymentMethod(PaymentType.PAYMENT_TOKEN);
		return orderPayment;
	}

	/**
	 * Checkout.
	 *
	 * @param shoppingCart    shoppingCart
	 * @param taxSnapshot     taxSnapshot
	 * @param customerSession customerSession
	 * @param orderPayment    order payment
	 * @return purchase id
	 */
	protected Single<String> checkout(
			final ShoppingCart shoppingCart,
			final ShoppingCartTaxSnapshot taxSnapshot,
			final CustomerSession customerSession,
			final OrderPayment orderPayment) {

		return purchaseRepository.checkout(shoppingCart, taxSnapshot, customerSession, orderPayment)
				.flatMap(this::handleCheckoutResults);
	}

	/**
	 * Handles the checkout result. Returning the purchase id if the checkout succeeded and an error if checkout fails.
	 *
	 * @param checkoutResult checkoutResult
	 * @return the purchase id if the checkout succeeded and an error if checkout fails
	 */
	protected Single<String> handleCheckoutResults(final CheckoutResults checkoutResult) {
		if (checkoutResult.isOrderFailed()) {
			return handleCheckoutFailure(checkoutResult.getFailureCause());
		} else {
			return Single.just(checkoutResult.getOrder().getGuid());
		}
	}

	/**
	 * Handles the checkout process failure.
	 *
	 * @param checkoutException checkoutException
	 * @return the error wrapped in a Single
	 */
	protected Single<String> handleCheckoutFailure(final RuntimeException checkoutException) {
		if (checkoutException instanceof EpServiceException) {
			String message = ExceptionUtils.getRootCauseMessage(checkoutException);
			return Single.error(ResourceOperationFailure.stateFailure(message));
		} else {
			return Single.error(ResourceOperationFailure.stateFailure("The purchase failed: " + checkoutException.getMessage()));
		}
	}

	@Reference
	public void setCartOrderRepository(final CartOrderRepository cartOrderRepository) {
		this.cartOrderRepository = cartOrderRepository;
	}

	@Reference
	public void setPurchaseRepository(final PurchaseRepository purchaseRepository) {
		this.purchaseRepository = purchaseRepository;
	}

	@Reference
	public void setPricingSnapshotRepository(final PricingSnapshotRepository pricingSnapshotRepository) {
		this.pricingSnapshotRepository = pricingSnapshotRepository;
	}

	@Reference
	public void setCustomerSessionRepository(final CustomerSessionRepository customerSessionRepository) {
		this.customerSessionRepository = customerSessionRepository;
	}

	@Reference
	public void setOrderRepository(final OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	@Reference
	public void setResourceOperationContext(final ResourceOperationContext resourceOperationContext) {
		this.resourceOperationContext = resourceOperationContext;
	}

	@Reference
	public void setConversionService(final ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	@Reference
	public void setCartHasItemsService(final CartHasItemsService cartHasItemsService) {
		this.cartHasItemsService = cartHasItemsService;
	}


}
