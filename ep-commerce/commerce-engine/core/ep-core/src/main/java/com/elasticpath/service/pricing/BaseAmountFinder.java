/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.service.pricing;

import java.util.Collection;
import java.util.List;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.BaseAmountObjectType;
import com.elasticpath.domain.pricing.PriceListStack;
import com.elasticpath.service.pricing.datasource.BaseAmountDataSource;

/**
 * <code>BaseAmountFinder</code> finds the base amounts related to different entities.
 */
public interface BaseAmountFinder {

	/**
	 * Get the list of base amounts related to the given sku in the given price lists.
	 * @param productSku the sku
	 * @param plStack the price list stack
	 * @param baseAmountDataSource the data source to be used to retrieve the base amounts
	 * @return list A list containing the related base amounts
	 */
	Collection<BaseAmount> getBaseAmounts(ProductSku productSku, PriceListStack plStack, BaseAmountDataSource baseAmountDataSource);

	/**
	 * Filters the given collection of base amounts according to the plGuid and object type. It not change the input collection.
	 * @param baseAmounts set of base amounts to filter
	 * @param plGuid price list descriptor guid.
	 * @param objectType product or sku
	 * @param guid product or sku guid
	 *
	 * @return filtered collection.
	 */
	List<BaseAmount> filterBaseAmounts(Collection<BaseAmount> baseAmounts, String plGuid, BaseAmountObjectType objectType,
			String guid);

}
