/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.coupon.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.COUPON_CODE;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.PURCHASE_ID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SCOPE;

import java.util.Collections;

import io.reactivex.Completable;
import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.coupon.specifications.PotentialCouponUse;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.rules.AppliedCoupon;
import com.elasticpath.domain.rules.AppliedRule;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ExceptionTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl.ReactiveAdapterImpl;
import com.elasticpath.service.rules.CouponService;
import com.elasticpath.service.rules.impl.CouponNotValidException;
import com.elasticpath.service.rules.impl.RuleValidationResultEnum;

/**
 * The tests for {@link CouponRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CouponRepositoryImplTest {

	private static final String CUSTOMER_EMAIL = "CUSTOMER_EMAIL";

	@Mock
	private CouponService couponService;

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private Order order;

	@Mock
	private AppliedRule appliedRule;

	@InjectMocks
	private NoErrorHandlingReactiveAdapterImpl reactiveAdapter;

	@InjectMocks
	private CouponRepositoryImpl couponRepository;

	@Before
	public void setUp() {
		couponRepository = new CouponRepositoryImpl(couponService, orderRepository, reactiveAdapter);
		when(orderRepository.findByGuidAsSingle(SCOPE, PURCHASE_ID)).thenReturn(Single.just(order));
	}

	@Test
	public void testCouponIsValidInStore() {
		setUpCouponServiceToReturnCoupon();
		setUpSpecificationToBeSatisfied(RuleValidationResultEnum.SUCCESS);
		couponRepository.validateCoupon(COUPON_CODE, SCOPE, CUSTOMER_EMAIL).test().assertNoErrors();
	}

	@Test
	public void testCouponRuleIsInvalidInStore() {
		setUpCouponServiceToReturnCoupon();
		setUpSpecificationToBeSatisfied(RuleValidationResultEnum.ERROR_UNSPECIFIED);
		couponRepository.validateCoupon(COUPON_CODE, SCOPE, CUSTOMER_EMAIL).test()
				.assertError(CouponNotValidException.class)
				.assertErrorMessage("Coupon 'coupon_code' is not valid");
	}

	private Coupon setUpCouponServiceToReturnCoupon() {
		Coupon coupon = mock(Coupon.class);
		when(couponService.findByCouponCode(COUPON_CODE)).thenReturn(coupon);
		return coupon;
	}


	private void setUpSpecificationToBeSatisfied(final RuleValidationResultEnum isSatisfied) {
		if (isSatisfied.isSuccess()) {
			doNothing().when(couponService).validateCoupon(any(PotentialCouponUse.class), anyString());
		} else {
			doThrow(new CouponNotValidException("coupon_code")).when(couponService).validateCoupon(any(PotentialCouponUse.class), anyString());
		}
	}

	@Test
	public void testFindCouponCodeReturnsCoupon() {
		Coupon coupon = setUpCouponServiceToReturnCoupon();

		couponRepository.findByCouponCode(COUPON_CODE)
				.test()
				.assertValue(coupon);
	}

	@Test
	public void testFindCouponCodeReturnsNotFound() {
		when(couponService.findByCouponCode(COUPON_CODE)).thenReturn(null);

		couponRepository.findByCouponCode(COUPON_CODE)
				.test()
				.assertError(throwable -> throwable.equals(
						ResourceOperationFailure.notFound(CouponRepositoryImpl.COUPON_CODE_NOT_FOUND)
				));
	}

	@Test
	public void getAppliedCouponReturnsNoValuesWhenOrderHasNoAppliedCoupons() {
		couponRepository.getAppliedCoupons(SCOPE, PURCHASE_ID)
				.test()
				.assertNoErrors()
				.assertNoValues()
				.assertComplete();
	}

	@Test
	public void getAppliedCouponReturnsNoValuesWhenAppliedRuleOnOrderHasNoLinkedCoupon() {
		when(order.getAppliedRules()).thenReturn(Collections.singleton(appliedRule));

		couponRepository.getAppliedCoupons(SCOPE, PURCHASE_ID)
				.test()
				.assertNoErrors()
				.assertNoValues()
				.assertComplete();
	}

	@Test
	public void getAppliedCouponReturns() {
		AppliedCoupon appliedCoupon = mock(AppliedCoupon.class);

		when(order.getAppliedRules()).thenReturn(Collections.singleton(appliedRule));
		when(appliedRule.getAppliedCoupons()).thenReturn(Collections.singleton(appliedCoupon));
		when(appliedCoupon.getCouponCode()).thenReturn(COUPON_CODE);
		when(appliedCoupon.getUsageCount()).thenReturn(1);

		couponRepository.getAppliedCoupons(SCOPE, PURCHASE_ID)
				.test()
				.assertNoErrors()
				.assertValue(coupon -> coupon.getCouponCode().equals(appliedCoupon.getCouponCode()))
				.assertValue(coupon -> coupon.getUsageCount() == appliedCoupon.getUsageCount());
	}

	private static final class NoErrorHandlingReactiveAdapterImpl extends ReactiveAdapterImpl {

		/**
		 * Constructor.
		 *
		 * @param exceptionTransformer the exception transformer
		 */
		private NoErrorHandlingReactiveAdapterImpl(final ExceptionTransformer exceptionTransformer) {
			super(exceptionTransformer);
		}

		/**
		 * Create a completable without error handling fixture (i.e. no calling .onErrorResumeNext as opposed to the parent).
		 * Thus tests are able to easily assert for and analyze initial exceptions and not the Throwable objects that get
		 * returned as a result of parent's .onErrorResumeNext(..) call.
		 *
		 * @param serviceCall serviceCall
		 * @return completable
		 */
		@Override
		public Completable fromServiceAsCompletable(final Runnable serviceCall) {
			return Completable.fromRunnable(serviceCall);
		}

	}
}
