/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.promotions.wizard;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.elasticpath.cmclient.core.promotions.CouponCollectionModel;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.domain.rules.CouponUsageType;
import com.elasticpath.service.rules.CouponService;
import com.elasticpath.service.rules.CouponUsageService;

/**
 * Tests the {@code CouponUsageController}. Note that this tests the integration
 * between CouponUsageCollectionModel and CouponUsageController so that changes
 * in the interface between those two classes can be tested.
 */
@SuppressWarnings({ "PMD.AvoidDuplicateLiterals" })
public class CouponUsageControllerTest {

	@Rule
	public final MockitoRule rule = MockitoJUnit.rule();

	/**
	 * Tests that adding a single coupon usage resulting in a coupon and a coupon
	 * usage being added to the database.
	 */
	@Test
	public void testAdd() {
		CouponCollectionModel model = new CouponCollectionModel(CouponUsageType.LIMIT_PER_SPECIFIED_USER);
		
		final CouponService couponService = mock(CouponService.class);
		final CouponUsageService couponUsageService = mock(CouponUsageService.class);
		final Coupon coupon = mock(Coupon.class);
		final CouponUsage couponUsage = mock(CouponUsage.class);
		final CouponConfig couponConfig = mock(CouponConfig.class);
		
		CouponUsageController controller = new CouponUsageController() {
			@Override
			public CouponService getCouponService() {
				return couponService;
			}
			
			@Override
			public CouponUsageService getCouponUsageService() {
				return couponUsageService;
			}
			
			@Override
			Coupon getNewCoupon() {
				return coupon;
			}
			
			@Override
			CouponUsage getNewCouponUsage() {
				return couponUsage;
			}
		};
		
		final Coupon addedCoupon = mock(Coupon.class, "addedCoupon"); //$NON-NLS-1$
		
		when(couponService.add(coupon)).thenReturn(addedCoupon);

		model.add("ABC", "test@test.com"); //$NON-NLS-1$ //$NON-NLS-2$
		model.setCouponConfig(couponConfig);
		controller.updateDatabase(model);

		verify(coupon).setCouponCode("ABC"); //$NON-NLS-1$
		verify(coupon).setCouponConfig(couponConfig);
		verify(couponUsage).setCustomerEmailAddress("test@test.com"); //$NON-NLS-1$
		verify(couponUsage).setUseCount(0);
		verify(couponUsage).setActiveInCart(true);
		verify(couponUsage).setCoupon(addedCoupon);
		verify(couponService).add(coupon);
		verify(couponUsageService).add(couponUsage);
	}
	
	/**
	 * Tests that adding two coupon usages with the same coupon results in a single coupon and two coupon
	 * usage being added to the database.
	 */
	@Test
	public void testAddSameCoupon() {
		CouponCollectionModel model = new CouponCollectionModel(CouponUsageType.LIMIT_PER_SPECIFIED_USER);
				
		final CouponUsage couponUsage = mock(CouponUsage.class);
		final CouponUsage couponUsage2 = mock(CouponUsage.class, "couponUsage2"); //$NON-NLS-1$
		final Coupon coupon = mock(Coupon.class);
		final Coupon addedCoupon = mock(Coupon.class, "addedCoupon"); //$NON-NLS-1$
		final CouponService couponService = mock(CouponService.class);
		final CouponUsageService couponUsageService = mock(CouponUsageService.class);
		final CouponConfig couponConfig = mock(CouponConfig.class);
		
		CouponUsageController controller = new CouponUsageController() {
			@Override
			public CouponService getCouponService() {
				return couponService;
			}
			
			@Override
			public CouponUsageService getCouponUsageService() {
				return couponUsageService;
			}
			
			@Override
			Coupon getNewCoupon() {
				return coupon;
			}
			
			private int couponUsageInvocationCount;
			@Override
			CouponUsage getNewCouponUsage() {
				couponUsageInvocationCount++;
				if (couponUsageInvocationCount == 1) {
					return couponUsage;
				}
				return couponUsage2;				
			}
		};

		when(couponService.add(coupon)).thenReturn(addedCoupon);

		model.setCouponConfig(couponConfig);
		model.add("ABC", "test@test.com"); //$NON-NLS-1$ //$NON-NLS-2$
		model.add("ABC", "test2@test.com"); //$NON-NLS-1$ //$NON-NLS-2$
		controller.updateDatabase(model);

		verify(coupon).setCouponCode("ABC"); //$NON-NLS-1$
		verify(coupon).setCouponConfig(couponConfig);
		verify(couponUsage).setCoupon(addedCoupon);
		verify(couponUsage).setUseCount(0);
		verify(couponUsage).setActiveInCart(true);
		verify(couponUsage2).setCoupon(addedCoupon);
		verify(couponUsage2).setUseCount(0);
		verify(couponUsage2).setActiveInCart(true);

		verify(couponService).add(coupon);
		verify(couponUsageService).add(couponUsage);
		verify(couponUsageService).add(couponUsage2);
	}
	
	/**
	 * Tests that adding a single coupon resulting in a coupon being added to the database.
	 */
	@Test
	public void testAddNoEmail() {
		CouponCollectionModel model = new CouponCollectionModel(CouponUsageType.LIMIT_PER_COUPON);
		
		final CouponService couponService = mock(CouponService.class);
		final CouponUsageService couponUsageService = mock(CouponUsageService.class);
		final Coupon coupon = mock(Coupon.class);
		final CouponUsage couponUsage = mock(CouponUsage.class);
		final CouponConfig couponConfig = mock(CouponConfig.class);
		
		CouponUsageController controller = new CouponUsageController() {
			@Override
			public CouponService getCouponService() {
				return couponService;
			}
			
			@Override
			public CouponUsageService getCouponUsageService() {
				return couponUsageService;
			}
			
			@Override
			Coupon getNewCoupon() {
				return coupon;
			}
			
			@Override
			CouponUsage getNewCouponUsage() {
				return couponUsage;
			}
		};
		
		final Coupon addedCoupon = mock(Coupon.class, "addedCoupon"); //$NON-NLS-1$
		
		when(couponService.add(coupon)).thenReturn(addedCoupon);

		model.add("ABC"); //$NON-NLS-1$
		model.setCouponConfig(couponConfig);
		controller.updateDatabase(model);
		verify(coupon).setCouponConfig(couponConfig);
		verify(couponService).add(coupon);
	}
	
	/**
	 * Tests that adding a single coupon results in a coupon being added to the database.
	 * Tests that, when the email address is not null but empty, that no coupon usage is created.
	 */
	@Test
	public void testAddEmailEmpty() {
		CouponCollectionModel model = new CouponCollectionModel(CouponUsageType.LIMIT_PER_SPECIFIED_USER);
		
		final CouponService couponService = mock(CouponService.class);
		final CouponUsageService couponUsageService = mock(CouponUsageService.class);
		final Coupon coupon = mock(Coupon.class);
		final CouponUsage couponUsage = mock(CouponUsage.class);
		final CouponConfig couponConfig = mock(CouponConfig.class);
		
		CouponUsageController controller = new CouponUsageController() {
			@Override
			public CouponService getCouponService() {
				return couponService;
			}
			
			@Override
			public CouponUsageService getCouponUsageService() {
				return couponUsageService;
			}
			
			@Override
			Coupon getNewCoupon() {
				return coupon;
			}
			
			@Override
			CouponUsage getNewCouponUsage() {
				return couponUsage;
			}
		};
		
		final Coupon addedCoupon = mock(Coupon.class, "addedCoupon"); //$NON-NLS-1$
		
		when(couponService.add(coupon)).thenReturn(addedCoupon);

		model.add("ABC", null); //$NON-NLS-1$
		model.setCouponConfig(couponConfig);
		controller.updateDatabase(model);

		verify(coupon).setCouponConfig(couponConfig);
		verify(couponService).add(coupon);
	}
}
