/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.repositories.impl.components;

import java.util.List;

import io.reactivex.Observable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemComponentsIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemIdentifier;
import com.elasticpath.rest.id.type.PathIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;

/**
 * This repository returns a list of components given components identifier.
 * 
 * @param <CS> Components identifier
 * @param <C> Component identifier
 */
@Component
public class ComponentsEntityRepositoryImpl<CS extends PurchaseLineItemComponentsIdentifier, C extends PurchaseLineItemIdentifier>
		implements LinksRepository<PurchaseLineItemComponentsIdentifier, PurchaseLineItemIdentifier> {

	private OrderRepository orderRepository;

	@Override
	public Observable<PurchaseLineItemIdentifier> getElements(final PurchaseLineItemComponentsIdentifier identifier) {
		PurchaseLineItemIdentifier purchaseLineItemIdentifier = identifier.getPurchaseLineItem();
		List<String> guidPathFromRootItem = purchaseLineItemIdentifier.getLineItemId().getValue();
		PurchaseIdentifier purchaseIdentifier = purchaseLineItemIdentifier.getPurchaseLineItems().getPurchase();
		String scope = purchaseIdentifier.getPurchases().getScope().getValue();
		String purchaseId = purchaseIdentifier.getPurchaseId().getValue();
		return orderRepository.findOrderSku(scope, purchaseId, guidPathFromRootItem)
				.flatMapObservable(orderSku -> Observable.fromIterable(orderSku.getChildren()))
				.map(componentOrderSku -> createComponentLineItemIdentifier(identifier, componentOrderSku.getGuid()));
	}

	private PurchaseLineItemIdentifier createComponentLineItemIdentifier(
			final PurchaseLineItemComponentsIdentifier identifier, final String componentId) {

		return PurchaseLineItemIdentifier.builder()
				.withPurchaseLineItems(identifier.getPurchaseLineItem().getPurchaseLineItems())
				.withLineItemId(PathIdentifier.of(identifier.getPurchaseLineItem().getLineItemId(), componentId))
				.build();
	}

	@Reference
	public void setOrderRepository(final OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

}
