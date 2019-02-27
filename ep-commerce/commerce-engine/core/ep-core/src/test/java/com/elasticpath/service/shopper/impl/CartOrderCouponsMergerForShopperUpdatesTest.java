/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.shopper.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;

import java.util.Arrays;
import java.util.Collections;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.cartorder.impl.CartOrderImpl;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.cartorder.CartOrderService;

/**
 * Tests {@link CartOrderCouponsMergerForShopperUpdates}.
 */
public class CartOrderCouponsMergerForShopperUpdatesTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private CartOrderService cartOrderService;

	private CartOrderCouponsMergerForShopperUpdates cartOrderCouponsMergerForShopperUpdates;

	private CustomerSession customerSession;

	private final CartOrder cartOrderWithCoupons = new CartOrderImpl();

	private final CartOrder emptyCartOrder = new CartOrderImpl();

	private static final String[] COUPON_CODES = {"COUPON_ONE", "COUPON_TWO", "COUPON_THREE"};

	private static final String MYSTERY_MAN = "MysteryMan";

	private static final String REGULAR_JOE = "RegularJoe";

	@Before
	public void setUp() throws Exception {
		cartOrderWithCoupons.addCoupons(Arrays.asList(COUPON_CODES));
		cartOrderService = context.mock(CartOrderService.class);
		customerSession = context.mock(CustomerSession.class);
		cartOrderCouponsMergerForShopperUpdates = new CartOrderCouponsMergerForShopperUpdates(cartOrderService);

		context.checking(new Expectations() {
			{
				allowing(cartOrderService).createOrderIfPossible(with(any(ShoppingCart.class)));
				allowing(cartOrderService).saveOrUpdate(with(any(CartOrder.class)));
			}
		});

	}

	@Test
	public void testCouponsMergeWhenTransitioningFromAnonymousToRegistered() {
		Shopper anonymousShopper = createShopper(MYSTERY_MAN, cartOrderWithCoupons);
		Shopper registeredShopper = createShopper(REGULAR_JOE, emptyCartOrder);

		context.checking(new Expectations() {
			{
				oneOf(cartOrderService).getCartOrderCouponCodesByShoppingCartGuid(MYSTERY_MAN);
				will(returnValue(Arrays.asList(COUPON_CODES)));
			}
		});

		arrangeCustomerSessionToReturnShopper(registeredShopper);

		cartOrderCouponsMergerForShopperUpdates.invalidateShopper(customerSession, anonymousShopper);

		assertThat(emptyCartOrder.getCouponCodes(), hasItems(COUPON_CODES));
	}

	@Test
	public void testNoCouponsMergeWhenInvalidShopperHasNoCartOrder() {
		Shopper anonymousShopper = createShopper(MYSTERY_MAN, null);
		Shopper registeredShopper = createShopper(REGULAR_JOE, emptyCartOrder);

		context.checking(new Expectations() {
			{
				oneOf(cartOrderService).getCartOrderCouponCodesByShoppingCartGuid(MYSTERY_MAN);
				will(returnValue(Collections.emptyList()));
			}
		});

		arrangeCustomerSessionToReturnShopper(registeredShopper);

		cartOrderCouponsMergerForShopperUpdates.invalidateShopper(customerSession, anonymousShopper);

		assertThat(emptyCartOrder.getCouponCodes(), not(hasItems(COUPON_CODES)));
	}


	private void arrangeCustomerSessionToReturnShopper(final Shopper shopper) {
		context.checking(new Expectations() {
			{
				allowing(customerSession).getShopper();
				will(returnValue(shopper));
			}
		});
	}

	private Shopper createShopper(final String shopperName, final CartOrder cartOrder) {

		final Shopper shopper = context.mock(Shopper.class, shopperName + "Shopper");
		final Customer customer = context.mock(Customer.class, shopperName + "Customer");
		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class, shopperName + "Cart");

		context.checking(new Expectations() {
			{
				allowing(shopper).getCustomer();
				will(returnValue(customer));

				allowing(shopper).getCurrentShoppingCart();
				will(returnValue(shoppingCart));

				allowing(shoppingCart).getGuid();
				will(returnValue(shopperName));

				allowing(cartOrderService).findByShoppingCartGuid(shopperName);
				will(returnValue(cartOrder));
			}
		});
		return shopper;
	}

}
