/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.common.pricing.service;

import java.util.Collection;
import java.util.Currency;
import java.util.Map;

import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.pricing.PriceListStack;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.pricing.datasource.BaseAmountDataSourceFactory;

/**
 * Obtains the promoted price for the given product/SKUs.
 */
public interface PromotedPriceLookupService {
	/**
	 * Obtain the Prices for a given {@link ProductSku} in a {@link PriceListStack}.
	 *
	 * @param sku to look up
	 * @param plStack a {@link PriceListStack} for price lookup
	 * @param store store
	 * @return the price map where key is the Price List guid and value is the price for this guid
	 */
	Map<String, Price> getSkuPrices(ProductSku sku, PriceListStack plStack, Store store);

	/**
	 * Obtain the Price for a given {@link ProductSku} in a {@link PriceListStack}.
	 *
	 * @param sku to look up
	 * @param plStack a {@link PriceListStack} for price lookup
	 * @param store store
	 * @return the catalog promoted price for the sku
	 */
	Price getSkuPrice(ProductSku sku, PriceListStack plStack, Store store);


	/**
	 * Obtain the Price for a given {@link Product} in a {@link PriceListStack}.
	 *
	 * @param product the product to look up
	 * @param plStack a {@link PriceListStack} for price lookup
	 * @param store store
	 * @return the catalog promoted price for the product
	 */
	Price getProductPrice(Product product, PriceListStack plStack, Store store);


	/**
	 * Obtain the Price for a given {@link Product} in a {@link PriceListStack}.
	 *
	 * @param product the product to look up
	 * @param plStack a {@link PriceListStack} for price lookup
	 * @param store store
	 * @param dataSourceFactory the BaseAmountDataSourceFactory to be used to access the base amounts.
	 * @return the catalog promoted price for the product
	 */
	Price getProductPrice(Product product, PriceListStack plStack, Store store,
							BaseAmountDataSourceFactory dataSourceFactory);


	/**
	 * Obtain the prices for the given collection of {@link Product} in a {@link PriceListStack}.
	 *
	 * @param products the products to look up
	 * @param plStack a {@link PriceListStack} for price lookup
	 * @param store store
	 * @return price map where key is the product code and value is the price for this code
	 */
	Map<String, Price> getProductsPrices(Collection<Product> products,
											PriceListStack plStack, Store store);


	/**
	 * Apply catalog promotions on a given product price.
	 *
	 * @param product the product used to determine the catalog promotion conditions to apply to the price
	 * @param store the store catalog used to determine the catalog promotions
	 * @param currency currency
	 * @param price to be promoted
	 */
	void applyCatalogPromotions(Product product, Store store, Currency currency, Price price);


}
