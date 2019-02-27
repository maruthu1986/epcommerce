/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.integration.epcommerce.repository.coupon.info;

import io.reactivex.Observable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.coupons.CouponinfoIdentifier;
import com.elasticpath.rest.definition.coupons.OrderCouponIdentifier;
import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;

/**
 * Repository that maps coupon information to the list of coupons applied to the order.
 *
 * @param <CI> Coupon Info identifier
 * @param <OC> Order Coupon identifier
 */
@Component
public class CouponinfoToOrderCouponsRepository<CI extends CouponinfoIdentifier, OC extends OrderCouponIdentifier>
		implements LinksRepository<CouponinfoIdentifier, OrderCouponIdentifier> {

	private CartOrderRepository cartOrderRepository;

	@Override
	public Observable<OrderCouponIdentifier> getElements(final CouponinfoIdentifier identifier) {
		OrderIdentifier order = identifier.getOrder();
		String storeCode = identifier.getOrder().getScope().getValue();
		String cartOrderGuid = identifier.getOrder().getOrderId().getValue();

		return cartOrderRepository.findByGuidAsSingle(storeCode, cartOrderGuid)
				.flatMapObservable(cartOrder -> Observable.fromIterable(cartOrder.getCouponCodes())
						.map(couponCode -> buildOrderCouponIdentifier(order, couponCode)));
	}

	/**
	 * Build order coupon identifier.
	 *
	 * @param order      order
	 * @param couponCode coupon code
	 * @return order coupon identifier
	 */
	protected OrderCouponIdentifier buildOrderCouponIdentifier(final OrderIdentifier order, final String couponCode) {
		IdentifierPart<String> objectIdentifierPart = StringIdentifier.of(couponCode);

		return OrderCouponIdentifier.builder()
				.withCouponId(objectIdentifierPart)
				.withOrder(order)
				.build();
	}

	@Reference
	public void setCartOrderRepository(final CartOrderRepository cartOrderRepository) {
		this.cartOrderRepository = cartOrderRepository;
	}

}
