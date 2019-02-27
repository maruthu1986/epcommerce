/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.repositories.impl;

import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.springframework.core.convert.ConversionService;

import com.elasticpath.domain.customer.Address;
import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.purchases.PurchaseBillingaddressIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;

/**
 * Purchase Billing Address Entity Repository.
 *
 * @param <E> the entity type
 * @param <I> the identifier type
 */
@Component
public class PurchaseBillingAddressEntityRepositoryImpl<E extends AddressEntity, I extends PurchaseBillingaddressIdentifier>
		implements Repository<AddressEntity, PurchaseBillingaddressIdentifier> {

	private static final String BILLING_ADDRESS_NOT_FOUND = "Billing address not found";

	private OrderRepository orderRepository;

	private ReactiveAdapter reactiveAdapter;

	private ConversionService conversionService;


	@Override
	public Single<AddressEntity> findOne(final PurchaseBillingaddressIdentifier identifier) {
		String purchaseId = identifier.getPurchase().getPurchaseId().getValue();
		String scope = identifier.getPurchase().getPurchases().getScope().getValue();

		return orderRepository.findByGuidAsSingle(scope, purchaseId)
				.flatMap(order -> reactiveAdapter.fromNullableAsSingle(order::getBillingAddress, BILLING_ADDRESS_NOT_FOUND))
				.map(this::convertCustomerAddressToAddressEntity);
	}

	/**
	 * Converts Address to AddressEntity.
	 *
	 * @param address address
	 * @return address entity
	 */
	protected AddressEntity convertCustomerAddressToAddressEntity(final Address address) {
		return conversionService.convert(address, AddressEntity.class);
	}

	@Reference
	public void setOrderRepository(final OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	@Reference
	public void setReactiveAdapter(final ReactiveAdapter reactiveAdapter) {
		this.reactiveAdapter = reactiveAdapter;
	}

	@Reference
	public void setConversionService(final ConversionService conversionService) {
		this.conversionService = conversionService;
	}
}
