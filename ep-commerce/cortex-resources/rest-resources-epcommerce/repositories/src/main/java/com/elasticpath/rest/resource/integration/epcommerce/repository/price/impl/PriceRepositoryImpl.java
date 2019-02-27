/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.price.impl;

import java.util.Collection;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import io.reactivex.Single;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.common.dto.sellingchannel.ShoppingItemDtoFactory;
import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shopper.ShopperReference;
import com.elasticpath.domain.shoppingcart.DiscountRecord;
import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerSessionRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.price.PriceRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.store.StoreRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;

/**
 * Repository that consolidates access to price domain concepts.
 */
@Singleton
@Named("priceRepository")
public class PriceRepositoryImpl implements PriceRepository {
	/**
	 * cache identifier.
	 */
	public static final String CACHE_PRICE_REPO_KEY_PREFIX = "priceRepository";
	/**
	 * error message when product price not found.
	 */
	public static final String PRODUCT_PRICE_NOT_FOUND = "No price found for product with guid = '%s'";
	/**
	 * error message when sku price not found.
	 */
	public static final String SKU_PRICE_NOT_FOUND = "No price found for sku with code = '%s'";
	private static final int SINGLE_QTY = 1;

	private final ShoppingItemDtoFactory shoppingItemDtoFactory;
	private final StoreRepository storeRepository;
	private final CustomerSessionRepository customerSessionRepository;
	private final PriceLookupFacade priceLookupFacade;
	private final ProductSkuRepository productSkuRepository;
	private final ReactiveAdapter reactiveAdapter;

	/**
	 * Creates instance with needed services wired in.
	 *
	 * @param shoppingItemDtoFactory    shopping item dto factory
	 * @param storeRepository           store repository
	 * @param customerSessionRepository customer session repository
	 * @param priceLookupFacade         price lookup facade
	 * @param productSkuRepository      product sku repository
	 * @param reactiveAdapter           reactive adapter
	 */
	@Inject
	PriceRepositoryImpl(
			@Named("shoppingItemDtoFactory") final ShoppingItemDtoFactory shoppingItemDtoFactory,
			@Named("storeRepository") final StoreRepository storeRepository,
			@Named("customerSessionRepository") final CustomerSessionRepository customerSessionRepository,
			@Named("priceLookupFacade") final PriceLookupFacade priceLookupFacade,
			@Named("productSkuRepository") final ProductSkuRepository productSkuRepository,
			@Named("reactiveAdapter") final ReactiveAdapter reactiveAdapter) {

		this.shoppingItemDtoFactory = shoppingItemDtoFactory;
		this.storeRepository = storeRepository;
		this.customerSessionRepository = customerSessionRepository;
		this.priceLookupFacade = priceLookupFacade;
		this.productSkuRepository = productSkuRepository;
		this.reactiveAdapter = reactiveAdapter;
	}


	@Override
	@CacheResult(uniqueIdentifier = "getPrice")
	public Single<Price> getPrice(final String storeCode, final String skuCode) {
		return customerSessionRepository.findOrCreateCustomerSessionAsSingle()
				.map(ShopperReference::getShopper)
				.flatMap(shopper -> Single.just(shoppingItemDtoFactory.createDto(skuCode, SINGLE_QTY))
						.flatMap(shoppingItemDto -> getPriceFromShoppingItemDto(storeCode, shopper, shoppingItemDto, skuCode)));
	}

	private Single<Price> getPriceFromShoppingItemDto(final String storeCode, final Shopper shopper, final ShoppingItemDto shoppingItemDto,
													  final String skuCode) {
		return storeRepository.findStoreAsSingle(storeCode)
				.flatMap(store -> getPriceFromStore(shopper, shoppingItemDto, store, skuCode));
	}

	private Single<Price> getPriceFromStore(final Shopper shopper, final ShoppingItemDto shoppingItemDto, final Store store, final String skuCode) {
		return reactiveAdapter.fromServiceAsSingle(() ->
				priceLookupFacade.getShoppingItemDtoPrice(shoppingItemDto, store, shopper), String.format(SKU_PRICE_NOT_FOUND, skuCode));
	}

	@Override
	@CacheResult(uniqueIdentifier = "getLowestPrice")
	public Single<Price> getLowestPrice(final String skuCode) {
		return getLowestPriceImpl(skuCode, true)
				.map(LowestPrice::getPrice);
	}

	@Override
	@CacheResult
	public Single<Set<Long>> getLowestPriceRules(final String storeCode, final String itemId) {
		return getLowestPriceImpl(itemId, false)
				.map(LowestPrice::getRules);
	}

	private Single<LowestPrice> getLowestPriceImpl(final String skuCode, final boolean validateMultipleSkus) {
		return getProduct(skuCode, validateMultipleSkus)
				.flatMap(product -> getPriceUsingPromotedPrice(product)
						.map(this::getLowestPriceAndRules));
	}

	private LowestPrice getLowestPriceAndRules(final Price price) {
		final Collection<DiscountRecord> discountRecords = price.getDiscountRecords();
		final Set<Long> appliedRules = ImmutableSet.copyOf(
				Iterables.transform(discountRecords, createDiscountRecordAppliedRulesFunction()));
		return new LowestPrice(price, appliedRules);
	}

	private Single<Price> getPriceUsingPromotedPrice(final Product product) {
		return customerSessionRepository.findOrCreateCustomerSessionAsSingle()
				.map(ShopperReference::getShopper)
				.flatMap(shopper -> storeRepository.findStoreAsSingle(shopper.getStoreCode())
						.flatMap(store -> getPriceSingleForProduct(product, shopper, store)));
	}

	private Single<Price> getPriceSingleForProduct(final Product product, final Shopper shopper, final Store store) {
		return reactiveAdapter.fromNullableAsSingle(() -> priceLookupFacade
				.getPromotedPriceForProduct(product, store, shopper), String.format(PRODUCT_PRICE_NOT_FOUND, product.getGuid()));
	}

	private Single<Product> getProduct(final String skuCode, final boolean validateMultipleSkus) {
		return productSkuRepository.getProductSkuWithAttributesByCodeAsSingle(skuCode).flatMap(productSku -> {
			Product product = productSku.getProduct();
			assert product != null : "product must not be null";
			assert product.getGuid() != null : "product guid must not be null.";
			if (validateMultipleSkus && !product.hasMultipleSkus()) {
				return Single.error(ResourceOperationFailure.notFound());
			}
			return Single.just(product);
		});
	}

	@Override
	@CacheResult(uniqueIdentifier = "priceExists")
	public Single<Boolean> priceExists(final String storeCode, final String skuCode) {
		return getPrice(storeCode, skuCode)
				.map(price -> Boolean.TRUE)
				.onErrorReturn(throwable -> Boolean.FALSE);
	}

	/**
	 * Factory method for creating a function to transform from a {@link DiscountRecord} to an applied rule ID.
	 *
	 * @return a function to transform from a {@link DiscountRecord} to an applied rule ID.
	 */
	protected Function<? super DiscountRecord, Long> createDiscountRecordAppliedRulesFunction() {
		return new DiscountRecordToAppliedRuleIdFunction();
	}

	/**
	 * Named immutable lowest price pair.
	 */
	static class LowestPrice {
		private final Price price;
		private final Set<Long> rules;

		/**
		 * constructor.
		 *
		 * @param price price
		 * @param rules rules
		 */
		LowestPrice(final Price price, final Set<Long> rules) {
			this.price = price;
			this.rules = rules;
		}

		public Set<Long> getRules() {
			return rules;
		}

		public Price getPrice() {

			return price;
		}
	}

	/**
	 * A function to transform from a {@link DiscountRecord} to an applied rule ID.
	 */
	private static final class DiscountRecordToAppliedRuleIdFunction implements Function<DiscountRecord, Long> {
		@Override
		public Long apply(final DiscountRecord input) {
			return input.getRuleId();
		}
	}

}
