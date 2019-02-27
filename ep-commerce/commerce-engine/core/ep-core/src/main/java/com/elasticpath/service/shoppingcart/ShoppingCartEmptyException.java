/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.shoppingcart;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.exception.InvalidBusinessStateException;
import com.elasticpath.domain.shoppingcart.ShoppingCart;

/**
 * Exception thrown when a {@link ShoppingCart} is unexpectedly empty.
 */
public class ShoppingCartEmptyException extends EpServiceException implements InvalidBusinessStateException {

	private static final long serialVersionUID = 6129361439895502593L;

	private final List<StructuredErrorMessage> structuredErrorMessages;

	private ShoppingCart shoppingCart;

	/**
	 * Constructor including the offending {@link ShoppingCart}.
	 *
	 * @param message           the exception message
	 * @param emptyShoppingCart the empty Shopping Cart
	 * @deprecated use {@link #ShoppingCartEmptyException(String, Collection <StructuredErrorMessage>)} instead.
	 */
	@Deprecated
	public ShoppingCartEmptyException(final String message, final ShoppingCart emptyShoppingCart) {
		super(message);
		shoppingCart = emptyShoppingCart;
		structuredErrorMessages = asList(new StructuredErrorMessage(null, message, null));
	}

	/**
	 * The constructor.
	 *
	 * @param message                 the reason for this <code>UserStatusInactiveException</code>.
	 * @param structuredErrorMessages the detailed reason for this <code>UserStatusInactiveException</code>.
	 */
	public ShoppingCartEmptyException(final String message, final Collection<StructuredErrorMessage> structuredErrorMessages) {
		super(message);
		this.structuredErrorMessages = structuredErrorMessages == null ? emptyList() : ImmutableList.copyOf(structuredErrorMessages);
	}


	/**
	 * returns the shopping cart.
	 *
	 * @return ShoppingCart
	 * @deprecated Shouldn't be used at all. Please change you're exception handling
	 */
	@Deprecated
	public ShoppingCart getShoppingCart() {
		return shoppingCart;
	}

	@Override
	public List<StructuredErrorMessage> getStructuredErrorMessages() {
		return structuredErrorMessages;
	}

}
