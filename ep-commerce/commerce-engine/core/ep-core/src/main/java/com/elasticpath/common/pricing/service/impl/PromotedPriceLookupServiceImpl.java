/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.common.pricing.service.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.elasticpath.common.pricing.service.PromotedPriceLookupService;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.pricing.PriceListStack;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalog.BundleIdentifier;
import com.elasticpath.service.pricing.PriceLookupService;
import com.elasticpath.service.pricing.PriceProvider;
import com.elasticpath.service.pricing.PricedEntityFactory;
import com.elasticpath.service.pricing.datasource.BaseAmountDataSourceFactory;
import com.elasticpath.service.pricing.datasource.BaseAmountDataSourceFactoryBuilder;
import com.elasticpath.service.pricing.datasource.impl.NoPreprocessBaseAmountDataSourceFactory;
import com.elasticpath.service.rules.EpRuleEngine;

/**
 * This class looks up the promoted price for products/SKUs.
 */
public class PromotedPriceLookupServiceImpl implements PromotedPriceLookupService {

	private PriceLookupService priceLookupService;
	private PricedEntityFactory pricedEntityFactory;
	private BundleIdentifier bundleIdentifier;
	private EpRuleEngine epRuleEngine;
	private BeanFactory beanFactory;
	private static final Logger LOG = Logger.getLogger(PromotedPriceLookupServiceImpl.class);

	@Override
	public Map<String, Price> getSkuPrices(final ProductSku sku, final PriceListStack plStack, final Store store) {
		if (getBundleIdentifier().isCalculatedBundle(sku)) {
			LOG.error("Operation is not supported on calculated bundles.");
			return Collections.emptyMap();
		}
		Map<String, Price> prices = priceLookupService.getSkuPrices(sku, plStack);

		for (Price price : prices.values()) {
			applyCatalogPromotions(sku.getProduct(), store, price.getCurrency(), price);
		}
		return prices;
	}

	@Override
	public Price getSkuPrice(final ProductSku sku, final PriceListStack plStack, final Store store) {
		return getSkuPrice(sku, plStack, store, new NoPreprocessBaseAmountDataSourceFactory());
	}

	private Price getSkuPrice(final ProductSku sku, final PriceListStack plStack, final Store store,
								final BaseAmountDataSourceFactory dataSourceFactory) {
		PriceProvider priceProvider = getPriceProvider(store, plStack, dataSourceFactory);
		Price priceForSku = getPricedEntityFactory().createPricedProductSku(sku, plStack, priceProvider, dataSourceFactory).getPrice();
		if (priceForSku != null) {
			applyCatalogPromotions(sku.getProduct(), store, priceForSku.getCurrency(), priceForSku);
		}
		return priceForSku;
	}

	@Override
	public Price getProductPrice(final Product product, final PriceListStack plStack, final Store store) {
		return getProductPrice(product, plStack, store, new NoPreprocessBaseAmountDataSourceFactory());
	}

	@Override
	public Price getProductPrice(final Product product, final PriceListStack plStack, final Store store,
									final BaseAmountDataSourceFactory dataSourceFactory) {
		PriceProvider priceProvider = getPriceProvider(store, plStack, dataSourceFactory);
		return getPricedEntityFactory().createPricedProduct(product, priceProvider).getPrice();
	}

	private PriceProvider getPriceProvider(final Store store, final PriceListStack plStack,
											final BaseAmountDataSourceFactory dataSourceFactory) {
		return new PriceProvider() {

			@Override
			public Price getProductSkuPrice(final ProductSku productSku) {
				return PromotedPriceLookupServiceImpl.this.getSkuPrice(productSku, plStack, store, dataSourceFactory);
			}

			@Override
			public Price getProductPrice(final Product product) {
				return PromotedPriceLookupServiceImpl.this.getProductPrice(product, plStack, store, dataSourceFactory);
			}

			@Override
			public Currency getCurrency() {
				return plStack.getCurrency();
			}
		};
	}

	@Override
	public Map<String, Price> getProductsPrices(final Collection<Product> products,
												final PriceListStack plStack, final Store store) {
		final Map<String, Price> productCodePrice = new HashMap<>(products.size());
		BaseAmountDataSourceFactoryBuilder builder = getDataSourceFactoryBuilder();
		BaseAmountDataSourceFactory dataSourceFactory = builder.priceListStack(plStack).products(products).build();
		for (Product product : products) {
			Price price = getProductPrice(product, plStack, store, dataSourceFactory);
			productCodePrice.put(product.getCode(), price);
		}
		return productCodePrice;
	}

	/**
	 * @param product product
	 * @param store store
	 * @param currency currency
	 * @param priceForSku price for sku
	 */
	@Override
	public void applyCatalogPromotions(final Product product, final Store store, final Currency currency,
									   final Price priceForSku) {
		if (!getBundleIdentifier().isCalculatedBundle(product)) {
			getEpRuleEngine().fireCatalogPromotionRules(Arrays.asList(product), currency, store,
					Collections.singletonMap(product.getCode(), Arrays.asList(priceForSku)));
		}
	}



	protected BaseAmountDataSourceFactoryBuilder getDataSourceFactoryBuilder() {
		return getBeanFactory().getBean(ContextIdNames.BASE_AMOUNT_DATA_SOURCE_FACTORY_BUILDER);
	}


	public void setPriceLookupService(final PriceLookupService priceLookupService) {
		this.priceLookupService = priceLookupService;
	}

	protected PriceLookupService getPriceLookupService() {
		return priceLookupService;
	}

	public void setPricedEntityFactory(final PricedEntityFactory pricedEntityFactory) {
		this.pricedEntityFactory = pricedEntityFactory;
	}

	protected PricedEntityFactory getPricedEntityFactory() {
		return pricedEntityFactory;
	}

	public void setBundleIdentifier(final BundleIdentifier bundleIdentifier) {
		this.bundleIdentifier = bundleIdentifier;
	}

	protected BundleIdentifier getBundleIdentifier() {
		return bundleIdentifier;
	}

	public void setEpRuleEngine(final EpRuleEngine epRuleEngine) {
		this.epRuleEngine = epRuleEngine;
	}

	protected EpRuleEngine getEpRuleEngine() {
		return epRuleEngine;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

}
