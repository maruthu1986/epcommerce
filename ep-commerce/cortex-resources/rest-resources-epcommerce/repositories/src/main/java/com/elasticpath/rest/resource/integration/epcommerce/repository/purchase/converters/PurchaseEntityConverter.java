/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.converters;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.springframework.core.convert.converter.Converter;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.PurchaseStatus;
import com.elasticpath.rest.resource.integration.epcommerce.transform.DateTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;

/**
 * Converter for Order to PurchaseEntity.
 */
@Singleton
@Named
public class PurchaseEntityConverter implements Converter<Order, PurchaseEntity> {

	private static final Map<OrderStatus, PurchaseStatus> STATUS_MAP = createStatusMap();

	private final MoneyTransformer moneyTransformer;

	private final DateTransformer dateTransformer;

	private final ResourceOperationContext resourceOperationContext;

	/**
	 * Constructor.
	 *
	 * @param moneyTransformer         moneyTransformer
	 * @param dateTransformer          dateTransformer
	 * @param resourceOperationContext resourceOperationContext
	 */
	@Inject
	public PurchaseEntityConverter(
			@Named("moneyTransformer") final MoneyTransformer moneyTransformer,
			@Named("dateTransformer") final DateTransformer dateTransformer,
			@Named("resourceOperationContext") final ResourceOperationContext resourceOperationContext) {
		this.moneyTransformer = moneyTransformer;
		this.dateTransformer = dateTransformer;
		this.resourceOperationContext = resourceOperationContext;
	}

	private static Map<OrderStatus, PurchaseStatus> createStatusMap() {
		Map<OrderStatus, PurchaseStatus> statusMap = new HashMap<>();
		statusMap.put(OrderStatus.AWAITING_EXCHANGE, PurchaseStatus.IN_PROGRESS);
		statusMap.put(OrderStatus.FAILED, PurchaseStatus.IN_PROGRESS);
		statusMap.put(OrderStatus.IN_PROGRESS, PurchaseStatus.IN_PROGRESS);
		statusMap.put(OrderStatus.ONHOLD, PurchaseStatus.IN_PROGRESS);
		statusMap.put(OrderStatus.PARTIALLY_SHIPPED, PurchaseStatus.IN_PROGRESS);
		statusMap.put(OrderStatus.COMPLETED, PurchaseStatus.COMPLETED);
		statusMap.put(OrderStatus.CANCELLED, PurchaseStatus.CANCELED);
		return Collections.unmodifiableMap(statusMap);
	}

	@Override
	public PurchaseEntity convert(final Order order) {
		Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());
		return convert(order, locale);
	}

	/**
	 * Convert an order to a purchase entity.
	 *
	 * @param order  order
	 * @param locale locale
	 * @return the purchase entity
	 */
	PurchaseEntity convert(final Order order, final Locale locale) {
		return PurchaseEntity.builder()
				.withPurchaseNumber(order.getOrderNumber())
				.withOrderId(order.getCartOrderGuid())
				.withPurchaseId(order.getGuid())
				.withStatus(STATUS_MAP.get(order.getStatus()).name())
				.withPurchaseDate(dateTransformer.transformToEntity(order.getCreatedDate(), locale))
				.withMonetaryTotal(Collections.singleton(moneyTransformer.transformToEntity(order.getTotalMoney(), locale)))
				.withTaxTotal(moneyTransformer.transformToEntity(order.getTotalTaxMoney(), locale))
				.build();
	}
}
