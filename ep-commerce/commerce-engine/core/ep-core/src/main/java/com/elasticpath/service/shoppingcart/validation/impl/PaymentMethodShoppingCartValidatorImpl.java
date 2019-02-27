/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import java.util.Collection;
import java.util.Collections;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.base.common.dto.StructuredErrorMessageType;
import com.elasticpath.base.common.dto.StructuredErrorResolution;
import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.service.shoppingcart.validation.ShoppingCartValidationContext;
import com.elasticpath.service.shoppingcart.validation.ShoppingCartValidator;

/**
 * Determines if a valid payment method has been specified.
 */
public class PaymentMethodShoppingCartValidatorImpl implements ShoppingCartValidator {

	/**
	 * Message id for this validation.
	 */
	public static final String MESSAGE_ID = "need.payment.method";

	private PricingSnapshotService pricingSnapshotService;

	@Override
	public Collection<StructuredErrorMessage> validate(final ShoppingCartValidationContext context) {
		// We don't have a cart order to check once we're in the ValidationCheckoutAction.
		if (context.getCartOrder() == null) {
			return Collections.emptyList();
		}

		final ShoppingCart shoppingCart = context.getShoppingCart();
		final ShoppingCartPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);

		// If payment required
		if (pricingSnapshot.getSubtotal().signum() > 0 || pricingSnapshot.getShippingCost().getAmount().signum() > 0) {
			CartOrder cartOrder = context.getCartOrder();
			if (cartOrder.getPaymentMethod() == null) {
				StructuredErrorMessage errorMessage = new StructuredErrorMessage(StructuredErrorMessageType.NEEDINFO, MESSAGE_ID,
						"Payment method must be specified.", Collections.emptyMap(),
						new StructuredErrorResolution(CartOrder.class, cartOrder.getGuid()));
				return Collections.singletonList(errorMessage);
			}
		}

		return Collections.emptyList();
	}

	protected PricingSnapshotService getPricingSnapshotService() {
		return pricingSnapshotService;
	}

	public void setPricingSnapshotService(final PricingSnapshotService pricingSnapshotService) {
		this.pricingSnapshotService = pricingSnapshotService;
	}
}
