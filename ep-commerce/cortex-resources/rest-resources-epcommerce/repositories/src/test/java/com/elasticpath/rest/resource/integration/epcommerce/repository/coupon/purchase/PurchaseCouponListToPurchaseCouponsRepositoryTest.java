package com.elasticpath.rest.resource.integration.epcommerce.repository.coupon.purchase;

import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.COUPON_CODE;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.PURCHASE_ID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.coupon.CouponTestFactory.buildPurchaseCouponListIdentifier;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.order.repositories.BillingAddressInfoToAddressRepositoryImplTest.SCOPE;

import io.reactivex.Observable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.rules.AppliedCoupon;
import com.elasticpath.rest.definition.coupons.PurchaseCouponListIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.coupon.CouponRepository;

/**
 * Test for {@link PurchaseCouponListToPurchaseCouponsRepository}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PurchaseCouponListToPurchaseCouponsRepositoryTest {

	@InjectMocks
	private PurchaseCouponListToPurchaseCouponsRepository<PurchaseCouponListIdentifier, com.elasticpath.rest.definition.coupons
			.PurchaseCouponIdentifier> repository;

	@Mock
	private CouponRepository couponRepository;

	@Mock
	private AppliedCoupon appliedCoupon;

	@Before
	public void setup() {
		when(appliedCoupon.getCouponCode()).thenReturn(COUPON_CODE);
	}

	@Test
	public void getElementsWithValidPurchaseIdReturnsPurchaseCouponIdentifier() {
		when(couponRepository.getAppliedCoupons(SCOPE, PURCHASE_ID)).thenReturn(Observable.just(appliedCoupon));
		PurchaseCouponListIdentifier purchaseCouponListIdentifier = buildPurchaseCouponListIdentifier(SCOPE, PURCHASE_ID);
		repository.getElements(purchaseCouponListIdentifier)
				.test()
				.assertValue(purchaseCouponIdentifier -> purchaseCouponIdentifier.getCouponId().getValue().equals(COUPON_CODE));
	}

	@Test
	public void getElementsWithInvalidPurchaseIdReturns() {
		when(couponRepository.getAppliedCoupons(SCOPE, PURCHASE_ID)).thenReturn(Observable.empty());
		PurchaseCouponListIdentifier purchaseCouponListIdentifier = buildPurchaseCouponListIdentifier(SCOPE, PURCHASE_ID);
		repository.getElements(purchaseCouponListIdentifier)
				.test()
				.assertNoValues()
				.assertComplete();
	}
}