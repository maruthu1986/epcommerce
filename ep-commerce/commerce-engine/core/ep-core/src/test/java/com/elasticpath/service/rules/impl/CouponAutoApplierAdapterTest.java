/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.rules.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.rules.CouponAutoApplierService;

/**
 * Coupon auto adapter test.
 */
public class CouponAutoApplierAdapterTest {

	private static final String EMAIL = "EMAIL";

	private static final String EXISTING = "EXISTING";

	private static final String COUPON_TO_ADD = "COUPON_TO_ADD";

	private static final String COUPON_TO_REMOVE = "COUPON_TO_REMOVE";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final CouponAutoApplierService couponAutoApplierService = context.mock(CouponAutoApplierService.class);

	private final CouponAutoApplierAdapter adapter = new CouponAutoApplierAdapter();

	private final Set<String> toRemove = new HashSet<>();

	private final Set<String> toApply = new HashSet<>();

	private ShoppingCart cart;

	private final CartOrder mockCartOrder = context.mock(CartOrder.class);

	private final Store mockStore = context.mock(Store.class);

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		adapter.setCouponAutoApplierService(couponAutoApplierService);

		cart = createShoppingCart();
		cart.applyPromotionCode(EXISTING);

		context.checking(new Expectations() {
			{
				allowing(couponAutoApplierService)
						.filterValidCouponsForCustomer(with(any(Set.class)), with(any(Store.class)), with(EMAIL));
				will(returnValue(toRemove));

				allowing(couponAutoApplierService).retrieveCouponsApplicableToAutoApply(cart.getStore(), EMAIL);
				will(returnValue(toApply));

				ignoring(mockCartOrder).getCouponCodes();
			}
		});

	}

	/**
	 * Tests apply coupon to cart.
	 */
	@Test
	public void testAutoApplicationForShoppingCart() {
		toApply.add(COUPON_TO_ADD);

		boolean result = adapter.filterAndAutoApplyCoupons(cart);

		assertTrue("Shopping cart should be updated by adding coupons", result);
		assertEquals("Expects that 2 coupons were applied to the cart", 2, cart.getPromotionCodes().size());
	}

	/**
	 * Tests remove user specific coupons.
	 */
	@Test
	public void testFilteringForShoppingCart() {
		cart.applyPromotionCode(COUPON_TO_REMOVE);
		toRemove.add(COUPON_TO_REMOVE);

		boolean result = adapter.filterAndAutoApplyCoupons(cart);

		assertTrue("Shopping cart should be updated by removing coupons", result);
		assertEquals("Expects only existing coupon not mark for remove to exist.", 1, cart.getPromotionCodes().size());
	}
	
	/**
	 * Tests remove user specific coupons.
	 */
	@Test
	public void testNoUpdatesOnAutoApplyForShoppingCart() {
		
		boolean result = adapter.filterAndAutoApplyCoupons(cart);

		assertFalse("Shopping cart should be not be updated", result);
		assertEquals("Expects only existing coupon not mark for remove to exist.", 1, cart.getPromotionCodes().size());
	}
	
	/**
	 * Tests apply coupon to cart.
	 */
	@Test
	public void testAutoApplicationForCartOrder() {
		toApply.add(COUPON_TO_ADD);
		
		verifyCartOrderAddsCoupon();

		boolean result = adapter.filterAndAutoApplyCoupons(mockCartOrder, mockStore, EMAIL);

		assertTrue("Cart Order should be updated by adding coupons", result);
	}

	/**
	 * Tests remove user specific coupons.
	 */
	@Test
	public void testFilteringForCartOrder() {
		toRemove.add(COUPON_TO_REMOVE);

		verifyCartOrderRemovesCoupon();

		boolean result = adapter.filterAndAutoApplyCoupons(mockCartOrder, mockStore, EMAIL);

		assertTrue("Cart Order should be updated by removing coupons", result);
	}
	
	/**
	 * Tests remove user specific coupons.
	 */
	@Test
	public void testNoUpdatesOnAutoApplyForCartOrder() {

		context.checking(new Expectations() {
			{
				allowing(mockCartOrder).addCoupons(toApply);
				will(returnValue(false));

				allowing(mockCartOrder).removeCoupons(toRemove);
				will(returnValue(false));
			}
		});

		boolean result = adapter.filterAndAutoApplyCoupons(mockCartOrder, mockStore, EMAIL);

		assertFalse("Cart Order should not be updated", result);
	}

	private ShoppingCart createShoppingCart() {
		final ShoppingCart cart = new ShoppingCartImpl() {

			private static final long serialVersionUID = -4855082756391799003L;

			private Set<String> codes = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

			private Shopper shopper;

			@Override
			public boolean applyPromotionCode(final String promotionCode) {
				codes.add(promotionCode);
				return true;
			}

			@Override
			public void removePromotionCode(final String promotionCode) {
				codes.remove(promotionCode);
			}

			@Override
			public boolean removePromotionCodes(final Collection<String> promotionCodes) {

				return codes.removeAll(promotionCodes);
			}

			@Override
			public boolean applyPromotionCodes(final Collection<String> promotionCodes) {

				return codes.addAll(promotionCodes);
			}

			@Override
			public Set<String> getPromotionCodes() {
				return codes;
			}

			@Override
			public void setShopper(final Shopper shopper) {
				this.shopper = shopper;
			}

			@Override
			public Shopper getShopper() {
				return shopper;
			}

			@Override
			public Store getStore() {
				return mockStore;
			}
		};

		final Customer customer = context.mock(Customer.class);
		final Shopper shopper = context.mock(Shopper.class);
		context.checking(new Expectations() {
			{
				allowing(shopper).getCustomer();
				will(returnValue(customer));

				allowing(customer).getEmail();
				will(returnValue(EMAIL));
			}
		});
		cart.setShopper(shopper);
		return cart;
	}

	private void verifyCartOrderAddsCoupon() {
		context.checking(new Expectations() {
			{
				allowing(mockCartOrder).addCoupons(toApply);
				will(returnValue(true));

				allowing(mockCartOrder).removeCoupons(toRemove);
				will(returnValue(false));
			}
		});
	}

	private void verifyCartOrderRemovesCoupon() {
		context.checking(new Expectations() {
			{
				allowing(mockCartOrder).addCoupons(toApply);
				will(returnValue(false));

				allowing(mockCartOrder).removeCoupons(toRemove);
				will(returnValue(true));
			}
		});
	}
}
