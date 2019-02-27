/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.shoppingcart.validation.ShoppingCartValidationContext;
import com.elasticpath.service.shoppingcart.validation.ShoppingCartValidator;
import com.elasticpath.service.shoppingcart.validation.ShoppingItemValidationContext;
import com.elasticpath.service.shoppingcart.validation.ShoppingItemValidator;

/**
 * Delegates shopping item validators from shopping cart validator.
 */
public class ShoppingItemDelegateFromShoppingCartValidatorImpl implements ShoppingCartValidator {

	private Collection<ShoppingItemValidator> shoppingItemValidators;
	private ProductSkuLookup productSkuLookup;
	private BeanFactory beanFactory;
	private PriceLookupFacade priceLookupFacade;

	@Override
	public Collection<StructuredErrorMessage> validate(final ShoppingCartValidationContext shoppingCartValidationContext) {
		Map<ShoppingItem, ProductSku> shoppingItemProductSkuMap = shoppingCartValidationContext.getShoppingCart().getShoppingItemProductSkuMap();

		return shoppingItemProductSkuMap.entrySet().stream()
				.flatMap(entry -> {
					ShoppingItem shoppingItem = entry.getKey();
					ProductSku productSku = entry.getValue();
					ProductSku parentProductSku = getParentProductSku(shoppingItemProductSkuMap, shoppingItem);

					ShoppingItemValidationContext context = beanFactory.getBean(ContextIdNames.SHOPPING_ITEM_VALIDATION_CONTEXT);
					context.setProductSku(productSku);
					context.setParentProductSku(parentProductSku);
					context.setShopper(shoppingCartValidationContext.getShoppingCart().getShopper());
					context.setStore(shoppingCartValidationContext.getShoppingCart().getStore());
					context.setShoppingCart(shoppingCartValidationContext.getShoppingCart());
					context.setShoppingItem(shoppingItem);
					context.setPromotedPrice(priceLookupFacade.getPromotedPriceForSku(productSku, context.getStore(), context.getShopper()));
					return shoppingItemValidators.stream().flatMap(validator -> validator.validate(context).stream());
				})
				.collect(Collectors.toSet());
	}

	private ProductSku getParentProductSku(final Map<ShoppingItem, ProductSku> shoppingItemProductSkuMap, final ShoppingItem childShoppingItem) {
		for (Map.Entry<ShoppingItem, ProductSku> shoppingItemProductSkuEntry : shoppingItemProductSkuMap.entrySet()) {
			if (shoppingItemProductSkuEntry.getKey().getChildren().contains(childShoppingItem)) {
				return shoppingItemProductSkuEntry.getValue();
			}
		}
		return null;
	}

	protected Collection<ShoppingItemValidator> getShoppingItemValidators() {
		return shoppingItemValidators;
	}

	public void setShoppingItemValidators(final Collection<ShoppingItemValidator> shoppingItemValidators) {
		this.shoppingItemValidators = shoppingItemValidators;
	}

	protected ProductSkuLookup getProductSkuLookup() {
		return productSkuLookup;
	}

	public void setProductSkuLookup(final ProductSkuLookup productSkuLookup) {
		this.productSkuLookup = productSkuLookup;
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
