/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.repositories.impl.options;

import java.util.List;

import com.google.common.annotations.VisibleForTesting;
import io.reactivex.Observable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionsIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;

/**
 * This repository returns a lit of options given `options` identifier.
 *
 * @param <OSI> options identifier
 * @param <OI> option identifier
 */
@Component
public class OptionsEntityRepositoryImpl<OSI extends PurchaseLineItemOptionsIdentifier, OI extends PurchaseLineItemOptionIdentifier>
		implements LinksRepository<PurchaseLineItemOptionsIdentifier, PurchaseLineItemOptionIdentifier> {

	/**
	 * Error for not found options.
	 */
	@VisibleForTesting
	static final String OPTIONS_NOT_FOUND = "No options found for line item.";
	private OrderRepository orderRepository;

	@Override
	public Observable<PurchaseLineItemOptionIdentifier> getElements(final PurchaseLineItemOptionsIdentifier identifier) {
		PurchaseLineItemIdentifier purchaseLineItemIdentifier = identifier.getPurchaseLineItem();
		List<String> guidPathFromRootItem = purchaseLineItemIdentifier.getLineItemId().getValue();
		PurchaseIdentifier purchaseIdentifier = purchaseLineItemIdentifier.getPurchaseLineItems().getPurchase();
		String scope = purchaseIdentifier.getPurchases().getScope().getValue();
		String purchaseId = purchaseIdentifier.getPurchaseId().getValue();
		return orderRepository.findProductSku(scope, purchaseId, guidPathFromRootItem)
				.flatMapObservable(productSku -> Observable.fromIterable(productSku.getOptionValueCodes()))
				.map(optionId -> buildPurchaseLineItemOptionIdentifier(identifier, optionId))
				.switchIfEmpty(Observable.error(ResourceOperationFailure.notFound(OPTIONS_NOT_FOUND)));
	}

	/**
	 * Build identifier.
	 *
	 * @param identifier options identifier
	 * @param optionId option id
	 * @return option identifier
	 */
	protected PurchaseLineItemOptionIdentifier buildPurchaseLineItemOptionIdentifier(
			final PurchaseLineItemOptionsIdentifier identifier, final String optionId) {

		return PurchaseLineItemOptionIdentifier.builder()
				.withOptionId(StringIdentifier.of(optionId))
				.withPurchaseLineItemOptions(identifier)
				.build();
	}

	@Reference
	public void setOrderRepository(final OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}
}
