/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.repositories;

import io.reactivex.Observable;
import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.springframework.core.convert.ConversionService;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;

/**
 * Repository for cart.
 *
 * @param <E> the entity type
 * @param <I> the identifier type
 */
@Component
public class CartEntityRepositoryImpl<E extends CartEntity, I extends CartIdentifier>
		implements Repository<CartEntity, CartIdentifier> {

	private ShoppingCartRepository shoppingCartRepository;

	private ConversionService conversionService;

	private ResourceOperationContext resourceOperationContext;

	@Override
	public Single<CartEntity> findOne(final CartIdentifier cartIdentifier) {
		return shoppingCartRepository.getShoppingCart(cartIdentifier.getCartId().getValue())
				.map(shoppingCart -> conversionService.convert(shoppingCart, CartEntity.class));
	}

	@Override
	public Observable<CartIdentifier> findAll(final IdentifierPart<String> scope) {
		String userId = resourceOperationContext.getUserIdentifier();
		return shoppingCartRepository.findAllCarts(userId, scope.getValue())
				.map(cartId -> CartIdentifier.builder()
						.withCartId(StringIdentifier.of(cartId))
						.withScope(scope)
						.build());
	}

	@Reference
	public void setShoppingCartRepository(final ShoppingCartRepository shoppingCartRepository) {
		this.shoppingCartRepository = shoppingCartRepository;
	}

	@Reference
	public void setConversionService(final ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	@Reference
	public void setResourceOperationContext(final ResourceOperationContext resourceOperationContext) {
		this.resourceOperationContext = resourceOperationContext;
	}
}
