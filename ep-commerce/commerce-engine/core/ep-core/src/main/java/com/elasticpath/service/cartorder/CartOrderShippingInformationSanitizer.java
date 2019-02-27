/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.service.cartorder;

import java.util.function.Supplier;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.service.shipping.ShippingOptionResult;

/**
 * Clears invalid shipping information on a {@link CartOrder}.
 */
public interface CartOrderShippingInformationSanitizer {
	
	/**
	 * Clears invalid shipping address and shipping option codes on a {@link CartOrder}.
	 *
	 * @param cartOrder the cart order
	 * @return {@code true} if the cart order was sanitized; {@code false} otherwise.
	 */
	boolean sanitize(CartOrder cartOrder);

	/**
	 * Clears invalid shipping address and shipping options on a {@link CartOrder}.
	 *
	 * @param cartOrder the cart order
	 * @param shippingAddress the shipping address
	 * @param shippingOptionResultSupplier a supplier of the {@link ShippingOptionResult} which we only call if we need to resolve the available
	 * shipping options as part of the sanitizing, otherwise it's left uncalled.
	 *
	 * @return {@code true} if the cart order was sanitized; {@code false} otherwise.
	 */
	boolean sanitize(CartOrder cartOrder, Address shippingAddress, Supplier<ShippingOptionResult> shippingOptionResultSupplier);

}
