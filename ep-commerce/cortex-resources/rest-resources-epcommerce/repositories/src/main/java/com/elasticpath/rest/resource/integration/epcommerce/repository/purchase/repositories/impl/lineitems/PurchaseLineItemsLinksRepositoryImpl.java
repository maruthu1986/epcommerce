/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.repositories.impl.lineitems;

import io.reactivex.Observable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemsIdentifier;
import com.elasticpath.rest.id.type.PathIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;

/**
 * Repository for Purchase Line Items link.
 *
 * @param <I>  the identifier type
 * @param <LI> the linked identifier type
 */
@Component
public class PurchaseLineItemsLinksRepositoryImpl<I extends PurchaseLineItemsIdentifier, LI extends PurchaseLineItemIdentifier>
		implements LinksRepository<PurchaseLineItemsIdentifier, PurchaseLineItemIdentifier> {

	private OrderRepository orderRepository;

	@Override
	public Observable<PurchaseLineItemIdentifier> getElements(final PurchaseLineItemsIdentifier identifier) {
		PurchaseIdentifier purchaseIdentifier = identifier.getPurchase();
		String scope = purchaseIdentifier.getPurchases().getScope().getValue();
		String purchaseId = purchaseIdentifier.getPurchaseId().getValue();

		return orderRepository.findByGuidAsSingle(scope, purchaseId)
				.flatMapObservable(order -> Observable.fromIterable(order.getRootShoppingItems()))
				.map(linesItem -> buildPurchaseLineItemIdentifier(purchaseIdentifier, linesItem));
	}

	/**
	 * Build the PurchaseLineItemIdentifier.
	 *
	 * @param purchaseIdentifier purchaseIdentifier
	 * @param lineItem           lineItem
	 * @return line item identifier
	 */
	protected PurchaseLineItemIdentifier buildPurchaseLineItemIdentifier(final PurchaseIdentifier purchaseIdentifier, final ShoppingItem lineItem) {
		return PurchaseLineItemIdentifier.builder()
				.withLineItemId(PathIdentifier.of(lineItem.getGuid()))
				.withPurchaseLineItems(PurchaseLineItemsIdentifier.builder()
						.withPurchase(purchaseIdentifier)
						.build())
				.build();
	}

	@Reference
	public void setOrderRepository(final OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

}
