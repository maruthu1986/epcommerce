/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.repositories.impl.components;

import java.util.List;

import io.reactivex.Observable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemComponentsIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;

/**
 * Repository that returns components given line item.
 *
 * @param <LI> line item identifier
 * @param <LCI> line item components identifier
 */
@Component
public class LineItemToComponentsRepositoryImpl<LI extends PurchaseLineItemIdentifier, LCI extends PurchaseLineItemComponentsIdentifier>
		implements LinksRepository<PurchaseLineItemIdentifier, PurchaseLineItemComponentsIdentifier> {

	private OrderRepository orderRepository;

	@Override
	public Observable<PurchaseLineItemComponentsIdentifier> getElements(final PurchaseLineItemIdentifier identifier) {
		List<String> guidPathFromRootItem = identifier.getLineItemId().getValue();
		PurchaseIdentifier purchaseIdentifier = identifier.getPurchaseLineItems().getPurchase();
		String scope = purchaseIdentifier.getPurchases().getScope().getValue();
		String purchaseId = purchaseIdentifier.getPurchaseId().getValue();
		return orderRepository.findProductSku(scope, purchaseId, guidPathFromRootItem)
				.map(ProductSku::getProduct)
				.map(product -> product instanceof ProductBundle)
				.flatMapObservable(isBundle -> isBundle ? buildPurchaseLineItemComponentsIdentifier(identifier) : Observable.empty())
				.onErrorResumeNext(Observable.empty());
	}

	private Observable<PurchaseLineItemComponentsIdentifier> buildPurchaseLineItemComponentsIdentifier(final PurchaseLineItemIdentifier identifier) {
		return Observable.just(PurchaseLineItemComponentsIdentifier.builder()
				.withPurchaseLineItem(identifier)
				.build());
	}

	@Reference
	public void setOrderRepository(final OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}
}
