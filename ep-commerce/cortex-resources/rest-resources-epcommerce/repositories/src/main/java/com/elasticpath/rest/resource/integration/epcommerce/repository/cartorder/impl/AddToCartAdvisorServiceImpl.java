/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.impl;

import java.util.Collections;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.reactivex.Completable;
import io.reactivex.Observable;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.advise.Message;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.resource.StructuredErrorMessageIdConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.AddToCartAdvisorService;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.StructuredErrorMessageTransformer;
import com.elasticpath.rest.schema.StructuredMessageTypes;
import com.elasticpath.service.shoppingcart.validation.AddProductSkuToCartValidationService;
import com.elasticpath.service.shoppingcart.validation.ProductSkuValidationContext;

/**
 * Shopping Item Validation Service.
 */
@Singleton
@Named("addToCartAdvisorService")
public class AddToCartAdvisorServiceImpl implements AddToCartAdvisorService {

	private final ProductSkuRepository productSkuRepository;
	private final AddProductSkuToCartValidationService addToCartValidationService;
	private final StructuredErrorMessageTransformer structuredErrorMessageTransformer;
	private final ShoppingCartRepository shoppingCartRepository;

	/**
	 * Constructor.
	 *
	 * @param productSkuRepository              the product sku repository
	 * @param addToCartValidationService        the add to cart validation service
	 * @param structuredErrorMessageTransformer the structured error message transformer
	 * @param shoppingCartRepository            the shopping cart repository
	 */
	@Inject
	@SuppressWarnings({"checkstyle:parameternumber"})
	public AddToCartAdvisorServiceImpl(
			@Named("productSkuRepository") final ProductSkuRepository productSkuRepository,
			@Named("addProductSkuToCartValidationService") final AddProductSkuToCartValidationService addToCartValidationService,
			@Named("structuredErrorMessageTransformer") final StructuredErrorMessageTransformer structuredErrorMessageTransformer,
			@Named("shoppingCartRepository") final ShoppingCartRepository shoppingCartRepository) {
		this.productSkuRepository = productSkuRepository;
		this.shoppingCartRepository = shoppingCartRepository;
		this.addToCartValidationService = addToCartValidationService;
		this.structuredErrorMessageTransformer = structuredErrorMessageTransformer;
	}

	@Override
	public Observable<Message> validateItemPurchasable(final String scope, final String itemId) {

		return shoppingCartRepository.getDefaultShoppingCartGuid()
				.flatMapObservable(defaultShoppingCartGuid -> productSkuRepository.getProductSkuWithAttributesByCodeAsSingle(itemId)
						.flatMapObservable(prodSku -> validateItemPurchasable(scope, defaultShoppingCartGuid, prodSku, null)));

	}


	@Override
	public Observable<Message> validateItemPurchasable(final String scope, final String cartId, final String itemId) {
		return productSkuRepository.getProductSkuWithAttributesByCodeAsSingle(itemId)
				.flatMapObservable(productSku -> validateItemPurchasable(scope, cartId, productSku, null));
	}
	@Override
	public Observable<Message> validateItemPurchasable(final String scope, final String cartId,  final ProductSku productSku,
			final ProductSku parentProductSku) {

		return shoppingCartRepository.getShoppingCart(cartId)
				.flatMapObservable(shoppingCart -> {
					ProductSkuValidationContext context = addToCartValidationService.buildContext(productSku, parentProductSku,
							shoppingCart.getStore(), shoppingCart.getShopper());
					return Observable.fromIterable(structuredErrorMessageTransformer.transform(ImmutableList.copyOf(addToCartValidationService
							.validate(context)), cartId));
				});
	}

	@Override
	public Completable validateLineItemEntity(final LineItemEntity lineItemEntity) {
		Integer quantity = lineItemEntity.getQuantity();
		if (quantity == null) {
			return this.getInvalidQuantityErrorMessage();
		}

		return Completable.complete();
	}

	/**
	 * Get structured error message.
	 *
	 * @return the message
	 */
	private Completable getInvalidQuantityErrorMessage() {
		Map<String, String> data = ImmutableMap.of("field-name", "quantity");
		String message = "'quantity' value must be an integer.";
		Message structuredError = Message.builder()
				.withType(StructuredMessageTypes.ERROR)
				.withId(StructuredErrorMessageIdConstants.INVALID_INTEGER_FIELD)
				.withDebugMessage(message)
				.withData(data)
				.build();
		return Completable.error(ResourceOperationFailure.badRequestBody(message, Collections.singletonList(structuredError)));
	}
}
