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
import com.elasticpath.rest.definition.availabilities.AvailabilityForItemIdentifier;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.id.transform.IdentifierTransformerProvider;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.product.StoreProductRepository;

/**
 * Item Availability Entity Repository.
 *
 * @param <E> the entity type
 * @param <I> the identifier type
 */
@Component
public class ItemAvailabilityEntityRepositoryImpl<E extends AvailabilityEntity, I extends AvailabilityForItemIdentifier>
		implements Repository<AvailabilityEntity, AvailabilityForItemIdentifier> {

	private ItemRepository itemRepository;
	private StoreProductRepository storeProductRepository;
	private ConversionService conversionService;
	private IdentifierTransformerProvider identifierTransformerProvider;

	@Override
	public Single<AvailabilityEntity> findOne(final AvailabilityForItemIdentifier identifier) {
		final String scope = identifier.getItem().getItems().getScope().getValue();
		final String encodedItemId = identifierTransformerProvider.forUriPart(ItemIdentifier.ITEM_ID)
				.identifierToUri(identifier.getItem().getItemId());

		return itemRepository.getSkuForItemIdAsSingle(encodedItemId)
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
	public void setItemRepository(final ItemRepository itemRepository) {
		this.itemRepository = itemRepository;
	}

	@Reference
	public void setStoreProductRepository(final StoreProductRepository storeProductRepository) {
		this.storeProductRepository = storeProductRepository;
	}

	@Reference
	public void setConversionService(final ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	@Reference
	public void setIdentifierTransformerProvider(final IdentifierTransformerProvider identifierTransformerProvider) {
		this.identifierTransformerProvider = identifierTransformerProvider;
	}
}
