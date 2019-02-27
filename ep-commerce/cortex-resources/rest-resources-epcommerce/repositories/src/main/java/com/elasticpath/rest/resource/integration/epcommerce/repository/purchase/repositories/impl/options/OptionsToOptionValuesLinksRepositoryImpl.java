/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.repositories.impl.options;

import java.util.List;

import com.google.common.annotations.VisibleForTesting;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionValueIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;

/**
 * Links repository which returns option values given option identifier.
 *
 * @param <OI> option identifier
 * @param <VI> option value identifier
 */
@Component
public class OptionsToOptionValuesLinksRepositoryImpl<OI extends PurchaseLineItemOptionIdentifier, VI extends PurchaseLineItemOptionValueIdentifier>
		implements LinksRepository<PurchaseLineItemOptionIdentifier, PurchaseLineItemOptionValueIdentifier> {

	/**
	 * Value for option not found error.
	 */
	@VisibleForTesting
	static final String VALUE_FOR_OPTION_NOT_FOUND = "Option value for option was not found.";
	private static final String PRODUCT_SKU_NOT_FOUND = "Product SKU not found.";
	private ReactiveAdapter reactiveAdapter;
	private OrderRepository orderRepository;

	@Override
	public Observable<PurchaseLineItemOptionValueIdentifier> getElements(final PurchaseLineItemOptionIdentifier identifier) {
		PurchaseLineItemIdentifier purchaseLineItemIdentifier = identifier.getPurchaseLineItemOptions().getPurchaseLineItem();
		List<String> guidPathFromRootItem = purchaseLineItemIdentifier.getLineItemId().getValue();
		String optionId = identifier.getOptionId().getValue();
		PurchaseIdentifier purchaseIdentifier = purchaseLineItemIdentifier.getPurchaseLineItems().getPurchase();
		String scope = purchaseIdentifier.getPurchases().getScope().getValue();
		String purchaseId = purchaseIdentifier.getPurchaseId().getValue();
		return orderRepository.findProductSku(scope, purchaseId, guidPathFromRootItem)
				.onErrorResumeNext(Single.error(ResourceOperationFailure.notFound(PRODUCT_SKU_NOT_FOUND)))
				.map(ProductSku::getOptionValueMap)
				.flatMap(stringSkuOptionValueMap ->
						reactiveAdapter.fromNullableAsSingle(() -> stringSkuOptionValueMap.get(optionId), VALUE_FOR_OPTION_NOT_FOUND))
				.map(SkuOptionValue::getOptionValueKey)
				.map(valueKey -> buildOptionValueIdentifier(identifier, valueKey))
				.toObservable();
	}

	/**
	 * Builds option value identifier.
	 *
	 * @param identifier option identifier
	 * @param valueKey key used to retrieve the value of the line item option
	 * @return option value identifier
	 */
	protected PurchaseLineItemOptionValueIdentifier buildOptionValueIdentifier(
			final PurchaseLineItemOptionIdentifier identifier, final String valueKey) {

		return PurchaseLineItemOptionValueIdentifier.builder()
				.withOptionValueId(StringIdentifier.of(valueKey))
				.withPurchaseLineItemOption(identifier)
				.build();
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
