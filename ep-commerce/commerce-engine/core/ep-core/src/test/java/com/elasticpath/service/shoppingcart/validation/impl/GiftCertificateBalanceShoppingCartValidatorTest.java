/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.base.common.dto.StructuredErrorMessageType;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.service.catalog.GiftCertificateService;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.service.shoppingcart.validation.ShoppingCartValidationContext;

@RunWith(MockitoJUnitRunner.class)
public class GiftCertificateBalanceShoppingCartValidatorTest {

	private static final String GIFT_CODE1 = "GIFT_CODE_1";

	private static final String GIFT_CODE2 = "GIFT_CODE_2";

	private static final BigDecimal TWENTY = BigDecimal.valueOf(20.0);

	private static final BigDecimal FIVE = BigDecimal.valueOf(5.0);

	private static final BigDecimal FIFTEEN = BigDecimal.valueOf(15.0);

	@InjectMocks
	private GiftCertificateBalanceShoppingCartValidatorImpl validator;

	@Mock
	private ShoppingCart shoppingCart;

	@Mock
	private ShoppingCartPricingSnapshot shoppingCartPricingSnapshot;

	@Mock
	private PricingSnapshotService pricingSnapshotService;

	@Mock
	private GiftCertificateService giftCertificateService;

	@Mock
	private ShoppingCartValidationContext context;

	@Mock
	private GiftCertificate giftCertificate1;

	@Mock
	private GiftCertificate giftCertificate2;

	@Before
	public void setUp() throws Exception {
		Set<GiftCertificate> giftCertificates = new HashSet<>();

		giftCertificates.add(giftCertificate1);
		giftCertificates.add(giftCertificate2);

		given(giftCertificate1.getGiftCertificateCode()).willReturn(GIFT_CODE1);
		given(giftCertificate2.getGiftCertificateCode()).willReturn(GIFT_CODE2);

		given(shoppingCart.getAppliedGiftCertificates()).willReturn(giftCertificates);

		given(pricingSnapshotService.getPricingSnapshotForCart(any(ShoppingCart.class))).willReturn(shoppingCartPricingSnapshot);

		given(context.getShoppingCart()).willReturn(shoppingCart);

		given(giftCertificateService.findByGiftCertificateCode(GIFT_CODE1)).willReturn(giftCertificate1);
		given(giftCertificateService.findByGiftCertificateCode(GIFT_CODE2)).willReturn(giftCertificate2);

	}

	@Test
	public void hasEnoughFunds() {
		// Given
		given(giftCertificateService.findByGiftCertificateCode(GIFT_CODE1)).willReturn(giftCertificate1);
		given(giftCertificateService.findByGiftCertificateCode(GIFT_CODE2)).willReturn(giftCertificate2);
		given(shoppingCartPricingSnapshot.getGiftCertificateDiscount()).willReturn(BigDecimal.TEN);
		given(giftCertificateService.getBalance(any(GiftCertificate.class))).willReturn(BigDecimal.TEN);

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).isEmpty();

	}

	@Test
	public void someGiftCardsUnavailable() {
		StructuredErrorMessage errorMessage = new StructuredErrorMessage(StructuredErrorMessageType.ERROR, "cart.gift.certificate.not.found",
				String.format("Gift certificate '%s' not found.", GIFT_CODE1), ImmutableMap.of("gift-certificate-code", GIFT_CODE1));

		// Given
		given(giftCertificateService.findByGiftCertificateCode(GIFT_CODE1)).willReturn(null);
		given(giftCertificateService.findByGiftCertificateCode(GIFT_CODE2)).willReturn(giftCertificate2);
		given(shoppingCartPricingSnapshot.getGiftCertificateDiscount()).willReturn(BigDecimal.TEN);
		given(giftCertificateService.getBalance(any(GiftCertificate.class))).willReturn(BigDecimal.TEN);

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).isEqualTo(Collections.singletonList(errorMessage));

	}

	@Test
	public void doesNotHaveEnough() {

		Collection<StructuredErrorMessage> errorMessages = ImmutableList.of(
				new StructuredErrorMessage(StructuredErrorMessageType.ERROR,
						"cart.gift.certificate.insufficient.balance",
						"Gift certificates does not have sufficient balance to process the payment.",
						ImmutableMap.of("gc-payment-required", TWENTY.toString(),
								"gc-balance", FIFTEEN.toString())));

		// Given
		given(shoppingCartPricingSnapshot.getGiftCertificateDiscount()).willReturn(TWENTY);
		given(giftCertificateService.getBalance(giftCertificate1)).willReturn(BigDecimal.TEN);
		given(giftCertificateService.getBalance(giftCertificate2)).willReturn(FIVE);

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).containsOnlyElementsOf(errorMessages);
	}

}