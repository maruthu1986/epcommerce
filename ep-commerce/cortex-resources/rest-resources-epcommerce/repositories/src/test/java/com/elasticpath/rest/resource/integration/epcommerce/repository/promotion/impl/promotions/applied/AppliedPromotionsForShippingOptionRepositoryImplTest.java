/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.promotions.applied;

import static java.util.Collections.singletonList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ErrorCheckPredicate.createErrorCheckPredicate;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionTestFactory
		.buildAppliedPromotionsForShippingOptionIdentifier;

import java.util.Collections;

import com.google.common.collect.ImmutableList;
import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForShippingOptionIdentifier;
import com.elasticpath.rest.definition.promotions.PromotionIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipping.ShippingOptionRepository;
import com.elasticpath.service.shipping.ShippingOptionService;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * Test for {@link AppliedPromotionsForShippingOptionRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class AppliedPromotionsForShippingOptionRepositoryImplTest {

	private final AppliedPromotionsForShippingOptionIdentifier identifier = buildAppliedPromotionsForShippingOptionIdentifier();

	@Mock
	private ShippingOption shippingOption;

	@Mock
	private ShoppingCart shoppingCart;

	@Mock
	private ShoppingCartPricingSnapshot shoppingCartPricingSnapshot;

	@InjectMocks
	private AppliedPromotionsForShippingOptionRepositoryImpl<AppliedPromotionsForShippingOptionIdentifier, PromotionIdentifier> repository;

	@Mock
	private CartOrderRepository cartOrderRepository;

	@Mock
	private ShippingOptionRepository shippingOptionRepository;

	@Mock
	private PricingSnapshotRepository pricingSnapshotRepository;

	@Mock
	private PromotionRepository promotionRepository;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private ShippingOptionService shippingOptionService;

	@Before
	public void setUp() {

		when(shippingOptionService.getShippingOptions(shoppingCart).getAvailableShippingOptions()).thenReturn(singletonList(shippingOption));

	}

	@Test
	public void verifyGetElementsReturnNotFoundWhenShoppingCartNotFound() {
		when(cartOrderRepository.getEnrichedShoppingCartForShipments(ResourceTestConstants.SCOPE, ResourceTestConstants.SHIPMENT_DETAILS_ID))
				.thenReturn(Single.error(ResourceOperationFailure.notFound(ResourceTestConstants.NOT_FOUND)));

		repository.getElements(identifier)
				.test()
				.assertError(createErrorCheckPredicate(ResourceTestConstants.NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyGetElementsReturnNotFoundWhenPricingSnapshotIsNotFound() {
		when(cartOrderRepository.getEnrichedShoppingCartForShipments(ResourceTestConstants.SCOPE, ResourceTestConstants.SHIPMENT_DETAILS_ID))
				.thenReturn(Single.just(shoppingCart));
		when(pricingSnapshotRepository.getShoppingCartPricingSnapshotSingle(shoppingCart))
				.thenReturn(Single.error(ResourceOperationFailure.notFound(ResourceTestConstants.NOT_FOUND)));

		repository.getElements(identifier)
				.test()
				.assertError(createErrorCheckPredicate(ResourceTestConstants.NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyGetElementsReturnEmptyWhenShippingOptionNotFound() {
		when(cartOrderRepository.getEnrichedShoppingCartForShipments(ResourceTestConstants.SCOPE, ResourceTestConstants.SHIPMENT_DETAILS_ID))
				.thenReturn(Single.just(shoppingCart));
		when(pricingSnapshotRepository.getShoppingCartPricingSnapshotSingle(shoppingCart)).thenReturn(Single.just(shoppingCartPricingSnapshot));
		when(shippingOptionRepository.getShippingOption(any(), any()))
				.thenReturn(Single.error(ResourceOperationFailure.notFound(ResourceTestConstants.NOT_FOUND)));

		repository.getElements(identifier)
				.test()
				.assertError(createErrorCheckPredicate(ResourceTestConstants.NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyGetElementsReturnEmptyWhenPromotionsAreNotFound() {
		when(cartOrderRepository.getEnrichedShoppingCartForShipments(ResourceTestConstants.SCOPE, ResourceTestConstants.SHIPMENT_DETAILS_ID))
				.thenReturn(Single.just(shoppingCart));
		when(pricingSnapshotRepository.getShoppingCartPricingSnapshotSingle(shoppingCart)).thenReturn(Single.just(shoppingCartPricingSnapshot));
		when(shippingOptionRepository.getShippingOption(any(), any()))
				.thenReturn(Single.just(shippingOption));
		when(promotionRepository.getAppliedShippingPromotions(shoppingCartPricingSnapshot, shippingOption))
				.thenReturn(Collections.emptyList());

		repository.getElements(identifier)
				.test()
				.assertNoErrors()
				.assertNoValues();
	}

	@Test
	public void verifyGetElementsReturnPromotionIdentifiers() {
		when(cartOrderRepository.getEnrichedShoppingCartForShipments(ResourceTestConstants.SCOPE, ResourceTestConstants.SHIPMENT_DETAILS_ID))
				.thenReturn(Single.just(shoppingCart));
		when(pricingSnapshotRepository.getShoppingCartPricingSnapshotSingle(shoppingCart)).thenReturn(Single.just(shoppingCartPricingSnapshot));
		when(shippingOptionRepository.getShippingOption(any(), any()))
				.thenReturn(Single.just(shippingOption));
		when(promotionRepository.getAppliedShippingPromotions(shoppingCartPricingSnapshot, shippingOption))
				.thenReturn(ImmutableList.of("0", "1"));

		repository.getElements(identifier)
				.test()
				.assertNoErrors()
				.assertValueAt(0, promotionIdentifier -> promotionIdentifier.getPromotionId().getValue().equals("0"))
				.assertValueAt(1, promotionIdentifier -> promotionIdentifier.getPromotionId().getValue().equals("1"));
	}
}
