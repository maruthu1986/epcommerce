/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.repositories.impl.options;

import java.util.List;

import com.google.common.collect.ImmutableSortedMap;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionsIdentifier;
import com.elasticpath.rest.id.util.CompositeIdUtil;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;

/**
 * Repository that returns options for the given line item.
 *
 * @param <I> line item identifier
 * @param <O> line item options identifier
 */
@Component
public class LineItemToOptionsRepositoryImpl<I extends PurchaseLineItemIdentifier, O extends PurchaseLineItemOptionsIdentifier>
		implements LinksRepository<PurchaseLineItemIdentifier, PurchaseLineItemOptionsIdentifier> {

	private ItemRepository itemRepository;
	private ReactiveAdapter reactiveAdapter;
	private OrderRepository orderRepository;

	@Override
	public Observable<PurchaseLineItemOptionsIdentifier> getElements(final PurchaseLineItemIdentifier identifier) {
		PurchaseIdentifier purchaseIdentifier = identifier.getPurchaseLineItems().getPurchase();
		List<String> guidPathFromRootItem = identifier.getLineItemId().getValue();
		String scope = purchaseIdentifier.getPurchases().getScope().getValue();
		String purchaseId = purchaseIdentifier.getPurchaseId().getValue();
		return orderRepository.findProductSku(scope, purchaseId, guidPathFromRootItem)
				.map(ProductSku::getSkuCode)
				.map(this::encodeItemId)
				.flatMap(this::doesSkuContainOptions)
				.flatMapObservable(containsOptions -> containsOptions ? buildPurchaseLineItemOptionsIdentifier(identifier) : Observable.empty())
				.onErrorResumeNext(Observable.empty());
	}

	private Observable<PurchaseLineItemOptionsIdentifier> buildPurchaseLineItemOptionsIdentifier(final PurchaseLineItemIdentifier identifier) {
		return Observable.just(PurchaseLineItemOptionsIdentifier.builder()
				.withPurchaseLineItem(identifier)
				.build());
	}

	private String encodeItemId(final String skuCode) {
		return CompositeIdUtil.encodeCompositeId(
				ImmutableSortedMap.of(ItemRepository.SKU_CODE_KEY, skuCode)
		);
	}

	/**
	 * Checks if item has options.
	 *
	 * @param skuCode skuCode
	 * @return true any options are present for this item
	 */
	protected Single<Boolean> doesSkuContainOptions(final String skuCode) {
		return reactiveAdapter.fromRepository(() -> itemRepository.getSkuOptionsForItemId(skuCode))
				.isEmpty()
				.map(empty -> !empty);
	}

	@Reference
	public void setItemRepository(final ItemRepository itemRepository) {
		this.itemRepository = itemRepository;
	}

	@Reference
	public void setReactiveAdapter(final ReactiveAdapter reactiveAdapter) {
		this.reactiveAdapter = reactiveAdapter;
	}

	@Reference
	public void setOrderRepository(final OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}
}
