/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.service.pricing.datasource;

import java.util.Collection;
import java.util.List;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.pricing.PriceListAssignment;
import com.elasticpath.domain.pricing.PriceListDescriptor;
import com.elasticpath.domain.pricing.PriceListStack;

/**
 * Builds {@link BaseAmountDataSourceFactory}s that can provide base amounts for a variety of items and settings.
 */
public interface BaseAmountDataSourceFactoryBuilder {

	/**
	 * Builds the {@link BaseAmountDataSourceFactory} given the parameters that are passed before.
	 * @return an instance of {@link com.elasticpath.service.pricing.datasource.impl.CollectionBaseAmountDataSourceFactory}.
	 */
	BaseAmountDataSourceFactory build();

	/**
	 * Prepares for the given products.
	 * @param products products for which the data source factory can provide base amounts
	 * @return same builder instance
	 */
	BaseAmountDataSourceFactoryBuilder products(Product... products);

	/**
	 * Prepares for the given products.
	 * @param products products for which the data source factory can provide base amounts
	 * @return same builder instance
	 */
	BaseAmountDataSourceFactoryBuilder products(Collection<Product> products);

	/**
	 * Prepares for the given SKUs.
	 * @param skus SKUs for which the data source factory can provide base amounts
	 * @return same builder instance
	 */
	BaseAmountDataSourceFactoryBuilder skus(ProductSku... skus);

	/**
	 * Prepares for the given products.
	 * @param skus SKUs for which the data source factory can provide base amounts
	 * @return same builder instance
	 */
	BaseAmountDataSourceFactoryBuilder skus(Collection<ProductSku> skus);

	/**
	 * Prepares for the given PriceListDescriptors.
	 * @param priceListDescriptors PriceListDescriptors for which the data source factory can provide base amounts
	 * @return same builder instance
	 */
	BaseAmountDataSourceFactoryBuilder priceLists(PriceListDescriptor... priceListDescriptors);

	/**
	 * Prepares for the given PriceListDescriptors.
	 * @param priceListDescriptors PriceListDescriptors for which the data source factory can provide base amounts
	 * @return same builder instance
	 */
	BaseAmountDataSourceFactoryBuilder priceLists(Collection<PriceListDescriptor> priceListDescriptors);


	/**
	 * Prepares for the given PriceListAssignments.
	 * @param plas PriceListAssignments for which the data source factory can provide base amounts
	 * @return same builder instance
	 */
	BaseAmountDataSourceFactoryBuilder priceListAssignments(PriceListAssignment... plas);

	/**
	 * Prepares for the given PriceListAssignments.
	 * @param plas PriceListAssignments for which the data source factory can provide base amounts
	 * @return same builder instance
	 */
	BaseAmountDataSourceFactoryBuilder priceListAssignments(Collection<PriceListAssignment> plas);


	/**
	 * Prepares for the given PriceListDescriptors.
	 * @param plGuids GUIDs of PriceListDescriptors for which the data source factory can provide base amounts
	 * @return same builder instance
	 */
	BaseAmountDataSourceFactoryBuilder priceListGuids(List<String> plGuids);


	/**
	 * Prepares for the given PriceListStack.
	 * @param plStack the PriceListStack for which the data source factory can provide base amounts
	 * @return same builder instance
	 */
	BaseAmountDataSourceFactoryBuilder priceListStack(PriceListStack plStack);


	/**
	 * Propers for the objects (products/SKUs) with the given GUIDs.
	 * @param objectGuids GUIDs of the products/SKUs for which the data source factory can provide base amounts
	 * @return same builder instance
	 */
	BaseAmountDataSourceFactoryBuilder objectGuids(List<String> objectGuids);

}
