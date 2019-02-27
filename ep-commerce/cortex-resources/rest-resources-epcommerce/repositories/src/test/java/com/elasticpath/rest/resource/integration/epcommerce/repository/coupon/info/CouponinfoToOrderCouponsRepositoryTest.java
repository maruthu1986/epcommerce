package com.elasticpath.rest.resource.integration.epcommerce.repository.coupon.info;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.IdentifierTestFactory.buildOrderIdentifier;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.CART_ORDER_GUID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.COUPON_CODE;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SCOPE;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.coupon.CouponTestFactory.buildCouponinfoIdentifier;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.coupon.CouponTestFactory.buildOrderCouponIdentifier;

import java.util.Collections;
import java.util.HashSet;

import com.google.common.collect.ImmutableList;
import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.coupons.CouponinfoIdentifier;
import com.elasticpath.rest.definition.coupons.OrderCouponIdentifier;
import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;

/**
 * Test for {@link CouponinfoToOrderCouponsRepository}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CouponinfoToOrderCouponsRepositoryTest {

	private static final String ORDER_WITH_GUID_NOT_FOUND =
			String.format("No cart order with GUID %s was found in store %s.", CART_ORDER_GUID, SCOPE);

	@Mock
	private CartOrderRepository cartOrderRepository;

	@Mock
	private CartOrder cartOrder;

	@InjectMocks
	private CouponinfoToOrderCouponsRepository<CouponinfoIdentifier, OrderCouponIdentifier> couponinfoToOrderCouponsRepository;

	@Before
	public void setUp() {
		when(cartOrderRepository.findByGuidAsSingle(SCOPE, CART_ORDER_GUID)).thenReturn(Single.just(cartOrder));
	}

	@Test
	public void getElementsReturnsNotFoundErrorWhenNoCartOrderFoundForTheGivenOrderGuid() {
		when(cartOrderRepository.findByGuidAsSingle(SCOPE, CART_ORDER_GUID))
				.thenReturn(Single.error(ResourceOperationFailure.notFound(ORDER_WITH_GUID_NOT_FOUND)));

		couponinfoToOrderCouponsRepository.getElements(
				buildCouponinfoIdentifier(SCOPE, CART_ORDER_GUID))
				.test()
				.assertError(ResourceOperationFailure.notFound(ORDER_WITH_GUID_NOT_FOUND));
	}

	@Test
	public void getElementsReturnsNoValuesWhenNoCouponsFoundForTheRetrievedCartOrder() {
		couponinfoToOrderCouponsRepository.getElements(
				buildCouponinfoIdentifier(SCOPE, CART_ORDER_GUID))
				.test()
				.assertNoErrors()
				.assertNoValues()
				.assertComplete();
	}

	@Test
	public void getElementsReturnsOrderCouponIdentifierWhenTheCartOrderHasACoupon() {
		OrderCouponIdentifier orderCouponIdentifier = buildOrderCouponIdentifier(COUPON_CODE, CART_ORDER_GUID, SCOPE);

		when(cartOrder.getCouponCodes()).thenReturn(Collections.singleton(COUPON_CODE));

		couponinfoToOrderCouponsRepository.getElements(
				buildCouponinfoIdentifier(SCOPE, CART_ORDER_GUID))
				.test()
				.assertValue(identifier -> identifier.equals(orderCouponIdentifier));
	}

	@Test
	public void getElementsReturnsObservableOfOrderCouponIdentifiersWhenTheCartOrderHasMultipleCoupons() {
		String couponCode = "coupon code 2";

		OrderCouponIdentifier orderCouponIdentifier = buildOrderCouponIdentifier(COUPON_CODE, CART_ORDER_GUID, SCOPE);

		OrderCouponIdentifier orderCouponIdentifier2 = buildOrderCouponIdentifier(couponCode, CART_ORDER_GUID, SCOPE);

		when(cartOrder.getCouponCodes())
				.thenReturn(new HashSet<>(ImmutableList.of(couponCode, COUPON_CODE)));

		couponinfoToOrderCouponsRepository.getElements(
				buildCouponinfoIdentifier(SCOPE, CART_ORDER_GUID))
				.test()
				.assertValueCount(2)
				.assertValues(orderCouponIdentifier2, orderCouponIdentifier);
	}

	@Test
	public void testBuildOrderCouponIdentifier() {
		OrderIdentifier orderIdentifier = buildOrderIdentifier(SCOPE, CART_ORDER_GUID);

		OrderCouponIdentifier orderCouponIdentifier = buildOrderCouponIdentifier(COUPON_CODE, CART_ORDER_GUID, SCOPE);

		assertThat(couponinfoToOrderCouponsRepository.buildOrderCouponIdentifier(orderIdentifier, COUPON_CODE)).isEqualTo(orderCouponIdentifier);
	}
}