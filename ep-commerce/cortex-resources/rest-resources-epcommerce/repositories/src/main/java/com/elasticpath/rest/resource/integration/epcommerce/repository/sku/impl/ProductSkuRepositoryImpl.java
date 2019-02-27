/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.sku.impl;

import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import io.reactivex.Maybe;
import io.reactivex.Single;

import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.id.util.CompositeIdUtil;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.catalogview.StoreProductService;
import com.elasticpath.service.store.StoreService;

/**
 * Product SKU repository.
 */
@Singleton
@Named("productSkuRepository")
public class ProductSkuRepositoryImpl implements ProductSkuRepository {

	private static final String NOT_FOUND_MESSAGE = "Could not find item for item ID.";

	private final ProductSkuLookup productSkuLookup;

	private final ReactiveAdapter reactiveAdapter;

	private final StoreService storeService;

	private final StoreProductService storeProductService;

	/**
	 * Instantiates a new product sku repository.
	 *
	 * @param productSkuLookup the product sku lookup
	 * @param reactiveAdapter the reactive adapter
	 * @param storeService the store service
	 * @param storeProductService the store product service
	 */
	@Inject
	public ProductSkuRepositoryImpl(
			@Named("productSkuLookup") final ProductSkuLookup productSkuLookup,
			@Named("reactiveAdapter")  final ReactiveAdapter reactiveAdapter,
			@Named("storeService") final StoreService storeService,
			@Named("storeProductService") final StoreProductService storeProductService) {

		this.productSkuLookup = productSkuLookup;
		this.reactiveAdapter = reactiveAdapter;
		this.storeService = storeService;
		this.storeProductService = storeProductService;
	}

	@Override
	@CacheResult(uniqueIdentifier = "getProductSkuWithAttributesByCodeAsSingle")
	public Single<ProductSku> getProductSkuWithAttributesByCodeAsSingle(final String skuCode) {
		return reactiveAdapter.fromServiceAsSingle(() -> productSkuLookup.findBySkuCode(skuCode), NOT_FOUND_MESSAGE);
	}


	@Override
	@CacheResult(uniqueIdentifier = "getProductSkuWithAttributesByCode")
	public ExecutionResult<ProductSku> getProductSkuWithAttributesByCode(final String skuCode) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				ProductSku productSku = Assign.ifNotNull(productSkuLookup.findBySkuCode(skuCode),
						OnFailure.returnNotFound(NOT_FOUND_MESSAGE));
				return ExecutionResultFactory.createReadOK(productSku);
			}
		}.execute();
	}

	@Override
	@CacheResult(uniqueIdentifier = "getProductSkuWithAttributesByGuidAsSingle")
	public Single<ProductSku> getProductSkuWithAttributesByGuidAsSingle(final String skuGuid) {
		return reactiveAdapter.fromServiceAsSingle(() -> productSkuLookup.findByGuid(skuGuid), NOT_FOUND_MESSAGE);
	}

	@Override
	@CacheResult(uniqueIdentifier = "getProductSkuWithAttributesByGuid")
	public ExecutionResult<ProductSku> getProductSkuWithAttributesByGuid(final String skuGuid) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				ProductSku productSku = Assign.ifNotNull(productSkuLookup.findByGuid(skuGuid),
						OnFailure.returnNotFound(NOT_FOUND_MESSAGE));
				return ExecutionResultFactory.createReadOK(productSku);
			}
		}.execute();
	}

	@Override
	@CacheResult
	public ExecutionResult<Boolean> isProductBundle(final String skuGuid) {
		return new ExecutionResultChain() {
			@Override
			protected ExecutionResult<?> build() {
				ProductSku sku = Assign.ifSuccessful(getProductSkuWithAttributesByGuid(skuGuid));
				return ExecutionResultFactory.createReadOK(sku.getProduct() instanceof ProductBundle);
			}
		}.execute();
	}

	@Override
	@CacheResult(uniqueIdentifier = "isProductSkuExist")
	public ExecutionResult<Boolean> isProductSkuExist(final String encodedItemId) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				String skuCode = Assign.ifSuccessful(getField(encodedItemId));
				Boolean isProductSkuExist = productSkuLookup.isProductSkuExist(skuCode);
				return ExecutionResultFactory.createReadOK(isProductSkuExist);
			}
		}.execute();
	}

	private static ExecutionResult<String> getField(final String itemId) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				Map<String, String> compositeItemIdFields = Assign.ifNotNull(CompositeIdUtil.decodeCompositeId(itemId),
					OnFailure.returnNotFound("Item not found"));

				return ExecutionResultFactory.createReadOK(compositeItemIdFields.get("S"));
			}
		}.execute();
	}

	@Override
	@CacheResult(uniqueIdentifier = "isDisplayableProductSkuForStore")
	public Single<Boolean> isDisplayableProductSkuForStore(final String productSkuCode, final String storeCode) {

		return reactiveAdapter.<ProductSku>fromServiceAsMaybe(() -> productSkuLookup.findBySkuCode(productSkuCode))
				.map(productSku -> storeProductService.getProductForStore(productSku.getProduct(),
						storeService.findStoreWithCode(storeCode)))
				.map(StoreProduct::isProductDisplayable)
				.switchIfEmpty(Maybe.just(false))
				.toSingle();

	}
}
