/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.shoppingcart.validation.AddProductSkuToCartValidationService;
import com.elasticpath.service.shoppingcart.validation.ProductSkuValidationContext;

/**
 * Implements @{link AddProductSkuToCartValidationService}.
 */
public class AddProductSkuToCartValidationServiceImpl
		extends AbstractAggregateValidator<ProductSkuValidationContext, ProductSkuValidationContext>
		implements AddProductSkuToCartValidationService {

	private BeanFactory beanFactory;

	private PriceLookupFacade priceLookupFacade;

	@Override
	public ProductSkuValidationContext buildContext(final ProductSku productSku, final ProductSku parentProductSku,
			final Store store, final Shopper shopper) {
		final ProductSkuValidationContext context = beanFactory.getBean(ContextIdNames.PRODUCT_SKU_VALIDATION_CONTEXT);
		context.setProductSku(productSku);
		context.setParentProductSku(parentProductSku);
		context.setStore(store);
		context.setShopper(shopper);
		context.setPromotedPrice(priceLookupFacade.getPromotedPriceForSku(productSku, store, shopper));
		return context;
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	protected PriceLookupFacade getPriceLookupFacade() {
		return priceLookupFacade;
	}

	public void setPriceLookupFacade(final PriceLookupFacade priceLookupFacade) {
		this.priceLookupFacade = priceLookupFacade;
	}
}
