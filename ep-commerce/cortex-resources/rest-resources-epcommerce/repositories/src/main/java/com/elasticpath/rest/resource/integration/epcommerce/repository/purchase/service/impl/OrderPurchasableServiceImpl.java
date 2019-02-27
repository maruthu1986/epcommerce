/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.service.impl;

import com.google.common.collect.ImmutableList;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.rest.advise.Message;
import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.service.OrderPurchasableService;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.StructuredErrorMessageTransformer;
import com.elasticpath.service.shoppingcart.validation.PurchaseCartValidationService;

/**
 * Implementation of the order purchasable service.
 */
@Component
public class OrderPurchasableServiceImpl implements OrderPurchasableService {

	private CartOrderRepository cartOrderRepository;

	private PurchaseCartValidationService validationService;

	private StructuredErrorMessageTransformer messageConverter;

	@Override
	public Observable<Message> validateOrderPurchasable(final OrderIdentifier order) {
		String storeCode = order.getScope().getValue();
		String cartOrderGuid = order.getOrderId().getValue();

		return cartOrderRepository.findByGuidAsSingle(storeCode, cartOrderGuid)
				.flatMapObservable(cartOrder -> cartOrderRepository.getEnrichedShoppingCartSingle(storeCode, cartOrder)
								.flatMap(shoppingCart -> Single.just(validationService.buildContext(shoppingCart, cartOrder))
										.map(context -> validationService.validate(context))
										.map(structuredErrorMessages -> messageConverter.transform(ImmutableList.copyOf(structuredErrorMessages),
												shoppingCart.getGuid()))
								)
								.flatMapObservable(Observable::fromIterable)
				);
	}


	@Reference
	public void setCartOrderRepository(final CartOrderRepository cartOrderRepository) {
		this.cartOrderRepository = cartOrderRepository;
	}

	@Reference
	public void setValidationService(final PurchaseCartValidationService validationService) {
		this.validationService = validationService;
	}

	@Reference
	public void setMessageConverter(final StructuredErrorMessageTransformer messageConverter) {
		this.messageConverter = messageConverter;
	}

}
