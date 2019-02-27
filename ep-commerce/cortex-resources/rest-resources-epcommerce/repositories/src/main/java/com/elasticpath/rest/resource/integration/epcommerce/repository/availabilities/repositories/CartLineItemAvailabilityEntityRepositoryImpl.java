/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.availabilities.repositories;

import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.springframework.core.convert.ConversionService;

import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.availabilities.AvailabilityEntity;
import com.elasticpath.rest.definition.availabilities.AvailabilityForCartLineItemIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.product.StoreProductRepository;

/**
 * Cart Line Item Availability Entity Repository.
 *
 * @param <E> the entity type
 * @param <I> the identifier type
 */
@Component
public class CartLineItemAvailabilityEntityRepositoryImpl<E extends AvailabilityEntity, I extends AvailabilityForCartLineItemIdentifier>
		implements Repository<AvailabilityEntity, AvailabilityForCartLineItemIdentifier> {

	private ShoppingCartRepository shoppingCartRepository;
	private StoreProductRepository storeProductRepository;
	private ConversionService conversionService;

	@Override
	public Single<AvailabilityEntity> findOne(final AvailabilityForCartLineItemIdentifier identifier) {
		final String lineItemId = identifier.getLineItem().getLineItemId().getValue();
		final String scope = identifier.getLineItem().getLineItems().getCart().getScope().getValue();

		return shoppingCartRepository.getProductSku(identifier.getLineItem().getLineItems().getCart().getCartId().getValue(), lineItemId)
				.flatMap(productSku -> getStoreProduct(scope, productSku)
						.map(storeProduct -> convertStoreProductSkuToAvailabilityEntity(storeProduct, productSku)));
	}

	/**
	 * Get the StoreProduct from the given ProductSku and scope.
	 *
	 * @param scope      scope
	 * @param productSku productSku
	 * @return the store product
	 */
	protected Single<StoreProduct> getStoreProduct(final String scope, final ProductSku productSku) {
		return storeProductRepository.findDisplayableStoreProductWithAttributesByProductGuidAsSingle(scope, productSku.getProduct().getGuid());
	}

	/**
	 * Converts StoreProduct and ProductSku to an AvailabilityEntity.
	 *
	 * @param storeProduct storeProduct
	 * @param productSku   productSku
	 * @return AvailabilityEntity
	 */
	protected AvailabilityEntity convertStoreProductSkuToAvailabilityEntity(final StoreProduct storeProduct, final ProductSku productSku) {
		return conversionService.convert(new Pair<>(storeProduct, productSku), AvailabilityEntity.class);
	}

	@Reference
	public void setShoppingCartRepository(final ShoppingCartRepository shoppingCartRepository) {
		this.shoppingCartRepository = shoppingCartRepository;
	}

	@Reference
	public void setStoreProductRepository(final StoreProductRepository storeProductRepository) {
		this.storeProductRepository = storeProductRepository;
	}

	@Reference
	public void setConversionService(final ConversionService conversionService) {
		this.conversionService = conversionService;
	}
}
