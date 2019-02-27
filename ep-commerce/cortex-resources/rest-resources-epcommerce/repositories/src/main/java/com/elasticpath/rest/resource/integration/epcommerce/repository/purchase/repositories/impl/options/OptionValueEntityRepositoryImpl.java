/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.repositories.impl.options;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.repository.Repository;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionValueEntity;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionValueIdentifier;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;

/**
 * Repository to retrieve option Value entities.
 * 
 * @param <E> option value entity
 * @param <I> option value identifier
 */
@Component
public class OptionValueEntityRepositoryImpl<E extends PurchaseLineItemOptionValueEntity, I extends PurchaseLineItemOptionValueIdentifier>
		implements Repository<PurchaseLineItemOptionValueEntity, PurchaseLineItemOptionValueIdentifier> {

	/**
	 * Error for option value being not found.
	 */
	@VisibleForTesting
	static final String VALUE_NOT_FOUND = "Option value not found.";
	private ResourceOperationContext resourceOperationContext;
	private OrderRepository orderRepository;

	@Override
	public Single<PurchaseLineItemOptionValueEntity> findOne(final PurchaseLineItemOptionValueIdentifier identifier) {
		PurchaseLineItemOptionIdentifier purchaseLineItemOptionIdentifier = identifier.getPurchaseLineItemOption();
		PurchaseLineItemIdentifier purchaseLineItemIdentifier = purchaseLineItemOptionIdentifier.getPurchaseLineItemOptions().getPurchaseLineItem();
		List<String> guidPathFromRootItem = purchaseLineItemIdentifier.getLineItemId().getValue();
		String optionId = purchaseLineItemOptionIdentifier.getOptionId().getValue();
		String valueId = identifier.getOptionValueId().getValue();
		PurchaseIdentifier purchaseIdentifier = purchaseLineItemIdentifier.getPurchaseLineItems().getPurchase();
		String scope = purchaseIdentifier.getPurchases().getScope().getValue();
		String purchaseId = purchaseIdentifier.getPurchaseId().getValue();
		return orderRepository.findProductSku(scope, purchaseId, guidPathFromRootItem)
				.map(productSku -> productSku.getProduct().getProductType())
				.map(ProductType::getSkuOptions)
				.flatMap(skuOptions -> findOptionValue(optionId, valueId, skuOptions));
	}

	/**
	 * Find option value and build entity if found.
	 *
	 * @param optionId option id
	 * @param valueId value id
	 * @param skuOptions sku options
	 * @return option value entity
	 */
	protected Single<PurchaseLineItemOptionValueEntity> findOptionValue(
			final String optionId, final String valueId, final Set<SkuOption> skuOptions) {

		for (SkuOption option : skuOptions) {
			if (option.getOptionKey().equals(optionId)) {
				SkuOptionValue skuOptionValue = option.getOptionValue(valueId);
				if (skuOptionValue != null) {
					return Single.just(buildOptionValueEntity(skuOptionValue));
				}
			}
		}

		return Single.error(ResourceOperationFailure.notFound(VALUE_NOT_FOUND));
	}
	
	private PurchaseLineItemOptionValueEntity buildOptionValueEntity(final SkuOptionValue skuOptionValue) {
		Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());
		
		return PurchaseLineItemOptionValueEntity.builder()
				.withName(skuOptionValue.getOptionValueKey())
				.withDisplayName(skuOptionValue.getDisplayName(locale, true))
				.build();
	}

	@Reference
	public void setResourceOperationContext(final ResourceOperationContext resourceOperationContext) {
		this.resourceOperationContext = resourceOperationContext;
	}

	@Reference
	public void setOrderRepository(final OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}
}
