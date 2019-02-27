/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.shoppingcart.validation.AddOrUpdateShoppingItemDtoToCartValidationService;
import com.elasticpath.service.shoppingcart.validation.ShoppingItemDtoValidationContext;

/**
 * Implements @{link AddProductSkuToCartValidationService}.
 */
public class AddOrUpdateShoppingItemDtoToCartValidationServiceImpl
		extends AbstractAggregateValidator<ShoppingItemDtoValidationContext, ShoppingItemDtoValidationContext>
		implements AddOrUpdateShoppingItemDtoToCartValidationService {

	private BeanFactory beanFactory;

	private ProductSkuLookup productSkuLookup;

	private PriceLookupFacade priceLookupFacade;

	@Override
	public ShoppingItemDtoValidationContext buildContext(final ShoppingCart shoppingCart, final ShoppingItemDto shoppingItemDto,
														 final Object parentShoppingItem, final boolean isUpdate) {
		final ShoppingItemDtoValidationContext context = beanFactory.getBean(ContextIdNames.SHOPPING_ITEM_DTO_VALIDATION_CONTEXT);

		context.setShoppingCart(shoppingCart);
		context.setShoppingItemDto(shoppingItemDto);
		context.setParentShoppingItem(parentShoppingItem);
		context.setShopper(shoppingCart.getShopper());
		context.setStore(shoppingCart.getStore());
		context.setUpdate(isUpdate);

		ProductSku productSku = productSkuLookup.findBySkuCode(shoppingItemDto.getSkuCode());
		context.setProductSku(productSku);
		if (productSku != null) {
			context.setPromotedPrice(priceLookupFacade.getPromotedPriceForSku(productSku, shoppingCart.getStore(), shoppingCart.getShopper()));
		}

		if (parentShoppingItem != null) {
			if (parentShoppingItem instanceof ShoppingItem) {
				ShoppingItem thisParentShoppingItem = (ShoppingItem) parentShoppingItem;
				ProductSku parentProductSku = productSkuLookup.findByGuid(thisParentShoppingItem.getSkuGuid());
				context.setParentProductSku(parentProductSku);
			} else if (parentShoppingItem instanceof ShoppingItemDto) {
				ShoppingItemDto thisParentShoppingItemDto = (ShoppingItemDto) parentShoppingItem;
				ProductSku parentProductSku = productSkuLookup.findBySkuCode(thisParentShoppingItemDto.getSkuCode());
				context.setParentProductSku(parentProductSku);
			}
		}
		return context;
	}

	@Override
	public Collection<StructuredErrorMessage> validate(final ShoppingItemDtoValidationContext context) {
		return getAllContexts(context)
				.flatMap(newContext -> super.validate(newContext).stream())
				.collect(Collectors.toList());
	}

	private Stream<ShoppingItemDtoValidationContext> getAllContexts(final ShoppingItemDtoValidationContext context) {
		return Stream.concat(Stream.of(context),
					context.getShoppingItemDto().getConstituents().stream()
							.filter(ShoppingItemDto::isSelected)
							.flatMap(constituentShoppingItemDto -> getAllContexts(
									buildContext(context.getShoppingCart(), constituentShoppingItemDto, context.getShoppingItemDto(), false)))
							.filter(constituentContext -> constituentContext.getProductSku() != null)
					);
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	protected ProductSkuLookup getProductSkuLookup() {
		return productSkuLookup;
	}

	public void setProductSkuLookup(final ProductSkuLookup productSkuLookup) {
		this.productSkuLookup = productSkuLookup;
	}

	protected PriceLookupFacade getPriceLookupFacade() {
		return priceLookupFacade;
	}

	public void setPriceLookupFacade(final PriceLookupFacade priceLookupFacade) {
		this.priceLookupFacade = priceLookupFacade;
	}
}
