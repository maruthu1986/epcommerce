/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.service.cache.impl;

import static java.lang.String.format;
import static java.lang.System.identityHashCode;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

import org.apache.log4j.Logger;

import com.elasticpath.cache.Cache;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItemContainer;
import com.elasticpath.shipping.connectivity.dto.ShippableItemContainer;
import com.elasticpath.shipping.connectivity.dto.ShippingAddress;
import com.elasticpath.shipping.connectivity.dto.ShippingCalculationResult;
import com.elasticpath.shipping.connectivity.service.ShippingCalculationService;
import com.elasticpath.shipping.connectivity.service.cache.ShippingCalculationResultCacheKey;
import com.elasticpath.shipping.connectivity.service.cache.ShippingCalculationResultCacheKeyBuilder;

/**
 * An implementation of both {@link ShippingCalculationService} that caches results to avoid duplicate requests being made to delegate providers.
 * <p>
 * delegates that actually return priced {@link ShippingCalculationResult} objects
 * as some external Shipping Option services may not be able to provide unpriced options. In this case any subsequent priced requests made will also
 * make use of the priced result as long as none of the cart items, pricing etc., has changed in the meantime.
 */
public class CachingShippingCalculationServiceImpl implements ShippingCalculationService {
	private static final Logger LOG = Logger.getLogger(CachingShippingCalculationServiceImpl.class);

	private Cache<ShippingCalculationResultCacheKey, ShippingCalculationResult> cache;
	private Supplier<ShippingCalculationResultCacheKeyBuilder> cacheKeyBuilderSupplier;
	private ShippingCalculationService fallBackShippingCalculationService;

	@Override
	public ShippingCalculationResult getUnpricedShippingOptions(final ShippableItemContainer<?> shippableItemContainer) {

		final ShippingAddress destination = shippableItemContainer.getDestinationAddress();
		final String storeCode = shippableItemContainer.getStoreCode();

		final ShippingCalculationResult result;

		if (LOG.isDebugEnabled()) {
			LOG.debug(format("Searching for unpriced shipping calculation result in cache using store [%s] and destination [%s]...",
					storeCode, destination.toString()));
		}

		final ShippingCalculationResultCacheKey unpricedCacheKey = createUnpricedCacheKey(shippableItemContainer);

		final Optional<ShippingCalculationResult> cachedResult = getCachedShippingCalculationResult(unpricedCacheKey);
		if (cachedResult.isPresent()) {
			result = cachedResult.get();
		} else {
			LOG.debug("Calling delegate UnpricedShippingCalculationPlugin for result.");
			result = fallBackShippingCalculationService.getUnpricedShippingOptions(shippableItemContainer);

			cacheShippingCalculationResult(unpricedCacheKey, result);

		}

		return result;
	}

	@Override
	public ShippingCalculationResult getPricedShippingOptions(final PricedShippableItemContainer<?> pricedShippableItemContainer) {
		final ShippingCalculationResult result;

		final String storeCode = pricedShippableItemContainer.getStoreCode();
		final ShippingAddress destination = pricedShippableItemContainer.getDestinationAddress();

		if (LOG.isDebugEnabled()) {
			LOG.debug(format("Searching for priced shipping calculation result in cache using store [%s] and destination [%s]...",
					storeCode, destination.toString()));
		}

		final ShippingCalculationResultCacheKey cacheKey = createPricedCacheKey(pricedShippableItemContainer);

		final Optional<ShippingCalculationResult> cachedResult = getCachedShippingCalculationResult(cacheKey);
		if (cachedResult.isPresent()) {
			result = cachedResult.get();
		} else {
			LOG.debug("Calling delegate PricedShippingCalculationPlugin for result.");
			result = fallBackShippingCalculationService.getPricedShippingOptions(pricedShippableItemContainer);

			// Cache the result as a priced result
			cacheShippingCalculationResult(cacheKey, result);

			LOG.debug("Just cached result using a priced cache key, now caching with an unpriced cache key to also service unpriced requests.");
			final ShippingCalculationResultCacheKey unpricedCacheKey = createUnpricedCacheKey(pricedShippableItemContainer);
			cacheShippingCalculationResult(unpricedCacheKey, result);
		}

		return result;
	}

	@Override
	public ShippingCalculationResult getAllShippingOptions(final String storeCode, final Locale locale) {
		final ShippingCalculationResult result;

		if (LOG.isDebugEnabled()) {
			LOG.debug(format("Searching for all shipping calculation result in cache using store [%s] and locale [%s]...",
					storeCode, locale.toString()));
		}

		final ShippingCalculationResultCacheKey cacheKey = getCacheKeyBuilderSupplier().get()
				.withStoreCode(storeCode)
				.withLocale(locale)
				.build();

		final Optional<ShippingCalculationResult> cachedResult = getCachedShippingCalculationResult(cacheKey);
		if (cachedResult.isPresent()) {
			result = cachedResult.get();
		} else {
			LOG.debug("Calling delegate fallBackShippingCalculationService for result.");

			result = fallBackShippingCalculationService.getAllShippingOptions(storeCode, locale);

			cacheShippingCalculationResult(cacheKey, result);
		}

		return result;
	}

	@Override
	public ShippingCalculationResult getUnpricedShippingOptions(final ShippingAddress destAddress, final String storeCode, final Locale locale) {
		final ShippingCalculationResult result;

		if (LOG.isDebugEnabled()) {
			LOG.debug(format("Searching for unpriced shipping calculation result in cache using store [%s] and destination [%s]...",
					storeCode, locale.toString()));
		}

		final ShippingCalculationResultCacheKey cacheKey = getCacheKeyBuilderSupplier().get()
				.withDestination(destAddress)
				.withStoreCode(storeCode)
				.withLocale(locale)
				.build();

		final Optional<ShippingCalculationResult> cachedResult = getCachedShippingCalculationResult(cacheKey);
		if (cachedResult.isPresent()) {
			result = cachedResult.get();
		} else {
			LOG.debug("Calling delegate fallBackShippingCalculationService for result.");

			result = fallBackShippingCalculationService.getUnpricedShippingOptions(destAddress, storeCode, locale);

			cacheShippingCalculationResult(cacheKey, result);
		}

		return result;
	}

	/**
	 * Gets shipping calculation result from cache for given key.
	 *
	 * @param cacheKey the cache key
	 * @return cached shipping calculation result
	 */
	protected Optional<ShippingCalculationResult> getCachedShippingCalculationResult(final ShippingCalculationResultCacheKey cacheKey) {
		final Optional<ShippingCalculationResult> result = Optional.ofNullable(getCache().get(cacheKey));

		if (LOG.isDebugEnabled()) {
			if (result.isPresent()) {
				LOG.debug(
						format("Shipping calculation result found in cache. Result id: [%d], Key: [%s]", identityHashCode(result.get()), cacheKey));
			} else {
				LOG.debug("Shipping calculation result not found in cache. Key: " + cacheKey);
			}
		}

		return result;
	}

	/**
	 * Stores the given shipping calculation result in cache using given key.
	 *
	 * @param cacheKey the cache key
	 * @param result   the shipping calculation result
	 */
	protected void cacheShippingCalculationResult(final ShippingCalculationResultCacheKey cacheKey, final ShippingCalculationResult result) {
		if (LOG.isDebugEnabled()) {
			LOG.debug(format("Caching Shipping calculation result. Result id: [%d], Key: [%s]", identityHashCode(result), cacheKey));
		}
		getCache().put(cacheKey, result);
	}

	/**
	 * Creates a cache key for an unpriced shipping calculation result.
	 *
	 * @param shippableItemContainer the generic shippable item container
	 * @return the created cache key.
	 */
	protected ShippingCalculationResultCacheKey createUnpricedCacheKey(final ShippableItemContainer<?> shippableItemContainer) {
		return getCacheKeyBuilderSupplier().get()
				.withShippableItems(shippableItemContainer.getShippableItems())
				.withDestination(shippableItemContainer.getDestinationAddress())
				.withStoreCode(shippableItemContainer.getStoreCode())
				.withLocale(shippableItemContainer.getLocale())
				.build();
	}

	/**
	 * Creates a cache key for a priced shipping calculation result.
	 *
	 * @param pricedShippableItemContainer the priced shippable item container.
	 * @return the created cache key.
	 */
	protected ShippingCalculationResultCacheKey createPricedCacheKey(final PricedShippableItemContainer<?> pricedShippableItemContainer) {
		return getCacheKeyBuilderSupplier().get()
				.withPricedShippableItems(pricedShippableItemContainer.getShippableItems())
				.withDestination(pricedShippableItemContainer.getDestinationAddress())
				.withStoreCode(pricedShippableItemContainer.getStoreCode())
				.withLocale(pricedShippableItemContainer.getLocale())
				.withCurrency(pricedShippableItemContainer.getCurrency())
				.build();
	}

	public Cache<ShippingCalculationResultCacheKey, ShippingCalculationResult> getCache() {
		return this.cache;
	}

	public void setCache(final Cache<ShippingCalculationResultCacheKey, ShippingCalculationResult> cache) {
		this.cache = cache;
	}

	protected Supplier<ShippingCalculationResultCacheKeyBuilder> getCacheKeyBuilderSupplier() {
		return this.cacheKeyBuilderSupplier;
	}

	public void setCacheKeyBuilderSupplier(final Supplier<ShippingCalculationResultCacheKeyBuilder> cacheKeyBuilderSupplier) {
		this.cacheKeyBuilderSupplier = cacheKeyBuilderSupplier;
	}

	public void setShippingCalculationService(final ShippingCalculationService fallBackShippingCalculationService) {
		this.fallBackShippingCalculationService = fallBackShippingCalculationService;
	}

}
