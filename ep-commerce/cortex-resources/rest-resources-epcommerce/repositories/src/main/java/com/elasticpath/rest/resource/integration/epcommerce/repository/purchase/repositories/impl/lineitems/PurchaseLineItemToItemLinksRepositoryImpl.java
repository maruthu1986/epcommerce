/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.repositories.impl.lineitems;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Single;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.base.ScopeIdentifierPart;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.items.ItemsIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemIdentifier;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;

/**
 * Links repository handles creating {@link ItemIdentifier} from current {@link PurchaseLineItemIdentifier}.
 *
 * @param <E>  type of entity.
 * @param <LI> type of link identifier.
 */
@Component
public class PurchaseLineItemToItemLinksRepositoryImpl<E extends PurchaseLineItemIdentifier, LI extends ItemIdentifier>
		implements LinksRepository<PurchaseLineItemIdentifier, ItemIdentifier> {

	private OrderRepository orderRepository;
	private ItemRepository itemRepository;
	private ProductSkuRepository productSkuRepository;

	@Override
	public Observable<ItemIdentifier> getElements(final PurchaseLineItemIdentifier identifier) {
		PurchaseIdentifier purchaseIdentifier = identifier.getPurchaseLineItems().getPurchase();
		String scope = purchaseIdentifier.getPurchases().getScope().getValue();
		String purchaseId = purchaseIdentifier.getPurchaseId().getValue();
		List<String> guidPathFromRootItem = identifier.getLineItemId().getValue();
		return orderRepository.findOrderSku(scope, purchaseId, guidPathFromRootItem)
				.flatMap(this::buildItemId)
				.flatMapObservable(itemId -> buildItemIdentifier(itemId, scope));
	}

	private Observable<ItemIdentifier> buildItemIdentifier(final IdentifierPart<Map<String, String>> itemId, final String scope) {
		return Observable.just(ItemIdentifier.builder()
				.withItemId(itemId)
				.withItems(ItemsIdentifier.builder()
						.withScope(ScopeIdentifierPart.of(scope))
						.build())
				.build());
	}

	/**
	 * Builds item id from ordersku.
	 *
	 * @param orderSku the ordersku
	 * @return Single of item id.
	 */
	protected Single<IdentifierPart<Map<String, String>>> buildItemId(final OrderSku orderSku) {

		final ProductSku sku = productSkuRepository.getProductSkuWithAttributesByGuidAsSingle(orderSku.getSkuGuid()).blockingGet();

		return Single.just(itemRepository.getItemIdForProductSku(sku));

	}

	@Reference
	public void setOrderRepository(final OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	@Reference
	public void setItemRepository(final ItemRepository itemRepository) {
		this.itemRepository = itemRepository;
	}

	@Reference
	public void setProductSkuRepository(final ProductSkuRepository productSkuRepository) {
		this.productSkuRepository = productSkuRepository;
	}
}
