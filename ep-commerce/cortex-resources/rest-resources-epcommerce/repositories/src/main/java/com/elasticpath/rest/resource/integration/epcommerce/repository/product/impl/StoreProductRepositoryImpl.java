/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.product.impl;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import io.reactivex.Single;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.product.StoreProductRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.store.StoreRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;
import com.elasticpath.service.catalog.ProductLookup;
import com.elasticpath.service.catalogview.StoreProductService;

/**
 * Repository for {@link StoreProduct}s.
 */
@Singleton
@Named("storeProductRepository")
public class StoreProductRepositoryImpl implements StoreProductRepository {
	private final StoreRepository storeRepository;
	private final ProductLookup coreProductLookup;
	private final StoreProductService coreStoreProductService;
	private final ProductSkuRepository productSkuRepository;
	private final ReactiveAdapter reactiveAdapter;

	/**
	 * Instantiates a new store product repository impl.
	 *
	 * @param storeRepository         the store repository
	 * @param coreProductLookup       the core product lookup
	 * @param coreStoreProductService the core store product service
	 * @param productSkuRepository    the product sku repository
	 * @param reactiveAdapter         reactiveAdapter
	 */
	@Inject
	StoreProductRepositoryImpl(
			@Named("storeRepository") final StoreRepository storeRepository,
			@Named("productLookup") final ProductLookup coreProductLookup,
			@Named("storeProductService") final StoreProductService coreStoreProductService,
			@Named("productSkuRepository") final ProductSkuRepository productSkuRepository,
			@Named("reactiveAdapter") final ReactiveAdapter reactiveAdapter) {

		this.storeRepository = storeRepository;
		this.coreProductLookup = coreProductLookup;
		this.coreStoreProductService = coreStoreProductService;
		this.productSkuRepository = productSkuRepository;
		this.reactiveAdapter = reactiveAdapter;
	}

	@Override
	public ExecutionResult<StoreProduct> findDisplayableStoreProductWithAttributesByProductGuid(final String storeCode, final String productGuid) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				Product product = Assign.ifNotNull(findByGuid(productGuid), OnFailure.returnNotFound("Product not found"));
				StoreProduct storeProduct = Assign.ifSuccessful(findDisplayableStoreProductWithAttributesForProduct(storeCode, product));

				return ExecutionResultFactory.createReadOK(storeProduct);
			}
		}.execute();
	}

	@Override
	public Single<StoreProduct> findDisplayableStoreProductWithAttributesByProductGuidAsSingle(final String storeCode, final String productGuid) {
		return reactiveAdapter.fromServiceAsSingle(() -> findByGuid(productGuid), "Product not found")
				.flatMap(product -> findDisplayableStoreProductWithAttributesForProductAsSingle(storeCode, product));
	}

	@Override
	public Single<StoreProduct> findDisplayableStoreProductWithAttributesBySkuGuid(final String storeCode, final String skuGuid) {
		return productSkuRepository.getProductSkuWithAttributesByGuidAsSingle(skuGuid)
				.flatMap(productSku -> findDisplayableStoreProductWithAttributesForProductAsSingle(storeCode, productSku.getProduct()));
	}

	@CacheResult
	private ExecutionResult<StoreProduct> findDisplayableStoreProductWithAttributesForProduct(final String storeCode, final Product product) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				Store store = Assign.ifSuccessful(storeRepository.findStore(storeCode));
				StoreProduct storeProduct = Assign.ifNotNull(coreStoreProductService.getProductForStore(product, store),
						OnFailure.returnNotFound("Store product not found"));
				return ExecutionResultFactory.createReadOK(storeProduct);
			}
		}.execute();
	}

	@Override
	@CacheResult
	public Product findByGuid(final String productGuid) {
		return coreProductLookup.findByGuid(productGuid);
	}

	@Override
	public List<Product> findByUids(final List<Long> productUids) {
		List<Product> productsToReturn = new ArrayList<>();

		for (Long productUid : productUids) {
			Product product = findByUid(productUid);
			productsToReturn.add(product);
		}

		return productsToReturn;
	}

	private Single<StoreProduct> findDisplayableStoreProductWithAttributesForProductAsSingle(final String storeCode, final Product product) {
		return storeRepository.findStoreAsSingle(storeCode)
				.flatMap(store -> reactiveAdapter
						.fromServiceAsSingle(() -> getProductForStore(product, store), "Store product not found"));
	}

	@CacheResult
	private Product findByUid(final long productUid) {
		return coreProductLookup.findByUid(productUid);
	}

	@CacheResult
	private StoreProduct getProductForStore(final Product product, final Store store) {
		return coreStoreProductService.getProductForStore(product, store);
	}
}
