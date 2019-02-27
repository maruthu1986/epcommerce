/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.integration.epcommerce.repository.coupon.order;

import java.util.Set;

import io.reactivex.Completable;
import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.repository.Repository;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.definition.coupons.OrderCouponIdentifier;
import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.definition.orders.OrdersMediaTypes;
import com.elasticpath.rest.form.SubmitResult;
import com.elasticpath.rest.form.SubmitStatus;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.coupon.CouponEntityBuilder;
import com.elasticpath.rest.resource.integration.epcommerce.repository.coupon.CouponRepository;

/**
 * Repository for the coupon entity which is retrieved for each coupon applied to order.
 *
 * @param <E> the entity type
 * @param <I> the identifier type
 */
@Component
public class OrderCouponToCouponinfoRepository<E extends CouponEntity, I extends OrderCouponIdentifier>
		implements Repository<CouponEntity, OrderCouponIdentifier> {

	/**
	 * Coupon not found for the order.
	 */
	static final String COUPON_IS_NOT_FOUND = "Coupon is not found for order.";

	private CartOrderRepository cartOrderRepository;
	private ShoppingCartRepository shoppingCartRepository;
	private CouponRepository couponRepository;
	private CouponEntityBuilder builder;

	@Override
	public Single<SubmitResult<OrderCouponIdentifier>> submit(final CouponEntity entity, final IdentifierPart<String> scope) {
		String orderId = entity.getParentId();
		String storeCode = scope.getValue();
		String couponCode = entity.getCode();
		return cartOrderRepository.findByGuidAsSingle(storeCode, orderId)
				.flatMap(cartOrder -> shoppingCartRepository.getShoppingCart(cartOrder.getShoppingCartGuid())
						.map(shoppingCart -> shoppingCart.getShopper().getCustomer().getEmail())
						.flatMap(shopperEmail -> couponRepository.validateCoupon(couponCode, storeCode, shopperEmail)
								.andThen(buildOrderCouponIdentifier(cartOrder, storeCode, couponCode, orderId))));
	}

	@Override
	public Single<CouponEntity> findOne(final OrderCouponIdentifier identifier) {
		String couponId = identifier.getCouponId().getValue();
		String scope = identifier.getOrder().getScope().getValue();
		String orderId = identifier.getOrder().getOrderId().getValue();

		return getCouponDetailsForOrder(scope, orderId, couponId);
	}

	@Override
	public Completable delete(final OrderCouponIdentifier identifier) {
		String couponId = identifier.getCouponId().getValue();
		String storeCode = identifier.getOrder().getScope().getValue();
		String cartOrderGuid = identifier.getOrder().getOrderId().getValue();

		return cartOrderRepository.findByGuidAsSingle(storeCode, cartOrderGuid)
				.flatMap(cartOrder -> Single.just(cartOrder.removeCoupon(couponId)) //Save cart order after coupons have been removed
						.flatMap(removed -> removed ? cartOrderRepository.saveCartOrderAsSingle(cartOrder) : Single.never()))
				.toCompletable();
	}

	/**
	 * Create appropriate coupon identifier.
	 * If coupon not valid throw an error.
	 *
	 * @param cartOrder cart order
	 * @param storeCode store code
	 * @param couponCode coupon code
	 * @param orderId order itd
	 * @return submit result of coupon identifier
	 */
	protected Single<SubmitResult<OrderCouponIdentifier>> buildOrderCouponIdentifier(
			final CartOrder cartOrder, final String storeCode, final String couponCode, final String orderId) {
			return couponRepository.findByCouponCode(couponCode)
					.flatMap(coupon -> Single.just(cartOrder.addCoupon(coupon.getCouponCode()))
							.flatMap(isNewlyAdded -> handleCouponCreation(cartOrder, storeCode, orderId, coupon, isNewlyAdded)));
	}

	/**
	 * Update cart order if required and construct identifier with appropriate status code.
	 *
	 * @param cartOrder cart order
	 * @param storeCode store code
	 * @param orderId order id
	 * @param coupon coupon
	 * @param isNewlyAdded is this coupon CREATED or it is EXISTING
	 * @return submit result of order coupon identifier
	 */
	protected Single<SubmitResult<OrderCouponIdentifier>> handleCouponCreation(
			final CartOrder cartOrder, final String storeCode, final String orderId, final Coupon coupon, final boolean isNewlyAdded) {

		Single<CouponEntity> couponEntity;
		if (isNewlyAdded) {
			//Save cart order if new coupon has been added (propagate error if needed)
			couponEntity = cartOrderRepository.saveCartOrderAsSingle(cartOrder)
					.flatMap(order -> builder.build(coupon, OrdersMediaTypes.ORDER.id(), orderId));
		} else {
			couponEntity = builder.build(coupon, OrdersMediaTypes.ORDER.id(), orderId);
		}

		return couponEntity
				.map(entity -> constructOrderCouponIdentifier(orderId, storeCode, entity, isNewlyAdded));
	}

	/**
	 * Build order coupon identifier.
	 *
	 * @param cartOrderGuid cart order
	 * @param storeCode store code
	 * @param couponEntity coupon entity to build from
	 * @param isNewlyAdded is this coupon CREATED or it is EXISTING
	 * @return order coupon identifier
	 */
	protected SubmitResult<OrderCouponIdentifier> constructOrderCouponIdentifier(final String cartOrderGuid, final String storeCode,
																				 final CouponEntity couponEntity, final boolean isNewlyAdded) {

		OrderCouponIdentifier identifier = OrderCouponIdentifier.builder()
				.withCouponId(StringIdentifier.of(couponEntity.getCode()))
				.withOrder(OrderIdentifier.builder()
						.withOrderId(StringIdentifier.of(cartOrderGuid))
						.withScope(StringIdentifier.of(storeCode))
						.build())
				.build();

		return SubmitResult.<OrderCouponIdentifier>builder()
				.withIdentifier(identifier)
				.withStatus(isNewlyAdded
						? SubmitStatus.CREATED
						: SubmitStatus.EXISTING)
				.build();
	}


	/**
	 * Get coupon details for the order.
	 *
	 * @param storeCode store code
	 * @param orderId cart order guid
	 * @param couponId id for the coupon
	 * @return coupon entity
	 */
	protected Single<CouponEntity> getCouponDetailsForOrder(final String storeCode, final String orderId, final String couponId) {
		return cartOrderRepository.findByGuidAsSingle(storeCode, orderId)
				.map(CartOrder::getCouponCodes)
				.flatMap(couponCodes -> getCouponEntity(couponId, couponCodes, orderId));
	}

	/**
	 * Get coupon entity.
	 *
	 * @param couponId coupon id
	 * @param couponCodes coupon codes
	 * @param orderId order id
	 * @return coupon entity
	 */
	protected Single<CouponEntity> getCouponEntity(final String couponId, final Set<String> couponCodes, final String orderId) {
		if (couponCodes.contains(couponId)) {
			return couponRepository.findByCouponCode(couponId)
					.flatMap(coupon -> builder.build(coupon, OrdersMediaTypes.ORDER.id(), orderId));
		}
		return Single.error(ResourceOperationFailure.notFound(COUPON_IS_NOT_FOUND));
	}

	@Reference
	public void setCouponEntityBuilder(final CouponEntityBuilder builder) {
		this.builder = builder;
	}

	@Reference
	public void setCartOrderRepository(final CartOrderRepository cartOrderRepository) {
		this.cartOrderRepository = cartOrderRepository;
	}

	@Reference
	public void setCouponRepository(final CouponRepository couponRepository) {
		this.couponRepository = couponRepository;
	}

	@Reference
	public void setShoppingCartRepository(final ShoppingCartRepository shoppingCartRepository) {
		this.shoppingCartRepository = shoppingCartRepository;
	}

}
