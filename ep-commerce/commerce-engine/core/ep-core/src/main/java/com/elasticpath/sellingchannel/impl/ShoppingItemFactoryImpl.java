/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.sellingchannel.impl;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.InvalidProductStructureException;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.sellingchannel.ShoppingItemFactory;

/**
 * {@see ShoppingItemFactory}.
 */
public class ShoppingItemFactoryImpl implements ShoppingItemFactory {

	private BeanFactory beanFactory;

	@Override
	public ShoppingItem createShoppingItem(final ProductSku sku, final Price price,
			final int quantity, final int ordering, final Map<String, String> itemFields) {
		sanityCheck(sku, price);

		ShoppingItem shoppingItem = createShoppingItemBean();
		if (sku != null) {
			shoppingItem.setSkuGuid(sku.getGuid());
		}

		shoppingItem.setPrice(quantity, price);
		shoppingItem.setOrdering(ordering);
		shoppingItem.mergeFieldValues(itemFields);

		return shoppingItem;
	}

	/**
	 * @return a shopping item form bean instance
	 */
	protected ShoppingItem createShoppingItemBean() {
		return beanFactory.getBean(ContextIdNames.SHOPPING_ITEM);
	}

	/**
	 * Gets minimum quantity from product sku.
	 *
	 * @param sku Product Sku.
	 * @return the quantity
	 */
	protected int getMinQuantity(final ProductSku sku) {
		return sku.getProduct().getMinOrderQty();
	}

	/**
	 * Do sanity check.
	 *
	 * @param sku   product sku.
	 * @param price price.
	 */
	protected void sanityCheck(final ProductSku sku, final Price price) {
		if (sku.getProduct() == null) {
			throw new InvalidProductStructureException("Missing product object for sku " + sku.getSkuCode());
		}

		if (StringUtils.isEmpty(sku.getProduct().getCode())) {
			throw new InvalidProductStructureException("Missing product code for sku " + sku.getSkuCode());
		}
	}

	/**
	 * Sets the bean factory for creating beans.
	 *
	 * @param beanFactory The bean factory.
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

}
