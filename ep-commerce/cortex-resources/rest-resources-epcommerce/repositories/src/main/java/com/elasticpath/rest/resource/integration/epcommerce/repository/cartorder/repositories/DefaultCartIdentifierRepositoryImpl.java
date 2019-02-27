/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.repositories;

import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.repository.AliasRepository;
import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.carts.DefaultCartIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;

/**
 * Repository for Default Cart Identifiers.
 *
 * @param <AI>  the Alias identifier type
 * @param <I> the identifier type
 */
@Component
public class DefaultCartIdentifierRepositoryImpl<AI extends DefaultCartIdentifier, I extends CartIdentifier>
		implements AliasRepository<DefaultCartIdentifier, CartIdentifier> {
	private static final Logger LOG = LoggerFactory.getLogger(DefaultCartIdentifierRepositoryImpl.class);

	private ShoppingCartRepository shoppingCartRepository;

	@Override
	public Single<CartIdentifier> resolve(final DefaultCartIdentifier defaultCartIdentifier) {
		return shoppingCartRepository.getDefaultShoppingCartGuid()
				.onErrorResumeNext(this::getFallback)
				.flatMap(cartGuid -> Single.just(CartIdentifier.builder()
						.withCartId(StringIdentifier.of(cartGuid))
						.withScope(defaultCartIdentifier.getScope())
						.build()));
	}

	private Single<String> getFallback(final Throwable throwable) {
		LOG.debug("Error getting default cart guid.", throwable);
		return shoppingCartRepository.getDefaultShoppingCart().map(ShoppingCart::getGuid);
	}

	@Reference
	public void setShoppingCartRepository(final ShoppingCartRepository shoppingCartRepository) {
		this.shoppingCartRepository = shoppingCartRepository;
	}
}


