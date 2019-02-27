/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.price.impl;

import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ErrorCheckPredicate.createErrorCheckPredicate;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.IdentifierTestFactory.buildPriceForShipmentLineItemIdentifier;

import java.util.Currency;
import java.util.Locale;

import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.prices.PriceForShipmentLineItemIdentifier;
import com.elasticpath.rest.definition.prices.ShipmentLineItemPriceEntity;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;

/**
 * Test for {@link PriceForShipmentLineItemEntityRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PriceForShipmentLineItemEntityRepositoryImplTest {

	private final PriceForShipmentLineItemIdentifier priceForShipmentLineItemIdentifier =
			buildPriceForShipmentLineItemIdentifier(ResourceTestConstants.SCOPE, ResourceTestConstants.PURCHASE_ID,
					ResourceTestConstants.SHIPMENT_ID, ResourceTestConstants.SHIPMENT_LINE_ITEM_ID);

	@Mock
	private OrderSku orderSku;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private ShoppingItemPricingSnapshot shoppingItemPricingSnapshot;

	@InjectMocks
	private PriceForShipmentLineItemEntityRepositoryImpl<ShipmentLineItemPriceEntity, PriceForShipmentLineItemIdentifier> repository;

	@Mock
	private MoneyTransformer moneyTransformer;

	@Mock
	private ShipmentRepository shipmentRepository;

	@Mock
	private ResourceOperationContext resourceOperationContext;

	@Mock
	private PricingSnapshotRepository pricingSnapshotRepository;

	@Before
	public void setUp() {
		when(resourceOperationContext.getSubject()).thenReturn(TestSubjectFactory.createWithScopeAndUserIdAndLocale(ResourceTestConstants.SCOPE,
				ResourceTestConstants.USER_ID, Locale.ENGLISH));
	}

	@Test
	public void verifyFindOneReturnNotFoundWhenOrderSkuNotFound() {
		when(shipmentRepository.getOrderSkuWithParentId(
				ResourceTestConstants.SCOPE, ResourceTestConstants.PURCHASE_ID, ResourceTestConstants.SHIPMENT_ID,
				ResourceTestConstants.SHIPMENT_LINE_ITEM_ID, null))
				.thenReturn(Single.error(ResourceOperationFailure.notFound(ResourceTestConstants.NOT_FOUND)));

		repository.findOne(priceForShipmentLineItemIdentifier)
				.test()
				.assertError(createErrorCheckPredicate(ResourceTestConstants.NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyFindOneReturnNotFoundWheShoppingItemPricingSnapshotNotFound() {
		when(shipmentRepository.getOrderSkuWithParentId(
				ResourceTestConstants.SCOPE, ResourceTestConstants.PURCHASE_ID, ResourceTestConstants.SHIPMENT_ID,
				ResourceTestConstants.SHIPMENT_LINE_ITEM_ID, null)).thenReturn(Single.just(orderSku));
		when(pricingSnapshotRepository.getPricingSnapshotForOrderSku(orderSku))
				.thenReturn(Single.error(ResourceOperationFailure.notFound(ResourceTestConstants.NOT_FOUND)));

		repository.findOne(priceForShipmentLineItemIdentifier)
				.test()
				.assertError(createErrorCheckPredicate(ResourceTestConstants.NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyFindOneReturnsShipmentLineItemPriceEntity() {
		Money money = Money.valueOf(1, Currency.getInstance("USD"));
		CostEntity costEntity = CostEntity.builder().build();

		when(shipmentRepository.getOrderSkuWithParentId(ResourceTestConstants.SCOPE, ResourceTestConstants.PURCHASE_ID,
				ResourceTestConstants.SHIPMENT_ID, ResourceTestConstants.SHIPMENT_LINE_ITEM_ID, null))
				.thenReturn(Single.just(orderSku));
		when(pricingSnapshotRepository.getPricingSnapshotForOrderSku(orderSku)).thenReturn(Single.just(shoppingItemPricingSnapshot));
		when(shoppingItemPricingSnapshot.getPriceCalc().forUnitPrice().withCartDiscounts().getMoney()).thenReturn(money);
		when(moneyTransformer.transformToEntity(money, Locale.ENGLISH)).thenReturn(costEntity);

		repository.findOne(priceForShipmentLineItemIdentifier)
				.test()
				.assertNoErrors()
				.assertValue(shipmentLineItemPriceEntity -> shipmentLineItemPriceEntity.getPurchasePrice().contains(costEntity));
	}
}
