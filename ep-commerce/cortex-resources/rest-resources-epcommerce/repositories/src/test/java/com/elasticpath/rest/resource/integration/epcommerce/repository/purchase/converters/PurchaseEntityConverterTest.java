/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.converters;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.money.Money;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.base.DateEntity;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.PurchaseStatus;
import com.elasticpath.rest.resource.integration.epcommerce.transform.DateTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;


/**
 * The test of {@link PurchaseEntityConverter}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PurchaseEntityConverterTest {

	private static final String ORDER_GUID = "orderGuid";
	private static final String CART_ORDER_GUID = "cartOrderGuid";

	@Mock
	private MoneyTransformer mockMoneyTransformer;
	@Mock
	private DateTransformer mockDateTransformer;

	@InjectMocks
	private PurchaseEntityConverter purchaseEntityConverter;

	/**
	 * Tests {@link PurchaseEntityConverter#convert(Order)}.
	 */
	@Test
	public void testTransformToEntity() {
		Order domainOrder = mock(Order.class);

		Locale locale = Locale.CANADA;
		OrderStatus orderStatus = OrderStatus.COMPLETED;

		Date createdDate = new Date();

		CostEntity expectedCostEntity = ResourceTypeFactory.createResourceEntity(CostEntity.class);
		DateEntity expectedDateEntity = ResourceTypeFactory.createResourceEntity(DateEntity.class);

		when(domainOrder.getCartOrderGuid()).thenReturn(CART_ORDER_GUID);
		when(domainOrder.getGuid()).thenReturn(ORDER_GUID);
		when(domainOrder.getStatus()).thenReturn(orderStatus);

		Money mockMoney = Money.valueOf(BigDecimal.ONE, Currency.getInstance("CAD"));
		when(domainOrder.getTotalMoney()).thenReturn(mockMoney);
		when(domainOrder.getTotalTaxMoney()).thenReturn(mockMoney);

		when(domainOrder.getCreatedDate()).thenReturn(createdDate);
		when(mockMoneyTransformer.transformToEntity(mockMoney, locale)).thenReturn(expectedCostEntity);
		when(mockDateTransformer.transformToEntity(createdDate, locale)).thenReturn(expectedDateEntity);

		PurchaseEntity purchaseEntity = purchaseEntityConverter.convert(domainOrder, locale);

		assertEquals(CART_ORDER_GUID, purchaseEntity.getOrderId());
		assertEquals(ORDER_GUID, purchaseEntity.getPurchaseId());

		assertEquals(PurchaseStatus.COMPLETED.name(), purchaseEntity.getStatus());
		assertEquals(expectedDateEntity, purchaseEntity.getPurchaseDate());

		assertThat(purchaseEntity.getMonetaryTotal(), hasItem(expectedCostEntity));
		assertEquals(expectedCostEntity, purchaseEntity.getTaxTotal());
	}
}
